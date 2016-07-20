
package com.eostek.tv.launcher.business.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;
import org.xmlpull.v1.XmlPullParserException;

import com.eostek.tv.launcher.HomeApplication;
import com.eostek.tv.launcher.model.MetroInfo;
import com.eostek.tv.launcher.model.MetroPage;
import com.eostek.tv.launcher.util.UIUtil;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

/**
 * projectName： TVLauncher
 * moduleName： DataFromXML.java
 *
 * @author cloud.li
 * @version 1.0.0
 * @time  2014-7-18 上午10:57:42
 * @Copyright © 2014 Eos Inc.
 */

/**
 * Example: DataFromXML data data = new DataFromXML(); 
 * XmlPullParser parser = mContext.getResources().getXml(R.xml.page); 
 * data.parse(parser);
 * 
 * xml:
 * <MetroPageList>
 * <MetroPage title="影视库" appCategory="0">
 *   <MetroInfo id="0" title="最近观看" appCategory="0" positionX="0" positionY="0" widthSize="300" heightSize="300">
 *     <typeTitle>影视库</typeTitle>
 *     <clsName>com.eostek.scifly.video.history.VideoListViewHistory</clsName>
 *     <pkgName>com.eostek.scifly.video</pkgName>
 *     <itemType>0</itemType>
 *     <iconPath>video_icon_history.png</iconPath>
 *     <iconPathF/>
 *     <apkUrl/>
 *     <videoFlg>0</videoFlg>
 *     <StrFlg/>
 *   </MetroInfo>
 *  </MetroPage>
 * </MetroPageList>
 */
public class DataFromXML {

    public static final String PAGE_TAG_LIST = "MetroPageList";

    public static final String PAGE_TAG_NAME = "MetroPage";

    public static final String PAGE_TAG_TITLE = "title";

    public static final String PAGE_TAG_APPCATEGORY = "appCategory";

    public static final String METROINFO_TAG_NAME = "MetroInfo";

    public static final String METROINFO_TAG_ID = "id";

    public static final String METROINFO_TAG_TYPE_TITLE = "typeTitle";

    public static final String METROINFO_TAG_APP_TITLE = "title";

    public static final String METROINFO_TAG_CLASS_NAME = "clsName";

    public static final String METROINFO_TAG_PACKAGE_NAME = "pkgName";

    public static final String METROINFO_TAG_POSITION_X = "positionX";

    public static final String METROINFO_TAG_POSITION_Y = "positionY";

    public static final String METROINFO_TAG_WIDTH = "widthSize";

    public static final String METROINFO_TAG_HEIGHT = "heightSize";

    public static final String METROINFO_TAG_APP_TYPE = "itemType";

    public static final String METROINFO_TAG_APPCATEGORY = "appCategory";

    public static final String METROINFO_TAG_STR_FLAG = "StrFlg";

    public static final String METROINFO_TAG_INT_FLAG = "videoFlg";

    public static final String METROINFO_TAG_ICON_PATH_BACKGROUND = "iconPath";

    public static final String METROINFO_TAG_ICON_PATH_FOREGROUD = "IconPathF";

    public static final String METROINFO_TAG_ICON_APP_URL = "apkUrl";

    private List<MetroPage> pages; // All page configure

