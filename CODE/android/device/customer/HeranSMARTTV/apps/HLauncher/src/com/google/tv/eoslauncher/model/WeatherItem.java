package com.google.tv.eoslauncher.model;


/**
 * 天气数据存放类.
 */
public class WeatherItem {

	public int day;
	/** 城市 */
	public String city;
	/** 白天天气 */
	public String status1;
	/** 夜晚天气 */
	public String status2;
	/** 白天风向 */
	public String direction1;
	/** 夜晚风向 */
	public String direction2;
	/** 白天风级 */
	public String power1;
	/** 夜晚风级 */
	public String power2;
	/** 白天温度 */
	public String temperature1;
	/** 夜晚温度 */
	public String temperature2;
	/** 白天体感度 */
	public String tgd1;
	/** 夜晚体感度 */
	public String tgd2;
	/** 紫外线说明 */
	public String zwx_l;
	/** 穿衣说明 */
	public String chy_l;
	/** 污染说明 */
	public String pollution_l;
	/** 运动说明 */
	public String yd_l;
	/** 日期 */
	public String savedate_weather;
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStatus1() {
		return status1;
	}
	public void setStatus1(String status1) {
		this.status1 = status1;
	}
	public String getStatus2() {
		return status2;
	}
	public void setStatus2(String status2) {
		this.status2 = status2;
	}
	public String getDirection1() {
		return direction1;
	}
	public void setDirection1(String direction1) {
		this.direction1 = direction1;
	}
	public String getDirection2() {
		return direction2;
	}
	public void setDirection2(String direction2) {
		this.direction2 = direction2;
	}
	public String getPower1() {
		return power1;
	}
	public void setPower1(String power1) {
		this.power1 = power1;
	}
	public String getPower2() {
		return power2;
	}
	public void setPower2(String power2) {
		this.power2 = power2;
	}
	public String getTemperature1() {
		return temperature1;
	}
	public void setTemperature1(String temperature1) {
		this.temperature1 = temperature1;
	}
	public String getTemperature2() {
		return temperature2;
	}
	public void setTemperature2(String temperature2) {
		this.temperature2 = temperature2;
	}
	public String getTgd1() {
		return tgd1;
	}
	public void setTgd1(String tgd1) {
		this.tgd1 = tgd1;
	}
	public String getTgd2() {
		return tgd2;
	}
	public void setTgd2(String tgd2) {
		this.tgd2 = tgd2;
	}
	public String getZwx_l() {
		return zwx_l;
	}
	public void setZwx_l(String zwx_l) {
		this.zwx_l = zwx_l;
	}
	public String getChy_l() {
		return chy_l;
	}
	public void setChy_l(String chy_l) {
		this.chy_l = chy_l;
	}
	public String getPollution_l() {
		return pollution_l;
	}
	public void setPollution_l(String pollution_l) {
		this.pollution_l = pollution_l;
	}
	public String getYd_l() {
		return yd_l;
	}
	public void setYd_l(String yd_l) {
		this.yd_l = yd_l;
	}
	public String getSavedate_weather() {
		return savedate_weather;
	}
	public void setSavedate_weather(String savedate_weather) {
		this.savedate_weather = savedate_weather;
	}
}
