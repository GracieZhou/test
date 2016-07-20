
package com.eostek.history.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import scifly.provider.SciflyStore;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.provider.Browser;
import android.text.format.DateUtils;
import android.util.Log;

import com.bq.tv.traxex.ShelfLayout;
import com.eostek.history.HistoryHolder;
import com.eostek.history.R;
import com.eostek.history.model.HistoryCategory;
import com.eostek.history.model.HistoryItem;
import com.eostek.history.model.TopLevelFragmentType;
import com.eostek.history.ui.item.HistoryItemBuilder;
import com.eostek.history.ui.leftnavbar.NewLeftNavBar;
import com.google.common.collect.ImmutableList;

/**
 * Container of left navigation bar.
 */
public class LeftNavBarContainer {

    private static final String TAG = "LeftNavBarContainer";

    private Context mContext;

    private Activity mActivity;

    private HistoryHolder mHolder;

    protected NewLeftNavBar mLeftNavBar;

    HistoryItemBuilder mHiBuilder;

    List<HistoryCategory> mCurrentHistoryCategorys;

    /**
     * Constructor of LeftNavBarContainer.
     * 
     * @param mContext
     * @param holder
     */
    public LeftNavBarContainer(Context mContext, HistoryHolder holder) {
        this.mContext = mContext;

        mActivity = (Activity) mContext;

        mHolder = holder;

        mLeftNavBar = new NewLeftNavBar((Activity) mContext);

        mHiBuilder = new HistoryItemBuilder(mContext);

    }

    /**
     * Constructor of LeftNavBarContainer.
     * 
     * @param mContext
     */
    public LeftNavBarContainer(Context mContext) {
        this.mContext = mContext;

        mLeftNavBar = new NewLeftNavBar((Activity) mContext);

        mHiBuilder = new HistoryItemBuilder(mContext);

    }

    // 所有文件都显示.
    public static final TopLevelFragmentType TODAY = new TopLevelFragmentType("today", Uri.parse("localfs://all"),
            R.drawable.tab_today, R.string.tab_today, ShelfLayout.SHELVES);

    public static final TopLevelFragmentType YESTERDAY = new TopLevelFragmentType("yesterday",
            Uri.parse("localfs://video"), R.drawable.tab_yesterday, R.string.tab_yesterday, ShelfLayout.SHELVES);

    public static final TopLevelFragmentType LAST_WEEK = new TopLevelFragmentType("7 day ago",
            Uri.parse("localfs://photo"), R.drawable.tab_7_day_ago, R.string.tab_7_day_ago, ShelfLayout.SHELVES);

    public static final TopLevelFragmentType LAST_MONTH = new TopLevelFragmentType("30 day ago",
            Uri.parse("localfs://music"), R.drawable.tab_30_day_ago, R.string.tab_30_day_ago, ShelfLayout.SHELVES);

    public static final TopLevelFragmentType A_MONTH_AGO = new TopLevelFragmentType("greater than 30 day",
            Uri.parse("localfs://music"), R.drawable.tab_greater_than_30_day, R.string.tab_greater_than_30_day,
            ShelfLayout.SHELVES);

    public static final TopLevelFragmentType CLEAN_HISTORY = new TopLevelFragmentType("clean history",
            Uri.parse("localfs://music"), R.drawable.tab_clean_history, R.string.tab_clean_history, ShelfLayout.SHELVES);

