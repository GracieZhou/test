package com.bq.tv.task.ui.functionitem;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class VoiceControllerItem extends FunctionItem implements FunctionItem.OnClickBehavior {
    
    private static String TAG = "VoiceControllerItem";
    
    public VoiceControllerItem(Context context, Dialog dialog, int id, String intentStr) {
        super(context, dialog, id, intentStr);
        mOnClickBehavior=this;
    }

    @Override
    public void onClick() {
        // TODO Auto-generated method stub
        Intent intent=new Intent(mIntentStr);
        //intent.setPackage("com.eostek.sciflyui.voicecontroller");
        Log.d(TAG,"Launch voiceController, intent===>"+intent);
        mContext.startService(intent);
        mDialog.dismiss();
    }
}
