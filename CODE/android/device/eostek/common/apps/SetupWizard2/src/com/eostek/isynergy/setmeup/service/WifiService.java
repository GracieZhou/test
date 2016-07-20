
package com.eostek.isynergy.setmeup.service;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.common.utils.activity.ActivityUtils;
import com.eostek.isynergy.setmeup.R;
import com.eostek.isynergy.setmeup.common.Constants;
import com.eostek.isynergy.setmeup.common.FileUtils;
import com.eostek.isynergy.setmeup.common.Constants.ACTION_TYPE;
import com.eostek.isynergy.setmeup.common.comp.FloatWindow;
import com.eostek.isynergy.setmeup.config.ServiceManager;
import com.eostek.isynergy.setmeup.config.external.ExternalServiceManager;
import com.eostek.isynergy.setmeup.config.nickname.DevNameService;
import com.eostek.isynergy.setmeup.jni.JNIImpl;
import com.eostek.isynergy.setmeup.listener.ServiceListener;
import com.eostek.isynergy.setmeup.listener.ServiceListenerImpl;
import com.eostek.isynergy.setmeup.ui.SetmeupMainActivity;

/**
 * 与wifi相关的服务
 * 
 * @author nickyang
 */

public class WifiService extends Service {

    private final String TAG = "SetmeupWifiService";

    private final int MES_COUNTER_SHOW = 0;

    private final int MES_COUNTER_REFRESH = 1;

    private final int MES_COUNTER_STOP = 2;

    /**
     * 服务管理，可以从这里获取本应用的所有服务
     */
    private ServiceManager serManager;

    /**
     * 所有的JNi 接口都封装在这个类中
     */
    private JNIImpl jni;

    /**
     * 监听所有native的回调
     */
    private ServiceListener listener;

    private SwitchNetWorkTask task = null;

    private NetworkMonitor nwMonitor = new NetworkMonitor();

    private Thread connTHread;

    private Object connLock = new Object();

    private ConnNetworkListener connListener = new ConnNetworkListener();

    private boolean isJNIStartup = false;

    private HandlerThread jniThread;

    private Handler jniHandler;

    private JNITask jniTask;

    private FloatWindow fw;

    private long lastLongPress = 0;

    // 最大间隔ms
    private long internalLongPress = 300;

    @SuppressLint("HandlerLeak")
    private Handler counterHandler = new Handler() {
        private int MAX_COUNTER = 5;

        public void handleMessage(Message mes) {
            int what = mes.what;
            if (what == MES_COUNTER_SHOW) {
                MAX_COUNTER = 5;
                showCounter();

            } else if (what == MES_COUNTER_REFRESH) {
                if (MAX_COUNTER > 0) {
                    if (!isLongPressRelease()) {
                        refreshCounter(MAX_COUNTER--);
                        sendEmptyMessageDelayed(MES_COUNTER_REFRESH, 1000);
                    } else {
                        sendEmptyMessage(MES_COUNTER_STOP);
                    }
                } else// 倒计时结束，开始reset操作
                {
                    task.setNetworkState(NETWORK_STATE.NETWORK_STATE_RESET);

                    synchronized (connLock) {
                        connLock.notifyAll();
                    }
                    sendEmptyMessage(MES_COUNTER_STOP);
                }

            } else if (what == MES_COUNTER_STOP) {
                MAX_COUNTER = 5;
                closeCounter();
            }
        }
    };

    enum NETWORK_STATE {
        NETWORK_STATE_CONNECED, NETWORK_STATE_DISCONNECTED, NETWORK_STATE_RESET;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Log.d(TAG, "onCreate");
        IntentFilter nscFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        nscFilter.addAction("CUSTOM_CONNECT_STATUS_CHANGED");
        nscFilter.addAction(Constants.ACTION_MODIFY_DEV_NAME);
        registerReceiver(nwMonitor, nscFilter);

