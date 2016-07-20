
package com.android.settings.display.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.BaseSettingActivity;
import com.android.settings.R;
import com.android.settings.SettingPreference;
import com.android.settings.display.ScreenPositionActivity;
import com.android.settings.display.ScreenZoomActivity;

/**
 * ScreenSettingFragment.
 * 
 * @author Davis
 * @date 2015-8-19
 */
public class ScreenSettingFragment extends PreferenceFragment implements OnPreferenceClickListener {

    private static final String SCREEN_SCALE_KEY = "screen_scale";

    private static final String SCREEN_TRANSLATE_KEY = "screen_translate";

    private SettingPreference mScreenScalePreference;

    private SettingPreference mScreenTranslatePreference;

    private BaseSettingActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_screen_setting);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_preference_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = (BaseSettingActivity) getActivity();
        mActivity.setSubTitle(R.string.screen_settings);

        mScreenScalePreference = (SettingPreference) findPreference(SCREEN_SCALE_KEY);
        mScreenScalePreference.setOnPreferenceClickListener(this);

        mScreenTranslatePreference = (SettingPreference) findPreference(SCREEN_TRANSLATE_KEY);
        mScreenTranslatePreference.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(SCREEN_SCALE_KEY)) {
            Intent intent = new Intent(mActivity, ScreenZoomActivity.class);
            startActivity(intent);
        } else if (key.equals(SCREEN_TRANSLATE_KEY)) {
            Intent intent = new Intent(mActivity, ScreenPositionActivity.class);
            startActivity(intent);
        }
        return false;
    }
}
