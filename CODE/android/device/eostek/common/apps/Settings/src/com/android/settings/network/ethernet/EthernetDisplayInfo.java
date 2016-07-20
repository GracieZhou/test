
package com.android.settings.network.ethernet;

public class EthernetDisplayInfo {

    private String mIp;

    private String mNetmask;

    private String mGateway;

    private String mDNS1;

    private boolean isAutoIp = false;

    public String getIp() {
        return mIp;
    }

    public void setIp(String mIp) {
        this.mIp = mIp;
    }

    public String getNetmask() {
        return mNetmask;
    }

    public void setNetmask(String mNetmask) {
        this.mNetmask = mNetmask;
    }

    public String getGateway() {
        return mGateway;
    }

    public void setGateway(String mGateway) {
        this.mGateway = mGateway;
    }

    public String getDNS1() {
        return mDNS1;
    }

    public void setDNS1(String dNS1) {
        mDNS1 = dNS1;
    }

    public boolean isAutoIp() {
        return isAutoIp;
    }

    public void setAutoIp(boolean isAutoIp) {
        this.isAutoIp = isAutoIp;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EthernetDisplayInfo)) {
            return false;
        }
        EthernetDisplayInfo info = (EthernetDisplayInfo) o;

        if (info.isAutoIp == this.isAutoIp && info.mIp == this.mIp && info.mGateway == this.mGateway
                && info.mNetmask == this.mNetmask && info.mDNS1 == this.mDNS1) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("isAutoIp : " + isAutoIp);
        sb.append(" mIp : " + mIp);
        sb.append(" mGateway : " + mGateway);
        sb.append(" mNetmask : " + mNetmask);
        sb.append(" mDNS1 : " + mDNS1);

        return sb.toString();
    }
}
