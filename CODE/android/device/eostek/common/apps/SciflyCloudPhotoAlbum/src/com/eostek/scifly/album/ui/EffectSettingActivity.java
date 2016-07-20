
package com.eostek.scifly.album.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.eostek.scifly.album.Constants;
import com.eostek.scifly.album.R;
import com.eostek.scifly.album.view.TextSelectorWidget;
import com.eostek.scifly.album.view.ValueChangeListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class EffectSettingActivity extends Activity {
    @ViewInject(R.id.switch_effect)
    private TextSelectorWidget mSwitchEffect;

    @ViewInject(R.id.time_period)
    private TextSelectorWidget mTimePeriod;

    @ViewInject(R.id.slideshow)
    private Button mSlideShow;

    private SharedPreferences mPreferences;

    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_effect_setting);
        ViewUtils.inject(this);
        mPreferences = getSharedPreferences(Constants.SHAREPREFRER_STRING, Context.MODE_PRIVATE);
        if (getIntent() != null) {
            mBundle = getIntent().getBundleExtra("bundle");
        }
        initView();
    }

    @OnClick(R.id.slideshow)
    private void btnOnclick(View view) {
        startSlideShow(true);
    }

    private void initView() {
        mSwitchEffect.setItemName(getString(R.string.switch_effect));
        mSwitchEffect.setValueItems(getResources().getStringArray(R.array.pager_effects));
        mSwitchEffect.setCurrentValue(mPreferences.getInt(Constants.EFFECT_KEY, 0));

        mTimePeriod.setItemName(getString(R.string.time_period));
        mTimePeriod.setValueRange(1, 10);
        mTimePeriod.setFlag(TextSelectorWidget.TIME_SECOND_FLAG);
        mTimePeriod.setCurrentValue(mPreferences.getInt(Constants.PERIOD_KEY, 1));

        registListener();
    }

    private void startSlideShow(boolean autoplay) {
        Intent intent = new Intent(Constants.IMAG_EPAGER_ACTION);
        if (mBundle != null) {
            mBundle.putBoolean(Constants.AUTO_PLAY, true);
        }
        intent.putExtra("bundle", mBundle);
        startActivity(intent);
        finish();
    }

    private void registListener() {
        mSwitchEffect.setValueChangeListener(new ValueChangeListener() {

            @Override
            public void onValueChanged(int value) {
                mPreferences.edit().putInt(Constants.EFFECT_KEY, value).commit();
            }
        });
        mTimePeriod.setValueChangeListener(new ValueChangeListener() {

            @Override
            public void onValueChanged(int value) {
                mPreferences.edit().putInt(Constants.PERIOD_KEY, value).commit();
            }
        });
    }
}
