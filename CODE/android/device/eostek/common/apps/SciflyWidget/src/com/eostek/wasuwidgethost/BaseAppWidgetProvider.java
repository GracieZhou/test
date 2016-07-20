
package com.eostek.wasuwidgethost;

import java.util.ArrayList;
import java.util.List;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * projectName： WasuWidgetHost.
 * moduleName： BaseAppWidgetProvider.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-8-28 8:16:18 pm
 * @Copyright © 2014 Eos Inc.
 */

public class BaseAppWidgetProvider extends AppWidgetProvider {

    private static final String TAG = BaseAppWidgetProvider.class.getSimpleName();

    protected Context mContext;

    protected AppWidgetManager mAppWidgetManager;

    protected AppWidgetHost mAppWidgetHost;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        mAppWidgetManager = AppWidgetManager.getInstance(context);
        mAppWidgetHost = new AppWidgetHost(context, 2048);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * clear the former widget.
     * @param clsName
     */
    protected final void clearFormerWidget(String clsName) {
        // before add new widget ,remove the former widgets
        AppWidgetProviderInfo providerInfo = new AppWidgetProviderInfo();
        List<AppWidgetProviderInfo> appWidgetInfos = new ArrayList<AppWidgetProviderInfo>();
        appWidgetInfos = mAppWidgetManager.getInstalledProviders();
        for (int j = 0; j < appWidgetInfos.size(); j++) {
            providerInfo = appWidgetInfos.get(j);
            if (providerInfo.provider.getClassName().equals(clsName)) {
                int[] ids = mAppWidgetManager.getAppWidgetIds(providerInfo.provider);
                Log.v(clsName, "ids = " + ids.length);
                // just to make sure there is only one widget instance exists
                if (ids.length > 1) {
                    for (int i = 0; i < ids.length - 1; i++) {
                        mAppWidgetHost.deleteAppWidgetId(ids[i]);
                        Log.d(TAG, "deleteAppWidgetId id = " + ids[i]);
                    }
                }
            }
        }
    }

}
