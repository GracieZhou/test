
package com.eostek.tv.launcher.ui;

import java.io.File;
import java.math.BigDecimal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.ui.view.DownLoadProgressBar;
import com.eostek.tv.launcher.util.LConstants;
import com.eostek.tv.launcher.util.UIUtil;
import com.ieostek.tms.feedback.tool.CommonUtil;
import com.ieostek.tms.upgrade.UpgradeBean;
import com.ieostek.tms.upgrade.intface.DefaultUpgradeImp;
import com.ieostek.tms.upgrade.intface.IUpgrade;
import com.ieostek.tms.upgrade.listener.IDownloadListener;

/**
 * projectName： TVLauncher 
 * moduleName： UpgradeActivity.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2014-9-22 下午2:39:40
 * @Copyright © 2014 Eos Inc.
 */

public class UpgradeActivity extends Activity {

    private static final String TAG = "UpgradeActivity";

    private TextView upgradeTitle;

    private TextView upgradeTips;

    private TextView upgradeStatus;

    private Button upgradeNow;

    private Button upgradeCancel;

    private View upgradeControl;

    private DownLoadProgressBar mDownloadProgress;

    private IUpgrade upgrade;

    private UpgradeBean upgradeBean = new UpgradeBean();

    private String versionName = "";

    String newVersionName = "";

    String newVersionFileSize = "";

    String newVersionDesc = "";

    private volatile boolean startUpdate = false;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.upgrade);
        WindowManager windowManager = getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        Intent intent = getIntent();
        if (getIntent() != null) {
            newVersionName = intent.getStringExtra("newversionname");
            newVersionFileSize = (new BigDecimal(Float.valueOf(intent.getStringExtra("newversionfilesize")) / 1024.0f)
                    .setScale(2, BigDecimal.ROUND_HALF_UP)) + "M";
            newVersionDesc = intent.getStringExtra("newversiondesc");
        }
        Point point = new Point();
        display.getSize(point);
        lp.width = point.x / 2;
        lp.height = point.y / 2;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        initView();
        registerOnclickListenter();
        upgrade = new DefaultUpgradeImp();
        String packName = getPackageName();
        versionName = UIUtil.getVersionName(UpgradeActivity.this);
        int versionCode = UIUtil.getVersionCode(UpgradeActivity.this);
        Log.i(TAG, "packName=====" + packName + "========versionCode=====" + versionCode);
        init(UIUtil.getBBCode(), UIUtil.getSpecialCode(), 2, Environment.getExternalStorageDirectory().getPath()
                + "/Download/", versionCode, packName, upgrade);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // when the acitivity is invisible,just finish it
        setFinish();
    }

    private void initView() {
        upgradeTitle = (TextView) findViewById(R.id.upgrade_title);
        upgradeTitle.setText(R.string.app_upgrade);
        upgradeTips = (TextView) findViewById(R.id.upgrade_tips);
        upgradeStatus = (TextView) findViewById(R.id.upgrade_status);
        upgradeControl = findViewById(R.id.upgrade_bt_control);
        upgradeNow = (Button) findViewById(R.id.upgrade_now);
        upgradeCancel = (Button) findViewById(R.id.upgrade_cancel);
        mDownloadProgress = (DownLoadProgressBar) findViewById(R.id.progress);
        mDownloadProgress.setVisibility(View.GONE);
    }

    private void registerOnclickListenter() {
        upgradeNow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                upgradeTitle.setText(getResources().getString(R.string.upgrade_downloading));
                upgradeStatus.setVisibility(View.VISIBLE);
                mDownloadProgress.setVisibility(View.VISIBLE);
                upgradeControl.setVisibility(View.GONE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (upgrade.checkUpgrade()) {
                            upgrade.startDownLoadTask(downloadListener);
                            startUpdate = true;
                        }

                    }
                }).start();
            }
        });

        upgradeCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setFinish();
            }
        });
    }

    private IDownloadListener downloadListener = new IDownloadListener() {

        @Override
        public void onDownloadSizeChange(int percent) {
            Log.i(TAG, "download percent:" + percent);
            mDownloadProgress.setProgress(percent);
            if (percent == 100) {
                installAPK(upgradeBean.getDownLoadPath() + "/" + upgradeBean.getPkgName() + ".apk");
                setFinish();
            }
        }

        @Override
        public void onDownloadException(final int errorCode) {
            Log.e(TAG, "download error:" + errorCode);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    upgradeStatus.setText(getResources().getString(R.string.app_upgrade_status_failure) + errorCode);
                }
            });
            // TODO 下载失败，错误代码显示
        }
    };

    /**
     * install apk.
     */
    private void installAPK(String path) {
        Log.e(TAG, "installAPK...");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    /**
     * init upgrade object
     * 
     * @param bbNo bb number
     * @param specialNo Special number
     * @param installType Install type
     * @param dowloadPath Download path
     * @param context
     */
    public void init(String bbNo, String specialNo, int installType, String dowloadPath, int versionCode,
            String packName, IUpgrade upgrd) {
        upgradeBean.setServerUrl(LConstants.UPGRADE_SERVER_URL);
        upgradeBean.setPkgName(packName);
        upgradeBean.setCurrVersionCode(versionCode);
        upgradeBean.setBbNO(bbNo);
        upgradeBean.setDeviceCode(specialNo);
        upgradeBean.setInstallTyle(installType);
        upgrd.init(this, upgradeBean);
        if (dowloadPath == null || dowloadPath.equals("")) {
            upgradeBean.setDownLoadPath(getFilesDir().getParentFile().getParent() + File.separator
                    + upgradeBean.getPkgName());
        } else {
            upgradeBean.setDownLoadPath(dowloadPath);
        }
        if (this.upgradeBean.getSysVersion() <= 0) {
            upgradeBean.setSysVersion(CommonUtil.getOsVersionInt());
        }
        upgradeTips.setText(getResources().getString(R.string.oldversionname) + versionName + "\n"
                + getResources().getString(R.string.newversionname) + newVersionName + "\n"
                + getResources().getString(R.string.newversionsize) + newVersionFileSize + "\n"
                + getResources().getString(R.string.updatedesc) + "\n" + newVersionDesc);
        cleanDownloadCache();
    }

    /**
     * clear download cache
     */
    private boolean cleanDownloadCache() {
        File file = new File(upgradeBean.getDownLoadPath() + "/" + upgradeBean.getPkgName() + ".apk");
        return file.delete();
    }

    private void setFinish() {
        if (startUpdate) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        UpgradeActivity.this.finish();
    }

}
