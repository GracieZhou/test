
package scifly.provider.metadata;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * This class is an Item of a PkgUsage.
 * 
 * @author Psso.Song
 */
/** @hide **/
public class PkgUsage implements Parcelable {

    private static final String TAG = "PkgUsage";

    public long mId;

    public String mPkgname;

    public long mTime;

    public PkgUsage() {
        this.mId = 0;
        this.mPkgname = "";
        this.mTime = 0;
    }

    public PkgUsage(Parcel parcel) {
        Log.d(TAG, "parcel : " + parcel.toString());
        this.mId = parcel.readLong();
        this.mPkgname = parcel.readString();
        this.mTime = parcel.readLong();
    }

    public PkgUsage(String pkgname, long time) {
        this.mId = 0;
        this.mPkgname = pkgname;
        this.mTime = time;
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
        dest.writeLong(mTime);
    }

    @Override
    public String toString() {
        if (true) {
            Log.d(TAG, "mId : " + mId + " mPkgname : " + mPkgname + " mTime : " + mTime);
        }
        return super.toString();
    }

    public static final Parcelable.Creator<PkgUsage> CREATOR = new Creator<PkgUsage>() {

        @Override
        public PkgUsage[] newArray(int size) {
            return new PkgUsage[size];
        }

        @Override
        public PkgUsage createFromParcel(Parcel source) {
            return new PkgUsage(source);
        }
    };

}
