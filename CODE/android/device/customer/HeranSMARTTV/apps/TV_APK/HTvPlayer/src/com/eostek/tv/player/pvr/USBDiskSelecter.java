
package com.eostek.tv.player.pvr;

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

import com.eostek.tv.player.R;
import com.mstar.android.storage.MStorageManager;

public abstract class USBDiskSelecter {

    private static final String TAG = "USBDiskSelecter";

    private static final int maxDiskperPage = 4;

    private UsbReceiver usbReceiver = new UsbReceiver();

    private int usbDriverCount = 0;

    private ArrayList<String> usbDriverLabel = new ArrayList<String>();

    private ArrayList<String> usbDriverPath = new ArrayList<String>();

    private Dialog currentDialog = null;

    private MStorageManager storageManager;

    private Context context = null;

    private usbListener usblistener = null;

    protected boolean noDismiss = false;

    private boolean isUnregister = false;

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
        if (!isUnregister) {
            context.unregisterReceiver(usbReceiver);
            isUnregister = true;
        }
        currentDialog = null;
    }

    public int getDriverCount() {
        return usbDriverCount;
    }

    private void registerUSBDetector() {
        IntentFilter iFilter;
        iFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addDataScheme("file");
        try {
            context.registerReceiver(usbReceiver, iFilter);
        } catch (Exception e) {
        }
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
            // can't use virtual sdcard.The virtual sdcard path is /mnt/sdcard.
            if (path.equals("/mnt/sdcard")) {
                continue;
            }
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
        View view = layout.inflate(R.layout.eos_usb_driver_selecter, null);
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
            int free = msg.arg1;
            int all = msg.arg2;
            pb.setProgress((int) ((1 - (float) free / all) * 100));
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
                view = layout.inflate(R.layout.eos_usb_driver_item, parent, false);
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
                    msg.arg1 = sf.getFreeBlocks();
                    msg.arg2 = sf.getBlockCount();
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
            Log.d(TAG, "pageLength = " + pageLength);
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
        SharedPreferences sp = context.getSharedPreferences("save_setting_select",
                Context.MODE_PRIVATE);
        return sp.getBoolean("IS_ALREADY_CHOOSE_DISK", false);
    }

    private String getChooseDiskLable() {

        SharedPreferences sp = context.getSharedPreferences("save_setting_select",
                Context.MODE_PRIVATE);
        return sp.getString("DISK_LABEL", "unknown");
    }

    private String getChooseDiskPath() {

        SharedPreferences sp = context.getSharedPreferences("save_setting_select",
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

            Log.e(TAG, "dir:" + dir + " name:" + filename);
            if (new File(dir, filename).isDirectory())
                return true;
            else {
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
                // 依次查看哪个目录下面的文件路径是否存在
                // 此处的tmp是 /mnt/usb/sda1
                // 光检查这个目录不对，需要检查/mnt/usb/sda1/_MSTPVR/这个目录才对
                if (isFileExisted(tmp.getAbsolutePath() + "/_MSTPVR/")
                        || com.eostek.tv.player.pvr.UsbReceiver.isDiskExisted(new String(tmp
                                .getAbsolutePath()))) {
                    return tmp.getAbsolutePath();
                }
            }
            // 如果循环检测完毕，都没有找到合适路径，则表明没有插入任何设备。
            return null;
        } else {
            return null;
        }
    }

    public static final String NO_DISK = "NO_DISK";

    public static final String CHOOSE_DISK = "CHOOSE_DISK";

    public String getBestDiskPath() {
        if (getChooseDiskSettings()) {
            // 已经选择了磁盘 从文件中读取存储的磁盘路径 注意 此处的label其实不重要，因为有可能和真是的盘符对不上
            String path = getChooseDiskPath();// eg: /mnt/usb/sda1/ 注意 这个函数
                                              // 如果文件中没有保存 返回的unknown
            // 判断该路径对应的path是否存在
            if (isFileExisted(path + "/_MSTPVR")
                    || com.eostek.tv.player.pvr.UsbReceiver.isDiskExisted(path)) {
                // 该路径对应的文件存在
                return path;
            } else {
                // 该路径对应的路径不存在 从别的目录任意选择一个
                String parent = "/mnt/usb/";
                String firstDisk = getFirstUseableDiskAtParentDir(parent);
                // 没有找到合适的目录
                if (firstDisk == null) {
                    return NO_DISK;
                }
                // 找到合适的目录
                else {
                    return firstDisk;
                }
            }

        } else {
            // 没有设置，则进行选择磁盘 而且此处需要进行控制 如果设备为0 的话 则不需要控制 这里也有可能没有disk 所以还需要判断
            return CHOOSE_DISK;
        }

    }

    /**
     * @param path like /mnt/usb/sda1
     * @return 根据path返回该path对应的label 如果我们保存了label 这个不管用，而应该根据path来获取相应的label。
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
            // can't use virtual sdcard.The virtual sdcard path is /mnt/sdcard.
            if (path.equals("/mnt/sdcard")) {
                continue;
            }
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

        // remove diskPath 开始和结束的/
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
        // 如果便利所有的usb 都没有找到该path对应的label 就返回null
        return null;
    }

    /* add by owen.qin end */

}