    /**
     * convert xml to list
     * 
     * @param parser
     */
    public void parse(XmlPullParser p) throws XmlPullParserException, IOException {
        XmlPullParser parser = p;
        // Xml.newPullParser();
        // parser.setInput(inputStream, "UTF-8");
        /*
         * XmlPullParser parser = Xml.newPullParser();
         * //由android.util.Xml创建一个XmlPullParser实例 parser.setInput(inputStream,
         * "UTF-8");
         */
        int eventType = parser.getEventType();
        MetroPage page = null;
        MetroInfo info = null;
        List<MetroInfo> infos = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    pages = new ArrayList<MetroPage>();
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals(PAGE_TAG_NAME)) {
                        // parser.getAttributeValue(null, PAGE_TAG_TITLE);
                        page = new MetroPage(parser.getAttributeValue(null, PAGE_TAG_TITLE), Integer.parseInt(parser
                                .getAttributeValue(null, PAGE_TAG_APPCATEGORY)));
                        infos = new ArrayList<MetroInfo>();
                    } else if (parser.getName().equals(METROINFO_TAG_NAME)) {
                        info = new MetroInfo(Integer.parseInt(parser.getAttributeValue(null, METROINFO_TAG_ID)),
                                Integer.parseInt(parser.getAttributeValue(null, METROINFO_TAG_POSITION_X)),
                                Integer.parseInt(parser.getAttributeValue(null, METROINFO_TAG_POSITION_Y)),
                                Integer.parseInt(parser.getAttributeValue(null, METROINFO_TAG_WIDTH)),
                                Integer.parseInt(parser.getAttributeValue(null, METROINFO_TAG_HEIGHT)));
                        info.setTitle(parser.getAttributeValue(null, METROINFO_TAG_APP_TITLE));
                        info.setAppCategory(Integer.parseInt(parser.getAttributeValue(null, METROINFO_TAG_APPCATEGORY)));
                    } else if (parser.getName().equals(METROINFO_TAG_TYPE_TITLE)) {
                        String text = parser.nextText();
                        info.setTypeTitle(text == null ? "" : text);
                    } else if (parser.getName().equals(METROINFO_TAG_CLASS_NAME)) {
                        String text = parser.nextText();
                        info.setClsName(text == null ? "" : text);
                    } else if (parser.getName().equals(METROINFO_TAG_PACKAGE_NAME)) {
                        String text = parser.nextText();
                        info.setPkgName(text == null ? "" : text);
                    } else if (parser.getName().equals(METROINFO_TAG_APP_TYPE)) {
                        String text = parser.nextText();
                        info.setItemType(Integer.parseInt(text == null ? "0" : text));
                    } else if (parser.getName().equals(METROINFO_TAG_ICON_PATH_BACKGROUND)) {
                        String text = parser.nextText();
                        info.setIconPathB(text == null ? "" : text);
                    } else if (parser.getName().equals(METROINFO_TAG_ICON_APP_URL)) {
                        String text = parser.nextText();
                        info.setApkUrl(text == null ? "" : text);
                    } else if (parser.getName().equals(METROINFO_TAG_INT_FLAG)) {
                        String text = parser.nextText();
                        info.setExtraIntInfo(Integer.parseInt(text == null ? "0" : text));
                    } else if (parser.getName().equals(METROINFO_TAG_STR_FLAG)) {
                        String text = parser.nextText();
                        info.setExtraStrInfo(text == null ? "" : text);
                    } else if (parser.getName().equals(METROINFO_TAG_ICON_PATH_FOREGROUD)) {
                        String text = parser.nextText();
                        info.setIconPathF(text == null ? "" : text);
                    }

