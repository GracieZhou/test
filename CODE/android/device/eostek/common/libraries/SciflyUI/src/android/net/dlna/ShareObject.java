package android.net.dlna;

public class ShareObject {
	public ShareObject() {

	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getParentID() {
		return parent_id;
	}

	public void setParentID(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	private String id;
	private String parent_id;
	private String title;
	private String metadata;

	@Override
	public String toString() {
		return "ShareObject [id=" + id + ", parent_id=" + parent_id
				+ ", title=" + title + "]";
	}

}
