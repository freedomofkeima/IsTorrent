/*
 * New Torrent Class
 * @freedomofkeima - Iskandar Setiadi ( iskandarsetiadi@students.itb.ac.id )
 * July 2013
 *
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class NewTorrent extends JFrame {
	
	/** List of Attributes */
	private JFrame frame; /* Frame for closing panel */
	private JPanel container; /* New Torrent panel */
	private ButtonGroup bgGroup; /* Radio Button group for file */
	private JRadioButton single, multiple; /* Radio Button for file */
	private JLabel fileMessage, radioButtonMessage, directoryMessage, torrentMessage, extMessage, trackerMessage; /* Label for file help */
	private Graphics2D line, line2, line3, line4; /* Add line */
	private JTextField txtPath, directoryPath, torrentName, trackerName; /* Text field for browse */
	private JButton btnBrowse, btnBrowse2; /* Button for browse */
	private JButton cancel, finish; /* Button for finishing */
	
	private static String FILE_SEPARATOR = "/";
	private static String EXTENSION_SEPARATOR = ".keima";
	
	public NewTorrent() {
		initComponents(); //initialize Screen
		frame = this; //initialize Frame
	}
	
	
	private void initComponents() {
		container = new JPanel() {
			public void paintComponent(Graphics g) {
				/** Add lines */
				line = (Graphics2D) g;
				line2 = (Graphics2D) g;
				line3 = (Graphics2D) g;
				line4 = (Graphics2D) g;				
				line.draw(new Line2D.Double(30, 34, 450, 34));
				line2.draw(new Line2D.Double(30, 124, 450, 124));
				line3.draw(new Line2D.Double(30, 214, 450, 214));
				line4.draw(new Line2D.Double(30, 304, 450, 304));
			}
		}; 
		/** Construct panel */
		container.setLayout(null);
		
		/** Message for file help */
		fileMessage = new JLabel("Select a file");
		fileMessage.setBounds(30, 100, 300, 20);
		fileMessage.setOpaque(true);
		container.add(fileMessage);
		
		radioButtonMessage = new JLabel("Select FILE for single file, DIRECTORY for multiple file");
		radioButtonMessage.setBounds(30, 10, 450, 20);
		container.add(radioButtonMessage);
		
		/** Radio Button Section */
		bgGroup = new ButtonGroup();
		
		single = new JRadioButton("FILE");
		single.setBounds(30, 50, 200, 20);
		single.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent e) {
		      /** Case if FILE is selected */
		      fileMessage.setText("Select a file");
		      txtPath.setText("");
		    }
		});
		container.add(single);
		
		multiple = new JRadioButton("DIRECTORY");
		multiple.setBounds(250, 50, 200, 20);
		multiple.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent e) {
		      /** Case if DIRECTORY is selected */
		      fileMessage.setText("Select a directory");
		      txtPath.setText("");
		    }
		});
		container.add(multiple);
		
		bgGroup.add(single);
		bgGroup.add(multiple);
		single.setSelected(true);
		
		/** Section for selecting file / directory */
		txtPath = new JTextField();
	    txtPath.setBounds(30, 140, 320, 20);
	    container.add(txtPath);
	    
	    btnBrowse = new JButton("Browse");
	    btnBrowse.setBounds(355, 140, 100, 20);
	    container.add(btnBrowse);
	    
	    /** Add an Action Listener for Browse button */
	    btnBrowse.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	          JFileChooser fileChooser = new JFileChooser();

	          // For Directory
	          if (multiple.isSelected())
	            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

	          // For File
	          if (single.isSelected())
	            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

	          fileChooser.setAcceptAllFileFilterUsed(false);

	          int rVal = fileChooser.showOpenDialog(null);
	          if (rVal == JFileChooser.APPROVE_OPTION) {
	            txtPath.setText(fileChooser.getSelectedFile().toString());
	          }
	        }
	      });
	    
	    /** Message for save directory */
		directoryMessage = new JLabel("Select a directory to save this torrent file");
		directoryMessage.setBounds(30, 190, 300, 20);
		container.add(directoryMessage);
		
		/** Section for selecting save directory */
		directoryPath = new JTextField();
	    directoryPath.setBounds(30, 230, 320, 20);
	    container.add(directoryPath);
	    
	    btnBrowse2 = new JButton("Browse");
	    btnBrowse2.setBounds(355, 230, 100, 20);
	    container.add(btnBrowse2);
	    
	    /** Add an Action Listener for Browse button */
	    btnBrowse2.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	          JFileChooser fileChooser2 = new JFileChooser();

	          fileChooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


	          fileChooser2.setAcceptAllFileFilterUsed(false);

	          int rVal = fileChooser2.showOpenDialog(null);
	          if (rVal == JFileChooser.APPROVE_OPTION) {
	            directoryPath.setText(fileChooser2.getSelectedFile().toString());
	          }
	        }
	      });		
		
	    /** Message for file name */
		torrentMessage = new JLabel("Your torrent name");
		torrentMessage.setBounds(30, 280, 300, 20);
		container.add(torrentMessage);		
		
		torrentName = new JTextField();
	    torrentName.setBounds(30, 320, 320, 20);
	    container.add(torrentName);
	    
		extMessage = new JLabel(".keima");
		extMessage.setBounds(360, 320, 50, 20);
		container.add(extMessage);
		
		/** Message for Tracker */
		trackerMessage = new JLabel("Tracker Link : ");
		trackerMessage.setBounds(30, 380, 100, 20);
		container.add(trackerMessage);		
		
		trackerName = new JTextField(TorrentPreferences.defaultTracker);
	    trackerName.setBounds(130, 380, 320, 20);
	    container.add(trackerName);
		
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
	        		/** Create a torrent file */
	        		createNewTorrent();
	        		/** Close panel */
	        		frame.dispose();
	        	} else {
	        		/** Show Alert Message */
	       		  if (res == -1) JOptionPane.showMessageDialog(container, "Fields are null");
	       		  if (res == 1)  JOptionPane.showMessageDialog(container, "File / Directory Path is empty");
	       		  if (res == 2)  JOptionPane.showMessageDialog(container, "Save Path is empty");
	       		  if (res == 3)  JOptionPane.showMessageDialog(container, "Torrent file name is empty");
	       		  if (res == 4)  JOptionPane.showMessageDialog(container, "File / Directory Path is not exist");
	       		  if (res == 5)  JOptionPane.showMessageDialog(container, "Save Path is not exist");
	       		  if (res == 6)  JOptionPane.showMessageDialog(container, "Tracker Link is empty");
	        	}
	        }
	    });	
	    
        /** Add panel to screen */
        add(container);
        setTitle("Add New Torrent");
        setSize(500, 500); //Set UI resolution
        setLocationRelativeTo(null); //Set UI Relative Location
        setVisible(true); //Set UI Visibility
        setResizable(false); //Set UI Resize-ability
	}
	
	private int validateInput() {
		//txtPath, directoryPath, torrentName
		int retVal = 0;
		if ((txtPath != null) && (directoryPath != null) && (torrentName != null)) {
			if (txtPath.getText().trim().isEmpty()) return 1; /* Error code 1 */
			if (directoryPath.getText().trim().isEmpty()) return 2; /* Error code 2 */
			if (torrentName.getText().trim().isEmpty()) return 3; /* Error code 3 */
			
			File folderExist = new File(txtPath.getText());
			if (!folderExist.exists()) return 4; /* Error code 4 */
			
			File folderExist2 = new File(directoryPath.getText());
			if (!folderExist2.exists()) return 5; /* Error code 5 */
			
			if (trackerName.getText().trim().isEmpty()) return 6; /* Error code 6 */
			
		} else retVal = -1; /* Null entry */
		return retVal;
	}
	
	/** Write all torrents information to destinated file */
    private void createNewTorrent(){
    	String Path = directoryPath.getText() + FILE_SEPARATOR + torrentName.getText() + EXTENSION_SEPARATOR;
    	File f = new File(Path);
    	if (!f.exists()) {
        	try {
    			f.createNewFile();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}	
    	}
    	
    	FileWriter fw;
		try {
			fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			/** Add file name */
			File file = new File(txtPath.getText());
			bw.write(file.getName() + "\n");
			
			/** Add number of File and file informations */
			if (single.isSelected()) {
				bw.write("1\n");
				
				/** file informations */
				
				/** file relative name */
				bw.write(file.getName() + "\n");
				
				/** file length */
				long size = FileHelper.pieceCount(file.length(), TorrentPreferences.blockSize);
				bw.write(size + "\n");
				
				/** file SHA pieces */
				ArrayList<String> fileSHA = FileHelper.fileTorrentSHA1(file.getAbsolutePath(), size);
				for (int i = 0; i < size; i++) bw.write((i+1) + " : " + fileSHA.get(i) + "\n");
				
				/** main file SHA */
				bw.write(FileHelper.mainTorrentSHA1(fileSHA) + "\n");
			}
			if (multiple.isSelected()) {
				FileWalker fWalker = new FileWalker();
				int length = fWalker.walkCount(txtPath.getText());
				bw.write(length + "\n");
				
				/** file informations */
				fWalker.walk(txtPath.getText(), file.getAbsolutePath(), bw);
			}
			
			/** Add tracker / announce link */
			bw.write (trackerName.getText() + "\n");
			
			/** Add Credits */
			bw.write("Created by freedomofkeima");
			
			System.out.println("New Torrent : " + file.getName() + " processed!");
			
			bw.close();
			
			/** Checking whether your created torrent has been added previously */
		    ArrayList<String> dbTemp = UserInterface.getInstance();
		    for (int i = 0; i < dbTemp.size(); i++) {
		    	if (dbTemp.get(i).equals(file.getName())) {
		    		JOptionPane.showMessageDialog(container, "You're having this torrent previously! Torrent doesn't automatically added to your list!");
		    		return;
		    	}
		    }
		    
			/** Load Torrent to list */
			File tempFile = new File(f.getAbsolutePath());

			if (single.isSelected()) FileHelper.writeTempTorrentInformation(f, tempFile.getParent(), 1);
			else FileHelper.writeTempTorrentInformation(f, txtPath.getText(), 1);
			
		    UserInterface.setDatabase(file.getName());
		    
		    /** Rewrite ArrayList to db.txt */
			FileHelper.writeDatabaseFile();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}
