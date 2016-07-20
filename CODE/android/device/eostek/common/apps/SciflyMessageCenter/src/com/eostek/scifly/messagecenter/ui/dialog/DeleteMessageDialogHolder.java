
package com.eostek.scifly.messagecenter.ui.dialog;

import com.eostek.scifly.messagecenter.R;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * the holder for the dialog which is to be showing message text.
 */
public class DeleteMessageDialogHolder {

    private DeleteMessageDialog mDialog = null;

    /** delete button. */
    public Button delete = null;

    /** cancel button. */
    public Button deleteCancel = null;

    /** thumnail view. */
    public ImageView thumnail = null;

    /** theme name. */
    public TextView themeName = null;

    /**
     * Constructor.
     * 
     * @param dialog
     */
    public DeleteMessageDialogHolder(DeleteMessageDialog dialog) {
        this.mDialog = dialog;
    }

    /**
     * init views.
     */
    public void findViews() {
        // thumnail = (ImageView) mDialog.findViewById(R.id.themeImage_dialog);
        // themeName = (TextView) mDialog.findViewById(R.id.themeName);
        delete = (Button) mDialog.findViewById(R.id.deleteMsgButton);
        deleteCancel = (Button) mDialog.findViewById(R.id.deleteMsgButton_cancel);
    }

}
