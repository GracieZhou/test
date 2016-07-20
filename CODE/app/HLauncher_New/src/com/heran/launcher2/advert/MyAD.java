
package com.heran.launcher2.advert;

public class MyAD {
    
    private int id;

    private String gln; // 广告跳转地址

    private String ti; // 广告的标题

    private String siz; // 表示广告的尺寸大小，单位是像素，对应图片的像素

    private String upd; // 更新时间

    private String dsr; // 广告描述信息

    private int plt; // 广告播放切换模式

    private int dit; // 广告显示时间

    private String cpi; // CP接收信息

    private String pic; // 广告图片地址

    private String type; // 表示广告类型

    /**
     * @return 广告跳转地址
     */
    public String getGln() {
        return gln;
    }

    /**
     * @param gln 广告跳转地址
     */
    public void setGln(String gln) {
        this.gln = gln;
    }

    /**
     * @return 广告的标题
     */
    public String getTi() {
        return ti;
    }

    /**
     * @param ti 广告的标题
     */
    public void setTi(String ti) {
        this.ti = ti;
    }

    /**
     * @return 广告的尺寸大小，单位是像素，对应图片的像素
     */
    public String getSiz() {
        return siz;
    }

    /**
     * @param siz 广告的尺寸大小，单位是像素，对应图片的像素
     */
    public void setSiz(String siz) {
        this.siz = siz;
    }

    /**
     * @return 更新时间
     */
    public String getUpd() {
        return upd;
    }

    /**
     * @param upd 更新时间
     */
    public void setUpd(String upd) {
        this.upd = upd;
    }

    /**
     * @return 广告描述信息
     */
    public String getDsr() {
        return dsr;
    }

    /**
     * @param dsr 广告描述信息
     */
    public void setDsr(String dsr) {
        this.dsr = dsr;
    }

    /**
     * @return 广告播放切换模式
     */
    public int getPlt() {
        return plt;
    }

    /**
     * @param plt 广告播放切换模式
     */
    public void setPlt(int plt) {
        this.plt = plt;
    }

    /**
     * @return 广告显示时间
     */
    public int getDit() {
        return dit;
    }

    /**
     * @param dit 广告显示时间
     */
    public void setDit(int dit) {
        this.dit = dit;
    }

    /**
     * @return CP接收信息
     */
    public String getCpi() {
        return cpi;
    }

    /**
     * @param cpi CP接收信息
     */
    public void setCpi(String cpi) {
        this.cpi = cpi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return 广告图片地址
     */
    public String getPic() {
        return pic;
    }

    /**
     * @param pic 广告图片地址
     */
    public void setPic(String pic) {
        this.pic = pic;
    }

    /**
     * @return 表示广告类型
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 表示广告类型
     */
    public void setType(String type) {
        this.type = type;
    }

}
