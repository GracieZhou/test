
package com.android.settings.update.ota;

import com.android.settings.R;
import com.android.settings.widget.TitleWidget;
import com.android.settings.update.ota.Downloader.DownloadCallback;
import com.android.settings.update.ota.RomChecker.CheckCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SystemNetUpdateActivity extends Activity implements Constants {
    private static Logger sLog = new Logger(SystemNetUpdateActivity.class);

    private static PreferenceHelper sPreferenceHelper;

    private Context mContext;

    private RomChecker mRomChecker;

    // ////////////////////////////////////////////////////////////////////////////
    class ViewHolder {
        public View versionCheckingView;

        public LinearLayout layoutProgress;

        public LinearLayout layoutNewVersionFound;

        public LinearLayout layoutCurrentVersion;

        public LinearLayout layoutNoNewVersion;

        public LinearLayout layoutNoStorage;

        public TextView descriptionTv;

        public TextView progressTv;

        public ProgressBar progressBar;

        public TextView currentVersionTv;

        public TextView newVersionTv;

        public TextView packageSizeTv;

        public Button updateButton;

        public Button redownloadButton;

        public Button exitButton;
    }

    private ViewHolder mHolder = new ViewHolder();

    private void setTitleWidget() {
        TitleWidget tw = (TitleWidget) findViewById(R.id.title_widget);
        if (tw != null) {
            tw.setSubTitleText(getString(R.string.system_update), getString(R.string.system_net_update));
        }
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.update: {
                    PackageInfo pkgInfo = sPreferenceHelper.getPackageInfo();
                    DownloadHelper.registerCallback(mDownloadCallback);
                    DownloadHelper.downloadFile(pkgInfo.getUrl());
                }
                    break;
                case R.id.redownload: {
                    DownloadHelper.clearDownload();
                    PackageInfo pkgInfo = sPreferenceHelper.getPackageInfo();
                    DownloadHelper.registerCallback(mDownloadCallback);
                    DownloadHelper.downloadFile(pkgInfo.getUrl());
                }
                    break;
                case R.id.exit:
                    finish();
                    break;
            }
        }
    };

    private void initView() {
        setTitleWidget();
        mHolder.versionCheckingView = findViewById(R.id.system_update_version_checking);
        mHolder.descriptionTv = (TextView) findViewById(R.id.description);

        mHolder.descriptionTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        mHolder.descriptionTv.setScrollbarFadingEnabled(false);
        mHolder.descriptionTv.setText(getString(R.string.check_new_version));
        mHolder.descriptionTv.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mHolder.descriptionTv.setBackgroundResource(R.drawable.update_display_version);
                } else {
                    mHolder.descriptionTv.setBackgroundResource(R.drawable.update_not_display);
                }
            }
        });

        // Show progress, invisible now.
        mHolder.layoutProgress = (LinearLayout) findViewById(R.id.layout_show_progress);
        mHolder.progressTv = (TextView) findViewById(R.id.current_progress);
        mHolder.progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Show new version
        mHolder.layoutNewVersionFound = (LinearLayout) findViewById(R.id.layout_new_version_found);

        // Show current version
        mHolder.layoutCurrentVersion = (LinearLayout) findViewById(R.id.layout_current_version);

        // Show no new version
        mHolder.layoutNoNewVersion = (LinearLayout) findViewById(R.id.layout_no_new_version);

        // Show no enough storage space
        mHolder.layoutNoStorage = (LinearLayout) findViewById(R.id.layout_no_enough_storage);

        // Version
        mHolder.currentVersionTv = (TextView) findViewById(R.id.current_version);
        mHolder.currentVersionTv.setText(PackageInfo.getLocalFacVer());
        mHolder.newVersionTv = (TextView) findViewById(R.id.new_version);
        mHolder.newVersionTv.setText(Constants.VERSION_INVALID);

        // Package size TextView
        mHolder.packageSizeTv = (TextView) findViewById(R.id.package_size);

        // Buttons.
        mHolder.updateButton = (Button) findViewById(R.id.update);
        mHolder.updateButton.setOnClickListener(mOnClickListener);

        mHolder.redownloadButton = (Button) findViewById(R.id.redownload);
        mHolder.redownloadButton.setOnClickListener(mOnClickListener);

        mHolder.exitButton = (Button) findViewById(R.id.exit);
        mHolder.exitButton.setOnClickListener(mOnClickListener);
    }

    // Refresh UI Begin
    public void switchUi(int id, PackageInfo pkgInfo) {

        switch (id) {
            case UI_SPACE_NOT_ENOUGH:
                // Storage not enough.
                showLayout(mHolder.layoutNoStorage.getId());
                mHolder.newVersionTv.setText(pkgInfo.getFacVer());
                mHolder.descriptionTv.setText(pkgInfo.getUds());
                StringBuilder storageFailSb = new StringBuilder(getString(R.string.update_size));
                storageFailSb.append(pkgInfo.getSize() / 1024).append("MB");
                mHolder.packageSizeTv.setText(storageFailSb.toString());
                if (!mHolder.packageSizeTv.isShown()) {
                    mHolder.packageSizeTv.setVisibility(View.VISIBLE);
                }
                showButton(mHolder.exitButton.getId());
                break;
            case UI_VERSION_CHECK_FAILED:
                // Get version info failed.
                showLayout(mHolder.layoutNoNewVersion.getId());
                mHolder.newVersionTv.setText(Constants.VERSION_INVALID);
                mHolder.descriptionTv.setText(getString(R.string.no_new_version_available));
                if (mHolder.packageSizeTv.isShown()) {
                    mHolder.packageSizeTv.setVisibility(View.INVISIBLE);
                }
                showButton(mHolder.exitButton.getId());
                break;
            case UI_VERSION_EQUALS:
                // Current version is the latest.
                showLayout(mHolder.layoutCurrentVersion.getId());
                mHolder.newVersionTv.setText(pkgInfo.getFacVer());
                mHolder.descriptionTv.setText(pkgInfo.getUds());
                StringBuilder currentSb = new StringBuilder(getString(R.string.update_size));
                currentSb.append(pkgInfo.getSize() / 1024).append("MB");
                mHolder.packageSizeTv.setText(currentSb.toString());
                if (!mHolder.packageSizeTv.isShown()) {
                    mHolder.packageSizeTv.setVisibility(View.VISIBLE);
                }
                showButton(mHolder.exitButton.getId());
                break;
            case UI_VERSION_NEWER:
                // Find a new version.
                showLayout(mHolder.layoutNewVersionFound.getId());
                mHolder.newVersionTv.setText(pkgInfo.getFacVer());
                mHolder.descriptionTv.setText(pkgInfo.getUds());
                StringBuilder networkSb = new StringBuilder(getString(R.string.update_size));
                networkSb.append(pkgInfo.getSize() / 1024).append("MB");
                mHolder.packageSizeTv.setText(networkSb.toString());
                if (!mHolder.packageSizeTv.isShown()) {
                    mHolder.packageSizeTv.setVisibility(View.VISIBLE);
                }
                showButton(mHolder.updateButton.getId());
                break;
            case UI_SHOW_PROGRESS:
                // Find a new version.
                showLayout(mHolder.layoutProgress.getId());
                mHolder.newVersionTv.setText(pkgInfo.getFacVer());
                mHolder.descriptionTv.setText(pkgInfo.getUds());
                StringBuilder sb = new StringBuilder(getString(R.string.update_size));
                sb.append(pkgInfo.getSize() / 1024).append("MB");
                mHolder.packageSizeTv.setText(sb.toString());
                if (!mHolder.packageSizeTv.isShown()) {
                    mHolder.packageSizeTv.setVisibility(View.VISIBLE);
                }
                updateProgress(0);
                break;
        }
    }

    private void showLayout(int id) {

        mHolder.versionCheckingView.setVisibility(View.INVISIBLE);
        mHolder.layoutProgress.setVisibility(View.INVISIBLE);
        mHolder.layoutNewVersionFound.setVisibility(View.INVISIBLE);
        mHolder.layoutCurrentVersion.setVisibility(View.INVISIBLE);
        mHolder.layoutNoNewVersion.setVisibility(View.INVISIBLE);
        mHolder.layoutNoStorage.setVisibility(View.INVISIBLE);

        switch (id) {
            case R.id.system_update_version_checking:
                mHolder.versionCheckingView.setVisibility(View.VISIBLE);
                break;
            case R.id.layout_show_progress:
                mHolder.layoutProgress.setVisibility(View.VISIBLE);
                break;
            case R.id.layout_new_version_found:
                mHolder.layoutNewVersionFound.setVisibility(View.VISIBLE);
                break;
            case R.id.layout_current_version:
                mHolder.layoutCurrentVersion.setVisibility(View.VISIBLE);
                break;
            case R.id.layout_no_new_version:
                mHolder.layoutNoNewVersion.setVisibility(View.VISIBLE);
                break;
            case R.id.layout_no_enough_storage:
                mHolder.layoutNoStorage.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showButton(int id) {

        mHolder.updateButton.setVisibility(View.INVISIBLE);
        mHolder.redownloadButton.setVisibility(View.INVISIBLE);
        mHolder.exitButton.setVisibility(View.INVISIBLE);

        Button button = null;

        switch (id) {
            case R.id.update:
                button = mHolder.updateButton;
                break;
            case R.id.redownload:
                button = mHolder.redownloadButton;
                break;
            case R.id.exit:
                button = mHolder.exitButton;
                break;
        }

        if (null != button) {
            button.setVisibility(View.VISIBLE);
            button.requestFocus();
            button.setFocusableInTouchMode(true);
            button.requestFocusFromTouch();
        }

    }

    private void redownload(String error) {
        if (TextUtils.isEmpty(error)) {
            mHolder.progressTv.setText(getString(R.string.download_error));
        } else {
            mHolder.progressTv.setText(getString(R.string.download_error) + " : " + error);
        }

        mHolder.layoutProgress.setVisibility(View.VISIBLE);
        showButton(mHolder.redownloadButton.getId());
    }

    private void downloadPaused(String error) {
        mHolder.progressTv.setText(R.string.error_download_paused);
        mHolder.layoutProgress.setVisibility(View.VISIBLE);
        sLog.error("DownloadPaused:" + error);
    }

    private void updateProgress(int newProgress) {
        showButton(-1);

        int curProgress = mHolder.progressBar.getProgress();
        if (curProgress <= 0) {
            mHolder.progressTv.setText("0%");
            mHolder.progressBar.setProgress(0);
            curProgress = 0;
        }

        if (newProgress >= 0 && newProgress != curProgress) {
            mHolder.progressTv.setText(newProgress + "%");
            mHolder.progressBar.setProgress(newProgress);
            // Download completed, show exit buttion.
            if (newProgress == 100) {
                showButton(mHolder.exitButton.getId());
            }
        }

        if (!mHolder.layoutProgress.isShown()) {
            showLayout(mHolder.layoutProgress.getId());
        }
    }

    // Refresh UI End

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.system_net_update);

        initView();

        mContext = this;
        sPreferenceHelper = PreferenceHelper.getInstance(mContext);
        mRomChecker = new RomChecker(mContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sLog.debug("onResume");

        mRomChecker.setCheckCallback(mCheckCallback);
        boolean checking = mRomChecker.isScanning();
        if (!checking) {
            mRomChecker.check(OTA_TYPE_INCREASE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        sLog.debug("onPause");
        DownloadHelper.unregisterCallback();
    }

    @Override
    protected void onDestroy() {
        sLog.debug("onDestroy");
        super.onDestroy();
    }

    private CheckCallback mCheckCallback = new CheckCallback() {

        @Override
        public void onStartChecking() {
            DownloadHelper.init(mContext, mDownloadCallback);
        }

        @Override
        public void onVersionFound(final PackageInfo pkgInfo) {

            if (null == pkgInfo || !pkgInfo.isLegal()) {
                return;
            }

            sLog.debug("versionFound <<<<< " + pkgInfo.toString());

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (DownloadHelper.checkIfDownloading(pkgInfo.getMd5())) {
                        DownloadHelper.restartDownloading();
                        sLog.info("OTA package is downloading .");
                        switchUi(UI_SHOW_PROGRESS, pkgInfo);
                        DownloadHelper.registerCallback(mDownloadCallback);
                    } else {

                        if (pkgInfo.equalsThanCurrent()) {
                            DownloadHelper.clearDownload();
                            switchUi(UI_VERSION_EQUALS, pkgInfo);
                        } else if (pkgInfo.isNewerThanCurrent()) {

                            if (DownloadHelper.checkIfDownloadCompleted(pkgInfo.getMd5())) {
                                sLog.info("OTA package had download finished.");
                                switchUi(UI_SHOW_PROGRESS, pkgInfo);
                                mDownloadCallback.onDownloadFinished(sPreferenceHelper.getDownloadPath(),
                                        pkgInfo.getMd5());
                            } else {
                                DownloadHelper.clearDownload();
                                sPreferenceHelper.savePackageInfo(pkgInfo);
                                boolean hasEnoughSpace = DownloadHelper.hasEnoughSpace(pkgInfo.getSize());
                                if (hasEnoughSpace) {
                                    switchUi(UI_VERSION_NEWER, pkgInfo);
                                } else {
                                    switchUi(UI_SPACE_NOT_ENOUGH, pkgInfo);
                                }
                            }

                        } else {
                            sPreferenceHelper.setEtag("");
                            DownloadHelper.clearDownload();
                            switchUi(UI_VERSION_CHECK_FAILED, null);
                        }
                    }
                }
            });
        }

        @Override
        public void onCheckError() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    switchUi(UI_VERSION_CHECK_FAILED, null);
                }
            });
        }
    };

    // ////////////////////////////////////////////////////////////////////////
    private DownloadCallback mDownloadCallback = new DownloadCallback() {

        @Override
        public void onDownloadStarted() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    updateProgress(0);
                }
            });

        }

        @Override
        public void onDownloadProgress(final int progress) {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    updateProgress(progress);
                }
            });

        }

        @Override
        public void onDownloadFinished(final String path, final String md5) {
            sLog.info("onDownloadFinished:" + path + ", md5:" + md5);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    onDownloadProgress(100);
                    // than broadcast download finished intent.
                    Intent intent = new Intent(ACTION_DOWNLOAD_FINISHED);
                    intent.setClass(mContext, UpdateService.class);
                    intent.putExtra(EXTRA_PATH, path);
                    intent.putExtra(EXTRA_MD5, md5);
                    mContext.startService(intent);
                }
            });

        }

        @Override
        public void onDownloadError(final String reason) {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    redownload(reason);
                }
            });

        }

        @Override
        public void onDownloadPaused(final String reason) {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    downloadPaused(reason);
                }
            });

        }
    };
}
