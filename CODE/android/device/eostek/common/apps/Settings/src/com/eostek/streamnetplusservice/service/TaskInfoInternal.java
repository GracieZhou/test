
package com.eostek.streamnetplusservice.service;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskInfoInternal implements Parcelable {

    public TaskInfoInternal(int progress, int speed, long size, int state, String url, String detail, String sessionID,
            String dlKey) {
        super();
        progress_ = progress;
        speed_ = speed;
        size_ = size;
        state_ = state;
        play_url_ = url;
        detail_ = detail;
        session_id_ = sessionID;
        dl_key_ = dlKey;
    }

    public TaskInfoInternal() {
    }

    public void setProgress(int progress) {
        progress_ = progress;
    }

    public void setSpeed(int speed) {
        speed_ = speed;
    }

    public void setSize(long size) {
        size_ = size;
    }

    public void setTaskState(int state) {
        state_ = state;
    }

    public void setPlayURL(String url) {
        play_url_ = url;
    }

    public void setDetail(String detail) {
        detail_ = detail;
    }

    public void setSessionID(String id) {
        session_id_ = id;
    }

    public void setDLKey(String key) {
        dl_key_ = key;
    }

    public int getProgress() {
        return progress_;
    }

    public int getSpeed() {
        return speed_;
    }

    public long getSize() {
        return size_;
    }

    public int getTaskState() {
        return state_;
    }

    public String getPlayURL() {
        return play_url_;
    }

    public String getDetail() {
        return detail_;
    }

    public String getSessionID() {
        return session_id_;
    }

    public String getDLKey() {
        return dl_key_;
    }

    private int progress_; // in percent

    private int speed_; // download speed(Unit: Byte/s)

    private long size_; // media file total size

    private int state_;

    private String play_url_; // original http URL

    private String detail_; // resolution and so on

    private String session_id_; // task session id

    private String dl_key_; // dl key

    @Override
    public String toString() {
        return "Task [progress=" + progress_ + ", speed=" + speed_ + ", size=" + size_ + ", state=" + state_
                + ", play url=" + play_url_ + ", session id=" + session_id_ + ", detail=" + detail_ + ", dl key="
                + dl_key_ + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(progress_);
        dest.writeInt(speed_);
        dest.writeLong(size_);
        dest.writeInt(state_);
        dest.writeString(play_url_);
        dest.writeString(detail_);
        dest.writeString(session_id_);
        dest.writeString(dl_key_);
    }

    private TaskInfoInternal(Parcel source) {
        this.progress_ = source.readInt();
        this.speed_ = source.readInt();
        this.size_ = source.readLong();
        this.state_ = source.readInt();
        this.play_url_ = source.readString();
        this.detail_ = source.readString();
        this.session_id_ = source.readString();
        this.dl_key_ = source.readString();
    }

    public static final Parcelable.Creator<TaskInfoInternal> CREATOR = new Creator<TaskInfoInternal>() {

        @Override
        public TaskInfoInternal createFromParcel(Parcel source) {
            return new TaskInfoInternal(source);
        }

        @Override
        public TaskInfoInternal[] newArray(int size) {
            return new TaskInfoInternal[size];
        }
    };
}