        registResetReceiver();
        // 在网络切换成功的时候进行初始化
        if (jniThread == null) {
            jniThread = new HandlerThread("jniThread");
            jniThread.start();

            jniHandler = new Handler(jniThread.getLooper());
        }

        CreateTask createTask = new CreateTask();
        Thread thread = new Thread(createTask);
        thread.start();
        /************** 建立配置环境，将配置文件cp 到 /data/data/pn/app_config *******************/
        /*
         * try { initENV(); } catch (IOException e) { Toast.makeText(this,
         * getString(R.string.fail_setup_config), Toast.LENGTH_LONG).show();
         * return; }
         */

        /**************** 开启网络链接 *********************************************************/
        /*
         * if(connTHread == null && task == null) { //ConnectivityManager cm =
         * (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
         * //NetworkInfo activeNetwork = cm.getActiveNetworkInfo(); //boolean
         * networkIsConnect = (activeNetwork != null) &&
         * activeNetwork.isConnectedOrConnecting(); SharedPreferences sp =
         * getApplicationContext
         * ().getSharedPreferences(Constants.SP_SET_ME_UP,Context.MODE_PRIVATE);
         * String sValue = sp.getString(Constants.LAST_SETTING_WIFI_SSID, null);
         * if (null != sValue) { Log.d(TAG, "set wifi already : " + sValue);
         * //networkIsConnect = true; } task = new
         * SwitchNetWorkTask(connListener); if (null != sValue) {
         * task.setNetworkState(NETWORK_STATE.NETWORK_STATE_CONNECED); } else {
         * //SharedPreferences sp =
         * getApplicationContext().getSharedPreferences(
         * Constants.SP_SET_ME_UP,Context.MODE_PRIVATE); Editor editor =
         * sp.edit(); editor.putString(Constants.LAST_SETTING_WIFI_SSID,
         * "HAVESET"); editor.commit(); Log.d(TAG, "set SharedPreferences : " +
         * "HAVESET"); task.setNetworkState(NETWORK_STATE.NETWORK_STATE_RESET);
         * } connTHread = new Thread(task); connTHread.start(); }
         */
        /**************** 初始化JNI ***********************************************************/

    }

    class JNITask implements Runnable {

        private String devName;

        JNITask(String devName) {
            this.devName = devName;
        }

        @Override
        public synchronized void run() {
            if (jni != null && isJNIStartup) {
                jni.finalize();

                isJNIStartup = false;
            }

            String desFile = FileUtils.getDescriptionFilePath(getApplicationContext());
            if (desFile == null) {
                Log.d(TAG, "Failed to set device name...");
                return;
            }

            initJNI(devName, desFile);
        }

    }

