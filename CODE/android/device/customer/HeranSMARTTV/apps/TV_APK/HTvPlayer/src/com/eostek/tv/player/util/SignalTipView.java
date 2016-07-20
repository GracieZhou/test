
package com.eostek.tv.player.util;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eostek.tv.player.PlayerActivity;
import com.eostek.tv.player.R;

public class SignalTipView extends RelativeLayout {
    private static final String TAG = "SignalTipView";

    private View view;

    private LinearLayout signalTip_layout;

    private TextView signalTipsTxt;

    private static boolean hasShow = false;

    private static final int MOVETIPS = 0x01;

    private static final int DELAYMOVETIME = 5000;

    private int screenWidth = 0;

    private int screenHeight = 0;

    private Activity myContext;

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

    }

    public SignalTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SignalTipView(Context context) {
        super(context);
        this.myContext = (Activity) context;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        view = LayoutInflater.from(context).inflate(R.layout.eos_signal_tips, this, true);
        signalTip_layout = (LinearLayout) view.findViewById(R.id.signaltip);
        signalTipsTxt = (TextView) view.findViewById(R.id.signaltip_txt);
    }

    public boolean isShow() {
        return hasShow;
    }

    public void setText(final String tip) {
        if (signalTipsTxt.getText().toString().equals(tip)) {
            return;
        }
        mMoveHandler.removeMessages(MOVETIPS);
        mMoveHandler.sendEmptyMessageDelayed(MOVETIPS, DELAYMOVETIME);
        Log.e(TAG, "show signal tips." + tip);
        myContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                signalTip_layout.setVisibility(VISIBLE);
                hasShow = true;
                signalTipsTxt.setText(tip);
            }
        });
    }

    public void dismiss() {
        if (getText().equals("") || getText().equals(PlayerActivity.UNSUPPORT_TIP)) {
            return;
        }
        mMoveHandler.removeMessages(MOVETIPS);
        Log.e(TAG, "dismiss signal tips." + getText());
        myContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) signalTipsTxt
                        .getLayoutParams();
                params.setMargins(screenWidth / 2, screenHeight / 2, 0, 0);
                signalTipsTxt.setLayoutParams(params);
                signalTip_layout.setVisibility(GONE);
                hasShow = false;
                signalTipsTxt.setText("");
            }
        });

    }

    private void moveTips() {
        mMoveHandler.removeMessages(MOVETIPS);
        mMoveHandler.sendEmptyMessageDelayed(MOVETIPS, DELAYMOVETIME);
        myContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) signalTipsTxt
                        .getLayoutParams();
                Random random = new Random(SystemClock.uptimeMillis());
                int width = random.nextInt(screenWidth - signalTipsTxt.getWidth() - 100);
                int height = random.nextInt(screenHeight - signalTipsTxt.getHeight() - 100);
                params.setMargins(width, height, 0, 0);
                signalTipsTxt.setLayoutParams(params);
            }
        });

    }

    public String getText() {
        return signalTipsTxt.getText().toString();
    }
}
