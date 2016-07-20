
package com.eostek.sciflyui.thememanager.ui;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.sciflyui.thememanager.R;

/**
 * DeleteThemeDialogHolder.
 */
public class DeleteThemeDialogHolder {

    /**
     * mDialog.
     */
    protected DeleteThemeDialog mDialog = null;

    /**
     * delete.
     */
    protected Button delete = null;

    /**
     * deleteCancel.
     */
    protected Button deleteCancel = null;

    /**
     * thumnail.
     */
    protected ImageView thumnail = null;

    /**
     * themeName.
     */
    protected TextView themeName = null;

    /**
     * constructor.
     * 
     * @param dialog dialog
     */
    public DeleteThemeDialogHolder(DeleteThemeDialog dialog) {
        this.mDialog = dialog;
    }

    /**
     * find Views.
     */
    public final void findViews() {
        thumnail = (ImageView) mDialog.findViewById(R.id.themeImage_dialog);
        themeName = (TextView) mDialog.findViewById(R.id.themeName);
        delete = (Button) mDialog.findViewById(R.id.deleteThemeButton);
        deleteCancel = (Button) mDialog.findViewById(R.id.deleteThemeButton_cancel);
    }
}
