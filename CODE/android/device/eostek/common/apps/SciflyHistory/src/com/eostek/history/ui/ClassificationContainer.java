
package com.eostek.history.ui;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import scifly.provider.SciflyStore;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.history.R;
import com.eostek.history.model.HistoryCategory;
import com.eostek.history.model.HistoryItem;
import com.eostek.history.model.MoreItem;
import com.eostek.history.ui.animation.UIAnimationUtil;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayAdapterView.OnItemClickListener;
import com.jess.ui.TwoWayAdapterView.OnItemSelectedListener;
import com.jess.ui.TwoWayGridView;

/**
 * Class of classification container.
 */
public class ClassificationContainer {

    /**
     * Tag used to show in logcat.
     */
    public static final String TAG = "ClassificationContainer";

    private Context mContext;

    private Activity mActivity;

    List<HistoryClassification> mClassifications = new ArrayList<ClassificationContainer.HistoryClassification>();

    int[] mClassificationIds = new int[] {
            R.id.classification_item1, R.id.classification_item2, R.id.classification_item3, R.id.classification_item4
    };

    /**
     * Index of apk entry.
     */
    public static final int INDEX_APK_ENTRY = 0;

    /**
     * Index of media video.
     */
    public static final int INDEX_MEDIA_VIDEO = 1;

    /**
     * Index of media vod.
     */
    public static final int INDEX_MEDIA_VOD = 2;

    /**
     * Index of web page.
     */
    public static final int INDEX_WEB_PAGE = 3;

    final int[] mCategory = new int[] {
            SciflyStore.Footprints.CATEGORY_APK_ENTRY, SciflyStore.Footprints.CATEGORY_MEDIA_VIDEO,
            SciflyStore.Footprints.CATEGORY_MEDIA_VOD, SciflyStore.Footprints.CATEGORY_WEB_PAGE
    };

    final int[] mTitileStringIds = new int[] {
            R.string.apps, R.string.local_media, R.string.vod_media, R.string.chrome
    };

    /**
     * Class of comparator category.
     */
    public class ComparatorCategory implements Comparator {
        @Override
        public int compare(Object a, Object b) {

            if (!(a instanceof HistoryCategory && b instanceof HistoryCategory)) {
                return 0;
            }

            HistoryCategory aObj = (HistoryCategory) a;
            HistoryCategory bObj = (HistoryCategory) b;
            int indexA = 0;
            int indexB = 0;

            for (int i = 0; i < mCategory.length; i++) {
                if (aObj.getCategory() == mCategory[i]) {
                    indexA = i;
                    break;
                }
            }

            for (int i = 0; i < mCategory.length; i++) {
                if (bObj.getCategory() == mCategory[i]) {
                    indexB = i;
                    break;
                }
            }

            return indexB - indexA;
        }
    }

    /**
     * Update history category.
     * 
     * @param categorys
     */
    @SuppressWarnings("unchecked")
    public void UpdateHistoryCategorys(List<HistoryCategory> categorys) {

        clearCategorys();

        setAllClassificationGone();

        if (categorys == null || categorys.size() == 0) {
            showClassificationEmptyWindow();
            return;
        }

        showClassificationWindow();

        ComparatorCategory comparator = new ComparatorCategory();
        Collections.sort(categorys, comparator);

        for (HistoryCategory category : categorys) {
            for (int i = 0; i < mCategory.length; i++) {
                if (mCategory[i] == category.getCategory()) {
                    Log.i(TAG, "category " + category.getTitle() + " i = " + i);
                    generateAdapter(i, category);
                    setClassificationVisibility(i, View.VISIBLE);
                    break;
                }
            }
        }

        initClassifications();

    }

    private void clearCategorys() {
        if (mClassifications != null) {
            mClassifications.clear();
        }
    }

    /**
     * Constructor of ClassificationContainer.
     * 
     * @param mContext
     */
    public ClassificationContainer(Context mContext) {
        this.mContext = mContext;

        if (mContext instanceof Activity) {
            mActivity = (Activity) mContext;
        } else {
            return;
        }

    }

    private void generateAdapter(int index, HistoryCategory category) {

        TwoWayGridView gridView = (TwoWayGridView) mActivity.findViewById(mClassificationIds[index]).findViewById(
                R.id.gridview);

        gridView.setNumRows(1);
        gridView.setNumColumns(5);
        gridView.setSelector(mContext.getResources().getDrawable(R.drawable.transparent_outter_focus));

        HistoryClassification hc = new HistoryClassification();
        hc.setGridView(gridView).setTitleString(mContext.getResources().getString(mTitileStringIds[index]))
                .setTitle(mTitileStringIds[index]).setViewId(mClassificationIds[index]).setCategory(mCategory[index]);

        gridView.setAdapter(new HistoryBaseAdapter(mContext, category));

        gridView.setLayoutAnimation(UIAnimationUtil.getLayoutShowAnimation(0, 200));

        mClassifications.add(0, hc);
    }

