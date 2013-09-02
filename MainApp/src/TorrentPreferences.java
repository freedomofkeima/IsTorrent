/*
 * Torrent Preferences Class
 * @freedomofkeima - Iskandar Setiadi ( iskandarsetiadi@students.itb.ac.id )
 * July 2013
 *
 */

public class TorrentPreferences {
	
	/** List of preferences */
	
	public static String saveDirectory = System.getProperty("user.dir");
	
	public static String defaultTracker = "http://localhost/PrivateTorrent/IsTracker/announce.php";
	
	/** TODO : Each pieces consists of multiple blocks */
	public static long blockSize = 65536; //default 65536 bytes = 64 KiB
	
	public static int defaultPort = 6789; //default port
	
	public static int updateTime = 120000; //default peers update time
	
	public static int maxThread = 3; //default threads
}
