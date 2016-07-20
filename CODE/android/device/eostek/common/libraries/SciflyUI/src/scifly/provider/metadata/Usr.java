
package scifly.provider.metadata;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/** @hide **/
public class Usr implements Parcelable {

    private static final String TAG = "UserInfo";

    public int mId;

    public String mName;

    public long mBonus;

    public long mCoin;

    public long mTime;

    public String mRemark;

    public String mReserve;

    public Usr() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeLong(mBonus);
        dest.writeLong(mCoin);
        dest.writeLong(mTime);
        dest.writeString(mRemark);
        dest.writeString(mReserve);
    }

    @Override
    public String toString() {
        if (true) {
            Log.d(TAG, "mName : " + mName);
        }
        return super.toString();
    }

    public Usr(Parcel parcel) {
        this.mId = parcel.readInt();
        this.mName = parcel.readString();
        this.mBonus = parcel.readLong();
        this.mCoin = parcel.readLong();
        this.mTime = parcel.readLong();
        this.mRemark = parcel.readString();
        this.mReserve = parcel.readString();
    }

    public static final Parcelable.Creator<Usr> CREATOR = new Creator<Usr>() {

        @Override
        public Usr[] newArray(int size) {
            return new Usr[size];
        }

        @Override
        public Usr createFromParcel(Parcel source) {
            return new Usr(source);
        }
    };
}
