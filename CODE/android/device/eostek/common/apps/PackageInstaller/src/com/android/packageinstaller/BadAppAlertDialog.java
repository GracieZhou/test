
package com.android.packageinstaller;

import java.util.Timer;
import java.util.TimerTask;

import scifly.app.common.Commons;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * @author frankzhang
 */
public class BadAppAlertDialog {

    private Context mContext;

    private static final Commons.CommonLog sLogger = new Commons.CommonLog(BadAppAlertDialog.class.getSimpleName());

    private AlertDialog mAlertDlg;

    private Button mInstallBtn;

    private Timer mCountDownTimer;

    private long mCountDown;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            mInstallBtn.setText(mContext.getString(R.string.install_btn_countdown, mCountDown));

        }
    };

    private int mMsgWhat = Commons.MSG_DLG_BTN_CANCEL;

    public BadAppAlertDialog(Context context, final Handler handler, final long countdownTime) {
        sLogger.d("Constructor");
        mContext = context;
        mCountDown = countdownTime;
        mCountDownTimer = new Timer();

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Translucent);
        mAlertDlg = builder.create();
        mAlertDlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mAlertDlg.show();
        mAlertDlg.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                sLogger.d("onDismiss : " + mMsgWhat);
                mCountDownTimer.cancel();
                if (null != handler) {
                    handler.sendEmptyMessage(mMsgWhat);
                }

            }
        });
        Window window = mAlertDlg.getWindow();
        window.setContentView(R.layout.security_alert_dlg);
        mInstallBtn = (Button) window.findViewById(R.id.bt_install);
        mInstallBtn.setText(mContext.getString(R.string.install_btn_countdown, countdownTime));

        mInstallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mMsgWhat = Commons.MSG_DLG_BTN_OK;
                sLogger.d("onClick : " + mMsgWhat);
                mAlertDlg.dismiss();
            }
        });

        Button cancelButton = (Button) window.findViewById(R.id.bt_cancel);
        cancelButton.setFocusable(true);
        cancelButton.setFocusableInTouchMode(true);
        cancelButton.requestFocus();
        cancelButton.requestFocusFromTouch();
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mMsgWhat = Commons.MSG_DLG_BTN_CANCEL;
                sLogger.d("onClick : " + mMsgWhat);
                mAlertDlg.dismiss();
            }
        });

        mCountDownTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                mCountDown--;
                mHandler.obtainMessage().sendToTarget();
                sLogger.d("run:: left " + mCountDown + " s");
                if (0 == mCountDown && mAlertDlg.isShowing()) {
                    mMsgWhat = Commons.MSG_DLG_BTN_OK;
                    mAlertDlg.dismiss();
                }
            }
        }, 1000, 1000);
    }

    public boolean isShowing() {
        sLogger.d("isShowing");
        return mAlertDlg.isShowing();
    }

    public void dismiss() {
        sLogger.d("dismiss");
        mAlertDlg.dismiss();
    }
}
