
package com.eostek.tv.pvr;

import java.io.File;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout.LayoutParams;

import com.eostek.tv.R;
import com.eostek.tv.pvr.adapter.USBSelectorAdapter;
import com.eostek.tv.utils.LogUtil;
import com.eostek.tv.utils.UtilsTools;
import com.mstar.android.storage.MStorageManager;

public abstract class USBDiskSelecter {

    private static final int mMaxDiskperPage = 4;

    private Context mContext = null;

    /** the dialog to show usb select view **/
    private Dialog mSelectDialog = null;

    private MStorageManager mStorageManager;

    private UsbReceiver mUsbReceiver;;

    private UsbListener mUsblistener = null;

    /** save mounted drivder label info **/
    private ArrayList<String> mUsbDriverLabelList = new ArrayList<String>();

    /** save mounted drivder path info **/
    private ArrayList<String> mUsbDriverPathList = new ArrayList<String>();

    private int mUsbDriverCount = 0;

    /**  **/
    protected boolean mNoDismiss = false;

    /** 判断UsbReceiver是否被 注销，避免多次注销 **/
    private boolean mIsUnregister = false;

    public USBDiskSelecter(Context context) {
        this.mContext = context;
        mStorageManager = MStorageManager.getInstance(mContext);
        getUSBDriverInfo();
        registerUSBDetector();
    }

    public void start() {
        // if there is only one device,just select the deivce
        if (mUsbDriverCount == 1) {
            onItemChosen(0, mUsbDriverLabelList.get(0), mUsbDriverPathList.get(0));
            return;
        } else {
            mSelectDialog = new Dialog(mContext, R.style.UsbSelecterDialog);
            mSelectDialog.setContentView(getUSBSelecterView(mUsbDriverCount));
            mSelectDialog.show();
        }
    }

    public void dismiss() {
        if (!mIsUnregister) {
            mContext.unregisterReceiver(mUsbReceiver);
            mIsUnregister = true;
        }
        dismissDialog();
    }

    public int getDriverCount() {
        return mUsbDriverCount;
    }

    private void registerUSBDetector() {
        if (mUsbReceiver == null) {
            mUsbReceiver = new UsbReceiver();
        }
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        iFilter.addDataScheme("file");
        mContext.registerReceiver(mUsbReceiver, iFilter);
        mIsUnregister = true;
    }

    /**
     * get all mounted volume devices information
     */
    private void getUSBDriverInfo() {
        // empty the former data
        mUsbDriverCount = 0;
        mUsbDriverLabelList.clear();
        mUsbDriverPathList.clear();

        String[] volumes = mStorageManager.getVolumePaths();
        if (volumes == null) {
            return;
        }

        File file = new File("proc/mounts");
        if (!file.exists() || file.isDirectory()) {
            file = null;
        }

        for (int i = 0; i < volumes.length; ++i) {
            String state = mStorageManager.getVolumeState(volumes[i]);
            // we only select the mounted device
            if (state == null || !state.equals(Environment.MEDIA_MOUNTED)) {
                continue;
            }

            String path = volumes[i];

            // can't use virtual sdcard.The virtual sdcard path is /mnt/sdcard.
            if (path.equals("/mnt/sdcard")) {
                continue;
            }

            /** 这一段逻辑比较奇怪，没有完全弄明白 **/
            String volumeLabel = mStorageManager.getVolumeLabel(path);
            if (volumeLabel != null) {
                // get rid of the long space in the Label word
                String[] tempVolumeLabel = volumeLabel.split(" ");
                volumeLabel = "";
                for (int j = 0; j < tempVolumeLabel.length; j++) {
                    if (j != tempVolumeLabel.length - 1) {
                        volumeLabel += tempVolumeLabel[j] + " ";
                        continue;
                    }
                    volumeLabel += tempVolumeLabel[j];
                }
            }

            String[] pathPartition = path.split("/");
            // the last part is label name
            String label = path.split("/")[pathPartition.length - 1];
            label += ": " + UtilsTools.getFileSystem(file, path) + "\n" + volumeLabel;

            mUsbDriverLabelList.add(mUsbDriverCount, label);
            mUsbDriverPathList.add(mUsbDriverCount, path);
            mUsbDriverCount++;
        }
        return;
    }

