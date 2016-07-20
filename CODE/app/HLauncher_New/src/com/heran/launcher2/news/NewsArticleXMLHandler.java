
package com.heran.launcher2.news;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class NewsArticleXMLHandler extends DefaultHandler {
    private final static String TAG = "NewsArticleXMLHandler";

    private ArrayList<NewsArticle> list = null; // 解析後的XML內容

    private NewsArticle na = null;

    private String currentValue = null;// 當前節點的XML文本值

    private StringBuilder sb = new StringBuilder(); // 防止內容被覆蓋,而只擷取最後一段

    private Boolean inArticleDate = false;

    private Boolean inArticleTime = false;

    private Boolean inTitle = false;

    private Boolean inContentUrl = false;

    private Boolean inImagePath = false;

    private Boolean inContent = false;

    public NewsArticleXMLHandler() {
        // 設置需要解析的節點名稱
    }

    @Override
    public void startDocument() throws SAXException {
        // 接收文檔開始的通知
        // 實例化ArrayList用于存放解析XML後的數據
        list = new ArrayList<NewsArticle>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // 接收元素開始的通知
        // 紀錄當前節點的名稱

        sb.setLength(0);

        if ("ArticleItem".equals(localName)) {
            na = new NewsArticle();
        } else if ("ArticleDate".equals(localName)) {
            inArticleDate = true;
        } else if ("ArticleTime".equals(localName)) {
            inArticleTime = true;
        } else if ("Title".equals(localName)) {
            inTitle = true;
        } else if ("ContentUrl".equals(localName)) {
            inContentUrl = true;
        } else if ("ImagePath".equals(localName)) {
            inImagePath = true;
        } else if ("Content".equals(localName)) {
            inContent = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // 接收元素中字符數據的通知
        // 當前節點有值的情况下才繼續執行
        if (inArticleDate) {
            currentValue = new String(ch, start, length);
            if (currentValue != null && !currentValue.equals("") && !currentValue.equals("\n")) {
                // na.articleDate = currentValue;
                sb.append(ch, start, length);
            }
        } else if (inArticleTime) {
            currentValue = new String(ch, start, length);
            if (currentValue != null && !currentValue.equals("") && !currentValue.equals("\n")) {
                // na.articleTime = currentValue;
                sb.append(ch, start, length);
            }
        } else if (inTitle) {
            currentValue = new String(ch, start, length);
            if (currentValue != null && !currentValue.equals("") && !currentValue.equals("\n")) {
                // na.title = currentValue;
                sb.append(ch, start, length);
            }
        } else if (inContentUrl) {
            currentValue = new String(ch, start, length);
            if (currentValue != null && !currentValue.equals("") && !currentValue.equals("\n")) {
                // na.contentUrl = currentValue;
                sb.append(ch, start, length);
            }
        } else if (inImagePath) {
            currentValue = new String(ch, start, length);
            if (currentValue != null && !currentValue.equals("") && !currentValue.equals("\n")) {
                // na.imagePath = currentValue;
                sb.append(ch, start, length);
            }

        } else if (inContent) {
            currentValue = new String(ch, start, length);
            if (currentValue != null && !currentValue.equals("　") && !currentValue.equals("")) {

                // 等內容全部擷取完,再 endElement 存入內容
                sb.append(ch, start, length);
            }

        }
        currentValue = null;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // 接收元素结束的通知。
        if (inArticleDate) {
            na.articleDate = sb.toString();
            inArticleDate = false;
        } else if (inArticleTime) {
            na.articleTime = sb.toString();
            inArticleTime = false;
        } else if (inTitle) {
            na.title = sb.toString();
            inTitle = false;
        } else if (inContentUrl) {
            na.contentUrl = sb.toString();
            inContentUrl = false;
        } else if (inImagePath) {
            na.imagePath = sb.toString();
            list.add(na);
            inImagePath = false;
        } else if (inContent) {
            na.content = sb.toString();
            inContent = false;
        }

    }

    @Override
    public void endDocument() throws SAXException {
        na = null;
    }

    public ArrayList<NewsArticle> getList() {
        return list;
    }

}
