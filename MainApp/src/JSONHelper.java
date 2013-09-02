/*
 * JSON Helper
 * @freedomofkeima - Iskandar Setiadi ( iskandarsetiadi@students.itb.ac.id )
 * July 2013
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class JSONHelper {
	
	/*
	 * Parse JSON from Tracker
	 */
	public static ArrayList<JSONObject> parseJSON(String tracker, String info_hash, String ip, String port){
		
		ArrayList<JSONObject> ret = new ArrayList<JSONObject>();
		
	    HttpURLConnection connection; //Connection service
	    URL url = null; //Target URL
	    String response = null; //Return Response
	    
	    String parameters = "info_hash=" + info_hash + "&ip=" + ip + "&port=" + port; //Default parameter

	    try
	    {
	        url = new URL(tracker + "?" + parameters); /* URL link with its info */
	        
	        /** Open Connection to tracker */
	        connection = (HttpURLConnection) url.openConnection();
	        connection.setDoOutput(true);
	        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        connection.setRequestMethod("POST");

	        String line = ""; //Initialize line

	        /** Open InputStream and Buffered Reader */
	        InputStreamReader isr = new InputStreamReader(connection.getInputStream());
	        BufferedReader reader = new BufferedReader(isr);
	        StringBuilder sb = new StringBuilder();
	        while ((line = reader.readLine()) != null)
	        {
	        	 sb.append(line);
	        }
	        response = "{\"peers\": " + sb.toString() + "}";

	        /*
	         * JSON Parser, using json_simple-1.1.jar
	         */
	        JSONParser parser = new JSONParser();
	        JSONObject mainJSON = null;
			try {
				mainJSON = (JSONObject) parser.parse(response);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        JSONArray infoPeers = (JSONArray) mainJSON.get("peers"); //Get peers info
	        
	        /** Suppress warning for Compilation level */
	        @SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator = infoPeers.iterator();
			while (iterator.hasNext()) {
				JSONObject peer = iterator.next(); //each peer info
				ret.add((JSONObject) peer.clone());
			}

			/** Close Connection */
	        isr.close();
	        reader.close(); 
	    }
	    catch(IOException e) /* Input Output Error Exception */
	    {
	        System.out.println("Network Error: " + e); //Print Error Message
	    }	    

	    return ret;
	}
	
	/*
	 * Print JSON Info to screen
	 */
	public static void printJSON(ArrayList<JSONObject> list){
		if (list != null) /* Null checking */
			for (int i = 0; i < list.size(); i++){
				JSONObject peer = list.get(i); //each peer info
				System.out.println("Port: " + peer.get("port")); //print Port info
				System.out.println("IP: " + peer.get("ip")); //print IP info
			}
	}

}
