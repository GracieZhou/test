
package com.heran.launcher2.news;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class NewsCategoryXMLHandler extends DefaultHandler {
    private final static String TAG = "NewsCategoryXMLHandler";

    private ArrayList<NewsCategory> list = null;// 解析後的XML內容

    private NewsCategory nc = null; // 存放當前需要紀錄的節點的XML內容

    private String currentValue = null;// 當前節點的XML文本值

    private Boolean inCategoryID = false;

    private Boolean inName = false;

    private Boolean inSortID = false;

    public NewsCategoryXMLHandler() {
        // 設置需要解析的節點名稱
    }

    @Override
    public void startDocument() throws SAXException {
        // 接收文檔開始的通知
        // 實例化ArrayList用于存放解析XML後的數據
        list = new ArrayList<NewsCategory>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // 接收元素開始的通知
        // 紀錄當前節點的名稱
        if ("NCCategory".equals(localName)) {
            nc = new NewsCategory();
        } else if ("CategoryID".equals(localName)) {
            inCategoryID = true;
        } else if ("Name".equals(localName)) {
            inName = true;
        } else if ("SortID".equals(localName)) {
            inSortID = true;
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // 接收元素中字符數據的通知
        // 當前節點有值的情况下才繼續執行
        if (inCategoryID) {
            currentValue = new String(ch, start, length);
            if (currentValue != null && !currentValue.equals("") && !currentValue.equals("\n")) {

                nc.categoryID = currentValue;
            }
        } else if (inName) {
            currentValue = new String(ch, start, length);
            if (currentValue != null && !currentValue.equals("") && !currentValue.equals("\n")) {
                nc.name = currentValue;
            }
        } else if (inSortID) {
            currentValue = new String(ch, start, length);
            if (currentValue != null && !currentValue.equals("") && !currentValue.equals("\n")) {
                nc.sortID = currentValue;
            }
        }
        currentValue = null;

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // 接收元素结束的通知。

        if (inCategoryID) {
            inCategoryID = false;
        } else if (inName) {
            inName = false;
        } else if (inSortID) {
            list.add(nc);
            inSortID = false;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        nc = null;
    }

    public ArrayList<NewsCategory> getList() {
        return list;
    }

}
