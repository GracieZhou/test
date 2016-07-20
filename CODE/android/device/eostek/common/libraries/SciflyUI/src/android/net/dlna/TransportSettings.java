package android.net.dlna;

public class TransportSettings {
	public TransportSettings() {

	}

	public TransportSettings(PlayMode play_mode,
			RecordQualityMode rec_quality_mode) {
		super();
		this.play_mode = play_mode;
		this.rec_quality_mode = rec_quality_mode;
	}

	public PlayMode getPlayMode() {
		return play_mode;
	}

	public void setPlayMode(PlayMode play_mode) {
		this.play_mode = play_mode;
	}

	public RecordQualityMode getRecordQualityMode() {
		return rec_quality_mode;
	}

	public void setRecordQualityMode(RecordQualityMode rec_quality_mode) {
		this.rec_quality_mode = rec_quality_mode;
	}

	private PlayMode play_mode;
	private RecordQualityMode rec_quality_mode;
}
