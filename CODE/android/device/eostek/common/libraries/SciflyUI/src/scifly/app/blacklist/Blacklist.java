
package scifly.app.blacklist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author frankzhang
 */
public class Blacklist implements Parcelable {

    /**
     * An app's package name.
     */
    public String pkg;

    /**
     * The probability of a process will be killed.
     */
    public int factor;

    /**
     * The reason of this pkg added in blacklist.
     */
    public String desc;

    /**
     * Default constructor.
     */
    public Blacklist() {
    }

    /**
     * @param pkg  package name
     * @param factor random factor
     * @param desc description
     */
    public Blacklist(String pkg, int factor, String desc) {
        this.pkg = pkg;
        this.factor = factor;
        this.desc = desc;
    }

    /**
     * Reset the fields.
     */
    public void clear() {
        this.pkg = null;
        this.factor = -1;
        this.desc = null;
    }

    @Override
    public String toString() {
        return String.format("Blacklist:{pkg=%s,\tfactor=%d,\tdesc=%s}\n", this.pkg, this.factor, this.desc);
    }

    // -------------------------------------------
    // Parcelable implements
    /**
     * @param parcel data coming from binder call
     */
    public Blacklist(Parcel parcel) {
        this.pkg = parcel.readString();
        this.desc = parcel.readString();
        this.factor = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int paramInt) {
        dest.writeString(this.pkg);
        dest.writeString(this.desc);
        dest.writeInt(this.factor);
    }

    /**
     * This only for binder call.
     */
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public static final Parcelable.Creator<Blacklist> CREATOR = new Parcelable.Creator() {
        public Blacklist[] newArray(int size) {
            return new Blacklist[size];
        }

        public Blacklist createFromParcel(Parcel source) {
            return new Blacklist(source);
        }
    };
    // -------------------------------------------------------------------------------------
}
