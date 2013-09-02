/*
 * Add Torrent Class
 * @freedomofkeima - Iskandar Setiadi ( iskandarsetiadi@students.itb.ac.id )
 * July 2013
 *
 */


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;


@SuppressWarnings("serial")
public class AddTorrent extends JFrame {
	
	/** List of Attributes */
	private JFrame frame; /* Frame for closing panel */
	private JPanel container; /* Add Torrent panel */
	private JLabel saveMessage, contentMessage, nameMessage, sizeMessage; /* Label for file help */
	private Graphics2D line, line2; /* Add line */
	private JTextField directoryPath; /* Text field for browse */
	private JButton btnBrowse; /* Button for browse */
	private JButton cancel, finish; /* Button for finishing */
	
	public AddTorrent() {
		initFirstComponents(); //initialize First Screen
		frame = this; //initialize Frame
	}
	
	/**
	 * Select torrent file with *.keima extensions
	 */
	private void initFirstComponents() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.addChoosableFileFilter(new FileFilter() {
        	 
            public String getDescription() {
                return "IsTorrents (*.keima)";
            }
         
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(".keima");
                }
            }
        });
        
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        fileChooser.setAcceptAllFileFilterUsed(false);

        int rVal = fileChooser.showOpenDialog(null);
        if (rVal == JFileChooser.APPROVE_OPTION) {
        	File selectedFile = fileChooser.getSelectedFile();
        	System.out.println("Add Torrent : Processing " + selectedFile.getName());
        	initSecondComponents(selectedFile); //initialize Second Screen
        }
	}
	
	/**
	 * Torrent save path and several descriptions regarding IsTorrent
	 */
	private void initSecondComponents(final File f) {
		container = new JPanel() {
			public void paintComponent(Graphics g) {
				/** Add lines */
				line = (Graphics2D) g;
				line2 = (Graphics2D) g;
				line.draw(new Line2D.Double(30, 34, 450, 34));
				line2.draw(new Line2D.Double(30, 124, 450, 124));
			}
		}; 
		/** Construct panel */
		container.setLayout(null);
		
		/** Message for Save Path */
		saveMessage = new JLabel("Save in");
		saveMessage.setBounds(30, 10, 300, 20);
		saveMessage.setOpaque(true);
		container.add(saveMessage);
		
		directoryPath = new JTextField();
	    directoryPath.setBounds(30, 50, 320, 20);
	    container.add(directoryPath);
	    
	    /** Browse button for save directory path */
	    btnBrowse = new JButton("Browse");
	    btnBrowse.setBounds(355, 50, 100, 20);
	    container.add(btnBrowse);
	    
	    /** Add an Action Listener for Browse button */
	    btnBrowse.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	 JFileChooser fileChooser = new JFileChooser();
		         
	        	 // For Directory
		         fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		         fileChooser.setAcceptAllFileFilterUsed(false);

		         int rVal = fileChooser.showOpenDialog(null);
		         if (rVal == JFileChooser.APPROVE_OPTION) {
		           directoryPath.setText(fileChooser.getSelectedFile().toString());
		         }
	        }
	      });
	    
	    /** Message for Contents */
		contentMessage = new JLabel("Contents (Torrent Info)");
		contentMessage.setBounds(30, 100, 300, 20);
		contentMessage.setOpaque(true);
		container.add(contentMessage);
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(f.getAbsoluteFile()));
		    String line = br.readLine();
		     
			nameMessage = new JLabel("Name : " + line);
			nameMessage.setBounds(30, 150, 450, 20);
			nameMessage.setOpaque(true);
			container.add(nameMessage);
		     
		    br.close();  
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sizeMessage = new JLabel("Torrent Size   : " + f.length() + " bytes");
		sizeMessage.setBounds(30, 190, 400, 20);
		sizeMessage.setOpaque(true);
		container.add(sizeMessage);
		
		/** Cancel and Finish button */
		cancel = new JButton("Cancel");
		cancel.setBounds(270, 440, 100, 25);
		container.add(cancel);
		
	    cancel.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	/** Close panel */
	        	frame.dispose();
	        }
	    });	
		
		finish = new JButton("Finish");
		finish.setBounds(380, 440, 100, 25);
		container.add(finish);
	    finish.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	int res = validateInput();
	        	if (res == 0) {
	        		/** Add Torrent to list */
	        		addTorrent(f);
	        		/** Close panel */
	        		frame.dispose();
	        	} else {
	        		/** Show Alert Message */
		       		  if (res == -1) JOptionPane.showMessageDialog(container, "Fields are null");
		       		  if (res == 1)  JOptionPane.showMessageDialog(container, "Save Path is empty");
		       		  if (res == 2)  JOptionPane.showMessageDialog(container, "Save Path is not exist");
	        	}
	        }
	    });	
	    
        /** Add panel to screen */
        add(container);
        setTitle("Add Torrent to List");
        setSize(500, 500); //Set UI resolution
        setLocationRelativeTo(null); //Set UI Relative Location
        setVisible(true); //Set UI Visibility
        setResizable(false); //Set UI Resize-ability
		
	}
	
	private int validateInput() {
		int retVal = 0;
		if (directoryPath != null) {
			if (directoryPath.getText().trim().isEmpty()) return 1; /* Error code 1 */
			
			File folderExist = new File(directoryPath.getText());
			if (!folderExist.exists()) return 2; /* Error code 2 */
			
		} else retVal = -1; /* Null entry */
		return retVal;
	}
	
	/** Add all torrent informations to tracker
	 *  Save to db.txt (corresponding to a file [torrent_id].txt
	 */
	private void addTorrent(File f) {
		/** Add information to current ArrayList */
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(f.getAbsoluteFile()));
		    String line = br.readLine();
		    
		    /** Checking whether this new torrent has been added previously */
		    ArrayList<String> dbTemp = UserInterface.getInstance();
		    for (int i = 0; i < dbTemp.size(); i++) {
		    	if (dbTemp.get(i).equals(line)) {
		    		JOptionPane.showMessageDialog(container, "You've added this torrent previously!");
		    		return;
		    	}
		    }
			
			/** Write all torrent initial information to file */
			FileHelper.writeTempTorrentInformation(f, directoryPath.getText(), 0);
			
		    /** Add torrent information to database */
			UserInterface.setDatabase(line);
			
			/** Rewrite ArrayList to db.txt */
			FileHelper.writeDatabaseFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
