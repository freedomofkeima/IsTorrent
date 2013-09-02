/*
 * File Helper
 * @freedomofkeima - Iskandar Setiadi ( iskandarsetiadi@students.itb.ac.id )
 * July 2013
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class FileHelper {
	
	/** Convert byteArray to HexString */
	public static String byteArrayToHexString(byte[] b) {
		  String result = "";
		  for (int i=0; i < b.length; i++) {
		    result +=
		          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		  }
		  return result;
	}
	
	/** Convert HexString to byteArray */
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	/** Convert byte[] to SHA1 encryption */
	public static String toSHA1(byte[] convertme) {
	    MessageDigest md = null;
	    try {
	        md = MessageDigest.getInstance("SHA-1");
	    }
	    catch(NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } 
	    return byteArrayToHexString(md.digest(convertme));
	}
	
	/** TODO : Instead of ArrayList, use HashMap<#piece, String>, digest SHA1 with multi-threads for better performances */
	
	/** Return all SHA1 hashes from a file */
	public static ArrayList<String> fileTorrentSHA1(String fileName, long size) {
		ArrayList<String> ret = new ArrayList<String>();
		byte[] receive = new byte[(int) TorrentPreferences.blockSize];
		for (int i = 0; i < size; i++) {
	        receive = FileHelper.getFile(fileName, 0 + (i * TorrentPreferences.blockSize), ((i+1) * TorrentPreferences.blockSize) - 1);
		    ret.add(FileHelper.toSHA1(receive));
		}
		return ret;
	}
	
	/** Return main hashes from all SHA1 hashes */
	public static String mainTorrentSHA1(ArrayList<String> hash) {
		String ret = "";
		for (int i = 0; i < hash.size(); i++) ret = ret + hash.get(i);
		try {
			ret = toSHA1(ret.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			ret = "";
		}
		return ret;
	}
	
	/** Get File bytes */
	public static byte[] getFile(String fileName, long offset, long length) {
		RandomAccessFile f;
		byte[] ret = null;
		try {
			f = new RandomAccessFile(fileName, "rw");
			f.seek(offset); //Seek to certain offset
			ret = new byte[(int) length];
			f.read(ret, 0, (int) length);
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/** Write bytes to file */
	public static synchronized boolean writeToFile(String fileName, long offset, long length, byte[] data) {
		boolean isSuccess = false;
		File _f = new File(fileName);
		File parent = _f.getParentFile();
		if(!parent.exists() && !parent.mkdirs()){
		    throw new IllegalStateException("Couldn't create dir: " + parent);
		}	
		
		RandomAccessFile f;
		
		try {
			f = new RandomAccessFile(fileName, "rw");
			f.seek(offset * length); //Seek to certain offset
			if (offset != 0) f.write(data, 0, (int) length);
			else f.write(data, 0, (int) length - 1);
			f.close();
			isSuccess = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	/** Return number of pieces */
	public static long pieceCount(long fileSize, long pieceSize) {
		Double retD = Math.ceil(fileSize / pieceSize);
		long ret = retD.intValue();
		if (ret * pieceSize < fileSize) ret = ret + 1; // fault tolerance
		return ret;
	}
	
	/** Write database file */
	public static void writeDatabaseFile() {
		File f = new File(TorrentPreferences.saveDirectory + "/db/db.dat");
		if (f.exists()) f.delete();
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
		try {
			PrintWriter oFile = new PrintWriter(f);
			ArrayList<String> dbTemp = UserInterface.getInstance();
			for (int i = 0; i < dbTemp.size(); i++) oFile.println(dbTemp.get(i));
			oFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println("Database list has been updated successfully");
	}
	
	/** Write torrent information to apps temporary file (f -> oFile) */
	public static synchronized void writeTempTorrentInformation(File f, String dir, int flag) {
		File f2 = new File(TorrentPreferences.saveDirectory + "/db/" + (UserInterface.getInstance().size()) + ".dat");
		if (f2.exists()) f2.delete();
		if (!f2.exists()) {
			try {
				f2.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/** Read file f while writing to file oFile */
		String sCurrentLine;
		 
		BufferedReader br;
		PrintWriter oFile;
		try {
			br = new BufferedReader(new FileReader(f));
			oFile = new PrintWriter(f2);
			
			/** Variables which are being used */
			int currLine = 0; 
			int numberOfFile = 0;
			int status = 0; 
			int pieceCount = 0;
			
			while ((sCurrentLine = br.readLine()) != null) {
				/** Move all informations to file */
				currLine++;
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				if (currLine == 1) {
					/** 1st line = Name of this torrent */
					oFile.println(sCurrentLine);
					/** 2nd line = Current date (added) */
					oFile.println(dateFormat.format(date));
					/** 3rd line = Finished date (left blank) */
					if (flag == 1) oFile.println(dateFormat.format(date));
					else oFile.println("");
					/** 4th line = Save location */
					oFile.println(dir);
				}
				/** 5th line = Number of file in this torrent */
				if (currLine == 2) {
					if (sCurrentLine.equals("1")) System.out.println("Single file detected");
					else System.out.println("Multiple file detected");
					oFile.println(sCurrentLine);
					numberOfFile = Integer.parseInt(sCurrentLine);
				}
				
				if (currLine > 2) {
					if (numberOfFile != 0) {
						if (status == 0) {
							/** File name */
							oFile.println(sCurrentLine);
							status++;
						} else if (status == 1) {
							/** Number of pieces in that file */
							oFile.println(sCurrentLine);
							pieceCount = Integer.parseInt(sCurrentLine);
							status++;
						} else if (status == 2) {
							/**
							 * C = Complete
							 * I = Incomplete
							 * D = Downloading
							 */
							if (flag == 0) oFile.println("I " + sCurrentLine);
							else if (flag == 1) oFile.println("C " + sCurrentLine);
							else if (flag == 2) oFile.println("D " + sCurrentLine);
							pieceCount --;
							if (pieceCount == 0) status++;
						} else if (status == 3) {
							oFile.println(sCurrentLine); /** Main hash */
							status = 0; /** Next file */
							numberOfFile--;
						}
					} else oFile.println(sCurrentLine); /** After all informations have been read */
				}

			}
			
			/** Close both files */
			br.close();
			oFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/** Update temporary torrent information */
	public static synchronized void updateTempTorrentInformation(TorrentModel object) {
		File f2 = new File(TorrentPreferences.saveDirectory + "/db/" + object.getId() + ".dat");
		if (f2.exists()) f2.delete();
		if (!f2.exists()) {
			try {
				f2.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		PrintWriter oFile;
		try {
			oFile = new PrintWriter(f2);
			
			/** File informations */
			oFile.println(object.getTorrentName());
			
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			oFile.println(df.format(object.getAddDate()));
			if (object.getFinishDate() != null) oFile.println(df.format(object.getFinishDate()));
			else oFile.println("");
			oFile.println(object.getMainDirectory());
			oFile.println(object.getSize());
			for (int i = 0; i < object.getSize(); i++) {
				oFile.println(object.getTorrentFile().get(i).getTorrentFileName());
				oFile.println(object.getTorrentFile().get(i).getPieceSize());
				for (int j = 0; j < object.getTorrentFile().get(i).getPieceSize(); j++) {
					oFile.println(object.getTorrentFile().get(i).getPieceInformation().get(j) + " " + (j+1) + " : "
							+ object.getTorrentFile().get(i).getPieceHash().get(j));
				}
				oFile.println(object.getTorrentFile().get(i).getMainHash());
			}
			oFile.println(object.getTracker());
			oFile.println("Created by freedomofkeima");


			oFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}
