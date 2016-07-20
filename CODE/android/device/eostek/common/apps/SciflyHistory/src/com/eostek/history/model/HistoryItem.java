
package com.eostek.history.model;

import scifly.provider.SciflyStore;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.bq.tv.traxex.util.BitmapUtils;
import com.eostek.history.R;

/**
 * Class of history item.
 */
public class HistoryItem {
    private static final String TAG = "HistoryItem"; 

    protected static final String schema_prix = "history://"; 

    private Uri mUri;

    private String mCategoryName;

    private String mPackageName;

    private String mClassName;

    private String mData;

    private String mName;

    private String mTimeLabel;

    private int mCategory;

    private Bitmap smallImage = null;

    // private static final int CCTV[] = { R.drawable.cctv_1, R.drawable.cctv_2,
    // R.drawable.cctv_3, R.drawable.cctv_4, R.drawable.cctv_5,
    // R.drawable.cctv_6, R.drawable.cctv_7, R.drawable.cctv_8,
    // R.drawable.cctv_9 };
    //
    // private static final int VIDEO[] = { R.drawable.video_1,
    // R.drawable.video_2,
    // R.drawable.video_3, R.drawable.video_4, R.drawable.video_5,
    // R.drawable.video_6,
    // R.drawable.video_7, R.drawable.video_8, R.drawable.video_9,
    // R.drawable.video_10,
    // R.drawable.video_11, R.drawable.video_12, R.drawable.video_13,
    // R.drawable.video_14,
    // R.drawable.video_15 };

    /**
     * Constructor of HistoryItem.
     * @param categoryName
     * @param packageName
     * @param className
     * @param data
     * @param displayName
     * @param timeLabel
     */
    public HistoryItem(String categoryName, String packageName, String className, String data, String displayName,
            String timeLabel) {
        mUri = Uri.parse(schema_prix + packageName + "/" + timeLabel);
        mCategoryName = categoryName;
        mPackageName = packageName;
        mClassName = className;
        mData = data;
        mName = displayName;
        mTimeLabel = timeLabel;
    }

    /**
     * Get uri.
     * @return
     */
    public Uri getUri() {
        return mUri;
    }

    /**
     * Get category name.
     * @return
     */
    public String getCategoryName() {
        return mCategoryName;
    }

    /**
     * Get package name.
     * @return
     */
    public String getPackageName() {
        return mPackageName;
    }

    /**
     * Get class name.
     * @return
     */
    public String getClassName() {
        return mClassName;
    }

    /**
     * Get data.
     * @return
     */
    public String getData() {
        return mData;
    }

    /**
     * Set name.
     * @param name
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Get name.
     * @return
     */
    public String getName() {
        return mName;
    }

    /**
     * Get time label.
     * @return
     */
    public String getTimeLabel() {
        return mTimeLabel;
    }

    /**
     * Get category.
     * @return
     */
    public int getCategory() {
        return mCategory;
    }

    /**
     * Set category.
     * @param category
     */
    public void setCategory(int category) {
        mCategory = category;
    }

    /**
     * Get small image.
     * @return
     */
    public Bitmap getSmallImage() {
        return smallImage;
    }

    /**
     * Set small image.
     * @param smallImage
     */
    public void setSmallImage(Bitmap smallImage) {
        this.smallImage = smallImage;
    }

    /**
     * Load drawable.
     * @param context
     * @return
     */
    public Drawable loadDrawable(Context context) {
        Resources resource = context.getResources();
        Drawable drawable = resource.getDrawable(R.drawable.one_pixel);

        if (resource.getString(R.string.apps).equals(mCategoryName)) {
            PackageManager pm = context.getPackageManager();
            try {
                drawable = pm.getActivityIcon(new ComponentName(mPackageName, mClassName));
            } catch (NameNotFoundException e) {
                e.printStackTrace();
                drawable = context.getResources().getDrawable(R.drawable.ic_default);
            }
        } else if (resource.getString(R.string.chrome).equals(mCategoryName)) {
            drawable = context.getResources().getDrawable(R.drawable.ic_chromium);
        }

        return drawable;
    }

