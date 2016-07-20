//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2014 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>


package com.eostek.tvmenu.advance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.eostek.tvmenu.R;
import com.eostek.tvmenu.utils.Constants;

import com.mstar.android.storage.MStorageManager;

public abstract class USBDiskSelecter {

    private static final String TAG = "USBDiskSelecter";

    final private int maxDiskperPage = 4;

    private UsbReceiver usbReceiver = new UsbReceiver();

    private int usbDriverCount = 0;

    private ArrayList<String> usbDriverLabel = new ArrayList<String>();

    private ArrayList<String> usbDriverPath = new ArrayList<String>();

    private Dialog currentDialog = null;

    private MStorageManager storageManager;

    private Context context = null;

    private usbListener usblistener = null;

    protected boolean noDismiss = false;

    public USBDiskSelecter(Context mContext) {
        context = mContext;
        storageManager = MStorageManager.getInstance(mContext);
        updateUSBDriverInfo();
        registerUSBDetector();

    }

    public void start() {
        if (usbDriverCount == 1) {
            onItemChosen(0, usbDriverLabel.get(0), usbDriverPath.get(0));
            return;
        }
        Dialog al = new Dialog(context, R.style.UsbSelecterDialog);
        al.setContentView(getUSBSelecterView(usbDriverCount));
        al.show();
        currentDialog = al;
    }

    public void dismiss() {
        context.unregisterReceiver(usbReceiver);
        currentDialog = null;
    }

    public int getDriverCount() {
        return usbDriverCount;
    }

    private void registerUSBDetector() {
        IntentFilter iFilter;
        iFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addDataScheme("file");
        context.registerReceiver(usbReceiver, iFilter);
        iFilter = new IntentFilter(Intent.ACTION_MEDIA_EJECT);
        iFilter.addDataScheme("file");
        context.registerReceiver(usbReceiver, iFilter);
    }

    private void updateUSBDriverInfo() {

        String[] volumes = storageManager.getVolumePaths();
        usbDriverCount = 0;
        usbDriverLabel.clear();
        usbDriverPath.clear();
        if (volumes == null) {
            return;
        }

        File file = new File("proc/mounts");
        if (!file.exists() || file.isDirectory()) {
            file = null;
        }

        for (int i = 0; i < volumes.length; ++i) {
            String state = storageManager.getVolumeState(volumes[i]);
            if (state == null || !state.equals(Environment.MEDIA_MOUNTED)) {
                continue;
            }
            String path = volumes[i];
            String[] pathPartition = path.split("/");
            String label = pathPartition[pathPartition.length - 1]; // the last
                                                                    // part

            String volumeLabel = storageManager.getVolumeLabel(path);
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
            /* add by owen.qin begin */
            /* label=linux2pc(label); */
            /* add by owen.qin end */
            label += ": " + getFileSystem(file, path) + "\n" + volumeLabel;
            usbDriverLabel.add(usbDriverCount, label);
            usbDriverPath.add(usbDriverCount, path);
            usbDriverCount++;
        }
        return;
    }

    /* add by owen.qin begin */
    private String linux2pc(String name) {

        if (name == null || name.length() < 4) {

            return "unknown";
        }
        char tmp = (char) (name.charAt(2) + 2);

        return Character.toUpperCase(tmp) + "";

    }

    /* add by owen.qin end */

