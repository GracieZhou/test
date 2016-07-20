
package scifly.provider.metadata;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/** @hide **/
public class Msg implements Parcelable {

    private static final String TAG = "Msg";

    public long mId;

    public String mUserId;

    public String mUserInfo;

    public String mImgUrl;

    public String mTitle;

    public long mTime;

    public int mStatus;

    public String mData;

    public String mThumb;

    public String mExtra;

    public int mSource;

    public int mCategory;

    public int mBlocked;

    public String mReserve;

    public Msg() {
        this.mId = 0;
        this.mUserId = "";
        this.mUserInfo = "";
        this.mImgUrl = "";
        this.mTitle = "";
        this.mTime = 0;
        this.mStatus = 0;
        this.mData = "";
        this.mThumb = "";
        this.mExtra = "";
        this.mSource = 0;
        this.mCategory = 0;
        this.mBlocked = 0;
        this.mReserve = "";
    }

    public Msg(String userId, String title, int status, int source, int category) {
        this.mId = 0;
        this.mUserId = userId;
        this.mUserInfo = "";
        this.mImgUrl = "";
        this.mTitle = title;
        this.mTime = 0;
        this.mStatus = status;
        this.mData = "";
        this.mThumb = "";
        this.mExtra = "";
        this.mSource = source;
        this.mCategory = category;
        this.mBlocked = 0;
        this.mReserve = "";
    }

    public Msg(Parcel parcel) {
        Log.d(TAG, "parcel : " + parcel.toString());
        this.mId = parcel.readLong();
        this.mUserId = parcel.readString();
        this.mUserInfo = parcel.readString();
        this.mImgUrl = parcel.readString();
        this.mTitle = parcel.readString();
        this.mTime = parcel.readLong();
        this.mStatus = parcel.readInt();
        this.mData = parcel.readString();
        this.mThumb = parcel.readString();
        this.mExtra = parcel.readString();
        this.mSource = parcel.readInt();
        this.mCategory = parcel.readInt();
        this.mBlocked = parcel.readInt();
        this.mReserve = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d(TAG, "writeToParcel#parcel : " + dest.toString());
        dest.writeLong(mId);
        dest.writeString(mUserId);
        dest.writeString(mUserInfo);
        dest.writeString(mImgUrl);
        dest.writeString(mTitle);
        dest.writeLong(mTime);
        dest.writeInt(mStatus);
        dest.writeString(mData);
        dest.writeString(mThumb);
        dest.writeString(mExtra);
        dest.writeInt(mSource);
        dest.writeInt(mCategory);
        dest.writeInt(mBlocked);
        dest.writeString(mReserve);
    }

    @Override
    public String toString() {
        if (true) {
            Log.d(TAG, "mId : " + mId + " mUserId : " + mUserId + " mUserInfo : " + mUserInfo + " mImgUrl : " + mImgUrl
                    + " mTitle : " + mTitle + " mTime : " + mTime + " mStatus : " + mStatus + " mData : " + mData
                    + " mThumb : " + mThumb + " mExtra : " + mExtra + " mSource : " + mSource + " mCategory : "
                    + mCategory + " mBlocked : " + mBlocked + " mReserve : " + mReserve);
        }
        return super.toString();
    }

    public static final Parcelable.Creator<Msg> CREATOR = new Creator<Msg>() {

        @Override
        public Msg[] newArray(int size) {
            return new Msg[size];
        }

        @Override
        public Msg createFromParcel(Parcel source) {
            return new Msg(source);
        }
    };

}
