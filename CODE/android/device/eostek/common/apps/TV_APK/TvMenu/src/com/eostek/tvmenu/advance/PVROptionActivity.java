package com.eostek.tvmenu.advance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.eostek.tvmenu.utils.Constants;
import com.mstar.android.storage.MStorageManager;
import com.mstar.android.tv.TvPvrManager;

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
import com.eostek.tvmenu.R;

public class PVROptionActivity extends Activity{

  private static final String TAG = "PVROptionActivity";
  private PVROptionActivity context = null;
  private TextView selectDisk = null;
  private TextView timeShiftSize = null;
  private LinearLayout diskFormat = null;
  private TextView diskFormatStatus = null;
  private LinearLayout speedCheck = null;
  private TextView speedCheckResult = null;
  private LinearLayout alwaysTimeShift = null;
  private TextView alwaysTimeShiftType = null;
  private USBDiskSelecter usbSelecter = null;
  private String selectedDiskPath = null;
  private clickListener listener = null;
  private boolean waitToFormat = false;
  private boolean waitToSpeedTest = false;
  private MStorageManager storageManager ;
  private UsbReceiver usbReceiver = new UsbReceiver();
  private TvPvrManager mPvrManager = null;

  private void saveLastTimeShiftSize(int value){
      SharedPreferences sp=getSharedPreferences(Constants.SAVE_SETTING_SELECT, MODE_PRIVATE);
      SharedPreferences.Editor editor=sp.edit();
      editor.putInt("LAST_SHIFT_SIZE", value);
      editor.commit();
  }

  private int getLastTimeShiftSize(){
      SharedPreferences sp=getSharedPreferences(Constants.SAVE_SETTING_SELECT, MODE_PRIVATE);
      return sp.getInt("LAST_SHIFT_SIZE", 0);
  }

  private class clickListener implements OnClickListener {

      public void onClick(View view) {
          switch (view.getId()) {
              case R.id.pvr_file_system_select_disk: {
                  waitToFormat = false;
                  int usbDriverCount = usbSelecter.getDriverCount();
                  if (usbDriverCount > 0) {
                      usbSelecter.start();
                  } else {
                      Toast.makeText(context, R.string.str_pvr_insert_usb, Toast.LENGTH_SHORT).show();
                      return;
                  }
              }
              break;
              case R.id.pvr_file_system_time_shift_size: {
                  /*add by owen.qin begin*/
                  AlertDialog.Builder builder=new AlertDialog.Builder(PVROptionActivity.this);
                  final String [] items={"512M","1G","2G","4G"};
                  builder.setSingleChoiceItems(items, getLastTimeShiftSize(), new DialogInterface.OnClickListener() {

                      @Override
                      public void onClick(DialogInterface dialog, int which) {

                          switch(which){

                              case 0:
                                  mPvrManager.setTimeShiftFileSize(512*1024);
                                  saveLastTimeShiftSize(0);
                                  break;
                              case 1:
                                  mPvrManager.setTimeShiftFileSize(1*1024*1024);
                                  saveLastTimeShiftSize(1);
                                  break;
                              case 2:
                                  mPvrManager.setTimeShiftFileSize(2*1024*1024);
                                  saveLastTimeShiftSize(2);
                                  break;
                              case 3:
                                  mPvrManager.setTimeShiftFileSize(4*1024*1024);
                                  saveLastTimeShiftSize(3);
                                  break;
                          }
                      }
                  });
                  builder.create().show();
                  /*add by owen.qin end*/
              }
              break;
              case R.id.pvr_file_system_format_layout: {
                  if (selectedDiskPath == null) {
                      int usbDriverCount = usbSelecter.getDriverCount();
                      if (usbDriverCount > 0) {
                          usbSelecter.start();
                          waitToFormat = true;
                      } else {
                          Toast.makeText(context, R.string.str_pvr_insert_usb, Toast.LENGTH_SHORT).show();
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
                          Toast.makeText(context, R.string.str_pvr_insert_usb, Toast.LENGTH_SHORT).show();
                          return;
                      }
                  } else {
                      startSpeedTest();
                  }
              }
              break;
              case R.id.pvr_file_system_always_layout: {
                  if (mPvrManager.isAlwaysTimeShift() == true) {
                      mPvrManager.setAlwaysTimeShift(false);
                      alwaysTimeShiftType.setText(getResources().getString(R.string.str_set_off));
                  } else {
                      mPvrManager.setAlwaysTimeShift(true);
                      alwaysTimeShiftType.setText(getResources().getString(R.string.str_set_on));
                  }
              }
              break;
          }
      }
  }

  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      context = this;
      mPvrManager = TvPvrManager.getInstance();
      setContentView(R.layout.pvr_option);
      selectDisk = (TextView) findViewById(R.id.pvr_file_system_select_disk);
      timeShiftSize = (TextView) findViewById(R.id.pvr_file_system_time_shift_size);
      diskFormat = (LinearLayout) findViewById(R.id.pvr_file_system_format_layout);
      diskFormatStatus = (TextView) findViewById(R.id.pvr_file_system_format_context);
      speedCheck = (LinearLayout) findViewById(R.id.pvr_file_system_speed_layout);
      speedCheckResult = (TextView) findViewById(R.id.pvr_file_system_speed_context);
      alwaysTimeShift = (LinearLayout) findViewById(R.id.pvr_file_system_always_layout);
      alwaysTimeShiftType = (TextView) findViewById(R.id.pvr_file_system_always_context);
      diskFormatStatus.setVisibility(View.GONE);
      speedCheckResult.setVisibility(View.GONE);
      storageManager = MStorageManager.getInstance(this);
      //storageManager = (MStorageManager) getApplicationContext().getSystemService(STORAGE_SERVICE);

