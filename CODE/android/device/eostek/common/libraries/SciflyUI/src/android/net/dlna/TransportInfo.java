package android.net.dlna;

public class TransportInfo {
	public TransportInfo() {

	}

	public TransportInfo(TransportState current_transport_state,
			TransportStatus current_transport_status, String current_speed) {
		super();
		this.current_transport_state = current_transport_state;
		this.current_transport_status = current_transport_status;
		this.current_speed = current_speed;
	}

	public TransportState getCurrentTransportState() {
		return current_transport_state;
	}

	public void setCurrentTransportState(TransportState current_transport_state) {
		this.current_transport_state = current_transport_state;
	}

	public TransportStatus getCurrentTransportStatus() {
		return current_transport_status;
	}

	public void setCurrentTransportStatus(
			TransportStatus current_transport_status) {
		this.current_transport_status = current_transport_status;
	}

	public String getCurrentSpeed() {
		return current_speed;
	}

	public void setCurrentSpeed(String current_speed) {
		this.current_speed = current_speed;
	}

	TransportState current_transport_state; // /transport state��Refer to the
											// DLNA_TransportState attribute
											// description
	TransportStatus current_transport_status;// /transport status��Refer to the
												// DLNA_TransportStatus
												// attribute description
	String current_speed; // /speed
}
