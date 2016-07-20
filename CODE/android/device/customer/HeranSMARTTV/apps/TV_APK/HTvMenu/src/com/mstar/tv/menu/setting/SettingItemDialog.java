
package com.mstar.tv.menu.setting;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mstar.tv.MenuConstants;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.ui.EosCustomSettingActivity;

/*
 * @projectName： EOSTVMenu
 * @moduleName： SettingItemDialog.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time  2013-12-18
 * @Copyright © 2013 EOSTEK, Inc.
 */
public class SettingItemDialog extends AlertDialog {
    
    private final static String TAG = "SettingItemDialog";
    
    private List<EosSettingItem> mItems;

    private int mLastPosition = 0;

    private int mCurPosition = 0;

    private int mNextPosition = 0;

    private Activity mContext;

    private TextView title_txt;

    private TextView titleCur_txt;

    private TextView next_txt;

    private TextView last_txt;

    private TextView progress_txt;

    private LinearLayout progress_context;

    private LinearLayout title_cur;

    private ProgressBar bar;

    protected SettingItemDialog(Context context) {
        super(context);
    }

    public SettingItemDialog(Activity context, List<EosSettingItem> items, int curPosition) {
        super(context);
        this.mContext = context;
        this.mItems = items;
        this.mCurPosition = curPosition;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.eos_setting_dialog);
        ((EosCustomSettingActivity) mContext).getHandler().removeMessages(EosCustomSettingActivity.DELAYFINISH);
        ((EosCustomSettingActivity) mContext).getHandler().sendEmptyMessageDelayed(
                EosCustomSettingActivity.DELAYFINISH, EosCustomSettingActivity.TODIMISSDELAYTIME);

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.settingdialog_anim_in);
        findViewById(R.id.dialog_rl).startAnimation(animation);
        Window w = getWindow();
        w.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams wl = w.getAttributes();
        wl.dimAmount = 0.0f;
        wl.gravity = Gravity.BOTTOM;
        w.setAttributes(wl);

        progress_context = (LinearLayout) findViewById(R.id.progress_context);
        title_cur = (LinearLayout) findViewById(R.id.title_cur);
        title_txt = (TextView) findViewById(R.id.title_txt);
        titleCur_txt = (TextView) findViewById(R.id.title_cur_txt);
        last_txt = (TextView) findViewById(R.id.last_txt);
        next_txt = (TextView) findViewById(R.id.next_txt);
        progress_txt = (TextView) findViewById(R.id.value);
        bar = (ProgressBar) findViewById(R.id.progress);
        initData();
    }

    private void initData() {
        setCurValue();
        setLastTitle();
        setNextTitle();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        ((EosCustomSettingActivity) mContext).getHandler().removeMessages(EosCustomSettingActivity.DELAYFINISH);
        ((EosCustomSettingActivity) mContext).getHandler().sendEmptyMessageDelayed(
                EosCustomSettingActivity.DELAYFINISH, EosCustomSettingActivity.TODIMISSDELAYTIME);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_UP:
                    dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                    dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                dismiss();
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                do {
                    if (mCurPosition == 0) {
                        mCurPosition = mItems.size() - 1;
                    } else {
                        mCurPosition--;
                    }
                } while (!mItems.get(mCurPosition).getFocusable());
                Log.v(TAG, "mCurPosition = " + mCurPosition);
                setCurValue();
                setLastTitle();
                setNextTitle();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                do {
                    if (mCurPosition == mItems.size() - 1) {
                        mCurPosition = 0;
                    } else {
                        mCurPosition++;
                    }
                } while (!mItems.get(mCurPosition).getFocusable());
                Log.v(TAG, "mCurPosition = " + mCurPosition);
                setCurValue();
                setLastTitle();
                setNextTitle();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mItems.get(mCurPosition).getItemType() == MenuConstants.ITEMTYPE_BUTTON) {
                    mItems.get(mCurPosition).itemClicked(mCurPosition);
                } else if (mItems.get(mCurPosition).getFocusable()) {
                    mItems.get(mCurPosition).onKeyDown(keyCode, event, mCurPosition);
                    initData();
                }
                break;
            case KeyEvent.KEYCODE_ENTER:
                if (mItems.get(mCurPosition).getItemType() == MenuConstants.ITEMTYPE_BUTTON) {
                    mItems.get(mCurPosition).itemClicked(mCurPosition);
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void dismiss() {
        ((EosCustomSettingActivity) mContext).getHandler().sendEmptyMessageDelayed(
                EosCustomSettingActivity.DELAYFINISH, EosCustomSettingActivity.TODIMISSDELAYTIME);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.settingdialog_anim_out);
        animation.setFillAfter(true);
        findViewById(R.id.dialog_rl).startAnimation(animation);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                SettingItemDialog.super.dismiss();
                mContext.findViewById(R.id.main).setVisibility(View.VISIBLE);
                ((ListView) mContext.findViewById(R.id.context_lst)).requestFocus();
            }
        });
    }

    private void setLastTitle() {
        if (mItems.size() < 2) {
            last_txt.setVisibility(View.INVISIBLE);
            return;
        }
        mLastPosition = mCurPosition;
        do {
            if (mLastPosition == 0) {
                mLastPosition = mItems.size() - 1;
            } else {
                mLastPosition --;
            }
        } while (!mItems.get(mLastPosition).getFocusable());
        Log.v(TAG, "mLastPosition = " + mLastPosition);
        last_txt.setText(mItems.get(mLastPosition).getTitle());
    }

    private void setNextTitle() {
        if (mItems.size() < 1) {
            next_txt.setVisibility(View.INVISIBLE);
            return;
        }
        mNextPosition = mCurPosition;
        do {
            if (mNextPosition == mItems.size() - 1) {
                mNextPosition = 0;
            } else {
                mNextPosition++;
            }
        } while (!mItems.get(mNextPosition).getFocusable());
        Log.v(TAG, "mNextPosition = " + mNextPosition);
        next_txt.setText(mItems.get(mNextPosition).getTitle());
    }

    public void setCurValue() {
        EosSettingItem item = mItems.get(mCurPosition);
        title_txt.setText(item.getTitle());
        if (item.getItemType() == MenuConstants.ITEMTYPE_ENUM) {
            title_txt.setText(item.getTitle());
            progress_context.setVisibility(View.VISIBLE);
            title_cur.setVisibility(View.GONE);
            findViewById(R.id.bar_fl).setVisibility(View.VISIBLE);
            bar.setVisibility(View.GONE);
            progress_txt.setText(item.getValues()[item.getCurValue()]);
        } else if (item.getItemType() == MenuConstants.ITEMTYPE_DIGITAL) {
            title_txt.setText(item.getTitle());
            progress_context.setVisibility(View.VISIBLE);
            title_cur.setVisibility(View.GONE);
            findViewById(R.id.bar_fl).setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) bar.getLayoutParams();
            if (item.getStartValue() == 0 && item.getEndValue() == 100) {
                params.width = item.getCurValue() * 6;
                bar.setLayoutParams(params);
                if (item.getCurValue() == item.getStartValue()) {
                    bar.setVisibility(View.GONE);
                } else {
                    bar.setVisibility(View.VISIBLE);
                }
            } else if (item.getStartValue() == -50 && item.getEndValue() == 50) {
                params.width = (item.getCurValue() + 50) * 6;
                bar.setLayoutParams(params);
                if (item.getCurValue() == item.getStartValue()) {
                    bar.setVisibility(View.GONE);
                } else {
                    bar.setVisibility(View.VISIBLE);
                }
            } else if (item.getStartValue() == 0 && item.getEndValue() == 128) {
                params.width = item.getCurValue() * 5;
                bar.setLayoutParams(params);
                if (item.getCurValue() == item.getStartValue()) {
                    bar.setVisibility(View.GONE);
                } else {
                    bar.setVisibility(View.VISIBLE);
                }
            }
            progress_txt.setText(String.valueOf(item.getCurValue()));
        } else if (item.getItemType() == MenuConstants.ITEMTYPE_BOOL) {
            title_txt.setText(item.getTitle());
            progress_context.setVisibility(View.VISIBLE);
            title_cur.setVisibility(View.GONE);
            findViewById(R.id.bar_fl).setVisibility(View.VISIBLE);
            bar.setVisibility(View.GONE);
            if (item.getBoolValue()) {
                progress_txt.setText(item.getValues()[1]);
            } else {
                progress_txt.setText(item.getValues()[0]);
            }
        } else if (item.getItemType() == MenuConstants.ITEMTYPE_BUTTON) {
            findViewById(R.id.bar_fl).setBackgroundResource(R.drawable.setbar_bg2);
            progress_txt.setText(R.string.entertip);
            title_cur.setVisibility(View.VISIBLE);
            titleCur_txt.setText(item.getTitle());
        }
    }
}
