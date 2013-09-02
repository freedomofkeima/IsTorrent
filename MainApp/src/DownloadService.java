import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.json.simple.JSONObject;

/*
 * Download Service for Torrent
 * @freedomofkeima - Iskandar Setiadi ( iskandarsetiadi@students.itb.ac.id )
 * August 2013
 *
 */

public class DownloadService {
	
	private TorrentModel object;
	private ArrayList<JSONObject> jsonObject;
	private PeerUpdate updateService;
	private FileUpdate fileCheckerService;
	
	private FileReceiver[] receiveService;
	
	public DownloadService(TorrentModel _object) {
		object = _object;
		/** Start Peers */
		updateService = new PeerUpdate();
		updateService.start();
		
		fileCheckerService = new FileUpdate();
		fileCheckerService.start();
		
		/** Start receive Service */
		receiveService = new FileReceiver[TorrentPreferences.maxThread];
		
		for (int i = 0; i < TorrentPreferences.maxThread; i++) {
			receiveService[i] = new FileReceiver();
			receiveService[i].start();
		}
	}

	public TorrentModel getObject() {
		return object;
	}

	public void setObject(TorrentModel object) {
		this.object = object;
	}
	
	public ArrayList<JSONObject> getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(ArrayList<JSONObject> jsonObject) {
		this.jsonObject = jsonObject;
	}

	private void refreshJSON() {
		try {
			jsonObject = JSONHelper.parseJSON(object.getTracker(), FileHelper.toSHA1(object.getTorrentName().getBytes()), 
					InetAddress.getLocalHost().getHostAddress().toString(), String.valueOf(TorrentPreferences.defaultPort));
			JSONHelper.printJSON(jsonObject);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Internal class for Peers Update Service
	 * @author Iskandar Setiadi
	 *
	 */
	class PeerUpdate extends Thread {
		public void run() {
			while (true) {
				refreshJSON();
				System.out.println("Peers for " + object.getTorrentName() + " is being updated.");
				try {
					Thread.sleep(TorrentPreferences.updateTime); //Sleep
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
	}
	
	class FileUpdate extends Thread {
		public void run() {
			while (true) {
				/** Check whether this torrent has been completed */
				boolean isFinished = true;
				for (int i = 0; i < object.getSize(); i++) {
					for (int j = 0; j < object.getTorrentFile().get(i).getPieceInformation().size(); j++) {
						if (!object.getTorrentFile().get(i).getPieceInformation().get(j).equals("C")) isFinished = false;
					}
				}
				/** TODO : Use final hash to show message */
				
				/** If yes, add information to temporary files, adding finish time */
				if (isFinished) {
					object.setFinishDate(new Date());
					for (int i = 0; i < UserInterface.getData().size(); i++) {
						if (object.getId() == UserInterface.getData().get(i).getId()) {
							UserInterface.getData().get(i).setFinishDate(new Date());
						}
					}
					FileHelper.updateTempTorrentInformation(object);
					/** Status complete at table */	
					if (UserInterface.getTableModel() != null) {
						UserInterface.getTableModel().refreshTable(UserInterface.getData());
						UserInterface.getTable().setModel(UserInterface.getTableModel());
						UserInterface.getTable().revalidate();
					}
				}
				
				try {
					Thread.sleep(3000); //Sleep
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}	
		}
	}
	
	/**
	 * Search for incomplete piece status, ask for wanted piece to peers (Leech)
	 * @author Iskandar Setiadi
	 *
	 */
	class FileReceiver extends Thread {
		public void run() {
			/** In case pieces are incomplete */
			while (object.getFinishDate() == null) {
				Random generator = new Random(System.nanoTime()); 
				int i = generator.nextInt(object.getSize());
				int j = generator.nextInt(object.getTorrentFile().get(i).getPieceSize());
				if (object.getTorrentFile().get(i).getPieceInformation().get(j).equals("I")) {
					/** Ask for this file */
					if (jsonObject == null) refreshJSON();
					int k = generator.nextInt(jsonObject.size());
					try {
						/** If destination != local */
						/** TODO : Remove destinated IP if unreachable */
						if ((jsonObject.get(k).get("port").toString().equals(String.valueOf(TorrentPreferences.defaultPort))) &&
						(jsonObject.get(k).get("ip").toString().equals(InetAddress.getLocalHost().getHostAddress().toString()))) {
							//Do Nothing
						} else {
							TCPClient request = new TCPClient(jsonObject.get(k).get("ip").toString(), 
									Integer.parseInt(jsonObject.get(k).get("port").toString()));
							System.out.println("Requesting " + object.getTorrentName()
									+ " - File No." + (i+1) + " - Pieces No." + (j+1)
									+ " From " + jsonObject.get(k).get("ip").toString()
									+ " Port " + jsonObject.get(k).get("port").toString());
							
							/** Ask for your requested pieces here 
							 * If request has the following piece, download it
							 * Check validity of requested piece
							 * If valid, write to the file destination and update temporary file (using FileHelper)
							 **/
							byte[] fileData = request.sendData(object.getTorrentName(), i, j, object.getTorrentFile().get(i).getPieceHash().get(j));
							if (fileData != null) {
								String filename = "";
								if (object.getSize() == 1) filename = object.getMainDirectory() + "\\" + object.getTorrentFile().get(0).getTorrentFileName();
								else filename = object.getMainDirectory() + "\\" + UserInterface.getInstance().get((int) object.getId() - 1) + object.getTorrentFile().get(i).getTorrentFileName();
							    
								boolean isSuccess = FileHelper.writeToFile(filename, j, TorrentPreferences.blockSize, fileData);
								
								if (isSuccess) {
									object.getTorrentFile().get(i).setPieceInformation("C", j);
									for (int l = 0; l < UserInterface.getData().size(); l++) {
										if (UserInterface.getData().get(l).getId() == object.getId()) {
											UserInterface.getData().get(l).getTorrentFile().get(i).setPieceInformation("C", j);
										}
									}
									/** Update temporary file database */
									FileHelper.updateTempTorrentInformation(object);
								}
							}
							
						}
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				}
				
				/** This is un-needed, yet, for easier observation in demo / testing mode */
				try {
					Thread.sleep(200); //Sleep for 200ms
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	

}