      if (mPvrManager.isAlwaysTimeShift() == true) {
          alwaysTimeShiftType.setText(getResources().getString(R.string.str_set_on));
      } else {
          alwaysTimeShiftType.setText(getResources().getString(R.string.str_set_off));
      }

      usbSelecter = new USBDiskSelecter(this) {

          @Override
          public void onItemChosen(int position, String diskLabel, String diskPath) {
              String fat = "FAT";
              String ntfs = "NTFS";
              selectDisk.setText(diskLabel);
              selectedDiskPath = diskPath;
              if(diskPath.isEmpty()) {
                  Log.e(TAG, "=============>>>>> USB Disk Path is NULL !!!");
                  return;
              }
              Log.d(TAG, "=============>>>>> USB Disk Path = " + diskPath);
              if(diskLabel.regionMatches(6, fat, 0, 3)) {
                  mPvrManager.setPvrParams(diskPath, (short) 2);
              } else if(diskLabel.regionMatches(6, ntfs, 0, 4)||!diskLabel.contains(fat)){
                  mPvrManager.setPvrParams(diskPath, (short) 6);
              }
              /*add by owen.qin begin*/
              saveChooseDiskSettings(true,diskPath,diskLabel);
              /*add by owen.qin end*/

              if (waitToFormat) {
                  /*add by owen.qin begin to check is ntfs before format choosed disk*/
                  if(diskLabel.regionMatches(6, ntfs, 0, 4)||!diskLabel.contains(fat)) {
                      Toast.makeText(PVROptionActivity.this, R.string.str_pvr_unsurpt_flsystem, Toast.LENGTH_SHORT).show();
                      waitToFormat=false;
                      return ;
                  }
                  /*add by owen.qin end*/
                  formatConfirm();
              } else if (waitToSpeedTest) {
                  waitToSpeedTest = false;
                  /*add by owen.qin begin to check is ntfs before format choosed disk*/
                  if(diskLabel.regionMatches(6, ntfs, 0, 4)||!diskLabel.contains(fat)) {
                      Toast.makeText(PVROptionActivity.this, R.string.str_pvr_unsurpt_flsystem, Toast.LENGTH_SHORT).show();
                      return ;
                  }
                  /*add by owen.qin end*/
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
      usbSelecter.dismiss();
      super.onDestroy();
      unregisterReceiver(usbReceiver);
  }
  /*add by owen.qin begin*/
  private void saveChooseDiskSettings(boolean flag,String path,String label){
      SharedPreferences sp=getSharedPreferences(Constants.SAVE_SETTING_SELECT, MODE_PRIVATE);
      Editor editor=sp.edit();
      editor.putBoolean("IS_ALREADY_CHOOSE_DISK", flag);
      editor.putString("DISK_PATH", path);
      editor.putString("DISK_LABEL", label);
      editor.commit();
  }
  /*add by owen.qin end*/

  private void initUIListeners() {
      selectDisk.setOnClickListener(listener);
      timeShiftSize.setOnClickListener(listener);
      diskFormat.setOnClickListener(listener);
      speedCheck.setOnClickListener(listener);
      alwaysTimeShift.setOnClickListener(listener);
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
              .setPositiveButton(R.string.str_program_edit_dialog_ok, new DialogInterface.OnClickListener() {

                  public void onClick(DialogInterface dialog, int id) {
                      startFormat();
                  }
              }).setNegativeButton(R.string.str_program_edit_dialog_cancel, new DialogInterface.OnClickListener() {

                  public void onClick(DialogInterface dialog, int id) {
                      dialog.dismiss();
                      waitToFormat = false;
                  }
              });
      AlertDialog dialog = builder.create();
      dialog.show();
      dialog.getButton(AlertDialog.BUTTON_NEGATIVE).requestFocus();
  }

  private void stopRecordAndPlayback() {
      if (mPvrManager.isPlaybacking()) {
          mPvrManager.stopPlayback();
      }
      if (mPvrManager.isRecording()) {
          mPvrManager.stopRecord();
      }
  }

  private void startFormatDisk(final String path) {
      final ProgressDialog mpDialog = new ProgressDialog(this);
      mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      mpDialog.setMessage(getResources().getString(R.string.str_pvr_format_usb_progressing));
      mpDialog.setIndeterminate(false);
      mpDialog.setCancelable(false);
      mpDialog.show();
      final Handler handler = new Handler();
      diskFormatStatus.setText(null);
      new Thread(new Runnable() {
          @Override
          public void run() {
              final boolean result = storageManager.formatVolume(path);
              handler.post(new Runnable() {

                  @Override
                  public void run() {
                      if (result) {
                          Log.d(TAG, "Success to format" + path);
                          if (storageManager.mountVolume(path)) {
                              Log.d(TAG, "Success to mount" + path + " again");
                              diskFormatStatus.setText(R.string.str_pvr_file_system_format_context);
                          } else {
                              Log.d(TAG, "Fail to mount" + path + " again");
                          }
                      } else {
                          Log.d(TAG, "Fail to format" + path);
                      }
                      mpDialog.dismiss();
                  }
              });
          }
      }).start();
  }

  private void startFormat() {
      stopRecordAndPlayback();
      if (storageManager.getVolumeState(selectedDiskPath).equals(Environment.MEDIA_MOUNTED)) {
          waitToFormat = true;
          storageManager.unmountVolume(selectedDiskPath, true, false);
      } else if (storageManager.getVolumeState(selectedDiskPath).equals(Environment.MEDIA_UNMOUNTED)) {
          startFormatDisk(selectedDiskPath);
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
              final String Speed = checkSpeed(selectedDiskPath) + "KB/S";
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

  public int checkSpeed(String path) {

         int setSize = 100*1024*1024; // for file size
         long size = 0;
         byte [] data = new byte[512]; // for block size each time we write
         long duration = 0;
         String filePath = path + "/usbspeedtest.txt"; // file name for test

          // Create file
          FileOutputStream output = null;
          File file = new File(filePath);
          duration = System.currentTimeMillis();
          try {
              output = new FileOutputStream(filePath);

              // Get file size in bytes
              size = getFileSize(filePath);

              // Write file whilst the size is smaller than setSize
              while (size < setSize) {
                  output.write(data);
                  output.flush();
                  size = getFileSize(filePath);
              }

              duration = System.currentTimeMillis() - duration;
              output.close();
          }
          catch (IOException  e) {
              e.printStackTrace();
              return mPvrManager.checkUsbSpeed();
          }

          //System.out.println("Finished at - " + size);
          if (file != null) {
              file.delete();
          }
          if (duration == 0) {
              return 0;
          } else {
              return (int)(setSize/duration);
          }
  }

  public static long getFileSize(String filename) {

      File file = new File(filename);
          if (!file.exists() || !file.isFile()) {
              System.out.println("File does not exist");
              return -1;
          }
      return file.length();
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
                      startFormatDisk(path);
                  }
              }
              return;
          }

          if(path.equals(selectedDiskPath) && action.equals(Intent.ACTION_MEDIA_REMOVED)){
              selectDisk.setText(R.string.str_pvr_file_system_select_disk);
              selectedDiskPath = null;
          }
      }
  }

}

