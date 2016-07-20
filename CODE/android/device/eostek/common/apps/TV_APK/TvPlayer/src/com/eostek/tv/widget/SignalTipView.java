
package com.eostek.tv.widget;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eostek.tv.R;

public class SignalTipView extends RelativeLayout {
    private View mView;

    private LinearLayout signalTip_layout;

    private TextView mSignalTipsTxt;

    private static boolean hasShow = false;

    private static final int MOVETIPS = 0x01;

    private static final int DELAYMOVETIME = 5000;

    private int screenWidth = 0;

    private int screenHeight = 0;

    private Activity mContext;

    @SuppressLint("HandlerLeak")
    private Handler mMoveHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MOVETIPS:
                    moveTips();
                    break;
                default:
                    break;
            }
        }
    };

    public SignalTipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SignalTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SignalTipView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.mContext = (Activity) context;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        mView = LayoutInflater.from(context).inflate(R.layout.signal_tips, this, true);
        signalTip_layout = (LinearLayout) mView.findViewById(R.id.signaltip);
        mSignalTipsTxt = (TextView) mView.findViewById(R.id.signaltip_txt);
    }

    public boolean isShow() {
        return hasShow;
    }

    public void setText(final String tip) {
        if (mSignalTipsTxt.getText().toString().equals(tip)) {
            return;
        }
        mMoveHandler.removeMessages(MOVETIPS);
        mMoveHandler.sendEmptyMessageDelayed(MOVETIPS, DELAYMOVETIME);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                signalTip_layout.setVisibility(VISIBLE);
                hasShow = true;
                mSignalTipsTxt.setText(tip);
            }
        });
    }

    public void dismiss() {
        if (getText().equals("")) {
            return;
        }
        mMoveHandler.removeMessages(MOVETIPS);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) mSignalTipsTxt
                        .getLayoutParams();
                params.setMargins(screenWidth / 2, screenHeight / 2, 0, 0);
                mSignalTipsTxt.setLayoutParams(params);
                signalTip_layout.setVisibility(GONE);
                hasShow = false;
                mSignalTipsTxt.setText("");
            }
        });

    }

    private void moveTips() {
        mMoveHandler.removeMessages(MOVETIPS);
        mMoveHandler.sendEmptyMessageDelayed(MOVETIPS, DELAYMOVETIME);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) mSignalTipsTxt
                        .getLayoutParams();
                Random random = new Random(SystemClock.uptimeMillis());
                int width = random.nextInt(screenWidth - mSignalTipsTxt.getWidth() - 100);
                int height = random.nextInt(screenHeight - mSignalTipsTxt.getHeight() - 100);
                params.setMargins(width, height, 0, 0);
                mSignalTipsTxt.setLayoutParams(params);
            }
        });

    }

    public String getText() {
        return mSignalTipsTxt.getText().toString();
    }
}
