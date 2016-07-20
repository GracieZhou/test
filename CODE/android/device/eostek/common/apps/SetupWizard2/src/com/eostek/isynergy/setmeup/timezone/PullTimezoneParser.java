
package com.eostek.isynergy.setmeup.timezone;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.eostek.isynergy.setmeup.model.TimeZoneModel;

public class PullTimezoneParser {

    public static ArrayList<TimeZoneModel> Parse(String provinceString) {
        ArrayList<TimeZoneModel> timezoneArray = new ArrayList<TimeZoneModel>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            XmlPullParser parser = factory.newPullParser();

            parser.setInput(new StringReader(provinceString));

            timezoneArray = ParseXml(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return timezoneArray;
    }

    public static ArrayList<TimeZoneModel> Parse(InputStream provinceIS) {
        ArrayList<TimeZoneModel> timezoneArray = new ArrayList<TimeZoneModel>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            XmlPullParser parser = factory.newPullParser();

            parser.setInput(provinceIS, "utf-8");

            timezoneArray = ParseXml(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return timezoneArray;
    }

    public static ArrayList<TimeZoneModel> ParseXml(XmlPullParser parser) {
        ArrayList<TimeZoneModel> timezoneArray = new ArrayList<TimeZoneModel>();
        TimeZoneModel timezoneTemp = null;

        try {
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        String tagName = parser.getName();
                        if (tagName.equals("timezone")) {

                            timezoneTemp = new TimeZoneModel();
                            timezoneTemp.setTimeZoneId(parser.getAttributeValue(0));
                            timezoneTemp.setTimeZoneName(parser.getAttributeValue(1));
                            timezoneTemp.setGmt(parser.getAttributeValue(2));
                            timezoneArray.add(timezoneTemp);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.END_DOCUMENT:

                        break;
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return timezoneArray;
    }

}
