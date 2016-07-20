
package com.eostek.scifly.messagecenter.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.eostek.scifly.messagecenter.R;

/**
 * the dialog which is transparent
 */
public class TransparentDialog extends AlertDialog {

    /**
     * Constructor.
     * 
     * @param context
     */
    public TransparentDialog(Context context) {
        super(context);
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
