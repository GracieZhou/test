
package com.eostek.sciflyui.thememanager.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.sciflyui.thememanager.R;
import com.eostek.sciflyui.thememanager.ThemeDisplayAct;
import com.eostek.sciflyui.thememanager.cache.ThemeDataCache;
import com.eostek.sciflyui.thememanager.task.ThemeModel;
import com.eostek.sciflyui.thememanager.util.BitmapUtils;
import com.eostek.sciflyui.thememanager.util.Constant;
import com.eostek.sciflyui.thememanager.util.ThemeManagerUtils;

/**
 * adapter about item of theme.
 * 
 * @author Admin
 */
public class GridViewBaseAdapter extends BaseAdapter {
    /**
     * whether update all.
     */
    private boolean updateAll = true;

    /**
     * @return is Update All
     */
    public final boolean isUpdateAll() {
        return updateAll;
    }

    /**
     * @param mupdateAll set updateAll
     */
    public final void setUpdateAll(boolean mupdateAll) {
        this.updateAll = mupdateAll;
    }

    private List<ThemeModel> mThemes;

    private LayoutInflater mInflater;

    private ThemeDisplayAct mThemeDisplayAct;

    /**
     * TAG STRING.
     */
    protected static final String TAG = "GridViewBaseAdapter";

    private int updateSelected = 0;

    private ThemeDataCache mImageLoader;

    /**
     * constructor.
     * 
     * @param context an application environment.
     * @param themes values.
     */
    public GridViewBaseAdapter(ThemeDisplayAct context, List<ThemeModel> themes) {
        updateAll = true; // flag of updating all items or single item
        this.mThemes = themes;
        this.mThemeDisplayAct = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoader = ThemeDataCache.getCacheLoader(mThemeDisplayAct);
    }

    /**
     * @return updateSelected
     */
    public final int getUpdateSelected() {
        return updateSelected;
    }

    /**
     * @param mUpdateSelected updateSelected
     */
    public final void setUpdateSelected(int mUpdateSelected) {
        this.updateSelected = mUpdateSelected;
    }

    @Override
    public final int getCount() {
        return mThemes.size();
    }

    @Override
    public final Object getItem(int position) {
        return mThemes.get(position);
    }

    @Override
    public final long getItemId(int position) {
        return position;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        // if (convertView != null) {
        // return convertView;
        // }

        if (!updateAll && position != updateSelected) {
            return convertView;
        }

        // find views begin:
        convertView = mInflater.inflate(R.layout.theme_item, null);
        TextView textView = (TextView) convertView.findViewById(R.id.item_textview);

        final ImageView img = (ImageView) convertView.findViewById(R.id.theme_item);

        ImageView selected = (ImageView) convertView.findViewById(R.id.selected);
        ImageView download = (ImageView) convertView.findViewById(R.id.download);
        // TextView percent = (TextView) convertView.findViewById(R.id.percent);
        // ProgressBar progressBar = (ProgressBar)
        // convertView.findViewById(R.id.progress);

        // find views end.

        final ThemeModel model = mThemes.get(position);
        Log.i(TAG, "" + position + " type =" + model.mType);

        String str = model.mTitle;

        if (model.mType == ThemeModel.TYPE.LOCAL || model.mType == ThemeModel.TYPE.DEFAULT) {
            if (model.equals(mThemeDisplayAct.mCurrentThemeModel)) {
                if (parent.getChildCount() == position) {
                    Log.i(TAG, "selected = " + position);
                    selected.setVisibility(View.VISIBLE);
                    mThemeDisplayAct.mCurrentView = convertView;

                }

            }
        } else if (model.mType == ThemeModel.TYPE.ONLINE) {

            download.setVisibility(View.VISIBLE);
        }
        textView.setText(str);

        loadImage(model, img);

        return convertView;
    }

    private void loadImage(ThemeModel model, ImageView img) {

        String fileName = model.mTitle + model.mThemeVersion;

        String imgPath = Constant.IMAGE_CACHE + fileName + ".png";

        Uri uri = Uri.parse(imgPath);
        String uriString = "File:///" + uri.toString();
        if (model.mType == ThemeModel.TYPE.ONLINE && model.mThumbUrl != null) {
            uriString = model.mThumbUrl;
            mImageLoader.loadImage(uriString, img, R.drawable.default_img, R.drawable.default_img,
                    R.drawable.default_img, imgPath);
        } else if (model.mType == ThemeModel.TYPE.LOCAL) {
            mImageLoader.loadImage(uriString, img, R.drawable.default_img, R.drawable.default_img,
                    R.drawable.default_img, imgPath);
        } else if (model.mType == ThemeModel.TYPE.DEFAULT) {

            File file = new File(imgPath);

            if (file.exists()) {
                mImageLoader.loadImage(uriString, img, R.drawable.default_img, R.drawable.default_img,
                        R.drawable.default_img);
                return;
            }

            Drawable drawable;
            Bitmap bitmap = null;
            try {
                drawable = ThemeManagerUtils.unzipThumbnail(mThemeDisplayAct.getApplicationContext(), model.mLocalUrl);

                if (drawable != null) {
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                    if (bitmap != null) {
                        img.setImageBitmap(bitmap);
                    } else {
                        img.setImageResource(R.drawable.default_img);
                    }
                    BitmapUtils.saveBitmap(bitmap, imgPath);
                }
            } catch (IOException e) {
                img.setImageResource(R.drawable.default_img);
            }

        }

        Log.i(TAG, "create bitmap uri: " + uriString);

    }

    /**
     * @param themes Themes to set
     */
    public final void setmThemes(List<ThemeModel> themes) {
        if (themes != null) {
            this.mThemes.clear();
            this.mThemes.addAll(themes);
        } else {
            this.mThemes = themes;
        }
    }

    /**
     * obtain values of theme.
     * 
     * @return set of theme.
     */

    public final List<ThemeModel> getmThemes() {
        return mThemes;
    }

}
