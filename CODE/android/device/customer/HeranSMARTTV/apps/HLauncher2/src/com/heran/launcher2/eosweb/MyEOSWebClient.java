
package com.heran.launcher2.eosweb;

import com.heran.launcher2.smalltv.SmallTvFragment;
import com.mstar.android.MKeyEvent;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkProperties;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.EOSWebClient;
import android.webkit.EOSWebView;
import android.webkit.WebView;
import scifly.provider.SciflyStore;
import scifly.provider.metadata.Footprint;
import scifly.provider.metadata.Usr;

/**
 * projectName： EosLauncher moduleName： MyEOSWebClient.java
 * 
 * @author fenoss.hu
 * @version 1.0.0
 * @time 2014-1-22 下午2:10:03
 * @Copyright © 2014 Eos Inc.
 */
public class MyEOSWebClient extends EOSWebClient {

    private Activity mContext;

    private WebView mWebView = null;

    private SmallTvFragment mHomeFragment = null;

    private final ConnectivityManager mConnManager;

    public MyEOSWebClient(Activity mContext, EOSWebView mWebView) {
        this(mContext, mWebView, null);
    }

    public MyEOSWebClient(Activity mContext, EOSWebView mWebView, SmallTvFragment mHomeFragment) {
        this.mContext = mContext;
        this.mWebView = mWebView;
        this.mHomeFragment = mHomeFragment;
        mWebView.initEosWebview();
        mWebView.setFocusStyle("#ff6699", 0);
        mConnManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public boolean handerKey(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent intent = null;
            switch (keyCode) {
                case MKeyEvent.KEYCODE_MSTAR_REVEAL:
                    intent = new Intent("com.eos.android.intent.action.screenballot");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    return false;
                case MKeyEvent.KEYCODE_SUBTITLE:
                    intent = new Intent(Intent.ACTION_MAIN, null);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    intent.putExtra("gotolanucher", "heran");
                    mContext.startActivity(intent);
                    return false;
                case MKeyEvent.KEYCODE_MSTAR_UPDATE:
                    intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    return false;
                case KeyEvent.KEYCODE_PROG_GREEN:
                    mContext.startService(new Intent("qingyu.TaskSwitch.launch.runningtask"));
                    return false;
                case MKeyEvent.KEYCODE_FREEZE:
                    startApk("com.android.browser", "com.android.browser.BrowserActivity", null);
                    return false;
                default:
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean jowinlogin(String usrname, String hy, String coin) {
        Usr user = new Usr();
        user.mName = usrname;
        user.mBonus = Long.parseLong(hy);
        user.mCoin = Integer.parseInt(coin);
        SciflyStore.User.putUser(mContext.getContentResolver(), user);
        return true;
    }

    @Override
    public boolean jowinlogin(int id, String usrname, String hy, String coin) {
        Usr user = new Usr();
        user.mId = id;
        user.mName = usrname;
        user.mBonus = Long.parseLong(hy);
        user.mCoin = Integer.parseInt(coin);
        SciflyStore.User.putUser(mContext.getContentResolver(), user);
        return true;
    }

    @Override
    public boolean jowinout(String usrname) {
        Usr user = new Usr();
        user.mName = "Guest";
        user.mBonus = 0;
        user.mCoin = 0;
        SciflyStore.User.putUser(mContext.getContentResolver(), user);
        return true;
    }

    @Override
    public void launcherPageLeft() {
        // getvodPlay();
    }

    @Override
    public void launcherPageRight() {
        super.launcherPageRight();
    }

    @Override
    public String getEthAddr() {
        String mac = "00:88:88:00:00:01";
        try {
            mac = TvManager.getInstance().getEnvironment("ethaddr");
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return mac;
    }

    @Override
    public String getIpAddr() {
        String ipAddress = "";
        if (getNetworkTypeName().equals("WIFI")) {
            WifiManager wifiManager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            ipAddress = Formatter.formatIpAddress(dhcpInfo.ipAddress);
        } else if (getNetworkTypeName().equals("ETHERNET")) {
            LinkProperties linkProperties = mConnManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
            if (null == linkProperties) {
                return "0.0.0.0";
            }
            String ipStr = linkProperties.getAddresses().toString();
            ipAddress = ipStr.substring(2, ipStr.length() - 2);
        }

        return ipAddress;
    }

    @Override
    public void setUsrInfo(String user, String ip, String log, String time) {
        Log.d("ShopFragment", "====>=user" + user + "ip=" + ip + "log=" + log + "time=" + time);
        Log.d("ShopFragment", "setUsrInfo start");
        Footprint item = new Footprint();
        item.mData = "com.provider.test";
        item.mCategory = SciflyStore.Footprints.CATEGORY_USER_LOGIN;
        // item.mTime = Calendar.getInstance().getTimeInMillis();
        Log.d("ShopFragment", "setUsrInfo end");
        item.mUser = "Mandy";
        item.mRemark = "login out";
        item.mReserve = "172.23.67.188";
        item.mTitle = "xxx";
        boolean flag = SciflyStore.Footprints.putFootprints(mContext.getContentResolver(), item);
        Log.d("ShopFragment", "setUsrInfo:" + flag);
    }

    @Override
    public void setValue(String key, String value) {
        super.setValue(key, value);
    }

    @Override
    public String getUser() {
        Usr user = SciflyStore.User.getUser(mContext.getContentResolver());
        if (user != null) {
            if (user.mName == null) {
                user.mName = "Guest";
            }
        } else {
            user = new Usr();
            user.mName = "Guest";
        }
        return user.mName;
    }

    @Override
    public String getValue(String key) {
        return super.getValue(key);
    }

    @Override
    public void clearWebViewFocus() {
        Log.d("MyEOSWebClient", "clearWebViewFocus");
        if (mHomeFragment != null) {
            mHomeFragment.backCurrentSource();
        }
    }

    @Override
    public void requestWebViewFocus() {
        Log.d("MyEOSWebClient", "requestWebViewFocus");
        if (mHomeFragment != null) {
            handlerWebview.postDelayed(requestFocus_thread, 1000);
        }
    }

    @Override
    public void AppStoreBackEvent() {
        Log.d("MyEOSWebClient", "AppStoreBackEvent +++");
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                    Log.d("MyEOSWebClient", "AppStoreBackEvent goBack");
                } else {
                    mContext.finish();
                }
            }
        });
        super.AppStoreBackEvent();
    }

