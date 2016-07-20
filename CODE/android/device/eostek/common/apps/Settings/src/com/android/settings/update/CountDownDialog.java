package com.android.settings.update;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.settings.R;

/**
 * This clasee is the count down dialog.
 * @author king
 */
public class CountDownDialog {
    /**
     * the TAG for debug log.
     */
    private Context context;
    private long millisInFutrue;
    private long countDownInterval;
    private AlertDialog dialog;
    private TextView text;
    private int event = Constants.MSG_DIALOG_CANCEL;

    /**
     * show dialog.
     * @return if ok,show dialog
     */
    public boolean isShowing() {
        return dialog.isShowing();
    }

    public CountDownDialog(Context context, final Handler handler, final long millisInFutrue, final long countDownInterval) {
        this.context = context;
        this.millisInFutrue = millisInFutrue / 1000;
        this.countDownInterval = countDownInterval;

        Log.d(Constants.TAG, "CountDownDialog++++++++++++++++++++++++");
        AlertDialog.Builder builder = new AlertDialog.Builder(context,android.R.style.Theme_Translucent);
        dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
        dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                Log.d(Constants.TAG, "CountDownDialog->dismiss after "
                        + (millisInFutrue / 1000 - CountDownDialog.this.millisInFutrue)
                        + "seconds passed");
                handler.sendEmptyMessage(event);
            }
        });
        Window window = dialog.getWindow();
        window.setContentView(R.layout.counting_dialog);
        text = (TextView) window.findViewById(R.id.counting);
        text.setText(context.getString(R.string.update_count_down, this.millisInFutrue));
        mHandler.sendEmptyMessageDelayed(0, countDownInterval);

        Button updateButton = (Button) window.findViewById(R.id.reboot_now);
        updateButton.setFocusable(true);
        updateButton.setFocusableInTouchMode(true);
        updateButton.requestFocus();
        updateButton.requestFocusFromTouch();
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d(Constants.TAG, "CountDownDialog->User press OK button, install package now");
                event = Constants.MSG_DIALOG_OK;
                dialog.dismiss();
            }
        });
        Button cancelButton = (Button) window.findViewById(R.id.reboot_later);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d(Constants.TAG, "CountDownDialog->User press Cancel button, install package later");
                event = Constants.MSG_DIALOG_CANCEL;
                dialog.dismiss();
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            millisInFutrue--;
            text.setText(context.getString(R.string.update_count_down, millisInFutrue));
            if (millisInFutrue > 0 && dialog.isShowing()) {
                sendEmptyMessageDelayed(0, countDownInterval);
            }
            if (millisInFutrue == 0 && dialog.isShowing()) {
                Log.d(Constants.TAG, "CountDownDialog->30s passed, we need to install package!");
                event = Constants.MSG_DIALOG_OK;
                dialog.dismiss();
            }
        };
    };
}
