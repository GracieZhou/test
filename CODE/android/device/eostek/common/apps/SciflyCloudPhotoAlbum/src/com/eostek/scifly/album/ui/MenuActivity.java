
package com.eostek.scifly.album.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.eostek.scifly.album.Constants;
import com.eostek.scifly.album.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class MenuActivity extends Activity {
    private final int mDelayTime = 5 * 1000;

    private final int FADE_MSG = 1;

    @ViewInject(R.id.slideshow)
    private TextView mSlideShowView;

    @ViewInject(R.id.setting)
    private TextView mSettingView;

    private Bundle mBundle;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == FADE_MSG) {
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_layout);
        ViewUtils.inject(this);
        if (getIntent() != null) {
            mBundle = getIntent().getBundleExtra("bundle");
        }
        mHandler.sendEmptyMessageDelayed(FADE_MSG, mDelayTime);
    }

    @OnClick({
            R.id.slideshow, R.id.setting
    })
    private void textViewOnclick(View view) {
        switch (view.getId()) {
            case R.id.slideshow:
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
                break;
            case R.id.setting:
                Intent intent = new Intent(Constants.EFFECT_SETTING_ACTION);
                if (mBundle != null) {
                    intent.putExtra("bundle", mBundle);
                }
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mHandler.removeMessages(FADE_MSG);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mHandler.sendEmptyMessageDelayed(FADE_MSG, mDelayTime);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.fade_out_right);
    }
}
