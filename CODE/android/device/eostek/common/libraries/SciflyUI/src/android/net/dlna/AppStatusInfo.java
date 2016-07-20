package android.net.dlna;

public class AppStatusInfo {
	public AppStatusInfo() {

	}

	public AppStatusInfo(int progress,
			AppStatusState status, String uri) {
		super();
		this.m_progress = progress;
		this.m_status = status;
		this.m_uri = uri;
	}

	public int getAppProgress() {
		return m_progress;
	}

	public void setAppProgress(int progress) {
		this.m_progress = progress;
	}

	public AppStatusState getAppStatus() {
		return m_status;
	}

	public void setAppStatus(
			AppStatusState status) {
		this.m_status = status;
	}

	public String getAppUri() {
		return m_uri;
	}

	public void setAppUri(String uri) {
		this.m_uri = uri;
	}

	int m_progress; // /transport state��Refer to the
											// DLNA_TransportState attribute
											// description
	AppStatusState m_status;// /transport status��Refer to the
												// DLNA_TransportStatus
												// attribute description
	String m_uri; // /speed
}
