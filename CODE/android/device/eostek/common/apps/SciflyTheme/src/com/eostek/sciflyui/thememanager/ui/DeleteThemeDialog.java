
package com.eostek.sciflyui.thememanager.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.sciflyui.thememanager.R;
import com.eostek.sciflyui.thememanager.ThemeDisplayAct;

/**
 * DeleteThemeDialog.
 */
public class DeleteThemeDialog extends AlertDialog {

    ThemeDisplayAct activity;

    Context mContext;

    DeleteThemeDialogHolder holder;

    static final int DELETE_DIALOG_WIDTH = 800;

    static final int DELETE_DIALOG_HEIGHT = 520;

    /**
     * DeleteThemeDialog.
     * 
     * @param context context
     */
    public DeleteThemeDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_theme_dialog);

        holder = new DeleteThemeDialogHolder(this);
        holder.findViews();

        Window window = getWindow();
        window.setBackgroundDrawableResource(R.drawable.setting_tips_bg);
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = DELETE_DIALOG_WIDTH;
        p.height = DELETE_DIALOG_HEIGHT;
        window.setAttributes(p);

    }

    /**
     * @param listener listener
     */
    public final void setDeleteListener(View.OnClickListener listener) {
        if (listener != null && holder.delete != null) {
            holder.delete.setOnClickListener(listener);
        }
    }

    /**
     * @param listener listener
     */
    public final void setDeleteCancelListener(View.OnClickListener listener) {
        if (listener != null && holder.deleteCancel != null) {
            holder.deleteCancel.setOnClickListener(listener);
        }
    }

    /**
     * @param selectedPosition selectedPosition
     */
    public final void show(int selectedPosition) {
        this.show();
        activity = (ThemeDisplayAct) mContext;

        TextView themeName = (TextView) activity.getHolder().getGirdview().getChildAt(selectedPosition)
                .findViewById(R.id.item_textview);
        ImageView themeThumbnail = (ImageView) activity.getHolder().getGirdview().getChildAt(selectedPosition)
                .findViewById(R.id.theme_item);

        themeThumbnail.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(themeThumbnail.getDrawingCache());
        themeThumbnail.setDrawingCacheEnabled(false);

        holder.thumnail.setImageBitmap(bmp);
        holder.themeName.setText(themeName.getText());
    }

}
