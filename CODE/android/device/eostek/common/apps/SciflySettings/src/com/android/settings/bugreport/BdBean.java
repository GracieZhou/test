
package com.android.settings.bugreport;

import java.util.List;
/**
 * 
 * @ClassName:  BdBean. 
 * @Description:Contains multiple records feedback(Do what with a phrase to describe the file).   
 * @author: lucky.li.  
 * @date:   Sep 15, 2015 9:23:00 AM.   
 * @Copyright:  Eostek Co., Ltd. Copyright ,  All rights reserved.
 */
public class BdBean {

    private List<RecordDetailBean> its;

    public List<RecordDetailBean> getIts() {
        return its;
    }

    public void setIts(List<RecordDetailBean> its) {
        this.its = its;
    }
}
