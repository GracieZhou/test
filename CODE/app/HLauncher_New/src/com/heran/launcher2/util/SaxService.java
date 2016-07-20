
package com.heran.launcher2.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.heran.launcher2.news.NewsArticle;
import com.heran.launcher2.news.NewsArticleXMLHandler;
import com.heran.launcher2.news.NewsCategory;
import com.heran.launcher2.news.NewsCategoryXMLHandler;

import android.util.Log;

public class SaxService {
    private final static String TAG = "SaxService";

    public SaxService() {
        // TODO Auto-generated constructor stub
    }

    // 這個 InputStream 乃針對某個 webservice 回傳的資料流 , 在這 Function 中應該依照不同的資料流 來實例化不同的
    // MyHandler , 以取得不同的結果
    public static List<HashMap<String, String>> readWeatherXML(InputStream inputStream) {
        try {
            Log.d(TAG, "readXML mode 0");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // 實例化 SAX 解析器
            SAXParser sParser = factory.newSAXParser();
            // 實例化DefaultHandler,設置需要解析的節點 , 要把 type 傳入 Handler 來解析不同的格式
            MyHandler myHandler = new MyHandler();
            // 開始解析
            sParser.parse(inputStream, myHandler);
            inputStream.close();
            return myHandler.getList();
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(TAG, "readWeatherXML e = " + e.getMessage().toString());
        }
        Log.d(TAG, "readWeatherXML return null");
        return null;
    }

    public static ArrayList<NewsCategory> readNewsCategoryXML(InputStream inputStream) {
        try {
            Log.d(TAG, "readXML mode 1");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // 實例化 SAX 解析器
            SAXParser sParser = factory.newSAXParser();
            // 實例化DefaultHandler,設置需要解析的節點 , 要把 type 傳入 Handler 來解析不同的格式
            NewsCategoryXMLHandler myHandler = new NewsCategoryXMLHandler();
            // 開始解析
            sParser.parse(inputStream, myHandler);
            inputStream.close();
            return myHandler.getList();
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(TAG, "readNewsCategoryXML e = " + e.getMessage().toString());
        }
        Log.d(TAG, "readNewsCategoryXML return null");
        return null;
    }

    public static ArrayList<NewsArticle> readNewsArticleXML(InputStream inputStream) {
        try {
            Log.d(TAG, "readXML mode 2");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // 實例化 SAX 解析器
            SAXParser sParser = factory.newSAXParser();
            // 實例化DefaultHandler,設置需要解析的節點 , 要把 type 傳入 Handler 來解析不同的格式
            NewsArticleXMLHandler myHandler = new NewsArticleXMLHandler();
            // 開始解析
            sParser.parse(inputStream, myHandler);
            inputStream.close();
            return myHandler.getList();
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(TAG, "readNewsArticleXML e = " + e.getMessage().toString());
        }
        Log.d(TAG, "readNewsArticleXML return null");
        return null;
    }

}
