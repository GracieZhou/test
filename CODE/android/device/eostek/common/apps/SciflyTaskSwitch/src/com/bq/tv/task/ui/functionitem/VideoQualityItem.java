package com.bq.tv.task.ui.functionitem;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

public class VideoQualityItem extends FunctionItem implements FunctionItem.OnClickBehavior {

    private static String TAG = "VideoItem";

    public VideoQualityItem(Context context, Dialog dialog, int id, String intentStr) {
        super(context, dialog, id, intentStr);
        mOnClickBehavior = this;
    }
    
    @Override
    protected void switchSource() {
        // TODO Auto-generated method stub
        Log.d(TAG,"Video Quality was clicked, do not switch source!");
    }
    
    @Override
    public void onClick() {
        // TODO Auto-generated method stub
        startActivityByAction();
        mDialog.dismiss();
    }
}
