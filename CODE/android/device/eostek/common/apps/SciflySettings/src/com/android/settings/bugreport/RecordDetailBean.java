
package com.android.settings.bugreport;

import java.io.Serializable;

/**
 * @ClassName: RecordDetailBean.
 * @Description:Feedback record details.
 * @author: lucky.li.
 * @date: Sep 15, 2015 9:33:47 AM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class RecordDetailBean implements Serializable {
    /**
     * @Fields serialVersionUID : flag.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The content of the user feedback
     */
    private String submitContent;

    /**
     * Processing state
     * 
     * @see 0----- being dealt with
     * @see 1-----received
     * @see 2-----resolved
     * @see 3-----Thank you for your feedback/back
     * @see 4-----published
     */
    private int status;

    /**
     * Processing result
     */
    private String result;

    /**
     * User feedback time
     */
    private long submitTime;

    /**
     * Time to deal with problems
     */
    private long processTime;

    /**
     * The release version number to solve the problem
     */
    private String publishVersion;

    /**
     * The internal version to solve the problem
     */
    private String innerVersion;

    /**
     * mantisID
     */
    private int mantisID;

    public String getSubmitContent() {
        return submitContent;
    }

    public void setSubmitContent(String submitContent) {
        this.submitContent = submitContent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(int submitTime) {
        this.submitTime = submitTime;
    }

    public long getProcessTime() {
        return processTime;
    }

    public void setProcessTime(int processTime) {
        this.processTime = processTime;
    }

    public String getPublishVersion() {
        return publishVersion;
    }

    public void setPublishVersion(String publishVersion) {
        this.publishVersion = publishVersion;
    }

    public String getInnerVersion() {
        return innerVersion;
    }

    public void setInnerVersion(String innerVersion) {
        this.innerVersion = innerVersion;
    }

    public int getMantisID() {
        return mantisID;
    }

    public void setMantisID(int mantisID) {
        this.mantisID = mantisID;
    }

}
