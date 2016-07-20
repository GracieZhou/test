
package com.heran.launcher2.advert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import scifly.media.EosPlayer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;


public class VideoPlayView extends SurfaceView {

    private String TAG = "VideoPlayView";

    // settable by the client
    private Uri mUri;

    private int mDuration;

    // all possible internal states
    private static final int STATE_ERROR = -1;

    private static final int STATE_IDLE = 0;

    private static final int STATE_PREPARING = 1;

    private static final int STATE_PREPARED = 2;

    private static final int STATE_PLAYING = 3;

    private static final int STATE_PAUSED = 4;

    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private int mCurrentState = STATE_IDLE;

    private int mTargetState = STATE_IDLE;

    // All the stuff we need for playing and showing a video
    private SurfaceHolder mSurfaceHolder = null;

    private EosPlayer mEosPlayer = null;

    private int mVideoWidth;

    private int mVideoHeight;

    private playerCallback myPlayerCallback = null;

    private int mSeekWhenPrepared; // recording the seek position while

    private boolean isVoiceOpen = true;

    private float currentVoice = 1.0f;

    private long startTime;

    private long startSeekTime;

    private long endSeekTime;

    private Context mContext = null;

    // 时移时间
    private int mStartPosition = 0;

    private HandlerThread mHandlerThread = new HandlerThread("VideoPlayView");

    private Handler mHandler = null;

    public static final int BUFFERING_START_PAUSE_TIME = 5 * 60;

    private static final int DELAYFLAG = 999;

    private static final int DELAYFLAG_SHOW = 998;

    private static final int DELAYTIME_SHOW = 300;

