import java.util.ArrayList;
import java.util.Date;


public class TorrentModel {
	
	private long id;
	private String torrentName;
	private Date addDate;
	private Date finishDate;
	private String mainDirectory;
	private int size;
	private ArrayList<TorrentFile> torrentFile;
	private String tracker;
	
	public TorrentModel() {
		id = -1;
		torrentName = "";
		addDate = new Date();
		finishDate = new Date();
		mainDirectory = "";
		size = -1;
		torrentFile = new ArrayList<TorrentFile>();
		tracker = "";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTorrentName() {
		return torrentName;
	}

	public void setTorrentName(String torrentName) {
		this.torrentName = torrentName;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public String getMainDirectory() {
		return mainDirectory;
	}

	public void setMainDirectory(String mainDirectory) {
		this.mainDirectory = mainDirectory;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public ArrayList<TorrentFile> getTorrentFile() {
		return torrentFile;
	}

	public void setTorrentFile(ArrayList<TorrentFile> torrentFile) {
		this.torrentFile = torrentFile;
	}
	
	public void setTorrentFile(TorrentFile torrentFile) {
		this.torrentFile.add(torrentFile);
	}

	public String getTracker() {
		return tracker;
	}

	public void setTracker(String tracker) {
		this.tracker = tracker;
	}
	

}
