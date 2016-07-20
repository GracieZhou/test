
package com.eostek.tvmenu.picture;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.TvMenuActivity;
import com.eostek.tvmenu.TvMenuHolder;

public class ItemDialog extends AlertDialog {

    private final static String TAG = "ItemDialog";

    private List<SettingItem> mItems;

    private int mLastPosition = 0;

    private int mCurPosition = 0;

    private int mNextPosition = 0;

    private Activity mContext;

    private TextView titleTxt;

    private TextView nextTxt;

    private TextView lastTxt;

    private TextView valueTxt;

    private TextView mSeekbarTxt;

    private SeekBar mSeekBar;

    private ViewGroup mCurViewGroup;

    private ViewGroup mEnumViewGroup;

    private ViewGroup mSeekbarViewGroup;
    
    private PictureSettingFragment mFragment;

    public ItemDialog(PictureSettingFragment f, List<SettingItem> items, int curPosition) {
        super(f.getActivity());
        this.mFragment = f;
        this.mContext = f.getActivity();
        this.mItems = items;
        this.mCurPosition = curPosition;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingItem item = mItems.get(mCurPosition);
        this.setContentView(R.layout.setting_dialog);
        titleTxt = (TextView) findViewById(R.id.title_txt);
        lastTxt = (TextView) findViewById(R.id.last_txt);
        nextTxt = (TextView) findViewById(R.id.next_txt);
        valueTxt = (TextView) findViewById(R.id.value);
        titleTxt.setText(item.getTitle());
        mEnumViewGroup = (ViewGroup) findViewById(R.id.bar_enum_fl);
        mSeekbarViewGroup = (ViewGroup) findViewById(R.id.seekbar_rl);
        mSeekbarTxt = (TextView) findViewById(R.id.dialog_seekbar_number);
        mSeekBar = (SeekBar) findViewById(R.id.dialog_seekbar);
        mSeekBar.setFocusable(false);
        Drawable d = mContext.getResources().getDrawable(R.drawable.seekbar_thumb2);
        mSeekBar.setThumbOffset(d.getIntrinsicWidth() / 2);
        if (item.getItemType() == PictureConstants.TYPE_ITEM_ENUM) {
            mEnumViewGroup.setVisibility(View.VISIBLE);
            valueTxt.setText(item.getValues()[item.getCurValue()]);
            mCurViewGroup = mEnumViewGroup;
        } else if (item.getItemType() == PictureConstants.TYPE_ITEM_DIGITAL) {
            mSeekbarViewGroup.setVisibility(View.VISIBLE);
            mCurViewGroup = mSeekbarViewGroup;
            updateSeekBar(item);
        }
        ((TvMenuActivity) mContext).getHandler().removeMessages(TvMenuHolder.FINISH);
        ((TvMenuActivity) mContext).getHandler().sendEmptyMessageDelayed(TvMenuHolder.FINISH,
                TvMenuActivity.DIMISS_DELAY_TIME);

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.settingdialog_anim_in);
        findViewById(R.id.dialog_rl).startAnimation(animation);
        Window w = getWindow();
        WindowManager.LayoutParams wl = w.getAttributes();
        wl.dimAmount = 0.0f;
        wl.gravity = Gravity.BOTTOM;
        w.setAttributes(wl);

        updateTitle();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        ((TvMenuActivity) mContext).getHandler().removeMessages(TvMenuHolder.FINISH);
        ((TvMenuActivity) mContext).getHandler().sendEmptyMessageDelayed(TvMenuHolder.FINISH,
                TvMenuActivity.DIMISS_DELAY_TIME);
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
                mFragment.mHolder.updateFocus(mCurPosition);
                updateValueView();
                updateTitle();
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
                mFragment.mHolder.updateFocus(mCurPosition);
                updateValueView();
                updateTitle();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mItems.get(mCurPosition).getFocusable()) {
                    mItems.get(mCurPosition).updateItemValue(keyCode, mCurPosition);
                    updateValueView();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void dismiss() {
        mFragment.mHolder.updateFocus(mCurPosition);
        ((TvMenuActivity) mContext).getHandler().sendEmptyMessageDelayed(TvMenuHolder.FINISH,
                TvMenuActivity.DIMISS_DELAY_TIME);
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
                ItemDialog.super.dismiss();
                mContext.findViewById(R.id.main).setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateTitle() {
        updateLastTitle();
        updateNextTitle();
    }
    
    private void updateLastTitle() {
        if (mItems.size() < 2) {
            lastTxt.setVisibility(View.INVISIBLE);
            return;
        }
        mLastPosition = mCurPosition;
        do {
            if (mLastPosition == 0) {
                mLastPosition = mItems.size() - 1;
            } else {
                mLastPosition--;
            }
        } while (!mItems.get(mLastPosition).getFocusable());
        Log.v(TAG, "mLastPosition = " + mLastPosition);
        lastTxt.setText(mItems.get(mLastPosition).getTitle());
    }

    private void updateNextTitle() {
        if (mItems.size() < 1) {
            nextTxt.setVisibility(View.INVISIBLE);
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
        nextTxt.setText(mItems.get(mNextPosition).getTitle());
    }

    public void updateValueView() {
        SettingItem item = mItems.get(mCurPosition);
        titleTxt.setText(item.getTitle());
        if (item.getItemType() == PictureConstants.TYPE_ITEM_ENUM) {
            mCurViewGroup.setVisibility(View.INVISIBLE);
            mEnumViewGroup.setVisibility(View.VISIBLE);
            mCurViewGroup = mEnumViewGroup;
            titleTxt.setText(item.getTitle());
            valueTxt.setText(item.getValues()[item.getCurValue()]);
        } else if (item.getItemType() == PictureConstants.TYPE_ITEM_DIGITAL) {
            mCurViewGroup.setVisibility(View.INVISIBLE);
            mSeekbarViewGroup.setVisibility(View.VISIBLE);
            mCurViewGroup = mSeekbarViewGroup;
            titleTxt.setText(item.getTitle());
            updateSeekBar(item);
        }
    }
    
    void updateSeekBar(SettingItem item) {
        int curValue = item.getCurValue();
        FrameLayout.LayoutParams paramsStrength = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        if (item.getStartValue() == 0 && item.getEndValue() == 100) {
            mSeekBar.setMax(100);
            mSeekBar.setProgress(curValue);
            paramsStrength.leftMargin = curValue * 601 / 100 - 4;
        } else if (item.getStartValue() == -50 && item.getEndValue() == 50) {
            mSeekBar.setMax(100);
            mSeekBar.setProgress(curValue + 50);
            paramsStrength.leftMargin = (curValue + 50) * 601 / 100 - 4;
        } else if (item.getStartValue() == 0 && item.getEndValue() == 128) {
            mSeekBar.setMax(128);
            mSeekBar.setProgress(curValue);
            paramsStrength.leftMargin = curValue * 601 / 128 - 4;
        }
        mSeekbarTxt.setText(String.valueOf(curValue));
        mSeekbarTxt.setLayoutParams(paramsStrength);
        mSeekbarTxt.setWidth(70);
    }
}
