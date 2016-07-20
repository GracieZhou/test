
package com.eostek.tv.pvr;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.tv.R;
import com.eostek.tv.pvr.adapter.PVRFilePreViewAdapter;
import com.eostek.tv.pvr.bean.ListviewItemBean;
import com.eostek.tv.utils.Constants;
import com.eostek.tv.utils.LogUtil;
import com.eostek.tv.utils.TVUtils;
import com.eostek.tv.utils.TVUtils.PVRHelper;
import com.eostek.tv.utils.UtilsTools;
import com.eostek.tv.widget.AnimatedSelector;
import com.mstar.android.tv.TvPvrManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.PvrFileInfo;

/** 这个文件主要是预览所有PVR录影，选中的时候则播放，点击的时候进入PVR录影 **/
public class PVRFilePreviewActivity extends Activity {

    private static final int DELAYPLAY = 0x09;

    private final static int TOUPDATE_TIME = 0x0001;

    private final static int DELAY_UPDATETIME = 60 * 1000;

    private final static int TOFINISH_TIME = 0x0002;

    private final static int DELAY_TOFINISH_TIME = 3 * 1000;

    private static final int DELAYPLAY_TIME = 1000;

    private ListView mListView;

    private SurfaceView mPVRPlaybackView = null;

    private ProgressBar mPVRPlaybackProgress = null;

    private PVRFilePreViewAdapter mPvrAdapter;

    private ImageView mPVRSelector;

    // add by owen.qin to show the totalTime
    private TextView mTotalTime;

    private TextView mLCNTV;

    private AnimatedSelector mAnimatedSelector;

    private UsbReceiver mUsbReceiver = null;

    private USBDiskSelecter mUsbSelecter = null;

    private boolean mIsScalerInitialized = false;

    private boolean mIsItemClicked = false;

    // listview中被选中播放的Item的index
    private int mCurPvrPosition = 0;

    // 记录当前正在录影的文件index
    private int mCurRecording = -1;

    private boolean mIsRecordingItem = false;

    // if back from full screen,we don't response back key quickly.
    /** 通过分析代码，这个变量只在onPause的时候起到作用，如果为true,则立即finish,否则延时2秒 **/
    private boolean mIsQuickFinish = false;

    /**
     * the first time coming
     */
    private boolean mIsFirstIn = false;

