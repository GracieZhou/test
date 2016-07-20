package android.net.dlna;

public class MediaMetaData {
	public MediaMetaData() {

	}

	public MediaMetaData(long size, long duration, int bitrate,
			int sample_frequency, int bits_persample, int nrAudioChannels,
			String resolution, int color_depth, String mime_type) {
		super();
		this.size = size;
		this.duration = duration;
		this.bitrate = bitrate;
		this.sample_frequency = sample_frequency;
		this.bits_persample = bits_persample;
		this.nr_audio_channels = nrAudioChannels;
		this.resolution = resolution;
		this.color_depth = color_depth;
		this.mime_type = mime_type;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getDuration() {
		return duration;
	}

	public String getMIMEType() {
		return mime_type;
	}

	public void setMIMEType(String mime_type) {
		this.mime_type = mime_type;
	}

	/**
	 * @param: duration��Unit:ms
	 * */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getBitrate() {
		return bitrate;
	}

	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}

	public int getSampleFrequency() {
		return sample_frequency;
	}

	public void setSampleFrequency(int sample_frequency) {
		this.sample_frequency = sample_frequency;
	}

	public int getBitsPersample() {
		return bits_persample;
	}

	public void setBitsPersample(int bits_persample) {
		this.bits_persample = bits_persample;
	}

	public int getNrAudioChannels() {
		return nr_audio_channels;
	}

	public void setNrAudioChannels(int nrAudioChannels) {
		this.nr_audio_channels = nrAudioChannels;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public int getColorDepth() {
		return color_depth;
	}

	public void setColorDepth(int color_depth) {
		this.color_depth = color_depth;
	}

	long size;
	long duration;
	int bitrate;
	int sample_frequency;
	int bits_persample;
	int nr_audio_channels;
	String resolution;
	int color_depth;
	String mime_type;
}