
package com.eostek.tv.pvr;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.tv.R;
import com.eostek.tv.pvr.USBDiskSelecter.UsbListener;
import com.eostek.tv.pvr.bean.PVRImageFlag;
import com.eostek.tv.utils.ChannelManagerExt;
import com.eostek.tv.utils.Constants;
import com.eostek.tv.utils.LogUtil;
import com.eostek.tv.utils.TVUtils;
import com.eostek.tv.utils.UtilsTools;
import com.eostek.tv.utils.TVUtils.PVRHelper;
import com.eostek.tv.widget.MTSView;
import com.eostek.tv.widget.TextProgress;
import com.eostek.tv.widget.TimeChooser;
import com.mstar.android.MKeyEvent;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPvrManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumPvrStatus;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.PvrPlaybackSpeed.EnumPvrPlaybackSpeed;
import com.mstar.android.tvapi.dtv.vo.DtvAudioInfo;
import com.mstar.android.tvapi.dtv.vo.EpgEventInfo;

@SuppressWarnings("deprecation")
public class PVRActivity extends Activity {

    public enum PVR_MODE {
        // /pvr mode none
        E_PVR_MODE_NONE,
        // /pvr mode record
        E_PVR_MODE_RECORD,
        // /pvr mode playback
        E_PVR_MODE_PLAYBACK,
        // /pvr mode time shift
        E_PVR_MODE_TIME_SHIFT,
        // /pvr mode always time shift
        E_PVR_MODE_ALWAYS_TIME_SHIFT,
        // /pvr mode file browser
        E_PVR_MODE_FILE_BROWSER,
        // /pvr mode short
        E_PVR_MODE_SHORT,
    }

    public enum PVR_AB_LOOP_STATUS {
        // /pvr ab loop not set
        E_PVR_AB_LOOP_STATUS_NONE,
        // /pvr ab loop set a position
        E_PVR_AB_LOOP_STATUS_A,
        // /pvr ab loop set b position
        E_PVR_AB_LOOP_STATUS_AB,
    }

    private static final int INVALID_TIME = 0xFFFFFFFF;

    // save current video's frequency.
    public static int mCurrentRecordingProgrammFrency = -1;

    public static boolean mIsPVRActivityActive = false;

    private PVR_AB_LOOP_STATUS mSetPvrABLoop = PVR_AB_LOOP_STATUS.E_PVR_AB_LOOP_STATUS_NONE;

    private int mPvrABLoopStartTime = INVALID_TIME;

    private int mPvrABLoopEndTime = INVALID_TIME;

    private PVR_MODE mCurPvrMode = PVR_MODE.E_PVR_MODE_NONE;

    private final int DIALOG_SAVING_PROGRESS = 0;

    private final int DIALOG_TIME_CHOOSE = 1;

    private boolean mIsMenuHide = false;

    /** 是否从PVR文件列表中跳转过来的 **/
    private boolean mIsPVRFilePreviewCalled = false;

    private boolean mIsPvrPlayMode = false;

    private boolean mIsPvrShiftTimeMode = false;

    private boolean mIsNotifyRecordStop = false;

    private boolean mIsWatchRcodFilInRcoding = false;

    private TvPvrManager pvr = null;

    private PVRImageFlag mPvrImageFlag = null;

    private RelativeLayout mRootView = null;

    private RelativeLayout mRecordingLayout = null;

    /** 录影 **/
    private ImageButton mRecorderButton = null;

    /** 开始 **/
    private ImageButton mPlayButton = null;

    /** 停止 **/
    private ImageButton mStopButton = null;

    /** 暂停 **/
    private ImageButton mPauseButton = null;

    /** 快退 **/
    private ImageButton mRevButton = null;

    /** 快进 **/
    private ImageButton mFfButton = null;

    /** 慢速播放 **/
    private ImageButton mSlowButton = null;

    /** 选时播放 **/
    private ImageButton mSelectTimeButton = null;

    /** 下一段 **/
    private ImageButton mBackwardButton = null;

    /** 上一段 **/
    private ImageButton mForwardButton = null;

    private TextView mServiceNameText = null;

    private TextView mEventNameText = null;

    private TextView mTotalRecordTime = null;

    private TextView mUsbLabel = null;

    private TextView mUsbPercentage = null;

    private String mRecordDiskPath = null;

    private String mRecordDiskLable = null;

    private USBDiskSelecter mUsbDiskSelecter = null;

    private AnimatorSet mMenuShowAnimation;

    private AnimatorSet mMenuHideAnimation;

    private AnimatorSet mRecordIconAnimation;

    private AlertDialog mTimeChooserDialog = null;

    private TextProgress mProgress = null;

    private ProgressBar mUsbFreeSpaceBar = null;

    private Button mResetJump2Timebtn = null;

    /* For prompt alert dialog to notify user */
    private Dialog mStopRecordDialog = null;

    private KeyEvent mPreviousEvent;

    // ham
    private TextView mTextViewPlay = null;

    private ProgressBar mLoopabProgressBar = null;

    private int mAProgress = 0;

    private android.widget.RelativeLayout.LayoutParams mLoopABLParams;

    private int mLooptime;

    private int mCurrentlooptime;

    private TextView mPlaySpeedTextView;

    private short mAaudioLangPosLive = -1;

    private static final int MENUDISMISS = 0x09;

    private static final int MENUDISMISSDELAYTIME = 10 * 1000;

    private MyUsbReceiver mUsbReceiver = null;

    private String mPvrRecordStr;

    private ChannelManagerExt mChannelManagerExt;

    private MTSView mMtsView = null;

