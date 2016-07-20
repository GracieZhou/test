
package com.eostek.history.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.eostek.history.R;

/**
 * Class of More items.
 */
public class MoreItem extends HistoryItem {

    /**
     * Constructor of MoreItem.
     * 
     * @param name
     */
    public MoreItem(String name) {
        super(null, null, null, null, name, "");
    }

    @Override
    public Drawable loadDrawable(Context context) {

        Drawable drawable = context.getResources().getDrawable(R.drawable.more);

        return drawable;
    }

    @Override
    public Bitmap loadBitmap(Context context, int w, int h) {
        Drawable drawable = context.getResources().getDrawable(R.drawable.more);

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
