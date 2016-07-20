
package com.eostek.tv.launcher.model;

import java.util.ArrayList;
import java.util.List;

/*
 * projectName： TVLauncher
 * moduleName： MetroPage.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-7-11 下午6:15:28
 * @Copyright © 2014 Eos Inc.
 */
/**
 * 保存每个页面的相关信息，包括一个Metro List和标题
 **/
public class MetroPage {

    private String mTitle;

    private int mAppCategory;

    private List<MetroInfo> mlist = new ArrayList<MetroInfo>();
    
    /** the country flag,contains language and country,like zh-cn **/
    private String counLang;

    public MetroPage() {

    }

    public MetroPage(String tle, int index) {
        this.mTitle = tle;
        this.mAppCategory = index;
    }

    /**
     * get child count in the page
     * 
     * @return The count of child
     */
    public int getChildCount() {
        if (mlist == null) {
            return 0;
        } else {
            return mlist.size();
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String tit) {
        this.mTitle = tit;
    }

    public int getAppCategory() {
        return mAppCategory;
    }

    public void setAppCategory(int category) {
        this.mAppCategory = category;
    }

    public List<MetroInfo> getListInPage() {
        return mlist;
    }

    public void setList(List<MetroInfo> list) {
        this.mlist = list;
    }

    /**
     * get the current page width
     * 
     * @return The page width
     */
    public int getPageWidth() {
        int maxWidth = 0;
        for (int i = 0; i < mlist.size(); i++) {
            MetroInfo info = mlist.get(i);
            int width = info.getX() + info.getWidthSize();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    public String getCounLang() {
        return counLang;
    }

    public void setCounLang(String counLang) {
        this.counLang = counLang;
    }

}
