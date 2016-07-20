
package com.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.settings.sound.SoundSettingsActivity;
import com.android.settings.widget.TitleWidget;

public class DisplayAndSoundActivity extends Activity {
    private static final int SOUND_SETTINGS_ACTIVITY = 0;

    private static final int SCREEN_SETTINGS_ACTIVITY = 1;

    private TitleWidget mTitle;

    private Button mSoundBtn;

    private Button mDisplayBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sound_settings);
        findViews();
        setSubTitleText();
        registerListener();
    }

    private void findViews() {
        mTitle = (TitleWidget) findViewById(R.id.activity_display_sound_settings_title);
        mSoundBtn = (Button) findViewById(R.id.btn_sound);
        mSoundBtn.setText(getResources().getString(R.string.sound_settings));
        mDisplayBtn = (Button) findViewById(R.id.btn_display);
        mDisplayBtn.setText(getResources().getString(R.string.screen_settings));
        if (Build.DEVICE.equals("heran") || Build.DEVICE.equals("scifly_m202_1G")) {
            mDisplayBtn.setVisibility(View.VISIBLE);
        }
    }

    private void registerListener() {
        mSoundBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(SOUND_SETTINGS_ACTIVITY);
            }
        });
        mDisplayBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(SCREEN_SETTINGS_ACTIVITY);
            }
        });
    }

    private void setSubTitleText() {
        mTitle.setMainTitleText(getString(R.string.action_settings));
        if (Build.DEVICE.equals("heran") || Build.DEVICE.equals("scifly_m202_1G")) {
            mTitle.setFirstSubTitleText(getString(R.string.display_and_sound), true);
        } else {
            mTitle.setFirstSubTitleText(getString(R.string.sound), true);
        }
    }

    private void startActivity(int id) {
        if (id == SOUND_SETTINGS_ACTIVITY) {
            Intent intent = new Intent(this, SoundSettingsActivity.class);
            this.startActivity(intent);
        } else if (id == SCREEN_SETTINGS_ACTIVITY) {
            try {
                Class<?> screenSettingsActivity = Class.forName("com.android.settings.display.ScreenSettingsActivity");
                Intent intent = new Intent(this, screenSettingsActivity);
                this.startActivity(intent);
            } catch (IllegalArgumentException iAE) {
                throw iAE;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
