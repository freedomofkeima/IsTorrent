/*
 * File Writer Helper
 * @freedomofkeima - Iskandar Setiadi ( iskandarsetiadi@students.itb.ac.id )
 * July 2013
 *
 */

import java.io.File;
import java.io.IOException;


public class FileWriterHelper {
	
	/** List of attributes */
	private static FileWriterHelper instance = null;
	
	private FileWriterHelper() {
		super();
	}
	
	public synchronized void writeToFile(String sFileName, byte[] sData, long off, long length) {
		
			try {
				File file = new File(sFileName);

				if (!file.exists()) {
					/** Create directories and file */
					file.mkdirs();
					file.createNewFile();	
				}
				
				if (file.canWrite()) {
					/** Write to file */
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		
	}
	
	public FileWriterHelper getInstance() {
		if (instance == null) instance = new FileWriterHelper();
		return instance;
	}

}
