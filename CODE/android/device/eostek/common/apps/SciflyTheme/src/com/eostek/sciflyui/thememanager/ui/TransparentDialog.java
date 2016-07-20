
package com.eostek.sciflyui.thememanager.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.eostek.sciflyui.thememanager.R;

/**
 * TransparentDialog.
 */
public class TransparentDialog extends AlertDialog {

    /**
     * constructor.
     * 
     * @param context context
     */
    public TransparentDialog(Context context) {
        super(context, android.R.style.Theme_Translucent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tran_dialog);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void onBackPressed() {
    }
}
