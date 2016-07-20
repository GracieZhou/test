
package com.android.settings.bugreport;

/**
 * @ClassName: ShorcutFeedbackBean.
 * @Description:ShorcutFeedbackBean.
 * @author: lucky.li
 * @date: 2015-8-26 am 11:05:59
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved
 */
public class ShorcutFeedbackBean {
    /**
     * error info
     * 
     * @see 0 no error
     * @see 1 query fail
     * @see 2 json parse error
     */
    private String err;

    /**
     * success or failure
     * 
     * @see success
     * @see failure
     */
    private String msg;

    /**
     * The server returns the total number of information
     */
    private int total;

    /**
     * Common problem content
     */
    private String[] bd;

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String[] getBd() {
        return bd;
    }

    public void setBd(String[] bd) {
        this.bd = bd;
    }

}
