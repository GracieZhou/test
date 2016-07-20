
package com.eostek.tv.pvr.bean;

/*
 * projectName： Tv
 * moduleName： ListviewItemBean.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2015-7-28 下午5:32:05
 * @Copyright © 2014 Eos Inc.
 */

public class ListviewItemBean {

    /** LCN (logical channel number) **/
    private String mRvrLcn = null;

    /** service name (channel name) **/
    private String mPvrChannel = null;

    /** program name **/
    private String mPvrProgramService = null;

    public String getmRvrLcn() {
        return mRvrLcn;
    }

    public void setmRvrLcn(String mRvrLcn) {
        this.mRvrLcn = mRvrLcn;
    }

    public String getmPvrChannel() {
        return mPvrChannel;
    }

    public void setmPvrChannel(String mPvrChannel) {
        this.mPvrChannel = mPvrChannel;
    }

    public String getmPvrProgramService() {
        return mPvrProgramService;
    }

    public void setmPvrProgramService(String mPvrProgramService) {
        this.mPvrProgramService = mPvrProgramService;
    }

}
