
package scifly.provider.metadata;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * This class is an Item of a Statistics record.
 * 
 * @author Psso.Song
 */
/** @hide **/
public class StatisticsRecord implements Parcelable {

    private static final String TAG = "StatisticsRecord";

    public static final int STATISTICS_TYPE_SESSION_IN = 0;

    public static final int STATISTICS_TYPE_SESSION_OUT = 1;

    public static final int STATISTICS_TYPE_PAGE_IN = 2;

    public static final int STATISTICS_TYPE_PAGE_OUT = 3;

    public static final int STATISTICS_TYPE_CUSTOM_EVENT = 4;

    public long mId;

    public String mPkgname;

    public String mActivityName;

    public int mType;

    public long mTime;

    public String mCategory;

    public String mKey;

    public String mValue;

    public String mParams;

    public StatisticsRecord() {
        this.mId = 0;
        this.mPkgname = "";
        this.mActivityName = "";
        this.mType = -1;
        this.mTime = 0;
        this.mCategory = "";
        this.mKey = "";
        this.mValue = "";
        this.mParams = "";
    }

    public StatisticsRecord(Parcel parcel) {
        Log.d(TAG, "parcel : " + parcel.toString());
        this.mId = parcel.readLong();
        this.mPkgname = parcel.readString();
        this.mActivityName = parcel.readString();
        this.mType = parcel.readInt();
        this.mTime = parcel.readLong();
        this.mCategory = parcel.readString();
        this.mKey = parcel.readString();
        this.mValue = parcel.readString();
        this.mParams = parcel.readString();
    }

    public StatisticsRecord(String pkgname, int type, long time) {
        this.mId = 0;
        this.mPkgname = pkgname;
        this.mActivityName = "";
        this.mType = type;
        this.mTime = time;
        this.mCategory = "";
        this.mKey = "";
        this.mValue = "";
        this.mParams = "";
    }

    public StatisticsRecord(String pkgname, String activityName, int type, long time, String category, String key,
            String value) {
        this.mId = 0;
        this.mPkgname = pkgname;
        this.mActivityName = activityName;
        this.mType = type;
        this.mTime = time;
        this.mCategory = category;
        this.mKey = key;
        this.mValue = value;
        this.mParams = "";
    }

    public StatisticsRecord(String pkgname, String activityName, int type, long time, String category, String params) {
        this.mId = 0;
        this.mPkgname = pkgname;
        this.mActivityName = activityName;
        this.mType = type;
        this.mTime = time;
        this.mCategory = category;
        this.mKey = "";
        this.mValue = "";
        this.mParams = params;
    }

    public StatisticsRecord(String pkgname, String activityName, int type, long time, String category, String key,
            String value, String params) {
        this.mId = 0;
        this.mPkgname = pkgname;
        this.mActivityName = activityName;
        this.mType = type;
        this.mTime = time;
        this.mCategory = category;
        this.mKey = key;
        this.mValue = value;
        this.mParams = params;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d(TAG, "writeToParcel#parcel : " + dest.toString());
        dest.writeLong(mId);
        dest.writeString(mPkgname);
        dest.writeString(mActivityName);
        dest.writeInt(mType);
        dest.writeLong(mTime);
        dest.writeString(mCategory);
        dest.writeString(mKey);
        dest.writeString(mValue);
        dest.writeString(mParams);
    }

    @Override
    public String toString() {
        if (true) {
            Log.d(TAG, "mId : " + mId + " mPkgname : " + mPkgname + " mActivityName : " + mActivityName + " mType : "
                    + mType + " mTime : " + mTime + " mCategory : " + mCategory + " mKey: " + mKey + " mValue : "
                    + mValue + " mParams : " + mParams);
        }
        return super.toString();
    }

    public static final Parcelable.Creator<StatisticsRecord> CREATOR = new Creator<StatisticsRecord>() {

        @Override
        public StatisticsRecord[] newArray(int size) {
            return new StatisticsRecord[size];
        }

        @Override
        public StatisticsRecord createFromParcel(Parcel source) {
            return new StatisticsRecord(source);
        }
    };

}
