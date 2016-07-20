
package com.eostek.wasuwidgethost.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.eostek.scifly.widget.R;
import com.eostek.wasuwidgethost.WasuApplication;
import com.eostek.wasuwidgethost.model.PgmInfo;
import com.eostek.wasuwidgethost.util.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * projectName： WasuWidgetHost.
 * moduleName： MetaDataManager.java
 */
public class MetaDataManager extends ServiceJson {

    private static final String TAG = "MetaDataManager";

    private static MetaDataManager metaDataManager;

    private Context mContext;

    private Handler mHandler;

    private Thread mThread = null;

    private static int position = 0;

    private final Object isUpdate = new Object();

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    // down pgmInfolists
    private List<PgmInfo> pgmInfolists = new ArrayList<PgmInfo>();

    // loadurl image maps
    private ConcurrentHashMap<String, PgmInfo> imgurlmaps = new ConcurrentHashMap<String, PgmInfo>();

    /**
     * construct method.
     * @param context
     * @param handler
     */
    public MetaDataManager(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.video_icon_7day_b)
                .showImageOnFail(R.drawable.video_icon_7day_b).cacheInMemory(false).cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

    }

    /**
     * get meta manager instance.
     * @param context
     * @param mHandler
     * @return MetaDataManager
     */
    public static MetaDataManager getMetaManagerInstance(Context context, Handler mHandler) {
        if (metaDataManager == null) {
            metaDataManager = new MetaDataManager(context, mHandler);
        }
        return metaDataManager;
    }

    public final List<PgmInfo> getPgmInfoList() {
        return pgmInfolists;
    }

    /**
     * parse pgmInfo xml.
     * 
     * @param pgms
     * @return
     * @throws JSONException
     */
    public final void parsePgmXml() {
        if (pgmInfolists != null && pgmInfolists.size() > 0) {
            return;
        }
        if (mThread != null && mThread.isAlive()) {
            Log.e(TAG, "thread interrupt_1");
            mThread.interrupt();
            Log.e(TAG, "" + mThread.isInterrupted());
        }

        if (!ImageLoader.getInstance().isInited()) {
            WasuApplication.initImageLoader(mContext.getApplicationContext());
        }
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (isUpdate) {
                        imgurlmaps = new ConcurrentHashMap<String, PgmInfo>();
                        serverUrl = Constants.SERVERURL;
                        try {
                            JSONObject jsonObject = getJSONObject(serverUrl, "", true);
                            if (jsonObject != null) {
                                JSONArray itsJSONArray = jsonObject.optJSONArray("data");
                                parseJson(itsJSONArray);
                                if (imgurlmaps != null && imgurlmaps.size() > 0) {
                                    Iterator<String> keys = imgurlmaps.keySet().iterator();
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        startLoaded(key, imgurlmaps.get(key));
                                    }
                                }
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                }
            }
        });

        mThread.start();
    }

    private void parseJson(JSONArray jsonArray) throws JSONException {
        int count = jsonArray.length();
        for (int i = 0; i < count; i++) {
            PgmInfo info = new PgmInfo();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            info.setId((Integer) jsonObject.get("id"));
            info.setPic(jsonObject.optString("picUrl"));
            refreshImgurlmaps(jsonObject.optString("picUrl"), info);
            pgmInfolists.add(info);
        }
        for (PgmInfo pgmInfo : pgmInfolists) {
            Log.i(TAG, "id = " + pgmInfo.getId());
            Log.i(TAG, "picUrl = " + pgmInfo.getPic());
        }
    }

    // refresh imgurlmaps
    private void refreshImgurlmaps(String imgurl, PgmInfo minfo) {
        if (imgurl.isEmpty()) {
            return;
        }
        if (imgurlmaps == null) {
            imgurlmaps = new ConcurrentHashMap<String, PgmInfo>();
        }
        if (!imgurlmaps.containsKey(imgurl)) {
            imgurlmaps.put(imgurl, minfo);
        }
    }

    /**
     * Pictures asynchronous download.
     * @param url
     * @param minfo
     */
    public final void startLoaded(final String url, PgmInfo minfo) {
        if (url.isEmpty()) {
            return;
        }
        if (!ImageLoader.getInstance().isInited()) {
            return;
        }
        ImageSize targetSize = new ImageSize(400, 610);
        imageLoader.loadImage(url, targetSize, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Object loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                position = position + 1;
                Log.v(TAG, "onLoadingComplete " + url + ";" + System.currentTimeMillis());
                if (imgurlmaps != null && imgurlmaps.size() > 0 && position == imgurlmaps.size()) {
                    mHandler.sendEmptyMessage(Constants.DATA_ACCESS_SUCCESS);
                    imgurlmaps.clear();
                    position = 0;
                    imageLoader.clearMemoryCache();
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                if (mHandler != null) {
                    mHandler.removeMessages(Constants.DATA_ACCESS_SUCCESS);
                }
                if (imageLoader != null) {
                    imageLoader.stop();
                    imageLoader.clearMemoryCache();
                    imageLoader.destroy();
                }
                Log.v(TAG, "onLoadingFailed  url:" + url);
            }

        }, null);

    }

}
