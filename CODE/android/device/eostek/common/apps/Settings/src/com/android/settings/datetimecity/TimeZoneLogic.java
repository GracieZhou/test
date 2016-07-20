package com.android.settings.datetimecity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.xmlpull.v1.XmlPullParser;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.res.XmlResourceParser;
import com.android.settings.R;

public class TimeZoneLogic {
	private Activity mActivity;
	private XmlPullParser timezoneParser;
	private ArrayList<Timezone> timezoneArray;
	public String timezoneStr = "";
	 private static final String KEY_ID = "id";  
	private String fileName = "timezone.xml";
	public static String[] timezoneIdStr = new String[200];
	private static String[] timezoneNameStr = new String[200];
	private static String[] timezoneGmtStr = new String[200];

	public TimeZoneLogic(Activity activity) {
		super();
		this.mActivity = activity;
	}

	public void updateResource() {

		timezoneParser = getXMLFromResXml(fileName);
		timezoneArray = PullTimezoneParser.ParseXml(timezoneParser);
		for (Timezone pro : timezoneArray) {
			timezoneStr += pro.getId() + "*" + pro.getName() + "*"
					+ pro.getGmt() + "*";
		}
		String spStr[] = timezoneStr.split("\\*");
		int len = spStr.length;

		for (int i = 0; i < len; i++) {
			int j = i % 3;
			if (j == 0) {
				int k = i / 3;
				timezoneIdStr[k] = spStr[i];
			} else if (j == 1) {
				if (i == 1) {
					timezoneNameStr[0] = spStr[1];
				} else {
					int k = i / 3;
					timezoneNameStr[k] = spStr[i];
				}
			} else if (j == 2) {
				if (i == 2) {
					timezoneGmtStr[0] = spStr[2];
				} else {
					int k = i / 3;
					timezoneGmtStr[k] = spStr[i];
				}
			}

		}

	}

	public XmlResourceParser getXMLFromResXml(String fileName) {
		XmlResourceParser xmlParser = null;
		try {

			xmlParser = mActivity.getResources().getXml(R.xml.timezone);
			return xmlParser;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlParser;
	}

	public static List<Map<String, Object>> AdapterGetData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 83; i++) {

			Map<String, Object> map = new HashMap<String, Object>();
			String tzName = timezoneNameStr[i];
			String tzId = timezoneGmtStr[i];
			String tid = timezoneIdStr[i];
			map.put("timezone", tzName);
			map.put("id", tid);
			map.put("gmt", tzId);
			list.add(map);
		}
		return list;
	}

	public void setTimeZone(String tid) {
		AlarmManager mAlarmManager = (AlarmManager) mActivity
				.getSystemService(mActivity.ALARM_SERVICE);
		mAlarmManager.setTimeZone(tid);
	}
	
	public static int getCurrentTimeZone(TimeZoneAdapter adapter,TimeZone tz){
		final String defaultId = tz.getID();
        final int listSize = adapter.getCount();
        for (int i = 0; i < listSize; i++) {
            // Using HashMap<String, Object> induces unnecessary warning.
            final HashMap<?,?> map = (HashMap<?,?>)adapter.getItem(i);
            final String id = (String)map.get(KEY_ID);
            if (defaultId.equals(id)) {
                // If current timezone is in this list, move focus to it
                return i;
            }
        }
        return -1;
    }
		
}
