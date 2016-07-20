
package com.android.settings.deviceinfo;

import java.io.File;
import java.io.IOException;

import com.android.settings.R;
import com.android.settings.util.Utils;
import com.android.settings.widget.TitleWidget;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;
import scifly.device.Device;
import java.io.FileReader;
import java.io.BufferedReader;

public class MoreInfoActivity extends Activity {
    private static final String TAG = "About";

    private static final String FILENAME_CPU_INFO = "/proc/cpuinfo";

    private static final String FILENAME_MEMERY_INFO = "/proc/meminfo";

    private static final int EVENT_UPDATE_STATS = 500;

    private Handler mHandler;

    private TitleWidget mTitleWidget;

    private TextView tv_about_more_cpu_key;

    private TextView tv_about_more_cpu_value;

    private TextView tv_about_more_ram_key;

    private TextView tv_about_more_ram_value;

    private TextView tv_about_more_flash_key;

    private TextView tv_about_more_flash_value;

    private TextView tv_about_more_mac_key;

    private TextView tv_about_more_mac_value;

    private TextView tv_about_more_up_time_key;

    private TextView tv_about_more_up_time_value;

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_UPDATE_STATS:
                    updateTimes();
                    sendEmptyMessageDelayed(EVENT_UPDATE_STATS, 1000);
                    break;
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        findViews();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
        mHandler.sendEmptyMessage(EVENT_UPDATE_STATS);
    }

    public void onPause() {
        super.onPause();
        mHandler.removeMessages(EVENT_UPDATE_STATS);
    }

    public void findViews() {
        mTitleWidget = (TitleWidget) findViewById(R.id.activity_more_info_title);
        tv_about_more_cpu_key = (TextView) findViewById(R.id.tv_about_more_cpu_key);
        tv_about_more_cpu_value = (TextView) findViewById(R.id.tv_about_more_cpu_value);
        tv_about_more_ram_key = (TextView) findViewById(R.id.tv_about_more_ram_key);
        tv_about_more_ram_value = (TextView) findViewById(R.id.tv_about_more_ram_value);
        tv_about_more_flash_key = (TextView) findViewById(R.id.tv_about_more_flash_key);
        tv_about_more_flash_value = (TextView) findViewById(R.id.tv_about_more_flash_value);
        tv_about_more_mac_key = (TextView) findViewById(R.id.tv_about_more_mac_key);
        tv_about_more_mac_value = (TextView) findViewById(R.id.tv_about_more_mac_value);
        tv_about_more_up_time_key = (TextView) findViewById(R.id.tv_about_more_up_time_key);
        tv_about_more_up_time_value = (TextView) findViewById(R.id.tv_about_more_up_time_value);
    }

    public void initViews() {
        mTitleWidget = (TitleWidget) findViewById(R.id.activity_more_info_title);
        mTitleWidget.setMainTitleText(getString(R.string.action_settings));
        mTitleWidget.setFirstSubTitleText(getString(R.string.about), false);
        mTitleWidget.setSecondSubTitleText(getString(R.string.about_more_info));
        tv_about_more_cpu_key.setText(getResources().getString(R.string.about_more_cpu));
        tv_about_more_cpu_key.setTextColor(getResources().getColor(R.color.green));
        tv_about_more_cpu_value.setText(getCpuName());
        tv_about_more_cpu_value.setTextColor(getResources().getColor(R.color.green));
        tv_about_more_ram_key.setText(getResources().getString(R.string.about_more_ram));
        tv_about_more_ram_value.setText(getTotalMemory());
        tv_about_more_flash_key.setText(getResources().getString(R.string.about_more_flash));
        tv_about_more_flash_key.setTextColor(getResources().getColor(R.color.green));
        tv_about_more_flash_value.setText(getFlashSize());
        tv_about_more_flash_value.setTextColor(getResources().getColor(R.color.green));
        tv_about_more_mac_key.setText(getResources().getString(R.string.about_more_mac));
        tv_about_more_mac_value.setText(getMACAddress());

        tv_about_more_up_time_key.setText(getResources().getString(R.string.about_more_up_time));
        tv_about_more_up_time_key.setTextColor(getResources().getColor(R.color.green));
        tv_about_more_up_time_value.setTextColor(getResources().getColor(R.color.green));
        mHandler = new MyHandler();
    }

    private String getCpuName() {
        try {
            String text = Utils.readLine(FILENAME_CPU_INFO);
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (IOException e) {
            Log.e(TAG, "IO Exception when getting cpu name", e);
            return "Unavailable";
        }
    }

    public long getRomTotalSize() {
        File path = Environment.getDataDirectory();
//        long fileTotalLength=path.getTotalSpace();
//        return fileTotalLength;
        long fileFreeLength=path.getFreeSpace();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }

    public long getExternalSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
//        long fileTotalLength=path.getTotalSpace();
//        return fileTotalLength;
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }

    public String getFlashSize() {
        return Utils.formatStorageSize(this, getRomTotalSize() + getExternalSDTotalSize());
    }

    private String getTotalMemory() {
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(FILENAME_MEMERY_INFO);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();

            arrayOfString = str2.split(" ");
            for (String num : arrayOfString) {
                Log.i(str2, num + "/t");
            }

            String l8= arrayOfString[8];
            String l9= arrayOfString[9];
            if(l9.equals("kB")){
            	initial_memory = Integer.valueOf(arrayOfString[8]).intValue() * 1024;
            }else{
            	initial_memory = Integer.valueOf(arrayOfString[9]).intValue() * 1024;
            }
           
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return Utils.formatStorageSize(this, initial_memory);
    }

    public String getMACAddress() {
        return Device.getHardwareAddress(this);
    }

    private void updateTimes() {
        long at = SystemClock.uptimeMillis() / 1000;
        long ut = SystemClock.elapsedRealtime() / 1000;

        if (ut == 0) {
            ut = 1;
        }
        tv_about_more_up_time_value.setText(convert(ut));
    }

    private String convert(long t) {
        int s = (int) (t % 60);
        int m = (int) ((t / 60) % 60);
        int h = (int) ((t / 3600));

        return h + ":" + pad(m) + ":" + pad(s);
    }

    private String pad(int n) {
        if (n >= 10) {
            return String.valueOf(n);
        } else {
            return "0" + String.valueOf(n);
        }
    }
}