    class CreateTask implements Runnable {
        @Override
        public synchronized void run() {
            /************** 建立配置环境，将配置文件cp 到 /data/data/pn/app_config *******************/
            Log.d(TAG, "CreateTask::current time in ms " + System.currentTimeMillis());
            try {
                initENV();
            } catch (IOException e) {
                Log.e(TAG, "failed to init setmeup ");
                return;
            }

            Log.d(TAG, "CreateTask::current time in ms " + System.currentTimeMillis());
            /**************** 开启网络链接 *********************************************************/
            if (connTHread == null && task == null) {
                // ConnectivityManager cm = (ConnectivityManager)
                // getSystemService(Context.CONNECTIVITY_SERVICE);
                // NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                // boolean networkIsConnect = (activeNetwork != null) &&
                // activeNetwork.isConnectedOrConnecting();

                SharedPreferences sp = getApplicationContext().getSharedPreferences(Constants.SP_SET_ME_UP,
                        Context.MODE_PRIVATE);
                String sValue = sp.getString(Constants.LAST_SETTING_WIFI_SSID, null);
                if (null != sValue) {
                    Log.d(TAG, "set wifi already : " + sValue);
                    // networkIsConnect = true;
                }

                task = new SwitchNetWorkTask(connListener);
                if (null != sValue) {
                    task.setNetworkState(NETWORK_STATE.NETWORK_STATE_CONNECED);
                } else {
                    // SharedPreferences sp =
                    // getApplicationContext().getSharedPreferences(Constants.SP_SET_ME_UP,Context.MODE_PRIVATE);
                    Editor editor = sp.edit();
                    editor.putString(Constants.LAST_SETTING_WIFI_SSID, "HAVESET");
                    editor.commit();
                    Log.d(TAG, "set SharedPreferences : " + "HAVESET");
                    task.setNetworkState(NETWORK_STATE.NETWORK_STATE_RESET);
                }

                connTHread = new Thread(task);
                connTHread.start();
            }
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand...");
        // deal with calling back of setting
        if (intent != null) {
            int actionType = intent.getIntExtra(Constants.NAME_ACTION_TYPE, -1);

            Log.d(TAG, "actionType = " + actionType);

            if (actionType == Constants.ACTION_TYPE.DEVICE_NAME_SETTING.getValue()) {
                String devName = intent.getStringExtra(Constants.NAME_PARAMETER);
                onSettingDevName(actionType, devName);

            } else if (actionType == Constants.ACTION_TYPE.REQ_PAIRING_CODE.getValue()) {
                String pairingCode = intent.getStringExtra(Constants.NAME_PARAMETER);
                onReqPairingCode(actionType, pairingCode);

            } else if (actionType == Constants.ACTION_TYPE.TIMEZONE_SETTING.getValue()) {
                String timeZone = intent.getStringExtra(Constants.NAME_PARAMETER);
                onSettingTimeZone(actionType, timeZone);
            } else if (actionType == Constants.ACTION_TYPE.WIFI_SETTING.getValue()) {
                String wifi = intent.getStringExtra(Constants.NAME_PARAMETER);
                onSettingWifi(actionType, wifi);
            } else if (actionType == Constants.ACTION_TYPE.RESTORE_FACTORY.getValue()) {
                String para = intent.getStringExtra(Constants.NAME_PARAMETER);
                onRestoringFactory(actionType, para);
            } else if (actionType == Constants.ACTION_TYPE.ADNET_SETTING.getValue()) {
                String para = intent.getStringExtra(Constants.NAME_PARAMETER);
                onSettingAdNet(actionType, para);
            }

        }
        return START_STICKY;
    }

    private void onSettingDevName(int actionType, String devName) {
        String devName_ = devName;

        showPhrase(actionType, devName_);

        boolean isWifiApEnabled = ServiceManager.getInstance(getApplicationContext()).getWifiApService()
                .isWifiApEnabled();
        Log.d(TAG, "onSettingDevName::isWifiApEnabled = " + isWifiApEnabled);
        if (!isWifiApEnabled) {
            startJNI();
        }
    }

    private void onReqPairingCode(int actionType, String pairingCode) {
        Log.d(TAG, "onReqPairingCode pairingCode " + (pairingCode == null ? "" : pairingCode));

        String pairingCode_ = "";

        if (pairingCode == null || pairingCode.length() == 0) {
            return;
        }

        pairingCode_ = pairingCode;

        showPhrase(actionType, pairingCode_);
    }

    private void onSettingTimeZone(int actionType, String timeZone) {
        showPhrase(actionType, timeZone);
    }

    private void onRestoringFactory(int actionType, String para) {
        showPhrase(actionType, para);
    }

    private void onSettingAdNet(int actionType, String para) {
        showPhrase(actionType, para);
    }

    private void onSettingWifi(int actionType, String wifi) {
        showPhrase(actionType, wifi);
    }

    /**
     * 初始化SetMeUp JNI 库
     */
    private synchronized void initJNI(String name, String descriptionFile) {
        Log.d(TAG, "initJNI begin...");
        if (jni == null || listener == null) {
            jni = new JNIImpl();
            listener = new ServiceListenerImpl(getApplicationContext(), null);
        }

        if (!isJNIStartup) {
            jni.initialize(listener, name, descriptionFile);
            isJNIStartup = true;
        }
        Log.d(TAG, "initJNI end...");
    }

