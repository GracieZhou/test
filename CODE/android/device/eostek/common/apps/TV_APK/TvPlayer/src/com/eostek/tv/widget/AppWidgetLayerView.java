
package com.eostek.tv.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

public class AppWidgetLayerView extends FrameLayout {

    private static final String TAG = "AppWidgetLayerView";

    public static final String META_DATA_APPWIDGET_CATEGORY = "android.appwidget.category";

    public static final String APPWIDGET_CATEGORY_TV = "tv";

    public static final String PROVIDE_APPWIDGET_PERMISSION = "com.eostek.tv.player.permission.PROVIDE_APPWIDGET";

    private static final int APPWIDGET_HOST_ID = 0;

    private AppWidgetManager mAppWidgetManager;

    private AppWidgetHost mAppWidgetHost;

    private View mPreviouslyFocusedChild;

    private static Integer nextAppWidgetHostId = Integer.valueOf(0);

    private HashMap<String, AppWidgetHostView> mAppwidgets = new HashMap<String, AppWidgetHostView>();

    public AppWidgetLayerView(Context context) {
        this(context, loadTvAppWidgetList(context));
    }

    private AppWidgetLayerView(Context context, List<AppWidgetProviderInfo> appWidgets) {
        super(context);
        Log.e(TAG, "the widget total count:" + appWidgets.size());
        synchronized (nextAppWidgetHostId) {
            mAppWidgetManager = AppWidgetManager.getInstance(getContext());
            mAppWidgetHost = new AppWidgetHost(getContext(), APPWIDGET_HOST_ID);
            nextAppWidgetHostId = Integer.valueOf(1 + nextAppWidgetHostId.intValue());
            for (AppWidgetProviderInfo info : appWidgets) {
                int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
                mAppWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, info.provider);
                AppWidgetHostView widget = mAppWidgetHost.createView(getContext(), appWidgetId,
                        info);
                addView(widget, new FrameLayout.LayoutParams(-1, -1));
                mAppwidgets.put(info.provider.getClassName(), widget);
            }
        }
        setVisibility(GONE);
    }

    private static List<AppWidgetProviderInfo> loadTvAppWidgetList(Context context) {
        List<AppWidgetProviderInfo> appWidgets = AppWidgetManager.getInstance(context)
                .getInstalledProviders();
        appWidgets = filterAppWidgetsByMetadata(context, appWidgets, META_DATA_APPWIDGET_CATEGORY,
                APPWIDGET_CATEGORY_TV);
//        appWidgets = filterAppWidgetsByPermission(context, appWidgets, PROVIDE_APPWIDGET_PERMISSION);
        return appWidgets;
    }

    private static List<AppWidgetProviderInfo> filterAppWidgetsByMetadata(Context context,
            List<AppWidgetProviderInfo> appWidgets, String metaDataKey, String metaDataValue) {
        PackageManager pm = context.getPackageManager();
        List<AppWidgetProviderInfo> filteredAppWidgets = new ArrayList<AppWidgetProviderInfo>();
        for (AppWidgetProviderInfo appWidget : appWidgets) {
            try {
                ActivityInfo activityInfo = pm.getReceiverInfo(appWidget.provider,
                        PackageManager.GET_META_DATA);
                if (activityInfo.metaData != null) {
                    String value = activityInfo.metaData.getString(metaDataKey);
                    if ((value == metaDataValue)
                            || ((value != null) && (value.equals(metaDataValue)))) {
                        filteredAppWidgets.add(appWidget);
                    }
                }
            } catch (NameNotFoundException e) {
                Log.w(TAG, "Unable to locate app widget provider", e);
            }
        }
        return filteredAppWidgets;
    }

    @SuppressWarnings("unused")
    private static List<AppWidgetProviderInfo> filterAppWidgetsByPermission(Context context,
            List<AppWidgetProviderInfo> appWidgets, String requiredPermission) {
        List<AppWidgetProviderInfo> filteredAppWidgets = new ArrayList<AppWidgetProviderInfo>();
        for (AppWidgetProviderInfo appWidget : appWidgets) {
            if (appWidget.provider.getPackageName().equals(context.getPackageName())) {
                filteredAppWidgets.add(appWidget);
            }
        }
        return filteredAppWidgets;
    }

    public void toggleVisibility() {
        if (getVisibility() == VISIBLE) {
            mPreviouslyFocusedChild = findFocus();
            startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
            if (mPreviouslyFocusedChild != null
                    && mPreviouslyFocusedChild.getVisibility() == VISIBLE) {
                mPreviouslyFocusedChild.requestFocus();
            } else {
                requestFocus();
            }
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            mAppWidgetHost.startListening();
        } else {
            mAppWidgetHost.stopListening();
        }
    }

    public boolean shouldReceiveKey(int keyCode, KeyEvent event) {
        if (getVisibility() != VISIBLE) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return true;
            default:
                return false;
        }
    }

    public void onDestroy() {
        mAppWidgetHost.deleteHost();
    }
}