    @Override
    public void cleanWebViewHistory(String s) {
        Log.d("MyEOSWebClient", "cleanWebViewHistory +++");
        if (mWebView != null) {
            // all webview method should called in the same thread
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.clearHistory();
                }
            });
        }
        super.cleanWebViewHistory(s);
    }

    @Override
    public String getHeranFn(String fun, String value) {
        return super.getHeranFn(fun, value);
    }

    @Override
    public String getHeranFn1(int i, int j, int k) {
        return super.getHeranFn1(i, j, k);
    }

    @Override
    public int getHeranFn2(int i, int j, int k) {
        if (i == 1) {
            mContext.finish();
            return 1;
        }

        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            mContext.finish();
        }
        return super.getHeranFn2(i, j, k);
    }

    // handlertv postDelayed chanage source
    Handler handlerWebview = new Handler();

    Runnable requestFocus_thread = new Runnable() {
        @Override
        public void run() {
            mWebView.setFocusable(true);
            mWebView.requestFocus();
        }
    };

    /**
     * start an application
     * 
     * @param pckName PackageName
     * @param clsName ClassName
     * @param bundle additional parameters, options
     */
    public void startApk(String pckName, String clsName, Bundle bundle) {
        ComponentName componentName = new ComponentName(pckName, clsName);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        mContext.startActivity(intent);
    }

    @SuppressLint("Recycle")
    public boolean getvodPlay() {
        Instrumentation inst = new Instrumentation();
        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);
        return true;
    }

    private String getNetworkTypeName() {
        return mConnManager.getActiveNetworkInfo().getTypeName();

    }

}
