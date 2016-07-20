package com.bq.tv.task.ui.functionitem;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;

public class AllAppItem extends FunctionItem implements FunctionItem.OnClickBehavior {

    private final static String ALL_APP_PACKAGE_NAME_H901 = "eos.intent.action.ALL_ACTIVITY";
    
    public AllAppItem(Context context, Dialog dialog, int id, String intentStr) {
        super(context, dialog, id, intentStr);
        mOnClickBehavior = this;
    }
    
    @Override
    public void onClick() {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        String device = SystemProperties.get("ro.product.device");
        Log.v("AllAppItem","device = " + device);
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(device.equals("Leader") || device.equals("XShuaiUFO")){
                intent.setClassName("com.ts.launcher", "com.lejia.launcher0.activity.AppActivity");
                intent.putExtra("includeSystemApp",true);
            }else {
                intent.setAction(mIntentStr);
            }
            mContext.startActivity(intent);
            
        } catch (ActivityNotFoundException e) {
            intent = new Intent(ALL_APP_PACKAGE_NAME_H901);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);

        } finally {
        	mDialog.dismiss();
        }
    }
}
