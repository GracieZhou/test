package android.net.dlna;

public class DeviceInfo {
	public DeviceInfo() {

	}

	public DeviceInfo(String device_type, String friendly_name,
			String manufacturer, String manufacturer_url,
			String model_description, String model_name, String model_number,
			String model_url, String serial_number, String udn, String upc,
			String location, String address, int port) {
		super();
		this.device_type = device_type;
		this.friendly_name = friendly_name;
		this.manufacturer = manufacturer;
		this.manufacturer_url = manufacturer_url;
		this.model_description = model_description;
		this.model_name = model_name;
		this.model_number = model_number;
		this.model_url = model_url;
		this.serial_number = serial_number;
		this.udn = udn;
		this.upc = upc;
		this.location = location;
		this.address = address;
		this.port = port;
	}

	@Override
	public String toString() {
		return "DeviceInfo [device_type=" + device_type + ", friendly_name="
				+ friendly_name + ", manufacturer=" + manufacturer
				+ ", manufacturer_url=" + manufacturer_url
				+ ", model_description=" + model_description + ", model_name="
				+ model_name + ", model_number=" + model_number
				+ ", model_url=" + model_url + ", serial_number="
				+ serial_number + ", udn=" + udn + ", upc=" + upc
				+ ", location=" + location + ", address=" + address + ", port="
				+ port + "]";
	}

	/**
	 * get device type
	 * 
	 * @return device_type
	 */
	public String getDeviceType() {
		return device_type;
	}

	/**
	 * set device type
	 * 
	 * @param device_type
	 */
	public void setDeviceType(String device_type) {
		this.device_type = device_type;
	}

	/**
	 * get friendly name
	 * 
	 * @return friendly_name
	 */
	public String getFriendlyName() {
		return friendly_name;
	}

	/**
	 * set friendly name
	 * 
	 * @param friendly_name
	 */
	public void setFriendlyName(String friendly_name) {
		this.friendly_name = friendly_name;
	}

	/**
	 * get manu facturer
	 * 
	 * @return manufacturer
	 */
	public String getManufacturer() {
		return manufacturer;
	}

	/**
	 * set manu facturer
	 * 
	 * @param manufacturer
	 */
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	/**
	 * get URL of manu facturer
	 * 
	 * @return manufacturer_url
	 */
	public String getManufacturerURL() {
		return manufacturer_url;
	}

	/**
	 * set URL of manu facturer
	 * 
	 * @param manufacturer_url
	 */
	public void setManufacturerURL(String manufacturer_url) {
		this.manufacturer_url = manufacturer_url;
	}

	/**
	 * get model description
	 * 
	 * @return model_description
	 */
	public String getModelDescription() {
		return model_description;
	}

	/**
	 * set model description
	 * 
	 * @param model_description
	 */
	public void setModelDescription(String model_description) {
		this.model_description = model_description;
	}

	/**
	 * get model name
	 * 
	 * @return model_name
	 */
	public String getModelName() {
		return model_name;
	}

	/**
	 * set model name
	 * 
	 * @param model_name
	 */
	public void setModelName(String model_name) {
		this.model_name = model_name;
	}

	/**
	 * get model munber
	 * 
	 * @return model_number
	 */
	public String getModelNumber() {
		return model_number;
	}

	/**
	 * set model number
	 * 
	 * @param model_number
	 */
	public void setModelNumber(String model_number) {
		this.model_number = model_number;
	}

	/**
	 * get URL of model
	 * 
	 * @return model_url
	 */
	public String getModelURL() {
		return model_url;
	}

	/**
	 * set URL of model
	 * 
	 * @param model_url
	 */
	public void setModelURL(String model_url) {
		this.model_url = model_url;
	}

	/**
	 * get serial mumber
	 * 
	 * @return serial_number
	 */
	public String getSerialNumber() {
		return serial_number;
	}

	/**
	 * set serial number
	 * 
	 * @param serial_number
	 */
	public void setSerialNumber(String serial_number) {
		this.serial_number = serial_number;
	}

	/**
	 * get UDN
	 * 
	 * @return udn
	 */
	public String getUDN() {
		return udn;
	}

	/**
	 * set UDN
	 * 
	 * @param udn
	 */
	public void setUDN(String udn) {
		this.udn = udn;
	}

	/**
	 * get UPC
	 * 
	 * @return upc
	 */
	public String getUPC() {
		return upc;
	}

	/**
	 * set UPC
	 * 
	 * @param upc
	 */
	public void setUPC(String upc) {
		this.upc = upc;
	}

	/**
	 * Get download address of device description file
	 * 
	 * @param location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * set download address of device description file
	 * 
	 * @param location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * get IP
	 * 
	 * @param address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * set IP
	 * 
	 * @param address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * get port
	 * 
	 * @param port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * set port
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	private String device_type; // /device type
	private String friendly_name; // /friendly name
	private String manufacturer; // /manu facturer
	private String manufacturer_url; // /manu facturer URL
	private String model_description; // /model description
	private String model_name; // /model name
	private String model_number; // /model number
	private String model_url; // /model URL
	private String serial_number; // /serial number
	private String udn; // /unique device name
	private String upc; // /universal product code
	private String location; // /download address of device description file
	private String address; // /IP
	private int port; // /port
}
