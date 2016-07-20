
package com.eostek.tv.advertisement;

public class AdInfo {

    private String title;

    private String update_date;

    private String update_time;

    private String pic_url;

    private String description;

    private int dismiss_time;

    private int programme_id;

    private String source;

    private String webview_url;

    private int pos_x;

    private int pos_y;

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public int getProgramme_id() {
        return programme_id;
    }

    public void setProgramme_id(int programme_id) {
        this.programme_id = programme_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDismiss_time() {
        return dismiss_time;
    }

    public void setDismiss_time(int dismiss_time) {
        this.dismiss_time = dismiss_time;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getWebview_url() {
        return webview_url;
    }

    public void setWebview_url(String webview_url) {
        this.webview_url = webview_url;
    }

    public int getPos_x() {
        return pos_x;
    }

    public void setPos_x(int pos_x) {
        this.pos_x = pos_x;
    }

    public int getPos_y() {
        return pos_y;
    }

    public void setPos_y(int pos_y) {
        this.pos_y = pos_y;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
