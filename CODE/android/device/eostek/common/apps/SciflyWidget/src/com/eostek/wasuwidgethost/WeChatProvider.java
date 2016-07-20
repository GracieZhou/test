
package com.eostek.wasuwidgethost;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import scifly.device.Device;
import scifly.provider.SciflyStore;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.eostek.scifly.widget.R;
import com.eostek.tm.cpe.manager.CpeManager;
import com.eostek.wasuwidgethost.util.Constants;
import com.eostek.wasuwidgethost.util.HttpUtil;
import com.eostek.wasuwidgethost.util.Utils;

/**
 * projectName： moduleName：
 * 
 * @author
 * @version
 * @time
 * @Copyright © 2015 Eos Inc.
 */
public class WeChatProvider extends BaseAppWidgetProvider {

    private static final String TAG = "WeChatProvider";

    private static final String WX_NMAE_JPG = "wxqrcode.jpg";

    private static Context mContext;

    private static int[] mAppWidgetIds = null;

    private static int mWidgetID = 0;

    private Bitmap mWeChatBitmap = null;

    /**
     * access token stand a flag to get qrcode for first step.
     */
    private String access_token = null;

    /**
     * ticket stand a right to access qrcode.
     */

    private String ticket = null;

    private String qrcodeFile = null;

    /**
     * Refresh cycle.
     */
    private int expire_seconds = 604800;

    private long lastDownloadTime = 0;

    private static final String WECHAT = "wechat";

    private static final String EXPIRE_SECONDS = "expire_seconds";

    private int requestTokenCount = 0;

    private int requestTicketCount = 0;

    private String mBBNumber = null;

    private IMObserver mObserver = null;

