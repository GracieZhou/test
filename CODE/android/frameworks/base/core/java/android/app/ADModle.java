
package android.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * ad modle.
 *
 * @author shirley
 */
public class ADModle implements Parcelable {

    /** package name. */
    public String mPkgName;

    /** image download url. */
    public String mImageUrl;

    /** ad jump url. */
    public String mTargetUrl;

    /** ad show period of time. */
    public String mPeriod;

    /** ad show length of time. */
    public int mTime;

    /** ad id. */
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
     * @param pkgName  package name.
     * @param imageUrl  image url.
     * @param targetUrl  url for ads to jump.
     * @param period  when to show ads.
     * @param time  how long the ads show.
     * @param id  ads id.
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

    public String toString() {
        return mPkgName + ";" + mImageUrl + ";" + mTargetUrl + ";" + mPeriod + ";" + mTime + ";" + mAdId;
    };

    public boolean canShowAd(Context context, String pkgName) {
        boolean flag = false;
        if (pkgName.equals(this.mPkgName) && isTimeOk(this.mPeriod)) { // 匹配到广告
            flag = true;
        } else {
            if (!Instrumentation.isSystemApk(context, pkgName)) { // 不是系统应用，匹配正则
                Pattern pattern = Pattern.compile(this.mPkgName);
                Matcher matcher = pattern.matcher(pkgName);
                Log.d(TAG, "match : " + this.mPkgName + "," + pkgName + " : " + matcher.matches());
                if (matcher.matches() && isTimeOk(this.mPeriod)) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    private boolean isTimeOk(String period) {
        // "10：30-12：00,14：10-15：30"
        String[] peroids = period.split(",");
        for (String per : peroids) {
            if (isTimeAvaliable(per)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTimeAvaliable(String period) {
        // "10：30-12：00"
        Log.d(TAG, "period : " + period);

        boolean isOk = false;
        if (period != null) {
            try {
                String start = period.substring(0, period.indexOf("-"));
                String end = period.substring(period.indexOf("-") + 1, period.length());

                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);
                String now = hour + ":" + min;

                Log.d(TAG, "start : " + start + ",  end " + end + ",  now : " + now);

                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                Date dStart = format.parse(start);
                Date dEnd = format.parse(end);
                Date dNow = format.parse(now);

                long l1 = dNow.getTime() - dStart.getTime();
                long l2 = dEnd.getTime() - dNow.getTime();

                if (l1 >= 0 && l2 >= 0) {
                    isOk = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "time is ok : " + isOk);
        return isOk;
    }
}
