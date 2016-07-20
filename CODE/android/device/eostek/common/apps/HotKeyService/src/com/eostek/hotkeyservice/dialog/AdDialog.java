
package com.eostek.hotkeyservice.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;

import com.eostek.hotkeyservice.HotKeyService;
import com.eostek.hotkeyservice.R;

public class AdDialog extends Dialog {

    HotKeyService mContext;

    public AdDialog(HotKeyService context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("debug", "----->onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_ad_dialog);
        getWindow().setGravity(Gravity.TOP);

    }
}
