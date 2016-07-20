package com.eostek.wasuwidgethost.util;

import scifly.provider.SciflyStore;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * projectName： WasuWidgetHost.
 * moduleName： SettingsObserver.java
 * @author Admin
 * @function monitor city change.
 */
public class SettingsObserver extends ContentObserver {

    private Context mContext;
    private ContentResolver resolver;
    private Handler mHandler;
    private static final int MSG_CITY_CHANGE = 101;
    private static final int NUM_CHANGED = 102;

    /**
     * construction method.
     * @param handler
     * @param context
     */
    public SettingsObserver(Handler handler, Context context) {
        super(handler);
        this.mContext = context;
        this.mHandler = handler;
    }

    /**
     * register content observer.
     * @param context
     */
    public final void observe(Context context) {
        resolver = context.getContentResolver();
        resolver.registerContentObserver(Uri.parse("content://com.eostek.scifly.provider/global"), true, this);
    }

    /**
     * unregister content observer.
     */
    public final void unregisterContentObserver() {
        resolver.unregisterContentObserver(this);
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.v("change", "*******");
        mHandler.sendEmptyMessage(MSG_CITY_CHANGE);

        String msgNum = SciflyStore.Global.getString(mContext.getContentResolver(), SciflyStore.Global.MESSAGE_STATE);
        Message msg = Message.obtain();
        msg.what = NUM_CHANGED;
        msg.obj = msgNum;
        mHandler.sendMessage(msg);
    }
}
