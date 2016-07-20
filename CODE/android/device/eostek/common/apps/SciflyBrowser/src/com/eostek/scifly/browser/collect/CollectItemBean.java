
package com.eostek.scifly.browser.collect;


public class CollectItemBean {

    public static boolean isDeleteMode;

    public String imgUrl;

    public String md5str;

    public String httpurl;

    public CollectItemBean(String url, String md5, String httpurl) {
        this.imgUrl = url;
        this.md5str = md5;
        this.httpurl = httpurl;
    }

    public boolean isDeleteMode() {
        return isDeleteMode;
    }

    public void setDeleteMode(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getHttpurl() {
        return httpurl;
    }

    public void setHttpurl(String httpurl) {
        this.httpurl = httpurl;
    }

    public String getMd5str() {
        return md5str;
    }

    public void setMd5str(String md5str) {
        this.md5str = md5str;
    }
}