    private int mCurInputSource = TvCommonManager.INPUT_SOURCE_DTV;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MENUDISMISS) {
                mIsMenuHide = true;
                mRootView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pvr_activity);
        findView();

        mIsPVRActivityActive = true;

        pvr = TvPvrManager.getInstance();
        mChannelManagerExt = ChannelManagerExt.getInstance();

        if (getIntent().getExtras() != null) {
            mIsPVRFilePreviewCalled = getIntent().getExtras().getBoolean(Constants.FULL_PAGE_BROWSER_CALL, false);
            int value = getIntent().getExtras().getInt(Constants.PVR_ONE_TOUCH_MODE);
            if (value == Constants.PVR_PLAY_FLAG) {
                mIsPvrPlayMode = true;
            } else if (value == Constants.PVR_TIMESHIFT_FLAG) {
                mIsPvrShiftTimeMode = true;
            }

            // do pvr play
            if (mIsPVRFilePreviewCalled || mIsPvrPlayMode) {
                mRecorderButton.setVisibility(View.GONE);
                findViewById(R.id.usbInfoLayout).setVisibility(View.GONE);
                mProgress.getABProgressBar().setVisibility(View.GONE);
                mPvrImageFlag.setPauseFlag(false);
                mCurPvrMode = PVR_MODE.E_PVR_MODE_PLAYBACK;
            }

            // Do PVR TimeShift!!!!!
            if (mIsPvrShiftTimeMode) {
                mCurPvrMode = PVR_MODE.E_PVR_MODE_TIME_SHIFT;
                // 时移时重新设置字符串的值
                mPvrRecordStr = getResources().getString(R.string.str_pvr_is_time_shift);
            } else {
                // set pvr bar status
                setBarStatusOfStartRecord();
            }
        } else {
            // set pvr bar status
            setBarStatusOfStartRecord();
        }
        LogUtil.i("mPvrRecordStr = " + mPvrRecordStr);

        // show info and start record
        if (mIsPVRFilePreviewCalled) {
            mServiceNameText.setText(getIntent().getStringExtra("channel"));
            mEventNameText.setText(getIntent().getStringExtra("channelEvent"));
        } else {
            ProgramInfo curProgInfo = null;
            EpgEventInfo epgEventInfo = new EpgEventInfo();
            // get service info
            curProgInfo = mChannelManagerExt.getCurProgramInfo();
            String serviceNumberAndNameStr = "CH" + curProgInfo.number + " " + curProgInfo.serviceName;
            mServiceNameText.setText(serviceNumberAndNameStr);
            // get event info
            epgEventInfo = mChannelManagerExt.getCurEpgEventInfo(curProgInfo);
            if (epgEventInfo != null) {
                mEventNameText.setText(epgEventInfo.name);
            }
        }

        if (mIsPVRFilePreviewCalled) {
            mPlayButton.performClick();
            int total = pvr.getRecordedFileDurationTime(pvr.getCurPlaybackingFileName());
            mProgress.setMaxProgress(total);
            mTotalRecordTime.setText(UtilsTools.getTimeString(total));
            /*
             * to avoid request focus on stop or other image button , give the
             * request to invisible image button when the first focus is on the
             * stop button, when press the enter in pvr brower page, it will
             * perform button onclick
             */
            mRecorderButton.setEnabled(false);
            mRecorderButton.requestFocus();
            new PVRFilePreviewCalledPlayBackProgress().start();
        } else {
            if (mIsPvrPlayMode) {
                // if pvr file name is null,just finish
                String pvrFileName = getIntent().getExtras().getString("PVRRECORDEDFILE");
                if (pvrFileName == null) {
                    LogUtil.e("pvrFileName is NULL！");
                    finish();
                    return;
                }
                // start play pvr file
                mPlayButton.performClick();
                // show info
                String pvrFileLcn = "CH " + pvr.getFileLcn(0);
                String pvrFileServiceName = pvr.getFileServiceName(pvrFileName);
                String pvrFileEventName = pvr.getFileEventName(pvrFileName);
                String pvrFileServiceNumberAndNameStr = pvrFileLcn + " " + pvrFileServiceName;
                mServiceNameText.setText(pvrFileServiceNumberAndNameStr);
                mEventNameText.setText(pvrFileEventName);
                LogUtil.d("pvrFileNumber = " + pvr.getPvrFileNumber());
                LogUtil.d("current playback fileName = " + pvrFileName);
                LogUtil.d("pvrFileLcn = " + pvrFileLcn);
                LogUtil.d("pvrFileServiceName = " + pvrFileServiceName);
                LogUtil.d("pvrFileEventName = " + pvrFileEventName);

                // 此处停止PVR播放，对前面的mPlayButton.performClick();没有影响吗
                PVRHelper.stopPvrPlay();
                int playbackStatus = pvr.startPvrPlayback(pvrFileName);
                LogUtil.i("playbackStatus = " + playbackStatus);
                if (playbackStatus != TvPvrManager.PVR_STATUS_SUCCESS) {
                    Toast.makeText(this, R.string.pvrplayererror, Toast.LENGTH_LONG).show();
                }
                // update show info
                int totalTime = pvr.getRecordedFileDurationTime(pvrFileName);
                LogUtil.i("totalTime = " + totalTime);
                mProgress.setMaxProgress(totalTime);
                mTotalRecordTime.setText(UtilsTools.getTimeString(totalTime));
                mStopButton.requestFocus();
                new PlayBackProgress().start();
            } else {
                mRecorderButton.performClick();
            }
        }

        initUsbSelector();
        setButtonListener();
        mHandler.sendEmptyMessageDelayed(MENUDISMISS, MENUDISMISSDELAYTIME);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerUSBDetector();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Shielding global key
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_TV_INPUT:
            case MKeyEvent.KEYCODE_MSTAR_REVEAL:
            case MKeyEvent.KEYCODE_MSTAR_UPDATE:
            case MKeyEvent.KEYCODE_CC:
            case MKeyEvent.KEYCODE_SUBTITLE:
            case KeyEvent.KEYCODE_PROG_GREEN:
            case KeyEvent.KEYCODE_APP_SWITCH:
            case MKeyEvent.KEYCODE_TV_SETTING:
                if (mRootView.getVisibility() != View.VISIBLE) {
                    mRootView.setVisibility(View.VISIBLE);
                }
                mHandler.removeMessages(MENUDISMISS);
                mHandler.sendEmptyMessageDelayed(MENUDISMISS, MENUDISMISSDELAYTIME);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (mRootView.getVisibility() != View.VISIBLE) {
                    mRootView.setVisibility(View.VISIBLE);
                    mHandler.removeMessages(MENUDISMISS);
                    mHandler.sendEmptyMessageDelayed(MENUDISMISS, MENUDISMISSDELAYTIME);
                    return true;
                }
            default:
                break;
        }
        mHandler.removeMessages(MENUDISMISS);
        mHandler.sendEmptyMessageDelayed(MENUDISMISS, MENUDISMISSDELAYTIME);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        boolean bIsTimeShiftRecording = pvr.isTimeShiftRecording();

        if (bIsTimeShiftRecording) {
            // TimeShift
            AlertDialog.Builder dialog = getExitDialog(R.string.str_pvr_time_shift_exit_confirm);
            dialog.show();
        } else {
            // Recording
            if (mPvrImageFlag.isRecorderFlag()) {
                if (mIsMenuHide) {
                    menuShowAnimation();
                } else {
                    AlertDialog.Builder dialog = getExitDialog(R.string.str_pvr_exit_confirm);
                    dialog.show();
                }
            } else {
                if (mIsPvrPlayMode && pvr.isPlaybacking()) {
                    pvr.stopPlayback();
                    pvr.stopPlaybackLoop();
                }
                // if start from PVRFilePrviewActivity,back to it
                if (mIsPVRFilePreviewCalled) {
                    Intent intent = new Intent(this, PVRFilePreviewActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        // Use for press "index" key in PVR recording state then back to PVR
        // activity.
        createAnimation();
        mIsWatchRcodFilInRcoding = false;
        mChannelManagerExt.getAllChannels(this, mCurInputSource);
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // must store the new intent unless getIntent() will return the old one
        setIntent(intent);
        if (getIntent().getExtras() != null) {
            // if the intent is to stop pvr play,then stop it and finsh the
            // Activity
            boolean isNotifyPlaybackStop = (getIntent().getExtras().getInt(Constants.PVR_PLAYBACK_STOP) == 11);
            LogUtil.i("isNotifyRecordStop = " + mIsNotifyRecordStop);
            if (isNotifyPlaybackStop) {
                if (pvr.isPlaybacking()) {
                    updateAudioLanguage();
                    pvr.stopPlayback();
                    pvr.stopPlaybackLoop();
                    finish();
                }
            }

            // if the intent is to stop pvr record,then stop it and show dialog
            int flag = getIntent().getExtras().getInt(Constants.PVR_ONE_TOUCH_MODE);
            if (flag == Constants.PVR_DISMISS_FLAG) {
                mIsNotifyRecordStop = true;
            } else {
                mIsNotifyRecordStop = false;
            }
            if (mIsNotifyRecordStop) {
                saveAndExit();
                // if start by record,go to standby
                PVRHelper.goToStandbySystem();
            }
        }
    }

    private AlertDialog.Builder getExitDialog(int title) {
        // create dialog to exit
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.tips).setMessage(title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                        if (pvr.isPlaybacking()) {
                            updateAudioLanguage();
                            pvr.stopPlayback();
                            pvr.stopPlaybackLoop();
                        }
                        if (pvr.isTimeShiftRecording()) {
                            pvr.stopTimeShift();
                        }
                        mCurrentRecordingProgrammFrency = -1;
                        saveAndExit();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                });
        return dialog;

    }

    private void findView() {
    	mPvrImageFlag = new PVRImageFlag();
        mRootView = (RelativeLayout) findViewById(R.id.pvrrootmenu);
        mRecordingLayout = (RelativeLayout) findViewById(R.id.pvrisrecording);
        mRecorderButton = (ImageButton) findViewById(R.id.player_recorder);
        mPlayButton = (ImageButton) findViewById(R.id.player_play);
        mStopButton = (ImageButton) findViewById(R.id.player_stop);
        mPauseButton = (ImageButton) findViewById(R.id.player_pause);
        mRevButton = (ImageButton) findViewById(R.id.player_rev);
        mFfButton = (ImageButton) findViewById(R.id.player_ff);
        mSlowButton = (ImageButton) findViewById(R.id.player_slow);
        mSelectTimeButton = (ImageButton) findViewById(R.id.player_time);
        mBackwardButton = (ImageButton) findViewById(R.id.player_backward);
        mForwardButton = (ImageButton) findViewById(R.id.player_forward);
        mServiceNameText = (TextView) findViewById(R.id.textView1);
        mEventNameText = (TextView) findViewById(R.id.textView2);
        mTotalRecordTime = (TextView) findViewById(R.id.record_time);
        mTotalRecordTime.setText("00:00:00");
        mPlaySpeedTextView = (TextView) findViewById(R.id.play_speed);
        mPlaySpeedTextView.setVisibility(View.GONE);
        mMtsView = (MTSView) findViewById(R.id.mtsview);
        mUsbLabel = (TextView) findViewById(R.id.usbLabelName);
        mUsbPercentage = (TextView) findViewById(R.id.usbFreeSpacePercent);
        mProgress = (TextProgress) findViewById(R.id.play_record_progress);
        mUsbFreeSpaceBar = (ProgressBar) findViewById(R.id.usbFreeSpace);
        mUsbFreeSpaceBar.setMax(100);
        mPvrImageFlag.setPauseFlag(true);
        // ham
        mTextViewPlay = (TextView) findViewById(R.id.text_view_player_play);
        mLoopabProgressBar = mProgress.getABProgressBar();
        mLoopabProgressBar.setVisibility(View.GONE);
        mLoopABLParams = mProgress.getABParams();

        // 默认录影
        mPvrRecordStr = getResources().getString(R.string.str_pvr_is_recording);

    }

    private void setButtonListener() {
        // 处理录影逻辑
        mRecorderButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                createAnimation();
                try {
                    onKeyRecord();
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
            }
        });

        // 处理播放逻辑
        mPlayButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    onKeyPlay();
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
            }
        });

        // 处理停止播放逻辑
        mStopButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mPvrImageFlag.setStopFlag(true);
                try {
                    onKeyStop();
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
            }
        });

        // 处理暂停播放逻辑
        mPauseButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    onKeyPause();
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
            }
        });

        // 处理快退播放逻辑
        mRevButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    onKeyRev();
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                requestFocus();
                mRevButton.setImageResource(R.drawable.player_rev_focus);
                mPvrImageFlag.setRevFlag(true);
            }
        });

        // 处理快进播放逻辑
        mFfButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    onKeyFF();
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                requestFocus();
                mFfButton.setImageResource(R.drawable.player_ff_focus);
                mPvrImageFlag.setFfFlag(true);
            }
        });

        // 处理慢速播放逻辑
        mSlowButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    onKeySlowMotion();
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                requestFocus();
                mSlowButton.setImageResource(R.drawable.player_slow_focus);
                mPvrImageFlag.setSlowFlag(true);
            }
        });

        // 处理选时播放逻辑
        mSelectTimeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    onKeyGoToTime();
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                mSelectTimeButton.setImageResource(R.drawable.player_time_focus);
                mPvrImageFlag.setTimeFlag(true);
            }
        });

        // 处理播放上一段逻辑
        mForwardButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    onKeyBackward();
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                requestFocus();
                mBackwardButton.setImageResource(R.drawable.player_backward_focus);
                mPvrImageFlag.setBackwardFlag(true);
            }
        });

        // 处理播放下一段逻辑
        mBackwardButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    onKeyForward();
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                requestFocus();
                mForwardButton.setImageResource(R.drawable.player_forward_focus);
                mPvrImageFlag.setFowardFlag(true);
            }
        });
    }

    private void initUsbSelector() {
        mUsbDiskSelecter = new USBDiskSelecter(PVRActivity.this) {

            @Override
            public void onItemChosen(int position, String diskLabel, String diskPath) {
                mRecordDiskPath = diskPath;
                mRecordDiskLable = diskLabel;
                mUsbLabel.setText(diskLabel);
                LogUtil.i("current Selected Disk = " + mRecordDiskPath);
                LogUtil.i("current selected DiskLabel=" + mRecordDiskLable);
                try {
                    if (mIsPvrShiftTimeMode) {
                        doPVRTimeShift(true);
                        new PlayBackProgress().start();
                    } else {
                        // only support FAT format disk
                        if (mRecordDiskLable.regionMatches(6, "FAT", 0, 3)) {
                            doPVRRecord(true);
                            new PlayBackProgress().start();
                        } else if (mRecordDiskLable.regionMatches(6, "NTFS", 0, 4) || !mRecordDiskLable.contains("FAT")) {
                            this.mNoDismiss = true;
                            Toast.makeText(PVRActivity.this, R.string.str_pvr_unsurpt_flsystem, Toast.LENGTH_LONG)
                                    .show();
                            finish();
                            return;
                        }
                    }
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
            }
        };

        // 处理USB mount事件，感觉和最后的MyUsbReceiver功能有重复的部分
        mUsbDiskSelecter.setUSBListener(new UsbListener() {

            @Override
            public void onUSBUnmounted(String diskPath) {
            }

            @Override
            public void onUSBMounted(String diskPath) {
            }

            @Override
            public void onUSBEject(String diskPath) {
                if (mRecordDiskPath == null || !mRecordDiskPath.equals(diskPath)) {
                    return;
                }
                // stop pvr play
                if (pvr.isPlaybacking()) {
                    updateAudioLanguage();
                    pvr.stopPlayback();
                    pvr.stopPlaybackLoop();
                }
                // stop pvr time shift
                if (pvr.isTimeShiftRecording()) {
                    pvr.stopTimeShift();
                }
                pvr.stopRecord();
                finish();
                // move the disk when current status is record in boot.TV will
                // go to standby.
                PVRHelper.goToStandbySystem();
            }
        });
    }

    private void registerUSBDetector() {
        mUsbReceiver = new MyUsbReceiver();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        iFilter.addDataScheme("file");
        registerReceiver(mUsbReceiver, iFilter);
    }

    /**
     * For prompt alert dialog to notify user
     */
    private class OnStopRecordCancelClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            LogUtil.i("OnStopRecordCancelClickListener onClick");
            dialog.dismiss();
            mStopRecordDialog = null;
        }

    }

    private class OnStopRecordConfirmClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            mStopRecordDialog = null;
            if (pvr.isPlaybacking()) {
                updateAudioLanguage();
                pvr.stopPlayback();
                pvr.stopPlaybackLoop();
            }
            saveAndExit();

            switch (mPreviousEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_CHANNEL_UP:
                    mChannelManagerExt.channelUp();
                    break;
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                    mChannelManagerExt.channelDown();
                    break;
                case MKeyEvent.KEYCODE_CHANNEL_RETURN:
                    mChannelManagerExt.channelReturn();
                    break;
            }
            finish();
        }
    }

    private boolean showStopRecordDialog() {
        boolean bRet = true;
        do {
            if (mStopRecordDialog != null && mStopRecordDialog.isShowing()) {
                LogUtil.i("StopRecordDialog allready exist");
                bRet = false;
                break;
            }

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder = dialogBuilder.setTitle(R.string.str_stop_record_dialog_title);
            dialogBuilder = dialogBuilder.setMessage(R.string.str_stop_record_dialog_message);
            dialogBuilder = dialogBuilder
                    .setPositiveButton(android.R.string.ok, new OnStopRecordConfirmClickListener());
            dialogBuilder = dialogBuilder.setNegativeButton(android.R.string.cancel,
                    new OnStopRecordCancelClickListener());
            if (dialogBuilder == null) {
                LogUtil.w("AlertDialog.Builder init fail");
                bRet = false;
                break;
            }

            mStopRecordDialog = dialogBuilder.create();
            if (mStopRecordDialog == null) {
                LogUtil.w("AlertDialog.Builder create dialog fail");
                bRet = false;
                break;
            }

            mStopRecordDialog.show();
        } while (false);
        return bRet;
    }

    private boolean CheckNeedToStopRecord(KeyEvent tEvent) {
        boolean bRet = false;
        do {
            if (!pvr.isRecording()) {
                break;
            }

            int keyCode = tEvent.getKeyCode();
            if ((keyCode != KeyEvent.KEYCODE_CHANNEL_UP) && (keyCode != KeyEvent.KEYCODE_CHANNEL_DOWN)
                    && (keyCode != MKeyEvent.KEYCODE_CHANNEL_RETURN)) {
                break;
            }

            if (mIsMenuHide == true) {
                bRet = true;
                menuShowAnimation();
                break;
            }

            mPreviousEvent = tEvent;
            bRet = showStopRecordDialog();
        } while (false);
        return bRet;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_INFO:
                if (mRootView.getVisibility() != View.VISIBLE) {
                    mRootView.setVisibility(View.VISIBLE);
                }
                return true;
            case KeyEvent.KEYCODE_PROG_BLUE:
                return true;
            case KeyEvent.KEYCODE_PROG_GREEN: {
                return true;
            }
            case KeyEvent.KEYCODE_PROG_YELLOW: {
                return true;
            }
            case MKeyEvent.KEYCODE_MSTAR_INDEX: {
                mIsWatchRcodFilInRcoding = true;
                Intent intent = new Intent(this, PVRFilePreviewActivity.class);
                startActivity(intent);
                return true;
            }
            case MKeyEvent.KEYCODE_MTS: {
                if (mChannelManagerExt.getChannels().size() > 0) {
                    if (mMtsView.isShow()) {
                        mMtsView.changeMtsInfo(mCurInputSource);
                    } else {
                        mMtsView.getMtsInfo(mCurInputSource);
                    }
                }
                break;
            }
            case MKeyEvent.KEYCODE_SUBTITLE: {
                break;
            }
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                mPauseButton.performClick();
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                mPlayButton.performClick();
                return true;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                mFfButton.performClick();
                return true;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                mRevButton.performClick();
                return true;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                mStopButton.performClick();
                return true;
        }
        /* For prompt alert dialog to notify user */
        if (CheckNeedToStopRecord(event) == true) {
            return true;
        }
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            menuShowAnimation();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_SAVING_PROGRESS: {
                ProgressDialog mpDialog = new ProgressDialog(this);
                mpDialog.setMessage(getResources().getString(R.string.str_pvr_program_saving));
                mpDialog.setIndeterminate(false);
                mpDialog.setCancelable(false);
                return mpDialog;
            }
            case DIALOG_TIME_CHOOSE: {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.pvr_menu_dialog, (ViewGroup) findViewById(R.id.pvr_dialog));
                mResetJump2Timebtn = (Button) layout.findViewById(R.id.ResetJ2TBtn);
                mResetJump2Timebtn.setOnClickListener(J2TButtonListener);

                mTimeChooserDialog = new AlertDialog.Builder(PVRActivity.this).setTitle(R.string.str_player_time)
                        .setView(layout).setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.dismiss();
                                mHandler.sendEmptyMessageDelayed(MENUDISMISS, MENUDISMISSDELAYTIME);
                                mSelectTimeButton.setImageResource(R.drawable.playertime);
                                if (mTimeChooserDialog != null) {
                                    int hour = ((TimeChooser) mTimeChooserDialog
                                            .findViewById(R.id.pvr_menu_dialog_hours)).getValue();
                                    int minute = ((TimeChooser) mTimeChooserDialog
                                            .findViewById(R.id.pvr_menu_dialog_minutes)).getValue();
                                    int second = ((TimeChooser) mTimeChooserDialog
                                            .findViewById(R.id.pvr_menu_dialog_seconds)).getValue();
                                    int timeInSecond = hour * 3600 + minute * 60 + second;
                                    pvr.jumpPlaybackTime(timeInSecond);
                                }

                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.dismiss();
                                mHandler.sendEmptyMessageDelayed(MENUDISMISS, MENUDISMISSDELAYTIME);
                                mSelectTimeButton.setImageResource(R.drawable.playertime);
                                mTimeChooserDialog = null;
                            }
                        }).setOnKeyListener(new OnKeyListener() {

                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    dialog.dismiss();
                                    mHandler.sendEmptyMessageDelayed(MENUDISMISS, MENUDISMISSDELAYTIME);
                                    mSelectTimeButton.setImageResource(R.drawable.playertime);
                                    mTimeChooserDialog = null;
                                }
                                return false;
                            }
                        }).show();
                setBarStatusOfPlayToOthers();
                return mTimeChooserDialog;
            }
        }
        return null;
    }

    private OnClickListener J2TButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mTimeChooserDialog == null) {
                return;
            }

            // reset the time when click
            EditText hours, minutes, seconds;
            hours = (TimeChooser) mTimeChooserDialog.findViewById(R.id.pvr_menu_dialog_hours);
            minutes = (TimeChooser) mTimeChooserDialog.findViewById(R.id.pvr_menu_dialog_minutes);
            seconds = (TimeChooser) mTimeChooserDialog.findViewById(R.id.pvr_menu_dialog_seconds);
            hours.setText("");
            minutes.setText("");
            seconds.setText("");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUsbDiskSelecter.dismiss();
        if (mIsPVRFilePreviewCalled && pvr.isPlaybacking()) {
            updateAudioLanguage();
            pvr.stopPlayback();
            pvr.stopPlaybackLoop();
        }
    }

    /**
     * 设置准备录影时各个Button的状态
     */
    public void setBarStatusOfStartRecord() {
        mRecorderButton.setEnabled(true);
        mPlayButton.setEnabled(true);
        mStopButton.setEnabled(true);
        mPauseButton.setEnabled(true);
        mRevButton.setEnabled(false);
        mFfButton.setEnabled(false);
        mSlowButton.setEnabled(false);
        mSelectTimeButton.setEnabled(false);
        mBackwardButton.setEnabled(false);
        mForwardButton.setEnabled(false);
        mRecorderButton.setFocusable(true);
        mPlayButton.setFocusable(true);
        mStopButton.setFocusable(true);
        mPauseButton.setFocusable(true);
        mRevButton.setFocusable(false);
        mFfButton.setFocusable(false);
        mSlowButton.setFocusable(false);
        mSelectTimeButton.setFocusable(false);
        mBackwardButton.setFocusable(false);
        mForwardButton.setFocusable(false);
        clearFocus();
    }

    /**
     * 设置准备播放各个Button的状态
     */
    public void setBarStatusOfRecordToPlay() {
        mRecorderButton.setEnabled(false);
        mPlayButton.setEnabled(false);
        mStopButton.setEnabled(true);
        mPauseButton.setEnabled(true);
        mRevButton.setEnabled(true);
        mFfButton.setEnabled(true);
        mSlowButton.setEnabled(true);
        mSelectTimeButton.setEnabled(true);
        mBackwardButton.setEnabled(true);
        mForwardButton.setEnabled(true);
        mRecorderButton.setFocusable(false);
        mPlayButton.setFocusable(false);
        mStopButton.setFocusable(true);
        mPauseButton.setFocusable(true);
        mRevButton.setFocusable(true);
        mFfButton.setFocusable(true);
        mSlowButton.setFocusable(true);
        mSelectTimeButton.setFocusable(true);
        mBackwardButton.setFocusable(true);
        mForwardButton.setFocusable(true);
        requestFocus();
    }

    /**
     * 设置准备暂停录影时各个Button的状态
     */
    public void setBarStatusOfRecordToPause() {
        mRecorderButton.setEnabled(true);
        mPlayButton.setEnabled(true);
        mStopButton.setEnabled(true);
        mPauseButton.setEnabled(true);
        mRevButton.setEnabled(false);
        mFfButton.setEnabled(false);
        mSlowButton.setEnabled(false);
        mSelectTimeButton.setEnabled(false);
        mBackwardButton.setEnabled(false);
        mForwardButton.setEnabled(false);
        mRecorderButton.setFocusable(true);
        mPlayButton.setFocusable(true);
        mStopButton.setFocusable(true);
        mPauseButton.setFocusable(true);
        mRevButton.setFocusable(false);
        mFfButton.setFocusable(false);
        mSlowButton.setFocusable(false);
        mSelectTimeButton.setFocusable(false);
        mBackwardButton.setFocusable(false);
        mForwardButton.setFocusable(false);
        clearFocus();
    }

    private void requestFocus() {
        mRevButton.setImageResource(R.drawable.player_rev);
        mFfButton.setImageResource(R.drawable.player_ff);
        mSlowButton.setImageResource(R.drawable.player_slow);
        mSelectTimeButton.setImageResource(R.drawable.playertime);
        mBackwardButton.setImageResource(R.drawable.player_backward);
        mForwardButton.setImageResource(R.drawable.player_forward);
    }

    private void clearFocus() {
        mRevButton.setImageResource(R.drawable.player_rev_d);
        mFfButton.setImageResource(R.drawable.player_ff_d);
        mSlowButton.setImageResource(R.drawable.player_slow_d);
        mSelectTimeButton.setImageResource(R.drawable.playertime_d);
        mBackwardButton.setImageResource(R.drawable.player_backward_d);
        mForwardButton.setImageResource(R.drawable.player_forward_d);
    }

    public void setBarStatusOfPlayToPause() {
        mRecorderButton.setEnabled(true);
        mPlayButton.setEnabled(true);
        mStopButton.setEnabled(false);
        mPauseButton.setEnabled(true);
        mRevButton.setEnabled(true);
        mFfButton.setEnabled(true);
        mSlowButton.setEnabled(true);
        mSelectTimeButton.setEnabled(true);
        mBackwardButton.setEnabled(true);
        mForwardButton.setEnabled(true);
        mRecorderButton.setFocusable(true);
        mPlayButton.setFocusable(true);
        mStopButton.setFocusable(false);
        mPauseButton.setFocusable(true);
        mRevButton.setFocusable(true);
        mFfButton.setFocusable(true);
        mSlowButton.setFocusable(true);
        mSelectTimeButton.setFocusable(true);
        mBackwardButton.setFocusable(true);
        mForwardButton.setFocusable(true);
        requestFocus();
    }

    public void setBarStatusOfPlayToOthers() {
        mRecorderButton.setEnabled(true);
        mPlayButton.setEnabled(true);
        mStopButton.setEnabled(true);
        mPauseButton.setEnabled(true);
        mRevButton.setEnabled(true);
        mFfButton.setEnabled(true);
        mSlowButton.setEnabled(true);
        mSelectTimeButton.setEnabled(true);
        mBackwardButton.setEnabled(true);
        mForwardButton.setEnabled(true);
        mRecorderButton.setFocusable(true);
        mPlayButton.setFocusable(true);
        mStopButton.setFocusable(true);
        mPauseButton.setFocusable(true);
        mRevButton.setFocusable(true);
        mFfButton.setFocusable(true);
        mSlowButton.setFocusable(true);
        mSelectTimeButton.setFocusable(true);
        mBackwardButton.setFocusable(true);
        mForwardButton.setFocusable(true);
        requestFocus();
    }

    /**
     * 处理录影时逻辑
     * 
     * @throws TvCommonException
     */
    public void onKeyRecord() throws TvCommonException {
        // print the log for debug
        LogUtil.i("pvrImageFlag.isRecorderFlag()=" + mPvrImageFlag.isRecorderFlag());
        LogUtil.i("pvr.isRecordPaused()=" + pvr.isRecordPaused());
        LogUtil.i("pvr.getMountPath=" + pvr.getPvrMountPath());
        LogUtil.i("pvr.isRecording =" + pvr.isRecording());

        if (!mPvrImageFlag.isRecorderFlag()) {
            if (pvr.isRecording()) {
                // 如果正在录影，则不需要选择Disk
                mRecordDiskPath = pvr.getPvrMountPath();
                mRecordDiskLable = UtilsTools.getUsbLabelByPath(this, new String(mRecordDiskPath));
                mUsbLabel.setText(mRecordDiskLable);
                if (mIsPvrShiftTimeMode) {
                    doPVRTimeShift(true);
                    new PlayBackProgress().start();
                } else {
                    doPVRRecord(true);
                    new PlayBackProgress().start();
                }
            } else {
                // 获取所有挂载USB设备
                int usbDriverCount = mUsbDiskSelecter.getDriverCount();
                if (usbDriverCount <= 0) {
                    // 没有找到挂载设备，直接finish
                    Toast.makeText(PVRActivity.this, R.string.str_pvr_insert_usb, Toast.LENGTH_LONG).show();
                    if (PVRHelper.isBootedByRecord()) {
                        // to cancel the epg
                        TVUtils.cancelValidEpgTimerEvent();
                        PVRHelper.goToStandbySystem();
                    }
                    finish();
                } else {
                    // 找到挂载设备
                    String diskPath = UtilsTools.getBestDiskPath(this);
                    LogUtil.i("getBestDiskPath:" + diskPath);
                    if (UtilsTools.NO_DISK.equals(diskPath)) {
                        // 挂载设备不可用，直接finish
                        Toast.makeText(PVRActivity.this, R.string.str_pvr_insert_usb, Toast.LENGTH_LONG).show();
                        if (PVRHelper.isBootedByRecord()) {
                            TVUtils.cancelValidEpgTimerEvent();
                            PVRHelper.goToStandbySystem();
                        }
                        finish();
                    } else if (UtilsTools.CHOOSE_DISK.equals(diskPath)) {
                        LogUtil.i("choose disk,isBootedByRecord=" + PVRHelper.isBootedByRecord());
                        if (PVRHelper.isBootedByRecord()) {
                            // Screen cases recorded in authority, is
                            // automatically
                            // choose the disk for recording.
                            diskPath = UtilsTools.getAvaliableDiskForStandBy(PVRActivity.this);
                            LogUtil.i("getAvaliableDiskForStandBy=" + diskPath);
                            if (diskPath == null) {
                                TVUtils.cancelValidEpgTimerEvent();
                                PVRHelper.goToStandbySystem();
                            }
                        } else {
                            // 弹出挂载设备选择框
                            mUsbDiskSelecter.start();
                        }
                    } else {
                        // 找到可用的挂载设备
                        String diskLabel = UtilsTools.getUsbLabelByPath(PVRActivity.this, new String(diskPath));
                        mRecordDiskPath = diskPath;
                        mRecordDiskLable = diskLabel;
                        mUsbLabel.setText(diskLabel);
                        LogUtil.i("set disk to read information.");

                        try {
                            if (mIsPvrShiftTimeMode) {
                                doPVRTimeShift(true);
                                new PlayBackProgress().start();
                            } else {
                                // only support FAT format disk
                                if (mRecordDiskLable.regionMatches(6, "FAT", 0, 3)) {
                                    doPVRRecord(true);
                                    new PlayBackProgress().start();
                                } else if (mRecordDiskLable.regionMatches(6, "NTFS", 0, 4)
                                        || !mRecordDiskLable.contains("FAT")) {
                                    Toast.makeText(PVRActivity.this, R.string.str_pvr_unsurpt_flsystem,
                                            Toast.LENGTH_LONG).show();
                                    finish();
                                    PVRHelper.goToStandbySystem();
                                }
                            }
                        } catch (TvCommonException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        } else {
            if (pvr.isRecordPaused()) {
                // if in pause state,start pvr record
                doPVRRecord(true);
            } else {
                // if in pvr record state, pause pvr record
                doPVRRecord(false);
            }
        }
    }

    public void onKeyPlay() throws TvCommonException {
        if (mCurPvrMode == PVR_MODE.E_PVR_MODE_NONE) {
            return;
        }
        mPlayButton.setImageResource(R.drawable.player_play_focus);
        mPvrImageFlag.setPlayFlag(true);
        if (pvr.isTimeShiftRecording()) {
            // 处理时移录影逻辑
            mPlayButton.requestFocus();
            mPlaySpeedTextView.setVisibility(View.GONE);
            mPlaySpeedTextView.setText("");
            // 获取当前PVR播放速度，如果速度是非法的，则打印error,如果正常，则设置当前速度
            if (pvr.getPvrPlaybackSpeed() == EnumPvrPlaybackSpeed.E_PVR_PLAYBACK_SPEED_INVALID.ordinal()) {
                // copy the code from mstar tv player
                int errorCode = TvPvrManager.PVR_STATUS_SUCCESS;
                if (pvr.isAlwaysTimeShiftPlaybackPaused()) {
                    errorCode = pvr.startAlwaysTimeShiftPlayback();
                } else {
                    errorCode = pvr.startPvrTimeShiftPlayback();
                }
                if (TvPvrManager.PVR_STATUS_SUCCESS != errorCode) {
                    LogUtil.e("TimeShiftPlayback ErrorCode: " + errorCode);
                    return;
                }
            } else {
                mPlayButton.requestFocus();
                setPvrPlaybackSpeed(pvr.getPvrPlaybackSpeed());
            }
        } else {
            if (mCurPvrMode == PVR_MODE.E_PVR_MODE_PLAYBACK) {
                // 正在播放
                mPlayButton.requestFocus();
                mPlaySpeedTextView.setVisibility(View.VISIBLE);
                mPlaySpeedTextView.setText("");
                setPvrPlaybackSpeed(pvr.getPvrPlaybackSpeed());
            } else if (mCurPvrMode == PVR_MODE.E_PVR_MODE_RECORD) {
                mPlayButton.requestFocus();
                if (pvr.isPlaybacking()) {
                    // 在录影的同时也在播放
                    setPvrPlaybackSpeed(pvr.getPvrPlaybackSpeed());
                } else {
                    // 仅在录影
                    String strFileName = pvr.getCurRecordingFileName();
                    if (TvPvrManager.PVR_STATUS_SUCCESS != pvr.startPvrPlayback(strFileName)) {
                        LogUtil.e("startPlayback is not E_SUCCESS!");
                        return;
                    }
                    pvr.assignThumbnailFileInfoHandler(strFileName);
                    mCurPvrMode = PVR_MODE.E_PVR_MODE_PLAYBACK;
                }
            } else if (mCurPvrMode == PVR_MODE.E_PVR_MODE_SHORT) {
                LogUtil.e("curPvrMode is SHORT!");
            }

            // 更新Button状态
            if (mCurPvrMode == PVR_MODE.E_PVR_MODE_PLAYBACK || mCurPvrMode == PVR_MODE.E_PVR_MODE_TIME_SHIFT) {
                setBarStatusOfPlayToOthers();
            } else {
                setBarStatusOfRecordToPlay();
            }
        }
    }

    private void setPvrPlaybackSpeed(int speed) {
        switch (speed) {
            case TvPvrManager.PVR_PLAYBACK_SPEED_0X:
                pvr.resumePlayback();
                break;
            case TvPvrManager.PVR_PLAYBACK_SPEED_1X:
                try {
                    OnClick_ABLoop();
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                break;
            default:
                pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_1X);
                mPlaySpeedTextView.setVisibility(View.GONE);
                break;
        }
    }

    public void onKeyStop() throws TvCommonException {
        LogUtil.i("curPvrMode is :" + mCurPvrMode);
        if (mCurPvrMode == PVR_MODE.E_PVR_MODE_NONE) {
            return;
        }
        // 这一段的代码逻辑看不懂，暂时不动
        if (mCurPvrMode == PVR_MODE.E_PVR_MODE_PLAYBACK || mCurPvrMode == PVR_MODE.E_PVR_MODE_RECORD
                || mCurPvrMode == PVR_MODE.E_PVR_MODE_TIME_SHIFT) {
            if (mCurPvrMode == PVR_MODE.E_PVR_MODE_PLAYBACK || mCurPvrMode == PVR_MODE.E_PVR_MODE_TIME_SHIFT) {
                mPlayButton.setImageResource(R.drawable.player_play);
            }
            if (pvr.isPlaybacking()) {
                updateAudioLanguage();
                if (mPvrABLoopStartTime != INVALID_TIME) {
                    pvr.stopPlaybackLoop();
                    mPvrABLoopStartTime = mPvrABLoopEndTime = INVALID_TIME;
                    mLoopabProgressBar.setVisibility(View.INVISIBLE);
                    mTextViewPlay.setVisibility(View.GONE);
                    mTextViewPlay.setText(getString(R.string.str_player_play));
                    if (mIsPVRFilePreviewCalled) {
                        finish();
                    }
                    return;
                } else if (mIsPvrPlayMode) {
                    pvr.stopPlayback();
                    finish();
                }
            }
        }

        if (mCurPvrMode == PVR_MODE.E_PVR_MODE_RECORD) {// record & play
            if (pvr.isPlaybacking()) {
                // Reset_Start_Time();
                pvr.stopPlayback();
                setBarStatusOfRecordToPause();
                mPvrABLoopStartTime = mPvrABLoopEndTime = INVALID_TIME;
            } else if (pvr.isRecording()) {
                onBackPressed();
            }

        } else if (mCurPvrMode == PVR_MODE.E_PVR_MODE_TIME_SHIFT) {
            if (pvr.isPlaybacking()) {
                // Reset_Start_Time();
                pvr.stopPlayback();
                setBarStatusOfRecordToPause();
                mPvrABLoopStartTime = mPvrABLoopEndTime = INVALID_TIME;
                return;
            } else if (pvr.isTimeShiftRecording()) {
                onBackPressed();
            }
        } else if (mCurPvrMode == PVR_MODE.E_PVR_MODE_ALWAYS_TIME_SHIFT) {
            if (pvr.isPlaybacking()) {
                // close always time shift playback part.
                pvr.stopAlwaysTimeShiftPlayback();
            }
            mLoopabProgressBar.setVisibility(View.INVISIBLE);
            pvr.pausePvrAlwaysTimeShiftPlayback(false);
            return;
        } else if (mCurPvrMode == PVR_MODE.E_PVR_MODE_PLAYBACK) {
            if (pvr.isPlaybacking()) {
                pvr.stopPlayback();
            }
            if (pvr.isRecording()) {
                mPvrABLoopStartTime = mPvrABLoopEndTime = INVALID_TIME;
                setBarStatusOfRecordToPause();
                // SetPlaySpeedIcon();
                // DoValueUpdate_PVR_Playback_Time(0);
                if (mCurPvrMode == PVR_MODE.E_PVR_MODE_PLAYBACK) {
                    mCurPvrMode = PVR_MODE.E_PVR_MODE_RECORD;
                }
            } else {
                finish();
            }
        }
    }

    public void onKeyPause() throws TvCommonException {
        LogUtil.i("curPvrMode is :" + mCurPvrMode);
        if (mCurPvrMode == PVR_MODE.E_PVR_MODE_NONE) {
            return;
        }

        if (pvr.isTimeShiftRecording()) {
            if (pvr.isPlaybacking()) {
                handlePlaybackPause();
            } else {
                // freeze img and setup start time
                pvr.pausePvrAlwaysTimeShiftPlayback(true);
                setBarStatusOfRecordToPause();
            }
        } else if (mCurPvrMode == PVR_MODE.E_PVR_MODE_PLAYBACK) {
            LogUtil.i("stepInPlayback !!!!");
            handlePlaybackPause();
        } else if (mCurPvrMode == PVR_MODE.E_PVR_MODE_RECORD) {
            // record & play
            if (pvr.isPlaybacking()) {
                handlePlaybackPause();
            } else {
                pvr.pauseRecord();
            }
        }
        mPauseButton.requestFocus();
        return;
    }

    private void handlePlaybackPause() {
        pvr.stepInPlayback();
        // stop or pause
        setBarStatusOfRecordToPause();
    }

    /**
     * To determine whether the current reversal.
     * 
     * @return
     */
    private boolean isFastBackPlaying() {
        int speed = pvr.getPvrPlaybackSpeed();
        if (speed >= EnumPvrPlaybackSpeed.E_PVR_PLAYBACK_SPEED_32XFB.ordinal()
                && speed <= EnumPvrPlaybackSpeed.E_PVR_PLAYBACK_SPEED_1XFB.ordinal()) {
            return true;
        }
        return false;
    }

    public void onKeyRev() throws TvCommonException {
        // 设置播放速度及显示文字
        if (pvr.isPlaybacking()) {
            int curPlayBackSpeed = pvr.getPvrPlaybackSpeed();
            Log.v("tag", "onKeyRev:curPlayBackSpeed=" + curPlayBackSpeed);
            switch (curPlayBackSpeed) {
                case TvPvrManager.PVR_PLAYBACK_SPEED_1X:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_FB_1X);
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("1X");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_FB_1X:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_FB_2X);
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("2X");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_FB_2X:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_FB_4X);
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("4X");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_FB_4X:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_FB_8X);
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("8X");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_FB_8X:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_FB_16X);
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("16X");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_FB_16X:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_FB_32X);
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("32X");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_FB_32X:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_FB_32X);
                    mPlaySpeedTextView.setVisibility(View.GONE);
                    mPlaySpeedTextView.setText("");
                    break;
                default:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_FB_1X);
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("1X");
                    break;
            }
            // pvr bar status change
            setBarStatusOfPlayToOthers();
        }
    }

    public void onKeyFF() throws TvCommonException {
        if (pvr.isPlaybacking()) {
            int curPlayBackSpeed = pvr.getPvrPlaybackSpeed();
            Log.v("tag", "onKeyFF:curPlayBackSpeed=" + curPlayBackSpeed);
            switch (curPlayBackSpeed) {
                case TvPvrManager.PVR_PLAYBACK_SPEED_1X:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_FF_2X);
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("2X");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_FF_2X:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_FF_4X);
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("4X");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_FF_4X:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_FF_8X);
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("8X");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_FF_8X:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_FF_16X);
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("16X");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_FF_16X:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_FF_32X);
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("32X");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_FF_32X:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_1X);
                    mPlaySpeedTextView.setVisibility(View.GONE);
                    mPlaySpeedTextView.setText("");
                    break;
                default:
                    pvr.setPvrPlaybackSpeed(TvPvrManager.PVR_PLAYBACK_SPEED_FF_2X);
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("2X");
                    break;
            }
            // pvr bar status change
            setBarStatusOfPlayToOthers();
        }
    }

    public void onKeySlowMotion() throws TvCommonException {
        // slow motion
        if (pvr.isPlaybacking()) {
            int curPlayBackSpeed = pvr.getPvrPlaybackSpeed();
            switch (curPlayBackSpeed) {
                case TvPvrManager.PVR_PLAYBACK_SPEED_SF_32X:
                    pvr.setPvrPlaybackSpeed(EnumPvrPlaybackSpeed.E_PVR_PLAYBACK_SPEED_1X.ordinal());
                    mPlaySpeedTextView.setVisibility(View.GONE);
                    mPlaySpeedTextView.setText("");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_SF_16X:
                    pvr.setPvrPlaybackSpeed(EnumPvrPlaybackSpeed.E_PVR_PLAYBACK_SPEED_FF_1_32X.ordinal());
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("Slow/32");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_SF_8X:
                    pvr.setPvrPlaybackSpeed(EnumPvrPlaybackSpeed.E_PVR_PLAYBACK_SPEED_FF_1_16X.ordinal());
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("Slow/16");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_SF_4X:
                    pvr.setPvrPlaybackSpeed(EnumPvrPlaybackSpeed.E_PVR_PLAYBACK_SPEED_FF_1_8X.ordinal());
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("Slow/8");
                    break;
                case TvPvrManager.PVR_PLAYBACK_SPEED_SF_2X:
                    pvr.setPvrPlaybackSpeed(EnumPvrPlaybackSpeed.E_PVR_PLAYBACK_SPEED_FF_1_4X.ordinal());
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("Slow/4");
                    break;
                default:
                    pvr.setPvrPlaybackSpeed(EnumPvrPlaybackSpeed.E_PVR_PLAYBACK_SPEED_FF_1_2X.ordinal());
                    mPlaySpeedTextView.setVisibility(View.VISIBLE);
                    mPlaySpeedTextView.setText("Slow/2");
                    break;
            }
            // pvr bar status change
            setBarStatusOfPlayToOthers();
        }
    }

    public void onKeyGoToTime() throws TvCommonException {
        if (pvr.isPlaybacking()) {
            mHandler.removeMessages(MENUDISMISS);
            showDialog(DIALOG_TIME_CHOOSE);
        }
    }

    /** 处理播放上一段逻辑 **/
    public void onKeyBackward() throws TvCommonException {
        int PVRCurPlaybackTime = 0;
        // for ATshift
        if (!pvr.isPlaybacking() && pvr.isRecording() && mCurPvrMode == PVR_MODE.E_PVR_MODE_ALWAYS_TIME_SHIFT) {
            if (pvr.isAlwaysTimeShiftPlaybackPaused()) {
                if (pvr.startAlwaysTimeShiftPlayback() != TvPvrManager.PVR_STATUS_SUCCESS) {
                    return;
                }
            } else {
                pvr.pausePvrAlwaysTimeShiftPlayback(true);
                if (pvr.startAlwaysTimeShiftPlayback() != TvPvrManager.PVR_STATUS_SUCCESS) {
                    return;
                }
                setBarStatusOfRecordToPause();
                mPlayButton.requestFocus();
            }
            // update time
            PVRCurPlaybackTime = pvr.getCurPlaybackTimeInSecond();
            setBarStatusOfPlayToOthers();
        }

        if (pvr.isPlaybacking() && !pvr.isPlaybackPaused()
                && pvr.getPvrPlaybackSpeed() != EnumPvrPlaybackSpeed.E_PVR_PLAYBACK_SPEED_STEP_IN.ordinal()) {
            PVRCurPlaybackTime = pvr.getCurPlaybackTimeInSecond();
            if (PVRCurPlaybackTime > 30) {
                // Normal Jump Backward
                pvr.jumpPlaybackTime(PVRCurPlaybackTime - 30);
            } else {
                // Jump To Head
                pvr.jumpPlaybackTime(0);
            }
        }
    }

    public void onKeyForward() throws TvCommonException {
        // backward
        if (pvr.isPlaybacking() && !pvr.isPlaybackPaused()
                && pvr.getPvrPlaybackSpeed() != EnumPvrPlaybackSpeed.E_PVR_PLAYBACK_SPEED_STEP_IN.ordinal()) {
            TvManager.getInstance().getPvrManager().doPlaybackJumpForward();
        }
        // pvr bar status change
        setBarStatusOfPlayToOthers();
    }

    /** 处理循环播放点击逻辑 **/
    private void OnClick_ABLoop() throws TvCommonException {
        if (mSetPvrABLoop == PVR_AB_LOOP_STATUS.E_PVR_AB_LOOP_STATUS_NONE) {
            mPvrABLoopStartTime = pvr.getCurPlaybackTimeInSecond();
            mSetPvrABLoop = PVR_AB_LOOP_STATUS.E_PVR_AB_LOOP_STATUS_A;
            mTextViewPlay.setVisibility(View.VISIBLE);
            mTextViewPlay.setText(getString(R.string.str_player_play) + " A");
            mAProgress = mProgress.getProgress();

            int x = mProgress.getWidth() * mAProgress / mProgress.getProgressMax();
            mLoopABLParams.leftMargin = x;

            mLooptime = 0;
            mLoopabProgressBar.setMax(mLooptime);
            mLoopabProgressBar.setProgress(mLooptime);
            mLoopabProgressBar.setLayoutParams(mLoopABLParams);
            mLoopabProgressBar.setVisibility(View.VISIBLE);
        } else if (mSetPvrABLoop == PVR_AB_LOOP_STATUS.E_PVR_AB_LOOP_STATUS_A) {
            mPvrABLoopEndTime = pvr.getCurPlaybackTimeInSecond();
            LogUtil.i("b-a=" + (mPvrABLoopEndTime - mPvrABLoopStartTime));
            if (mPvrABLoopEndTime - mPvrABLoopStartTime <= 2) {
                Toast.makeText(this, R.string.pvraberror, Toast.LENGTH_SHORT).show();
                return;
            }
            pvr.startPlaybackLoop(mPvrABLoopStartTime, mPvrABLoopEndTime);
            mSetPvrABLoop = PVR_AB_LOOP_STATUS.E_PVR_AB_LOOP_STATUS_AB;
            mTextViewPlay.setVisibility(View.VISIBLE);
            mTextViewPlay.setText(getString(R.string.str_player_play) + " A-B");
            mLoopABLParams.width = (mProgress.getProgress() - mAProgress) * mProgress.getWidth()
                    / mProgress.getProgressMax();
            mLoopabProgressBar.setLayoutParams(mLoopABLParams);
            mLooptime++;
            mLoopabProgressBar.setMax(mLooptime);
            mCurrentlooptime = 0;
        } else {
            pvr.stopPlaybackLoop();
            mSetPvrABLoop = PVR_AB_LOOP_STATUS.E_PVR_AB_LOOP_STATUS_NONE;
            mTextViewPlay.setVisibility(View.GONE);
            mTextViewPlay.setText(getString(R.string.str_player_play));
            mLoopABLParams.width = 0;
            mLoopabProgressBar.setMax(0);
            mLoopabProgressBar.setVisibility(View.GONE);
        }

    }

    /**
     * 处理点击录影逻辑
     * 
     * @param type true时开始录影，false时暂停录影
     * @throws TvCommonException
     */
    private void doPVRRecord(boolean type) throws TvCommonException {
        if (!(mCurPvrMode == PVR_MODE.E_PVR_MODE_PLAYBACK || mCurPvrMode == PVR_MODE.E_PVR_MODE_TIME_SHIFT)) {
            mPlayButton.setImageResource(R.drawable.player_play);
        }
        if (type) {
            // 开始PVR录影
            mRecorderButton.setImageResource(R.drawable.player_recorder_focus);
            mRecordingLayout.setVisibility(View.VISIBLE);
            mPvrImageFlag.setRecorderFlag(true);
            mRecordIconAnimation.start();
            if (pvr.isRecordPaused()) {
                pvr.resumeRecord();
                return;
            }

            if (!pvr.isRecording()) {
                // 路径为空则直接返回
                if (mRecordDiskPath.isEmpty()) {
                    LogUtil.e("USB Disk Path is NULL !!!");
                    return;
                }
                // 只支持FAT格式的disk
                LogUtil.i("USB Disk Path = " + mRecordDiskPath);
                LogUtil.i("USB Disk Label = " + mRecordDiskLable);
                if (mRecordDiskLable.regionMatches(6, "FAT", 0, 3)) {
                    pvr.setPvrParams(mRecordDiskPath, (short) 2);
                } else if (mRecordDiskLable.regionMatches(6, "NTFS", 0, 4)) {
                    Toast.makeText(PVRActivity.this, R.string.formattip, Toast.LENGTH_SHORT).show();
                    return;
                }

                int status = pvr.startPvrRecord();
                LogUtil.i("status=" + status);
                // 处理录影失败
                if (status != EnumPvrStatus.E_SUCCESS.ordinal()) {
                    if (PVRHelper.isBootedByRecord()) {
                        TVUtils.cancelValidEpgTimerEvent();
                        PVRHelper.goToStandbySystem();
                        return;
                    }
                    // 弹出失败提示
                    if (status == EnumPvrStatus.E_ERROR_RECORD_OUT_OF_DISK_SPACE.ordinal()) {
                        Toast.makeText(this, R.string.pvrrecordfull, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, R.string.pvrrecordfail, Toast.LENGTH_LONG).show();
                    }

                    // 停止播放
                    if (pvr.isPlaybacking()) {
                        updateAudioLanguage();
                        pvr.stopPlayback();
                        pvr.stopPlaybackLoop();
                    }
                    // 停止time shift
                    if (pvr.isTimeShiftRecording()) {
                        pvr.stopTimeShift();
                    }
                    mCurrentRecordingProgrammFrency = -1;
                    saveAndExit();
                    return;
                }
            }

            mCurrentRecordingProgrammFrency = ChannelManagerExt.getInstance().getCurInfo().frequency;
            mCurPvrMode = PVR_MODE.E_PVR_MODE_RECORD;
            String strFileName = pvr.getCurRecordingFileName();
            LogUtil.i("doPVRRecord: current recording fileName = " + strFileName);
        } else {
            // 如果正在录影，则暂停录影
            mRecordingLayout.setVisibility(View.GONE);
            mRecordIconAnimation.end();
            mRecorderButton.setImageResource(R.drawable.player_recorder);
            pvr.pauseRecord();
        }
    }

    /**
     * 处理点击时移逻辑
     * 
     * @param type true时开始时移，false时暂停时移
     * @throws TvCommonException
     */
    private void doPVRTimeShift(boolean type) throws TvCommonException {
        // stop or pause
        pvr.stepInPlayback();
        setBarStatusOfStartRecord();
        mRecorderButton.setEnabled(false);
        mRecorderButton.setFocusable(false);

        if (type) {
            mRecorderButton.setImageResource(R.drawable.player_recorder_focus);
            mRecordingLayout.setVisibility(View.VISIBLE);
            mPvrImageFlag.setRecorderFlag(false);
            mRecordIconAnimation.start();
            mCurrentRecordingProgrammFrency = ChannelManagerExt.getInstance().getCurInfo().frequency;
            if (pvr.isRecordPaused()) {
                pvr.resumeRecord();
            } else {
                pvr.setPvrParams(mRecordDiskPath, (short) 2);
                int statue = pvr.startPvrTimeShiftRecord();
                if (statue == EnumPvrStatus.E_ERROR_TIMESHIFT_OUT_OF_DISK_SPACE.ordinal()) {
                    Toast.makeText(this, R.string.str_pvr_timeshift_filespaceerror, Toast.LENGTH_LONG).show();
                    finish();
                }
                mCurPvrMode = PVR_MODE.E_PVR_MODE_TIME_SHIFT;
            }
        } else {
            mRecordingLayout.setVisibility(View.GONE);
            mRecordIconAnimation.end();
            mRecorderButton.setImageResource(R.drawable.player_recorder);
            pvr.stopTimeShiftRecord();
        }
    }

    /**
     * update the current tv language
     */
    private void updateAudioLanguage() {
        DtvAudioInfo audioInfo = new DtvAudioInfo();
        TvChannelManager cd = TvChannelManager.getInstance();
        audioInfo = cd.getAudioInfo();
        if (audioInfo != null) {
            if (mAaudioLangPosLive != -1 && audioInfo.audioLangNum > mAaudioLangPosLive
                    && audioInfo.currentAudioIndex != mAaudioLangPosLive) {
                cd.switchAudioTrack(mAaudioLangPosLive);
                mAaudioLangPosLive = -1;
            }
        }
    }

    /**
     * 停止录影及finish
     */
    private void saveAndExit() {
        LogUtil.i("saveAndExit()");
        showDialog(DIALOG_SAVING_PROGRESS);
        pvr.stopRecord();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mRecorderButton.setImageResource(R.drawable.player_recorder);
                        mPvrImageFlag.setRecorderFlag(false);

                        if (!isFinishing()) {
                            dismissDialog(DIALOG_SAVING_PROGRESS);
                        }
                        LogUtil.i("finish()");
                        finish();
                    }
                }, 1500);
            }
        }).start();
    }

    private void updateUSBInfo() {
        int percent = UtilsTools.getAvailablePercent(mRecordDiskPath);
        mUsbPercentage.setText(percent + "%");
        mUsbFreeSpaceBar.setProgress(percent);
        // 如果内存使用超过98%，则停止播放和录影，并退出
        if (percent > 98) {
            Toast.makeText(PVRActivity.this, R.string.pvr_disk_full, Toast.LENGTH_LONG).show();
            if (pvr.isPlaybacking()) {
                updateAudioLanguage();
                pvr.stopPlayback();
                pvr.stopPlaybackLoop();
            }
            if (pvr.isTimeShiftRecording()) {
                pvr.stopTimeShift();
            }
            mCurrentRecordingProgrammFrency = -1;
            saveAndExit();
        }
    }

    private void createAnimation() {
        if (mMenuShowAnimation != null && mMenuHideAnimation != null) {
            return;
        }
        int height = mRootView.getHeight() + mRootView.getPaddingBottom();
        ObjectAnimator fadeInAlphaAnim = ObjectAnimator.ofFloat(mRootView, "alpha", 0f, 1f);
        fadeInAlphaAnim.setInterpolator(new DecelerateInterpolator());
        fadeInAlphaAnim.setDuration(300);
        ObjectAnimator fadeOutAlphaAnim = ObjectAnimator.ofFloat(mRootView, "alpha", 1f, 0f);
        fadeOutAlphaAnim.setInterpolator(new DecelerateInterpolator());
        fadeOutAlphaAnim.setDuration(300);
        ObjectAnimator moveUpAnim = ObjectAnimator.ofFloat(mRootView, "translationY", height, 0);
        moveUpAnim.setInterpolator(new DecelerateInterpolator());
        moveUpAnim.setDuration(300);
        ObjectAnimator moveDownAnim = ObjectAnimator.ofFloat(mRootView, "translationY", 0, height);
        moveDownAnim.setInterpolator(new DecelerateInterpolator());
        moveDownAnim.setDuration(300);
        mMenuShowAnimation = new AnimatorSet();
        mMenuHideAnimation = new AnimatorSet();
        mRecordIconAnimation = new AnimatorSet();
        mMenuShowAnimation.play(moveUpAnim).with(fadeInAlphaAnim);
        mMenuHideAnimation.play(moveDownAnim).with(fadeOutAlphaAnim);
        mMenuShowAnimation.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                mIsMenuHide = false;
                mRootView.setVisibility(View.VISIBLE);
                mRootView.requestFocus();
            }
        });

        mMenuHideAnimation.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                mRootView.setVisibility(View.GONE);
                mIsMenuHide = true;
            }
        });
        fadeOutAlphaAnim = ObjectAnimator.ofFloat(mRecordingLayout.findViewById(R.id.pvrrecordimage), "alpha", 1f, 0f);
        fadeOutAlphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeOutAlphaAnim.setDuration(2000);
        fadeOutAlphaAnim.setRepeatCount(Animation.INFINITE);
        fadeOutAlphaAnim.setRepeatMode(Animation.RESTART);
        fadeOutAlphaAnim.addListener(new AnimatorListener() {
            int count = 0;

            TextView text = (TextView) mRecordingLayout.findViewById(R.id.pvrrecordtext);

            @Override
            public void onAnimationStart(Animator animation) {
                text.setText(mPvrRecordStr + ".");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                String str = mPvrRecordStr;
                for (int i = 0; i < count + 1; i++) {
                    str += ".";
                }
                text.setText(str);
                count = (count + 1) % 8;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        mRecordIconAnimation.play(fadeOutAlphaAnim);
    }

    private void menuShowAnimation() {
        LogUtil.i("menuShow is " + mIsMenuHide);
        if (mIsMenuHide && mMenuShowAnimation != null && mMenuHideAnimation != null) {
            mMenuHideAnimation.end();
            mMenuShowAnimation.start();
        }
    }

    /** 更新播放进度 **/
    private class PlayBackProgress extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                LogUtil.i("isPlaybacking=" + pvr.isPlaybacking() + ";isFastBackPlaying=" + isFastBackPlaying());
                // Only [Record] & [TimeShift] mode need to update USB info.
                if (pvr.isRecording() || pvr.isTimeShiftRecording()) {
                    new usbInfoUpdate().start();
                }

                while ((pvr.isPlaybacking() || pvr.isRecording()) && !isFinishing()) {
                    final int currentTime = pvr.getCurPlaybackTimeInSecond();
                    // 获取PVR文件总时长
                    int tmpTime = 0;
                    if (pvr.isRecording() && !mIsWatchRcodFilInRcoding) {
                        tmpTime = pvr.getCurRecordTimeInSecond();
                    } else {
                        tmpTime = pvr.getRecordedFileDurationTime(pvr.getCurPlaybackingFileName());
                    }
                    final int total = tmpTime;
                    LogUtil.i("current time = " + currentTime + ";total = " + total);

                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            mProgress.setProgressMax(total);
                            mTotalRecordTime.setText(UtilsTools.getTimeString(total));

                            // 更新进度
                            int progress = -1;
                            if (mSetPvrABLoop == PVR_AB_LOOP_STATUS.E_PVR_AB_LOOP_STATUS_AB) {
                                mCurrentlooptime++;
                                progress = 0;
                                mLoopabProgressBar.setProgress(mCurrentlooptime % mLooptime + 1);
                            } else if (mSetPvrABLoop == PVR_AB_LOOP_STATUS.E_PVR_AB_LOOP_STATUS_A) {
                                mLooptime++;
                                progress = currentTime;
                                mLoopABLParams.width = (mProgress.getProgress() - mAProgress) * mProgress.getWidth()
                                        / mProgress.getProgressMax();
                                mLoopabProgressBar.setLayoutParams(mLoopABLParams);
                            } else {
                                progress = currentTime;
                            }
                            mProgress.setTextProgress(UtilsTools.getTimeString(currentTime), progress);

                            // 如果当前时间currentTime是非法的，则停止播放并退出
                            boolean timeIsValid = false;
                            if (pvr.isPlaybacking()) {
                                if (currentTime >= total || (isFastBackPlaying() && currentTime <= 0)) {
                                    timeIsValid = true;
                                }
                            }
                            if (timeIsValid) {
                                updateAudioLanguage();
                                pvr.stopPlayback();
                                pvr.stopPlaybackLoop();
                                if (pvr.isTimeShiftRecording()) {
                                    pvr.stopTimeShiftRecord();
                                }
                                finish();
                            }
                        }
                    });
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /** 处理从PVR文件列表中跳转过来时，播放进度更新 **/
    private class PVRFilePreviewCalledPlayBackProgress extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                while ((pvr.isPlaybacking() || pvr.isRecording()) && !isFinishing()) {
                    final int currentTime = pvr.getCurPlaybackTimeInSecond();
                    final int total = pvr.getRecordedFileDurationTime(pvr.getCurPlaybackingFileName());
                    LogUtil.i("current time = " + currentTime + ";tatal Time=" + total);

                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            mProgress.setTextProgress(UtilsTools.getTimeString(currentTime), currentTime);
                            // ham
                            if (mSetPvrABLoop == PVR_AB_LOOP_STATUS.E_PVR_AB_LOOP_STATUS_A) {
                                mLoopABLParams.width = (mProgress.getProgress() - mAProgress) * mProgress.getWidth()
                                        / mProgress.getProgressMax();
                                mLooptime++;
                                mLoopabProgressBar.setLayoutParams(mLoopABLParams);
                            }

                            // 如果当前时间currentTime是非法的，则停止播放并退出
                            boolean timeIsValid = false;
                            if (pvr.isPlaybacking()) {
                                if (currentTime >= total || (isFastBackPlaying() && currentTime <= 0)) {
                                    timeIsValid = true;
                                }
                            }
                            if (timeIsValid) {
                                updateAudioLanguage();
                                pvr.jumpPlaybackTime(0);
                                Intent intent = new Intent(PVRActivity.this, PVRFilePreviewActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /** 更新USB信息 **/
    private class usbInfoUpdate extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                while (!isFinishing()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateUSBInfo();
                        }
                    });
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        saveAndExit();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mUsbReceiver);
        mUsbReceiver = null;
        mIsPVRActivityActive = false;
    }

    private class MyUsbReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Uri uri = intent.getData();
            String path = uri.getPath();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {

            } else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                String mountPath = null;
                mountPath = pvr.getPvrMountPath();
                String tmp = path;// Lost the path of the disk.
                // Only remove the disk path is the same as the current PVR
                // mountpath to close the interface. Otherwise, remove the usb
                // does not remove the related equipment.
                if (tmp.equals(mountPath)) {
                    pvr.clearMetadata();
                    finish();
                }
            }
        }
    }
}
