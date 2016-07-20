
package com.android.settings.datetimecity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.R;
import com.android.settings.SettingPreference;
import com.android.settings.userbackup.BackUpData;
import com.android.settings.util.Utils;

/**
 * DateTimeCitySettingsFragment
 */
public class DateTimeCitySettingsFragment extends PreferenceFragment implements OnPreferenceClickListener,
        OnPreferenceChangeListener {

    private static final String TAG = "DateTimeCitySettings";

    private static final String COORRECT_TIME_KEY = "correcttime";

    private static final String IS_24_HOUR_KEY = "is24hour";

    private static final String TIMEZONE_KEY = "timezone";

    private static final String CITY_KEY = "city";

    private static final String HOURS_12 = "12";

    private static final String HOURS_24 = "24";

    private DateTimeCitySettingsActivity mActivity;

    private DateTimeCitySettingsLogic mLogic;

    private SettingPreference mCorrectTimePreference;

    private SettingPreference mIs24HourPreference;

    //private SettingPreference mTimeZonePreference;

    //private SettingPreference mCitySettingPreference;

    private static final int EVENT_UPDATE_STATS = 500;

    private static final int CORRECT_UPDATE_TIME = 60 * 1000;

    /**
     * get message and refrsh UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_datetimecity_setting);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_preference_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mActivity = (DateTimeCitySettingsActivity) this.getActivity();
        mActivity.setTitle("");
        super.onActivityCreated(savedInstanceState);
        mLogic = mActivity.mLogic;
        mCorrectTimePreference = (SettingPreference) findPreference(COORRECT_TIME_KEY);
        mIs24HourPreference = (SettingPreference) findPreference(IS_24_HOUR_KEY);
        //mTimeZonePreference = (SettingPreference) findPreference(TIMEZONE_KEY);
        //mCitySettingPreference = (SettingPreference) findPreference(CITY_KEY);
        initViews();
        mCorrectTimePreference.setOnPreferenceClickListener(this);
       // mTimeZonePreference.setOnPreferenceClickListener(this);
       // mCitySettingPreference.setOnPreferenceClickListener(this);
        mIs24HourPreference.setOnPreferenceChangeListener(this);
    }

    /**
     * assignment to the textview.
     */
    public void initViews() {
        mCorrectTimePreference.setRightText(mLogic.getSystemTime());
        final Calendar now = Calendar.getInstance();
        if(mLogic.getis24Hour()){
            mIs24HourPreference.setRightText(getActivity().getString(R.string.yes));
        }else{
            mIs24HourPreference.setRightText(getActivity().getString(R.string.no));
        }
		//remove city and time
        //mTimeZonePreference.setRightText(getTimeZoneText(now.getTimeZone()));
        //mCitySettingPreference.setRightText(CitySettingLogic.getCurrentCity(getActivity()));
        BackUpData.backupData("timezone", "time_zone", TimeZone.getDefault().getID());
    }

    /**
     * @return the boolean of whether current format is 24hour
     */
    private void is24HourFormat() {
        if (!mLogic.getis24Hour()) {
            Settings.System.putStringForUser(getActivity().getContentResolver(), Settings.System.TIME_12_24, HOURS_24,
                    UserHandle.USER_CURRENT);
        } else {
            Settings.System.putStringForUser(getActivity().getContentResolver(), Settings.System.TIME_12_24, HOURS_12,
                    UserHandle.USER_CURRENT);
        }
    }

    /**
     * get the name and gmt of current city.
     * 
     * @param tz
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    private String getTimeZoneText(TimeZone tz) {
        SimpleDateFormat sdf = new SimpleDateFormat("ZZZZ");
        sdf.setTimeZone(tz);
        Log.d(TAG, "<<<<<<< tz.getDisplayName()");
        return sdf.format(new Date()) + " " + tz.getDisplayName();
    }

    /**
     * change the time according to the current format.
     */
    public void refreshTime() {
        Log.d(TAG, "<<<<<<<<<enter into  refreshTime<<<<<<<<<");
        mCorrectTimePreference.setRightText(mLogic.getSystemTime());
        Log.d(TAG, "<<<<<<<<<mLogic.getSystemTime()<<<<<<<<<" + mLogic.getSystemTime());
        mActivity.mHandler.sendEmptyMessageDelayed(EVENT_UPDATE_STATS, CORRECT_UPDATE_TIME);
    }

    /**
     * the listener of preference.
     */
    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (COORRECT_TIME_KEY.endsWith(key)) {
            ((DateTimeCitySettingsActivity) getActivity())
                    .replaceFragment(new NtpServerFragment(), "NtpServerFragment");
        }
        if (key.equals(TIMEZONE_KEY)) {
            Utils.intentForward(this.getActivity(), TimeZoneSettingActivity.class);
            ((DateTimeCitySettingsActivity) getActivity()).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

        if (key.equals(CITY_KEY)) {
            Utils.intentForward(this.getActivity(), CitySettingActivity.class);
            ((DateTimeCitySettingsActivity) getActivity()).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference arg0, Object arg1) {
        is24HourFormat();
        mCorrectTimePreference.setRightText(mLogic.getSystemTime());
        mActivity.sendTimeChangeBroadcast();
        return true;
    }

}
