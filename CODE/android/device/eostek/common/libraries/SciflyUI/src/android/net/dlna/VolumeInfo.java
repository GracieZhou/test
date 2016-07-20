package android.net.dlna;

public class VolumeInfo {
	public VolumeInfo() {

	}

	public VolumeInfo(Channel channel, int current_volume) {
		super();
		this.channel = channel;
		this.current_volume = current_volume;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public int getCurrentVolume() {
		return current_volume;
	}

	public void setCurrentVolume(int current_volume) {
		this.current_volume = current_volume;
	}

	public String toString() {
		return "VolumeInfo [ channel=" + channel.toString()
				+ " current_volume " + current_volume + "]";
	}

	private Channel channel; // / Currently playing track, only support the
								// master channel
	private int current_volume; // / The volume size, range is 0-100
}
