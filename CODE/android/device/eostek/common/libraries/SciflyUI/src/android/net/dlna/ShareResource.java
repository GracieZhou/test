package android.net.dlna;

public class ShareResource {
	public String getURI() {
		return uri;
	}

	public void setURI(String uri) {
		this.uri = uri;
	}

	public ProtocolInfo getProtocolInfo() {
		return protocol_info;
	}

	public void setProtocolInfo(ProtocolInfo protocol_info) {
		this.protocol_info = protocol_info;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	private String uri;
	private ProtocolInfo protocol_info;
	private long size;

}