    private ArrayList<ListviewItemBean> mPvrItemList = new ArrayList<ListviewItemBean>();

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DELAYPLAY:
                    try {
                        // construct thumbnail list
                        String fileName = getFileNameByIndex(mCurPvrPosition);
                        LogUtil.i("current Selected fileName = " + fileName);
                        PVRHelper.constructThumbnailList(fileName);
                        if (mCurRecording == mCurPvrPosition) {
                            mIsRecordingItem = true;
                        } else {
                            mIsRecordingItem = false;
                        }
                        playRecordedFile(mCurPvrPosition);
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }
                    break;
                case TOUPDATE_TIME:
                    mLCNTV.setText(UtilsTools.formatDate(System.currentTimeMillis(), UtilsTools.timeformat));
                    mHandler.sendEmptyMessageDelayed(TOUPDATE_TIME, DELAY_UPDATETIME);
                    break;
                case TOFINISH_TIME:
                    mIsQuickFinish = true;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pvr_full_page_browser);

        mLCNTV = ((TextView) findViewById(R.id.pvr_lcn_data));
        mLCNTV.setText(UtilsTools.formatDate(System.currentTimeMillis(), UtilsTools.timeformat));
        // show the total time at first time.
        mTotalTime = (TextView) findViewById(R.id.total_time);
        mListView = (ListView) findViewById(R.id.pvr_listview);
        mPVRPlaybackView = (SurfaceView) findViewById(R.id.pvr_lcn_img);
        createPVRPlaybackView();
        mPVRPlaybackProgress = (ProgressBar) findViewById(R.id.pvr_progressBar);

        mPVRSelector = (ImageView) findViewById(R.id.pvr_selector);
        mAnimatedSelector = new AnimatedSelector(mPVRSelector, mListView.getSelector());
        mListView.setSelector(mAnimatedSelector);

        mUsbSelecter = new USBDiskSelecter(this) {
            @Override
            public void onItemChosen(int position, String diskLabel, String diskPath) {
                // Need to click on a disk to obtain PVR list instead of
                // directly go down.
                initMetaData(diskPath);
            }
        };

        int usbDriverCount = mUsbSelecter.getDriverCount();
        String bestPath = UtilsTools.getBestDiskPath(this);
        LogUtil.i("usbDriverCount is " + usbDriverCount + ";bestPath::" + bestPath);
        // 如果没有存储设备，则直接finish
        if (usbDriverCount <= 0 || UtilsTools.NO_DISK.equals(bestPath)) {
            Toast.makeText(this, R.string.str_pvr_insert_usb, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // 如果存储设备还未选择，则显示对话框来来选择
        if (UtilsTools.CHOOSE_DISK.equals(bestPath)) {
            mUsbSelecter.start();
            return;
        } else {
            // Has set the path and path exists is this the right way.
            if (PVRHelper.isPVRAvailable()) {
                initMetaData(bestPath);
            }
        }

        mIsFirstIn = true;
        mHandler.sendEmptyMessageDelayed(TOUPDATE_TIME, DELAY_UPDATETIME);
    }

    private void initMetaData(String bestPath) {
        LogUtil.i("USB Disk Path = " + bestPath);
        if (bestPath.isEmpty()) {
            LogUtil.i("USB Disk Path is NULL.");
            return;
        }
        // PVR录影文件的文件夹是固定的，类似于/mnt/usb/sda1//_MSTPVR/
        String strMountPath = bestPath + "/_MSTPVR";
        PVRHelper.clearMetadata();
        PVRHelper.setPvrParams(bestPath, (short) 2);
        PVRHelper.createMetadata(strMountPath);
        
        // 初始化Listview数据和设置监听器
        setListviewAdaperAndListener();
        try {
            // 初始化PVR文件列表
            constructRecorderList();
            // 默认显示第一个文件的相关信息
            String fileName = getFileNameByIndex(0);
            int total = PVRHelper.getRecordedFileDurationTime(fileName);
            mTotalTime.setText(UtilsTools.getTimeString(total));
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        PVRHelper.setMetadataSortAscending(true);
        // 启动线程更新播放时间
        new PlayBackProgress().start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListView != null) {
            mListView.setSelection(0);
            if (mListView.isInTouchMode()) {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mListView.setFocusableInTouchMode(true);
                        mListView.requestFocusFromTouch();
                        mListView.requestFocus();
                        mListView.setSelection(0);
                    }
                }, 500);
            }
        }
        mHandler.sendEmptyMessageDelayed(TOFINISH_TIME, DELAY_TOFINISH_TIME);
    }

    private void setListviewAdaperAndListener() {
        mPvrAdapter = new PVRFilePreViewAdapter(this, mPvrItemList, mCurRecording);
        mListView.setAdapter(mPvrAdapter);
        mListView.setDividerHeight(0);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                // 停止播放，设置TV全屏
                try {
                    PVRHelper.stopPvrPlay();
                    /** 这一段逻辑不怎么明白 **/
                    if (mCurPvrPosition == pos) {
                        PVRHelper.startPvrPlayback(getFileNameByIndex(pos));
                        PVRHelper.jumpPlaybackTime(0);
                    }
                    TVUtils.scaleToFullScreen();
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                mIsItemClicked = true;
                // 跳转到PVRActivity，准备录影
                Intent intent = new Intent();
                intent.setClass(PVRFilePreviewActivity.this, PVRActivity.class);
                intent.putExtra(Constants.FULL_PAGE_BROWSER_CALL, true);
                intent.putExtra("channel", mPvrItemList.get(pos).getmRvrLcn() + " "
                        + mPvrItemList.get(pos).getmPvrChannel());
                intent.putExtra("channelEvent", mPvrItemList.get(pos).getmPvrProgramService());
                startActivity(intent);
                finish();
            }
        });

        mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // 选中的时候开始播放选中的录影文件
                mAnimatedSelector.ensureViewVisible();
                mCurPvrPosition = pos;
                mHandler.removeMessages(DELAYPLAY);
                // 如果是第一次进来，则直接播放，不延时
                if (mIsFirstIn) {
                    mHandler.sendEmptyMessageDelayed(DELAYPLAY, 0);
                    mIsFirstIn = false;
                } else {
                    mHandler.sendEmptyMessageDelayed(DELAYPLAY, DELAYPLAY_TIME);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mAnimatedSelector.hideView();
            }
        });

        mListView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && !mListView.isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        mAnimatedSelector.hideView();
                    }
                }
                return false;
            }
        });

        mListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showSelector(hasFocus);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerUSBDetector();
    }

    private void showSelector(boolean bShow) {
        if (mAnimatedSelector == null)
            return;
        if (bShow) {
            mAnimatedSelector.ensureViewVisible();
        } else {
            mAnimatedSelector.hideView();
        }
    }

    @Override
    protected void onPause() {
        super.onStop();
        mUsbSelecter.dismiss();
        unregisterReceiver(mUsbReceiver);
        mUsbReceiver = null;

        // 根据mIsQuickFinish变量来判断finish的时间
        int delayTime = 2000;
        if (mIsQuickFinish) {
            delayTime = 0;
        }
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                try {
                    // 根据是否要进入录影来进行不同操作
                    if (mIsItemClicked) {
                        TVUtils.setVideoMute();
                    } else {
                        PVRHelper.stopPvrPlay();
                    }

                    if (mIsScalerInitialized) {
                        TVUtils.scaleToFullScreen();
                    }
                    finish();
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
            }
        }, delayTime);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_PROG_RED: {
                    // to delete record file.
                    try {
                        String fileName = getFileNameByIndex(mCurPvrPosition);
                        LogUtil.i("get fileName;" + fileName);
                        if (null == fileName) {
                            return false;
                        }
                        // 停止播放并删除文件
                        PVRHelper.stopPvrPlayDirect();
                        PVRHelper.deletefile(0, fileName);
                        // 重新创建PVR文件列表
                        constructRecorderList();
                        /** 原来的代码无此逻辑，如果删除的是最后一个文件，通过mCurPvrPosition会出现异常 **/
                        if (mCurPvrPosition > mPvrItemList.size() - 1) {
                            mCurPvrPosition = mPvrItemList.size() - 1;
                        }
                        fileName = getFileNameByIndex(mCurPvrPosition);
                        PVRHelper.constructThumbnailList(fileName);
                        LogUtil.i("jump filename:" + fileName);
                        PVRHelper.jumpToThumbnail(mCurPvrPosition);
                        return true;
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }
                }
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void setRecordingItem(boolean isRecordingItem) {
        this.mIsRecordingItem = isRecordingItem;
    }

    /**
     * register the broastCast.
     */
    private void registerUSBDetector() {
        mUsbReceiver = new UsbReceiver();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        iFilter.addDataScheme("file");
        registerReceiver(mUsbReceiver, iFilter);
    }

    /**
     * creat play view.
     */
    private void createPVRPlaybackView() {
        Callback callback = new Callback() {

            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            public void surfaceCreated(SurfaceHolder holder) {
                TVUtils.setDisplay(holder);
            }
        };
        mPVRPlaybackView.getHolder().addCallback(callback);
    }

    /**
     * scale small tv window.
     * 
     * @return
     * @throws TvCommonException
     */
    private boolean initTVScaler() throws TvCommonException {
        if (mIsScalerInitialized) {
            return true;
        }
        int[] location = new int[2];
        mPVRPlaybackView.getLocationOnScreen(location);
        int height = mPVRPlaybackView.getHeight();
        int width = mPVRPlaybackView.getWidth();
        int scalerWidth = getResources().getInteger(R.integer.init_tv_scaler_width);
        int scalerHeight = getResources().getInteger(R.integer.init_tv_scaler_height);
        boolean result = TVUtils.setScreenSize(location[0], location[1], width, height, scalerWidth, scalerHeight);
        if (result) {
            mIsScalerInitialized = true;
        }
        return result;
    }

    /**
     * to play the record files.
     * 
     * @param pos
     * @throws TvCommonException
     */
    private void playRecordedFile(int pos) throws TvCommonException {
        String fileName = getFileNameByIndex(pos);
        LogUtil.i("current playback fileName is " + fileName);
        if (fileName == null || !initTVScaler() || PVRHelper.getCurPlaybackingFileName().equals(fileName)) {
            return;
        }
        // start play
        PVRHelper.stopPvrPlay();
        int playbackStatus = PVRHelper.startPvrPlayback(fileName);
        if (TvPvrManager.PVR_STATUS_SUCCESS != playbackStatus) {
            LogUtil.e("playRecordedFile Error");
        }
        // update play progress
        int total = PVRHelper.getRecordedFileDurationTime(fileName);
        LogUtil.i("current playback file totalTime is " + total);
        mPVRPlaybackProgress.setMax(total);
        mTotalTime.setText(UtilsTools.getTimeString(total));
    }

    /**
     * 获取所有的PVR录影文件，并保存在{@link #mPvrItemList} 里面
     * 
     * @throws TvCommonException
     */
    public void constructRecorderList() throws TvCommonException {
        mPvrItemList.clear();
        int pvrFileNumber = PVRHelper.getPvrFileNumber();
        for (int i = 0; i < pvrFileNumber; i++) {
            PvrFileInfo fileInfo = new PvrFileInfo();
            fileInfo = PVRHelper.getPvrFileInfo(i);
            String strFileName = PVRHelper.getCurRecordingFileName();
            if (strFileName.equals(fileInfo.filename)) {
                mCurRecording = i;
            }
            // init the bean object and add to list
            ListviewItemBean item = new ListviewItemBean();
            item.setmRvrLcn("CH " + PVRHelper.getFileLcn(i));
            item.setmPvrChannel(PVRHelper.getFileServiceName(fileInfo.filename));
            item.setmPvrProgramService(PVRHelper.getFileEventName(fileInfo.filename));
            mPvrItemList.add(item);
        }
        // udpate the listview
        mPvrAdapter.notifyDataSetChanged();
        mListView.invalidate();
    }

    private String getFileNameByIndex(int index) throws TvCommonException {
        if (mPvrItemList.isEmpty()) {
            return null;
        }
        return PVRHelper.getFileNameByIndex(index);
    }

    private class PlayBackProgress extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                while (!isFinishing()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 每隔100ms更新一次播放进度
                            int currentTime = PVRHelper.getCurPlaybackTimeInSecond();
                            if (mIsRecordingItem) {
                                int total = PVRHelper.getPvrTotalTime();
                                mTotalTime.setText(UtilsTools.getTimeString(total));
                                mPVRPlaybackProgress.setMax(total);
                            }
                            mPVRPlaybackProgress.setProgress(currentTime);
                        }
                    });
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class UsbReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Uri uri = intent.getData();
            String path = uri.getPath();
            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                String mountPath = null;
                mountPath = PVRHelper.getPvrMountPath();
                // Only remove the disk path is the same as the current PVR
                // mountpath to close the interface. Otherwise, remove the usb
                // does not remove the related equipment.
                if (path.equals(mountPath)) {
                    PVRHelper.clearMetadata();
                    finish();
                }
            }
        }
    }

}
