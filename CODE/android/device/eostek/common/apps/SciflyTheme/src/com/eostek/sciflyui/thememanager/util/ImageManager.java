
package com.eostek.sciflyui.thememanager.util;

import android.graphics.drawable.Drawable;

import com.eostek.sciflyui.thememanager.task.ThemeModel;

/**
 * image manager.
 */
public class ImageManager {

    /**
     * @param model model
     * @return Drawable
     */
    public static Drawable getView(ThemeModel model) {
        String mFileName = model.mTitle + model.mThemeVersion;
        String mImgPath = Constant.IMAGE_CACHE + mFileName + ".png";
        return Drawable.createFromPath(mImgPath);
    }

}
