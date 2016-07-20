
package com.eostek.scifly.ime.french;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.eostek.scifly.ime.AbstractIME;

public class HomeWatcher {

    static final String TAG = "HomeWatcher";

    private AbstractIME mContext;

    private IntentFilter mFilter;

    private IntentFilter mMFilter;

    private OnHomePressedListener mListener;

    private InnerRecevier mRecevier;

    private MenuReceiver mMReceiver;

    // 回调接口
    public interface OnHomePressedListener {
        public void onHomePressed();

        public void onHomeLongPressed();

        public void onMenuPressed();
    }

    public HomeWatcher(AbstractIME context) {
        mContext = context;
        mRecevier = new InnerRecevier();
        mMReceiver = new MenuReceiver();
      mMFilter = new IntentFilter("com.eostek.tv.OSDMENU");
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    /**
     * 设置监听
     * 
     * @param listener
     */
    public void setOnHomeOrMenuPressedListener(OnHomePressedListener listener) {
        mListener = listener;
    }

    /**
     * 开始监听，注册广播
     */
    public void startWatch() {
        if (mRecevier != null || mMReceiver != null) {
            mContext.registerReceiver(mRecevier, mFilter);
           mContext.registerReceiver(mMReceiver, mMFilter);
        }
    }

    /**
     * 停止监听，注销广播
     */
    public void stopWatch() {
//        FIXME
        if (mRecevier != null) {
            mContext.unregisterReceiver(mRecevier);
        }
        if (mMReceiver != null) {
            mContext.unregisterReceiver(mMReceiver);
        }
    }

    /**
     * 广播接收者
     */
    class InnerRecevier extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";

        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";

        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (mListener != null) {
                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                            // 短按home键
                            mListener.onHomePressed();
                        } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                            // 长按home键
                            mListener.onHomeLongPressed();
                        }
                    }
                }
            }
        }
    }

    class MenuReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.eostek.tv.OSDMENU")) {
                if (mListener != null) {
                    mListener.onMenuPressed();
                }
            }
        }
    }
}
