package android.net.dlna;

import java.util.*;

public class ShareItem extends ShareObject {
	public ShareItem() {

	}

	public ArrayList<ShareResource> getShareResource() {
		return share_resource;
	}

	private ArrayList<ShareResource> share_resource;
}