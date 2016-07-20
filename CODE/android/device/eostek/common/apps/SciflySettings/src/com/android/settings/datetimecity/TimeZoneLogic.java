
package com.android.settings.datetimecity;

import java.util.ArrayList;
import java.util.TimeZone;
import org.xmlpull.v1.XmlPullParser;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;

import com.android.settings.R;
/**
 * Parse time zone file and getcurrent timezone
 *
 */
public class TimeZoneLogic {
    private Activity mActivity;

    private XmlPullParser timezoneParser;

    public static ArrayList<Timezone> timezoneArray;

    public String timezoneStr = "";

    public static String[] timezoneIdStr = new String[200];

    private static String[] timezoneNameStr = new String[200];

    private static String[] timezoneGmtStr = new String[200];

    public TimeZoneLogic(Activity activity) {
        super();
        this.mActivity = activity;
    }

    /**
     * Deposit　 time zone information in three array
     */
    public void getDetailArray() {
        timezoneParser = mActivity.getResources().getXml(R.xml.timezone);
        timezoneArray = PullTimezoneParser.ParseXml(timezoneParser);
        for (int i = 0; i < timezoneArray.size(); i++) {
            timezoneIdStr[i] = timezoneArray.get(i).getId();
            timezoneNameStr[i] = timezoneArray.get(i).getName();
            timezoneGmtStr[i] = timezoneArray.get(i).getGmt();
        }
    }

    /**
     * set TimeZone
     * 
     * @param tid
     */
    public void setTimeZone(String tid) {
        AlarmManager mAlarmManager = (AlarmManager) mActivity.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setTimeZone(tid);
    }

    /**
     * @param adapter
     * @param tz
     * @return current timezone
     */
    public static int getCurrentTimeZone(TimeZoneAdapter adapter, TimeZone tz) {
        final String defaultId = tz.getID();
        final int listSize = adapter.getCount();
        for (int i = 0; i < listSize; i++) {
            // Using HashMap<String, Object> induces unnecessary warning.
            final Timezone timezone = adapter.getItem(i);
            final String id = timezone.getId();
            if (defaultId.equals(id)) {
                // If current timezone is in this list, move focus to it
                return i;
            }
        }
        return -1;
    }

}