    /**
     * 初始化apk运行时配置文件
     * 
     * @throws IOException
     */
    private void initENV() throws IOException {
        File configFolderDest = getApplicationContext().getDir(Constants.CONFIG_CUSTOM_DIR, Context.MODE_PRIVATE);

        FileUtils.copyAssetFolder(getApplicationContext(), Constants.CONFIG_CUSTOM_DIR,
                configFolderDest.getAbsolutePath());

        File descriptionFile = getApplicationContext().getDir(Constants.CONFIG_CUSTOM_DIR, Context.MODE_PRIVATE);

        Runtime.getRuntime().exec("chmod -R 777 " + descriptionFile.getAbsolutePath());
    }

    @Override
    public void onDestroy() {
        if (jni != null) {
            jni.finalize();
        }

        if (task != null) {
            task.setStop(true);
            synchronized (connLock) {
                connLock.notifyAll();
            }
        }

        unregisterReceiver(nwMonitor);

        unregistResetReceiver();

        if (jniThread != null) {
            if (jniTask != null) {
                jniHandler.removeCallbacks(jniTask);
            }

            jniThread.quit();
            jniThread = null;
            jniHandler = null;
        }

        ExternalServiceManager.getInstance(getApplicationContext()).release();
    }

    /**
     * 决定是否启动wifi 还是 AP
     * 
     * @author nickyang
     */

    private boolean isConnecting = false;

    class SwitchNetWorkTask implements Runnable {
        private ConnNetworkListener listener;

        private final int MAX_NUM = 25;

        SwitchNetWorkTask(ConnNetworkListener listener) {
            this.listener = listener;
        }

        private NETWORK_STATE state;

        public void setNetworkState(NETWORK_STATE state) {
            Log.d(TAG, "set network state " + state);
            this.state = state;
        }

        private boolean isStop = false;

        public void setStop(boolean isStop) {
            this.isStop = isStop;
        }

        @Override
        public void run() {
            while (!isStop) {
                Log.d(TAG, "start to check time out...state=" + state + " isConnection=" + isConnecting);

                // [2014-05-27 raindy] 不处理WIFI网络断开情况
                if (NETWORK_STATE.NETWORK_STATE_CONNECED == this.state) {
                    Log.d(TAG, "network monitor: network state is conneced");
                    listener.onConnect(Constants.SuccessCode.CONNECT_TO_WIFI.getValue());
                    isConnecting = false;
                } else if (NETWORK_STATE.NETWORK_STATE_RESET == this.state) {
                    serManager = ServiceManager.getInstance(getApplicationContext());
                    int ret = serManager.doAction(ACTION_TYPE.SWITCH_NETWORK, null);
                    Log.d(TAG, "NETWORK_STATE_RESET of SWITCH_NETWORK is " + ret);
                    listener.onConnect(ret);
                    isConnecting = true;
                } else if (isConnecting) // 如果是reset网络或首次启动，检查网络连接状态，如果没有连接上网络，再次启动AP
                {
                    for (int i = 0; i <= MAX_NUM; i++) {
                        Log.d(TAG, "current network state " + this.state + " NETWORK_STATE_CONNECED is "
                                + NETWORK_STATE.NETWORK_STATE_CONNECED);
                        if (NETWORK_STATE.NETWORK_STATE_CONNECED == this.state) {
                            Log.d(TAG, "network monitor: network state is conneced");
                            // ConnNetworkListener listener = new
                            // ConnNetworkListener();
                            listener.onConnect(Constants.SuccessCode.CONNECT_TO_WIFI.getValue());
                            isConnecting = false;
                            break;
                        } else if (i == MAX_NUM && NETWORK_STATE.NETWORK_STATE_DISCONNECTED == this.state
                                && ActivityUtils.isActivityTop(getApplicationContext(), Constants.MAIN_ACTIVITY_NAME)) {
                            serManager = ServiceManager.getInstance(getApplicationContext());
                            int ret = serManager.doAction(ACTION_TYPE.SWITCH_NETWORK, null);
                            Log.d(TAG, "network state check timeout and Result of SWITCH_NETWORK is " + ret);
                            listener.onConnect(ret);
                        }

                        Log.d(TAG, "current loop index " + i);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    Log.d(TAG, "wait for network status change ...");
                    synchronized (connLock) {
                        connLock.wait();
                    }
                } catch (InterruptedException e) {

                }
            }
        }
    }

