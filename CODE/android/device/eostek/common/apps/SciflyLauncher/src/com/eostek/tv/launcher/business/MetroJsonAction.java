
package com.eostek.tv.launcher.business;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import scifly.device.Device;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.HomeApplication;
import com.eostek.tv.launcher.business.database.DBManager;
import com.eostek.tv.launcher.model.JsonHeadBean;
import com.eostek.tv.launcher.model.MetroInfo;
import com.eostek.tv.launcher.model.MetroPage;
import com.eostek.tv.launcher.util.LConstants;
import com.eostek.tv.launcher.util.UIUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 
 * projectName： TVLauncher
 * moduleName： MetroJsonAction.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-11-6 上午11:20:32
 * @Copyright © 2014 Eos Inc.
 */
/**
 * get json data from network and parse the json string to MetroInfo
 **/
public class MetroJsonAction extends ServiceJson {
    private static final String TAG = "MetroJsonAction";

    private volatile boolean bThreadFlag = false;

    private final boolean DEBUG = true;

    private volatile int position = 0;

    private Context mContext;

    private Handler mHandler;

    private DBManager mDbManager;

    private Thread mThread = null;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    // down MetroJson
    private List<MetroPage> mpagelists = new ArrayList<MetroPage>();

    // loadurl image maps
    private ConcurrentHashMap<String, MetroInfo> imgurlmaps = new ConcurrentHashMap<String, MetroInfo>();

    private final Object mLock = new Object();

    private final Object mCntLock = new Object();

    private volatile boolean isDataError = false;

    private JsonHeadBean mHeadBean;

    private volatile String countryLanguage;

    // empty the former data and insert the new data
    // to DB
    Runnable dbThread = new Runnable() {
        @Override
        public void run() {
            if (mDbManager != null) {
                mDbManager.emptyDB(UIUtil.getLanguage());
                mDbManager.insertPages(getMetroPageList());
                mDbManager.insertJsonHeadBean(mHeadBean);
            }
        }
    };

    public MetroJsonAction(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        bThreadFlag = false;
        mDbManager = DBManager.getDbManagerInstance(HomeApplication.getInstance());

        options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error).cacheInMemory(false).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    public List<MetroPage> getMetroPageList() {
        return mpagelists;
    }

    public JsonHeadBean getJsonHeadBean() {
        return mHeadBean;
    }

    /**
     * stopThread
     * 
     * @param void
     * @return
     * @throws JSONException
     */
    public void stopThread() {
        bThreadFlag = false;
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        if (imageLoader.isInited()) {
            imageLoader.stop();
            imageLoader.clearMemoryCache();
            imageLoader.destroy();
        }
        
        // reset data when stop the load picture
        if (imgurlmaps != null && !imgurlmaps.isEmpty()) {
            imgurlmaps.clear();
        }
        position = 0;
    }

    /**
     * parse metro json
     * 
     * @param pgms
     * @return
     * @throws JSONException
     */
    public void parsePgmJson() {
        stopThread();
        if (!ImageLoader.getInstance().isInited()) {
            HomeApplication.initImageLoader(mContext.getApplicationContext());
        }
        bThreadFlag = true;
        if (mThread == null) {
            mThread = new Thread(new ParseJsonRunnable());
            mThread.start();
        }
    }

    private List<MetroInfo> parseMetroInfoJson(JSONArray jsonArray, String typeTitle, int appCategory)
            throws JSONException {
        List<MetroInfo> list = new ArrayList<MetroInfo>();
        Log.d(TAG, "jsonObject.length ::" + jsonArray.length());
        int count = jsonArray.length();
        String country = countryLanguage;
        for (int i = 0; i < count; i++) {
            MetroInfo minfo = new MetroInfo();
            JSONObject itsobj = jsonArray.getJSONObject(i);
            Log.i(TAG, "" + itsobj);
            // if the class or package name is empty,just return
            if (TextUtils.isEmpty(itsobj.getString("pn")) || TextUtils.isEmpty(itsobj.getString("cn"))) {
                isDataError = true;
                list.clear();
                return list;
            }
            minfo.setPkgName(itsobj.getString("pn"));
            minfo.setClsName(itsobj.getString("cn"));
            minfo.setTitle(itsobj.getString("ti"));
            minfo.setExtraStrInfo(itsobj.getString("sfg"));
            if (itsobj.getString("ifg").isEmpty()) {
                minfo.setExtraIntInfo(-1);
            } else {
                minfo.setExtraIntInfo(itsobj.getInt("ifg"));
            }
            minfo.setX(itsobj.getInt("x"));
            minfo.setY(itsobj.getInt("y"));
            minfo.setWidthSize(itsobj.getInt("w"));
            minfo.setHeightSize(itsobj.getInt("h"));
            minfo.setItemType(itsobj.getInt("ty"));
            minfo.setTypeTitle(typeTitle);
            minfo.setIconPathB(itsobj.getString("picB"));
            minfo.setIconPathF(itsobj.getString("picF"));
            minfo.setAppCategory(appCategory);
            minfo.setApkUrl(itsobj.getString("aln"));
            minfo.setCounLang(country);
            list.add(minfo);
            refreshImgurlmaps(itsobj.getString("picB"), minfo);
        }
        return list;
    }