    private View getUSBSelecterView(int usbCount) {
        LayoutInflater layout = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layout.inflate(R.layout.usb_driver_selecter, null);
        GridView usbListView = (GridView) view.findViewById(R.id.usb_driver_selecter);
        usbListView.setNumColumns(usbCount);
        int totalWidth = dip2px(520); // 520dip is the width of usbListView
        int itemGap = dip2px(5);// 5dip is the gap between two disk items
        if (usbCount <= maxDiskperPage) {
            LayoutParams params = new LayoutParams(totalWidth, LayoutParams.WRAP_CONTENT);
            usbListView.setLayoutParams(params);
            usbListView.setColumnWidth((totalWidth - usbCount * itemGap) / usbCount);
        } else {
            LayoutParams gridparams = new LayoutParams((dip2px(120 + 5)) * usbCount,
                    LayoutParams.WRAP_CONTENT);
            usbListView.setLayoutParams(gridparams);
            usbListView.setColumnWidth(dip2px(120));
        }
        usbListView.setAdapter(new listAdapter(usbCount));
        usbListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                onItemChosen(arg2, usbDriverLabel.get(arg2), usbDriverPath.get(arg2));
                if (!noDismiss) {
                    currentDialog.dismiss();
                    currentDialog = null;
                } else
                    noDismiss = false;
            }
        });
        usbListView.setOnItemSelectedListener(new usbDiskSelectedListener(
                (HorizontalScrollView) view.findViewById(R.id.usb_driver_scroller)));
        Button cancelButton = (Button) view.findViewById(R.id.usb_driver_selecter_cancel);
        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                currentDialog.dismiss();
                currentDialog = null;
            }
        });
        cancelButton.requestFocus();
        return view;
    }

    private static class UpdateHandler extends Handler {

        ProgressBar pb;

        ProgressBar tip;

        public UpdateHandler(ProgressBar pb, ProgressBar tip) {
            this.pb = pb;
            this.tip = tip;
        }

        @Override
        public void handleMessage(Message msg) {
            final int percentage = msg.arg1;
            pb.setProgress(percentage);
            tip.setVisibility(View.INVISIBLE);
        }

    }

    private class listAdapter extends BaseAdapter {

        private int itemCount = 0;

        public listAdapter(int count) {
            itemCount = count;
        }

        @Override
        public int getCount() {
            return itemCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            if (view == null) {
                LayoutInflater layout = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layout.inflate(R.layout.usb_driver_item, parent, false);
            }
            TextView itemName = (TextView) view.findViewById(R.id.usbItemName);
            itemName.setText(usbDriverLabel.get(position));

            ProgressBar diskInfo = (ProgressBar) view.findViewById(R.id.usbItemSpace);
            ProgressBar tip = (ProgressBar) view.findViewById(R.id.tip);

            diskInfo.setMax(100);
            tip.setVisibility(View.VISIBLE);

            final UpdateHandler handler = new UpdateHandler(diskInfo, tip);
            new Thread() {
                @Override
                public void run() {
                    StatFs sf = new StatFs(usbDriverPath.get(position));
                    Message msg = Message.obtain();
                    msg.arg1 = (int) (100 - ((sf.getFreeBlocks()*100) / sf.getBlockCount()));
                    handler.sendMessage(msg);
                }
            }.start();
            return view;
        }

    }

    private class usbDiskSelectedListener implements OnItemSelectedListener {

        private HorizontalScrollView scroller = null;

        private GridView itemParent = null;

        private int pageLength = 0;

        private final int Forward = 1;

        private final int Backward = 2;

        public usbDiskSelectedListener(HorizontalScrollView view) {
            scroller = view;
            itemParent = (GridView) scroller.findViewById(R.id.usb_driver_selecter);
            pageLength = dip2px(500);
            Log.d(TAG, "============>>>>>>>>>>> pageLength = " + pageLength);
        }

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            switch (getScrollType(arg2)) {
                case Backward:
                    scroller.scrollTo(itemParent.getChildAt(arg2).getLeft() - dip2px(20), 0);
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
            if (itemPositionLeft > scrollerPosition
                    && itemPositionRight < pageLength + scrollerPosition) {
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
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
                    || action.equals(Intent.ACTION_MEDIA_EJECT)) {
                updateUSBDriverInfo();
                if (currentDialog != null) {
                    if (usbDriverCount < 1) {
                        currentDialog.dismiss();
                        currentDialog = null;
                        return;
                    }
                    currentDialog.setContentView(getUSBSelecterView(usbDriverCount));
                }
            }
            if (usblistener != null) {
                Uri uri = intent.getData();
                String path = uri.getPath();
                if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                    usblistener.onUSBMounted(path);
                } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                    usblistener.onUSBUnmounted(path);
                } else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                    usblistener.onUSBEject(path);
                }
            }
        }
    }

    private int dip2px(float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private String getFileSystem(File file, String path) {
        if (file == null) {
            return "";
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (line != null) {
                String[] info = line.split(" ");
                if (info[1].equals(path)) {
                    if (info[2].equals("ntfs3g"))
                        return "NTFS";
                    if (info[2].equals("vfat"))
                        return "FAT";
                    else
                        return info[2];
                }
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public void setUSBListener(usbListener listener) {
        this.usblistener = listener;
    }

    public abstract void onItemChosen(int position, String diskLabel, String diskPath);

    public interface usbListener {

        public void onUSBUnmounted(String diskPath);

        public void onUSBMounted(String diskPath);

        public void onUSBEject(String diskPath);
    }

    /* add by owen.qin begin */
    private boolean getChooseDiskSettings() {

        SharedPreferences sp = context.getSharedPreferences(Constants.SAVE_SETTING_SELECT,
                Context.MODE_PRIVATE);
        return sp.getBoolean("IS_ALREADY_CHOOSE_DISK", false);

    }

    private String getChooseDiskLable() {

        SharedPreferences sp = context.getSharedPreferences(Constants.SAVE_SETTING_SELECT,
                Context.MODE_PRIVATE);
        return sp.getString("DISK_LABEL", "unknown");
    }

    private String getChooseDiskPath() {

        SharedPreferences sp = context.getSharedPreferences(Constants.SAVE_SETTING_SELECT,
                Context.MODE_PRIVATE);
        return sp.getString("DISK_PATH", "unknown");
    }

    private boolean isFileExisted(String path) {
        if (path == null || "".equals(path)) {
            return false;
        } else {
            File file = new File(path);
            if (file.exists()) {
                return true;
            } else {
                return false;
            }
        }

    }

    private static class DirectoryFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String filename) {

            System.out.println("dir:" + dir + " name:" + filename);
            if (new File(dir, filename).isDirectory()) {
                return true;
            } else {
                return false;
            }
        }

    }

    private String getFirstUseableDiskAtParentDir(String parent) {
        File file = new File(parent);
        if (file.isDirectory()) {
            FilenameFilter filter = new DirectoryFilter();
            File[] list = file.listFiles(filter);
            for (File tmp : list) {
                if (isFileExisted(tmp.getAbsolutePath() + "/_MSTPVR/")
                        || com.eostek.tvmenu.advance.USBBroadcastReceiver
                                .isDiskExisted(new String(tmp.getAbsolutePath()))) {
                    return tmp.getAbsolutePath();
                }
            }
            return null;
        } else {
            return null;
        }
    }

    public static final String NO_DISK = "NO_DISK";

    public static final String CHOOSE_DISK = "CHOOSE_DISK";

    public String getBestDiskPath() {
        if (getChooseDiskSettings()) {
            String path = getChooseDiskPath();

            if (isFileExisted(path + "/_MSTPVR")
                    || com.eostek.tvmenu.advance.USBBroadcastReceiver.isDiskExisted(path)) {
                return path;
            } else {
                String parent = "/mnt/usb/";
                String firstDisk = getFirstUseableDiskAtParentDir(parent);
                if (firstDisk == null) {
                    return NO_DISK;
                } else {
                    return firstDisk;
                }
            }
        } else {
            return CHOOSE_DISK;
        }

    }

    /**
     * @param path like /mnt/usb/sda1
     */
    public String getUsbLabelByPath(String diskPath) {

        MStorageManager storageManager = MStorageManager.getInstance(context);
        String[] volumes = storageManager.getVolumePaths();
        int usbDriverCount = 0;
        ArrayList<String> usbDriverLabel = new ArrayList<String>();
        ArrayList<String> usbDriverPath = new ArrayList<String>();
        usbDriverLabel.clear();
        usbDriverPath.clear();
        if (volumes == null) {
            return null;
        }

        File file = new File("proc/mounts");
        if (!file.exists() || file.isDirectory()) {
            file = null;
        }

        for (int i = 0; i < volumes.length; ++i) {
            String state = storageManager.getVolumeState(volumes[i]);
            if (state == null || !state.equals(Environment.MEDIA_MOUNTED)) {
                continue;
            }
            String path = volumes[i];
            String[] pathPartition = path.split("/");
            String label = pathPartition[pathPartition.length - 1]; // the last
                                                                    // part

            String volumeLabel = storageManager.getVolumeLabel(path);
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
            label += ": " + getFileSystem(file, path) + "\n" + volumeLabel;
            usbDriverLabel.add(usbDriverCount, label);
            usbDriverPath.add(usbDriverCount, path);
            usbDriverCount++;
        }

        if (diskPath.startsWith("/")) {
            diskPath = diskPath.substring(1);
        }
        if (diskPath.endsWith("/")) {
            diskPath = diskPath.substring(0, diskPath.length() - 1);
        }
        for (int i = 0; i < usbDriverPath.size(); i++) {
            if (usbDriverPath.get(i).contains(diskPath)) {
                return usbDriverLabel.get(i);
            }
        }
        return null;
    }

    /* add by owen.qin end */

}