    /**
     * add by owen.qin end
     */
    private View getUSBSelecterView(int usbCount) {
        LayoutInflater layout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layout.inflate(R.layout.eos_usb_driver_selecter, null);
        GridView usbListView = (GridView) view.findViewById(R.id.usb_driver_selecter);
        usbListView.setNumColumns(usbCount);
        // 520dip is the width of usbListView
        int totalWidth = UtilsTools.dip2px(mContext, 520);
        // 5dip is the gap between two disk items
        int itemGap = UtilsTools.dip2px(mContext, 5);
        if (usbCount <= mMaxDiskperPage) {
            LayoutParams params = new LayoutParams(totalWidth, LayoutParams.WRAP_CONTENT);
            usbListView.setLayoutParams(params);
            usbListView.setColumnWidth((totalWidth - usbCount * itemGap) / usbCount);
        } else {
            LayoutParams gridparams = new LayoutParams((UtilsTools.dip2px(mContext, 120 + 5)) * usbCount,
                    LayoutParams.WRAP_CONTENT);
            usbListView.setLayoutParams(gridparams);
            usbListView.setColumnWidth(UtilsTools.dip2px(mContext, 120));
        }

        usbListView.setAdapter(new USBSelectorAdapter(mContext, mUsbDriverLabelList));

        usbListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                onItemChosen(arg2, mUsbDriverLabelList.get(arg2), mUsbDriverPathList.get(arg2));
                if (!mNoDismiss) {
                    dismissDialog();
                } else {
                    mNoDismiss = false;
                }

            }
        });

        usbListView.setOnItemSelectedListener(new UsbDiskSelectedListener((HorizontalScrollView) view
                .findViewById(R.id.usb_driver_scroller)));
        Button cancelButton = (Button) view.findViewById(R.id.usb_driver_selecter_cancel);
        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // mSelectDialog.dismiss();
                // mSelectDialog = null;
                dismissDialog();
            }
        });
        cancelButton.requestFocus();
        return view;
    }

    private class UsbDiskSelectedListener implements OnItemSelectedListener {

        private HorizontalScrollView scroller = null;

        private GridView itemParent = null;

        private int pageLength = 0;

        private final int Forward = 1;

        private final int Backward = 2;

        public UsbDiskSelectedListener(HorizontalScrollView view) {
            scroller = view;
            itemParent = (GridView) scroller.findViewById(R.id.usb_driver_selecter);
            pageLength = UtilsTools.dip2px(mContext, 500);
            LogUtil.i("pageLength = " + pageLength);
        }

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            switch (getScrollType(arg2)) {
                case Backward:
                    scroller.scrollTo(itemParent.getChildAt(arg2).getLeft() - UtilsTools.dip2px(mContext, 20), 0);
                    break;
                case Forward:
                    scroller.scrollTo(itemParent.getChildAt(arg2).getRight() - pageLength, 0);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }

        private int getScrollType(int position) {
            int itemPositionLeft = itemParent.getChildAt(position).getLeft();
            int itemPositionRight = itemParent.getChildAt(position).getRight();
            int scrollerPosition = scroller.getScrollX();
            if (itemPositionLeft > scrollerPosition && itemPositionRight < pageLength + scrollerPosition) {
                return 0;
            }
            if (itemPositionLeft < scrollerPosition) {
                return Backward;
            }
            if (itemPositionRight > scrollerPosition + pageLength) {
                return Forward;
            }
            return 0;
        }
    }

    private class UsbReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED) || action.equals(Intent.ACTION_MEDIA_EJECT)) {
                getUSBDriverInfo();
                if (mSelectDialog != null) {
                    if (mUsbDriverCount < 1) {
                        // mSelectDialog.dismiss();
                        // mSelectDialog = null;
                        dismissDialog();
                        return;
                    }
                    mSelectDialog.setContentView(getUSBSelecterView(mUsbDriverCount));
                }
            }

            // 将mount消息通过usblistener传递出去
            if (mUsblistener != null) {
                Uri uri = intent.getData();
                String path = uri.getPath();
                if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                    mUsblistener.onUSBMounted(path);
                } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                    mUsblistener.onUSBUnmounted(path);
                } else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                    mUsblistener.onUSBEject(path);
                }
            }
        }
    }

    

    public void setUSBListener(UsbListener listener) {
        this.mUsblistener = listener;
    }

    public abstract void onItemChosen(int position, String diskLabel, String diskPath);

    /** 将系统USB 挂载相关事件通过此监听器传递出去 **/
    public interface UsbListener {

        public void onUSBUnmounted(String diskPath);

        public void onUSBMounted(String diskPath);

        public void onUSBEject(String diskPath);
    }

    private void dismissDialog() {
        if (mSelectDialog != null) {
            mSelectDialog.dismiss();
            mSelectDialog = null;
        }
    }

}
