
package com.mstar.tv.menu.setting.restore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import com.android.internal.os.storage.ExternalStorageFormatter;
import com.mstar.android.tv.TvFactoryManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.VideoWindowType;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;
import com.mstar.android.tvapi.factory.vo.EnumAcOnPowerOnMode;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.setting.util.Tools;

@SuppressWarnings("deprecation")
public class SystemRestoreFactoryActivity extends Activity {

    private static final String TAG = "SystemRestoreFactoryActivity";

    // backup path
    private static final String TV_DB_BACKUP_TV_DIR = "/tvconfig/TvBackup/Database/";

    private static final String TV_DB_FILE_USER_SETTING = "user_setting.db";

    private static final String TV_DB_FILE_USER_SETTING_JOURNAL = "user_setting.db-journal";

    private static final String TV_DB_BACKUP_FACTORY_DIR = "/tvconfig/Database/";

    private static final String TV_DB_FILE_FACTORY = "factory.db";

    private static final String TV_DB_FILE_FACTORY_JOURNAL = "factory.db-journal";

    private static final String TV_DB_DIR = "/tvdatabase/Database/";

    private static final int DISMISS_DIALOG = 0;

    private Context mContext;

    private CheckBox mCheckBox;

    private Button confirmButton;

    private ProgressDialog progressDialog;

    private ProgressDialog mDialog = null;

    private LinearLayout resetLayout;

    private Boolean isResetFactoryDB = false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler();

