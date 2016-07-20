
package com.eostek.history.model;

import android.net.Uri;

import com.bq.tv.traxex.ShelfLayout;

/**
 * Class of top level fragment type.
 */
public class TopLevelFragmentType {

    /**
     * Value of name.
     */
    public final String mName;

    /**
     * Value of icon id.
     */
    public final int mIconId;

    /**
     * Value of label resource.
     */
    public final int mLabelResource;

    /**
     * Value of uri.
     */
    public final Uri mUri;

    /**
     * Value of shelf layout.
     */
    public final ShelfLayout mShelfLayout;

    /**
     * Constructor of TopLevelFragmentType.
     * 
     * @param mName
     * @param mUri
     * @param mIconId
     * @param mLabelResource
     * @param mShelfLayout
     */
    public TopLevelFragmentType(String mName, Uri mUri, int mIconId, int mLabelResource, ShelfLayout mShelfLayout) {
        this.mName = mName;
        this.mUri = mUri;
        this.mIconId = mIconId;
        this.mLabelResource = mLabelResource;
        this.mShelfLayout = mShelfLayout;
    }
}
