
package com.heran.launcher2.advert;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;

import com.heran.launcher2.R;
import com.heran.launcher2.util.Utils;

@SuppressLint("HandlerLeak")
public class BootAdActivity extends Activity {
    private static final String TAG = "BootAdActivity";

    public BootAdLogic mBootAdLogic;

    public BootAdViewHodler mBootAdViewHodler;

    public static boolean isFullscreen = false;

    private static final int VIDEOVIEW_VISIBLE = 1;

    private static final int PICTURE_VIEW_VISIBLE = 2;

    private boolean finishMstarVideoAd = false;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PICTURE_VIEW_VISIBLE:
                    mBootAdViewHodler.loadNetWorkResources();
                    break;
                // 開始播放開機廣告
                case VIDEOVIEW_VISIBLE:
                    Log.i(TAG, "videoAddress=" + mBootAdLogic.mVideoAddress);
                    // 設定網頁影片
                    // mBootAdViewHodler.mBootVideoView.setVideoPath(Environment.getExternalStorageDirectory()+File.separator+"heran.mp4");
                    mBootAdViewHodler.mBootVideoView.setVideoURI(Uri.parse(mBootAdLogic.mVideoAddress));
                    mBootAdViewHodler.mBootVideoView.requestFocus();
                    mBootAdViewHodler.mBootVideoView.start();
                    mBootAdViewHodler.mBelowTitle.setText(R.string.loading_video);
                    mBootAdViewHodler.mBelowTitle.setBackgroundResource(R.drawable.openaalldsec);
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate()");
        setContentView(R.layout.bootad);
        initData();
    }

    private void initData() {
        mBootAdLogic = new BootAdLogic(this, mHandler);
        mBootAdViewHodler = new BootAdViewHodler(this);
        mBootAdViewHodler.findViews();
        mBootAdViewHodler.registerListens();
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!Utils.isAdVideoFinish()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i(TAG, "finish boot ad play");
                if (!finishMstarVideoAd) {
                    mHandler.sendEmptyMessage(PICTURE_VIEW_VISIBLE);
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "BootAdActivity onDestroy...");
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "BootAdActivity onPause...");
        if (!Utils.isAdVideoFinish()) {   //Utils.isAdVideoFinish()=1 广告结束
            SystemProperties.set("mstar.videoadvert.finished", "1");
//            TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
            finishMstarVideoAd = true;
        }
        mBootAdViewHodler.getHandler().removeCallbacks(mBootAdViewHodler.VideoCountDownRunnable);
        super.onPause();

    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "BootAdActivity onRestart...");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "BootAdActivity onResume...");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "BootAdActivity onStop...");
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int arg0, KeyEvent arg1) {
        Log.i(TAG, "keyevent:" + arg0 + "");
        return super.onKeyDown(arg0, arg1);

    }

}
