package com.utsmta.common;

public interface IToolKit {
	/**
	 * 
	 * @param serialNo
	 */
	public boolean setSerialNumber(String serialNo);
	
	/**
	 * 
	 * @return
	 */
	public String getSerialNumber();
	
	/**
	 * 
	 * @param macAddress
	 */
	public boolean setMacAddress(String macAddress);
	
	/**
	 * 
	 * @return
	 */
	public String getMacAddress();
	
	/**
	 * 
	 * @return
	 */
	public String getFetureCode();
	
	/**
	 * 
	 */
	public void shutdownSystem();
}
