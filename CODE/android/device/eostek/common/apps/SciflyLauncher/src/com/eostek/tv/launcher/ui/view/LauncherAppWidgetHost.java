
package com.eostek.tv.launcher.ui.view;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;

/*
 * projectName： TVLauncher
 * moduleName： LauncherAppWidgetHost.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-8-28 上午10:19:29
 * @Copyright © 2014 Eos Inc.
 */

public class LauncherAppWidgetHost extends AppWidgetHost {

    /**
     * @param context
     * @param hostId
     */
    public LauncherAppWidgetHost(Context context, int hostId) {
        super(context, hostId);
    }

    @Override
    protected AppWidgetHostView onCreateView(Context context, int appWidgetId, AppWidgetProviderInfo appWidget) {
        return new LauncherWidgetHostView(context);
    }

    @Override
    public void stopListening() {
        super.stopListening();
        clearViews();
    }

}
