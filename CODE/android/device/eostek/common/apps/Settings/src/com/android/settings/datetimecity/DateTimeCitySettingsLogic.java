package com.android.settings.datetimecity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.json.JSONException;
import org.json.JSONObject;
import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;
import com.android.settings.R;
import com.android.settings.SettingsApplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.SntpClient;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.settings.util.Utils;

public class DateTimeCitySettingsLogic {

	private static final String TAG = "TIMEZONE";

	private DateTimeCitySettingsActivity mActivity;

	private final int TIMESYNC_TIMEOUT = 20000;

	private final int MORETIMEOUT = 3600000;

	private final int TIMESYNC_STATUS_SUCCESS = 1;

	private final int CORRECT_TIMEOUT = 60000;

	private boolean mIs24Hour;

	private Context mContext;

	public Calendar mDummyDate;
	private String mNtpServer;

	private boolean isNtpServerAvailable = false;

	private ArrayList<String> mFullProvinceList;

	private static ArrayList<String> mFullCitiesList = new ArrayList<String>();

	public boolean getisNtpServerAvailable() {
		return isNtpServerAvailable;
	}

	private class TimeSyncThread implements Runnable {
		@Override
		public void run() {
			int status = 0;
			Log.d(TAG,
					"<<<<<autoRefreshTime(mNtpServer, TIMESYNC_TIMEOUT)<<"
							+ autoRefreshTime(mNtpServer, TIMESYNC_TIMEOUT));
			if (autoRefreshTime(mNtpServer, TIMESYNC_TIMEOUT)) {
				status = TIMESYNC_STATUS_SUCCESS;
				isNtpServerAvailable = true;
			} else {
				status = CORRECT_TIMEOUT;
				isNtpServerAvailable = false;
			}
		}

	}

	public String getmNtpServer() {
		return mNtpServer;
	}

	public void initmNtpServer() {
		int mNtpServerId = mActivity.getSharedPreferences("settings", 0)
				.getInt("ntp_server_id", 0);
		Log.d(TAG, "mNtpServerId = " + mNtpServerId);
		switch (mNtpServerId) {
		case 0:
			mNtpServer = mActivity.getString(R.string.ntpserver_china);
			break;
		case 1:
			mNtpServer = mActivity.getString(R.string.ntpserver_china);
			break;
		case 2:
			mNtpServer = mActivity.getString(R.string.ntpserver_american);
			break;
		}
	}

	public void startTimeSync() {
		TimeSyncThread runnable = new TimeSyncThread();
		// new Thread(runnable).start();
		// ProjectNameApplication.prepareExecutorService();
		// ((ProjectNameApplication) mDateTimeCitySettingsActivity
		// .getApplication()).execute(runnable);
		SettingsApplication.execute(runnable);
	}

	public DateTimeCitySettingsLogic(
			DateTimeCitySettingsActivity dateTimeCitySettingsActivity) {
		this.mActivity = dateTimeCitySettingsActivity;
		// init the mNtpServer:
		getFullAllProvinces();
		initmNtpServer();
	}

	/*
	 * public void SetTime(int hourofDay,int minute){
	 * 
	 * }
	 * 
	 * public void SetDate(int year,int month,int day){
	 * 
	 * }
	 */

	public void SetCity(String city) {

	}

	public void setis24Hour(boolean is24Hour) {
		this.mIs24Hour = is24Hour;
	}

	public boolean getis24Hour() {
		return android.text.format.DateFormat.is24HourFormat(mActivity);
	}

	public void setAutoTime(boolean isAuto) {

	}

	public void setTimeByInternet() {

	}

	public String getSystemTime() {
		SimpleDateFormat mFormat = new SimpleDateFormat(
				mActivity.getString(R.string.date_formate));
		String date = mFormat.format(new Date());
		if (!getis24Hour()) {
			int index = date.lastIndexOf(' ') + 1;
			String cut = date.substring(index, index + 2);
			if (Integer.parseInt(cut) >= 12) {
				date = date.replace("" + cut + ":",
						(Integer.parseInt(cut) - 12) + ":");
			}
		}
		return date;
	}

	synchronized public boolean autoRefreshTime(String mNtpServer,
			final int timeout) {
		if (mNtpServer == null) {
			mNtpServer = "1.cn.pool.ntp.org";
		}
		Log.d(TAG, ">>>autoRefreshTime>>>mNtpServer>>>>>>" + mNtpServer);
		System.out.println("NtpServer" + mNtpServer);
		System.out.println("autoRefreshTime");
		SntpClient client = new SntpClient();
		if (client.requestTime(mNtpServer, timeout)) {
			long mCachedNtpTime = client.getNtpTime();
			long mCachedNtpElapsedRealtime = client.getNtpTimeReference();
			long systemCurrentTime = System.currentTimeMillis();
			long ntpTime = mCachedNtpTime + SystemClock.elapsedRealtime()
					- mCachedNtpElapsedRealtime;
			if (Math.abs(ntpTime - systemCurrentTime) > 5 * 1000) {
				SystemClock.setCurrentTimeMillis(ntpTime);
			}
			mActivity.mHandler.sendEmptyMessage(0);
			return true;
		} else {

			URLConnection urlConnection = null;
			try {
				final URL url = new URL("http://" + mNtpServer);
				urlConnection = url.openConnection();
				urlConnection.setConnectTimeout(5000);
				urlConnection.connect();
			} catch (Exception e) {
				mActivity.mHandler.sendEmptyMessage(1);
				return false;
			}
			final long timestamp = urlConnection.getDate();
			final long systemCurrentTime = System.currentTimeMillis();
			if (Math.abs(timestamp - systemCurrentTime) > 5 * 1000) {
				// Make sure we don't overflow, since it's going to be converted
				// to an int
				if (timestamp / 1000 < Integer.MAX_VALUE) {
					SystemClock.setCurrentTimeMillis(timestamp);
					mActivity.mHandler.sendEmptyMessage(0);
					return true;
				}
			}
			mActivity.mHandler.sendEmptyMessage(0);
			return true;
		}
	}

