package android.net.dlna;

public class MediaInfo {
	public MediaInfo() {

	}

	public MediaInfo(int nr_tracks, String media_duration, String current_uri,
			String current_uri_metadata, String next_uri,
			String next_uri_metadata, String play_medium, String record_medium,
			String write_status) {
		super();
		this.nr_tracks = nr_tracks;
		this.media_duration = media_duration;
		this.current_uri = current_uri;
		this.current_uri_metadata = current_uri_metadata;
		this.next_uri = next_uri;
		this.next_uri_metadata = next_uri_metadata;
		this.play_medium = play_medium;
		this.record_medium = record_medium;
		this.write_status = write_status;
	}

	public int getNRTracks() {
		return nr_tracks;
	}

	public void setNRTracks(int nr_tracks) {
		this.nr_tracks = nr_tracks;
	}

	public String getMediaDuration() {
		return media_duration;
	}

	public void setMediaDuration(String media_duration) {
		this.media_duration = media_duration;
	}

	public String getCurrentURI() {
		return current_uri;
	}

	public void setCurrentURI(String current_uri) {
		this.current_uri = current_uri;
	}

	public String getCurrentURIMetadata() {
		return current_uri_metadata;
	}

	public void setCurrentURIMetadata(String current_uri_metadata) {
		this.current_uri_metadata = current_uri_metadata;
	}

	public String getNextURI() {
		return next_uri;
	}

	public void setNextURI(String next_uri) {
		this.next_uri = next_uri;
	}

	public String getNextURIMetadata() {
		return next_uri_metadata;
	}

	public void setNextURIMetadata(String next_uri_metadata) {
		this.next_uri_metadata = next_uri_metadata;
	}

	public String getPlayMedium() {
		return play_medium;
	}

	public void setPlayMedium(String play_medium) {
		this.play_medium = play_medium;
	}

	public String getRecordMedium() {
		return record_medium;
	}

	public void setRecordMedium(String record_medium) {
		this.record_medium = record_medium;
	}

	public String getWritestatus() {
		return write_status;
	}

	public void setWritestatus(String write_status) {
		this.write_status = write_status;
	}

	int nr_tracks; // /track number
	String media_duration; // /media duration��format��HH:MM::SS.F
	String current_uri; // /Current media URI
	String current_uri_metadata; // /Current URI of media metadata
	String next_uri; // /Next media URI
	String next_uri_metadata; // /net URI of media metadata
	String play_medium; // /play media��playing:��NETWORK����Otherwise:��NONE��
	String record_medium; // /must set to ��NONE��
	String write_status; // /must set to ��NOT_WRITABLE��
}
