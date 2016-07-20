package android.net.dlna;

public class ShareContainer extends ShareObject {
	public ShareContainer() {

	}

	public String toString() {
		return "ShareContainer [id=" + super.getID() + ", parent_id="
				+ super.getParentID() + ", title=" + super.getTitle() + "]";
	}
}