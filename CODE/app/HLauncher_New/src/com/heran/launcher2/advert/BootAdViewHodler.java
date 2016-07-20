
package com.heran.launcher2.advert;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.R;
import com.heran.launcher2.util.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class BootAdViewHodler {

    private static final String TAG = "BootAdViewHodler";

    private final BootAdActivity mBootAdActivity;

    public BootVideoView mBootVideoView;

    private MyBtnClickListener myBtnClickListener;

    private MyBtnOnKeyListener myBtnOnKeyListener;

    private final Context mContext;

    private TextView mTopTile;

    public TextView mBelowTitle;

    private FrameLayout mAdPicture;

    private String mSecText1;

    private boolean isClickBtn = false;

    private final Handler mHandler = new Handler();

    private MediaPlayer mediaPlayer;

    // 右上角按鈕選項
    private FrameLayout mMenuFrameLayout;

    private final Button[] mMenuBtn = new Button[10];

    private int mVideoLength = 0;

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
    }

    public void findViews() {
        mBootVideoView = (BootVideoView) mBootAdActivity.findViewById(R.id.videoView);
        mAdPicture = (FrameLayout) mBootAdActivity.findViewById(R.id.img_adCycle);
        mTopTile = (TextView) mBootAdActivity.findViewById(R.id.txt1);
        mBelowTitle = (TextView) mBootAdActivity.findViewById(R.id.txt2);
        mMenuFrameLayout = (FrameLayout) mBootAdActivity.findViewById(R.id.button_menu);
        for (int i = 0; i < 10; i++) {
            mMenuBtn[i] = (Button) mBootAdActivity.findViewById(mMenuBtnId[i]);
        }
    }

    public void registerListens() {
        myBtnClickListener = new MyBtnClickListener();
        myBtnOnKeyListener = new MyBtnOnKeyListener();
        for (int i = 0; i < 10; i++) {
            mMenuBtn[i].setOnClickListener(myBtnClickListener);
            mMenuBtn[i].setOnKeyListener(myBtnOnKeyListener);
        }
        mBootVideoView.setOnPreparedListener(new VideoViewOnPreparedListener());
        mBootVideoView.setOnErrorListener(new VideoViewOnErrorListener());
        mBootVideoView.setOnCompletionListener(new VideoViewOnCompletionListener());
        mBootAdActivity.mBootAdLogic.readLoopValue();
    }

    // 進入launcher後，播放靜態圖片
    public void loadNetWorkResources() {
        mAdPicture.setVisibility(View.VISIBLE);
        if (mBootAdActivity.mBootAdLogic.mLoopValue == 1) {
            mAdPicture.setBackgroundResource(R.drawable.test123_1);
        } else if (mBootAdActivity.mBootAdLogic.mLoopValue == 2) {
            mAdPicture.setBackgroundResource(R.drawable.test123_2);
        }

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
                        Log.d(TAG, "network success!!!");
                        // read json
                        HomeApplication.getInstance().addNetworkTask(mBootAdActivity.mBootAdLogic.chk_date_run);
                        // --------------
                        goToAdVideo = false;
                        Log.i(TAG, "goToAdVideo=" + goToAdVideo);
                    }
                    cancel();
                } else {
                    // 尚未連網
                    Log.i(TAG, "尚未連網");
                    mSecText1 = String.valueOf(20 - (arg0 / 1000));
                    mTopTile.setBackgroundColor(Color.parseColor("#5f000000"));
                    mTopTile.setText("網路偵測：" + mSecText1 + "秒");
                }
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "直接進入全螢幕電視 goToAdVideo=" + goToAdVideo);
                // 直接進入全螢幕電視
                if (goToAdVideo) {
                    mBootVideoView.stopPlayback();
                    BootAdActivity.isFullscreen = false;
                    in.setClass(mContext.getApplicationContext(), HomeActivity.class);
                    mContext.startActivity(in);
                    mBootAdActivity.finish();
                }
            }
        }.start();
    }

    // 右上角按鈕執行動作
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
            Log.d(TAG, "sarah-----onKey::" + keyCode + "!!");
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
                    Log.d(TAG, "sarah-----up!!!!!");
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
            if (mediaPlayer != null) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int oldPosition = -1;
                int lost = (mBootVideoView.getDuration() - currentPosition) / 1000;
                if (oldPosition != currentPosition) {
                    mBelowTitle.setText("廣告剩餘: " + lost + " 秒");
                }
                oldPosition = currentPosition;
                if (mBootVideoView.getDuration() == mediaPlayer.getCurrentPosition()) {
                    mHandler.removeCallbacks(VideoCountDownRunnable);
                } else {
                    mHandler.post(VideoCountDownRunnable);
                }
            }
        }
    };

    // 影片2播放中監聽事件
    class VideoViewOnPreparedListener implements OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer = mp;
            mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    Log.d(TAG, "onInfo, what = " + what);
                    Log.i(TAG, "what==" + what + "======>>>extra=" + extra);
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        disPlayMenuBtn();
                        mAdPicture.setVisibility(View.GONE);
                        mBelowTitle.setBackgroundResource(R.drawable.openaalldsec);
                        Log.i(TAG, "视频将要播放");
                        return true;
                    }
                    return false;
                }

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
            });
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
                    mTopTile.setText("您可以於" + mSecText1 + "秒後\n略過廣告");
                }
            }.start();

            mHandler.post(VideoCountDownRunnable);
        }
    }

    // 廣告按鈕背景圖替換 (選取與否, 按鈕號碼, 配對背景圖)
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

    // 影片2錯誤監聽事件
    class VideoViewOnErrorListener implements OnErrorListener {
        @Override
        public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
            Log.i(TAG, "视频播放错误,会主页 isClickBtn=" + isClickBtn);
            if (isClickBtn == false) {
                int i = -2;
                String error = "異常-EE";
                resumeHomeActivity(false, i, error);
            }
            return false;
        }
    }

    // 影片2結束監聽事件
    class VideoViewOnCompletionListener implements OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer arg0) {
            Log.i(TAG, "视频播放结束,会主页 isClickBtn=" + isClickBtn);
            if (isClickBtn == false) {

                String error = "不做任何選擇-EE";
                resumeHomeActivity(false, -1, error);

            }
        }
    }

    private void hidePlayMenuBtn() {
        for (int a = 0; a < mBootAdActivity.mBootAdLogic.mAdBtnNumber + 2; a++) {
            mMenuBtn[a].setVisibility(View.INVISIBLE);
        }
    }

    public void resumeHomeActivity(boolean isFullscreen, int i, String error) {
        mHandler.removeCallbacks(VideoCountDownRunnable);

        Intent in = new Intent();
        mBelowTitle.setVisibility(View.GONE);
        mBootVideoView.setVisibility(View.GONE);
        hidePlayMenuBtn();
        // 行為紀錄
        mBootAdActivity.mBootAdLogic.startHistory(i, error, mVideoLength, mBootAdActivity.mBootAdLogic.mVideoNumber);
        BootAdActivity.isFullscreen = isFullscreen;
        in.setClass(mContext.getApplicationContext(), HomeActivity.class);
        mContext.startActivity(in);
        mBootAdActivity.finish();
    }

    public void setVisible(int id, int visibility) {
        mBootAdActivity.findViewById(id).setVisibility(visibility);
    }
}
