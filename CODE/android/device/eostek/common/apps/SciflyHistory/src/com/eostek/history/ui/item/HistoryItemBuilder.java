
package com.eostek.history.ui.item;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Footprints;
import scifly.provider.metadata.Footprint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Browser;
import android.util.Log;

import com.bq.tv.traxex.ShelfLayout;
import com.eostek.history.R;
import com.eostek.history.model.HistoryCategory;
import com.eostek.history.model.HistoryItem;
import com.eostek.history.model.MoreItem;
import com.eostek.history.util.Constants;
import com.google.common.collect.Lists;

/**
 * Builder for history item.
 */
public class HistoryItemBuilder {

    private static final String TAG = "HistoryItemBuilder";

    private Context mContext;

    private List<HistoryCategory> mCategorys = new ArrayList<HistoryCategory>();

    /**
     * Value of get more count.
     */
    public static final int GET_MORE_COUNT = Constants.GET_MORE_COUNT;

    /**
     * Constructor of HistoryItemBuilder.
     * 
     * @param mContext
     */
    public HistoryItemBuilder(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Get category list.
     * 
     * @param currentType
     * @return
     */
    public List<HistoryCategory> getCategorysList(String currentType) {
        clearCategorys();
        loadCategorys(currentType);

        return mCategorys;
    }

    private void clearCategorys() {
        if (mCategorys != null) {
            mCategorys.clear();
        }
    }

    private void loadCategorys(String currentType) {

        if (currentType.equals("today")) {
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_MEDIA_VIDEO, SciflyStore.Footprints.PERIOD_TODAY));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_APK_ENTRY, SciflyStore.Footprints.PERIOD_TODAY));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_WEB_PAGE, SciflyStore.Footprints.PERIOD_TODAY));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_MEDIA_VOD, SciflyStore.Footprints.PERIOD_TODAY));
        } else if (currentType.equals("yesterday")) {
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_MEDIA_VIDEO,
                    SciflyStore.Footprints.PERIOD_YESTERDAY));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_APK_ENTRY,
                    SciflyStore.Footprints.PERIOD_YESTERDAY));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_WEB_PAGE, SciflyStore.Footprints.PERIOD_YESTERDAY));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_MEDIA_VOD,
                    SciflyStore.Footprints.PERIOD_YESTERDAY));
        } else if (currentType.equals("7 day ago")) {
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_MEDIA_VIDEO, SciflyStore.Footprints.PERIOD_WEEK));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_APK_ENTRY, SciflyStore.Footprints.PERIOD_WEEK));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_WEB_PAGE, SciflyStore.Footprints.PERIOD_WEEK));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_MEDIA_VOD, SciflyStore.Footprints.PERIOD_WEEK));
        } else if (currentType.equals("30 day ago")) {
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_MEDIA_VIDEO, SciflyStore.Footprints.PERIOD_MONTH));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_APK_ENTRY, SciflyStore.Footprints.PERIOD_MONTH));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_WEB_PAGE, SciflyStore.Footprints.PERIOD_MONTH));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_MEDIA_VOD, SciflyStore.Footprints.PERIOD_MONTH));
        } else if (currentType.equals("greater than 30 day")) {
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_MEDIA_VIDEO,
                    SciflyStore.Footprints.PERIOD_OVER_MONTH));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_APK_ENTRY,
                    SciflyStore.Footprints.PERIOD_OVER_MONTH));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_WEB_PAGE,
                    SciflyStore.Footprints.PERIOD_OVER_MONTH));
            add2Categorys(getCategory(SciflyStore.Footprints.CATEGORY_MEDIA_VOD,
                    SciflyStore.Footprints.PERIOD_OVER_MONTH));
        }

    }

    private void add2Categorys(HistoryCategory category) {
        if (category.getChildrenSize() > 0) {
            mCategorys.add(category);
        }
    }

    /**
     * Get category.
     * 
     * @param category
     * @param period
     * @return
     */
    public HistoryCategory getCategory(int category, int period) {
        boolean simpleFormat = false;
        List<Footprint> footprints = null;
        if (period == SciflyStore.Footprints.PERIOD_TODAY) {
            footprints = SciflyStore.Footprints.getFootprints(mContext.getContentResolver(), category,
                    SciflyStore.Footprints.PERIOD_TODAY);
            simpleFormat = true;
        } else if (period == SciflyStore.Footprints.PERIOD_YESTERDAY) {
            footprints = SciflyStore.Footprints.getFootprints(mContext.getContentResolver(), category,
                    SciflyStore.Footprints.PERIOD_YESTERDAY);
            simpleFormat = true;
        } else if (period == SciflyStore.Footprints.PERIOD_WEEK) {
            footprints = SciflyStore.Footprints.getFootprints(mContext.getContentResolver(), category,
                    SciflyStore.Footprints.PERIOD_WEEK);
        } else if (period == SciflyStore.Footprints.PERIOD_MONTH) {
            footprints = SciflyStore.Footprints.getFootprints(mContext.getContentResolver(), category,
                    SciflyStore.Footprints.PERIOD_MONTH);
        } else if (period == SciflyStore.Footprints.PERIOD_OVER_MONTH) {
            footprints = SciflyStore.Footprints.getFootprints(mContext.getContentResolver(), category,
                    SciflyStore.Footprints.PERIOD_OVER_MONTH);
        } else {
            return new HistoryCategory("", ShelfLayout.SHELVES, new ArrayList<HistoryItem>());
        }

        if (footprints == null || (footprints != null && footprints.size() == 0)) {
            Log.d(TAG, "foot prints is empty for category : " + category);
            if (category != SciflyStore.Footprints.CATEGORY_WEB_PAGE) {
                return new HistoryCategory("", ShelfLayout.SHELVES, new ArrayList<HistoryItem>());
            }
        }

        List<HistoryItem> children = Lists.newArrayList();
        String categoryName = "";
        String title = "";
        if (category == SciflyStore.Footprints.CATEGORY_APK_ENTRY) {
            categoryName = title = mContext.getString(R.string.apps);
            for (Footprint cell : footprints) {
                Intent intent;
                try {
                    intent = Intent.parseUri(cell.mData, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException e) {
                    return new HistoryCategory("", ShelfLayout.SHELVES, new ArrayList<HistoryItem>());
                }

                String pkg = intent.getComponent().getPackageName();
                String cls = intent.getComponent().getClassName();
                String timeLabel = "";
                if (simpleFormat) {
                    timeLabel = new SimpleDateFormat("HH:mm:ss").format(new Date(cell.mTime));
                } else {
                    timeLabel = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(cell.mTime));
                }
                HistoryItem item = new HistoryItem(categoryName, pkg, cls, cls, cell.mTitle, timeLabel);
                item.setCategory(category);
                children.add(item);
            }

        } else if (category == SciflyStore.Footprints.CATEGORY_MEDIA_VIDEO
                || category == SciflyStore.Footprints.CATEGORY_MEDIA_VOD) {
            categoryName = mContext.getString(R.string.media_browser);
            if (category == SciflyStore.Footprints.CATEGORY_MEDIA_VIDEO) {
                title = mContext.getString(R.string.local_media);
            } else if (category == SciflyStore.Footprints.CATEGORY_MEDIA_VOD) {
                title = mContext.getString(R.string.vod_media);
            }

            for (Footprint cell : footprints) {
                Intent intent = null;
                try {
                    intent = Intent.parseUri(cell.mData, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException e) {
                    return new HistoryCategory("", ShelfLayout.SHELVES, new ArrayList<HistoryItem>());
                }

                String pkg = intent.getComponent().getPackageName();
                String cls = intent.getComponent().getClassName();

                String timeLabel = "";
                if (simpleFormat) {
                    timeLabel = new SimpleDateFormat("HH:mm:ss").format(new Date(cell.mTime));
                } else {
                    timeLabel = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(cell.mTime));
                }
                HistoryItem item = new HistoryItem(categoryName, pkg, cls, cell.mData, cell.mTitle, timeLabel);

                item.setCategory(category);
                item.setSmallImage(cell.mThumb);

                children.add(item);
            }
        } else if (category == SciflyStore.Footprints.CATEGORY_WEB_PAGE) {
            categoryName = title = mContext.getString(R.string.chrome);
            String pkg = "com.chrome.tv.stable";
            String cls = "com.google.android.apps.chrome.Main";
            // adding the string "date is not null" to necessary condition to
            // only query the HISTORY TABLE
            Cursor cursor = mContext.getContentResolver().query(Browser.BOOKMARKS_URI, Browser.HISTORY_PROJECTION,
                    "date is not null", null, null);
            if (cursor == null) {
                Log.w(TAG, "cursor is null");
                return new HistoryCategory("", ShelfLayout.SHELVES, new ArrayList<HistoryItem>());
            }

            while (cursor.moveToNext()) {
                long time = cursor.getLong(Browser.HISTORY_PROJECTION_DATE_INDEX);
                long[] requestTime = getRequestTime(period);
                if (period == SciflyStore.Footprints.PERIOD_OVER_MONTH) {
                    if (time > requestTime[0]) {
                        continue;
                    }
                } else {
                    if (time < requestTime[0] || time > requestTime[1]) {
                        continue;
                    }

                }
                String timeLabel = "";
                if (simpleFormat) {
                    timeLabel = new SimpleDateFormat("HH:mm:ss").format(new Date(time));
                } else {
                    timeLabel = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(time));
                }
                String uri = cursor.getString(Browser.HISTORY_PROJECTION_URL_INDEX);
                String webTitle = cursor.getString(Browser.HISTORY_PROJECTION_TITLE_INDEX);
                if (webTitle.isEmpty()) {
                    webTitle = uri;
                }
                HistoryItem item = new HistoryItem(categoryName, pkg, cls, uri, webTitle, timeLabel);

                byte[] bitmapByte = cursor.getBlob(Browser.HISTORY_PROJECTION_THUMBNAIL_INDEX);
                Bitmap bitmap = null;
                if (bitmapByte != null) {
                    bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
                }
                item.setCategory(category);
                item.setSmallImage(bitmap);

                children.add(item);
            }
        }
        if (children.size() >= GET_MORE_COUNT) {
            addGetMoreItem(children);
        }

        HistoryCategory history = new HistoryCategory(title, ShelfLayout.SHELVES, children);
        return history;
    }

    private void addGetMoreItem(List<HistoryItem> children) {
        children.add(new MoreItem("" + mContext.getString(R.string.more)));
    }

    private long[] getRequestTime(int period) {
        Calendar calendar = Calendar.getInstance();
        long timeFrom = 0;
        long timeTo = 0;
        if (period == Footprints.PERIOD_TODAY) {
            // current time
            timeTo = calendar.getTimeInMillis();

            // 0:00 of today
            calendar.add(Calendar.DAY_OF_WEEK, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            timeFrom = calendar.getTimeInMillis();
        } else if (period == Footprints.PERIOD_YESTERDAY) {
            // yesterday 0:00
            calendar.add(Calendar.DAY_OF_WEEK, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            timeTo = calendar.getTimeInMillis();

            // day before today
            calendar.add(Calendar.DAY_OF_WEEK, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            timeFrom = calendar.getTimeInMillis();
        } else if (period == Footprints.PERIOD_WEEK) {
            calendar.add(Calendar.DAY_OF_WEEK, -2);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            timeTo = calendar.getTimeInMillis();

            calendar.add(Calendar.DAY_OF_WEEK, -5);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            timeFrom = calendar.getTimeInMillis();
        } else if (period == Footprints.PERIOD_MONTH) {
            calendar.add(Calendar.DAY_OF_WEEK, -8);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            timeTo = calendar.getTimeInMillis();

            calendar.add(Calendar.DAY_OF_WEEK, -22);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            timeFrom = calendar.getTimeInMillis();
        } else if (period == Footprints.PERIOD_OVER_MONTH) {
            calendar.add(Calendar.DAY_OF_WEEK, -31);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            timeTo = calendar.getTimeInMillis();

            calendar.add(Calendar.DAY_OF_WEEK, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            timeFrom = calendar.getTimeInMillis();
        }

        return new long[] {
                timeFrom, timeTo
        };
    }

}
