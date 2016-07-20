package android.net.dlna;

import java.util.ArrayList;

public class BrowseResult {
	public BrowseResult() {

	}

	public BrowseResult(ArrayList<ShareObject> share_objects, int total_matches) {
		super();
		this.share_objects = share_objects;
		this.total_matches = total_matches;
	}

	/**
	 * get protocol information
	 * 
	 * @return protocol information
	 * */
	public ArrayList<ShareObject> getShareObjects() {
		return share_objects;
	}

	/**
	 * set protocol information
	 * 
	 * @param protocol
	 *            information
	 * */
	public void setShareObjects(ArrayList<ShareObject> share_objects) {
		this.share_objects = share_objects;
	}

	/**
	 * get network information
	 * 
	 * @return network information
	 * */
	public int getTotalMatches() {
		return this.total_matches;
	}

	/**
	 * set network information
	 * 
	 * @param network
	 *            information
	 * */
	public void setTotalMatches(int total_matches) {
		this.total_matches = total_matches;
	}

	private ArrayList<ShareObject> share_objects;
	private int total_matches;
}
