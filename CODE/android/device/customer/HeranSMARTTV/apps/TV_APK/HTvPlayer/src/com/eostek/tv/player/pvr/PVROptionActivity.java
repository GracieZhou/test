
package com.eostek.tv.player.pvr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.tv.player.R;
import com.mstar.android.storage.MStorageManager;
import com.mstar.android.tv.TvPvrManager;

public class PVROptionActivity extends Activity {

    private final String TAG = "PVROptionActivity";

    private Context context = null;

    private TextView selectDisk = null;

    private TextView timeShiftSize = null;

    private LinearLayout diskFormat = null;

    private TextView diskFormatStatus = null;

    private LinearLayout speedCheck = null;

    private TextView speedCheckResult = null;

    private USBDiskSelecter usbSelecter = null;

    private String selectedDiskPath = null;

    private clickListener listener = null;

    private boolean waitToFormat = false;

    private boolean waitToSpeedTest = false;

    private MStorageManager storageManager;

    private UsbReceiver usbReceiver = new UsbReceiver();

    private TvPvrManager pvr;

    private void saveLastTimeShiftSize(int value) {
        SharedPreferences sp = getSharedPreferences("save_setting_select", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("LAST_SHIFT_SIZE", value);
        editor.commit();
    }

    private int getLastTimeShiftSize() {
        SharedPreferences sp = getSharedPreferences("save_setting_select", MODE_PRIVATE);
        return sp.getInt("LAST_SHIFT_SIZE", 0);
    }

    private class clickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.pvr_file_system_select_disk: {
                    waitToFormat = false;
                    int usbDriverCount = usbSelecter.getDriverCount();
                    if (usbDriverCount > 0) {
                        usbSelecter.start();
                    } else {
                        Toast.makeText(context, R.string.str_pvr_insert_usb, Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                }
                    break;
                case R.id.pvr_file_system_time_shift_size: {
                    /* add by owen.qin begin */
                    AlertDialog.Builder builder = new AlertDialog.Builder(PVROptionActivity.this);
                    final String[] items = {
                            "512M", "1G", "2G", "4G"
                    };
                    builder.setSingleChoiceItems(items, getLastTimeShiftSize(),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 在设置成功之后 保存当前的值，以便下次进入的时候，选中当前的选项。
                                    switch (which) {

                                        case 0:
                                            pvr.setTimeShiftFileSize(512 * 1024);
                                            saveLastTimeShiftSize(0);
                                            break;
                                        case 1:
                                            pvr.setTimeShiftFileSize(1 * 1024 * 1024);
                                            saveLastTimeShiftSize(1);
                                            break;
                                        case 2:
                                            pvr.setTimeShiftFileSize(2 * 1024 * 1024);
                                            saveLastTimeShiftSize(2);
                                            break;
                                        case 3:
                                            pvr.setTimeShiftFileSize(4 * 1024 * 1024);
                                            saveLastTimeShiftSize(3);
                                            break;
                                    }
                                }
                            });
                    builder.create().show();
                    /* add by owen.qin end */
                }
                    break;
                case R.id.pvr_file_system_format_layout: {
                    if (selectedDiskPath == null) {
                        int usbDriverCount = usbSelecter.getDriverCount();
                        if (usbDriverCount > 0) {
                            usbSelecter.start();
                            waitToFormat = true;
                        } else {
                            Toast.makeText(context, R.string.str_pvr_insert_usb, Toast.LENGTH_LONG)
                                    .show();
                            waitToFormat = false;
                            return;
                        }
                    } else {
                        formatConfirm();
                    }
                }
                    break;
                case R.id.pvr_file_system_speed_layout: {
                    if (selectedDiskPath == null) {
                        int usbDriverCount = usbSelecter.getDriverCount();
                        if (usbDriverCount > 0) {
                            usbSelecter.start();
                            waitToSpeedTest = true;
                        } else {
                            Toast.makeText(context, R.string.str_pvr_insert_usb, Toast.LENGTH_LONG)
                                    .show();
                            return;
                        }
                    } else {
                        startSpeedTest();
                    }
                }
                    break;
                case R.id.pvr_file_system_always_layout: {
                }
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        pvr = TvPvrManager.getInstance();
        setContentView(R.layout.eos_pvr_option);
        selectDisk = (TextView) findViewById(R.id.pvr_file_system_select_disk);
        timeShiftSize = (TextView) findViewById(R.id.pvr_file_system_time_shift_size);
        diskFormat = (LinearLayout) findViewById(R.id.pvr_file_system_format_layout);
        diskFormatStatus = (TextView) findViewById(R.id.pvr_file_system_format_context);
        speedCheck = (LinearLayout) findViewById(R.id.pvr_file_system_speed_layout);
        speedCheckResult = (TextView) findViewById(R.id.pvr_file_system_speed_context);
        diskFormatStatus.setVisibility(View.GONE);
        speedCheckResult.setVisibility(View.GONE);
        storageManager = MStorageManager.getInstance(this);
        usbSelecter = new USBDiskSelecter(this) {

            @Override
            public void onItemChosen(int position, String diskLabel, String diskPath) {
                String fat = "FAT";
                String ntfs = "NTFS";
                selectDisk.setText(diskLabel);
                selectedDiskPath = diskPath;

                if (diskPath.isEmpty()) {
                    Log.e("PVRFBActivity", "=============>>>>> USB Disk Path is NULL !!!");
                    return;
                }
                Log.d("PVRFBActivity", "=============>>>>> USB Disk Path = " + diskPath);
                if (diskLabel.regionMatches(6, fat, 0, 3)) {
                    pvr.setPvrParams(diskPath, (short) 2);
                } else if (diskLabel.regionMatches(6, ntfs, 0, 4) || !diskLabel.contains(fat)) {
                    pvr.setPvrParams(diskPath, (short) 6);
                }
                saveChooseDiskSettings(true, diskPath, diskLabel);

                if (waitToFormat) {
                    // 此处检查 是否是ntf格式的磁盘
                    /*
                     * add by owen.qin begin to check is ntfs before format
                     * choosed disk
                     */
                    if (diskLabel.regionMatches(6, ntfs, 0, 4) || !diskLabel.contains(fat)) {
                        Toast.makeText(PVROptionActivity.this, R.string.str_pvr_unsurpt_flsystem,
                                Toast.LENGTH_LONG).show();
                        waitToFormat = false;
                        return;
                    }
                    /* add by owen.qin end */
                    formatConfirm();
                } else if (waitToSpeedTest) {
                    waitToSpeedTest = false;
                    /*
                     * add by owen.qin begin to check is ntfs before format
                     * choosed disk
                     */
                    if (diskLabel.regionMatches(6, ntfs, 0, 4) || !diskLabel.contains(fat)) {
                        Toast.makeText(PVROptionActivity.this, R.string.str_pvr_unsurpt_flsystem,
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    /* add by owen.qin end */
                    startSpeedTest();
                }
            }
        };
        listener = new clickListener();
        initUIListeners();
        registerDiskDetector();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(usbReceiver);
    }

    /* add by owen.qin begin */
    private void saveChooseDiskSettings(boolean flag, String path, String label) {
        SharedPreferences sp = getSharedPreferences("save_setting_select", MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean("IS_ALREADY_CHOOSE_DISK", flag);
        editor.putString("DISK_PATH", path);
        editor.putString("DISK_LABEL", label);
        editor.commit();
    }

    /* add by owen.qin end */
    private void initUIListeners() {
        selectDisk.setOnClickListener(listener);
        timeShiftSize.setOnClickListener(listener);
        diskFormat.setOnClickListener(listener);
        speedCheck.setOnClickListener(listener);
    }

    private void registerDiskDetector() {
        IntentFilter iFilter;
        iFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addDataScheme("file");
        registerReceiver(usbReceiver, iFilter);

        iFilter = new IntentFilter(Intent.ACTION_MEDIA_UNMOUNTED);
        iFilter.addDataScheme("file");
        registerReceiver(usbReceiver, iFilter);

        iFilter = new IntentFilter(Intent.ACTION_MEDIA_REMOVED);
        iFilter.addDataScheme("file");
        registerReceiver(usbReceiver, iFilter);
    }

    private void formatConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.str_pvr_format_usb).setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        startFormat();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        waitToFormat = false;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).requestFocus();
    }