                    break;
                case XmlPullParser.END_TAG:
                    Log.i("end", parser.getName());
                    if (parser.getName().equals(METROINFO_TAG_NAME)) {
                        info.setCounLang(UIUtil.getLanguage());
                        infos.add(info);
                        info = null;
                    } else if (parser.getName().equals(PAGE_TAG_NAME)) {
                        page.setCounLang(UIUtil.getLanguage());
                        page.setList(infos);
                        pages.add(page);
                        page = null;
                        infos = null;
                    }
                    break;
                default:
                    break;
            }
            if (eventType == XmlPullParser.START_TAG && parser.getEventType() == XmlPullParser.END_TAG) {
                eventType = parser.getEventType();
            } else {
                eventType = parser.next();
            }
        }
    }

    /**
     * convert list to xml(String)
     * 
     * @param mpages
     * @param arg0 (example:"UTF-8")
     * @throws IOException
     * @return The format string from the given pages
     */
    public String serialize(List<MetroPage> mpages, String arg0) throws IOException {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        serializer.setOutput(writer);
        serializer.startDocument(arg0, true);
        serializer.startTag(null, PAGE_TAG_LIST);
        for (MetroPage p : mpages) {
            serializer.startTag(null, PAGE_TAG_NAME);
            serializer.attribute(null, PAGE_TAG_TITLE, p.getTitle());
            serializer.attribute(null, PAGE_TAG_APPCATEGORY, "" + p.getAppCategory());

            for (MetroInfo i : p.getListInPage()) {
                serializer.startTag(null, METROINFO_TAG_NAME);
                serializer.attribute(null, METROINFO_TAG_ID, "" + i.getId());
                serializer.attribute(null, METROINFO_TAG_APP_TITLE, i.getTitle() == null ? "" : i.getTitle());
                serializer.attribute(null, METROINFO_TAG_APPCATEGORY, "" + i.getAppCategory());
                serializer.attribute(null, METROINFO_TAG_POSITION_X, "" + i.getX());
                serializer.attribute(null, METROINFO_TAG_POSITION_Y, "" + i.getY());
                serializer.attribute(null, METROINFO_TAG_WIDTH, "" + i.getWidthSize());
                serializer.attribute(null, METROINFO_TAG_HEIGHT, "" + i.getHeightSize());

                serializer.startTag(null, METROINFO_TAG_TYPE_TITLE);
                serializer.text(i.getTypeTitle() == null ? "" : i.getTypeTitle());
                serializer.endTag(null, METROINFO_TAG_TYPE_TITLE);

                serializer.startTag(null, METROINFO_TAG_CLASS_NAME);
                serializer.text(i.getClsName() == null ? "" : i.getClsName());
                serializer.endTag(null, METROINFO_TAG_CLASS_NAME);

                serializer.startTag(null, METROINFO_TAG_PACKAGE_NAME);
                serializer.text(i.getPkgName() == null ? "" : i.getPkgName());
                serializer.endTag(null, METROINFO_TAG_PACKAGE_NAME);

                serializer.startTag(null, METROINFO_TAG_APP_TYPE);
                serializer.text("" + i.getItemType());
                serializer.endTag(null, METROINFO_TAG_APP_TYPE);

                serializer.startTag(null, METROINFO_TAG_ICON_PATH_BACKGROUND);
                serializer.text(i.getIconPathB() == null ? "" : i.getIconPathB());
                serializer.endTag(null, METROINFO_TAG_ICON_PATH_BACKGROUND);

                serializer.startTag(null, METROINFO_TAG_ICON_APP_URL);
                serializer.text(i.getApkUrl() == null ? "" : i.getApkUrl());
                serializer.endTag(null, METROINFO_TAG_ICON_APP_URL);

                serializer.startTag(null, METROINFO_TAG_INT_FLAG);
                serializer.text("" + i.getExtraIntInfo());
                serializer.endTag(null, METROINFO_TAG_INT_FLAG);

                serializer.startTag(null, METROINFO_TAG_STR_FLAG);
                serializer.text(i.getExtraStrInfo());
                serializer.endTag(null, METROINFO_TAG_STR_FLAG);
                serializer.startTag(null, METROINFO_TAG_ICON_PATH_FOREGROUD);
                serializer.text(i.getIconPathF());
                serializer.endTag(null, METROINFO_TAG_ICON_PATH_FOREGROUD);

                serializer.endTag(null, METROINFO_TAG_NAME);
            }
            serializer.endTag(null, PAGE_TAG_NAME);
        }
        serializer.endTag(null, PAGE_TAG_LIST);
        serializer.endDocument();
        return writer.toString();
    }

    /**
     * write the string to the given file,
     * 
     * @param context
     * @param fileName The file name
     * @throws IOException
     */
    public void writeFile(Context context, String fileName) throws IOException {
        DBManager dbManager = DBManager.getDbManagerInstance(HomeApplication.getInstance());
        List<MetroPage> mMetroPages = dbManager.getMetroPages();
        String writestr = serialize(mMetroPages, "UTF-8");
        try {
            FileOutputStream fout = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] bytes = writestr.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get all page configure
     * 
     * @return The page list
     */
    public List<MetroPage> getPages() {
        return pages;
    }

}
