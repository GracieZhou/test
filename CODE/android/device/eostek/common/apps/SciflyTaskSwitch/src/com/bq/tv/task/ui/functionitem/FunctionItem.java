
package com.bq.tv.task.ui.functionitem;

import com.eos.notificationcenter.utils.Util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class FunctionItem {

    protected LinearLayout mItem;

    protected String mIntentStr;

    protected Context mContext;
    protected Dialog mDialog; 

    protected PackageManager mPackageManager;

    protected OnClickBehavior mOnClickBehavior;

    public FunctionItem(Context context, Dialog dialog, int id, String intentStr) {
        this.mContext = context;
        this.mDialog = dialog;
        this.mIntentStr = intentStr;
        this.mItem = (LinearLayout) mDialog.findViewById(id);

        if (mPackageManager == null) {
            mPackageManager = mContext.getPackageManager();
        }
    }

    public void setOnClickListener() {
        mItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSource();
                if (mOnClickBehavior != null) {
                    mOnClickBehavior.onClick();
                }
            }
        });
    }
    
    protected void switchSource() {
        new Thread(new Runnable() {
            public void run() {
                Util.switchSource(mContext);
            }
        }).start();
    }

    public interface OnClickBehavior {
        public abstract void onClick();
    }

    protected void startActivityByPackageName() {
        Intent intent = mPackageManager.getLaunchIntentForPackage(mIntentStr);
        if (intent != null) {
            mContext.startActivity(intent);
        }
    }

    protected void startActivityByAction() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(mIntentStr);
        mContext.startActivity(intent);
    }
}