    public void initNavBar() {

        mLeftNavBar = new NewLeftNavBar((Activity) mContext);

        mLeftNavBar.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.leftnav_bar_background_dark));

        mLeftNavBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mLeftNavBar.showOptionsMenu(false);
        mLeftNavBar.setDisplayOptions(NewLeftNavBar.DISPLAY_ALWAYS_EXPANDED
                | NewLeftNavBar.DISPLAY_USE_LOGO_WHEN_EXPANDED, ActionBar.DISPLAY_USE_LOGO
                | ActionBar.DISPLAY_SHOW_TITLE | NewLeftNavBar.DISPLAY_ALWAYS_EXPANDED
                | NewLeftNavBar.DISPLAY_USE_LOGO_WHEN_EXPANDED);
    }

    /**
     * Get left navigation bar tabs.
     */
    public ImmutableList<TopLevelFragmentType> getLeftNavBarTabs() {
        return ImmutableList.of(TODAY, YESTERDAY, LAST_WEEK, LAST_MONTH, A_MONTH_AGO, CLEAN_HISTORY);
    }

    public void setupTabAndListener() {
        mLeftNavBar.removeAllTabs();

        ImmutableList<TopLevelFragmentType> tabList = getLeftNavBarTabs();
        String lastTabStr = TODAY.mName;

        Iterator<TopLevelFragmentType> iterator = tabList.iterator();
        while (iterator.hasNext()) {
            TopLevelFragmentType fragmentType = (TopLevelFragmentType) iterator.next();
            Tab tab = mLeftNavBar.newTab().setIcon(fragmentType.mIconId).setText(fragmentType.mLabelResource)
                    .setTabListener(new TabListener(fragmentType));
            mLeftNavBar.addTab(tab);
            if (fragmentType.mName.equals(lastTabStr)) {
                mLeftNavBar.selectTab(tab);
            }
        }
    }

    private class TabListener implements ActionBar.TabListener {
        private final TopLevelFragmentType mFragmentType;

        public TabListener(TopLevelFragmentType fragmentType) {
            mFragmentType = fragmentType;
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            Log.i(TAG, "onTabSelected type = " + mFragmentType.mName);

            if (mFragmentType.mName.equals("clean history")) {
                mHolder.updateHistoryCategorys(new ArrayList<HistoryCategory>());
                clearHistory();
                return;
            }

            mCurrentHistoryCategorys = mHiBuilder.getCategorysList(mFragmentType.mName);

            mHolder.updateHistoryCategorys(mCurrentHistoryCategorys);

            String tabText = (String) tab.getText();

            if (mFragmentType.mName.equals("today")) {
                tabText += "(" + formatDateTime(mContext, Calendar.getInstance().getTimeInMillis()) + ")";
            } else if (mFragmentType.mName.equals("yesterday")) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                tabText += "(" + formatDateTime(mContext, calendar.getTimeInMillis()) + ")";
            }

            mHolder.updateContentBlockTitle(tabText);

            Log.i(TAG, "mCurrentHistoryCategorys size = " + mCurrentHistoryCategorys.size());
            for (HistoryCategory hc : mCurrentHistoryCategorys) {
                Log.i(TAG, "hc.getCategory() " + hc.getCategory());

                if (hc.getCategory() == SciflyStore.Footprints.CATEGORY_APK_ENTRY) {
                    Log.i(TAG,
                            hc.getCategory() + " : SciflyStore.Footprints.CATEGORY_APK_ENTRY" + " title "
                                    + hc.getTitle());
                } else if (hc.getCategory() == SciflyStore.Footprints.CATEGORY_MEDIA_VIDEO) {
                    Log.i(TAG,
                            hc.getCategory() + " : SciflyStore.Footprints.CATEGORY_MEDIA_VIDEO" + " title "
                                    + hc.getTitle());
                } else if (hc.getCategory() == SciflyStore.Footprints.CATEGORY_WEB_PAGE) {
                    Log.i(TAG,
                            hc.getCategory() + " : SciflyStore.Footprints.CATEGORY_WEB_PAGE" + " title "
                                    + hc.getTitle());
                } else if (hc.getCategory() == SciflyStore.Footprints.CATEGORY_MEDIA_VOD) {
                    Log.i(TAG,
                            hc.getCategory() + " : SciflyStore.Footprints.CATEGORY_MEDIA_VOD" + " title "
                                    + hc.getTitle());
                }

                Log.i(TAG, "title" + hc.getTitle());
                for (HistoryItem item : hc.getChildren()) {
                    Log.i(TAG, item.toString());
                }
            }
        }

        private void clearHistory() {
            mHolder.updateContentBlockTitle("");
            new AlertDialog.Builder(mContext).setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle(R.string.warning_clean_history)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            try {
                                SciflyStore.Footprints.deleteFootprints(mContext.getContentResolver(), null, null);
                                Browser.clearHistory(mContext.getContentResolver());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).setNegativeButton(android.R.string.cancel, null).create().show();
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            Log.i(TAG, "onTabSelected");
            if (mFragmentType.mName.equals("clean history")) {
                clearHistory();
                return;
            }
        }
    }

    private void clearFragmentBackStack() {
        int i = mActivity.getFragmentManager().getBackStackEntryCount();
        for (int j = 0; j < i; j++) {
            mActivity.getFragmentManager().popBackStack();
        }
    }

    /**
     * Format date time.
     * 
     * @param context
     * @param millis
     * @return
     */
    public static String formatDateTime(Context context, long millis) {
        return DateUtils.formatDateTime(context, millis, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY
                | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_ABBREV_WEEKDAY);
    }

}
