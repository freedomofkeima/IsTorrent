/*
 * TCPServer & Client Class
 * @freedomofkeima - Iskandar Setiadi ( iskandarsetiadi@students.itb.ac.id )
 * July 2013
 *
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/*
 * TCP Server class
 */
public class TCPServer implements Runnable {
	
	public static int numClient = 0; //Static counter for active Clients thread
	private int port; //Listen to this port
	
	public TCPServer(int port){
		this.port = port;
	}
	
	protected void acceptData(){
        ServerSocket serverSocket; //Socket for Server TCP
        try {
			serverSocket = new ServerSocket(port); //Listen to port
	        while(true) {
            	System.out.println("new Client is connected"); //Log message
            	/*
            	 * Accept connection from clients
            	 */
	            Socket connectionSocket = serverSocket.accept();
	            if (connectionSocket != null) { //Ensure socket is not null
	                Client client = new Client(connectionSocket);
	                client.start();
	            }
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/** Implements runnable, prepare to Accept data */
	public void run() {
		acceptData();
	}

}

/*
 * Client class, each Client (piece) will run in different Thread
 */
class Client extends Thread {
    private Socket connectionSocket; //connection Socket
 
    /** Each Client is binded by one socket */
    public Client(Socket c) throws IOException {
        connectionSocket = c;
    }
 
    /** Extends thread, receive each client piece */
    public void run() {  
        	try {
        		TCPServer.numClient++; //increment numClient at each Client addition
				DataInputStream pieceInformation = new DataInputStream(connectionSocket.getInputStream());
			    DataOutputStream pieceAnswer = new DataOutputStream(connectionSocket.getOutputStream());
				String torrentName = pieceInformation.readUTF();
			    String torrentFilePosition = pieceInformation.readUTF();
			    String torrentPieceNo = pieceInformation.readUTF();
			    boolean isRequestAccepted = false;
			    TorrentModel t = null;
			    if (UserInterface.getData() != null) {
			    	for (int i = 0; i < UserInterface.getData().size(); i++) {
			    		if (UserInterface.getData().get(i).getTorrentName().equals(torrentName)) {
			    			t = UserInterface.getData().get(i);
			    			if (t.getTorrentFile().get(Integer.valueOf(torrentFilePosition)).getPieceInformation().get(Integer.valueOf(torrentPieceNo)).equals("C")) {
			    				/** File exists in seeder */
			    				isRequestAccepted = true;
			    			}
			    		}
			    	}
			    }
			    if (!isRequestAccepted) pieceAnswer.writeUTF("-1");
			    else {
			    	pieceAnswer.writeUTF("1");
			    	byte[] fileByte = new byte[(int) TorrentPreferences.blockSize];
			    	/** Within FileHelper, open file */
			    	if (t.getSize() == 1) {
			    		fileByte = FileHelper.getFile(t.getMainDirectory() + "\\" + t.getTorrentFile().get(0).getTorrentFileName(), 
			    				Integer.valueOf(torrentPieceNo) * TorrentPreferences.blockSize, ((Integer.valueOf(torrentPieceNo)+1) * TorrentPreferences.blockSize) - 1);
			    	} else {
			    		fileByte = FileHelper.getFile(t.getMainDirectory() + t.getTorrentFile().get(Integer.valueOf(torrentFilePosition)).getTorrentFileName(), 
			    				Integer.valueOf(torrentPieceNo) * TorrentPreferences.blockSize, ((Integer.valueOf(torrentPieceNo)+1) * TorrentPreferences.blockSize) - 1);			    		
			    	}
		        	pieceAnswer.writeInt(fileByte.length);
		        	if (fileByte.length > 0) {
		                pieceAnswer.write(fileByte, 0, fileByte.length);
		            }
			    }
        	} catch (IOException e) {
        		System.out.println("Client is disconnected");
			//	e.printStackTrace();
			} finally {
	        	TCPServer.numClient--; //decrement numClient at each Client deletion				
			}
    }
}
