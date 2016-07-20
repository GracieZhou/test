
package com.heran.launcher2.advert;

import com.heran.launcher2.R;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

@SuppressLint("HandlerLeak")
public class BootAdActivity extends Activity {
    private static final String TAG = "BootAdActivity";

    public BootAdLogic mBootAdLogic;

    public BootAdViewHodler mBootAdViewHodler;

    public static final int VIDEOVIEW_INVISIBLE = 1;

    private static final int PICTURE_VIEW_VISIBLE = VIDEOVIEW_INVISIBLE + 1;

    public static final int VIDEO_TIME_OUT = VIDEOVIEW_INVISIBLE + 2;

    public static final int VIDEO_TIME_OUT_LENGTH = 1 * 45 * 1000;// 45s

    private boolean finishMstarVideoAd = false;

    private String[] mBtnArraysName;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PICTURE_VIEW_VISIBLE:
                    mBootAdViewHodler.loadNetWorkResources();
                    mHandler.sendEmptyMessageDelayed(BootAdActivity.VIDEO_TIME_OUT,
                            BootAdActivity.VIDEO_TIME_OUT_LENGTH);
                    break;
                case VIDEOVIEW_INVISIBLE:
                    Log.i("BootAdLogic", "videoAddress=" + mBootAdLogic.mVideoAddress);
                    if (TextUtils.isEmpty(mBootAdLogic.mVideoAddress)) {
                        final int i = -2;
                        final String error = mBtnArraysName[3];
                        UIUtil.showToast(BootAdActivity.this, getResources().getString(R.string.network_not_good));
                        mBootAdViewHodler.resumeHomeActivity(false, i, error);
                    }
                    break;
                case VIDEO_TIME_OUT:
                    if (!BootAdViewHodler.isVideoPrepared) {
                        mBootAdViewHodler.mBootVideoView.stopPlayer();
                        Log.i(TAG, "video time out");
                        final int i = -2;
                        final String error = mBtnArraysName[3];
                        UIUtil.showToast(BootAdActivity.this, getResources().getString(R.string.network_not_good));
                        mBootAdViewHodler.resumeHomeActivity(false, i, error);
                    }
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bootad);
        initData();
    }

    private void initData() {
        mBootAdLogic = new BootAdLogic(this, mHandler);
        mBootAdViewHodler = new BootAdViewHodler(this);
        mBootAdViewHodler.findViews();
        mBootAdViewHodler.registerListens();
        mBtnArraysName = getResources().getStringArray(R.array.ad_btn_name);
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
                Log.d(TAG, "finish boot ad play");
                if (!finishMstarVideoAd) {
                    mHandler.sendEmptyMessage(PICTURE_VIEW_VISIBLE);
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "BootAdActivity onDestroy...");
        mHandler.removeMessages(BootAdActivity.VIDEO_TIME_OUT);
        mHandler.removeCallbacksAndMessages(null);
        clearDatas();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "BootAdActivity onPause...");
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
        if("com.utsmta.app.MainActivity".equals(Utils.getCurrentActivity(this))){
            finish();
        }
        if (!Utils.isAdVideoFinish()) {
            SystemProperties.set("mstar.videoadvert.finished", "1");
            finishMstarVideoAd = true;
        }
        mBootAdViewHodler.getHandler().removeCallbacks(mBootAdViewHodler.VideoCountDownRunnable);
        if (mBootAdViewHodler.mBootVideoView.isPlaying()) {
            mBootAdViewHodler.mBootVideoView.stopPlayer();
        }
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent keyevent) {
        Log.d(TAG, "keyevent:" + keycode + "");
        switch (keycode) {
            case KeyEvent.KEYCODE_BACK:
                clearDatas();
                break;

            default:
                break;
        }
        return super.onKeyDown(keycode, keyevent);

    }
    
    private void clearDatas(){
        if (mBootAdViewHodler.mVideoAsyncTask != null && mBootAdViewHodler.mVideoAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            mBootAdViewHodler.mVideoAsyncTask.cancel(true);  
            mBootAdViewHodler.mVideoAsyncTask = null;
        }
        if(mBootAdViewHodler.mBootVideoView!=null){
            mBootAdViewHodler.mBootVideoView.stopPlayer();
        }
    }

}
