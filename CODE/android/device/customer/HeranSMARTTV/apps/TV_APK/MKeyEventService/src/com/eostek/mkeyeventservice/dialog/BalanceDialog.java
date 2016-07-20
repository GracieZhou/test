
package com.eostek.mkeyeventservice.dialog;

import com.eostek.mkeyeventservice.Constants;
import com.eostek.mkeyeventservice.R;
import com.mstar.android.tv.TvAudioManager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import scifly.view.KeyEventExtra;

public class BalanceDialog extends Dialog {

    private static final String TAG = BalanceDialog.class.getSimpleName();

    /**
     * the keep time of dialog
     */
    private static final int DELAY_TIME = 3 * 1000;

    private long mLastClick = 0;

    private String[] mBanlace;

    private ImageView mLeft;

    private ImageView mRight;

    private TextView mValue;

    private int mCurValue = 0;

    private int mPosition = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.DISMISS:
                    dismiss();
                    break;
                case Constants.LEFT:
                    mLeft.setImageResource(R.drawable.conent_left_unaction);
                    break;
                case Constants.RIGHT:
                    mRight.setImageResource(R.drawable.conent_right_unaction);
                    break;
            }
        }
    };

    public BalanceDialog(Context context, int theme) {
        super(context, theme);
        mBanlace = context.getResources().getStringArray(R.array.Balance);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.balance_layout);
        findViews();
        initData();
    }

    private void findViews() {
        mLeft = (ImageView) findViewById(R.id.left);
        mRight = (ImageView) findViewById(R.id.right);
        mValue = (TextView) findViewById(R.id.balance);
    }

    private void initData() {
        mCurValue = getCurrentBalance();
        if (mCurValue != 0 && mCurValue != 100) {
            mCurValue = 50;
        }
        mPosition = getPosition(mCurValue);
        mValue.setText(mBanlace[mPosition]);
        mHandler.sendEmptyMessageDelayed(Constants.DISMISS, DELAY_TIME);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mHandler.removeMessages(Constants.DISMISS);
        mHandler.sendEmptyMessageDelayed(Constants.DISMISS, DELAY_TIME);

        switch (keyCode) {
            case KeyEventExtra.KEYCODE_MSTAR_RIGHT:
                mRight.setImageResource(R.drawable.conent_right_action);
                mPosition = (++mPosition % 3);
                mValue.setText(mBanlace[mPosition]);
                if (System.currentTimeMillis() - mLastClick > 200) {
                    setBalanceValue(mPosition);
                    mLastClick = System.currentTimeMillis();
                }
                return true;
            case KeyEventExtra.KEYCODE_MSTAR_LEFTD:
                mLeft.setImageResource(R.drawable.conent_left_action);
                if(mPosition == 0){
                    mPosition = mBanlace.length;
                }
                mPosition = (--mPosition % 3);
                mValue.setText(mBanlace[mPosition]);
                if (System.currentTimeMillis() - mLastClick > 200) {
                    setBalanceValue(mPosition);
                    mLastClick = System.currentTimeMillis();
                }
                return true;
            case KeyEventExtra.KEYCODE_MSTAR_MONO:
                dismiss();
                return true;
            default:
                mHandler.sendEmptyMessage(Constants.DISMISS);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEventExtra.KEYCODE_MSTAR_LEFTD:
                mHandler.sendEmptyMessage(Constants.LEFT);
                setBalanceValue(mPosition);
                return true;
            case KeyEventExtra.KEYCODE_MSTAR_RIGHT:
                mHandler.sendEmptyMessage(Constants.RIGHT);
                setBalanceValue(mPosition);
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private int getCurrentBalance() {
        int value = 0;
        value = TvAudioManager.getInstance().getBalance();
        return value;
    }

    private int getPosition(int value) {
        int position = 0;
        switch (value) {
            case 0:
                position = 0;
                break;
            case 50:
                position = 1;
                break;
            case 100:
                position = 2;
                break;
            default:
                break;
        }
        return position;
    }

    private void setBalanceValue(int position) {
        int value = 0;
        switch (position) {
            case 0:
                value = 0;
                break;
            case 1:
                value = 50;
                break;
            case 2:
                value = 100;
                break;
            default:
                break;
        }
        TvAudioManager.getInstance().setBalance(value);
        Log.d(TAG, "setBalanceValue = " + value);
    }
}
