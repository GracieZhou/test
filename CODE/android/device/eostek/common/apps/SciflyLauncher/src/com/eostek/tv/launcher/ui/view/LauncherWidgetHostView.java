
package com.eostek.tv.launcher.ui.view;


import com.eostek.tv.launcher.R;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/*
 * projectName： TVLauncher
 * moduleName： LauncherWidgetHostView.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-8-28 上午10:17:58
 * @Copyright © 2014 Eos Inc.
 */

public class LauncherWidgetHostView extends AppWidgetHostView {

    private LayoutInflater mInflater;

    private Context mContext;

    /**
     * @param context
     */
    public LauncherWidgetHostView(Context context) {
        super(context);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected View getErrorView() {
        return mInflater.inflate(R.layout.appwidget_error, this, false);
    }

}
