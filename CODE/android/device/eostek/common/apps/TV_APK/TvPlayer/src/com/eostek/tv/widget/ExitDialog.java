
package com.eostek.tv.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;

import com.eostek.tv.PlayerActivity;
import com.eostek.tv.R;

public class ExitDialog extends AlertDialog {
    private Context mContext;

    private FocusView focusView;

    private static final int TODIMISS = 0x05;

    private static final int TODIMISSDELAYTIME = 10 * 1000;

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (TODIMISS == msg.what) {
                dismiss();
            }
        }
    };

    public ExitDialog(Context context) {
        super(context, R.style.dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tipdialog);
        focusView = (FocusView) findViewById(R.id.focus_selector);
        setListener();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        myHandler.removeMessages(TODIMISS);
        myHandler.sendEmptyMessageDelayed(TODIMISS, TODIMISSDELAYTIME);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        myHandler.removeMessages(TODIMISS);
        super.onBackPressed();
    }

    private void setListener() {
        myHandler.sendEmptyMessageDelayed(TODIMISS, TODIMISSDELAYTIME);
        findViewById(R.id.sure_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                ((PlayerActivity) mContext).finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        findViewById(R.id.cancle_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myHandler.removeMessages(TODIMISS);
                dismiss();
            }
        });

        FocusChangeListener focusChangeListener = new FocusChangeListener();
        findViewById(R.id.cancle_btn).setOnFocusChangeListener(focusChangeListener);
        findViewById(R.id.sure_btn).setOnFocusChangeListener(focusChangeListener);
    }

    class FocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                Rect rect = new Rect();
                v.getGlobalVisibleRect(rect);
                focusView.startAnimation(v);
            }
        }
    }
}