    class ConnNetworkListener {
        public void onConnect(int connResult) {
            if (Constants.SuccessCode.CONNECT_TO_WIFI.getValue() == connResult) {
                startJNI();
                Log.d(TAG, "start to hide the main activity ...");
                // ActivityUtils.hideActivity(getApplicationContext(),
                // Constants.MAIN_ACTIVITY_NAME);
                if (ActivityUtils.isActivityTop(getApplicationContext(), Constants.MAIN_ACTIVITY_NAME)) {
                    Log.d(TAG, "Finish setmeup main activity ...");
                    showPhrase(Constants.CONTROLLER_UI_FINISH, null);
                }
            } else if (Constants.SuccessCode.SET_UP_WIFI_AP.getValue() == connResult) {
                startJNI();

                SharedPreferences preferences = getSharedPreferences("count", MODE_WORLD_READABLE);

                int count = preferences.getInt("count", 0);
                int count_0 = count;
                Log.d("xiangy", "count = " + count);
                Editor editor = preferences.edit();
                editor.putInt("count", ++count_0);
                editor.commit();
                if (count != 0) {
                    Log.d("xiangy", "show  SetmeupMainActivity...");
                    showPhrase(Constants.CONTROLLER_UI_SHOW, null);
                }

            }
        }

    }

    /**
     * 如果jni已经初始化会先反初始化然后再次初始化，否则直接进行初始化操作
     */
    private void startJNI() {
        String devName = null;

        String desFile = FileUtils.getDescriptionFilePath(getApplicationContext());
        if (desFile == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.fail_setup_config), Toast.LENGTH_LONG).show();
            return;
        }

        DevNameService extDevNameService = ServiceManager.getInstance(getApplicationContext()).getDevNameService();

        devName = extDevNameService.getDevName();

        if (devName != null && devName.length() > 0) {
            jniTask = new JNITask(devName);
            jniHandler.post(jniTask);
        }
    }

    /**
     * 监控网络状态的变化
     * 
     * @author nickyang
     */
    class NetworkMonitor extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (task == null) {
                return;
            }

