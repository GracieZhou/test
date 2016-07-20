
package com.eostek.wasuwidgethost.model;

/**
 * projectName：WasuWidgetHost.
 * moduleName： PgmInfo.java
 *
 */
public class PgmInfo {

    // Program Id
    private int mId;

    // Program Name
    private String cTitle;

    // Program Type
    private String cType;

    // Column identification (thematic categories of assets need this logo)
    private String mNodeId;

    // Pictures access address
    private String mPic;

    // Access to the data required to address player
    private String mLinkUrl;

    public final int getId() {
        return mId;
    }

    public final void setId(int id) {
        this.mId = id;
    }

    public final String getcTitle() {
        return cTitle;
    }

    public final void setcTitle(String ctitle) {
        this.cTitle = ctitle;
    }

    public final String getcType() {
        return cType;
    }

    public final void setcType(String ctype) {
        this.cType = ctype;
    }

    public final String getNodeId() {
        return mNodeId;
    }

    public final void setNodeId(String nodeId) {
        this.mNodeId = nodeId;
    }

    public final String getPic() {
        return mPic;
    }

    public final void setPic(String pic) {
        this.mPic = pic;
    }

    public final String getLinkUrl() {
        return mLinkUrl;
    }

    public final void setLinkUrl(String linkUrl) {
        this.mLinkUrl = linkUrl;
    }

}
