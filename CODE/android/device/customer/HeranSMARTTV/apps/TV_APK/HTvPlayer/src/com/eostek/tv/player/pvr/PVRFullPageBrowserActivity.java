
package com.eostek.tv.player.pvr;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.tv.player.R;
import com.eostek.tv.player.util.UtilsTools;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPvrManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumScreenMuteType;
import com.mstar.android.tvapi.common.vo.PvrFileInfo;
import com.mstar.android.tvapi.common.vo.VideoWindowType;

public class PVRFullPageBrowserActivity extends Activity {

    private final String TAG = "PVRFullPageBrowserActivity";

    private ListView listview;

    private LinearLayout gallery;

    private SurfaceView PVRPlaybackView = null;

    private ProgressBar PVRPlaybackProgress = null;

    // private ImageAdapter imageAdapter;
    private PVRListViewAdapter pvrAdapter;

    private ArrayList<listViewHolder> pvrList = new ArrayList<listViewHolder>();

    private TvPvrManager pvr = null;

    private UsbReceiver usbReceiver = null;

    private Handler handler = new Handler();

    private PVRThumbnail thumbnail = null;

    private USBDiskSelecter usbSelecter = null;

    // add by owen.qin to show the totalTime
    private TextView totalTime;

    private boolean IsScalerInitialized = false;

    private boolean IsItemClicked = false;

    private int current_pvr_position = 0;

    private TextView lcn_data_TV;

    private int current_recording = -1;

    private boolean isRecordingItem = false;

    private static final int DELAYPLAY = 0x09;

    private static final int DELAYPLAY_TIME = 1000;

    private final static int TOUPDATE_TIME = 0x0001;

    private final static int DELAY_UPDATETIME = 60 * 1000;

    private final static int TOFINISH_TIME = 0x0002;

    private final static int DELAY_TOFINISH_TIME = 3 * 1000;

