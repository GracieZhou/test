
package com.eostek.sciflyui.thememanager;

import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Admin view class
 */
public class ThemeHolder {

    private ThemeDisplayAct mActivity;

    /**
     * 
     */
    private GridView girdview = null;

    /**
     * image Of Theme.
     */
    private ImageView imageOfTheme = null;

    private TextView textOfTheme = null;

    private RelativeLayout mainLayout = null;

    /**
     * @return RelativeLayout
     */
    public RelativeLayout getMainLayout() {
        return mainLayout;
    }

    /**
     * @return GridView
     */
    public GridView getGirdview() {
        return girdview;
    }

    /**
     * @param activity activity
     */
    public ThemeHolder(ThemeDisplayAct activity) {
        this.mActivity = activity;
    }

    /**
     * getViews.
     */
    public void getViews() {
        girdview = (GridView) mActivity.findViewById(R.id.themesGridView);
        imageOfTheme = (ImageView) mActivity.findViewById(R.id.theme_item);
        textOfTheme = (TextView) mActivity.findViewById(R.id.item_textview);
        mainLayout = (RelativeLayout) mActivity.findViewById(R.id.main_layout);
    }
}
