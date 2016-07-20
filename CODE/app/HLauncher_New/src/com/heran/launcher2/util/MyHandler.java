package com.heran.launcher2.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;


public class MyHandler extends DefaultHandler {
    private final static String TAG = "MyHandler";
    private List<HashMap<String, String>> list = null; //解析後的XML內容
    private HashMap<String, String> map = null;  //存放當前需要紀錄的節點的XML內容
    private String currentTag = null;//當前讀取的XML節點
    private String currentValue = null;//當前節點的XML文本值
    private String nodeName = null;//需要解析的節點名稱
    private Boolean inLocationName = false;
    private Boolean inStartTime = false;

    public MyHandler() {
        // 設置需要解析的節點名稱
    }

    @Override
    public void startDocument() throws SAXException {
        // 接收文檔開始的通知
        // 實例化ArrayList用于存放解析XML後的數據
        Log.d(TAG,"startDocument");

        list = new ArrayList<HashMap<String, String>>();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        // 接收元素開始的通知  
        Log.d(TAG,"startElement");
/*
        Log.d(TAG,"uri = "+uri);        
        Log.d(TAG,"localName = "+localName);
        Log.d(TAG,"qName = "+qName);
*/        
/*        
        if (qName.equals(nodeName)) { // 如果找到 locationName 了
        //Attributes為當前節點的屬性值,如果存在屬性值,則屬性值也讀取

            map = new HashMap<String, String>();        
        }
*/      
        //紀錄當前節點的名稱        
        
        Log.d(TAG,"localName = "+localName);  
        if("locationName".equals(localName)){
            map = new HashMap<String, String>(); 
            currentTag = localName;
            inLocationName = true;
        }
        else if("startTime".equals(localName)){
            map = new HashMap<String, String>(); 
            currentTag = localName;
            inStartTime = true;
        }
        /*        
        if(localName.equals("locationName")){
            currentTag = localName;            
        }
*//*        
        else if(localName.equals("startTime")) {
            currentTag = localName;            
        }
        else if(localName.equals("endTime")) {
            currentTag = localName;
        }
        else if(localName.equals("parameterName")) {
            currentTag = localName;
        }
        else if(localName.equals("parameterValue")) {
            currentTag = localName;
        } */
/*        else {
            currentTag = "";
        }*/
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // 接收元素中字符數據的通知
        // 當前節點有值的情况下才繼續執行
        Log.d(TAG,"characters");        
        
//        map.put(currentTag, currentValue);
        
        if(inLocationName) {
            currentValue = new String(ch, start, length);
            if (currentValue != null && !currentValue.equals("")
                    && !currentValue.equals("\n")) {
                Log.d(TAG,"put node context into map");
                map.put(currentTag, currentValue);
            }            
        }
        else if(inStartTime) {
            currentValue = new String(ch, start, length);
            if (currentValue != null && !currentValue.equals("")
                    && !currentValue.equals("\n")) {
                Log.d(TAG,"put node context into map");
                map.put(currentTag, currentValue);
            }            
        }
        currentTag = null;
        currentValue = null;
        
/*        if(currentTag.equals("locationName")) {
          Log.d(TAG,"put locationName");
          currentValue = new String(ch, start, length); 
          map = new HashMap<String, String>();          
          map.put(currentTag, currentValue);
        }*/
        /*
        else if(currentTag.equals("startTime")) {
            Log.d(TAG,"put startTime");            
            currentValue = new String(ch, start, length);  
            map = new HashMap<String, String>();            
            map.put(currentTag, currentValue);
        }
        else if(currentTag.equals("endTime")) {
            Log.d(TAG,"put endTime");            
            currentValue = new String(ch, start, length);
            map = new HashMap<String, String>();            
            map.put(currentTag, currentValue);
        }
        else if(currentTag.equals("parameterName")) {
            Log.d(TAG,"put parameterName");            
            currentValue = new String(ch, start, length);
            map = new HashMap<String, String>();            
            map.put(currentTag, currentValue);
        }
        else if(currentTag.equals("parameterValue")) {
            Log.d(TAG,"put parameterValue");
            currentValue = new String(ch, start, length);            
            map = new HashMap<String, String>();            
            map.put(currentTag, currentValue);
        } */
/*        else {
            currentTag = "";
            currentValue = "";
        }*/
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // 接收元素结束的通知。
            Log.d(TAG,"endElement");        
            Log.d(TAG,"localName = "+localName);
            
            if (inLocationName) {
                Log.d(TAG,"add node into list");
                list.add(map);
                inLocationName = false;
                map = null;
            }     
            else if(inStartTime) {
                Log.d(TAG,"add node into list");
                list.add(map);
                inStartTime = false;
                map = null;
            }
/*            if(currentTag.equals("locationName")) {
                Log.d(TAG,"add locationName");                                
                list.add(map);
            }*/
/*            
            else if(currentTag.equals("startTime")) {
                Log.d(TAG,"add startTime");                
                list.add(map);
            }
            else if(currentTag.equals("endTime")) {
                Log.d(TAG,"add endTime");                
                list.add(map);
            }
            else if(currentTag.equals("parameterName")) {
                Log.d(TAG,"add parameterName");                
                list.add(map);
            }
            else if(currentTag.equals("parameterValue")) {
                Log.d(TAG,"add parameterValue");                
                list.add(map);
            } */
            //使用之後清空map,currentTag,currentValue 開始新一輪的讀取節點
/*            currentTag = null;
            currentValue = null;
            map = null;*/
    }

    public List<HashMap<String, String>> getList() {
        return list;
    }
    
    
}