    // if back from full screen,we don't response back key quickly.
    private boolean isQuickFinish = false;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DELAYPLAY:
                    try {
                        // construct thumbnail list
                        String fileName = getFileNameByIndex(current_pvr_position);
                        Log.d(TAG, "current Selected fileName = " + fileName);
                        constructThumbnailList(fileName);
                        if (current_recording == current_pvr_position) {
                            isRecordingItem = true;
                        } else {
                            isRecordingItem = false;
                        }
                        playRecordedFile(current_pvr_position);
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }
                    break;
                case TOUPDATE_TIME:
                    lcn_data_TV.setText(UtilsTools.formatDate(System.currentTimeMillis(), UtilsTools.timeformat));
                    myHandler.sendEmptyMessageDelayed(TOUPDATE_TIME, DELAY_UPDATETIME);
                    break;
                case TOFINISH_TIME:
                    isQuickFinish = true;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eos_pvr_full_page_browser);
        pvr = TvPvrManager.getInstance();
        lcn_data_TV = ((TextView) findViewById(R.id.pvr_lcn_data));
        lcn_data_TV.setText(UtilsTools.formatDate(System.currentTimeMillis(), UtilsTools.timeformat));
        myHandler.sendEmptyMessageDelayed(TOUPDATE_TIME, DELAY_UPDATETIME);
        listview = (ListView) findViewById(R.id.pvr_listview);
        PVRPlaybackView = (SurfaceView) findViewById(R.id.pvr_lcn_img);
        PVRPlaybackProgress = (ProgressBar) findViewById(R.id.pvr_progressBar);
        createPVRPlaybackView();
        usbSelecter = new USBDiskSelecter(this) {
            @Override
            public void onItemChosen(int position, String diskLabel, String diskPath) {
                initMetaData(diskPath);
                // Need to click on a disk to obtain PVR list instead of
                // directly go down.
            }
        };
        int usbDriverCount = usbSelecter.getDriverCount();
        String bestPath = usbSelecter.getBestDiskPath();
        Log.e(TAG, "usbDriverCount is " + usbDriverCount);
        Log.e(TAG, "bestPath::" + bestPath);
        if (usbDriverCount <= 0 || USBDiskSelecter.NO_DISK.equals(bestPath)) {
            Toast.makeText(this, R.string.str_pvr_insert_usb, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
//        createPVRPlaybackView();
        if (USBDiskSelecter.CHOOSE_DISK.equals(bestPath)) {
            usbSelecter.start();
            return;
        } else {
            // Has set the path and path exists is this the right way.
            initMetaData(bestPath);
        }
    }

    private void initMetaData(String bestPath) {
        if (bestPath.isEmpty()) {
            Log.e(TAG, "USB Disk Path is NULL.");
            return;
        }
        String strMountPath = bestPath + "/_MSTPVR";
        Log.d(TAG, "USB Disk Path = " + bestPath);
        pvr.clearMetadata();
        pvr.setPvrParams(bestPath, (short) 2);
        pvr.createMetadata(strMountPath);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Handler focusHandler = new Handler();
        if (listview != null && listview.isInTouchMode()) {
            focusHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    listview.setFocusableInTouchMode(true);
                    listview.requestFocusFromTouch();
                    listview.requestFocus();
                    listview.setSelection(0);
                }
            }, 500);
        } else if (listview != null) {
            listview.setSelection(0);
        }
        myHandler.sendEmptyMessageDelayed(TOFINISH_TIME, DELAY_TOFINISH_TIME);
    }

    private void init() {
        if (!isPVRAvailable()) {
            return;
        }
        gallery = (LinearLayout) findViewById(R.id.pvr_gallery);
        thumbnail = new PVRThumbnail(this, pvr) {

            @Override
            void onItemClicked(int position) {
                pvr.jumpToThumbnail(position);
            }
        };
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        gallery.addView(thumbnail);
        thumbnail.setLayoutParams(lp);
        pvrAdapter = new PVRListViewAdapter(this, pvrList);
        listview.setAdapter(pvrAdapter);
        listview.setDividerHeight(0);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                try {
//                    TvManager.getInstance().setVideoMute(true, EnumScreenMuteType.E_BLACK, 0,
//                            TvCommonManager.getInstance().getCurrentInputSource());
                    if (pvr.isPlaybacking()) {
                        pvr.stopPlayback();
                        pvr.stopPlaybackLoop();
                    }
                    if (current_pvr_position == pos) {
                        pvr.startPvrPlayback(getFileNameByIndex(pos));
                        pvr.jumpPlaybackTime(0);
                    }
                    scaleToFullScreen();
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                IsItemClicked = true;
                Intent intent = new Intent();
                intent.setClass(PVRFullPageBrowserActivity.this, PVRActivity.class);
                intent.putExtra("FullPageBrowserCall", true);
                intent.putExtra("channel", pvrList.get(pos).pvr_text_view_lcn + " "
                        + pvrList.get(pos).pvr_text_view_channel);
                intent.putExtra("channelEvent", pvrList.get(pos).pvr_text_view_program_service);
                startActivity(intent);
                finish();
            }
        });
        listview.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                current_pvr_position = pos;
                myHandler.removeMessages(DELAYPLAY);
                myHandler.sendEmptyMessageDelayed(DELAYPLAY, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // construct recorder list
        try {
            constructRecorderList();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        // show the total time at first time.
        totalTime = (TextView) findViewById(R.id.total_time);

        try {
            String fileName = getFileNameByIndex(0);
            int total = pvr.getRecordedFileDurationTime(fileName);
            totalTime.setText(getTimeString(total));

        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        pvr.setMetadataSortAscending(true);
        new PlayBackProgress().start();
    }

    private boolean isPVRAvailable() {
        Log.d(TAG, "pvr.getPvrFileNumber() = " + pvr.getPvrFileNumber());
        if (pvr.getPvrFileNumber() > 0) {
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerUSBDetector();
    }

    @Override
    protected void onPause() {
        super.onStop();
        usbSelecter.dismiss();
        unregisterReceiver(usbReceiver);
        usbReceiver = null;
        int delayTime = 2000;
        if (isQuickFinish) {
            delayTime = 0;
        }
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!IsItemClicked && pvr.isPlaybacking()) {
                        pvr.stopPlayback();
                        pvr.stopPlaybackLoop();
                    } else if (IsItemClicked) {
                        TvManager.getInstance().setVideoMute(false, EnumScreenMuteType.E_BLACK, 0,
                                TvCommonManager.getInstance().getCurrentInputSource());
                    }
                    if (IsScalerInitialized) {
                        scaleToFullScreen();
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
        boolean bRet = false;
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_PROG_RED: {
                    // to delete record file.
                    try {
                        String fileName;
                        fileName = getFileNameByIndex(current_pvr_position);
                        Log.i(TAG, "get fileName;" + fileName);
                        if (null == fileName) {
                            return false;
                        }
                        pvr.stopPlayback();
                        pvrList.clear();
                        pvr.deletefile(0, fileName);
                        try {
                            constructRecorderList();
                        } catch (TvCommonException e) {
                            e.printStackTrace();
                        }
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }
                    bRet = true;
                    try {
                        String fileName = getFileNameByIndex(current_pvr_position);
                        constructThumbnailList(fileName);
                        Log.i(TAG, "jump filename:" + fileName);
                        pvr.jumpToThumbnail(current_pvr_position);
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    break;
            }
        }
        if (bRet == false) {
            return super.dispatchKeyEvent(event);
        }
        return bRet;
    }

    /**
     * register the broastCast.
     */
    private void registerUSBDetector() {
        usbReceiver = new UsbReceiver();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        iFilter.addDataScheme("file");
        registerReceiver(usbReceiver, iFilter);
    }

    /**
     * creat play view.
     */
    private void createPVRPlaybackView() {
//        PVRPlaybackView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Callback callback = new Callback() {

            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    TvManager.getInstance().getPlayerManager().setDisplay(holder);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
            }
        };
        PVRPlaybackView.getHolder().addCallback(callback);
    }

    /**
     * scale small tv window.
     * 
     * @return
     * @throws TvCommonException
     */
    private boolean initTVScaler() throws TvCommonException {
        if (IsScalerInitialized) {
            return true;
        }
        VideoWindowType videoWindowType = new VideoWindowType();
        int[] location = new int[2];
        PVRPlaybackView.getLocationOnScreen(location);
        videoWindowType.x = location[0];
        videoWindowType.y = location[1];
        videoWindowType.height = PVRPlaybackView.getHeight();
        videoWindowType.width = PVRPlaybackView.getWidth();
        Log.e(TAG, "the [x,y][w,h]=" + videoWindowType.x + "," + videoWindowType.y + "][" + videoWindowType.width + ","
                + videoWindowType.height + "]");
        if (videoWindowType.width == 0 || videoWindowType.height == 0) {
            return false;
        }
        pvr.setPlaybackWindow(videoWindowType, getResources().getInteger(R.integer.init_tv_scaler_width),
                getResources().getInteger(R.integer.init_tv_scaler_height));
        IsScalerInitialized = true;
        return true;
    }

    /**
     * to play the record files.
     * 
     * @param pos
     * @throws TvCommonException
     */
    private void playRecordedFile(int pos) throws TvCommonException {
        String fileName = getFileNameByIndex(pos);
        if (fileName == null || !initTVScaler())
            return;
        Log.d(TAG, "current playback fileName is " + fileName);
        if (pvr.getCurPlaybackingFileName().equals(fileName))
            return;
        if (pvr.isPlaybacking()) {
            pvr.stopPlayback();
            pvr.stopPlaybackLoop();
        }
        int playbackStatus = pvr.startPvrPlayback(fileName);
        int total = pvr.getRecordedFileDurationTime(fileName);
        Log.d(TAG, "current playback file totalTime is " + total);
        PVRPlaybackProgress.setMax(total);
        totalTime.setText(getTimeString(total));
        if (TvPvrManager.PVR_STATUS_SUCCESS != playbackStatus) {
            Log.e(TAG, "playRecordedFile Error");
        }
    }

    private String getTimeString(int seconds) {
        String hour = "00";
        String minute = "00";
        String second = "00";
        if (seconds % 60 < 10) {
            second = "0" + seconds % 60;
        } else {
            second = "" + seconds % 60;
        }

        int offset = seconds / 60;
        if (offset % 60 < 10) {
            minute = "0" + offset % 60;
        } else {
            minute = "" + offset % 60;
        }

        offset = seconds / 3600;
        if (offset < 10) {
            hour = "0" + offset;
        } else {
            hour = "" + offset;
        }
        return hour + ":" + minute + ":" + second;
    }

    private void scaleToFullScreen() throws TvCommonException {
        VideoWindowType videoWindowType = new VideoWindowType();
        videoWindowType.height = 0;
        videoWindowType.width = 0;
        videoWindowType.x = 0xFFFF;
        videoWindowType.y = 0xFFFF;
        pvr.setPlaybackWindow(videoWindowType, 0, 0);
    }

    /**
     * get the record file list.
     * 
     * @throws TvCommonException
     */
    public void constructRecorderList() throws TvCommonException {
        pvrList.clear();
        int pvrFileNumber = pvr.getPvrFileNumber();
        for (int i = 0; i < pvrFileNumber; i++) {
            listViewHolder vh = new listViewHolder();
            int nSortKey = pvr.getMetadataSortKey();
            String pvrFileLcn = null;
            String pvrFileServiceName = null;
            String pvrFileEventName = null;
            PvrFileInfo fileInfo = new PvrFileInfo();
            fileInfo = pvr.getPvrFileInfo(i, nSortKey);
            pvrFileLcn = "CH " + pvr.getFileLcn(i);
            pvrFileServiceName = pvr.getFileServiceName(fileInfo.filename);
            pvrFileEventName = pvr.getFileEventName(fileInfo.filename);

            String strFileName = pvr.getCurRecordingFileName();
            if (strFileName.equals(fileInfo.filename)) {
                current_recording = i;
            }

            vh.setPvr_text_view_lcn(pvrFileLcn);
            vh.setPvr_text_view_channel(pvrFileServiceName);
            vh.setPvr_text_view_program_service(pvrFileEventName);
            pvrList.add(vh);
        }
        pvrAdapter.notifyDataSetChanged();
        listview.invalidate();
    }

    /**
     * get the file name by index.
     * 
     * @param index
     * @return
     * @throws TvCommonException
     */
    public String getFileNameByIndex(int index) throws TvCommonException {
        if (pvrList.isEmpty()) {
            return null;
        }
        PvrFileInfo fileInfo = new PvrFileInfo();
        fileInfo = pvr.getPvrFileInfo(index, pvr.getMetadataSortKey());
        return fileInfo.filename;
    }

    /**
     * get the selected(focus item) file name.
     * 
     * @return
     * @throws TvCommonException
     */
    public String getSelectedFileName() throws TvCommonException {
        if (pvrList.isEmpty()) {
            return null;
        }
        PvrFileInfo fileInfo = new PvrFileInfo();
        fileInfo = pvr.getPvrFileInfo(listview.getSelectedItemPosition(), pvr.getMetadataSortKey());
        return fileInfo.filename;
    }

    public void constructThumbnailList(String fileName) throws TvCommonException {
        if (fileName != null) {
            pvr.assignThumbnailFileInfoHandler(fileName);
        }
        thumbnail.updateThumbnail();
    }

    private class PlayBackProgress extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                while (!isFinishing()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            int currentTime = pvr.getCurPlaybackTimeInSecond();
                            if (isRecordingItem) {
                                int total = (pvr.isRecording()) ? pvr.getCurRecordTimeInSecond() : pvr
                                        .getCurPlaybackTimeInSecond();
                                totalTime.setText(getTimeString(total));
                                PVRPlaybackProgress.setMax(total);
                            }
                            PVRPlaybackProgress.setProgress(currentTime);
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
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                // TODO update list.
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

    /**
     * file item information.
     */
    private class listViewHolder {

        private String pvr_text_view_lcn = null;

        private String pvr_text_view_channel = null;

        private String pvr_text_view_program_service = null;

        public String getPvr_text_view_lcn() {
            return pvr_text_view_lcn;
        }

        public void setPvr_text_view_lcn(String pvrTextViewLcn) {
            pvr_text_view_lcn = pvrTextViewLcn;
        }

        public String getPvr_text_view_channel() {
            return pvr_text_view_channel;
        }

        public void setPvr_text_view_channel(String pvrTextViewChannel) {
            pvr_text_view_channel = pvrTextViewChannel;
        }

        public String getPvr_text_view_program_service() {
            return pvr_text_view_program_service;
        }

        public void setPvr_text_view_program_service(String pvrTextViewProgramService) {
            pvr_text_view_program_service = pvrTextViewProgramService;
        }
    }

    /**
     * file list adapter for pvr browser.
     */
    private class PVRListViewAdapter extends BaseAdapter {

        ArrayList<listViewHolder> mData = null;

        private Context mContext;

        public PVRListViewAdapter(Context context, ArrayList<listViewHolder> data) {
            mContext = context;
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.eos_pvr_listview_item, null);
            ImageView tmpImage = (ImageView) convertView.findViewById(R.id.player_recording_file);
            // is recording.
            if (current_recording == position) {
                tmpImage.setVisibility(View.VISIBLE);
                isRecordingItem = true;
            } else {
                tmpImage.setVisibility(View.INVISIBLE);
                isRecordingItem = false;
            }
            TextView tmpText = (TextView) convertView.findViewById(R.id.pvr_listview_item_index);
            tmpText.setText("" + (position + 1));
            tmpText = (TextView) convertView.findViewById(R.id.pvr_listview_item_lcn);
            tmpText.setText(mData.get(position).getPvr_text_view_lcn());
            tmpText = (TextView) convertView.findViewById(R.id.pvr_listview_item_channel);
            tmpText.setText(mData.get(position).getPvr_text_view_channel());
            tmpText = (TextView) convertView.findViewById(R.id.pvr_listview_item_program);
            tmpText.setText(mData.get(position).getPvr_text_view_program_service());
            return convertView;
        }
    }
}
