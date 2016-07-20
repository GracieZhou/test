
package com.android.settings.datetimecity;

import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * PullTimezoneParser
 */
public class PullTimezoneParser {
    /**
     * get the time zone list in doc.
     * 
     * @param parser
     * @return
     */
    public static ArrayList<Timezone> ParseXml(XmlPullParser parser) {
        ArrayList<Timezone> timezoneArray = new ArrayList<Timezone>();
        Timezone timezoneTemp = null;

        try {
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        String tagName = parser.getName();
                        if (tagName.equals("timezone")) {

                            timezoneTemp = new Timezone();
                            timezoneTemp.setId(parser.getAttributeValue(0));
                            timezoneTemp.setName(parser.getAttributeValue(1));
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
