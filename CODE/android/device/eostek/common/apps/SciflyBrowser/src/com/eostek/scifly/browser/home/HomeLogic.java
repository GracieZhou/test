
package com.eostek.scifly.browser.home;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import scifly.datacache.DataCacheConfiguration;
import scifly.datacache.DataCacheListener;
import scifly.datacache.DataCacheManager;
import scifly.device.Device;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.browser.BrowserSettings;
import com.eostek.scifly.browser.BrowserActivity;
import com.eostek.scifly.browser.ConnChangedReceiver;
import com.eostek.scifly.browser.modle.UrlModle;
import com.eostek.scifly.browser.util.Constants;
import com.eostek.scifly.browser.util.UIUtil;
import com.eostek.tm.cpe.manager.CpeManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;

/**
 * projectName： Browser moduleName： HomeLogic.java
 * 
 * @author Shirley.jiang & Ahri.chen
 * @time 2016-1-27 
 */
public class HomeLogic {

    private static final String TAG = "HomeLogic";

    private BrowserActivity mActivity;

    private HomeHolder mHolder;

    private ConnChangedReceiver mConnReceiver;

    private List<UrlModle> mList;

    private Thread mThread = null;

    private DisplayImageOptions mOptions;

    private HomeAdapter mAdapter;

    public DataCacheManager mDataManager;

    private ImageLoader mImageLoader = ImageLoader.getInstance();

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_GET_UPDATE_DATA:
                    getSuggestData();
                    break;
                case Constants.MSG_GET_LOCAL_DATA:
                    getLocalData();
                    break;

                case Constants.MSG_UPDATE_UI:
                    refreshUI();
                    break;