    /**
     * Load bitmap.
     * @param context
     * @param w
     * @param h
     * @return
     */
    public Bitmap loadBitmap(Context context, int w, int h) {

        Resources resource = context.getResources();
        Drawable drawable = resource.getDrawable(R.drawable.one_pixel);
        if (mCategoryName.equals(resource.getString(R.string.apps))) {
            PackageManager pm = context.getPackageManager();
            try {
                drawable = pm.getActivityIcon(new ComponentName(mPackageName, mClassName));
            } catch (NameNotFoundException e) {
                e.printStackTrace();
                drawable = context.getResources().getDrawable(R.drawable.ic_default);
            }

            return BitmapUtils.getWantedBitmap(((BitmapDrawable) drawable).getBitmap(), w, h);
        } else if (mCategoryName.equals(resource.getString(R.string.chrome))) {
            if (smallImage == null) {
                drawable = context.getResources().getDrawable(R.drawable.ic_chromium);
                return BitmapUtils.getWantedBitmap(((BitmapDrawable) drawable).getBitmap(), w, h);
            }

            return BitmapUtils.getWantedBitmap(smallImage, w, h);
        } else if (mCategoryName.equals(resource.getString(R.string.media_browser))) {
            if (mCategory == SciflyStore.Footprints.CATEGORY_MEDIA_VOD) {
                if (smallImage == null) {
                    Bitmap b = null;
                    drawable = resource.getDrawable(R.drawable.video);
                    b = ((BitmapDrawable) drawable).getBitmap();
                    return BitmapUtils.getWantedBitmap(b, w, h);
                }

                return BitmapUtils.getWantedBitmap(smallImage, w, h);
            } else if (mCategory == SciflyStore.Footprints.CATEGORY_MEDIA_VIDEO) {
                // File file = new File(mData);
                // String thumbnailPath = file.getParent() + "/." +
                // file.getName();
                // Log.i(TAG, "thumbnail path: " + thumbnailPath);
                // File thumbnailFile = new File(thumbnailPath);
                //
                // Bitmap b = null;
                // if (thumbnailFile.exists()) {
                // b = BitmapUtils.getImageBitmap(thumbnailPath, w, h);
                // } else {
                // if (file.exists()) {
                // b = BitmapUtils.getVideoBitmap(mData, w, h);
                // BitmapUtils.saveBitmap(b, thumbnailPath);
                // }
                // }
                //
                // if (b == null) {
                // drawable = resource.getDrawable(R.drawable.video);
                // b = ((BitmapDrawable) drawable).getBitmap();
                // }
                //
                // return BitmapUtils.getWantedBitmap(b, w, h);
                if (smallImage == null) {
                    Bitmap b = null;
                    drawable = resource.getDrawable(R.drawable.video);
                    b = ((BitmapDrawable) drawable).getBitmap();

                    return BitmapUtils.getWantedBitmap(b, w, h);
                }

                return BitmapUtils.getWantedBitmap(smallImage, w, h);
            }

        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Show labe. This function returns TRUE ALWAYS!
     * @return true.
     */
    public boolean showLabel() {
        return true;
    }

    @Override
    public String toString() {
        return "HistoryItem [mUri=" + mUri + ", mCategoryName=" + mCategoryName + ", mPackageName=" + mPackageName
                + ", mClassName=" + mClassName + ", mData=" + mData + ", mName=" + mName + ", mTimeLabel=" + mTimeLabel
                + ", CATEGORY=" + mCategory + ", smallImage=" + smallImage + "]";
    }

    /**
     * Destroy.
     * WARNIND:This function is EMPTY NOW!
     */
    public void destroy() {

    }
}
