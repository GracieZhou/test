package android.net.dlna;

public class VoiceSearchState {
	public VoiceSearchState() {

	}

	public VoiceSearchState(int state, String descrpition) {
		super();
		this.state = state;
		this.descrpition = descrpition;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDescription() {
		return descrpition;
	}

	public void setDescription(String descrpition) {
		this.descrpition = descrpition;
	}

	public String toString() {
		return "VoiceSearchState [ state =" + state
				+ ", descrpition " + descrpition + "]";
	}

	private int state; // / Currently voice state
	private String descrpition; // / state descrpition
}
