package com.android.settings.datetimecity;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;


import android.util.Log;
import android.util.Xml;

public class PullTimezoneParser {

	public static ArrayList<Timezone> Parse(String provinceString){
        ArrayList<Timezone> timezoneArray = new ArrayList<Timezone>();

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

    public static ArrayList<Timezone> Parse(InputStream provinceIS){
        ArrayList<Timezone> timezoneArray = new ArrayList<Timezone>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            XmlPullParser parser = factory.newPullParser();            

            parser.setInput(provinceIS,"utf-8");

            timezoneArray = ParseXml(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return timezoneArray;
    }

    public static ArrayList<Timezone> ParseXml(XmlPullParser parser){
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
                        if(tagName.equals("timezone")){ 
                        	
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
        }catch (IOException e) {
            e.printStackTrace();
        }

        return timezoneArray;
    }

}
