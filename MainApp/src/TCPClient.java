/*
 * TCPClient Class
 * @freedomofkeima - Iskandar Setiadi ( iskandarsetiadi@students.itb.ac.id )
 * July 2013
 *
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class TCPClient {
	
	private String ip; //Connect to this ip
	private int port; //Connect to this port
	
	public TCPClient(String ip, int port){
		this.ip = ip;
		this.port = port;
	}
	
	public byte[] sendData(String name, int fileNo, int pieceNo, String hash) {           
        Socket clientSocket; //Socket for TCP Client
		try {
			clientSocket = new Socket(ip, port); //Binding ip at port
			DataOutputStream pieceInformation = new DataOutputStream(clientSocket.getOutputStream());
	        DataInputStream pieceAnswer = new DataInputStream(clientSocket.getInputStream());
	        pieceInformation.writeUTF(name);
	        pieceInformation.writeUTF(String.valueOf(fileNo));
	        pieceInformation.writeUTF(String.valueOf(pieceNo));
	        /** At this point, all of default piece information has been sent */
	        String answer = pieceAnswer.readUTF();
	        if (answer.equals("1")) {
	        	System.out.println("File exists in Seeder");
	        	int len = pieceAnswer.readInt();
	        	byte[] fileByte = new byte[len];
	        	if (len > 0) {
	                pieceAnswer.readFully(fileByte);
	            }
	        	/** SHA Validation with our torrents SHA1 */
	        	if (FileHelper.toSHA1(fileByte).equals(hash)) {
	        		System.out.println("SHA1 is valid.");
	    	        clientSocket.close(); //closing client Socket
	        		return fileByte;
	        	} else System.out.println("SHA1 is not valid.");
	        }
	        else System.out.println("File doesn't exist in Seeder");
	        clientSocket.close(); //closing client Socket
	 
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Destination is not reachable.");
			//e.printStackTrace();
		}
		return null;
	}

}
