package com.android.settings.network.connectivity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ConnectionLogic {
    private static final String TAG = "Connection";
    protected static final int NORMAL = 0;
    protected static final int NO_ETHERNET_ERR_CODE = 10000;
    protected static final int NO_WIFI_ERR_CODE = 10001;
    protected static final int LOCAL_NETWORK_CONN_ERR_CODE = 10002;
    protected static final int GATEWAY_ERR_CODE = 93;
    public static boolean ProgressSign=false;
    
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
    //private Thread mConnectionThread;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case NETWORK_CONN_NORMAL:
                mConnectionListener.onStatusChanged(NETWORK_CONN_NORMAL, false,
                        NORMAL);
                break;
            case GATEWAY_NORMAL:
                mConnectionListener.onStatusChanged(GATEWAY_NORMAL, false,
                        NORMAL);
                break;
            case CENTER_NETWORK_NORMAL:
                mConnectionListener.onStatusChanged(CENTER_NETWORK_NORMAL,
                        false, NORMAL);
                break;
            case TIME_SERVER_NORMAL:
                mConnectionListener.onStatusChanged(TIME_SERVER_NORMAL, true,
                        NORMAL);
                break;
            case NETWORK_CONN_ABNORMAL:
                mConnectionListener.onStatusChanged(NETWORK_CONN_ABNORMAL,
                        false, LOCAL_NETWORK_CONN_ERR_CODE);
                break;
            case GATEWAY_ABNORMAL:
                mConnectionListener.onStatusChanged(GATEWAY_ABNORMAL, false,
                        GATEWAY_ERR_CODE);
                break;
            case CENTER_NETWORK_ABNORMAL:
                mConnectionListener.onStatusChanged(CENTER_NETWORK_ABNORMAL,
                        false, CENTER_NETWORK_ERR_CODE);
                break;
            case TIME_SERVER_ABNORMAL:
                mConnectionListener.onStatusChanged(TIME_SERVER_ABNORMAL,
                        false, TIME_SERVER_ERR_CODE);
                break;
            }
        }
    };

    private class ConnectionThread implements Runnable {
        public void run() {
            NetworkUtil networkUtil=NetworkUtil.getInstance(mContext);
            if (networkUtil.isNetworkConnected()) {
                mHandler.sendEmptyMessageDelayed(NETWORK_CONN_NORMAL, 0);
                while(!ProgressSign){
                    continue;
                }
                ProgressSign=false;
                if (networkUtil.isReachable(networkUtil.getGateWay())) {
                    mHandler.sendEmptyMessageDelayed(GATEWAY_NORMAL, 0);
                    while(!ProgressSign){
                        continue;
                    }
                    ProgressSign=false;
                    if (networkUtil.isReachable(CENTER_NETWORK_ORG)) {
                        mHandler.sendEmptyMessageDelayed(CENTER_NETWORK_NORMAL,
                                0);
                        while(!ProgressSign){
                                    continue;
                                }
                        ProgressSign=false;
                        if (networkUtil.isReachable(NTP_SERVER_NAME)) {
                            mHandler.sendEmptyMessageDelayed(
                                    TIME_SERVER_NORMAL, 0);
                        } else {
                            mHandler.sendEmptyMessageDelayed(
                                    TIME_SERVER_ABNORMAL, 0);
                        }
                    } else {
                        mHandler.sendEmptyMessageDelayed(
                                CENTER_NETWORK_ABNORMAL, 0);
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

    private void finish() {
    }
    
    public void isProgressFinish(boolean progress){
        this.ProgressSign=progress;
    }
}