	public static String getCurrentCity(Activity mActivity) {

		String city_name = SciflyStore.Global.getString(
				mActivity.getContentResolver(), Global.CITY_NAME);
		Log.d(TAG, "<<<<<<<<city_name<<<<<<<<<<<<<" + city_name);
		Log.e(TAG, ">>>>>>>>city_name.length = " + city_name.length());
		if (city_name.length() == 0) {
			Log.e(TAG,
					">>>>>>>>the scifly store have not save city name,return example city:taibei");
			return mActivity.getResources().getString(R.string.example_city);
		} else {
			String[] str = city_name.split(",");
			Log.e(TAG, ">>>>>>>>str.length = " + str.length);
			if (str.length - 1 > 0) {
				Log.e(TAG, ">>>>>the city is get from scifly store");
				if ("US".equals(getCurrentLanguage(mActivity))) {
					return str[1];
				} else if ("TW".equals(getCurrentLanguage(mActivity))) {
					return str[2];
				} else {
					return str[0];
				}
			} else {
				// city_name.length==1
				String location_city = "";
				Log.e(TAG, ">>>>>the city is get from location");
				String retStr = "";
				try {
					JSONObject json = new JSONObject(Global.getString(
							mActivity.getContentResolver(), Global.LOCATION));
					Log.d(TAG, ">>>>>>>>>get from location Json = " + json);
					location_city = json.getString("city");
					Log.e(TAG, "city_name from location is >>" + location_city);
					// identify the encode
					Log.d(TAG,
							"<<<<<<<<<mActivity.getString(R.string.city))<<<<<<<<<<<<"
									+ mActivity.getString(R.string.city));
					Log.d(TAG,
							"<<<<<<<<int<<<<<<<<<<<<"
									+ location_city.indexOf(mActivity
											.getString(R.string.city)));
					if (Utils.isChineseEncoding(location_city)) {
						location_city = location_city.substring(0,
								location_city.indexOf(mActivity
										.getString(R.string.city)));
					}
					String locationSpecies = "";
					for (int i = 0; i < mFullCitiesList.size(); i++) {
						String temp = mFullCitiesList.get(i);
						if (temp.contains(location_city)
								&& location_city != null) {
							locationSpecies = temp;
							break;
						}
					}

					if (locationSpecies != null && !locationSpecies.equals("")) {
						Log.i(TAG, "locationSpecies= " + locationSpecies);
						String[] strings = locationSpecies.split("\\(");
						strings[1] = strings[1].replace(")", "");
						strings[2] = strings[2].replace(")", "");
						if ("TW".equals(getCurrentLanguage(mActivity))) {
							retStr = strings[2];
						} else if ("US".equals(getCurrentLanguage(mActivity))) {
							retStr = strings[1];
						} else {
							retStr = strings[0];
						}
						StringBuffer strBuffer = new StringBuffer();
						strBuffer.append(strings[0] + ",");
						strBuffer.append(strings[1] + ",");
						strBuffer.append(strings[2]);
						locationSpecies = strBuffer.toString();
					}

					Log.d(TAG, ">>>>>>>>>save location_city to scify store = "
							+ location_city);
					SciflyStore.Global.putString(
							mActivity.getContentResolver(), Global.CITY_NAME,
							locationSpecies);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}catch (Exception e2){
				    e2.printStackTrace();
				}
				return retStr.equals("") ? location_city : retStr;
			}
		}
	}

	private void getFullAllProvinces() {
		HashMap<String, LinkedHashMap<String, String>> v2 = new HashMap<String, LinkedHashMap<String, String>>();
		InputStream inputStream = null;
		inputStream = mActivity.getResources().openRawResource(
				R.raw.weather_province_full);
		mFullProvinceList = new ArrayList<String>();
		mFullCitiesList = new ArrayList<String>();
		String str = null;
		String key = null;
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			while ((str = bufferedReader.readLine()) != null) {
				str = str.trim();
				if (str.indexOf("=") == -1
						&& (str.indexOf("[") == -1 && str.indexOf("]") == -1)) {
					continue;
				}
				if (str.indexOf("[") != -1 && str.indexOf("]") != -1) {
					key = str.replace("[", "").replace("]", "");
				}
				String[] strs = null;
				if (v2.containsKey(key)) {
					LinkedHashMap<String, String> mp = v2.get(key);
					strs = str.split("=");
					if (strs == null || strs.length != 2) {
						continue;
					}
					mp.put(strs[0].trim(), strs[1].trim());
					if (key.length() == 13) {
						mFullProvinceList.add(strs[1].trim());
					} else {
						mFullCitiesList.add(strs[1].trim());
					}
				} else {
					LinkedHashMap<String, String> mp = new LinkedHashMap<String, String>();
					v2.put(key, mp);
					strs = str.split("=");
					if (strs == null || strs.length != 2) {
						continue;
					}
					mp.put(strs[0].trim(), strs[1].trim());
				}
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getCurrentLanguage(Activity mActivity) {
		Configuration conf = mActivity.getResources().getConfiguration();
		String language = conf.locale.getCountry();
		return language;
	}

}
