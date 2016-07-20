package android.net.dlna;

public class PositionInfo {
	public PositionInfo() {

	}

	public PositionInfo(int track, String track_duration,
			String track_metadata, String track_uri, String rel_time,
			String abs_time, int rel_count, int abs_count) {
		super();
		this.track = track;
		this.track_duration = track_duration;
		this.track_metadata = track_metadata;
		this.track_uri = track_uri;
		this.rel_time = rel_time;
		this.abs_time = abs_time;
		this.rel_count = rel_count;
		this.abs_count = abs_count;
	}

	/**
	 * get track Serial number
	 * 
	 * @return track Serial number
	 * */
	public int getTrack() {
		return track;
	}

	/**
	 * set track Serial number
	 * 
	 * @param track
	 *            Serial number
	 * */
	public void setTrack(int track) {
		this.track = track;
	}

	/**
	 * get track duration��
	 * 
	 * @return duration��format��HH:MM::SS.F
	 * */
	public String getTrackDuration() {
		return track_duration;
	}

	/**
	 * set track duration��
	 * 
	 * @param duration
	 *            ��format��HH:MM::SS.F
	 * */
	public void setTrackDuration(String track_duration) {
		this.track_duration = track_duration;
	}

	/**
	 * get track metadata
	 * 
	 * @return metadata
	 * */
	public String getTrackMetadata() {
		return track_metadata;
	}

	/**
	 * set track metadata
	 * 
	 * @param metadata
	 * */
	public void setTrackMetadata(String track_metadata) {
		this.track_metadata = track_metadata;
	}

	/**
	 * get URI of track
	 * 
	 * @return URI
	 * */
	public String getTrackURI() {
		return track_uri;
	}

	/**
	 * set URI of track
	 * 
	 * @param URI
	 * */
	public void setTrackURI(String track_uri) {
		this.track_uri = track_uri;
	}

	/**
	 * get track time offset��
	 * 
	 * @return interval
	 * */
	public String getRelTime() {
		return rel_time;
	}

	/**
	 * set track time offset��
	 * 
	 * @param interval
	 * */
	public void setRelTime(String rel_time) {
		this.rel_time = rel_time;
	}

	/**
	 * get media time offset
	 * 
	 * @return interval
	 * */
	public String getAbsTime() {
		return abs_time;
	}

	/**
	 * set media time offset
	 * 
	 * @param interval
	 * */
	public void setAbsTime(String abs_time) {
		this.abs_time = abs_time;
	}

	/**
	 * Get currently playing track relative to the track count��
	 * 
	 * @return counter
	 * */
	public int getRelCount() {
		return rel_count;
	}

	/**
	 * set currently playing track relative to the track count��
	 * 
	 * @param counter
	 * */
	public void setRelCount(int rel_count) {
		this.rel_count = rel_count;
	}

	/**
	 * get Currently playing track relative to the media of the timer��
	 * 
	 * @return counter
	 * */
	public int getAbsCount() {
		return abs_count;
	}

	/**
	 * set Currently playing track relative to the media of the timer��
	 * 
	 * @param counter
	 * */
	public void setAbsCount(int abs_count) {
		this.abs_count = abs_count;
	}

	private int track;
	private String track_duration;
	private String track_metadata;
	private String track_uri;
	private String rel_time;
	private String abs_time;
	private int rel_count;
	private int abs_count;
}
