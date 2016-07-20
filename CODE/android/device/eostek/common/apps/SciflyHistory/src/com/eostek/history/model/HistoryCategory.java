
package com.eostek.history.model;

import java.util.List;

import com.bq.tv.traxex.ShelfLayout;
import com.google.common.collect.Lists;

/**
 * Class of history category.
 */
public class HistoryCategory {

    private String mTitle;

    private int mCategory = -1;

    private ShelfLayout mLayout;

    private List<HistoryItem> mChildren = Lists.newArrayList();

    /**
     * Constructor of HistoryCategory. WARNING: This function is EMPTY NOW!
     */
    public HistoryCategory() {
    }

    /**
     * Constructor of HistoryCategory.
     * 
     * @param title
     * @param layout
     * @param children
     */
    public HistoryCategory(String title, ShelfLayout layout, List<HistoryItem> children) {
        this.mTitle = title;
        this.mLayout = layout;
        this.mChildren = children;
        if (mChildren != null && mChildren.size() > 0) {
            mCategory = mChildren.get(0).getCategory();
        }
    }

    /**
     * Get title.
     * 
     * @return
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Get category.
     * 
     * @return
     */
    public int getCategory() {
        return mCategory;
    }

    /**
     * Set layout.
     * 
     * @param layout
     */
    public void setLayout(ShelfLayout layout) {
        this.mLayout = layout;
    }

    /**
     * Get layout.
     * 
     * @return
     */
    public ShelfLayout getLayout() {
        return mLayout;
    }

    /**
     * Set children.
     * 
     * @param children
     */
    public void setChildren(List<HistoryItem> children) {
        mChildren = children;
    }

    /**
     * Get children.
     * 
     * @return
     */
    public List<HistoryItem> getChildren() {
        return mChildren;
    }

    /**
     * Get children size.
     * 
     * @return
     */
    public int getChildrenSize() {
        return mChildren.size();
    }
}
