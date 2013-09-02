/*
 * Main Application Class
 * @freedomofkeima - Iskandar Setiadi ( iskandarsetiadi@students.itb.ac.id )
 * July 2013
 *
 */


public class Main {
	
	private static TCPServer server;
	
	public static TCPServer getServer() {
		if (server == null) initSingleton();
		return server;
	}
	
	public static void initSingleton() {
		server = new TCPServer(TorrentPreferences.defaultPort); //Listen to this port
		Thread serverThread = new Thread(server);
		serverThread.start();
	}

	public static void main(String args[]){
    
	    /** Activate TCP Server */
		initSingleton();
		try {
			Thread.sleep(200); //Sleep for 200ms, assuring that server has been started
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	    new UserInterface(); //Create an User Interface
	}
	

}
