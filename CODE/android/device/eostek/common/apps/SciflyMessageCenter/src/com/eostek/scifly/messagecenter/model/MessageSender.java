
package com.eostek.scifly.messagecenter.model;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import scifly.provider.metadata.Msg;

/**
 * This object describes a message sender.
 * 
 * @author Youpeng.Wan
 * @since 2.0 2014-6-10
 */
public class MessageSender {

    private long mDbId;// id of the item from the DB.

    private String mSenderId;// sender_id,only for Senders.

    private String mName;// friendly name.e.g.:scifly.

    private String mDescription;// description e.g.:it's a system message.

    private int mSource;// where the messages from,e.g.:CPE or iSynergy.

    private String mImgURL;// url of sender's image.

    private String mReserve;// reserved field.

    private List<Msg> mMessages = new ArrayList<Msg>();

    private int unReadCount = 0;
    
    private boolean mIsblocked;

    /**
     * Constructor.
     */
    public MessageSender() {
    }

    /**
     * Constructor.
     * 
     * @param userId
     * @param friendlyName
     * @param description
     * @param source
     * @param imgURL
     */
    public MessageSender(String userId, String friendlyName, String description, int source, String imgURL) {
        this.mSenderId = userId;
        this.mName = friendlyName;
        this.mDescription = description;
        this.mSource = source;
        this.mImgURL = imgURL;
    }

    /**
     * get the id from the database.
     * 
     * @return
     */
    public long getdbId() {
        return mDbId;
    }

    /**
     * set the id of the database.
     * 
     * @param mId
     */
    public void setdbId(long mId) {
        this.mDbId = mId;
    }

    /**
     * get the sender id of the sender.
     * 
     * @return
     */
    public String getSenderId() {
        return mSenderId;
    }

    /**
     * set the senfer id.
     * 
     * @param senderId
     */
    public void setSenderId(String senderId) {
        this.mSenderId = senderId;
    }

    /**
     * get the name of the sender.
     * 
     * @return
     */
    public String getName() {
        return mName;
    }

    /**
     * set the namd for the sender.
     * 
     * @param name
     */
    public void setName(String name) {
        if (name == null || name.length() == 0) {
            this.mName = "unknown";
        }

        this.mName = name;
    }

    /**
     * get the description of the sender.
     * 
     * @return
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * set the description for the sender.
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.mDescription = description;
    }

    /**
     * get the message source of the sender.
     * 
     * @return
     */
    public int getSource() {
        return mSource;
    }

    /**
     * set the message source for message.
     * 
     * @param source
     */
    public void setSource(int source) {
        this.mSource = source;
    }

    /**
     * get the user image of the url.
     * 
     * @return
     */
    public String getImgURL() {
        return mImgURL;
    }

    /**
     * set the user image by the url.
     * 
     * @param imgURL
     */
    public void setImgURL(String imgURL) {
        this.mImgURL = imgURL;
    }

    /**
     * reserver info
     * 
     * @return
     */
    public String getReserve() {
        return mReserve;
    }

    /**
     * reserver info
     * 
     * @param reserve
     */
    public void setReserve(String reserve) {
        this.mReserve = reserve;
    }

    /**
     * get the list of messages.
     * 
     * @return
     */
    public List<Msg> getMessages() {
        return mMessages;
    }

    /**
     * set the list of messages.
     * 
     * @param messages
     */
    public void setMessages(List<Msg> messages) {
        this.mMessages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof MessageSender)) {
            return false;
        }
        MessageSender msgSender = (MessageSender) o;

        if (TextUtils.isEmpty(msgSender.mSenderId) || TextUtils.isEmpty(mSenderId)) {
            return false;
        }

        if (msgSender.mSenderId.equals(mSenderId)) {
            return true;
        }
        return false;
    }

    /**
     * count the unread messages.
     * 
     * @return
     */
    public int getUnReadCount() {
        return unReadCount;
    }

    /**
     * set the unread messages.
     * 
     * @param unReadCount
     */
    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    /**
     * add unread message.
     */
    public void unReadMsgPlus() {
        this.unReadCount++;
    }

    /**
     * print info.
     */
    public String toString() {
        return "sender:[userId:" + mSenderId + "][nickName:" + mName + "][mSenderId:" + mSenderId + "][unRead:"
                + unReadCount + "]";
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public boolean isblocked() {
        return mIsblocked;
    }

    public void setBlocked(boolean mIsblocked) {
        this.mIsblocked = mIsblocked;
    }

}
