/*
 * FileWalker Class
 * @freedomofkeima - Iskandar Setiadi ( iskandarsetiadi@students.itb.ac.id )
 * July 2013
 *
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * FileWalker Class : Used to recursively iterate throughout multiple files in directories
 * @author freedomofkeima
 *
 */
public class FileWalker {

	/** Recursively count all files */
    public int walkCount(String path) {

    	int ret = 0;
        File root = new File( path );
        File[] list = root.listFiles();

        /** Return 0 if list null */
        if (list == null) return 0;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
            	/** Recursive */
                ret = ret + walkCount(f.getAbsolutePath());
            }
            else {
            	ret++;
            }
        }
        return ret;
    }
    
    /** Recursive through all paths */
    public void walk(String path, String currentPath, BufferedWriter bw) {
    	
        File root = new File( path );
        File[] list = root.listFiles();
        
        if (list == null) return;
        
        for ( File f : list ) {
            if ( f.isDirectory() ) {
            	/** Recursive */
                walk(f.getAbsolutePath(), currentPath, bw);
            }
            else {
            	/** Update file informations */
            	try {
            		/** file relative name */
        			bw.write(f.getAbsolutePath().substring(currentPath.length()) + "\n");
        			
        			/** file length */
    				long size = FileHelper.pieceCount(f.length(), TorrentPreferences.blockSize);
    				bw.write(size + "\n");
    				
    				/** file SHA pieces */
    				ArrayList<String> fileSHA = FileHelper.fileTorrentSHA1(f.getAbsolutePath(), size);
    				for (int i = 0; i < size; i++) bw.write((i+1) + " : " + fileSHA.get(i) + "\n");
    				
    				/** main file SHA */
    				bw.write(FileHelper.mainTorrentSHA1(fileSHA) + "\n");
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
            }
        }
        
    }
}