                default:
                    break;
            }
        };
    };

    public HomeLogic(BrowserActivity activity) {
        mActivity = activity;
    }

    public void setHolder(HomeHolder holder) {
        mHolder = holder;
    }

    public List<UrlModle> getList() {
        return mList;
    }

    /**
     * int data.
     */
    public void initData() {
        mDataManager = DataCacheManager.getInstance();
        initDataCache();

        // to solve from collect page to homepage is slow.
        if (mList == null || mList.size() == 0) {
            mHandler.sendEmptyMessage(Constants.MSG_GET_LOCAL_DATA);
        }
        mConnReceiver.startAddTak(mActivity);
    }

    /**
     * init cache manager.
     */
    public void initDataCache() {
        File cachePath = new File(Constants.CACHE_PATH);
        if (!cachePath.exists()) {
            Log.d(TAG, "create cache path : " + cachePath.mkdirs());
        }

        DataCacheConfiguration config = new DataCacheConfiguration.Builder()
                .maxDiskCacheSize(Constants.TOTAL_CACHE_SIZE).diskCacheSize(Constants.SINGLE_CACHE_SIZE)
                .diskCacheDir(cachePath).threadPriority(Thread.MAX_PRIORITY).build();
        mDataManager.init(mActivity, config);
    }

    /**
     * register net Receiver
     */
    public void registerReceiver() {
        mConnReceiver = new ConnChangedReceiver(mActivity, mHandler);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mActivity.registerReceiver(mConnReceiver, intentFilter);
    }

    private void getSuggestData() {
        stopThread();
        if (mThread == null) {
            mThread = new Thread(new ParseJsonRunnable());
            mThread.start();
        }
    }

    private void getLocalData() {
        String localConfig = UIUtil.getStringFromFile(Constants.SUGGEST_CONFIG_CACHE_PATH + "_" + getLanguage());
        if (isEmptyConfig(localConfig)) { // there is no cache.
            // use local data.
            loadLocalData();
        } else { // use cache data.
            parseJson(localConfig);
        }
    }

    private boolean isEmptyConfig(String config) {
        if (TextUtils.isEmpty(config)) {
            return true;
        }
        JSONObject json;
        try {
            json = new JSONObject(config);
            int err = json.getInt("err");
            if (err == 0) {
                JSONArray jsonArray = json.getJSONArray("bd");
                if (jsonArray != null && jsonArray.length() > 0) {
                    return false;
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }
    }

    private void stopThread() {
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }

    /**
     * get data and parse data.
     * if there has cache, use cache data.;
     * else  use local data.
     * @author shirley
     *
     */
    class ParseJsonRunnable implements Runnable {

        @Override
        public void run() {
            if (!UIUtil.isNetConnected(mActivity)) {
                return;
            }
            String jsonStr = getRequestString();
            Log.d(TAG, "request json=" + jsonStr);
            String response = getJsonString(jsonStr);
            if (!TextUtils.isEmpty(response)) {
                parseJson(response);
            } else {
                loadLocalData();
            }
        }

    }

    private void parseJson(String jsonString) {
        Log.d(TAG, "parse json=" + jsonString);
        try {
            JSONObject json = new JSONObject(jsonString);
            if (mList != null) {
                mList.clear();
            }
            int err = json.getInt("err");
            if (err == 0) {
                JSONArray jsonArray = json.getJSONArray("bd");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonChild = jsonArray.getJSONObject(i);
                    UrlModle urlModle = new UrlModle(jsonChild.getString("surl"), jsonChild
                            .getString("sname"), jsonChild.getString("spic"));
                    if (mList == null) {
                        mList = new ArrayList<UrlModle>();
                    }
                    mList.add(urlModle);
                }
                UIUtil.saveFileFromString(jsonString, Constants.SUGGEST_CONFIG_CACHE_PATH + "_" + getLanguage()); // save config to local.
                if (mList != null && mList.size() > 0) {
                    mHandler.obtainMessage(Constants.MSG_UPDATE_UI).sendToTarget();
                } else {
                    mHandler.obtainMessage(Constants.MSG_GET_LOCAL_DATA).sendToTarget();
                }
            } else if (err == 1) {
                Log.d(TAG, "err=" + err + ", parameter is wrong.");
            } else if (err == 2) {
                Log.d(TAG, "err=" + err + ", server exception.");
            } else {
                Log.d(TAG, "err=" + err);
            }
        } catch (JSONException e) {
            Log.d(TAG, "data is not modified.");
        }
    }

    private String getRequestString() {

        String devMac = Device.getHardwareAddress(mActivity);
        if (devMac != null && !devMac.equals("")) {
            String[] strArray = devMac.split(":");
            StringBuffer modifiedMac = new StringBuffer();
            for (int i = 0; i < strArray.length; i++) {
                modifiedMac.append(strArray[i]);
            }
            devMac = modifiedMac.toString();
        } else {
            devMac = "000000000000";
        }

        try {
            JSONObject json = new JSONObject();
            json.put("ifid", "GetBrowserConfig");
            json.put("mac", devMac);
            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getJsonString(String json) {
        CpeManager manager = CpeManager.getInstance();
        HttpURLConnection conn = null;
        Writer writer = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(Constants.SERVICE_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "text/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "json");
            conn.setRequestProperty("Accept-Language", getLanguage());
            String tTag = manager.getProductClass() + "_"
                    + mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionCode;
            Log.d(TAG, "Ttag=" + tTag);
            conn.setRequestProperty("Ttag", tTag);
            String tCip = manager.getBBNumber() + "_0_" + System.currentTimeMillis() + "_0";
            Log.d(TAG, "Tcip=" + tCip);
            conn.setRequestProperty("Tcip", tCip);
            conn.setRequestProperty("Host", Constants.SERVICE_HOST);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("If-None-Match", BrowserSettings.getInstance(mActivity).getSuggestEtag(getLanguage()));
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(50 * 1000);
            conn.setReadTimeout(50 * 1000);
            conn.setDoOutput(true);
            Log.d(TAG, "request header=" + conn.getRequestProperties().toString());

            conn.connect();
            writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            writer.write(json);
            writer.flush();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append("\r\n");
                }
                String eTag = conn.getHeaderField("Etag");
                Log.d(TAG, "eTag=" + eTag);
                BrowserSettings.getInstance(mActivity).saveSuggestEtag(eTag, getLanguage());
            } else {
                buffer.append(responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer.toString();
    }

    /**
     * init default data.
     * @return
     */
    public List<UrlModle> initAllUrl() {
        if (mList == null) {
            mList = new ArrayList<UrlModle>();
        }
        mList.clear();
        String language = getLanguage();
        if ("zh-CN".equals(language)) {
            UrlModle modle = new UrlModle("www.baidu.com", "assets://CN/baidu.png");
            mList.add(modle);
            modle = new UrlModle("http://www.autohome.com.cn/", "assets://CN/car.png");
            mList.add(modle);
            modle = new UrlModle("http://www.ifeng.com/", "assets://CN/fenghuang.png");
            mList.add(modle);
            modle = new UrlModle("http://www.hupu.com/", "assets://CN/hupu.png");
            mList.add(modle);
            modle = new UrlModle("http://36kr.com/", "assets://CN/kr.png");
            mList.add(modle);
            modle = new UrlModle("http://www.pcbaby.com.cn/", "assets://CN/pckids.png");
            mList.add(modle);
            modle = new UrlModle("http://www.rayli.com.cn/", "assets://CN/rayli.png");
            mList.add(modle);
            modle = new UrlModle("http://www.people.com.cn/", "assets://CN/renming.png");
            mList.add(modle);
            modle = new UrlModle("http://www.qq.com/", "assets://CN/tencent.png");
            mList.add(modle);
            modle = new UrlModle("http://news.163.com/", "assets://CN/wangyi.png");
            mList.add(modle);
            modle = new UrlModle("http://www.17173.com/", "assets://CN/yaoqi.png");
            mList.add(modle);
            modle = new UrlModle("http://www.zol.com.cn/", "assets://CN/zol.png");
            mList.add(modle);
        } else if ("zh-TW".equals(language)) {
            UrlModle modle = new UrlModle("http://www.libertytimes.com.tw/ ", "assets://TW/free.png");
            mList.add(modle);
            modle = new UrlModle("http://tw.yahoo.com/", "assets://TW/yahoo.png");
            mList.add(modle);
            modle = new UrlModle("http://www.google.com", "assets://TW/google.png");
            mList.add(modle);
            modle = new UrlModle("http://food.gogocn.com/", "assets://TW/food.png");
            mList.add(modle);
            modle = new UrlModle("http://www.bnext.com.tw/", "assets://TW/digital.png");
            mList.add(modle);
            modle = new UrlModle("http://www.chinatimes.com/", "assets://TW/chinatimes.png");
            mList.add(modle);
            modle = new UrlModle("http://www.kenkon.com.tw/", "assets://TW/kenkon.png");
            mList.add(modle);
            modle = new UrlModle("http://beauty.nownews.com/", "assets://TW/beauty.png");
            mList.add(modle);
            modle = new UrlModle("http://www.cnyes.com/", "assets://TW/cnyes.png");
            mList.add(modle);
            modle = new UrlModle("http://www.flickr.com/", "assets://TW/flickr.png");
            mList.add(modle);
            modle = new UrlModle("http://www.appledaily.com.tw/ ", "assets://TW/apple.png");
            mList.add(modle);
            modle = new UrlModle("http://udn.com/", "assets://TW/udn.png");
            mList.add(modle);
        } else {// en-US
            UrlModle modle = new UrlModle("http://www.google.com", "assets://TW/google.png");
            mList.add(modle);
            modle = new UrlModle("http://www.nationalgeographic.com/", "assets://EN/national.png");
            mList.add(modle);
            modle = new UrlModle("http://www.flickr.com/", "assets://TW/flickr.png");
            mList.add(modle);
            modle = new UrlModle("https://us.yahoo.com/ ", "assets://EN/yahoo.png");
            mList.add(modle);
            modle = new UrlModle("http://www.youtube.com/", "assets://EN/youtube.png");
            mList.add(modle);
            modle = new UrlModle("http://www.amazon.com/", "assets://EN/amazon.png");
            mList.add(modle);
            modle = new UrlModle("http://www.nba.com/", "assets://EN/nba.png");
            mList.add(modle);
            modle = new UrlModle("https://500px.com/", "assets://EN/500px.png");
            mList.add(modle);
            modle = new UrlModle("http://fia.com/", "assets://EN/fia.png");
            mList.add(modle);
            modle = new UrlModle("http://www.vogue.com/", "assets://EN/vogue.png");
            mList.add(modle);
            modle = new UrlModle("http://www.imdb.com/", "assets://EN/imdb.png");
            mList.add(modle);
            modle = new UrlModle("http://edition.cnn.com/", "assets://EN/cnn.png");
            mList.add(modle);
        }

        return mList;
    }

    private void loadLocalData() {
        mList = null;
        initAllUrl();
        mHandler.obtainMessage(Constants.MSG_UPDATE_UI).sendToTarget();
    }

    /**
     * refresh UI.
     */
    public void refreshUI() {
        if (mAdapter == null) {
            mAdapter = new HomeAdapter(mActivity, mList);
        }
        if (mHolder.getGridView().getAdapter() == null) {
            mAdapter.setList(mList);
            mHolder.getGridView().setAdapter(mAdapter);
        } else {
            ((HomeAdapter) mHolder.getGridView().getAdapter()).setList(mList);
            ((HomeAdapter) mHolder.getGridView().getAdapter()).notifyDataSetChanged();
        }
    }

    private String getLanguage() {
        String country = mActivity.getResources().getConfiguration().locale.getCountry();
        String language = mActivity.getResources().getConfiguration().locale.getLanguage();
        Log.d(TAG, "current language-country=" + language + "-" + country);
        return language + "-" + country;
    }

    /**
     * clear cache.
     */
    public void clearCache() {
        mDataManager.clearDiskCache();
        mDataManager.clearMemoryCache();
    }

    /**
     * close IME
     */
    public void closeIME() {
        View view = mActivity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) mActivity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    
    public void unregisterReceiver() {
        mActivity.unregisterReceiver(mConnReceiver);
    }
}
