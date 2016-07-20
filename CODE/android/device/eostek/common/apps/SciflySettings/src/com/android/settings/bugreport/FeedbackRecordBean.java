
package com.android.settings.bugreport;

/**
 * @ClassName: FeedbackRecordBean.
 * @Description:Server feedback user record.
 * @author: lucky.li.
 * @date: Sep 15, 2015 9:32:05 AM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class FeedbackRecordBean {

    /**
     * error info
     * 
     * @see 0 no error
     * @see 1 query fail
     * @see 2 json parse fail
     */
    private String err;

    /**
     * The server returns success or failure information
     */
    private String msg;

    /**
     * Feedback record details
     */
    private BdBean bd;

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public BdBean getBd() {
        return bd;
    }

    public void setBd(BdBean bd) {
        this.bd = bd;
    }

}
