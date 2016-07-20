
package com.android.settings.display;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.settings.R;
import com.android.settings.widget.TitleWidget;

public class ScreenSettingsActivity extends Activity {
    private static final int SCREEN_ZOOM_ACTIVITY = 0;

    private static final int SCREEN_POSITION_ACTIVITY = 1;

    private static final String LIST_ITEM_INFO = "screenItemInfo";

    private TitleWidget mTitle;

    private Button mScreenZoomBtn;
    private Button mScreenPositionBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_settings);
        findViews();
        setSubTitleText();
        registerListener();
    }

    private void findViews() {
        mTitle = (TitleWidget) findViewById(R.id.activity_screen_settings_title);
        mScreenZoomBtn=(Button)findViewById(R.id.btn_screen_zoom);
        mScreenZoomBtn.setText(getResources().getString(R.string.screen_zoom));
        mScreenPositionBtn=(Button)findViewById(R.id.btn_screen_position);
        mScreenPositionBtn.setText(getResources().getString(R.string.screen_position));

    }

    private void registerListener() {
        mScreenZoomBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(SCREEN_ZOOM_ACTIVITY);
            }
        });
        mScreenPositionBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(SCREEN_POSITION_ACTIVITY);
            }
        });
    }

    private void setSubTitleText() {
        mTitle.setMainTitleText(getString(R.string.action_settings));
        mTitle.setFirstSubTitleText(getString(R.string.display_and_sound), false);
        mTitle.setSecondSubTitleText(getString(R.string.screen_settings));
    }


    private void startActivity(int id) {
        if (id == SCREEN_ZOOM_ACTIVITY) {
            Intent intent = new Intent(this, ScreenZoomActivity.class);
            this.startActivity(intent);
        } else if (id == SCREEN_POSITION_ACTIVITY) {
            Intent intent = new Intent(this, ScreenPositionActivity.class);
            this.startActivity(intent);
        }
    }
}
