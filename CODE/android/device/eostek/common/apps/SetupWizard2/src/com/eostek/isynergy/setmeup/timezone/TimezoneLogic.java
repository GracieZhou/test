
package com.eostek.isynergy.setmeup.timezone;

import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.eostek.isynergy.setmeup.R;
import com.eostek.isynergy.setmeup.model.TimeZoneModel;

public class TimezoneLogic {
    private Context mContext;

    public String timezoneStr = "";

    private String fileName = "timezones.xml";

    private XmlPullParser timezoneParser;

    private TimeZoneFragment mTimeZoneFragment;

    public TimezoneLogic(TimeZoneFragment timeZoneFragment) {
        this.mTimeZoneFragment = timeZoneFragment;
        this.mContext = timeZoneFragment.getActivity();
    }

    public XmlResourceParser getXMLFromResXml(String fileName) {
        XmlResourceParser xmlParser = null;
        try {
            xmlParser = mContext.getResources().getXml(R.xml.timezones);
            return xmlParser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlParser;
    }

    /**
     * get timezone list
     * @return timezone list
     */
    public List<TimeZoneModel> getTimeZones() {

        timezoneParser = getXMLFromResXml(fileName);
        List<TimeZoneModel> timeZones = PullTimezoneParser.ParseXml(timezoneParser);
        return timeZones;
    }

}
