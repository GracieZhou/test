
package com.eostek.isynergy.setmeup.model;

public class TitleModel {
    private int mResourceId;

    private String mLocal;

    private String mDisplayName;

    public TitleModel(int id, String title) {
        this.mResourceId = id;
        this.mDisplayName = title;
    }

    public int getRerourceId() {
        return mResourceId;
    }

    public void setRerourceId(int rid) {
        this.mResourceId = rid;
    }

    public String getTitle() {
        return mDisplayName;
    }

    public void setTitle(String text) {
        this.mDisplayName = text;
    }

    public String getLocal() {
        return mLocal;
    }

    public void setLocal(String local) {
        this.mLocal = local;
    }
}