    @SuppressLint("HandlerLeak")
    private Handler mDelayHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DELAYFLAG) {
                Log.e(TAG, "Delay play the video 3*1000");
                openPlayer();
                requestLayout();
                invalidate();
            } else {
                Log.e(TAG, "set the surface to TRANSPARENT.");
                setBackgroundColor(Color.TRANSPARENT);
            }
            super.handleMessage(msg);
        }
    };

    public VideoPlayView(Context context) {
        super(context);
        mContext = context;
        initVideoView();
    }

    public VideoPlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        initVideoView();
    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initVideoView();
    }

    private void initVideoView() {
        Log.e(TAG, "initVideoView()");
        mVideoWidth = 0;
        mVideoHeight = 0;
        getHolder().addCallback(mSHCallback);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        if (!mHandlerThread.isAlive()) {
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }
    }

    public void setVideoPath(String path) {
        Log.e(TAG, "setVideoPath(String path)..." + path);
        setVideoPath(path, 0, false);
    }

    public void setVideoPath(String path, int startPosition, boolean isLocal) {
        Log.d(TAG, "setVideoPath... path before:" + path);

        mStartPosition = startPosition / 1000;
        mUri = Uri.parse(path);
        mSeekWhenPrepared = startPosition;
        Log.i(TAG, "setVideoURI::" + mUri + ";mSeekWhenPrepared::" + mSeekWhenPrepared);

        Log.i(TAG, "isLocal:" + isLocal);
        if (isLocal) {
            openPlayer();
            requestLayout();
            invalidate();
        } else {
            mDelayHandler.removeMessages(DELAYFLAG);
            // 去掉延迟3秒
            // mDelayHandler.sendEmptyMessageDelayed(DELAYFLAG, DELAYTIME);
            mDelayHandler.sendEmptyMessage(DELAYFLAG);
        }
    }

    /**
     * call before play next.
     */
    public void stopPlayback() {
        Log.e(TAG, "stopPlayback()..." + (myPlayerCallback == null) + ";mCurrentState=" + mCurrentState);
        if (myPlayerCallback != null) {
            myPlayerCallback.onPlayEnd();
        }
        // if (isNative) {
        // 当前状态不能为IDLE，否则调stop()会报(-38,0)错
        if (mEosPlayer != null && (mCurrentState != STATE_IDLE)) {
            mEosPlayer.stop();
            mEosPlayer.release();
            mEosPlayer = null;
        }

        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        // send message
        if (myPlayerCallback != null)
            myPlayerCallback.onStopPlayer();

    }

    /**
     * When abnormal stop play.
     */
    public void stopPlayer() {
        Log.e(TAG, "stopPlayer().");
        stopPlayback();
    }

    public long getStartTime() {
        Log.e(TAG, "getStartTime()..." + startTime);
        return startTime;
    }

    /**
     * Start player.
     */
    private void openPlayer() {
        Log.i(TAG, "openPlayer: " + mSurfaceHolder + " " + mUri);
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }

        // close the built-in music service of android
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        this.getContext().sendBroadcast(i);
        // Close the user's music callback interface
        if (myPlayerCallback != null)
            myPlayerCallback.onCloseMusic();

        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);

        openPlayerNative();
    }

    private void openPlayerNative() {
        Log.i(TAG, "openPlayerNative: " + mSurfaceHolder + " " + mUri);
        try {
            mEosPlayer = new EosPlayer();
            mDuration = -1;
            mEosPlayer.setOnPreparedListener(mNativePreparedListener);
            mEosPlayer.setOnVideoSizeChangedListener(mNatvieVideoSizeChangedListener);
            mEosPlayer.setOnCompletionListener(mNativeCompletionListener);
            mEosPlayer.setOnErrorListener(mNativeErrorListener);
            mEosPlayer.setOnBufferingUpdateListener(mNativeBufferingUpdateListener);
            mEosPlayer.setOnInfoListener(mNativeInfoListener);
            mEosPlayer.setOnSeekCompleteListener(mNativeSeekCompleteListener);
            startTime = System.currentTimeMillis();
            Log.i(TAG, "==mStartPosition==" + mStartPosition);
            Map<String, String> map = new HashMap<String, String>();
          
            if (mStartPosition >= 0) {
                map.put("x-start-position", "" + mStartPosition);
                mEosPlayer.setDataSource(mContext, mUri, map);
            } else {
                mEosPlayer.setDataSource(mContext, mUri, map);
            }

            if (mSurfaceHolder != null) {
                mEosPlayer.setDisplay(mSurfaceHolder);
            }
            mEosPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mEosPlayer.setScreenOnWhilePlaying(true);
            Log.i(TAG, "prepareAsync: " + mSurfaceHolder);
            mEosPlayer.prepareAsync();
            // we don't set the target state here either, but preserve the
            // target state that was there before.
            Log.i(TAG, "prepareAsync end");
            mCurrentState = STATE_PREPARING;
            mTargetState = STATE_PREPARED;
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            errorCallback(0);
            return;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            errorCallback(0);
            return;
        } catch (IllegalStateException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            errorCallback(0);
            return;
        } catch (SecurityException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            errorCallback(0);
            return;
        }
    }

    private void errorCallback(int errId) {
        Log.e(TAG, "errorCallback(int errId)..." + errId);
        mCurrentState = STATE_ERROR;
        mTargetState = STATE_ERROR;
        if (myPlayerCallback != null)
            myPlayerCallback.onError(EosPlayer.MEDIA_ERROR_UNKNOWN, errId);
    }

    // The following is a series of the player listener in callback
    // VideoSizeChanged
    private EosPlayer.OnVideoSizeChangedListener mNatvieVideoSizeChangedListener = new EosPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(EosPlayer mp, int width, int height) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            Log.d(TAG, "onVideoSizeChanged...mVideoWidth: " + mVideoWidth + ", mVideoHeight " + mVideoHeight);
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                // Note: can't literally change the size of the SurfaceView, can
                // affect the effect of the PIP
                // getHolder().setFixedSize(mVideoWidth, mVideoHeight);
            }
        }
    };

    /**
     * Prepared
     */
    private EosPlayer.OnPreparedListener mNativePreparedListener = new EosPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(EosPlayer mp) {
            mCurrentState = STATE_PREPARED;
            Log.i(TAG, "onPrepared..." + myPlayerCallback);

            // currentVoice = mMediaPlayer.getVolume();
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            if (myPlayerCallback != null) {
                myPlayerCallback.onPrepared(mVideoWidth, mVideoHeight);
            }
            // mSeekWhenPrepared may be changed after seekTo() call
            int seekToPosition = mSeekWhenPrepared;

            if (seekToPosition > 0) {
                Log.i(TAG, "******seekTo***** " + seekToPosition);
                seekTo(seekToPosition);
            }
        }
    };

    /**
     * Completion
     */
    private EosPlayer.OnCompletionListener mNativeCompletionListener = new EosPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(EosPlayer mp) {
            Log.e(TAG, "onCompletion(). mCurrentState:" + mCurrentState);
            if (myPlayerCallback != null && isInPlaybackState()) {
                mCurrentState = STATE_PLAYBACK_COMPLETED;
                mTargetState = STATE_PLAYBACK_COMPLETED;
                myPlayerCallback.onCompletion();
            }
        }
    };

    /**
     * Error
     */
    private EosPlayer.OnErrorListener mNativeErrorListener = new EosPlayer.OnErrorListener() {
        @Override
        public boolean onError(EosPlayer mp, int what, int extra) {
            Log.e(TAG, "onError...what::" + what + ", extra::" + extra);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            /* If an error handler has been supplied, use it and finish. */
            if (myPlayerCallback != null) {
                if (myPlayerCallback.onError(what, extra)) {
                    return true;
                }
            }
            return true;
        }
    };

    /**
     * BufferingUpdate
     */
    private EosPlayer.OnBufferingUpdateListener mNativeBufferingUpdateListener = new EosPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(EosPlayer mp, int percent) {
            if (myPlayerCallback != null)
                myPlayerCallback.onBufferingUpdate(percent);
        }
    };

    /**
     * Info
     */
    private EosPlayer.OnInfoListener mNativeInfoListener = new EosPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(EosPlayer mp, int what, int extra) {
            if (myPlayerCallback != null) {
                myPlayerCallback.onInfo(what, extra);
                mDelayHandler.removeMessages(DELAYFLAG_SHOW);
                mDelayHandler.sendEmptyMessageDelayed(DELAYFLAG_SHOW, DELAYTIME_SHOW);
                return true;
            }
            return false;
        }
    };

    /**
     * SeekComplete
     */
    private EosPlayer.OnSeekCompleteListener mNativeSeekCompleteListener = new EosPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(EosPlayer mp) {
            endSeekTime = System.currentTimeMillis();
            Log.i(TAG, "onSeekComplete...seek time : " + (endSeekTime - startSeekTime) + " ms");
            if (myPlayerCallback != null) {
                myPlayerCallback.onSeekComplete();
            }
        }
    };

    /**
     * Surface relevant callback interface.
     */
    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            mSurfaceHolder = holder;
            Log.i(TAG, "surfaceChanged...width::" + w + ", height::" + h);
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
            if (isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            Log.e(TAG, "surfaceCreated...");
            mSurfaceHolder = holder;
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.e(TAG, "surfaceDestroyed...");
            // after we return from this we can't use the surface any more
            // mSurfaceHolder = null;
            release(true);
        }
    };

    /*
     * release the media player in any state.
     */
    private void release(boolean cleartargetstate) {
        Log.i(TAG, "release...surfaceDestroyed::" + (mTargetState == STATE_IDLE));
        if (mTargetState == STATE_IDLE) {
            return;
        }
        mCurrentState = STATE_IDLE;

        if (myPlayerCallback != null) {
            myPlayerCallback.onFinish();
        }

        if (cleartargetstate) {
            mTargetState = STATE_IDLE;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mEosPlayer != null) {
                        try {
                            mEosPlayer.stop();
                        } catch (IllegalStateException e) {
                            Log.i(TAG, "stop fail! please try again!");
                        }
                        // release will done reset
                        mEosPlayer.release();
                    }
                    mEosPlayer = null;
                }
            });
        } else {
            if (mEosPlayer != null) {
                Log.i(TAG, "mEosPlayer.release()");
                mEosPlayer.release();
            }
            mEosPlayer = null;
        }
    }

    public void start() {
        Log.e(TAG, "start()");
        if (isInPlaybackState()) {
            mEosPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    public void pause() {
        Log.e(TAG, "pause()...");
        if (isInPlaybackState()) {
            if (mEosPlayer.isPlaying())
                mEosPlayer.pause();
            mCurrentState = STATE_PAUSED;
        }
        mTargetState = STATE_PAUSED;
        if (myPlayerCallback != null)
            myPlayerCallback.onPauseComplete();
    }

    /**
     * cache duration as mDuration for faster access.
     * 
     * @return
     */
    public int getDuration() {
        if (isInPlaybackState()) {
            if (mDuration > 0) {
                return mDuration;
            }
            mDuration = mEosPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    /**
     * Get the current play time.
     * 
     * @return
     */
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return mEosPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * Jump to a certain time.
     * 
     * @param msec
     */
    public void seekTo(final int msec) {
        Log.e(TAG, "seekTo(final int msec)...msec::" + msec);
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (isInPlaybackState()) {
                    startSeekTime = System.currentTimeMillis();
                    mEosPlayer.seekTo(msec);
                    mSeekWhenPrepared = 0;
                } else {
                    mSeekWhenPrepared = msec;
                }
            }
        }).start();
    }

    public boolean isPlaying() {
        boolean bPlaying = false;
        if (isInPlaybackState()) {
            if (mEosPlayer == null)
                return false;
            bPlaying = mEosPlayer.isPlaying();
            return bPlaying;
        } else {
            return bPlaying;
        }
    }

    /**
     * Determine whether normal play.
     * 
     * @return
     */
    public boolean isInPlaybackState() {
        boolean bNot_null = false;
        if (mEosPlayer != null) {
            bNot_null = true;
        }
        return (bNot_null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
    }

    public void setVideoScale(int leftMargin, int topMargin, int width, int height) {
        Log.e(TAG, "setVideoScale...");
        LayoutParams lp = (LayoutParams) getLayoutParams();
        lp.height = height;
        lp.width = width;

        setLayoutParams(lp);
    }

    public void setVideoScaleFrameLayout(int leftMargin, int topMargin, int width, int height) {
        Log.e(TAG, "setVideoScaleFrameLayout...");
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            // The following the forced outfit in the decision must be based on
            // the XML type.
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layoutParams;
            params.leftMargin = leftMargin;
            params.rightMargin = leftMargin;
            params.topMargin = topMargin;
            params.bottomMargin = topMargin;
            params.width = width;
            params.height = height;

            setLayoutParams(params);
        }
    }

    public void setVideoScaleLinearLayout(int leftMargin, int topMargin, int width, int height) {
        Log.e(TAG, "setVideoScaleLinearLayout...");
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            // The following the forced outfit in the decision must be based on
            // the XML type.
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutParams;
            params.leftMargin = leftMargin;
            params.rightMargin = leftMargin;
            params.topMargin = topMargin;
            params.bottomMargin = topMargin;
            params.width = width;
            params.height = height;

            setLayoutParams(params);
        }
    }

    public double calculateZoom(double ScrennWidth, double ScrennHeight) {
        double dRet = 1.0;
        double VideoWidth = (double) mVideoWidth;
        double VideoHeight = (double) mVideoHeight;
        double dw = ScrennWidth / VideoWidth;
        double dh = ScrennHeight / VideoHeight;
        if (dw > dh) {
            dRet = dh;
        }
        else {
            dRet = dw;
        }

        return dRet;
    }

    public void setVoice(boolean isSetOpen) {
        Log.e(TAG, "setVoice...");
        if (isInPlaybackState()) {
            if (isSetOpen) {
                mEosPlayer.setVolume(currentVoice, currentVoice);
            } else {
                mEosPlayer.setVolume(0, 0);
            }
            isVoiceOpen = isSetOpen;
        }
    }

    public void addVoice(boolean flag) {
        Log.e(TAG, "addVoice...");
        if (isInPlaybackState()) {
            int voice = getVoice();
            if (flag) {
                if (voice < 10) {
                    voice = voice + 1;
                }
            } else {
                if (voice > 0) {
                    voice = voice - 1;
                }
            }
            setVoice(voice);
        }
    }

    public void setVoice(int voice) {
        Log.e(TAG, "setVoidce..." + voice);
        if (isInPlaybackState()) {
            if (voice >= 0 && voice <= 100) {
                currentVoice = voice * 0.01f;
            }
            Log.i(TAG, "******currentVoice*******" + currentVoice);
            mEosPlayer.setVolume(currentVoice, currentVoice);
        }
    }

    public int getVoice() {
        Log.i(TAG, "getVoice()..." + currentVoice);
        if (isInPlaybackState()) {
            // currentVoice = mEosPlayer.getVolume();
        }
        return (int) (currentVoice * 100);
    }

    public boolean isVoiceOpen() {
        Log.e(TAG, "isVoiceOpen..." + isVoiceOpen);
        return isVoiceOpen;
    }

    /**
     * Register a callback to be invoked
     * 
     * @param l The callback that will be run
     */
    public void setPlayerCallbackListener(playerCallback l) {
        myPlayerCallback = l;
    }

    /**
     * User callback interface.
     */
    public interface playerCallback {

        // error tip
        boolean onError(int framework_err, int impl_err);

        // play complete
        void onCompletion();

        boolean onInfo(int what, int extra);

        void onBufferingUpdate(int percent);

        void onPrepared(int width, int height);

        // Finish back
        void onSeekComplete();

        // Video began to play before, closed music.
        void onCloseMusic();

        // Video began to stop player.
        void onStopPlayer();

        // 统计播放时长
        void onPlayEnd();

        /**
         * 当播放界面被全屏挡住时，finish当前的播放界面
         */
        void onFinish();

        void onPauseComplete();
    }

    /**
     * Set the speed of the video broadcast.
     * 
     * @param speed
     * @return
     */
    public boolean setPlayMode(int speed) {
        if (speed < -32 || speed > 32)
            return false;

        if (isInPlaybackState()) {
            Log.i(TAG, "****setPlayMode***" + speed);
            mEosPlayer.setPlayMode(speed);
            return true;
        }
        return false;
    }

    /**
     * For video broadcast speed.
     * 
     * @return
     */
    public int getPlayMode() {
        if (isInPlaybackState()) {
            return mEosPlayer.getPlayMode();
        }
        return 64;
    }

    public boolean isError(int framework_err, int impl_err) {
        boolean isError = false;
        // 下面都是 API level 17 加的错误.
        final int MEDIA_ERROR_IO = -1004;
        final int MEDIA_ERROR_MALFORMED = -1007;
        final int MEDIA_ERROR_TIMED_OUT = -110;
        final int MEDIA_ERROR_UNSUPPORTED = -1010;
        switch (framework_err) {
            case MEDIA_ERROR_IO:
                isError = true;
                break;
            case MEDIA_ERROR_MALFORMED:
                isError = true;
                break;
            case EosPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                isError = true;
                break;
            case EosPlayer.MEDIA_ERROR_SERVER_DIED:
                isError = true;
                break;
            case MEDIA_ERROR_TIMED_OUT:
                isError = true;
                break;
            case EosPlayer.MEDIA_ERROR_UNKNOWN:
                isError = true;
                break;
            case MEDIA_ERROR_UNSUPPORTED:
                isError = true;
                break;
        }
        return isError;
    }
}
