
package com.android.settings.sound;

import java.io.IOException;
import com.android.settings.R;
import com.android.settings.util.Utils;
import com.android.settings.widget.SwitchChangeListener;
import com.android.settings.widget.TextSwitchWidget;
import com.android.settings.widget.TitleWidget;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
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

    private static final String DEFAULT_NOTIFICATION_URI = "default_notification_uri";

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
        mTitle.setMainTitleText(getString(R.string.action_settings));
        if (Utils.SCIFLY_PLATFORM_TV.equals(SystemProperties.get("ro.scifly.platform", ""))) {
            mTitle.setFirstSubTitleText(getString(R.string.sound), false);
        } else {
            mTitle.setFirstSubTitleText(getString(R.string.display_and_sound), false);
        }
        mTitle.setSecondSubTitleText(getString(R.string.sound_settings));
        mKeyToneWidget.setText(getString(R.string.key_tone));
        mNotificationToneWidget.setText(getString(R.string.notification_tone));
        if (mSilentMode) {
            mKeyToneWidget.setStatus(false);
            mNotificationToneWidget.setStatus(false);
            return;
        }

        try {
            int effectEnabled = Settings.System.getInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED);
            mKeyToneWidget.setStatus(effectEnabled == 1);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }

        int volume = 0;
        try {
            volume = Settings.System.getInt(getContentResolver(), Settings.System.VOLUME_NOTIFICATION);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (volume == 0) {
            mNotificationToneWidget.setStatus(false);
            Log.d(TAG, ">>>>>the notification is mute ");
        } else {
            mNotificationToneWidget.setStatus(true);
            Log.d(TAG, ">>>>>the notification is  not mute ");
        }
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
                mNotificationToneWidget.setStatus(on);
                // mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, !on);
                // mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                Log.d(TAG, ">>>>>set volume =  " + (on ? 5 : 0));
                Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_NOTIFICATION, on ? 5 : 0);

                SharedPreferences sp = getSharedPreferences(DEFAULT_NOTIFICATION_URI, MODE_PRIVATE);
                if (on) {
                    Settings.System.putString(getContentResolver(), Settings.System.NOTIFICATION_SOUND,
                            sp.getString(DEFAULT_NOTIFICATION_URI, "content://settings/system/notification_sound"));
                } else {
                    if (TextUtils.isEmpty(sp.getString(DEFAULT_NOTIFICATION_URI, ""))) {
                        Editor editor = sp.edit();
                        editor.putString(DEFAULT_NOTIFICATION_URI,
                                Settings.System.getString(getContentResolver(), Settings.System.NOTIFICATION_SOUND));
                        editor.commit();
                    }
                    Settings.System.putString(getContentResolver(), Settings.System.NOTIFICATION_SOUND, null);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        boolean on = mNotificationToneWidget.getStatus();
        // mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, !on);
        Log.d(TAG, ">>>>>set volume =  " + (on ? 5 : 0));
        Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_NOTIFICATION, on ? 5 : 0);
        super.onPause();
    }

    @Override
    protected void onResume() {
        boolean on = mNotificationToneWidget.getStatus();
        // mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, !on);
        Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_NOTIFICATION, on ? 5 : 0);
        super.onResume();
    }
}
