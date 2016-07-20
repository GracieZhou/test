
package com.android.settings.datetimecity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.android.settings.R;
import com.android.settings.util.Utils;
import com.android.settings.widget.TitleWidget;

/**
 * DateTimeCitySettingsActivity
 */
public class DateTimeCitySettingsActivity extends Activity {

    private static final String TAG = "DateTimeCitySettings";

    public DateTimeCitySettingsLogic mLogic;

    private static final int EVENT_UPDATE_STATS = 500;

    private DateTimeCitySettingsFragment mDateTimeCitySettingsFragment;

    private NtpServerFragment mNtpServerFragment;

    private FragmentManager mFragmentManager;

    private TitleWidget mTitleWidget;

    public static final String NTP_CHINA = "China";

    public static final String NTP_TAIWAN = "Taiwan";

    public static final String NTP_USA = "USA";

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Utils.showToast(getApplicationContext(), R.string.correct_time_success);
                    break;
                case 1:
                    Utils.showToast(getApplicationContext(), R.string.correct_time_fail);
                    break;
                case EVENT_UPDATE_STATS:
                    Log.d(TAG, "<<<<<<<case EVENT_UPDATE_STATS<<<<<<<<<<<<");
                    mDateTimeCitySettingsFragment.refreshTime();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_time_main);
        mTitleWidget = (TitleWidget) findViewById(R.id.datetime_city_title);
        mTitleWidget.setSubTitleText(getString(R.string.area_time));
        mLogic = new DateTimeCitySettingsLogic(this);
        mFragmentManager = getFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_content, new DateTimeCitySettingsFragment(), "DateTimeCitySettingFragment")
                .commit();
    }

    @Override
    public void onResume() {
        mDateTimeCitySettingsFragment = (DateTimeCitySettingsFragment) getFragmentManager().findFragmentByTag(
                "DateTimeCitySettingFragment");
        Log.d(TAG, "<<<<<<<DateTimeCitySettingsFragment<<<<<<" + mDateTimeCitySettingsFragment);
        mNtpServerFragment = (NtpServerFragment) getFragmentManager().findFragmentByTag("NtpServerFragment");
        Log.d(TAG, "<<<<<<<mNtpServerFragment<<<<<<" + mNtpServerFragment);
        mTitleWidget.setSubTitleText(getString(R.string.area_time));
        super.onResume();
        mDateTimeCitySettingsFragment.initViews();
        mDateTimeCitySettingsFragment.refreshTime();
        sendTimeChangeBroadcast();
    }

    /**
     * if the format of time change,send broadcast to launcher.
     */
    public void sendTimeChangeBroadcast() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_TIME_CHANGED);
        sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        sendTimeChangeBroadcast();
        super.onPause();
    }

    @Override
    protected void onStop() {
        sendTimeChangeBroadcast();
        super.onStop();
    }

    /**
     * replace fragment.
     * 
     * @param cls
     * @param tag
     */
    public void replaceFragment(Fragment cls, String tag) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit,
                R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit);
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_content, cls, tag).commit();
    }

    /**
     * set titile in fragement.
     * 
     * @param title
     */
    public void setTitle(String title) {
        mTitleWidget.setSubTitleText(getString(R.string.area_time), title);
    }

    public void setTitle(int resId) {
        setTitle(getString(resId));
    }
}