    private void startFormat() {
        if (storageManager.getVolumeState(selectedDiskPath).equals(Environment.MEDIA_MOUNTED)) {
            waitToFormat = true;
            storageManager.unmountVolume(selectedDiskPath, true, false);
        } else if (storageManager.getVolumeState(selectedDiskPath).equals(
                Environment.MEDIA_UNMOUNTED)) {
            if (storageManager.formatVolume(selectedDiskPath)) {
                Log.d(TAG, "Success to format " + selectedDiskPath);
                if (storageManager.mountVolume(selectedDiskPath)) {
                    Log.d(TAG, "Success to mount " + selectedDiskPath + " again");
                    diskFormatStatus.setText(R.string.str_pvr_file_system_format_context);
                } else {
                    Log.d(TAG, "Fail to mount " + selectedDiskPath + " again");
                }
            } else {
                Log.d(TAG, "Fail to format " + selectedDiskPath);
            }
        } else {
            Log.d(TAG, "Can not format " + selectedDiskPath);
        }
    }

    private void startSpeedTest() {
        final ProgressDialog mpDialog = new ProgressDialog(this);
        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mpDialog.setMessage(getResources().getString(R.string.str_pvr_usb_speed_test_progressing));
        mpDialog.setIndeterminate(false);
        mpDialog.setCancelable(false);
        mpDialog.show();
        final Handler handler = new Handler();
        new Thread(new Runnable() {

            @Override
            public void run() {
                int testSpeed = 0;
                testSpeed = pvr.checkUsbSpeed();
                final String Speed = testSpeed + " KB/S";
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        speedCheckResult.setText(Speed);
                        speedCheckResult.setVisibility(View.VISIBLE);
                        mpDialog.dismiss();
                    }
                });
            }
        }).start();
    }

    private class UsbReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Uri uri = intent.getData();
            String path = uri.getPath();
            if (waitToFormat) {
                if (path.equals(selectedDiskPath)) {
                    waitToFormat = false;
                    diskFormatStatus.setVisibility(View.VISIBLE);
                    if (!action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                        Log.d(TAG, "Fail to unmount " + path + " for format");
                    } else {
                        Log.d(TAG, "Success to unmount " + path + " for format");
                        if (storageManager.formatVolume(path)) {
                            Log.d(TAG, "Success to format " + path);
                            if (storageManager.mountVolume(path)) {
                                Log.d(TAG, "Success to mount " + path + " again");
                                diskFormatStatus
                                        .setText(R.string.str_pvr_file_system_format_context);
                            } else {
                                Log.d(TAG, "Fail to mount " + path + " again");
                            }
                        } else {
                            Log.d(TAG, "Fail to format " + path);
                        }
                    }
                }
                return;
            }

            if (path.equals(selectedDiskPath) && action.equals(Intent.ACTION_MEDIA_REMOVED)) {
                selectDisk.setText(R.string.str_pvr_file_system_select_disk);
                selectedDiskPath = null;
            }
        }
    }
}