            String action = arg1.getAction();
            Log.d(TAG, "NetworkMonitor::action=" + action);
            if (ConnectivityManager.CONNECTIVITY_ACTION.equalsIgnoreCase(action))// 网络状态广播
            {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();

                if (info != null && info.isAvailable()) {
                    task.setNetworkState(NETWORK_STATE.NETWORK_STATE_CONNECED);
                    Log.d(TAG, "network is Available SharedPreferences putString " + Constants.LAST_SETTING_WIFI_SSID);
                } else {
                    task.setNetworkState(NETWORK_STATE.NETWORK_STATE_DISCONNECTED);
                }
                // [2014-05-27 raindy] 不处理WIFI网络断开情况
                Log.d(TAG, "notify all to deal network state change");
                synchronized (connLock) {
                    connLock.notifyAll();
                }
            } else if ("CUSTOM_CONNECT_STATUS_CHANGED".equals(action)) {
                isConnecting = true;
                task.setNetworkState(NETWORK_STATE.NETWORK_STATE_DISCONNECTED);
                Log.d(TAG, "notify all to deal network state change");
                synchronized (connLock) {
                    connLock.notifyAll();
                }
            } else if (Constants.ACTION_MODIFY_DEV_NAME.equals(action)) {
                DevNameService extDevNameService = ServiceManager.getInstance(getApplicationContext())
                        .getDevNameService();

                String devName = extDevNameService.getDevName();

                SharedPreferences sp = getApplicationContext().getSharedPreferences(Constants.SP_SET_ME_UP,
                        Context.MODE_PRIVATE);
                String lastDevName = sp.getString(Constants.LAST_SETTING_DEV_NAME, null);

                Log.d(TAG, "devName=" + (devName == null ? "" : devName) + "  lastDevName="
                        + (lastDevName == null ? "" : lastDevName));

                // 如果是由于setmeup的设置导致的广播，那么需要忽略
                if (!devName.equals(lastDevName)) {
                    startJNI();
                }
            }
        }
    }

    /**
     * 如果activity 在栈顶，那么将会将数据发送并且更新
     * 
     * @param type the value of action_type
     */
    private void showPhrase(int type, String para1) {
        Log.d(TAG, "showPhrase type=" + type + " para=" + para1);
        PackageManager pm = getPackageManager();
        ComponentName name = new ComponentName(this, SetmeupMainActivity.class);
        if (PackageManager.COMPONENT_ENABLED_STATE_ENABLED != pm.getComponentEnabledSetting(name)) {
            pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }

        Intent intent = new Intent(getApplicationContext(), SetmeupMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.NAME_ACTION_TYPE, type);
        if (para1 != null) {
            intent.putExtra(Constants.NAME_PARAMETER, para1);
        }

        getApplicationContext().startActivity(intent);
    }

    /**
     * [2014-05-27 raindy] Reset键广播接收注册
     */
    private void registResetReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_LONG_RESET_BUTTON);
        this.registerReceiver(resetBroadcastReceiver, intentFilter);
    }

    /**
     * [2014-05-27 raindy] Reset键广播接收反注册
     */
    private void unregistResetReceiver() {
        this.unregisterReceiver(resetBroadcastReceiver);
    }

    /**
     * [2014-05-27 raindy] Reset键广播接收处理
     */
    private BroadcastReceiver resetBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "resetBroadcastReceiver.........................");
            if (isLongPressRelease()) {
                counterHandler.sendEmptyMessage(MES_COUNTER_SHOW);
            }

            lastLongPress = System.currentTimeMillis();
        }
    };

    private void showCounter() {
        if (fw == null) {
            LayoutInflater factory = LayoutInflater.from(getApplicationContext());
            View warterMark = factory.inflate(R.layout.global_watermark, null);
            Log.d(TAG, "showCounter...");
            fw = new FloatWindow(getApplicationContext(), warterMark, false);
            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            int viewHeight = 400;
            int viewWidth = 400;
            int x = (metrics.widthPixels - viewWidth) / 2;
            int y = (metrics.heightPixels - viewHeight) / 2;

            fw.initPosition(x, y, viewWidth, viewHeight);
            fw.show();

            counterHandler.sendEmptyMessage(MES_COUNTER_REFRESH);
        }
    }

    private void refreshCounter(int counter) {
        if (fw != null) {
            Log.d(TAG, "refreshCounter...counter=" + counter);
            View warterMark = fw.getView();
            TextView tv = (TextView) warterMark.findViewById(R.id.text_counter);
            tv.setText(Integer.toString(counter));
            fw.updateView();
        }
    }

    private void closeCounter() {
        if (fw != null) {
            fw.getHandler().sendEmptyMessage(FloatWindow.WINDOW_CLOSE_FLAG);
        }

        counterHandler.removeMessages(MES_COUNTER_REFRESH);

        fw = null;
    }

    private boolean isLongPressRelease() {
        boolean isReleased = false;

        long currentTime = System.currentTimeMillis();

        Log.d(TAG, "consume " + Math.abs(currentTime - lastLongPress)
                + "ms to send long press broadcast and internalLongPress = " + internalLongPress);

        if (Math.abs(currentTime - lastLongPress) > internalLongPress) {
            isReleased = true;
        }

        return isReleased;
    }
}
