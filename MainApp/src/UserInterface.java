/*
 * User Interface Class
 * @freedomofkeima - Iskandar Setiadi ( iskandarsetiadi@students.itb.ac.id )
 * July 2013
 *
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;


@SuppressWarnings("serial")
public class UserInterface extends JFrame {
	
	/** List of Attributes */
	private JPanel container; /* Main application panel */
	private JPanel downloadMenu, downloadList, downloadInfo; /* Panels */
	private JSplitPane splitPane2, splitPane3; /* Split panel */
	private JMenuBar menuBar; /* Main Menu Bar */
	private JMenu file, preference, help; /* Menu list */
	private JMenuItem menuItem; /* Menu Item */
	private static ArrayList<TorrentModel> data;
	
	public static final String[] columnNames = {"ID", "Name", "#Files", "Status", "Added", "Completed On", ""};
	protected static JTable table;
	protected JScrollPane scroller;
	protected static TableModel tableModel;
	
	private static ArrayList<String> db; /* Database entries */
	
	public UserInterface() {
		initSingleton(); //initialize Singleton
		try {
			Thread.sleep(100); //Sleep for 100ms, assuring that data has been loaded
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initComponents(); //initialize Screen
	}
	
	private static void initSingleton() {
		if (db == null) db = new ArrayList<String>();

		String sCurrentLine;
		BufferedReader br;
		File f = new File(TorrentPreferences.saveDirectory + "/db/db.dat");
		if (!f.exists()) {
			/** Initialize file if file is not found */
			try {
				f.createNewFile();
				PrintWriter oFile = new PrintWriter(f);
				oFile.println("@end");
				oFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/** Read all db.txt data */
		
		if (data == null) data = new ArrayList<TorrentModel>();
		
		try {
			br = new BufferedReader(new FileReader(f));
			int count = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				count++; //increment count
				
				TorrentModel t = new TorrentModel();
				if (!sCurrentLine.equals("@end")) {
					t = getTorrentModelFromDatabase(count);
					data.add(t);
					new DownloadService(t); // Activate Download Service
				}
				
				db.add(sCurrentLine);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> getInstance() {
		if (db == null) initSingleton();
		return db;
	}
	
	public static void setDatabase(String text) {
		if (db == null) initSingleton();
		db.set(db.size() - 1, text);
		db.add("@end"); //marker
		
		/** Add data here */
		TorrentModel t = new TorrentModel();
		t = getTorrentModelFromDatabase(db.size() - 1);
		data.add(t);
		tableModel.addRow(data.get(data.size() - 1));
		new DownloadService(t);  // Activate Download Service
	}
	
	/**
	 * Return a TorrentModel from .dat
	 */
	public static TorrentModel getTorrentModelFromDatabase(int _id) {
		TorrentModel t = new TorrentModel(); //return value
		String sCurrentLine;
		BufferedReader br;
		File f = new File(TorrentPreferences.saveDirectory + "/db/" + _id + ".dat");
		t.setId(_id);
		
		try {
			br = new BufferedReader(new FileReader(f));
			int count = 0; int localCount = 0; int secondLocalCount = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				count++; // increment count
				
				/** 1st line defines Torrent Name */
				if (count == 1) t.setTorrentName(sCurrentLine);
				
				/** 2nd line defines Torrent Add Date */
				if (count == 2) {
					try {
						t.setAddDate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(sCurrentLine));
					} catch (ParseException e) {
						e.printStackTrace();
					}					
				}
				
				/** 3rd line defines Torrent Finish Date (if already finished) */
				if (count == 3) {
					if (sCurrentLine.trim().isEmpty()) t.setFinishDate(null);
					else {
						try {
							t.setFinishDate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(sCurrentLine));
						} catch (ParseException e) {
							e.printStackTrace();
						}							
					}
				}
				
				/** 4th line defines main directory path */
				if (count == 4) t.setMainDirectory(sCurrentLine);
				
				/** 5th line defines torrent file size */
				if (count == 5) t.setSize(Integer.parseInt(sCurrentLine));
				
				if ((count > 5) && (localCount != t.getSize())) {
					localCount++;
					TorrentFile myTorrentFile = new TorrentFile();
					/** Set file name */
					myTorrentFile.setTorrentFileName(sCurrentLine);
					sCurrentLine = br.readLine();
					if (sCurrentLine == null) return null;
					/** Set number of pieces */
					myTorrentFile.setPieceSize(Integer.parseInt(sCurrentLine));
					sCurrentLine = br.readLine();
					if (sCurrentLine == null) return null;
					/** Set pieces informations */
					for (int i = 0; i < myTorrentFile.getPieceSize(); i++) {
						String[] parts = sCurrentLine.split(" ");
						myTorrentFile.setPieceInformation(parts[0]);
						myTorrentFile.setPieceHash(parts[3]);
						sCurrentLine = br.readLine();
						if (sCurrentLine == null) return null;						
					}
					/** Set main hash */
					myTorrentFile.setMainHash(sCurrentLine);
				    t.setTorrentFile(myTorrentFile);	
				} else if (localCount == t.getSize()) {
					secondLocalCount++;
					
					/** 1st line = tracker information */
					if (secondLocalCount == 1) t.setTracker(sCurrentLine);
				}

			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return t;
	}
	

	private synchronized void initComponents() {
        container = new JPanel(); /* construct panel */
        //container.setLayout(null);
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        
        /** Divide downloadPane into 3 sections : downloadMenu, downloadList, downloadInfo */
        downloadMenu = new JPanel();
        downloadMenu.setBorder(BorderFactory.createLineBorder(Color.black));       
 
        downloadList = new JPanel();
        downloadList.setBorder(BorderFactory.createLineBorder(Color.black));    
        
        downloadInfo = new JPanel();
        downloadInfo.setBorder(BorderFactory.createLineBorder(Color.black));
        
        splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, downloadMenu, downloadList);
        splitPane2.setResizeWeight(0.15);
        
        splitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane2, downloadInfo);
        splitPane3.setResizeWeight(0.7);
        
        /** Add split pane */
        container.add(splitPane3);
        
        //Create tables
        tableModel = new TableModel(columnNames, data);
        tableModel.addTableModelListener(new UserInterface.ITableModelListener());
        table = new JTable();
        table.setModel(tableModel);
        table.setSurrendersFocusOnKeystroke(true);
        
        scroller = new javax.swing.JScrollPane(table);
        table.setPreferredScrollableViewportSize(new java.awt.Dimension(500, 300));
        table.getColumnModel().getColumn(TableModel.ID_INDEX).setPreferredWidth(10);
        table.getColumnModel().getColumn(TableModel.NAME_INDEX).setPreferredWidth(200);
        TableColumn hidden = table.getColumnModel().getColumn(TableModel.HIDDEN_INDEX);
        hidden.setMinWidth(2);
        hidden.setPreferredWidth(2);
        hidden.setMaxWidth(2);
        hidden.setCellRenderer(new Renderer(TableModel.HIDDEN_INDEX));
        
        downloadList.setLayout(new BorderLayout());
        downloadList.add(scroller, BorderLayout.CENTER);
        
        //Create the menu bar.
        menuBar = new JMenuBar();
        
        //Build menu
        file = new JMenu("File");
        menuBar.add(file);
        
        preference = new JMenu("Preferences");
  
        menuBar.add(preference);
        
        help = new JMenu("Help");
        menuBar.add(help);
        
        //A group of JMenu Items
        menuItem = new JMenuItem("Add Torrent");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Add a new Torrent to IsTorrent");
        menuItem.addActionListener(new ActionListener() {  
     	   //Handle JMenuItem event if mouse is clicked.  
     	   public void actionPerformed(ActionEvent event) {  
     	     /* Add Torrent */
     		  new AddTorrent();
     	   }  
     	  }  
        );  
        file.add(menuItem);

        menuItem = new JMenuItem("Create New Torrent");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Create a new IsTorrent file");
        menuItem.addActionListener(new ActionListener() {  
     	   //Handle JMenuItem event if mouse is clicked.  
     	   public void actionPerformed(ActionEvent event) {  
     	     /* Create a new IsTorrent File */
     	     new NewTorrent();
     	   }  
     	  }  
        );  
        file.add(menuItem);
        
        menuItem = new JMenuItem("Exit");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Close Application");
        menuItem.addActionListener(new ActionListener() {  
        	   //Handle JMenuItem event if mouse is clicked.  
        	   public void actionPerformed(ActionEvent event) {
        		 /* Exit application */
        		 System.out.println("--Goodbye (@freedomofkeima)--");
        	     System.exit(0);
        	   }  
        	  }  
        );  
        file.add(menuItem);
        
        menuItem = new JMenuItem("Open Preferences");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "IsTorrent preferences and stuff");
        menuItem.addActionListener(new ActionListener() {  
        	   //Handle JMenuItem event if mouse is clicked.  
        	   public void actionPerformed(ActionEvent event) {
        		 /* Open IsTorrent Preferences */
          		  JOptionPane.showMessageDialog(container, "Open Preferences is clicked");
        	   }  
        	  }  
        );  
        preference.add(menuItem);       
 
        menuItem = new JMenuItem("Statistics & About Us");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Statistics and About Us");
        menuItem.addActionListener(new ActionListener() {  
        	   //Handle JMenuItem event if mouse is clicked.  
        	   public void actionPerformed(ActionEvent event) {
        		 /* Open IsTorrent Preferences */
          		  JOptionPane.showMessageDialog(container, "Statistics & About Us is clicked");
          		  printTorrentInformation();
        	   }  
        	  }  
        ); 
        help.add(menuItem);
        
        /** Set menu Bar */
        setJMenuBar(menuBar);
        
        /** Add panel to screen */
        add(container);
        setTitle("IsTorrent : Created by freedomofkeima");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 600); //Set UI resolution
        setLocationRelativeTo(null); //Set UI Relative Location
        setVisible(true); //Set UI Visibility
        setResizable(false); //Set UI Resize-ability
	}

	public static ArrayList<TorrentModel> getData() {
		return data;
	}
	
	/**
	 * Print torrent informations to Command Prompt / Terminal
	 */
	protected void printTorrentInformation() {
		System.out.println("Data size: " + data.size());
		
		for (int i = 0; i < data.size(); i++) {
			System.out.println("Torrent Name : " + data.get(i).getTorrentName());
			System.out.println("Torrent Number of Files : " + data.get(i).getTorrentFile().size());
			System.out.println("Torrent Add Date : " + data.get(i).getAddDate());
		    
			System.out.println("");
			
			for (int j = 0; j < data.get(i).getTorrentFile().size(); j++) {
				System.out.println("Torrent File Name : " + data.get(i).getTorrentFile().get(j).getTorrentFileName());
				System.out.println("Torrent File Pieces : " + data.get(i).getTorrentFile().get(j).getPieceSize());
			}
			
			System.out.println("");
		}
	}
	
	
	public static TableModel getTableModel() {
		return tableModel;
	}

	public static void setTableModel(TableModel tableModel) {
		UserInterface.tableModel = tableModel;
	}

	public static JTable getTable() {
		return table;
	}

	public static void setTable(JTable table) {
		UserInterface.table = table;
	}

	/**
	 * Highlight last row
	 * @param row
	 */
    public void highlightLastRow(int row) {
        int lastrow = tableModel.getRowCount();
        if (row == lastrow - 1) {
            table.setRowSelectionInterval(lastrow - 1, lastrow - 1);
        } else {
            table.setRowSelectionInterval(row + 1, row + 1);
        }

        table.setColumnSelectionInterval(0, 0);
    }
	
    /**
     * Renderer Class
     *
     */
	class Renderer extends DefaultTableCellRenderer {
        protected int column;

        public Renderer(int _column) {
            column = _column;
        }

        public Component getTableCellRendererComponent(JTable table,
           Object value, boolean isSelected, boolean hasFocus, int row,
           int _column)
        {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == _column && hasFocus) {
                if ((UserInterface.tableModel.getRowCount() - 1) == row &&
                   !UserInterface.tableModel.hasEmptyRow())
                {
                    UserInterface.tableModel.addEmptyRow();
                }

                highlightLastRow(row);
            }

            return c;
        }
	}

	/**
	 * ITableModelListener
	 *
	 */
	public class ITableModelListener implements TableModelListener {
	
		@Override
		public void tableChanged(TableModelEvent evt) {
	        if (evt.getType() == TableModelEvent.UPDATE) {
	            int column = evt.getColumn();
	            int row = evt.getFirstRow();
	           // System.out.println("row: " + row + " column: " + column);
	            table.setColumnSelectionInterval(column + 1, column + 1);
	            table.setRowSelectionInterval(row, row);
	        }
		}
		
		
	}

}