    Runnable handlerRuntv = new Runnable() {
        @Override
        public void run() {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                try {
                    TvManager.getInstance().getAudioManager().enableMute(EnumMuteType.E_MUTE_ALL);
                    TvManager.getInstance().getPictureManager().disableBacklight();
                    TvManager.getInstance().setGpioDeviceStatus(36, false);
                    TvManager.getInstance().setGpioDeviceStatus(38, true);
                    TvManager.getInstance().enterSleepMode(true, false);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                finish();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "===>SystemRestoreFactoryActivity");
        setContentView(R.layout.system_restore_factory);
        resetLayout = (LinearLayout) findViewById(R.id.reset);
        mContext = this;
        registerListener();
        Intent intent = getIntent();
        if ((intent != null) && (intent.getExtras() != null)) {
            isResetFactoryDB = intent.getBooleanExtra("isResetFactoryDB", false);
            restoreFactoryCode();
        }
    }

    // Press source+NUM 5522 Reset operation done directly, respond to software
    // written into the preset value( reset factory.db)
    private void restoreFactoryCode() {
        setFullscale();
        new Thread(new Runnable() {
            @Override
            public void run() {
                resetFactoryData(mContext);
            }
        }).start();
        Intent intent = new Intent(ExternalStorageFormatter.FORMAT_AND_FACTORY_RESET);
        intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
        startService(intent);
        resetLayout.setVisibility(View.GONE);
        if (mDialog == null) {
            mDialog = new ProgressDialog(SystemRestoreFactoryActivity.this);
            mDialog.setIndeterminate(true);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.getWindow().setType(2003);
            mDialog.setMessage(getResources().getString(R.string.system_reset_wait));
            mDialog.show();
        }
    }

    private void registerListener() {
        mCheckBox = (CheckBox) findViewById(R.id.restore_factory_cb);
        mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

            }
        });

        mCheckBox.requestFocus();
        // find confirm button
        confirmButton = (Button) findViewById(R.id.clear_button);
        if (confirmButton == null) {
            return;
        }

        // register click event
        confirmButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Tools.isMonkeyTesting()) {
                    return;
                }

                if (mCheckBox.isChecked()) {
                    Log.d(TAG, "===>MASTER_CLEAR");
                    AlertDialog.Builder builder = new Builder(mContext);
                    Resources resource = mContext.getResources();
                    builder.setMessage(resource.getString(R.string.restore_factory_confirm_restore));
                    builder.setTitle(resource.getString(R.string.system_restore_factory));
                    builder.setPositiveButton(resource.getString(R.string.ok), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "choose ok to restore factory");
                            setFullscale();
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    resetFactoryData(mContext);
                                }
                            }).start();
                            Intent intent = new Intent(ExternalStorageFormatter.FORMAT_AND_FACTORY_RESET);
                            intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
                            startService(intent);
                            dialog.dismiss();
                            resetLayout.setVisibility(View.GONE);
                            if (mDialog == null) {
                                mDialog = new ProgressDialog(SystemRestoreFactoryActivity.this);
                                mDialog.setIndeterminate(true);
                                mDialog.setCancelable(false);
                                mDialog.setCanceledOnTouchOutside(false);
                                mDialog.getWindow().setType(2003);
                                mDialog.setMessage(getResources().getString(R.string.system_reset_wait));
                                mDialog.show();
                            }
                        }
                    });

                    builder.setNegativeButton(mContext.getResources().getString(R.string.cancle),
                            new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    // show dialog
                    builder.create().show();
                } else {
                    progressDialog = ProgressDialog.show(SystemRestoreFactoryActivity.this,
                            getResources().getString(R.string.restore_factory_reset_ing),
                            getResources().getString(R.string.system_reset_wait), true, false);
                    // start a new thread to reset TV setting
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            long time = System.currentTimeMillis();
                            resetFactoryData(mContext);
                            Log.v(TAG, "resetTvData---time = " + (System.currentTimeMillis() - time));
                            RestoreUtils.cleanWifiConfig(mContext);
                            mHandler.postDelayed(handlerRuntv, 2000);
                        }
                    }).start();
                    TvFactoryManager.getInstance().setPowerOnMode(EnumAcOnPowerOnMode.E_ACON_POWERON_SECONDARY);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int arg0, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_TV_INPUT:
                    if (confirmButton.hasFocus()) {
                        confirmButton.performClick();
                    } else {
                        mCheckBox.performClick();
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                case KeyEvent.KEYCODE_CHANNEL_UP:
                    if (confirmButton.hasFocus()) {
                        mCheckBox.requestFocus();
                    } else {
                        confirmButton.requestFocus();
                    }
                    return true;
                default:
                    break;
            }
        }
        return super.onKeyDown(arg0, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mDialog != null && mDialog.isShowing()) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * reset user_setting.db, factory.db and set tv channel ,volume,language
     * 
     * @param context
     */
    private void resetFactoryData(Context context) {
        boolean ret = restoreFiles();
        if (ret) {
            Log.d(TAG, "restoreFiles ===>successful");
        } else {
            Log.d(TAG, "restoreFiles===>failed");
        }
        RestoreUtils.resetTvData(context);
    }

    public boolean SetEnvironmentPowerOnMusicVolume(short volume) {
        boolean ret = false;
        try {
            if (TvManager.getInstance() != null) {
                ret = TvManager.getInstance().getFactoryManager().setEnvironmentPowerOnMusicVolume(volume);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        Log.d("SetEnvironmentPowerOnMusicVolume", "Set Music Volume: 0x" + Integer.toHexString(volume));
        return ret;
    }

    public enum EN_ANTENNA_TYPE {
        E_ROUTE_DTMB, E_ROUTE_DVBC, E_ROUTE_DVBT, E_ROUTE_MAX,
    }

    public int queryCurInputSrc() {
        int value = 0;
        Cursor cursor = getContentResolver().query(Uri.parse("content://mstar.tv.usersetting/systemsetting"), null,
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
        }
        cursor.close();
        return value;
    }

    private boolean restoreFiles() {
        boolean result = false;
        File srcFile = new File(TV_DB_BACKUP_TV_DIR, TV_DB_FILE_USER_SETTING);
        File destFile = new File(TV_DB_DIR, TV_DB_FILE_USER_SETTING);
        // reset user_setting.db
        result = copyFile(srcFile, destFile);
        Log.d(TAG, "===>restore user_setting.db data, " + result);
        if (!result) {
            // ret = false;
            return false;
        }

        srcFile = new File(TV_DB_BACKUP_TV_DIR, TV_DB_FILE_USER_SETTING_JOURNAL);
        destFile = new File(TV_DB_DIR, TV_DB_FILE_USER_SETTING_JOURNAL);
        // reset user_setting.db-journal
        result = copyFile(srcFile, destFile);
        Log.d(TAG, "===>restore user_setting.db-journal data, " + result);
        if (!result) {
            // ret = false;
            return false;
        }

        if (isResetFactoryDB) {
            srcFile = new File(TV_DB_BACKUP_FACTORY_DIR, TV_DB_FILE_FACTORY);
            destFile = new File(TV_DB_DIR, TV_DB_FILE_FACTORY);
            // reset factory.db
            result = copyFile(srcFile, destFile);
            Log.d(TAG, "===>restore factory.db data, " + result);
            if (!result) {
                return false;
            }

            srcFile = new File(TV_DB_BACKUP_FACTORY_DIR, TV_DB_FILE_FACTORY_JOURNAL);
            destFile = new File(TV_DB_DIR, TV_DB_FILE_FACTORY_JOURNAL);
            // reset factory.db-journal
            result = copyFile(srcFile, destFile);
            Log.d(TAG, "===>restore factory.db-journal data, " + result);
            if (!result) {
                return false;
            }
        }

        return true;
    }

    /**
     * copy a file from srcFile to destFile
     * 
     * @param srcFile
     * @param destFile
     * @return true if succeed,else false
     */
    private boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(srcFile).getChannel();
            outputChannel = new FileOutputStream(destFile).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            inputChannel.close();
            outputChannel.close();
            result = true;
            // change file mode
            chmod(destFile);
        } catch (IOException e) {
            Log.d(TAG, "copyFile(File srcFile, File destFile), " + e.getMessage());
            return false;
        }
        return result;
    }

    private void chmod(File file) {
        try {
            String command = "chmod 666 " + file.getAbsolutePath();
            Log.d(TAG, "command = " + command);

            Runtime runtime = Runtime.getRuntime();
            @SuppressWarnings("unused")
            Process proc = runtime.exec(command);
        } catch (IOException e) {
            Log.d(TAG, "chmod 666 fail!");
            e.printStackTrace();
        }
    }

    /**
     * Set fullscreen
     */
    public void setFullscale() {
        try {
            VideoWindowType videoWindowType = new VideoWindowType();
            videoWindowType.height = 0xFFFF;
            videoWindowType.width = 0xFFFF;
            videoWindowType.x = 0xFFFF;
            videoWindowType.y = 0xFFFF;
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getPictureManager().selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
                TvManager.getInstance().getPictureManager().setDisplayWindow(videoWindowType);
                TvManager.getInstance().getPictureManager().scaleWindow();
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        super.onDestroy();
    }
}