    /**
     * thread pools.
     */
    private ExecutorService mThreadExecutor = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.GET_TOCKEN:
                    addRunnable(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (httpGetAccessToken(Constants.REQUEST_ACCESS_TOKEN_URL)) {
                                    sendEmptyMessage(Constants.GET_TICKET);
                                    requestTokenCount = 0;
                                } else {
                                    if (requestTokenCount < 3) {
                                        ++requestTokenCount;
                                        sendEmptyMessage(Constants.GET_TOCKEN);
                                        Log.v(TAG, "request token fail");
                                    } else {
                                        requestTokenCount = 0;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    break;
                case Constants.GET_TICKET:
                    addRunnable(new Runnable() {
                        public void run() {
                            try {
                                if (httpPostqrcodeTicket(access_token)) {
                                    sendEmptyMessage(Constants.GET_QR_IMAGE);
                                    requestTicketCount = 0;
                                } else {
                                    if (requestTicketCount < 3) {
                                        ++requestTicketCount;
                                        sendEmptyMessage(Constants.GET_TICKET);
                                        Log.v(TAG, "request ticket fail");
                                    } else {
                                        requestTicketCount = 0;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                case Constants.GET_QR_IMAGE:
                    addRunnable(new Runnable() {
                        public void run() {
                            try {
                                if (downQRCodeImage(ticket)) {
                                    requestTicketCount = 0;
                                    requestTokenCount = 0;
                                    if (mAppWidgetIds != null) {
                                        Log.v(TAG, "mAppWidgetIds.size():" + mAppWidgetIds.length);
                                        for (int id : mAppWidgetIds) {
                                            updateWidgetView(id);
                                        }
                                    } else
                                        Log.v(TAG, "mAppWidgetIds is null");

                                } else {
                                    Log.v(TAG, "download QRCode fail");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.v(TAG, "onUpdate ");
        mContext = context;
        mAppWidgetIds = appWidgetIds;
        String dirPath = mContext.getCacheDir().getPath();
        qrcodeFile = dirPath + File.separator + WX_NMAE_JPG;// data/data/com.eostek.wasuwidgethost/cache
        clearFormerWidget(WeChatProvider.class.getName());
        Log.v(TAG, "id:" + mAppWidgetIds.toString());
        getQR();
    }

    synchronized private void getQR() {
        File file = new File(qrcodeFile);
        if (Utils.isNetConnected(mContext)) {
            if (!file.exists() || file.length() < 10) {
                start();
            } else {
                long time = System.currentTimeMillis();
                lastDownloadTime = getValueFromXml(WECHAT);
                if (time - lastDownloadTime >= getValueFromXml(EXPIRE_SECONDS) * 1000) {
                    start();
                } else {
                    if (mAppWidgetIds != null) {
                        for (int id : mAppWidgetIds) {
                            updateWidgetView(id);
                        }
                    } else
                        Log.v(TAG, "mAppWidgetIds is null");
                }
            }
        } else {
            if (mAppWidgetIds != null) {
                for (int id : mAppWidgetIds) {
                    updateWidgetViewWithOutNetWork(id);
                }
            } else
                Log.v(TAG, "mAppWidgetIds is null");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.i(TAG, "Name:SciflyWidget, Version:2.4.40, Date:2015-09-24, Publisher:Youpeng.Wan, REV:42628");

        mContext = context;
        String action = intent.getAction();
        Log.v(TAG, "onReceive action:" + action);
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            addRunnable(new Runnable() {

                @Override
                public void run() {
                    String dirPath = mContext.getCacheDir().getPath();
                    qrcodeFile = dirPath + File.separator + WX_NMAE_JPG;
                    getQR();
                }
            });

        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.v(TAG, "onDeleted");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        mContext = context;
        if (mObserver == null) {
            mObserver = new IMObserver(mHandler);
            mObserver.registerContentResolver(context);
        }
        Log.v(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        mObserver.unregisterContentResolver();
        Log.v(TAG, "onDisabled");
    }

    synchronized private void updateWidgetView(int widgetID) {

        if (mWeChatBitmap != null) {
            mWeChatBitmap.recycle();
            System.gc();
            mWeChatBitmap = null;
        }

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.wechat_ui);

        obtainQRCode();

        if (mWeChatBitmap == null) {
            Log.v(TAG, "mWeChatBitmap is null!!");
            return;
        }

        if (mWeChatBitmap.isRecycled()) {
            updateWidgetView(widgetID);
            return;
        }

        remoteViews.setImageViewBitmap(R.id.wechat, mWeChatBitmap);
        remoteViews.setViewVisibility(R.id.tip, View.VISIBLE);
        remoteViews.setTextViewText(R.id.tip, mContext.getResources().getString(R.string.qr_code_tip));
        if (mAppWidgetManager == null) {
            mAppWidgetManager = AppWidgetManager.getInstance(mContext);
        }
        mAppWidgetManager.updateAppWidget(widgetID, remoteViews);
        requestTicketCount = 0;
        requestTokenCount = 0;
        Log.v(TAG, "update widget...");
    }

    synchronized private void updateWidgetViewWithOutNetWork(int widgetID) {

        if (mWeChatBitmap != null) {
            mWeChatBitmap.recycle();
            System.gc();
            mWeChatBitmap = null;
        }

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.wechat_ui);
        remoteViews.setImageViewResource(R.id.wechat, R.drawable.wechat);
        remoteViews.setViewVisibility(R.id.tip, View.GONE);
        if (mAppWidgetManager == null) {
            mAppWidgetManager = AppWidgetManager.getInstance(mContext);
        }
        mAppWidgetManager.updateAppWidget(widgetID, remoteViews);
        requestTicketCount = 0;
        requestTokenCount = 0;
        Log.v(TAG, "update widget without network...");
    }

    private void start() {
        if (judgeDevice()) {
            mHandler.sendEmptyMessage(Constants.GET_TOCKEN);
        } else
            Log.v(TAG, "Device is error!");
    }

    private Boolean judgeDevice() {
        String feature = Device.getDeviceCode();
        Log.e(TAG, "feature : " + feature);
        if (feature.equals("EOS0NK20LD00TV00") || feature.equals("EOS0NK20LD32TV00")
                || feature.equals("EOS0MUJILD00TV00")) {
            return false;
        }
        return true;
    }

    private void obtainQRCode() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(qrcodeFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        long maxWidgetBitmapMemory = getMaxWidgetBitmapMemory();
        Bitmap bitmap = BitmapFactory.decodeStream(fis);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        Log.d(TAG, "obtainQRCode options=100, size=" + baos.toByteArray().length * 8);
        int options = 90;
        while (baos.toByteArray().length * 8 > maxWidgetBitmapMemory) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            Log.d(TAG, "options=" + options + "size=" + baos.toByteArray().length * 8);
            options -= 10;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        mWeChatBitmap = BitmapFactory.decodeStream(bais);
        if (mWeChatBitmap == null) {
            Log.d(TAG, "mWeChatBitmap==null");
            mHandler.sendEmptyMessage(Constants.GET_TOCKEN);
        }
    }

    /**
     * get access info.
     * 
     * @param tokenURL
     * @return
     * @throws Exception {"token":
     *             "M5LKeuggEN7IkoJ4E59tRxScjvyQs9STI8mMK8_dmHgH09LyQGI397s9a3GbN1aJVMAOv011t_UZmmFdimr1UvO42uIRef7YMCLHw9S-01U"
     *             ,"expires_in":7200}
     */
    private boolean httpGetAccessToken(String tokenURL) throws Exception {

        mBBNumber = getBBCode();
        if (TextUtils.isEmpty(mBBNumber) || "105075".equals(mBBNumber)) {
            return false;
        }
        String respon = HttpUtil.getUrlAsString(tokenURL);
        if (null == respon) {
            return false;
        }
        JSONObject obj = new JSONObject(respon);
        if (obj.has("token")) {
            access_token = obj.getString("token");
            if (obj.has("expires_in")) {
                expire_seconds = obj.getInt("expires_in");
            }
        } else if (obj.has("errcode")) {
            access_token = null;
            return false;
        } else {
            access_token = null;
            return false;
        }
        Log.v(TAG, "token:" + access_token);
        return true;
    }

    /**
     * get qrcode ticket as entry.
     * 
     * @param accesstoken
     * @return
     * @throws Exception {"expire_seconds": 1800, "action_name": "QR_SCENE",
     *             "action_info": {"scene": {"scene_id": 123}}}
     */
    private boolean httpPostqrcodeTicket(String accesstoken) throws Exception {

        String url = Constants.REQUEST_QR_TICKET_URL + accesstoken;

        JSONObject obj = new JSONObject();
        obj.put("expire_seconds", expire_seconds);
        obj.put("action_name", "QR_SCENE");
        JSONObject obj_scene_id = new JSONObject();
        obj_scene_id.put("scene_id", mBBNumber);
        JSONObject scene = new JSONObject();
        scene.put("scene", obj_scene_id);
        obj.put("action_info", scene);
        // Log.d(TAG, " qrcode json :"+obj.toString());
        String response = HttpUtil.postBody(url, obj.toString());
        // Log.d(TAG, " response :"+response);
        if (null == response) {
            Log.d(TAG, " response is null");
            return false;
        }
        JSONObject obj_ticket = new JSONObject(response);
        if (obj_ticket.has("ticket")) {
            ticket = obj_ticket.getString("ticket");
            expire_seconds = obj_ticket.getInt("expire_seconds");
            writeValueToXml(EXPIRE_SECONDS, expire_seconds);
        } else if (obj_ticket.has("errcode")) {
            return false;
        }
        Log.v(TAG, "ticket:" + ticket + "expire_seconds:" + expire_seconds);
        return true;
    }

    private boolean downQRCodeImage(String ticket) throws IOException {
        Log.v(TAG, "File path:" + qrcodeFile);
        if (null != ticket && !ticket.isEmpty()) {
            String url = Constants.REQUEST_QR_IMAGE_URL + ticket;
            if (HttpUtil.downloadFile(qrcodeFile, url)) {
                writeValueToXml(WECHAT, System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    /**
     * get BB code
     * 
     * @return The bb code
     */
    private static String getBBCode() {
        CpeManager manager = CpeManager.getInstance();
        String bbNum = manager.getBBNumber();
        Log.v(TAG, "getBBCode = " + bbNum);
        return bbNum;
    }

    /**
     * thread pool to handler thread.
     * 
     * @param runnable
     */
    private void addRunnable(Runnable runnable) {
        Log.i(TAG, "add thread sucess");
        if (mThreadExecutor == null) {
            mThreadExecutor = Executors.newSingleThreadExecutor();
        }
        mThreadExecutor.execute(runnable);
    }

    private long getValueFromXml(String str) {
        SharedPreferences sPreferences = mContext.getSharedPreferences(str, Context.MODE_PRIVATE);
        return sPreferences.getLong(str, 0);
    }

    /**
     * write string value to xml file {@link SharedPreferences#putString}
     * 
     * @param ctx Context object
     * @param key The name of the preference to retrieve
     * @param value The new value for the preference
     */
    private void writeValueToXml(String str, long value) {
        SharedPreferences sPreferences = mContext.getSharedPreferences(str, Context.MODE_PRIVATE);
        Editor editor = sPreferences.edit();
        editor.putLong(str, value);
        editor.commit();
    }

    class IMObserver extends ContentObserver {

        private ContentResolver mResolver;

        public IMObserver(Handler handler) {
            super(handler);
        }

        public void registerContentResolver(Context context) {
            mResolver = context.getContentResolver();
            mResolver.registerContentObserver(Uri.parse("content://com.eostek.scifly.provider/global"), true, this);
        }

        public void unregisterContentResolver() {
            mResolver.unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "onChange");
            String imState = SciflyStore.Global.getString(mResolver, SciflyStore.Global.IM_STATE);
            if ("1".equals(imState) && Utils.isNetConnected(mContext)) {
                String dirPath = mContext.getCacheDir().getPath();
                qrcodeFile = dirPath + File.separator + WX_NMAE_JPG;
                File file = new File(qrcodeFile);
                if (!file.exists() || file.length() < 10) {
                    start();
                } else {
                    long time = System.currentTimeMillis();
                    lastDownloadTime = getValueFromXml(WECHAT);
                    if (time - lastDownloadTime >= getValueFromXml(EXPIRE_SECONDS) * 1000) {
                        start();
                    }
                }
            }
        }
    }

    private long getMaxWidgetBitmapMemory() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        // Cap memory usage at 1.5 times the size of the display
        // 1.5 * 4 bytes/pixel * w * h ==> 6 * w * h
        long maxWidgetBitmapMemory = 6 * size.x * size.y;
        Log.d(TAG, "size.x=" + size.x + ", size.y=" + size.y + ", max=" + maxWidgetBitmapMemory);
        return maxWidgetBitmapMemory;
    }

}
