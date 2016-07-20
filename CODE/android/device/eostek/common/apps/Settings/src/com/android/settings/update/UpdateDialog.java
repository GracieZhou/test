package com.android.settings.update;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.settings.R;

/**
 * This class provide the update dialog.
 * @author king
 *
 */
public class UpdateDialog {
    private AlertDialog mDialog;
    private int event = Constants.MSG_CPE_DIALOG_CANCEL;

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public UpdateDialog(Context context, final Handler handler, final DisplayInfo displayInfo) {
        Log.d(Constants.TAG, "UpdateDialog++++++++++++++++++++++++");
        AlertDialog.Builder builder = new AlertDialog.Builder(context,android.R.style.Theme_Translucent);
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
        window.setContentView(R.layout.cmd_dialog);
        TextView messageTitle = (TextView) window.findViewById(R.id.tv_message_title);
        TextView messageTime = (TextView) window.findViewById(R.id.tv_message_time);
        TextView messageContent = (TextView) window.findViewById(R.id.tv_message_content);
        Button closeButton = (Button) window.findViewById(R.id.bt_close);
        Button updateButton = (Button) window.findViewById(R.id.bt_update);

        messageTitle.setText(context.getString(R.string.new_version) + displayInfo.getVersion());
        messageTime.setText(displayInfo.getTime());
        messageContent.setText(displayInfo.getDescription());
        updateButton.setFocusable(true);
        updateButton.setFocusableInTouchMode(true);
        updateButton.requestFocus();
        updateButton.requestFocusFromTouch();

        updateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d(Constants.TAG, "UpdateDialog->updateButton");
                event = Constants.MSG_CPE_DIALOG_OK;
                mDialog.dismiss();
            }
        });
        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d(Constants.TAG, "UpdateDialog->closeButton");
                event = Constants.MSG_CPE_DIALOG_CANCEL;
                mDialog.dismiss();
            }
        });
    }
}