    // refresh imgurlmaps
    private void refreshImgurlmaps(String imgurl, MetroInfo minfo) {
        if (imgurl.isEmpty() || minfo.getItemType() == 3) {
            return;
        }
        if (imgurlmaps == null) {
            imgurlmaps = new ConcurrentHashMap<String, MetroInfo>();
        }
        if (!imgurlmaps.containsKey(imgurl)) {
            imgurlmaps.putIfAbsent(imgurl, minfo);
        }
    }

    /**
     * Pictures asynchronous download
     * 
     * @param url
     * @param minfo
     */
    public void startLoaded(final String url, MetroInfo minfo) {
        if (url.isEmpty() || minfo.getItemType() == 3) {
            return;
        }
        if (!ImageLoader.getInstance().isInited()) {
            return;
        }
        float imgWidth = minfo.getWidthSize() * HomeApplication.getFactor();
        float imgHeght = minfo.getHeightSize() * HomeApplication.getFactor();
        ImageSize targetSize = new ImageSize((int) imgWidth, (int) imgHeght);
        imageLoader.loadImage(url, targetSize, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
                Log.v(TAG, "onLoadingStarted " + url + ";" + System.currentTimeMillis());
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Object loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                // add lock to avoid synchronize problem
                synchronized (mCntLock) {
                    position = position + 1;
                    Log.v(TAG, "onLoadingComplete " + url + ";" + System.currentTimeMillis() + "; imgurlmaps.size"
                            + imgurlmaps.size() + "; position:" + position);
                    if (imgurlmaps != null && imgurlmaps.size() > 0 && position == imgurlmaps.size()) {
                        // set the etag
                        mHeadBean.seteTag(geteTag());
                        mHandler.post(dbThread);
                        mHandler.sendEmptyMessage(LConstants.UPDATE_METRO_DATA);
                        imgurlmaps.clear();
                        position = 0;
                        // UIUtil.writeStringToXml(mContext, "ETag", geteTag());
                        imageLoader.clearMemoryCache();
                    }
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                // UIUtil.writeStringToXml(mContext, "ETag", "If-None-Match");
                DBManager.getDbManagerInstance(HomeApplication.getInstance()).resetETag(UIUtil.getLanguage());
                Log.e(TAG, "onLoadingFailed  url:" + url);
                // clear the cache when load failed
                // imageLoader.stop();
                // imageLoader.destroy();
                imageLoader.clearMemoryCache();
                imageLoader.clearDiskCache(url);
                mHandler.sendEmptyMessage(LConstants.UPDATE_DATA_FAIL);
            }

        }, null);

    }

    private String getProtocalVersion() {
        return "00";
    }

    private String getMacAddress() {
        String mac = Device.getHardwareAddress(mContext).replace(":", "");
        return mac;
    }

    private void addRequestHeads() {
        countryLanguage = UIUtil.getLanguage();
        // add params
        HashMap<String, String> headParams = new HashMap<String, String>();
        headParams.put("Ttag", UIUtil.getSpecialCode() + "_" + UIUtil.getVersionName(mContext) + "_"
                + getProtocalVersion());
        headParams.put("Tcip", UIUtil.getBBCode() + "_00_" + System.currentTimeMillis() + "_00");
        headParams.put("Accept-Language", countryLanguage);
        // headParams.put("If-None-Match", UIUtil.getStringFromXml(mContext,
        // "ETag"));
        headParams.put("If-None-Match", mDbManager.getETag(countryLanguage));
        addHeadMaps(headParams);
        Log.v(TAG, "Ttag--->" + headParams.get("Ttag"));
        Log.v(TAG, "Tcip--->" + headParams.get("Tcip"));
        Log.v(TAG, "Accept-Language--->" + headParams.get("Accept-Language"));
        Log.v(TAG, "If-None-Match--->" + headParams.get("If-None-Match"));

        countryLanguage = headParams.get("Accept-Language");
    }

    /**
     * The thread to handle ParseJsonThread
     */
    class ParseJsonRunnable implements Runnable {
        @Override
        public void run() {
            synchronized (mLock) {
                Log.v(TAG, "bThreadFlag" + bThreadFlag);
                while (bThreadFlag) {
                    Log.v(TAG, "bThreadFlag start ");
                    mpagelists = new ArrayList<MetroPage>();
                    imgurlmaps = new ConcurrentHashMap<String, MetroInfo>();
                    mHeadBean = new JsonHeadBean();
                    serverUrl = LConstants.SERVER_URL;
                    addRequestHeads();
                    String parameter = "ifid=GetLaucherConfig&resolution=" + UIUtil.getScreenHieghtPX(mContext)[1]
                            + "&mac=" + getMacAddress() + "&version=" + UIUtil.getVersionCode(mContext);
                    if (DEBUG) {
                        Log.d(TAG, "MetroJsonAction().parsePgmJson() " + parameter);
                    }
                    try {
                        JSONObject jsonObject = getJSONObject(parameter);
                        if (jsonObject != null) {
                            mHeadBean.setResponse(jsonObject.getInt("err"));
                            if (jsonObject.isNull("bk")) {
                                mHeadBean.setBackgroundUrl("");
                            } else {
                                mHeadBean.setBackgroundUrl(jsonObject.getString("bk"));
                            }
                            if (jsonObject.isNull("lg")) {
                                mHeadBean.setLogoUrl("");
                            } else {
                                mHeadBean.setLogoUrl(jsonObject.getString("lg"));
                            }
                            if (jsonObject.isNull("x")) {
                                mHeadBean.setLogoX(-1);
                            } else {
                                mHeadBean.setLogoX(jsonObject.getInt("x"));
                            }
                            if (jsonObject.isNull("y")) {
                                mHeadBean.setLogoY(-1);
                            } else {
                                mHeadBean.setLogoY(jsonObject.getInt("y"));
                            }
                            mHeadBean.setCounLang(countryLanguage);
                            Log.v(TAG, "mHeadBean = " + mHeadBean.toString());
                            JSONArray bdJSONArray = jsonObject.optJSONArray("bd");
                            if (bdJSONArray != null && bdJSONArray.length() > 0) {
                                for (int i = 0; i < bdJSONArray.length(); i++) {
                                    MetroPage metropage = new MetroPage();
                                    JSONObject bdobj = bdJSONArray.getJSONObject(i);
                                    String ti = bdobj.getString("ti");
                                    int in = bdobj.getInt("in");
                                    metropage.setTitle(ti);
                                    metropage.setAppCategory(in);
                                    JSONArray itsJSONArray = bdobj.optJSONArray("its");
                                    if (itsJSONArray != null && itsJSONArray.length() > 0) {
                                        metropage.setList(parseMetroInfoJson(itsJSONArray, ti, in));
                                        metropage.setCounLang(countryLanguage);
                                    }
                                    if (isDataError) {
                                        UIUtil.writeStringToXml(mContext, "ETag", geteTag());
                                        isDataError = false;
                                        bThreadFlag = false;
                                        return;
                                    }
                                    if (DEBUG) {
                                        Log.d(TAG, "ti:" + ti);
                                    }
                                    mpagelists.add(metropage);
                                }
                                // down img
                                if (imgurlmaps != null && imgurlmaps.size() > 0) {
                                    Iterator<String> keys = imgurlmaps.keySet().iterator();
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        startLoaded(key, imgurlmaps.get(key));
                                    }
                                }
                            }
                        }
                    } catch (ConnectTimeoutException e) {
                        Log.e(TAG, "network state : CONNECTION_TIMEOUT");
                    } catch (InterruptedIOException e) {
                        Log.e(TAG, "network state : SO_TIMEOUT");
                    } catch (ClientProtocolException e) {
                        Log.e(TAG, "network state : ClientProtocolException");
                    } catch (IOException e1) {
                        Log.e(TAG, "IOException :" + e1);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        Log.v(TAG, "parse json error!");
                    } finally {
                        bThreadFlag = false;
                    }
                }
            }
        }
    }

}
