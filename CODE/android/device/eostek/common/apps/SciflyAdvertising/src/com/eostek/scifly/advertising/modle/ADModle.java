
package com.eostek.scifly.advertising.modle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eostek.scifly.advertising.AdvertisingManager;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * ad modle.
 * 
 * @author shirley
 */
public class ADModle implements Parcelable {

    /** package name */
    public String mPkgName;

    /** image download url */
    public String mImageUrl;

    /** ad jump url */
    public String mTargetUrl;

    /** ad show period of time */
    public String mPeriod;

    /** ad show length of time */
    public int mTime;

    /** ad id */
    public String mAdId;

    private static final String TAG = "ADModle";

    /**
     * generate constructor with no fields.
     */
    public ADModle() {
        super();
    }

    /**
     * generate constructor with fields.
     * 
     * @param name String
     * @param url String
     * @param turl String
     * @param p String
     * @param t String
     */
    public ADModle(String pkgName, String imageUrl, String targetUrl, String period, int time, String id) {
        super();
        this.mPkgName = pkgName;
        this.mImageUrl = imageUrl;
        this.mTargetUrl = targetUrl;
        this.mPeriod = period;
        this.mTime = time;
        this.mAdId = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPkgName);
        dest.writeString(mImageUrl);
        dest.writeString(mTargetUrl);
        dest.writeString(mPeriod);
        dest.writeInt(mTime);
        dest.writeString(mAdId);
    }

    public static final Parcelable.Creator<ADModle> CREATOR = new Creator<ADModle>() {

        @Override
        public ADModle createFromParcel(Parcel source) {
            return new ADModle(source.readString(), source.readString(), source.readString(), source.readString(),
                    source.readInt(), source.readString());
        }

        @Override
        public ADModle[] newArray(int size) {
            return new ADModle[size];
        };
    };

    /**
     * override toString method.
     */
    public String toString() {
        return mPkgName + ";" + mImageUrl + ";" + mTargetUrl + ";" + mPeriod + ";" + mTime + ";" + mAdId;
    };

    /**
     * Match ads.
     * @param manager AdvertisingManager
     * @param pkgName String
     * @return boolean
     */
    public boolean canShowAd(AdvertisingManager manager, String pkgName) {
        boolean flag = false;
        if (pkgName.equals(this.mPkgName) && manager.isTimeOk(this.mPeriod)) { // 匹配到广告
            flag = true;
        } else {
            if (!manager.isSystemApk(this.mPkgName)) { // 不是系统应用，匹配正则
                Pattern pattern = Pattern.compile(this.mPkgName);
                Matcher matcher = pattern.matcher(pkgName);
                Log.d(TAG, "match : " + this.mPkgName + "," + pkgName + " : " + matcher.matches());
                if (matcher.matches() && manager.isTimeOk(this.mPeriod)) {
                    flag = true;
                }
            }
        }
        return flag;
    }
}
