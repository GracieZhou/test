
package com.android.settings.display.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.BaseSettingActivity;
import com.android.settings.R;
import com.android.settings.SettingPreference;
import com.android.settings.screensaver.DreamBackend;
import com.android.settings.screensaver.DreamBackend.DreamInfo;
import com.android.settings.screensaver.NotificationAccessHelper;
import com.android.settings.screensaver.NotificationCollectorService;

/**
 * ScreenSaverFragment.
 * 
 * @author Davis
 * @date 2015-8-21
 */
public class ScreenSaverFragment extends PreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = ScreenSaverFragment.class.getSimpleName();

    private static final String SCREEN_SAVER_START_TIME = "screen_saver_start_time";

    private static final String SCREENSAVER_ACTIVATE_ON_SLEEP = "screensaver_sleep_time";

    private static final int DISABLE_SCREEN_SAVER = 0;

    private static final int FIVE_MINUTE = 5 * 60 * 1000;

    private static final int TEN_MINUTE = 10 * 60 * 1000;

    private static final int THIRTY_MINUTE = 30 * 60 * 1000;

    private static final int ONE_HOUR = 60 * 60 * 1000;

    private static int[] screenSaverTime = new int[] {
            DISABLE_SCREEN_SAVER, FIVE_MINUTE, TEN_MINUTE, THIRTY_MINUTE, ONE_HOUR
    };

    private static final int DISABLE_SCREEN_SAVER_OPTION_SELECTED = 0;

    private static final int FIVE_MINUTE_OPTION_SELECTED = 1;

    private static final int TEN_MINUTE_OPTION_SELECTED = 2;

    private static final int THIRTY_MINUTE_OPTION_SELECTED = 3;

    private static final int ONE_HOUR_OPTION_SELECTED = 4;

    private static final String SCREENSAVER_PICTURE_FROM = "screen_saver_picture_from";

    private DreamBackend mBackend;

    private ComponentName mEnableName;

    private NotificationAccessHelper mAccessHelper;

    private List<DreamInfo> mDreamInfos = new ArrayList<DreamInfo>();

    private BaseSettingActivity mActivity;

    private SettingPreference mScreenSaverStartTimePreference;

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        addPreferencesFromResource(R.xml.preference_screen_saver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting_preference_fragment, container, false);

        mActivity = (BaseSettingActivity) getActivity();
        mActivity.setSubTitle(R.string.screen_saver_setting);

        mDreamInfos.clear();
        mBackend = new DreamBackend(mActivity);
        mDreamInfos = mBackend.getDreamInfos();
        for (DreamInfo info : mDreamInfos) {
            Log.v(TAG, "" + info.componentName.getClassName());
            if (info.componentName.getClassName().contains("EostekDream")) {
                mBackend.setActiveDream(info.componentName);
            }
        }
        mEnableName = new ComponentName(mActivity.getPackageName(), NotificationCollectorService.class.getName());
        mAccessHelper = new NotificationAccessHelper(mEnableName);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mScreenSaverStartTimePreference = (SettingPreference) findPreference(SCREEN_SAVER_START_TIME);
        mScreenSaverStartTimePreference.setOnPreferenceChangeListener(this);

        initViews();
    }

    private void initViews() {

        int select = 0;
        try {
            select = Settings.System.getInt(mActivity.getContentResolver(), SCREENSAVER_ACTIVATE_ON_SLEEP);
            Log.v(TAG, "SCREENSAVER_ACTIVATE_ON_SLEEP = " + select);
            if (select == DISABLE_SCREEN_SAVER) {
                select = DISABLE_SCREEN_SAVER_OPTION_SELECTED;
            } else if (select == FIVE_MINUTE) {
                select = FIVE_MINUTE_OPTION_SELECTED;
            } else if (select == TEN_MINUTE) {
                select = TEN_MINUTE_OPTION_SELECTED;
            } else if (select == THIRTY_MINUTE) {
                select = THIRTY_MINUTE_OPTION_SELECTED;
            } else if (select == ONE_HOUR) {
                select = ONE_HOUR_OPTION_SELECTED;
            } else {
                select = DISABLE_SCREEN_SAVER_OPTION_SELECTED;
            }
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }

        try {
            select = Settings.System.getInt(mActivity.getContentResolver(), SCREENSAVER_PICTURE_FROM);
            Log.v(TAG, "SCREENSAVER_PICTURE_FROM = " + select);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
            select = DISABLE_SCREEN_SAVER_OPTION_SELECTED;
        }

        // disable picture select for now


        /********************************
         * if (mStartScreenSaver.getValue() == 0) {
         * mScreenSaverPicture.setFocusable(false);
         * mScreenSaverPicture.disableText();
         * mScreenSaverDir.setFocusable(false); mScreenSaverDir.disableText(); }
         * else if (mScreenSaverPicture.getValue() == 0) {
         * mScreenSaverDir.setFocusable(false); mScreenSaverDir.disableText(); }
         **************/
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.v(TAG, "onValueChanged value = " + newValue);
        /*
         * String value; if (newValue instanceof String) { value =
         * newValue.toString(); }
         */

        int value = -1;
        String[] strArray = mActivity.getResources().getStringArray(R.array.screen_saver_start_time_arrays);
        for (int i = 0; i < strArray.length; i++) {
            if (newValue.equals(strArray[i])) {
                value = i;
		   break;
            }
        }
        Log.d(getClass().getSimpleName(), "the new value is ===> " + newValue + " " + value);

        String key = preference.getKey();
        if (key.equals(SCREEN_SAVER_START_TIME)) {

            // the default value,do not start screen saver
            if (value == 0) {
                mBackend.setEnabled(false);
                mAccessHelper.removeCurentEnableService(mActivity.getContentResolver());
            } else {
                mAccessHelper.saveCurentEnableService(mActivity.getContentResolver());
                if (!mBackend.isEnabled()) {
                    mBackend.setEnabled(true);
                }
            }
            int time = -1;
            time = screenSaverTime[value];
            Settings.System.putIntForUser(mActivity.getContentResolver(), SCREENSAVER_ACTIVATE_ON_SLEEP, time,
                    UserHandle.USER_CURRENT);
        }

        return true;
    }
}
