
package scifly.provider.metadata;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/** @hide **/
public class Footprint implements Parcelable {

    private static final String TAG = "Footprint";

    public long mId;

    public String mUser;

    public String mData;

    public String mTitle;

    public Bitmap mThumb;

    public long mTime;

    public int mCategory;

    public String mRemark;

    public String mReserve;

    public Footprint() {
        this.mId = 0;
        this.mUser = "";
        this.mData = "";
        this.mTitle = "";
        this.mTime = 0;
        this.mCategory = 0;
        this.mRemark = "";
        this.mReserve = "";
        this.mThumb = null;
    }

    public Footprint(Parcel parcel) {
        Log.d(TAG, "parcel : " + parcel.toString());
        this.mId = parcel.readLong();
        this.mUser = parcel.readString();
        this.mData = parcel.readString();
        this.mTitle = parcel.readString();
        this.mTime = parcel.readLong();
        this.mCategory = parcel.readInt();
        this.mRemark = parcel.readString();
        this.mReserve = parcel.readString();
        boolean hasThumb = parcel.readInt() == 1 ? true : false;
        if (hasThumb) {
            try {
                this.mThumb = Bitmap.CREATOR.createFromParcel(parcel);
            } catch (Exception e) {
                Log.w(TAG, "donot have thumb");
            }
        } else {
        }
    }

    public Footprint(String user, String data, String title, int category) {
        this.mId = 0;
        this.mUser = user;
        this.mData = data;
        this.mTitle = title;
        this.mTime = 0;
        this.mCategory = category;
        this.mRemark = "";
        this.mReserve = "";
        this.mThumb = null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d(TAG, "writeToParcel#parcel : " + dest.toString());
        dest.writeLong(mId);
        dest.writeString(mUser);
        dest.writeString(mData);
        dest.writeString(mTitle);
        dest.writeLong(mTime);
        dest.writeInt(mCategory);
        dest.writeString(mRemark);
        dest.writeString(mReserve);
        if (mThumb != null) {
            dest.writeInt(1);
            mThumb.writeToParcel(dest, 0);
            // dest.writeByteArray(b);
        } else {
            dest.writeInt(0);
        }
    }

    @Override
    public String toString() {
        if (true) {
            Log.d(TAG, "mId : " + mId + " mUser : " + mUser + " mData : " + mData + " mTitle : " + mTitle
                    + " mThumb : " + (mThumb == null ? "null" : mThumb) + " mTime : " + mTime + " mCategory: "
                    + mCategory + " mRemark : " + mRemark + " mReserve : " + mReserve);
        }
        return super.toString();
    }

    public static final Parcelable.Creator<Footprint> CREATOR = new Creator<Footprint>() {

        @Override
        public Footprint[] newArray(int size) {
            return new Footprint[size];
        }

        @Override
        public Footprint createFromParcel(Parcel source) {
            return new Footprint(source);
        }
    };

}
