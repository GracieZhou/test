
package com.android.settings.update.ota;

import com.android.settings.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.TextView;

public final class NewVersionDialog {

    public NewVersionDialog() {
    }

    private AlertDialog mDialog;

    private int event = Constants.MSG_CANCEL;

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public NewVersionDialog(Context context, final Handler handler, final PackageInfo pkgInfo) {
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
        window.setContentView(R.layout.new_version_dialog);
        TextView messageTitle = (TextView) window.findViewById(R.id.tv_message_title);
        TextView messageTime = (TextView) window.findViewById(R.id.tv_message_time);
        TextView messageContent = (TextView) window.findViewById(R.id.tv_message_content);
        messageContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        Button closeButton = (Button) window.findViewById(R.id.bt_close);
        Button updateButton = (Button) window.findViewById(R.id.bt_update);

        messageTitle.setText(context.getString(R.string.new_version) + pkgInfo.getFacVer());
        messageTime.setText(Utils.getDateAndTime(pkgInfo.getPublishTime()));
        messageContent.setText(pkgInfo.getUds());
        updateButton.setFocusable(true);
        updateButton.setFocusableInTouchMode(true);
        updateButton.requestFocus();
        updateButton.requestFocusFromTouch();

        updateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                event = Constants.MSG_UPGRADE;
                mDialog.dismiss();
            }
        });
        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                event = Constants.MSG_CANCEL;
                mDialog.dismiss();
            }
        });
    }

}
