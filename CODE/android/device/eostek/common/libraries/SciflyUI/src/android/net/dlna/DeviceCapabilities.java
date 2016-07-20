package android.net.dlna;

public class DeviceCapabilities {
	public DeviceCapabilities() {

	}

	public DeviceCapabilities(String play_media, String rec_media,
			String rec_quality_modes) {
		super();
		this.play_media = play_media;
		this.rec_media = rec_media;
		this.rec_quality_modes = rec_quality_modes;
	}

	/**
	 * get capable of playing media
	 * 
	 * @return capable of playing media
	 */
	public String getPlayMedia() {
		return play_media;
	}

	/**
	 * set capable of playing media
	 * 
	 * @param capable
	 *            of playing media
	 */
	public void setPlayMedia(String play_media) {
		this.play_media = play_media;
	}

	/**
	 * get capable of record media
	 * 
	 * @return capable of record media
	 */
	public String getRecMedia() {
		return rec_media;
	}

	/**
	 * set capable of record media
	 * 
	 * @param capable
	 *            of record media
	 */
	public void setRecMedia(String rec_media) {
		this.rec_media = rec_media;
	}

	/**
	 * get capable of record Quality model
	 * 
	 * @return capable of record Quality model
	 */
	public String getRecQualityModes() {
		return rec_quality_modes;
	}

	/**
	 * set capable of record Quality model
	 * 
	 * @param capable
	 *            of record Quality model
	 */
	public void setRecQualityModes(String rec_quality_modes) {
		this.rec_quality_modes = rec_quality_modes;
	}

	private String play_media;
	private String rec_media;
	private String rec_quality_modes;
};
