
package com.eostek.wasuwidgethost;

import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.eostek.scifly.widget.R;
import com.eostek.wasuwidgethost.business.MetaDataManager;
import com.eostek.wasuwidgethost.model.PgmInfo;
import com.eostek.wasuwidgethost.util.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
/**
 * projectName：WasuWidgetHost.
 * moduleName： MovieWidgetProvider.java
 * 
 */

public class MovieWidgetProvider extends BaseAppWidgetProvider {
    private static final String TAG = "MovieWidgetProvider";

    private static final String CLICK_NAME_ACTION = "com.eostek.wasuwidgethost.MovieWidgetProvider";

    private static Context mContext;

    private static AppWidgetManager mAppWidgetManager;

    // picture postion
    private static int position = 0;

    // mRemoteViews Layout
    private static int layoutId = 1;

    private static MetaDataManager metaDateManger = null;

    private static List<PgmInfo> pgmInfolists = null;

    private static RemoteViews mRemoteViews;

    private MyHandler mHandlerTask;

    private static MyRunnable switchImgThread;

    private static DisplayImageOptions options;

    private static Bitmap mloadedImage = null;

    // update pgminforlist
    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.DATA_ACCESS_SUCCESS:
                    if (metaDateManger != null) {
                        pgmInfolists = metaDateManger.getPgmInfoList();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    // update img
    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            Log.d(TAG, "<<<<<<<<<<<<<<<<<<  pgmInfolists : " + pgmInfolists);
            if (pgmInfolists != null && pgmInfolists.size() > 0) {
                if (pgmInfolists != null && position >= pgmInfolists.size()) {
                    position = 0;
                }
                if (mloadedImage != null) {
                    mloadedImage.recycle();
                }
                RemoteViews subViews01 = new RemoteViews(mContext.getPackageName(), R.layout.move_widget01);
                RemoteViews subViews = new RemoteViews(mContext.getPackageName(), R.layout.move_widget);
                mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.move_widget);
                if (layoutId == 1) {
                    mRemoteViews.removeAllViews(R.id.move_content);
                    mRemoteViews.addView(R.id.move_content, subViews01);
                    layoutId = 2;
                } else {
                    mRemoteViews.removeAllViews(R.id.move_content01);
                    mRemoteViews.addView(R.id.move_content, subViews);
                    layoutId = 1;
                }
                if (pgmInfolists != null) {
                    PgmInfo tmpInfo = pgmInfolists.get(position);
                    startLoaded(tmpInfo, mRemoteViews);
                    position = position + 1;
                    Log.d(TAG, "<<<<<<<<<<<<<<<<<<  postion : " + position);
                }
            } else {
                if (metaDateManger != null) {
                    metaDateManger.parsePgmXml();
                }
                mHandlerTask.removeCallbacks(switchImgThread);
                mHandlerTask.postDelayed(switchImgThread, Constants.IMGFLIPPER_TIME);
            }
        }

    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "<<<<<<<<<<<<<<<<<<  MovieWidgetProvider onReceive  : ");
        this.mContext = context;
        String action = intent.getAction();
        if (mAppWidgetManager == null) {
            mAppWidgetManager = AppWidgetManager.getInstance(context);
        }

        if (action.equals(CLICK_NAME_ACTION)) {
            startWasuApp();
        }
        if (action.equals(Constants.EOSTEK_WIDGET_START)) {
            onStart();
        }
        if (action.equals(Constants.EOSTEK_WIDGET_STOP)) {
            onStop();
        }

        Log.d(TAG, "<<<<<<<<<<<<<<<<<<  MovieWidgetProvider onReceive  : " + action);
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i(TAG, "onUpdate");
        clearFormerWidget(MovieWidgetProvider.class.getName());
        mAppWidgetManager = AppWidgetManager.getInstance(context);

        if (!ImageLoader.getInstance().isInited()) {
            WasuApplication.initImageLoader(context.getApplicationContext());
        }

        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.move_widget);
        setForegroud(mRemoteViews);
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            Intent intentClick = new Intent(CLICK_NAME_ACTION);
            // create PendingIntent
            PendingIntent pending = PendingIntent.getBroadcast(context, 0, intentClick, 0);
            mRemoteViews.setOnClickPendingIntent(R.id.move_layout, pending);
            // Tell the AppWidgetManager to perform an update on the current app
            // widget
            appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews);
        }
        if (mHandlerTask == null) {
            mHandlerTask = new MyHandler();
            switchImgThread = new MyRunnable();
            metaDateManger = MetaDataManager.getMetaManagerInstance(context, mHandlerTask);
        }
        if (options == null) {
            initImageLoader(context);
        }
        if (mHandlerTask != null) {
            mHandlerTask.removeCallbacks(switchImgThread);
            mHandlerTask.postDelayed(switchImgThread, Constants.IMGFLIPPER_TIME);
        }
        Log.d(TAG, "<<<<<<<<<<<<<<<<<<  MovieWidgetProvider mHandlerTask  : " + mHandlerTask);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        context = null;
        options = null;
        releaseReflectBitmap();
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().destroy();
        super.onDeleted(context, appWidgetIds);
        Log.d(TAG, "onDeleted");
    }

    public void onStart() {
        if (mHandlerTask != null) {
            mHandlerTask.removeCallbacks(switchImgThread);
            mHandlerTask.postDelayed(switchImgThread, Constants.IMGFLIPPER_TIME);
        }
    }

    /**
     * @param void
     */
    public void onStop() {
        if (mHandlerTask != null) {
            mHandlerTask.removeCallbacks(switchImgThread);
        }
    }

    /**
     * Restore default data.
     */
    public final void releaseReflectBitmap() {
        if (mRemoteViews != null) {
            mRemoteViews.removeAllViews(R.id.move_content01);
            mRemoteViews.removeAllViews(R.id.move_content);
            mRemoteViews = null;
        }
        mAppWidgetManager = null;
        if (mHandlerTask != null) {
            mHandlerTask.removeCallbacks(switchImgThread);
            mHandlerTask = null;
            switchImgThread = null;
        }
        if (pgmInfolists != null && pgmInfolists.size() > 0) {
            pgmInfolists.clear();
        }
        position = 0;
        layoutId = 1;
    }

    /**
     * Pictures asynchronous download.
     * @param tmpInfo
     * @param remoteViews
     */
    public final void startLoaded(final PgmInfo tmpInfo, final RemoteViews remoteViews) {
        if (!ImageLoader.getInstance().isInited()) {
            WasuApplication.initImageLoader(mContext.getApplicationContext());
        }
        ImageSize targetSize = new ImageSize(400, 610);
        final String url = tmpInfo.getPic();
        ImageLoader.getInstance().loadImage(url, targetSize, options, new SimpleImageLoadingListener() {
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
                Log.v(TAG, "onLoadingComplete " + url + ";" + System.currentTimeMillis());
                mloadedImage = (Bitmap) loadedImage;
                remoteViews.setImageViewBitmap(R.id.flipper, mloadedImage);
                setForegroud(remoteViews);
                Intent intentClick = new Intent(CLICK_NAME_ACTION);
                // create PendingIntent
                PendingIntent pending = PendingIntent.getBroadcast(mContext, 0, intentClick, 0);
                remoteViews.setOnClickPendingIntent(R.id.move_layout, pending);
                ComponentName thisWidget = new ComponentName(mContext, MovieWidgetProvider.class);
                if(mAppWidgetManager == null){
                    mAppWidgetManager = AppWidgetManager.getInstance(mContext);
                }
                mAppWidgetManager.updateAppWidget(thisWidget, remoteViews);
                mHandlerTask.removeCallbacks(switchImgThread);
                mHandlerTask.postDelayed(switchImgThread, Constants.IMGFLIPPER_TIME);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                Log.v(TAG, "onLoadingFailed  url:" + url);
                mHandlerTask.removeCallbacks(switchImgThread);
                mHandlerTask.postDelayed(switchImgThread, Constants.IMGFLIPPER_TIME);
            }
        }, null);
    }

    /**
     * start wasu app.
     */
    @SuppressLint("InlinedApi")
    public final void startWasuApp() {

        int id = 0;
        if (pgmInfolists == null || pgmInfolists.size() <= 0) {
            
        } else if (position >= pgmInfolists.size()) {
            position = 0;
        } else if (position == 0) {
            id = pgmInfolists.get(0).getId();
        } else {
            id = pgmInfolists.get(position - 1).getId();
        }
        Log.i(TAG, "position = " + position);
        Log.i(TAG, "id = " + id);
        Intent intent = new Intent("com.wasuali.action.programinfo");
        intent.putExtra("Id", id);
        intent.putExtra("Domain", "");
        intent.putExtra("IsFavorite", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{
            mContext.startActivity(intent);
            Log.d(TAG, "startWasuApp");
        }catch(ActivityNotFoundException e){
            Log.e(TAG, "can not start wasu application");
        }
        
    }

    /**
     * init imageloader.
     * @param context
     */
    public static void initImageLoader(Context context) {
        options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.video_icon_7day_b)
                .showImageOnFail(R.drawable.video_icon_7day_b).cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    private void setForegroud(RemoteViews mViews) {
        String language = Locale.getDefault().getLanguage();
        if (language.equals(Locale.CHINA.getLanguage())) {
            mViews.setImageViewResource(R.id.foreground, R.drawable.video_icon_7day_f);
        } else {
            mViews.setImageViewResource(R.id.foreground, R.drawable.video_icon_7day_fi);
        }
    }

}
