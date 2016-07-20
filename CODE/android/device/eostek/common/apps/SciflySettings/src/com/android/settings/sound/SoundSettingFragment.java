
package com.android.settings.sound;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.BaseSettingActivity;
import com.android.settings.R;
import com.android.settings.SettingPreference;
import com.android.settings.util.Utils;

/**
 * ScreenSettingFragment.
 * 
 * @author Davis
 * @date 2015-8-19
 */
public class SoundSettingFragment extends PreferenceFragment implements OnPreferenceClickListener {

    private static final String KEY_TONE_KEY = "key_tone";

    private static final String NOTIFICATION_KEY = "notification_tone";

    private SettingPreference mKeyTonePreference;

    private SettingPreference mNotificationTonePreference;

    private AudioManager mAudioManager;

    private BaseSettingActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_sound_setting);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting_preference_fragment, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = (BaseSettingActivity) getActivity();
        mActivity.setSubTitle(R.string.sound_settings);

        mKeyTonePreference = (SettingPreference) findPreference(KEY_TONE_KEY);
        mKeyTonePreference.setOnPreferenceClickListener(this);

        mNotificationTonePreference = (SettingPreference) findPreference(NOTIFICATION_KEY);
        mNotificationTonePreference.setOnPreferenceClickListener(this);

        String platform = SystemProperties.get("ro.scifly.platform", "");
        if (Utils.SCIFLY_PLATFORM_DONGLE.equals(platform)) {
            getPreferenceScreen().removePreference(mNotificationTonePreference);
        }

        initPreferenceStatus();
    }

    public void initPreferenceStatus() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
            boolean mSilentMode = (mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL);
            if (mSilentMode) {
                mKeyTonePreference.setChecked(false);
                mNotificationTonePreference.setChecked(false);
                return;
            }
        }

        try {
            boolean flag = Settings.System
                    .getInt(mActivity.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED) == 1;
            mKeyTonePreference.setChecked(flag);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }

        boolean isNotificationMute = TextUtils.isEmpty(Settings.System.getString(mActivity.getContentResolver(),
                Settings.System.NOTIFICATION_SOUND));
        mNotificationTonePreference.setChecked(!isNotificationMute);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(KEY_TONE_KEY)) {
            mKeyTonePreference.toggleButton();
            if (mKeyTonePreference.isChecked()) {
                mAudioManager.loadSoundEffects();
            } else {
                mAudioManager.unloadSoundEffects();
            }
            Settings.System.putInt(mActivity.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED,
                    mKeyTonePreference.isChecked() ? 1 : 0);
        } else if (key.equals(NOTIFICATION_KEY)) {
            mNotificationTonePreference.toggleButton();
            Settings.System.putString(mActivity.getContentResolver(), Settings.System.NOTIFICATION_SOUND,
                    mNotificationTonePreference.isChecked() ? "content://media/internal/audio/media/98" : "");
        }
        return false;
    }
}
