
package com.eostek.tvmenu.advance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.utils.Tools;
import com.mstar.android.tv.TvFactoryManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.factory.vo.EnumAcOnPowerOnMode;

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
import android.widget.Toast;

public class SystemRestoreFactoryActivity extends Activity {

    private static final String TAG = "SystemRestoreFactoryActivity";

    private static final String MSTAR_CLEAR = "android.intent.action.MASTER_CLEAR";

    // backup path
    private static final String TV_DB_BACKUP_DIR = "/tvdatabase/DatabaseBackup/";

    private static final String TV_DB_FILE__USER_SETTING = "user_setting.db";

    private static final String TV_DB_FILE_FACTORY = "factory.db";

    private static final String TV_DB_DIR = "/tvdatabase/Database/";

    private static final int DISMISS_DIALOG = 0;

    private Context mContext;

    private CheckBox mCheckBox;

    private Button confirmButton;

    private ProgressDialog progressDialog;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                finish();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "===>SystemRestoreFactoryActivity");
        setContentView(R.layout.system_restore_factory);

        mContext = this;
        registerListener();
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
                            resetFactoryData(mContext);
                            Toast.makeText(mContext,
                                    mContext.getResources().getString(R.string.restore_factory_system_reboot),
                                    Toast.LENGTH_LONG).show();
                            try {
                                TvFactoryManager.getInstance()
                                        .setPowerOnMode(EnumAcOnPowerOnMode.E_ACON_POWERON_DIRECT);
                                TvManager.getInstance().setEnvironment("htvstandbystate", "1");
                            } catch (TvCommonException e) {
                                Log.e(TAG, "setEnvironment htvstandbystate error !");
                            }
                            Log.d(TAG, "===>MASTER_CLEAR");
                            sendBroadcast(new Intent(MSTAR_CLEAR));
                            dialog.dismiss();
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
                            getResources().getString(R.string.restore_factory_reset_wait), true, false);
                    // start a new thread to reset TV setting
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            long time = System.currentTimeMillis();
                            // RestoreUtils.resetTvData(SystemRestoreFactoryActivity.this);
                            resetFactoryData(mContext);
                            Log.v(TAG, "resetTvData---time = " + (System.currentTimeMillis() - time));
                            mHandler.sendEmptyMessage(DISMISS_DIALOG);
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
        File srcFile = new File(TV_DB_BACKUP_DIR, TV_DB_FILE__USER_SETTING);
        File destFile = new File(TV_DB_DIR, TV_DB_FILE__USER_SETTING);
        // reset user_setting.db
        result = copyFile(srcFile, destFile);
        Log.d(TAG, "===>restore user_setting data, " + result);
        if (!result) {
            // ret = false;
            return false;
        }

        srcFile = new File(TV_DB_BACKUP_DIR, TV_DB_FILE_FACTORY);
        destFile = new File(TV_DB_DIR, TV_DB_FILE_FACTORY);
        // reset factory.db
        result = copyFile(srcFile, destFile);
        Log.d(TAG, "===>restore factory data, " + result);
        if (!result) {
            return false;
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
            Process proc = runtime.exec(command);
        } catch (IOException e) {
            Log.d(TAG, "chmod 666 fail!");
            e.printStackTrace();
        }
    }

}
