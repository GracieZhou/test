
package scifly.provider.metadata;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/** @hide **/
public class Blacklist implements Parcelable {

    private static final String TAG = "Blacklist";

    public String mPackage;

    public String mMessage;

    public int mLevel;

    @Override
    public int describeContents() {
        return 0;
    }

    public Blacklist() {
        this.mPackage = "";
        this.mMessage = "";
        this.mLevel = -1;
    }

    public Blacklist(String pkg, String message, int level) {
        this.mPackage = pkg;
        this.mMessage = message;
        this.mLevel = level;
    }

    public Blacklist(Parcel parcel) {
        this.mPackage = parcel.readString();
        this.mMessage = parcel.readString();
        this.mLevel = parcel.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPackage);
        dest.writeString(mMessage);
        dest.writeInt(mLevel);
    }

    @Override
    public String toString() {
        if (true) {
            Log.d(TAG, "mPackage : " + mPackage + " mMessage : " + mMessage + " mLevel : " + mLevel);
        }
        return super.toString();
    }

    public static final Parcelable.Creator<Blacklist> CREATOR = new Creator<Blacklist>() {

        @Override
        public Blacklist[] newArray(int size) {
            return new Blacklist[size];
        }

        @Override
        public Blacklist createFromParcel(Parcel source) {
            return new Blacklist(source);
        }
    };
}
