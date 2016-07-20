
package com.eostek.scifly.browser.modle;

public class UrlModle {

    public int mId;

    public String mUrl;

    public String mTitle;

    public long mTime;

    public String mImgUrl;

    public UrlModle() {
        super();
    }

    public UrlModle(int id, String url, String title, long time) {
        super();
        this.mId = id;
        this.mUrl = url;
        this.mTitle = title;
        this.mTime = time;
    }

    public UrlModle(String url, String imgUrl) {
        this.mUrl = url;
        this.mImgUrl = imgUrl;
    }
    
    public UrlModle(String url, String title, String imgUrl) {
        this.mUrl = url;
        this.mTitle = title;
        this.mImgUrl = imgUrl;
    }
}
