
package com.android.settings.network.connectivity;

import com.android.settings.util.Utils;

import scifly.middleware.network.EthernetManagerGlobal;
import scifly.middleware.network.IpConfig;
import scifly.middleware.network.StaticIpConfig;
import scifly.middleware.network.WifiManagerGlobal;
import scifly.util.Ping;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ConnectionLogic {
    private static final String TAG = "Connection";

    protected static final int NORMAL = 0;

    protected static final int NO_ETHERNET_ERR_CODE = 10000;

    protected static final int NO_WIFI_ERR_CODE = 10001;

    protected static final int LOCAL_NETWORK_CONN_ERR_CODE = 10002;

    protected static final int GATEWAY_ERR_CODE = 93;

    public static boolean ProgressSign = false;

    protected static final int CENTER_NETWORK_ERR_CODE = 10003;

    protected static final int TIME_SERVER_ERR_CODE = 10004;

    protected static final int UNKNOW_NETWORK_CONN_ERR_CODE = 10005;

    protected static final int TIME_SYNC_FAIL_ERR_CODE = 10006;

    protected static final int TIME_SYNC_WAIT_OVER_TIME_ERR_CODE = 10007;

    protected static final int CONN_CDN_SERVER_FAIL_ERR_CODE = 10008;

    protected static final int DOWNLOAD_FAIL_ERR_CODE = 10009;

    protected static final int NO_REQUEST_PAGE_ERR_CODE = 100010;

    protected static final int CANNOT_IDENTIFY_PAGE_ERR_CODE = 10011;

    protected static final int NETWORK_CONN_NORMAL = 1;

    protected static final int GATEWAY_NORMAL = 2;

    protected static final int CENTER_NETWORK_NORMAL = 3;

    protected static final int TIME_SERVER_NORMAL = 4;

    protected static final int NETWORK_CONN_ABNORMAL = 10;

    protected static final int GATEWAY_ABNORMAL = 20;

    protected static final int CENTER_NETWORK_ABNORMAL = 30;

    protected static final int TIME_SERVER_ABNORMAL = 40;

    private static final String CENTER_NETWORK_ORG = "www.baidu.com";

    private static final String NTP_SERVER_NAME = "1.cn.pool.ntp.org";

    private Context mContext;

    private ConnectionListener mConnectionListener;

    // private Thread mConnectionThread;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NETWORK_CONN_NORMAL:
                    mConnectionListener.onStatusChanged(NETWORK_CONN_NORMAL, false, NORMAL);
                    break;
                case GATEWAY_NORMAL:
                    mConnectionListener.onStatusChanged(GATEWAY_NORMAL, false, NORMAL);
                    break;
                case CENTER_NETWORK_NORMAL:
                    mConnectionListener.onStatusChanged(CENTER_NETWORK_NORMAL, false, NORMAL);
                    break;
                case TIME_SERVER_NORMAL:
                    mConnectionListener.onStatusChanged(TIME_SERVER_NORMAL, true, NORMAL);
                    break;
                case NETWORK_CONN_ABNORMAL:
                    mConnectionListener.onStatusChanged(NETWORK_CONN_ABNORMAL, false, LOCAL_NETWORK_CONN_ERR_CODE);
                    break;
                case GATEWAY_ABNORMAL:
                    mConnectionListener.onStatusChanged(GATEWAY_ABNORMAL, false, GATEWAY_ERR_CODE);
                    break;
                case CENTER_NETWORK_ABNORMAL:
                    mConnectionListener.onStatusChanged(CENTER_NETWORK_ABNORMAL, false, CENTER_NETWORK_ERR_CODE);
                    break;
                case TIME_SERVER_ABNORMAL:
                    mConnectionListener.onStatusChanged(TIME_SERVER_ABNORMAL, false, TIME_SERVER_ERR_CODE);
                    break;
            }
        }
    };

    protected boolean isNetworkConnected() {
        ConnectivityManager mConnManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ( Utils.isPppoeConnected(mContext)) {
            return true;
        }else {
            if (null == mConnManager.getActiveNetworkInfo()) {
                return false;
            }
        }
        return mConnManager.getActiveNetworkInfo().isConnected();
    }

    public boolean isReachable(String hostip) {
        Ping ping = new Ping(hostip);
        return ping.run();
    }

    private class ConnectionThread implements Runnable {
        public void run() {
            if (isNetworkConnected()) {
                mHandler.sendEmptyMessageDelayed(NETWORK_CONN_NORMAL, 0);
                while (!ProgressSign) {
                    continue;
                }
                ProgressSign = false;
                if (isReachable(getGateway())|| Utils.isPppoeConnected(mContext)) {
                    mHandler.sendEmptyMessageDelayed(GATEWAY_NORMAL, 0);
                    while (!ProgressSign) {
                        continue;
                    }
                    ProgressSign = false;
                    if (isReachable(CENTER_NETWORK_ORG)) {
                        mHandler.sendEmptyMessageDelayed(CENTER_NETWORK_NORMAL, 0);
                        while (!ProgressSign) {
                            continue;
                        }
                        ProgressSign = false;
                        if (isReachable(NTP_SERVER_NAME)) {
                            mHandler.sendEmptyMessageDelayed(TIME_SERVER_NORMAL, 0);
                        } else {
                            mHandler.sendEmptyMessageDelayed(TIME_SERVER_ABNORMAL, 0);
                        }
                    } else {
                        mHandler.sendEmptyMessageDelayed(CENTER_NETWORK_ABNORMAL, 0);
                    }
                } else {
                    mHandler.sendEmptyMessageDelayed(GATEWAY_ABNORMAL, 0);
                }
            } else {
                mHandler.sendEmptyMessageDelayed(NETWORK_CONN_ABNORMAL, 0);
            }
        }
    };

    public ConnectionLogic(Context context) {
        this.mContext = context;
    }

    public void startCheckConnection(ConnectionListener listener) {
        mConnectionListener = listener;

        ConnectionThread runnable = new ConnectionThread();
        new Thread(runnable).start();
    }

    public void isProgressFinish(boolean progress) {
        ProgressSign = progress;
    }

    protected IpConfig getIpConfig() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (ConnectivityManager.TYPE_ETHERNET == networkInfo.getType()) {
                EthernetManagerGlobal global = new EthernetManagerGlobal(mContext);
                Log.d(TAG, "network type : " + networkInfo.getType());
                Log.d(TAG, "global : " + global.getConfiguration());
                return global.getConfiguration();
            } else if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
                WifiManagerGlobal global = new WifiManagerGlobal(mContext);
                Log.d(TAG, "global : " + global.getConfiguration());
                return global.getConfiguration();
            }
        }else if ( Utils.isPppoeConnected(mContext)) {
            EthernetManagerGlobal global = new EthernetManagerGlobal(mContext);
            return global.getConfiguration();
        }

        return null;
    }

    private String getGateway() {
        IpConfig ipConfig = getIpConfig();
        StaticIpConfig staticIpConfig = ipConfig == null ? null : ipConfig.getStaticIpConfig();
        if (staticIpConfig != null && staticIpConfig.gateway != null) {
            return staticIpConfig.gateway.getHostAddress();
        }

        return "";
    }
}
