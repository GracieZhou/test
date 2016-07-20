
package com.eostek.scifly.messagecenter.ui.dialog;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.eostek.scifly.messagecenter.MainActivity;
import com.eostek.scifly.messagecenter.R;
import com.eostek.scifly.messagecenter.util.Util;

/**
 * Dialog of delete message.
 */
public class DeleteMessageDialog extends AlertDialog {

    private MainActivity mContext;

    private DeleteMessageDialogHolder holder;

    /**
     * Constructor.
     * 
     * @param context
     */
    public DeleteMessageDialog(MainActivity context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_message_dialog);

        holder = new DeleteMessageDialogHolder(this);
        holder.findViews();

        Window window = getWindow();
        WindowManager.LayoutParams p = window.getAttributes();
        p.height = Util.getDiemnsionPixelSize(mContext, R.dimen.DELETE_DIALOG_HEIGHT);
        p.width = Util.getDiemnsionPixelSize(mContext, R.dimen.DELETE_DIALOG_WIDTH);
        window.setAttributes(p);

    }

    /**
     * set Listener of delete message.
     * 
     * @param listener
     */
    public void setDeleteListener(View.OnClickListener listener) {
        if (listener != null && holder.delete != null) {
            holder.delete.setOnClickListener(listener);
        }
    }

    /**
     * set Listener of cancel delete message dialog.
     * 
     * @param listener
     */
    public void setDeleteCancelListener(View.OnClickListener listener) {
        if (listener != null && holder.deleteCancel != null) {
            holder.deleteCancel.setOnClickListener(listener);
        }
    }

    /**
     * show tips.
     * 
     * @param userName
     */
    public void show(String userName) {
        this.show();
        TextView deleteTip = (TextView) findViewById(R.id.delete_dialog_content);

        if (deleteTip == null) {
            return;
        }
        if ("".equals(userName) || userName == null) {
            userName = mContext.getResources().getString(R.string.current_user);
        }
        deleteTip.setText(deleteTip.getText().toString().replace("XXX", userName));

    }
}