    private void setAllClassificationGone() {
        for (int i = 0; i < mCategory.length; i++) {
            setClassificationVisibility(i, View.GONE);
        }
    }

    private void setClassificationVisibility(int index, int visisbility) {

        try {
            ViewGroup classificationView = (ViewGroup) mActivity.findViewById(mClassificationIds[index]);

            classificationView.setVisibility(visisbility);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initial classification.
     */
    public void initClassifications() {
        for (HistoryClassification hc : mClassifications) {
            setItemContent(hc);
            setItemListener(hc);
        }
    }

    private void setItemListener(HistoryClassification hc) {

        final TwoWayGridView gridView = hc.getGridView();

        setTwoWayGridViewListener(gridView, hc);

    }

    private void setGridViewListener(GridView gridView) {

        gridView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (!hasFocus) {
                    GridView gridView = (GridView) view;
                    gridView.setSelection(-1);
                }

            }
        });

        gridView.setOnItemSelectedListener(new GridView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
                Log.i(TAG, "Item at position " + position + " selected");
                HistoryBaseAdapter adapter = (HistoryBaseAdapter) parent.getAdapter();
                adapter.setLastSelectedPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                HistoryBaseAdapter adapter = (HistoryBaseAdapter) parent.getAdapter();
                HistoryItem item = (HistoryItem) parent.getItemAtPosition(position);
                Log.i(TAG, "category " + item.getCategory());
                dispatchItemClickEvent(item, adapter, null);
            }
        });

    }

    private void setTwoWayGridViewListener(final TwoWayGridView gridView, final HistoryClassification hc) {

        gridView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(TwoWayAdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Item at position " + position + " selected");
                HistoryBaseAdapter adapter = (HistoryBaseAdapter) parent.getAdapter();
                adapter.setLastSelectedPosition(position);
            }

            @Override
            public void onNothingSelected(TwoWayAdapterView<?> parent) {
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
                HistoryBaseAdapter adapter = (HistoryBaseAdapter) parent.getAdapter();
                HistoryItem item = (HistoryItem) parent.getItemAtPosition(position);
                Log.i(TAG, "category " + item.getCategory());
                dispatchItemClickEvent(item, adapter, hc);
            }
        });
    }

    private void dispatchItemClickEvent(HistoryItem item, HistoryBaseAdapter adapter, HistoryClassification hc) {

        if (item == null) {
            return;
        }

        if (item instanceof MoreItem) {
            Log.i(TAG, "need go to more");
            showMoreItemWindow(new HistoryBaseAdapter(mContext, adapter.getHistoryItemsFull()),
                    "" + hc.getTitleString());
        } else if (item instanceof HistoryItem) {
            String categoryName = item.getCategoryName();
            Resources resources = mActivity.getResources();
            Log.i(TAG, "categoryName: " + categoryName);
            if (categoryName.equals(resources.getString(R.string.media_browser))) {
                if (item.getCategory() == SciflyStore.Footprints.CATEGORY_MEDIA_VIDEO) {
                    Intent intent = null;
                    try {
                        intent = Intent.parseUri(item.getData(), Intent.URI_INTENT_SCHEME);
                    } catch (URISyntaxException e) {
                        Log.w(TAG, "Intent.parseUri exception", e);
                        Toast.makeText(mActivity, R.string.error_could_not_launch, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.d(TAG, "media file url : " + intent.getData().getPath());
                    File file = new File(intent.getData().getPath());
                    if (!file.exists()) {
                        String hint = mActivity.getString(R.string.hint_about_file);
                        String itemName = item.getName();
                        Toast.makeText(mActivity, String.format(hint, itemName), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        mActivity.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(mActivity, R.string.error_could_not_launch, Toast.LENGTH_SHORT).show();
                    }

                } else if (item.getCategory() == SciflyStore.Footprints.CATEGORY_MEDIA_VOD) {
                    try {
                        Intent intent = Intent.parseUri(item.getData(), Intent.URI_INTENT_SCHEME);
                        intent.putExtra("prevent_insert_record", true);
                        mActivity.startActivity(intent);
                    } catch (URISyntaxException e1) {
                        Log.w(TAG, "", e1);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(mActivity, R.string.error_could_not_launch, Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (categoryName.equals(resources.getString(R.string.apps))) {
                Intent intent = new Intent();
                intent.setClassName(item.getPackageName(), item.getClassName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

                try {
                    mActivity.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mActivity, R.string.error_could_not_launch, Toast.LENGTH_SHORT).show();
                }
            } else if (categoryName.equals(resources.getString(R.string.chrome))) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(item.getData()));
                try {
                    mActivity.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mActivity, R.string.error_could_not_launch, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Set content of item.
     * 
     * @param hc
     */
    public void setItemContent(HistoryClassification hc) {
        Resources r = mContext.getResources();

        View view = mActivity.findViewById(hc.mViewId);

        String title = r.getString(hc.mTitleId);

        setItemContent(view, title);
    }

    /**
     * Set content of item.
     * 
     * @param view
     * @param title
     */
    public void setItemContent(View view, String title) {
        setTitle(view, title);
    }

    /**
     * Set title.
     * 
     * @param view
     * @param title
     */
    public void setTitle(View view, String title) {
        TextView tv = (TextView) view.findViewById(R.id.classification_item_title);
        tv.setText("" + title);
    }

    /** used to save history classification informations */
    public class HistoryClassification {

        TwoWayGridView mGridView;

        String mTitleString;

        int mTitleId;

        int mViewId;

        int mCategory;

        /**
         * Get category.
         * 
         * @return
         */
        public int getCategory() {
            return mCategory;
        }

        /**
         * set category.
         * 
         * @param mCategory
         */
        public void setCategory(int mCategory) {
            this.mCategory = mCategory;
        }

        /**
         * Get grid view.
         * 
         * @return
         */
        public TwoWayGridView getGridView() {
            return mGridView;
        }

        /**
         * Get adapter.
         * 
         * @return
         */
        public ListAdapter getAdapter() {
            return mGridView.getAdapter();
        }

        /**
         * Set grid view.
         * 
         * @param mGridView
         * @return
         */
        public HistoryClassification setGridView(TwoWayGridView mGridView) {
            this.mGridView = mGridView;
            return this;
        }

        /**
         * Get title.
         * 
         * @return
         */
        public int getTitle() {
            return mTitleId;
        }

        /**
         * Set title.
         * 
         * @param mTitleId
         * @return
         */
        public HistoryClassification setTitle(int mTitleId) {
            this.mTitleId = mTitleId;
            return this;
        }

        /**
         * Get view id.
         * 
         * @return
         */
        public int getViewId() {
            return mViewId;
        }

        /**
         * Get title string.
         * 
         * @return
         */
        public String getTitleString() {
            return mTitleString;
        }

        /**
         * set title string.
         * 
         * @param mTitleString
         * @return
         */
        public HistoryClassification setTitleString(String mTitleString) {
            this.mTitleString = mTitleString;
            return this;
        }

        /**
         * set view id.
         * 
         * @param mViewId
         * @return
         */
        public HistoryClassification setViewId(int mViewId) {
            this.mViewId = mViewId;
            return this;
        }

    }

    /**
     * Return true if is focused.
     * 
     * @return
     */
    public boolean isFocused() {
        boolean foucused = false;

        for (HistoryClassification hc : mClassifications) {
            foucused = foucused || hc.getGridView().isFocused();
        }

        return foucused;
    }

    /**
     * Find next focus.
     * 
     * @param keyDirection
     */
    public void findNextFocus(int keyDirection) {

        Log.i(TAG, "mClassifications size " + mClassifications.size());

        for (HistoryClassification hc : mClassifications) {
            Log.i(TAG, " " + hc.getTitleString());
        }

        switch (keyDirection) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                findFocusDown();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                findFocusUp();
                break;
        }

    }

    private void findFocusUp() {
        Log.i(TAG, "findFocusUp");

        for (int i = mClassifications.size() - 1; i >= 0; i--) {
            if (mClassifications.get(i).getGridView().isFocused()) {
                Log.i(TAG, "index : " + i + "title = " + mClassifications.get(i).getTitleString() + " is focused");
                if (i - 1 >= 0) {
                    setSelectedGridView(i - 1);
                    break;
                }
            }
        }

    }

    private void findFocusDown() {
        Log.i(TAG, "findFocusDown");

        for (int i = 0; i < mCategory.length && i < mClassifications.size(); i++) {
            if (mClassifications.get(i).getGridView().isFocused()) {
                Log.i(TAG, "index : " + i + "title = " + mClassifications.get(i).getTitleString() + " is focused");
                if (i + 1 < mClassifications.size()
                        && mClassifications.get(i).getGridView().getVisibility() == View.VISIBLE) {

                    setSelectedGridView(i + 1);

                    break;

                }
            }
        }

    }

    private void setSelectedGridView(int i) {

        try {
            Log.i(TAG, "index : " + mClassifications.get(i).getTitleString() + " get focus");

            HistoryBaseAdapter adapter = (HistoryBaseAdapter) mClassifications.get(i).getAdapter();

            int lastSelectedPosition = adapter.getLastSelectedPostion();

            mClassifications.get(i).getGridView().requestFocus();
            mClassifications.get(i).getGridView().requestFocusFromTouch();

            mClassifications.get(i).getGridView().setSelection(lastSelectedPosition);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    /** get Position On Screen */
    public int getPositionOnScreen(TwoWayGridView twoWayGridView, int position) {
        if (position < 0 || position > twoWayGridView.getCount()) {
            return -1;
        }

        return position - twoWayGridView.getFirstVisiblePosition();
    }

    private void setMoreItemsTitle(View view, String title) {
        TextView tv = (TextView) view.findViewById(R.id.classification_more_title);
        tv.setText("" + title);
    }

    private void setClassificationEmptyWindowVisibiblity(int visibility) {
        if (getClassificationEmptyWindowView() != null) {
            getClassificationEmptyWindowView().setVisibility(visibility);
        }
    }

    private View getClassificationEmptyWindowView() {
        return mActivity.findViewById(R.id.classification_empty);
    }

    private void showMoreItemWindow(HistoryBaseAdapter adapter, String title) {
        Log.i(TAG, "showMoreItemWindow");

        setMoreItemsWindowVisibiblity(View.VISIBLE);
        setClassificationWindowVisibiblity(View.GONE);
        setClassificationEmptyWindowVisibiblity(View.GONE);

        setMoreItemsTitle(getMoreItemsWindowView(), title);
        if (adapter == null) {
            return;
        }

        GridView gridView = (GridView) getMoreItemsWindowView().findViewById(R.id.gridview);

        gridView.setAdapter(adapter);

        gridView.setLayoutAnimation(UIAnimationUtil.getLayoutShowAnimation(0, 100));

        setGridViewListener(gridView);

    }

    private void showClassificationWindow() {
        Log.i(TAG, "showClassificationWindow");

        if (getMoreItemsWindowView().getVisibility() == View.VISIBLE) {
            AnimationSet animation = UIAnimationUtil.getViewScaleHideAnimation(200);
            animation.addAnimation(UIAnimationUtil.getViewAlphaHideAnimation());
            getMoreItemsWindowView().setAnimation(animation);
        }

        setMoreItemsWindowVisibiblity(View.GONE);
        setClassificationWindowVisibiblity(View.VISIBLE);
        setClassificationEmptyWindowVisibiblity(View.GONE);
        getClassificationEmptyWindowView().findViewById(R.id.folder_empty_content).setAnimation(null);
    }

    private void showClassificationEmptyWindow() {
        Log.i(TAG, "showClassificationWindow");
        setMoreItemsWindowVisibiblity(View.GONE);
        setClassificationWindowVisibiblity(View.GONE);
        setClassificationEmptyWindowVisibiblity(View.VISIBLE);
        getClassificationEmptyWindowView().findViewById(R.id.folder_empty_content).setAnimation(
                UIAnimationUtil.getViewShowAnimation());
    }

    private View getClassificationWindowView() {
        return mActivity.findViewById(R.id.classification_all);
    }

    private View getMoreItemsWindowView() {
        return mActivity.findViewById(R.id.classification_more);
    }

    private void setClassificationWindowVisibiblity(int visibility) {
        if (getClassificationWindowView() != null) {
            getClassificationWindowView().setVisibility(visibility);
        }
    }

    private void setMoreItemsWindowVisibiblity(int visibility) {
        if (getMoreItemsWindowView() != null) {
            getMoreItemsWindowView().setVisibility(visibility);
        }
    }

    /** is Classification Window Showing */
    public boolean isClassificationWindowShowing() {
        if (getClassificationWindowView().getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    /** is MoreItem Window Showing */
    public boolean isMoreItemsWindowShowing() {
        if (getMoreItemsWindowView().getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    public boolean onBackPressed() {
        if (isClassificationWindowShowing() || isClassificationEmptyWindowShowing()) {
            return true;
        } else {
            showClassificationWindow();
            return false;
        }
    }

    private boolean isClassificationEmptyWindowShowing() {
        if (getClassificationEmptyWindowView().getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    /**
     * if certain gridview's first item is selected ,we press left so selector
     * can go to other view
     */
    public boolean isGridViewFirstItemSelected() {

        for (HistoryClassification hc : mClassifications) {
            HistoryBaseAdapter adapter = (HistoryBaseAdapter) hc.getAdapter();
            if (hc.getGridView().isFocused() && adapter.getLastSelectedPostion() == 0) {
                Log.i(TAG, hc.getTitleString() + " isFocused() " + hc.getGridView().isFocused());
                return true;
            }

        }
        return false;
    }

    /**
     * Update title of content block.
     * 
     * @param tabText
     */
    public void updateContentBlockTitle(String tabText) {
        TextView tv = (TextView) mActivity.findViewById(R.id.classification_time_label);

        if (tv != null) {
            tv.setText("" + tabText);
        }

    }

}
