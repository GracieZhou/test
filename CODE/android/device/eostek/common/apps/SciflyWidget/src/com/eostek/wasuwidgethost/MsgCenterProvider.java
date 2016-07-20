package com.eostek.wasuwidgethost;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import com.eostek.scifly.widget.R;
import com.eostek.tm.cpe.manager.CpeManager;
import com.eostek.wasuwidgethost.util.SettingsObserver;
import com.eostek.wasuwidgethost.util.Utils;

/**
 * projectName：WasuWidgetHost.
 * moduleName： MsgCenterProvider.java
 * 
 * @author Vicent
 * 
 */
public class MsgCenterProvider extends BaseAppWidgetProvider {

    private static final String TAG = "MsgCenterProvider";

    private static Context mContext;

    private static final int NUM_CHANGED = 102;

    private static final String CLICK_NAME_ACTION = "com.eostek.wasuwidgethost.MsgCenterProvider";

    private SettingsObserver observer = null;

    private String msgNum;

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case NUM_CHANGED:
                    msgNum = (String) msg.obj;
                    Log.d(TAG, "msgnum  " + msgNum);
                    updateWidget();
                    Log.d(TAG, "handler");
                    break;

                default:
                    break;
            }
        };
    };

    private static final String CPE_STAT_ACTION = "com.eostek.cpestatus";

    private static String status = "";

    @Override
    public void onEnabled(Context context) {
        // TODO Auto-generated method stub
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        final String action = intent.getAction();
        Log.d(TAG, "OnReceive :Action: " + action);
        mContext = context; 
        // handle action
        if (action.equals(CPE_STAT_ACTION)) {
            status = CpeManager.getInstance().getCpeStatus();
            Log.i(TAG, "status" + status);
            if (status.equals("1")) {
                // 在线
                updateWidget();
            } else if (status.equals("0")) {
                // 不在线
                updateWidget();
            }
        } else if (action.equals(CLICK_NAME_ACTION)) {
            String pkgName = "com.eostek.scifly.messagecenter";
            String clsName = "com.eostek.scifly.messagecenter.MainActivity";
            boolean isInstalled = Utils.isApkInstalled(mContext, pkgName, clsName);
            if (isInstalled) {
                Utils.startApp(mContext, pkgName, clsName);
            } else {
                Log.e(TAG, "apk not installed");
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // TODO Auto-generated method stub
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        mContext = context;
        clearFormerWidget(MsgCenterProvider.class.getName());
       if (observer == null) {
            observer = new SettingsObserver(mHandler, mContext);
            observer.observe(context);
        }

        updateWidget();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // TODO Auto-generated method stub
        super.onDeleted(context, appWidgetIds);
        if (observer != null) {
            observer.unregisterContentObserver();
        }
    }

    @Override
    public void onDisabled(Context context) {
        // TODO Auto-generated method stub
        super.onDisabled(context);

    }

    private void updateWidget() {
        RemoteViews mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.msgcenter_ui);
        ComponentName provider = new ComponentName(mContext, MsgCenterProvider.class);

        if (status.equals("1")) {
            // status is online.
            mRemoteViews.setImageViewResource(R.id.iv_msg, R.drawable.widget_msg_normal2);
            if (msgNum == null) {
                mRemoteViews.setTextViewText(R.id.tv_num, "0");
                mRemoteViews.setTextViewText(R.id.tv_plus, "");
            } else {
                if (Integer.parseInt(msgNum) > 99) {
                    mRemoteViews.setTextViewText(R.id.tv_num, "99");
                    mRemoteViews.setTextViewText(R.id.tv_plus, "+");
                } else {
                    mRemoteViews.setTextViewText(R.id.tv_num, msgNum);
                    mRemoteViews.setTextViewText(R.id.tv_plus, "");
                }
            }
        } else {
            // offline
            mRemoteViews.setImageViewResource(R.id.iv_msg, R.drawable.widget_msg_disable);
            mRemoteViews.setTextViewText(R.id.tv_num, "");
            mRemoteViews.setTextViewText(R.id.tv_plus, "");
        }

        AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(mContext);
        mAppWidgetManager.updateAppWidget(provider, mRemoteViews);
    }

}
