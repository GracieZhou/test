
package com.eostek.sciflyui.thememanager.util;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.eostek.sciflyui.thememanager.task.ThemeModel;

/**
 * DefaultHandler .
 */
public class XmlParser extends DefaultHandler {
    private List<ThemeModel> pgms;

    private ThemeModel curPgm;

    private String tagName = null;

    /**
     * @return getResults get Results
     */
    public final List<ThemeModel> getResults() {
        return pgms;
    }

    @Override
    public final void startDocument() throws SAXException {
        pgms = new ArrayList<ThemeModel>();
    }

    @Override
    public final void startElement(String uri, String localName, String name, Attributes attributes)
            throws SAXException {
        if (localName.equals("ScilfyUI-Theme")) {
            curPgm = new ThemeModel(ThemeModel.TYPE.LOCAL);
        }
        // if (localName.equals("pgm")) {
        // curPgm.setPgmId(attributes.getValue("id"));
        // }
        this.tagName = localName;
    }

    @Override
    public final void characters(char[] ch, int start, int length) throws SAXException {
        if (tagName != null) {
            String data = new String(ch, start, length);
            if (tagName.equals("title")) {
                curPgm.mTitle = data;
            } else if (tagName.equals("author")) {
                curPgm.mAuther = data;
            } else if (tagName.equals("version")) {
                curPgm.mThemeVersion = data;
            } else if (tagName.equals("spVersion")) {
                curPgm.mPlatformVersion = data;
            }
        }
    }

    @Override
    public final void endElement(String uri, String localName, String name) throws SAXException {
        if (localName.equals("ScilfyUI-Theme")) {
            pgms.add(curPgm);
            curPgm = null;
        }
        tagName = null;
    }
}
