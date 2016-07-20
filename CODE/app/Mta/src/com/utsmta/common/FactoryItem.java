package com.utsmta.common;

import java.util.Iterator;
import java.util.Properties;

import com.utsmta.utils.LogUtil;

public class FactoryItem extends Properties {
	private final String TAG = "FactoryItem";
	
	private static final long serialVersionUID = 1L;
	
	public static final String WIFI = "wifi";
	
	public static final String BLUETOOTH = "bluetooth";
	
	public static final String TOUCHPAD = "touchpad";
	
	public static final String DLP_POWER = "dlp_power";
	
	public static final String BATTERY_POWER = "battery_power";
	
	public static final String FAN_POWER = "fan_power";
	
	public static final String FAN_SPEED = "fan_speed";
	public static final String AMTA_NTC_TEMP = "amta_ntc_sensor";
	
	public static final String GRAVITY_SENSOR = "gravity_sensor";
	
	public static final String NTC_SENSOR = "ntc_sensor";
	
	public static final String LOUDSPEAKER = "loudspeaker";
	
	public static final String HEADSET = "headset";
	
	public static final String MICROPHONE_IN= "microphone_in";
	
	public static final String LINE_IN = "line_in";
	
	public static final String LINE_OUT = "line_out";
	
	public static final String SPDIF = "spdif";
	
	public static final String VIDEO = "video";
	
	public static final String ETH = "eth";
	
	public static final String USB1 = "usb1";
	
	public static final String USB2 = "usb2";
	public static final String ADC_ADJUST = "adc_adjust";
	public static final String AMTA_USB1 = "amta_usb1";
	public static final String AMTA_USB2 = "amta_usb2";
	
	private String name 	= null;
	
	private boolean result  = false;
	
	private boolean isActive = false;
	
	private int index = 0;
	
	public FactoryItem(String name){
		this.name 	= name;
		this.result = false;
	}
	
	public String getName(){
		return this.name;
	}
	
	public boolean getResult(){
		return this.result;
	}
	
	public void setResult(boolean result) {
		this.result = result;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	
	public boolean isActive(){
		return this.isActive;
	}
	
	public void setActive(boolean active){
		this.isActive = active;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public void printAllProperties(){
		for(Iterator it = keySet().iterator(); it.hasNext();){
			String key = (String) it.next();
			String value = (String) get(key);
			LogUtil.d("SubItem", "subitem key:"+key+" , value:"+value);
		}
	}
}
