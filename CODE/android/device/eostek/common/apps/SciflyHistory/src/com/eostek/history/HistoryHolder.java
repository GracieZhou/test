
package com.eostek.history;

import java.util.List;

import android.content.Context;

import com.eostek.history.model.HistoryCategory;
import com.eostek.history.ui.ClassificationContainer;
import com.eostek.history.ui.LeftNavBarContainer;
import com.eostek.history.ui.item.HistoryItemBuilder;

/**
 * Class of history holder.
 */
public class HistoryHolder {

    private Context mContext;

    private LeftNavBarContainer mLeftNavBarContainer;

    private ClassificationContainer mClassificationContainer;

    /**
     * Constructor of HistoryHolder.
     * 
     * @param mContext
     */
    public HistoryHolder(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Get views.
     */
    public void getViews() {
        mLeftNavBarContainer = new LeftNavBarContainer(mContext, this);

        mClassificationContainer = new ClassificationContainer(mContext);

        initNavBar();

        initClassifications();

    }

    /**
     * Return true if grid view is focused.
     * 
     * @return
     */
    public boolean isGridViewFocused() {
        return mClassificationContainer.isFocused();
    }

    private void initClassifications() {
        mClassificationContainer.initClassifications();
    }

    private void initNavBar() {
        mLeftNavBarContainer.initNavBar();
    }

    /**
     * Update history categorys.
     * 
     * @param mCurrentHistoryCategorys
     */
    public void updateHistoryCategorys(List<HistoryCategory> mCurrentHistoryCategorys) {
        mClassificationContainer.UpdateHistoryCategorys(mCurrentHistoryCategorys);
    }

    /**
     * Find next focus.
     * 
     * @param keyDirection
     */
    public void findNextFocus(int keyDirection) {
        mClassificationContainer.findNextFocus(keyDirection);
    }

    /**
     * Return true if grid view's first item is selected.
     * 
     * @return
     */
    public boolean isGridViewFirstItemSelected() {
        return mClassificationContainer.isGridViewFirstItemSelected();
    }

    /**
     * Return true if More item window is showing.
     * 
     * @return
     */
    public boolean isMoreItemsWindowShowing() {
        return mClassificationContainer.isMoreItemsWindowShowing();
    }

    /**
     * @return whether finish activity.
     */
    public boolean onBackPressed() {
        return mClassificationContainer.onBackPressed();
    }

    /**
     * Update content block title.
     * 
     * @param tabText
     */
    public void updateContentBlockTitle(String tabText) {
        mClassificationContainer.updateContentBlockTitle(tabText);
    }

    public LeftNavBarContainer getLeftNavBarContainer() {
        return mLeftNavBarContainer;
    }

    public ClassificationContainer getClassificationContainer() {
        return mClassificationContainer;
    }

}
