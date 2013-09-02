import java.util.ArrayList;


public class TorrentFile {
	
		private String torrentFileName;
		private int pieceSize;
		private ArrayList<String> pieceInformation;
		private ArrayList<String> pieceHash;
		private String mainHash;
		
		public TorrentFile() {
			torrentFileName = "";
			pieceSize = -1;
			pieceInformation = new ArrayList<String>();
			pieceHash = new ArrayList<String>();
			mainHash = "";
		}

		public String getTorrentFileName() {
			return torrentFileName;
		}

		public void setTorrentFileName(String torrentFileName) {
			this.torrentFileName = torrentFileName;
		}

		public int getPieceSize() {
			return pieceSize;
		}

		public void setPieceSize(int pieceSize) {
			this.pieceSize = pieceSize;
		}

		public ArrayList<String> getPieceInformation() {
			return pieceInformation;
		}

		public void setPieceInformation(ArrayList<String> pieceInformation) {
			this.pieceInformation = pieceInformation;
		}
		
		public void setPieceInformation(String pieceInformation, int offset) {
			this.pieceInformation.set(offset, pieceInformation);
		}
		
		public void setPieceInformation(String pieceInformation) {
			this.pieceInformation.add(pieceInformation);
		}

		public ArrayList<String> getPieceHash() {
			return pieceHash;
		}

		public void setPieceHash(ArrayList<String> pieceHash) {
			this.pieceHash = pieceHash;
		}
		
		public void setPieceHash(String pieceHash) {
			this.pieceHash.add(pieceHash);
		}

		public String getMainHash() {
			return mainHash;
		}

		public void setMainHash(String mainHash) {
			this.mainHash = mainHash;
		}
		
}
