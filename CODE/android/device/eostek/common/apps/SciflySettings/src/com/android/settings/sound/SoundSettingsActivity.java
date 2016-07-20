
package com.android.settings.sound;

import com.android.settings.R;
import com.android.settings.util.Utils;
import com.android.settings.widget.SwitchChangeListener;
import com.android.settings.widget.TextSwitchWidget;
import com.android.settings.widget.TitleWidget;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class SoundSettingsActivity extends Activity {

    private static final String TAG = "SoundSettingsActivity";

    private TitleWidget mTitle;

    private TextSwitchWidget mKeyToneWidget;

    private TextSwitchWidget mNotificationToneWidget;

    private AudioManager mAudioManager;

    private boolean mSilentMode;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_settings);
        findViews();
        initViews();
        registerListener();
        String platform = SystemProperties.get("ro.scifly.platform", "");
        if (Utils.SCIFLY_PLATFORM_DONGLE.equals(platform)) {
            mNotificationToneWidget.setVisibility(View.GONE);
        }
    }

    public void findViews() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mSilentMode = (mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL);
        }
        mTitle = (TitleWidget) findViewById(R.id.activity_sound_settings_title);
        mKeyToneWidget = (TextSwitchWidget) findViewById(R.id.key_tone);
        mNotificationToneWidget = (TextSwitchWidget) findViewById(R.id.notification_tone);

    }

    public void initViews() {
        mTitle.setSubTitleText(getString(R.string.sound_settings));
        mKeyToneWidget.setText(getString(R.string.key_tone));
        mNotificationToneWidget.setText(getString(R.string.notification_tone));
        if (mSilentMode) {
            mKeyToneWidget.setStatus(false);
            mNotificationToneWidget.setStatus(false);
            return;
        }

        try {
            boolean flag = Settings.System.getInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED) == 1;
            mKeyToneWidget.setStatus(flag);

        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }

        boolean isNotificationMute = TextUtils.isEmpty(Settings.System.getString(getContentResolver(),
                Settings.System.NOTIFICATION_SOUND));
        Log.d(TAG, "isNotificationMute = " + isNotificationMute);
        mNotificationToneWidget.setStatus(!isNotificationMute);
    }

    public void registerListener() {
        mKeyToneWidget.setSwitchChangeListener(new SwitchChangeListener() {
            public void onSwitchChanged(boolean on) {
                mKeyToneWidget.setStatus(on);
                if (on) {
                    mAudioManager.loadSoundEffects();
                } else {
                    mAudioManager.unloadSoundEffects();
                }
                Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, on ? 1 : 0);
            }
        });

        mKeyToneWidget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                boolean status = mKeyToneWidget.getStatus();
                mKeyToneWidget.setStatus(!status);
                Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, !status ? 1 : 0);
            }
        });

        mNotificationToneWidget.setSwitchChangeListener(new SwitchChangeListener() {
            public void onSwitchChanged(boolean on) {
                Log.d(TAG, ">>>>>>>on = " + on);
                mNotificationToneWidget.setStatus(on);
                // 此处最好的处理逻辑是控制设置音量，AudioManager里面有一种方法setStreamVolume，可以调节不同声音类型的音量，
                // 但是由于com.android.internal.R.bool.config_useMasterVolume是true，所以导致每次调节音量是整体在调节，不是对单个音量通道进行的
                Settings.System.putString(getContentResolver(), Settings.System.NOTIFICATION_SOUND,
                        on ? "content://media/internal/audio/media/98" : "");
            }
        });

        mNotificationToneWidget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                boolean status = mNotificationToneWidget.getStatus();
                mNotificationToneWidget.setStatus(!status);
                Settings.System.putString(getContentResolver(), Settings.System.NOTIFICATION_SOUND,
                        !status ? "content://media/internal/audio/media/98" : "");
            }
        });
    }

}
