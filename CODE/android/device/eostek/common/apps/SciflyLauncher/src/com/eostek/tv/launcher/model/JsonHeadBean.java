
package com.eostek.tv.launcher.model;

import java.io.Serializable;

/*
 * projectName： TVLauncher
 * moduleName： LogoBean.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-11-10 下午4:31:22
 * @Copyright © 2014 Eos Inc.
 */

public class JsonHeadBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * the http resonpse value,0 is ok,1 is request param error ,2 is servers
     * error
     **/
    private int response;

    /** the left margin size **/
    private int logoX;

    /** the top margin size **/
    private int logoY;

    /** true the launcher will show reflection,false not **/
    private boolean hasReflection;

    /** the logo url path **/
    private String logoUrl;

    /** the background url path **/
    private String backgroundUrl;

    /** the country flag,contains anguage and country,like zh-cn **/
    private String counLang;

    /**
     * the flag add to http request head param,server based on this variable to
     * determine whether the respnose update data
     **/
    private String eTag;

    public JsonHeadBean() {
    }

    /**
     * @return the logoX
     */
    public int getLogoX() {
        return logoX;
    }

    /**
     * @param X the logoX to set
     */
    public void setLogoX(int X) {
        this.logoX = X;
    }

    /**
     * @return the logoY
     */
    public int getLogoY() {
        return logoY;
    }

    /**
     * @param y the logoY to set
     */
    public void setLogoY(int y) {
        this.logoY = y;
    }

    /**
     * @return the hasReflection
     */
    public boolean isHasReflection() {
        return hasReflection;
    }

    /**
     * @param hasReflec the hasReflection to set
     */
    public void setHasReflection(boolean hasReflec) {
        this.hasReflection = hasReflec;
    }

    /**
     * @return the logoUrl
     */
    public String getLogoUrl() {
        return logoUrl;
    }

    /**
     * @param logo the logoUrl to set
     */
    public void setLogoUrl(String logo) {
        this.logoUrl = logo;
    }

    /**
     * @return the backgroundUrl
     */
    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    /**
     * @param background the backgroundUrl to set
     */
    public void setBackgroundUrl(String background) {
        this.backgroundUrl = background;
    }

    /**
     * @return the response
     */
    public int getResponse() {
        return response;
    }

    /**
     * @param responseValue the response to set
     */
    public void setResponse(int responseValue) {
        this.response = responseValue;
    }

    public String getCounLang() {
        return counLang;
    }

    public void setCounLang(String counLang) {
        this.counLang = counLang;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    @Override
    public String toString() {
        return "response=" + response + ";logoUrl = " + logoUrl + ";backgroundUrl = " + backgroundUrl + ";x = " + logoX
                + ";y = " + logoY + ";etag = " + eTag + ";counLang = " + counLang;
    }

}
