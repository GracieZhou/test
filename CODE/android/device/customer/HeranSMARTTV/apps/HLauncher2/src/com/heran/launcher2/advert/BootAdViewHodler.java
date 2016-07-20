
package com.heran.launcher2.advert;

import java.io.File;
import java.io.FileNotFoundException;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.R;
import com.heran.launcher2.advert.VideoPlayView.playerCallback;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class BootAdViewHodler {

    private static final String TAG = "BootAdViewHodler";

    private final BootAdActivity mBootAdActivity;

    public VideoPlayView mBootVideoView;

    private MyBtnClickListener myBtnClickListener;

    private MyBtnOnKeyListener myBtnOnKeyListener;

    private final Context mContext;

    private TextView mTopTile;

    public TextView mBelowTitle;

    private FrameLayout mAdPicture;

    private String mSecText1;

    private boolean isClickBtn = false;

    private final Handler mHandler = new Handler();

    private String[] mErrorArrays;

    private FrameLayout mMenuFrameLayout;// 右上角按鈕選項

    private final Button[] mMenuBtn = new Button[10];

    private int mVideoLength = 0;
    
    private ImageView bootad_img ;

    public static boolean isVideoPrepared = false;
    
    public RequestVideoAsyncTask mVideoAsyncTask;

    private static final int[] mMenuBtnId = {
            R.id.menubtn1, R.id.menubtn2, R.id.menubtn3, R.id.menubtn4, R.id.menubtn5, R.id.menubtn6, R.id.menubtn7,
            R.id.menubtn8, R.id.menubtn9, R.id.menubtn10
    };

    private final static int[] mAdMenuId = {
            R.drawable.admenu1_1, R.drawable.admenu2_1, R.drawable.admenu3_1, R.drawable.admenu4_1, R.drawable.admenu5_1
    };

    private final static int[] mAdMenuTwoId = {
            R.drawable.admenu1_2, R.drawable.admenu2_2, R.drawable.admenu3_2, R.drawable.admenu4_2, R.drawable.admenu5_2
    };

    public Handler getHandler() {
        return mHandler;
    }

    public BootAdViewHodler(BootAdActivity mBootAdActivity) {
        super();
        this.mBootAdActivity = mBootAdActivity;
        mContext = mBootAdActivity;
        mErrorArrays = mContext.getResources().getStringArray(R.array.error);
        mVideoAsyncTask = new RequestVideoAsyncTask();
    }

    public void findViews() {
        mBootVideoView = (VideoPlayView) mBootAdActivity.findViewById(R.id.videoView);
        mAdPicture = (FrameLayout) mBootAdActivity.findViewById(R.id.img_adCycle);
        mTopTile = (TextView) mBootAdActivity.findViewById(R.id.txt1);
        mBelowTitle = (TextView) mBootAdActivity.findViewById(R.id.txt2);
        bootad_img = (ImageView)mBootAdActivity.findViewById(R.id.bootad_img);
        mMenuFrameLayout = (FrameLayout) mBootAdActivity.findViewById(R.id.button_menu);
        for (int i = 0; i < 10; i++) {
            mMenuBtn[i] = (Button) mBootAdActivity.findViewById(mMenuBtnId[i]);
        }
        mBelowTitle.setText(R.string.loading_video);
        mBelowTitle.setBackgroundResource(R.drawable.openaalldsec);
    }

    public void registerListens() {
        myBtnClickListener = new MyBtnClickListener();
        myBtnOnKeyListener = new MyBtnOnKeyListener();
        for (int i = 0; i < 10; i++) {
            mMenuBtn[i].setOnClickListener(myBtnClickListener);
            mMenuBtn[i].setOnKeyListener(myBtnOnKeyListener);
        }
        mBootVideoView.setPlayerCallbackListener(plCallback);
        mBootAdActivity.mBootAdLogic.readLoopValue();
    }

    playerCallback plCallback = new playerCallback() {

        @Override
        public void onStopPlayer() {

        }

        @Override
        public void onSeekComplete() {

        }

        @Override
        public void onPrepared(int width, int height) {
            mBootVideoView.start();
            networkCountdown();
            mHandler.post(VideoCountDownRunnable);
        }

        @Override
        public void onPlayEnd() {

        }

        @Override
        public void onPauseComplete() {

        }

        @Override
        public boolean onInfo(int what, int extra) {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                Log.i(TAG, "onInfo VIDEO_TIME_OUT");
                isVideoPrepared = true;
                mHandler.removeMessages(BootAdActivity.VIDEO_TIME_OUT);
                disPlayMenuBtn();
//                mAdPicture.setVisibility(View.GONE);
                bootad_img.setVisibility(View.GONE);
                Log.i(TAG, "video starting");
                return true;
            }
            return false;
        }

        @Override
        public void onFinish() {

        }

        @Override
        public boolean onError(int framework_err, int impl_err) {
            Log.i(TAG, "video error, isClickBtn=" + isClickBtn);
            if (isClickBtn == false) {
                int i = -2;
                String error = mErrorArrays[0];
                resumeHomeActivity(false, i, error);
            }
            return false;
        }

        @Override
        public void onCompletion() {
            Log.i(TAG, "video Complet isClickBtn=" + isClickBtn);
            if (isClickBtn == false) {
                String error = mErrorArrays[1];
                resumeHomeActivity(false, -1, error);
            }
        }

        @Override
        public void onCloseMusic() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onBufferingUpdate(int percent) {
            // TODO Auto-generated method stub

        }
    };

    /**
     * Detection network CountDown
     */
    private void networkCountdown() {
        final int videoLenth = mBootVideoView.getDuration() / 1000;
        mVideoLength = videoLenth;
        new CountDownTimer(5 * 1000, 1 * 1000) {
            @Override
            public void onFinish() {
                mTopTile.setText("");
                mTopTile.setBackgroundColor(Color.parseColor("#00000000"));
                // 5個按鈕介面
                mMenuFrameLayout.setVisibility(View.VISIBLE);
                mMenuBtn[0].requestFocus();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                mSecText1 = String.valueOf(millisUntilFinished / 1000);
                mTopTile.setBackgroundColor(Color.parseColor("#5f000000"));
                mTopTile.setText(String.format(Utils.getResources(mContext, R.string.skip_advertising), mSecText1));
            }
        }.start();
    }

    /**
     * 進入launcher後，播放靜態圖片
     */
    public void loadNetWorkResources() {
    	bootad_img.setVisibility(View.VISIBLE);
    	readPicture(mBootAdActivity.mBootAdLogic.mLoopValue);
    	
//        mAdPicture.setVisibility(View.VISIBLE);
//        if (mBootAdActivity.mBootAdLogic.mLoopValue == 1) {
//            mAdPicture.setBackgroundResource(R.drawable.test123_1);
//        } else if (mBootAdActivity.mBootAdLogic.mLoopValue == 2) {
//            mAdPicture.setBackgroundResource(R.drawable.test123_2);
//        }

        // 偵測連網能力( 20秒 ) 修改秒數，204行也要修改
        new CountDownTimer(20 * 1000, 1 * 1000) {
            Intent in = new Intent();

            boolean goToAdVideo = true;

            @Override
            public void onTick(long arg0) {
                // 判斷是否連網
                if (Utils.isNetConnected(mBootAdActivity)) {
                    // 連網成功，接收json
                    if (goToAdVideo) {
                        Log.d(TAG, "network success!");
                        if(!"com.utsmta.app.MainActivity".equals(Utils.getCurrentActivity(mContext))){
                           mVideoAsyncTask.execute();
                        }
                        goToAdVideo = false;
                    }
                    cancel();
                } else {
                    Log.i(TAG, "no Net ");
                    mSecText1 = String.valueOf(20 - (arg0 / 1000));
                    mTopTile.setBackgroundColor(Color.parseColor("#5f000000"));
                    mTopTile.setText(Utils.getResources(mContext, R.string.network_detection) + mSecText1
                            + Utils.getResources(mContext, R.string.second));
                    Constants.BOOTAD_FINISH = true ;
                }
            }

            @Override
            public void onFinish() {
                Log.i(TAG, " goToAdVideo=" + goToAdVideo);
                // 直接進入全螢幕電視
                if (goToAdVideo && !"com.utsmta.app.MainActivity".equals(Utils.getCurrentActivity(mContext))) {
                    mBootVideoView.stopPlayback();
                    in.setClass(mContext.getApplicationContext(), HomeActivity.class);
                    mContext.startActivity(in);
                    mBootAdActivity.finish();
                }
                Constants.BOOTAD_FINISH = true ;
            }
        }.start();
    }


    
    class RequestVideoAsyncTask extends AsyncTask<Void, Void,Void >{

        @Override
        protected  Void doInBackground(Void... arg0) {
            try {
                String json = mBootAdActivity.mBootAdLogic.getJson(Constants.JSON_ADDRESS);
                mBootAdActivity.mBootAdLogic.parseJson(json);
                if(isCancelled()){
                    Log.d(TAG, "task is canceled!");
                    return null;
                }
                
                if (!TextUtils.isEmpty(mBootAdActivity.mBootAdLogic.mVideoAddress)) {
                    // mBootAdViewHodler.mBootVideoView.setVideoPath(Environment.getExternalStorageDirectory()+File.separator+"heran.mp4");

                    mBootAdActivity.mBootAdViewHodler.mBootVideoView
                            .setVideoPath(mBootAdActivity.mBootAdLogic.mVideoAddress);
                    mBootAdActivity.mBootAdViewHodler.mBootVideoView.requestFocus();
                } else if(!isCancelled()){
                    mBootAdActivity.mBootAdLogic.mHandler.sendEmptyMessage(BootAdActivity.VIDEOVIEW_INVISIBLE);
                }
                Log.d(TAG, "getJson end ");
            } catch (Exception e) {
                Log.d(TAG, "task Exception : " + e);
            }
            return null;
        }
        
    }

    /**
     * 右上角按鈕執行動作
     */
    class MyBtnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            isClickBtn = true;
            int id = 0;
            String btnLastName;
            for (int i = 0; i < 10; i++) {
                if (v.getId() == mMenuBtnId[i]) {
                    id = i;
                    break;
                }
            }
            btnLastName = mBootAdActivity.mBootAdLogic.mBtnName[id];
            mBootAdActivity.mBootAdLogic.startHistory(id, btnLastName, mVideoLength,
                    mBootAdActivity.mBootAdLogic.mVideoNumber);
            mBootAdActivity.mBootAdLogic.goToWeb(mBootAdActivity.mBootAdLogic.mActionNumber[id],
                    mBootAdActivity.mBootAdLogic.mAdBtnLink[id]);
        }
    }

    class MyBtnOnKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            Log.d(TAG, "onKey=" + keyCode);
            boolean btnState = false;
            int btnNumber = 0;
            int matchNumber = 0;
            int allAdBtnNumber = mBootAdActivity.mBootAdLogic.mAllAdBtnNumber;
            int[] btnOpen = mBootAdActivity.mBootAdLogic.mBtnOpen;
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    switch (v.getId()) {
                        case R.id.menubtn1:
                            btnState = true;
                            btnNumber = 0;
                            matchNumber = 0;
                            break;
                        case R.id.menubtn2:
                            if (allAdBtnNumber == 2) {
                                return true;
                            } else {
                                btnState = true;
                                btnNumber = 1;
                                matchNumber = 1;
                            }
                            break;
                        case R.id.menubtn3:
                            if (allAdBtnNumber > 3) {
                                btnState = true;
                                btnNumber = 2;
                                matchNumber = 2;
                            } else {
                                return true;
                            }
                            break;
                        case R.id.menubtn4:
                            if (allAdBtnNumber > 4) {
                                btnState = true;
                                btnNumber = 3;
                                matchNumber = 3;
                            } else {
                                return true;
                            }
                            break;
                        case R.id.menubtn5:
                            if (allAdBtnNumber > 5) {
                                btnState = true;
                                btnNumber = 4;
                                matchNumber = 4;
                            } else {
                                return true;
                            }
                            break;
                        case R.id.menubtn6:
                            if (allAdBtnNumber > 6) {
                                btnState = true;
                                btnNumber = 5;
                                matchNumber = 5;
                            } else {
                                return true;
                            }
                            break;
                        case R.id.menubtn7:
                            if (allAdBtnNumber > 7) {
                                btnState = true;
                                btnNumber = 6;
                                matchNumber = 6;
                            } else {
                                return true;
                            }
                            break;
                        case R.id.menubtn8:
                            if (allAdBtnNumber > 8) {
                                btnState = true;
                                btnNumber = 1;
                                matchNumber = 7;
                            } else {
                                return true;
                            }
                            break;
                        case R.id.menubtn9:
                            if (allAdBtnNumber > 9) {
                                btnState = true;
                                btnNumber = 8;
                                matchNumber = 8;
                            } else {
                                return true;
                            }
                            break;
                        case R.id.menubtn10:
                            return true;
                    }
                    btnPicChange(btnState, btnNumber, btnOpen[matchNumber]);
                    btnPicChange(!btnState, btnNumber + 1, btnOpen[matchNumber + 1]);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    switch (v.getId()) {
                        case R.id.menubtn1:
                            return true;
                        case R.id.menubtn2:
                            btnState = false;
                            btnNumber = 0;
                            matchNumber = 0;
                            break;
                        case R.id.menubtn3:
                            btnState = false;
                            btnNumber = 1;
                            matchNumber = 1;
                            break;
                        case R.id.menubtn4:
                            btnState = false;
                            btnNumber = 2;
                            matchNumber = 2;
                            break;
                        case R.id.menubtn5:
                            btnState = false;
                            btnNumber = 3;
                            matchNumber = 3;
                            break;
                        case R.id.menubtn6:
                            btnState = false;
                            btnNumber = 4;
                            matchNumber = 4;
                            break;
                        case R.id.menubtn7:
                            btnState = false;
                            btnNumber = 5;
                            matchNumber = 5;
                            break;
                        case R.id.menubtn8:
                            btnState = false;
                            btnNumber = 6;
                            matchNumber = 6;
                            break;
                        case R.id.menubtn9:
                            btnState = false;
                            btnNumber = 7;
                            matchNumber = 7;
                            break;
                        case R.id.menubtn10:
                            btnState = false;
                            btnNumber = 8;
                            matchNumber = 8;
                            break;
                    }
                    btnPicChange(btnState, btnNumber, btnOpen[matchNumber]);
                    btnPicChange(!btnState, btnNumber + 1, btnOpen[matchNumber + 1]);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    switch (v.getId()) {
                        case R.id.menubtn1:
                            return true;
                        case R.id.menubtn2:
                            return true;
                        case R.id.menubtn3:
                            return true;
                        case R.id.menubtn4:
                            return true;
                        case R.id.menubtn5:
                            return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * 视频倒计时
     */
    Runnable VideoCountDownRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBootVideoView.isPlaying()) {
                int currentPosition = mBootVideoView.getCurrentPosition();
                int oldPosition = -1;
                int lost = (mBootVideoView.getDuration() - currentPosition) / 1000;
                if (oldPosition != currentPosition) {
                    mBelowTitle
                            .setText(String.format(Utils.getResources(mContext, R.string.advertising_surplus), lost));
                }
                oldPosition = currentPosition;
                if (mBootVideoView.getDuration() == mBootVideoView.getCurrentPosition()) {
                    mHandler.removeCallbacks(VideoCountDownRunnable);
                } else {
                    mHandler.post(VideoCountDownRunnable);
                }
            }
        }
    };

    private void disPlayMenuBtn() {
        boolean flag = false;
        for (int a = 0; a < mBootAdActivity.mBootAdLogic.mAdBtnNumber + 2; a++) {
            mMenuBtn[a].setVisibility(View.VISIBLE);
            if (a == 0) {
                flag = false;
            } else {
                flag = true;
            }
            btnPicChange(flag, a, mBootAdActivity.mBootAdLogic.mBtnOpen[a]);
        }
    }

    /**
     * 廣告按鈕背景圖替換 (選取與否, 按鈕號碼, 配對背景圖)
     * 
     * @param btnState
     * @param btnNumber
     * @param matchNumber
     */
    public void btnPicChange(boolean btnState, int btnNumber, int matchNumber) {
        int id = 0;
        for (int i = 1; i <= 10; i++) {
            if (matchNumber >= 5 && matchNumber == i) {
                if (btnState) {
                    id = R.drawable.admenu5_1;
                } else {
                    id = R.drawable.admenu5_2;
                }
                break;
            } else {
                if (matchNumber == i) {
                    if (btnState) {
                        id = mAdMenuId[i - 1];
                    } else {
                        id = mAdMenuTwoId[i - 1];
                    }
                    break;
                }
            }
        }
        mMenuBtn[btnNumber].setBackgroundResource(id);
    }

    private void hidePlayMenuBtn() {
        for (int a = 0; a < mBootAdActivity.mBootAdLogic.mAdBtnNumber + 2; a++) {
            mMenuBtn[a].setVisibility(View.INVISIBLE);
        }
    }

    public void resumeHomeActivity(boolean isFullscreen, int i, String error) {
        mHandler.removeCallbacks(VideoCountDownRunnable);
        mBootVideoView.stopPlayback();
        Intent in = new Intent();
        mBelowTitle.setVisibility(View.GONE);
        mBootVideoView.setVisibility(View.GONE);
        hidePlayMenuBtn();
        mBootAdActivity.mBootAdLogic.startHistory(i, error, mVideoLength, mBootAdActivity.mBootAdLogic.mVideoNumber);
        if(!"com.utsmta.app.MainActivity".equals(Utils.getCurrentActivity(mContext))){
         in.setClass(mContext.getApplicationContext(), HomeActivity.class);
        }
        mContext.startActivity(in);
        mBootAdActivity.finish();
    }
    private void readPicture(int i){
    	String Path = "/data/video/";
    	File tmpFile = new File(Path,String.valueOf(i)+".png");
    	Uri uri = Uri.fromFile(tmpFile);
   	
    	 ContentResolver cr = mContext.getContentResolver();
         
         try
         {
         //讀取照片，型態為Bitmap
         Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));    
        	 bootad_img.setImageBitmap(bitmap);                         
         } 
         catch (FileNotFoundException e)
         {
        	 bootad_img.setImageResource(R.drawable.test123_1);
        	 Log.i("willy3", e.getMessage());
         }
      }
}
