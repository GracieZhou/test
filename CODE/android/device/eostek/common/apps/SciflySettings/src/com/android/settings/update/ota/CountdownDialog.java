
package com.android.settings.update.ota;

import com.android.settings.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CountdownDialog {

    private Context mContext;

    private AlertDialog mDialog;

    private TextView mTextTv;

    private int event = Constants.MSG_REBOOT_LATER;

    private int mCountdownTime = 30;

    private Handler mHandler = new Handler();

    private Runnable mCountDownRunnable = new Runnable() {

        @Override
        public void run() {
            mTextTv.setText(mContext.getString(R.string.update_count_down, mCountdownTime));

            if (mCountdownTime > 0) {
                --mCountdownTime;
                mHandler.postDelayed(this, 1000);
            } else if (isShowing()) {
                mDialog.dismiss();
                event = Constants.MSG_REBOOT_NOW;
            }
        }
    };

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    CountdownDialog(Context context, final Handler handler) {
        mContext = context;

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Translucent);
        mDialog = builder.create();
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDialog.show();
        mDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                handler.sendEmptyMessage(event);
            }
        });
        Window window = mDialog.getWindow();
        window.setContentView(R.layout.countdown_dialog);
        mTextTv = (TextView) window.findViewById(R.id.counting);
        mTextTv.setText(context.getString(R.string.update_count_down, mCountdownTime));

        Button updateButton = (Button) window.findViewById(R.id.reboot_now);
        updateButton.setFocusable(true);
        updateButton.setFocusableInTouchMode(true);
        updateButton.requestFocus();
        updateButton.requestFocusFromTouch();
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                event = Constants.MSG_REBOOT_NOW;
                mDialog.dismiss();
            }
        });

        Button cancelButton = (Button) window.findViewById(R.id.reboot_later);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                event = Constants.MSG_REBOOT_LATER;
                mDialog.dismiss();
            }
        });

        mHandler.post(mCountDownRunnable);
    }
}
