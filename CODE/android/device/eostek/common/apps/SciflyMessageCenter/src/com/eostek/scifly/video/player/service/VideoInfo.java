
package com.eostek.scifly.video.player.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Java Bean for Video Info.
 */
public class VideoInfo implements Parcelable {
    /**
     * 节目URL
     */
    public String videoUrl;

    /**
     * URL类型（0:未破解;1:破解）
     */
    public int videoType;

    /**
     * 节目名称
     */
    public String videoName;

    /**
     * 节目来源
     */
    public String videoSource;

    /**
     * 节目组ID
     */
    public int pgrpId;

    /**
     * content id.
     */
    public String contentId;

    /**
     * 节目ID
     */
    public int programId;

    /**
     * program content id.
     */
    public String pgmContentId;

    /**
     * 节目索引
     */
    public int programIndex;

    /**
     * 节目分类
     */
    public String channelCode;

    /**
     * 播放时间
     */
    public int curPosition;

    /**
     * HD.
     */
    public int hd;

    /**
     * Constructor.
     */
    public VideoInfo() {
        videoUrl = "";
        videoType = 0;
        videoName = "";
        videoSource = "";
        pgrpId = -1;
        contentId = "";
        programId = -1;
        pgmContentId = "";
        programIndex = -1;
        channelCode = "";
        curPosition = 0;
        hd = 3;
    }

    /**
     * Constructor.
     * 
     * @param in
     */
    public VideoInfo(Parcel in) {
        videoUrl = in.readString();
        videoType = in.readInt();
        videoName = in.readString();
        videoSource = in.readString();
        pgrpId = in.readInt();
        contentId = in.readString();
        programId = in.readInt();
        pgmContentId = in.readString();
        programIndex = in.readInt();
        channelCode = in.readString();
        curPosition = in.readInt();
        hd = in.readInt();
    }

    /**
     * 
     */
    public static final Parcelable.Creator<VideoInfo> CREATOR = new Parcelable.Creator<VideoInfo>() {
        public VideoInfo createFromParcel(Parcel in) {
            return new VideoInfo(in);
        }

        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoUrl);
        dest.writeInt(videoType);
        dest.writeString(videoName);
        dest.writeString(videoSource);
        dest.writeInt(pgrpId);
        dest.writeString(contentId);
        dest.writeInt(programId);
        dest.writeString(pgmContentId);
        dest.writeInt(programIndex);
        dest.writeString(channelCode);
        dest.writeInt(curPosition);
        dest.writeInt(hd);
    }

    @Override
    public String toString() {
        return "toString()...videoUrl[" + videoUrl + "], videoType[" + videoType + "], videoName[" + videoName
                + "], videoSource [" + videoSource + "], pgrpId[" + pgrpId + "], programId[" + programId
                + "], programIndex[" + programIndex + "], channelCode[" + channelCode + "], curPosition ["
                + curPosition + "], HD [" + hd + "]";
    }
}
