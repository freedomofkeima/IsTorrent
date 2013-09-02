import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;


@SuppressWarnings("serial")
public class TableModel extends AbstractTableModel {
	
	public static final int ID_INDEX = 0;
	public static final int NAME_INDEX = 1;
	public static final int SIZE_INDEX = 2;
	public static final int STATUS_INDEX = 3;
	public static final int ADDED_INDEX = 4;
	public static final int COMPLETED_INDEX = 5;
	public static final int HIDDEN_INDEX = 6;
	
	protected String[] columnNames;
	protected Vector<TorrentModel> dataVector;
	
	public TableModel(String[] columnNames, ArrayList<TorrentModel> data) {
		this.columnNames = columnNames;
		dataVector = new Vector<TorrentModel>();
		for (int i = 0; i < data.size(); i++) {
			dataVector.add(data.get(i));
		}
	}
	
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int column) {
        switch (column) {
        case ID_INDEX:
        case NAME_INDEX:
        case SIZE_INDEX:
        case STATUS_INDEX:
        case ADDED_INDEX:
        case COMPLETED_INDEX:
           return String.class;
        default:
           return Object.class;
        }
    }
    
    @Override
    public Object getValueAt(int row, int column) {
    	TorrentModel record = null;
    	if (dataVector.size() > row) record = dataVector.get(row);
    	if (record != null) {
        	switch (column) {
        	case ID_INDEX:
        		return record.getId();
        	case NAME_INDEX:
        		return record.getTorrentName();
        	case SIZE_INDEX:
        		return record.getSize();
        	case STATUS_INDEX:
        		if (record.getFinishDate() != null) {
        			if (record.getFinishDate().before(record.getAddDate())) return "Incomplete";
        			else return "Complete";
        		} else return "Incomplete";
        	case ADDED_INDEX:
        		return record.getAddDate();
        	case COMPLETED_INDEX:
        		return record.getFinishDate();
        	  default:
        		  return new Object();
        	}	
    	} else return new Object();
    }

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return dataVector.size();
	}
	
    public boolean hasEmptyRow() {
        if (dataVector.size() == 0) return false;
        TorrentModel record = dataVector.get(dataVector.size() - 1);
        if (record.getTorrentName().trim().equals(""))
        {
           return true;
        }
        else return false;
    }
	
    public void addEmptyRow() {
    	dataVector.add(new TorrentModel());
    	 fireTableRowsInserted(dataVector.size() - 1, dataVector.size() - 1);
    }
    
    public void addRow(TorrentModel _torrentModel) {
    	dataVector.add(_torrentModel);
   	    fireTableRowsInserted(dataVector.size() - 1, dataVector.size() - 1);   	
    }
    
    public void refreshTable(ArrayList<TorrentModel> _data) {
    	dataVector.clear();
    	for (int i = 0; i < _data.size(); i++) {
    		dataVector.add(_data.get(i));
    	}
    	this.fireTableDataChanged();
    }

}
