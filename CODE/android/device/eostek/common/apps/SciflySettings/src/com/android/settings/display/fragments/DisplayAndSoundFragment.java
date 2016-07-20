
package com.android.settings.display.fragments;

import com.android.settings.BaseSettingActivity;
import com.android.settings.R;
import com.android.settings.SettingPreference;
import com.android.settings.sound.SoundSettingFragment;
import com.android.settings.util.Utils;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * ScreenSettingFragment.
 * 
 * @author Davis
 * @date 2015-8-19
 */
public class DisplayAndSoundFragment extends PreferenceFragment implements OnPreferenceClickListener {

    private static final String SCREEN_KEY = "screen";

    private static final String SOUND_KEY = "sound";

    private static final String SCREENSAVER_KEY = "screensaver";

    private SettingPreference mScreenPreference;

    private SettingPreference mSoundPreference;

    private SettingPreference mScreenSaverPreference;

    private BaseSettingActivity mActivity;

    /*
     * public interface onPreferenceClickListener { void
     * onPreferenceClick(String key); }
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_display_sound);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_preference_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (BaseSettingActivity) getActivity();
        mActivity.setSubTitle();

        mScreenPreference = (SettingPreference) findPreference(SCREEN_KEY);
        if (mScreenPreference != null) {
            mScreenPreference.setOnPreferenceClickListener(this);
        }

        mSoundPreference = (SettingPreference) findPreference(SOUND_KEY);
        if (mSoundPreference != null) {
            mSoundPreference.setOnPreferenceClickListener(this);
        }

        mScreenSaverPreference = (SettingPreference) findPreference(SCREENSAVER_KEY);
        if (mScreenSaverPreference != null) {
            mScreenSaverPreference.setOnPreferenceClickListener(this);
        }

        if (!(Build.DEVICE.equals("scifly_m202_1G") || Build.DEVICE.equals("heran"))) {
            if (mScreenPreference != null) {
                getPreferenceScreen().removePreference(mScreenPreference);
            }
//            if (mScreenSaverPreference != null) {
//                getPreferenceScreen().removePreference(mScreenSaverPreference);
//            }
        }
        
        String platform = SystemProperties.get("ro.scifly.platform", "");
        if (Utils.SCIFLY_PLATFORM_DONGLE.equals(platform)) {
            if (mSoundPreference != null) {
                getPreferenceScreen().removePreference(mSoundPreference);
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        Log.d(getClass().getSimpleName(), "fra99999gmentnceClick=====>" + key);
        if (key.equals(SCREEN_KEY)) {
            mActivity.addFragment(new ScreenSettingFragment());
        }

        if (key.equals(SOUND_KEY)) {
            mActivity.addFragment(new SoundSettingFragment());
        }

        if (key.equals(SCREENSAVER_KEY)) {
            mActivity.addFragment(new ScreenSaverFragment());
        }
        return false;

    }
}
