
package com.eostek.isynergy.setmeup.config.network;

public class NetworkConfigure {
    private String ssid;

    private String securityMode;

    private String pw;

    private boolean isAdvanced;

    /**
     * IP 分配模式 STATIC ：静态分配 DHCP：动态分配
     */
    private String assignmentType;

    /**
     * IP address
     */
    private String Ip;

    /**
     * IP 前缀 比如 24
     */
    private int prefixIp;

    private String gateWay;

    public String getIp() {
        return Ip;
    }

    public void setIp(String ip) {
        Ip = ip;
    }

    public int getPrefixIp() {
        return prefixIp;
    }

    public void setPrefixIp(int prefixIp) {
        this.prefixIp = prefixIp;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getSecurityMode() {
        return securityMode;
    }

    public void setSecurityMode(String securityMode) {
        this.securityMode = securityMode;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public boolean isAdvanced() {
        return isAdvanced;
    }

    public void setAdvanced(boolean isAdvanced) {
        this.isAdvanced = isAdvanced;
    }

    public String getGateWay() {
        return gateWay;
    }

    public void setGateWay(String gateWay) {
        this.gateWay = gateWay;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ssid = ").append(ssid == null ? "null" : ssid).append("\r\n").append("securityMode = ")
                .append(securityMode == null ? "null" : securityMode).append("\r\n").append("pw = ")
                .append(pw == null ? "null" : pw).append("\r\n").append("isAdvanced = ").append(isAdvanced)
                .append("\r\n").append("assignmentType = ").append(assignmentType == null ? "null" : assignmentType)
                .append("\r\n").append("Ip = ").append(Ip == null ? "null" : Ip).append("\r\n").append("prefixIp = ")
                .append(prefixIp).append("\r\n").append("gateWay = ").append(gateWay == null ? "null" : gateWay)
                .append("\r\n");

        return buffer.toString();
    }
}
