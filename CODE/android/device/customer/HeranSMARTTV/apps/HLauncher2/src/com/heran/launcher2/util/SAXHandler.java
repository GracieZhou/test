
package com.heran.launcher2.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class SAXHandler extends DefaultHandler {
    private final static String TAG = "MyHandler";

    private List<HashMap<String, String>> list = null; // 解析後的XML內容

    private HashMap<String, String> map = null; // 存放當前需要紀錄的節點的XML內容

    private String currentTag = null;// 當前讀取的XML節點

    private String currentValue = null;// 當前節點的XML文本值

    private Boolean inLocationName = false;

    private Boolean inStartTime = false;

    public SAXHandler() {
        // 設置需要解析的節點名稱
    }

    @Override
    public void startDocument() throws SAXException {
        // 接收文檔開始的通知
        // 實例化ArrayList用于存放解析XML後的數據
        Log.d(TAG, "startDocument");

        list = new ArrayList<HashMap<String, String>>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // 接收元素開始的通知
        Log.d(TAG, "startElement");
        // 紀錄當前節點的名稱

        Log.d(TAG, "localName = " + localName);
        if ("locationName".equals(localName)) {
            map = new HashMap<String, String>();
            currentTag = localName;
            inLocationName = true;
        } else if ("startTime".equals(localName)) {
            map = new HashMap<String, String>();
            currentTag = localName;
            inStartTime = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // 接收元素中字符數據的通知
        // 當前節點有值的情况下才繼續執行
        Log.d(TAG, "characters");

        if (inLocationName) {
            currentValue = new String(ch, start, length);
            if (currentValue != null && !currentValue.equals("") && !currentValue.equals("\n")) {
                Log.d(TAG, "put node context into map");
                map.put(currentTag, currentValue);
            }
        } else if (inStartTime) {
            currentValue = new String(ch, start, length);
            if (currentValue != null && !currentValue.equals("") && !currentValue.equals("\n")) {
                Log.d(TAG, "put node context into map");
                map.put(currentTag, currentValue);
            }
        }
        currentTag = null;
        currentValue = null;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // 接收元素结束的通知。
        Log.d(TAG, "endElement");
        Log.d(TAG, "localName = " + localName);

        if (inLocationName) {
            Log.d(TAG, "add node into list");
            list.add(map);
            inLocationName = false;
            map = null;
        } else if (inStartTime) {
            Log.d(TAG, "add node into list");
            list.add(map);
            inStartTime = false;
            map = null;
        }
    }

    public List<HashMap<String, String>> getList() {
        return list;
    }

}
