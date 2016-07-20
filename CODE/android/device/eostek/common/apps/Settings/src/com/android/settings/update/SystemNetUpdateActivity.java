
package com.android.settings.update;

import scifly.dm.EosDownloadListener;
import scifly.dm.EosDownloadManager;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.update.UpdateService.UpdateBinder;
import com.android.settings.widget.TitleWidget;
import com.eostek.streamnetplusservice.service.StreamNetManager;
import com.eostek.streamnetplusservice.service.TaskInfoInternal;
import com.eostek.streamnetplusservice.service.TaskListener;

/**
 * This class provide the system net update activity.
 * 
 * @author Psso.Song
 */
public class SystemNetUpdateActivity extends Activity {

    private View mVersionCheckingView;

    private LinearLayout mLayoutProgress;

    private LinearLayout mLayoutNewVersionFound;

    private LinearLayout mLayoutCurrentVersion;

    private LinearLayout mLayoutNoNewVersion;

    private LinearLayout mLayoutNoStorage;

    private TextView mTvDescription;

    private TextView mTvProgress;

    private ProgressBar mProgressBar;

    private TextView mTvCurrentVersion;

    private TextView mTvNewVersion;

    private TextView mTvPackageSize;

    private Button mBtUpdate;

    private Button mBtContinue;

    private Button mBtExit;

    private UpdateService mService;

    private StreamNetManager mSnManager;

    private EosDownloadManager mManager;

    private VersionInfor mInfo;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            log("onServiceDisconnected...");
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            log("onServiceConnected...");
            mService = ((UpdateBinder) arg1).getService();
            if (mService != null) {
                log("start checkVersion...");
                mService.checkVersion(Constants.EXTRA_INCREMENTAL);
                registerReceiver();
            }
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Constants.MSG_CONTINUE_DOWNLOAD:
                    mTvProgress.setText(getString(R.string.download_error));
                    mLayoutProgress.setVisibility(View.VISIBLE);
                    showButtion(mBtContinue.getId());
                    break;
                case Constants.MSG_PROGRESS_AVAILABLE:
                    int newProgress = msg.arg1;
                    int curProgress = mProgressBar.getProgress();
                    if (newProgress > curProgress) {
                        mTvProgress.setText(newProgress + "%");
                        mProgressBar.setProgress(newProgress);
                        // Download completed, show exit buttion.
                        if (newProgress == 100) {
                            showButtion(mBtExit.getId());
                        }
                    }
                    if (mProgressBar.getProgress() <= 0) {
                        mTvProgress.setText("0%");
                        mProgressBar.setProgress(0);
                    }
                    if (!mLayoutProgress.isShown()) {
                        showLayout(mLayoutProgress.getId());
                    }
                    break;
            }
        };
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.ACTION_VERSION_INFO_AVAILABLE.equals(action)) {
                int version = intent.getIntExtra(Constants.EXTRA_VERSION_INFO, Constants.VERSION_STATUS_INIT);
                log("Version info available ,version=" + Utils.int2String(version));
                mInfo = mService.getVersionInfo();
                // Fresh UI
                freshUI(version);
            }
        }
    };

    private void freshUI(int version) {
        if (mInfo == null) {
            // show error
            showLayout(mLayoutNoNewVersion.getId());
            mTvNewVersion.setText(Constants.VERSION_INVALID);
            mTvDescription.setText(getString(R.string.no_new_version_available));
            if (mTvPackageSize.isShown()) {
                mTvPackageSize.setVisibility(View.INVISIBLE);
            }
            showButtion(mBtExit.getId());
            return;
        }
        switch (version) {
            case Constants.VERSION_STATUS_INIT:
                break;
            case Constants.VERSION_STORAGE_FAIL:
                // Storage not enough.
                showLayout(mLayoutNoStorage.getId());
                mTvNewVersion.setText(mInfo.getVersion());
                mTvDescription.setText(mInfo.getUds());
                StringBuilder storageFailSb = new StringBuilder(getString(R.string.update_size));
                storageFailSb.append(mInfo.getSize() / 1024).append("MB");
                mTvPackageSize.setText(storageFailSb.toString());
                if (!mTvPackageSize.isShown()) {
                    mTvPackageSize.setVisibility(View.VISIBLE);
                }
                showButtion(mBtExit.getId());
                break;
            case Constants.VERSION_STATUS_FAILED:
                // Get version info failed.
                showLayout(mLayoutNoNewVersion.getId());
                mTvNewVersion.setText(Constants.VERSION_INVALID);
                mTvDescription.setText(getString(R.string.no_new_version_available));
                if (mTvPackageSize.isShown()) {
                    mTvPackageSize.setVisibility(View.INVISIBLE);
                }
                showButtion(mBtExit.getId());
                break;
            case Constants.VERSION_STATUS_CURRENT:
                // Current version is the latest.
                showLayout(mLayoutCurrentVersion.getId());
                mTvNewVersion.setText(mInfo.getVersion());
                mTvDescription.setText(mInfo.getUds());
                StringBuilder currentSb = new StringBuilder(getString(R.string.update_size));
                currentSb.append(mInfo.getSize() / 1024).append("MB");
                mTvPackageSize.setText(currentSb.toString());
                if (!mTvPackageSize.isShown()) {
                    mTvPackageSize.setVisibility(View.VISIBLE);
                }
                showButtion(mBtExit.getId());
                break;
            case Constants.VERSION_STATUS_LAST:
                // Last version is available, show progress.
                mTvNewVersion.setText(mInfo.getLastVersion());
                mTvDescription.setText(mInfo.getLastUds());
                StringBuilder lastSb = new StringBuilder(getString(R.string.update_size));
                lastSb.append(mInfo.getLastSize() / 1024).append("MB");
                mTvPackageSize.setText(lastSb.toString());
                if (!mTvPackageSize.isShown()) {
                    mTvPackageSize.setVisibility(View.VISIBLE);
                }
                if (!mLayoutProgress.isShown()) {
                    mTvProgress.setText("0%");
                    mProgressBar.setProgress(0);
                    showLayout(mLayoutProgress.getId());
                }
                if (startMonitor()) {
                    showButtion(-1);
                } else {
                    mTvProgress.setText(getString(R.string.download_error));
                    showLayout(mLayoutProgress.getId());
                    showButtion(mBtExit.getId());
                }
                break;
            case Constants.VERSION_STATUS_NETWORK:
                // Find a new version.
                showLayout(mLayoutNewVersionFound.getId());
                mTvNewVersion.setText(mInfo.getVersion());
                mTvDescription.setText(mInfo.getUds());
                StringBuilder networkSb = new StringBuilder(getString(R.string.update_size));
                networkSb.append(mInfo.getSize() / 1024).append("MB");
                mTvPackageSize.setText(networkSb.toString());
                if (!mTvPackageSize.isShown()) {
                    mTvPackageSize.setVisibility(View.VISIBLE);
                }
                showButtion(mBtUpdate.getId());
                break;
        }
    }

    private void showLayout(int id) {
        switch (id) {
            case -1:
                // Hide all Layout.
                mVersionCheckingView.setVisibility(View.INVISIBLE);
                mLayoutProgress.setVisibility(View.INVISIBLE);
                mLayoutNewVersionFound.setVisibility(View.INVISIBLE);
                mLayoutCurrentVersion.setVisibility(View.INVISIBLE);
                mLayoutNoNewVersion.setVisibility(View.INVISIBLE);
                mLayoutNoStorage.setVisibility(View.INVISIBLE);
                break;
            case R.id.system_update_version_checking:
                mVersionCheckingView.setVisibility(View.VISIBLE);
                mLayoutProgress.setVisibility(View.INVISIBLE);
                mLayoutNewVersionFound.setVisibility(View.INVISIBLE);
                mLayoutCurrentVersion.setVisibility(View.INVISIBLE);
                mLayoutNoNewVersion.setVisibility(View.INVISIBLE);
                mLayoutNoStorage.setVisibility(View.INVISIBLE);
                break;
            case R.id.layout_show_progress:
                mVersionCheckingView.setVisibility(View.INVISIBLE);
                mLayoutProgress.setVisibility(View.VISIBLE);
                mLayoutNewVersionFound.setVisibility(View.INVISIBLE);
                mLayoutCurrentVersion.setVisibility(View.INVISIBLE);
                mLayoutNoNewVersion.setVisibility(View.INVISIBLE);
                mLayoutNoStorage.setVisibility(View.INVISIBLE);
                break;
            case R.id.layout_new_version_found:
                mVersionCheckingView.setVisibility(View.INVISIBLE);
                mLayoutProgress.setVisibility(View.INVISIBLE);
                mLayoutNewVersionFound.setVisibility(View.VISIBLE);
                mLayoutCurrentVersion.setVisibility(View.INVISIBLE);
                mLayoutNoNewVersion.setVisibility(View.INVISIBLE);
                mLayoutNoStorage.setVisibility(View.INVISIBLE);
                break;
            case R.id.layout_current_version:
                mVersionCheckingView.setVisibility(View.INVISIBLE);
                mLayoutProgress.setVisibility(View.INVISIBLE);
                mLayoutNewVersionFound.setVisibility(View.INVISIBLE);
                mLayoutCurrentVersion.setVisibility(View.VISIBLE);
                mLayoutNoNewVersion.setVisibility(View.INVISIBLE);
                mLayoutNoStorage.setVisibility(View.INVISIBLE);
                break;
            case R.id.layout_no_new_version:
                mVersionCheckingView.setVisibility(View.INVISIBLE);
                mLayoutProgress.setVisibility(View.INVISIBLE);
                mLayoutNewVersionFound.setVisibility(View.INVISIBLE);
                mLayoutCurrentVersion.setVisibility(View.INVISIBLE);
                mLayoutNoNewVersion.setVisibility(View.VISIBLE);
                mLayoutNoStorage.setVisibility(View.INVISIBLE);
                break;
            case R.id.layout_no_enough_storage:
                mVersionCheckingView.setVisibility(View.INVISIBLE);
                mLayoutProgress.setVisibility(View.INVISIBLE);
                mLayoutNewVersionFound.setVisibility(View.INVISIBLE);
                mLayoutCurrentVersion.setVisibility(View.INVISIBLE);
                mLayoutNoNewVersion.setVisibility(View.INVISIBLE);
                mLayoutNoStorage.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showButtion(int id) {
        switch (id) {
            case -1:
                // Hide all buttons.
                mBtUpdate.setVisibility(View.INVISIBLE);
                mBtContinue.setVisibility(View.INVISIBLE);
                mBtExit.setVisibility(View.INVISIBLE);
                break;
            case R.id.update:
                mBtUpdate.setVisibility(View.VISIBLE);
                mBtUpdate.requestFocus();
                mBtUpdate.setFocusableInTouchMode(true);
                mBtUpdate.requestFocusFromTouch();

                mBtContinue.setVisibility(View.INVISIBLE);
                mBtExit.setVisibility(View.INVISIBLE);
                break;
            case R.id.continuing:
                mBtUpdate.setVisibility(View.INVISIBLE);

                mBtContinue.setVisibility(View.VISIBLE);
                mBtContinue.requestFocus();
                mBtContinue.setFocusableInTouchMode(true);
                mBtContinue.requestFocusFromTouch();

                mBtExit.setVisibility(View.INVISIBLE);
                break;
            case R.id.exit:
                mBtUpdate.setVisibility(View.INVISIBLE);
                mBtContinue.setVisibility(View.INVISIBLE);

                mBtExit.setVisibility(View.VISIBLE);
                mBtExit.requestFocus();
                mBtExit.setFocusableInTouchMode(true);
                mBtExit.requestFocusFromTouch();
                break;
        }
    }

    private boolean startMonitor() {
        if (mInfo == null) {
            loge("Fail to get version info");
            return false;
        }
        String engine = mInfo.getEngine();
        if (Utils.isEmpty(engine)) {
            loge("Fail to get engine");
            return false;
        }
        log("Start monitoring with engine " + engine + " ...");
        if (Constants.DOWNLOAD_ENGINE_P2P.equalsIgnoreCase(engine)) {
            String lastVersion = mInfo.getLastVersion();
            if (Utils.isEmpty(lastVersion)) {
                loge("Fail to get lastVersion");
                return false;
            }
            String snId = mInfo.getLastSnId();
            if (Utils.isEmpty(snId)) {
                loge("Fail to get snId");
                return false;
            }

            final TaskInfoInternal info = mSnManager.getTaskInfo(snId);
            if (info == null) {
                loge("Fail to get Sn+ download task info");
                return false;
            }
            switch (info.getTaskState()) {
                case Constants.SN_TASK_STATE_COMPLETE:
                    Message completedMsg = mHandler.obtainMessage(Constants.MSG_PROGRESS_AVAILABLE);
                    completedMsg.arg1 = 100;
                    mHandler.sendMessage(completedMsg);
                    break;
                case Constants.SN_TASK_STATE_ERROR:
                case Constants.SN_TASK_STATE_PAUSED:
                    mHandler.sendEmptyMessage(Constants.MSG_CONTINUE_DOWNLOAD);
                    break;
                case Constants.SN_TASK_STATE_READY:
                case Constants.SN_TASK_STATE_RUNNING:
                    break;
            }
            mSnManager.setTaskListener(snId, new TaskListener() {
                @Override
                public void OnInfo(int progress, int speed) throws RemoteException {
                    super.OnInfo(progress, speed);
                    if (progress % 10 == 0) {
                        log("Sn+ download onInfo, progress=" + progress + ", speed=" + speed);
                    }
                    Message msg = mHandler.obtainMessage(Constants.MSG_PROGRESS_AVAILABLE);
                    msg.arg1 = progress;
                    mHandler.sendMessage(msg);
                }

                @Override
                public void OnComplete() throws RemoteException {
                    super.OnComplete();
                    log("Sn+ download completed!");
                    Message msg = mHandler.obtainMessage(Constants.MSG_PROGRESS_AVAILABLE);
                    msg.arg1 = 100;
                    mHandler.sendMessage(msg);

                    if (Utils.isEmpty(info.getDetail())) {
                        TaskInfoInternal t = mSnManager.getTaskInfo(mInfo.getLastSnId());
                        if (t != null && !Utils.isEmpty(t.getDetail())) {
                            mInfo.setLastSnFilePath(t.getDetail());
                            Utils.putString(getApplicationContext(), Constants.PREFERENCE_LAST_SN_FILE_PATH,
                                    t.getDetail());
                            mService.showCountingDialog(t.getDetail());
                        } else {
                            loge("ERROR Fail to get last sn download file path!!!");
                        }
                    } else {
                        mService.showCountingDialog(info.getDetail());
                    }
                }

                @Override
                public void OnTaskChanged(int state) throws RemoteException {
                    super.OnTaskChanged(state);
                    switch (state) {
                        case Constants.SN_TASK_STATE_ERROR:
                        case Constants.SN_TASK_STATE_PAUSED:
                            Message msg = mHandler.obtainMessage(Constants.MSG_CONTINUE_DOWNLOAD);
                            mHandler.sendMessage(msg);
                            break;
                        case Constants.SN_TASK_STATE_READY:
                        case Constants.SN_TASK_STATE_RUNNING:
                            showButtion(-1);
                            break;
                    }
                }

                @Override
                public void OnError(int code, String detail) throws RemoteException {
                    super.OnError(code, detail);
                    loge("Sn+ download error, code=" + code + ", detail=" + detail);
                    mHandler.sendEmptyMessage(Constants.MSG_CONTINUE_DOWNLOAD);
                }
            }, true);
        } else if (Constants.DOWNLOAD_ENGINE_HTTP.equalsIgnoreCase(engine)) {
            long id = mInfo.getLastId();
            if (id == -1) {
                loge("Fail to get download id");
                return false;
            }
            int status = mManager.getDownloadStatus(id);
            switch (status) {
                case DownloadManager.STATUS_FAILED:
                case DownloadManager.STATUS_PAUSED:
                    Message msg = mHandler.obtainMessage(Constants.MSG_CONTINUE_DOWNLOAD);
                    mHandler.sendMessage(msg);
                    break;
                case DownloadManager.STATUS_PENDING:
                case DownloadManager.STATUS_SUCCESSFUL:
                case DownloadManager.STATUS_RUNNING:
                    mManager.restartTask(id, new EosDownloadListener() {

                        @Override
                        public void onDownloadStatusChanged(int status) {
                            switch (status) {
                                case DownloadManager.STATUS_FAILED:
                                case DownloadManager.STATUS_PAUSED:
                                    Message msg = mHandler.obtainMessage(Constants.MSG_CONTINUE_DOWNLOAD);
                                    mHandler.sendMessage(msg);
                                    break;
                                case DownloadManager.STATUS_PENDING:
                                case DownloadManager.STATUS_RUNNING:
                                    showButtion(-1);
                                    break;
                            }
                        }

                        @Override
                        public void onDownloadSize(long size) {
                        }

                        @Override
                        public void onDownloadComplete(int progress) {
                            if (progress % 10 == 0) {
                                log("Http download onDownloadComplete, progress=" + progress);
                            }
                            Message msg = mHandler.obtainMessage(Constants.MSG_PROGRESS_AVAILABLE);
                            msg.arg1 = progress;
                            mHandler.sendMessage(msg);
                        }
                    });
                    break;
            }
        }
        return true;
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.update:
                    log("Click update button");
                    mService.startUpdate();
                    break;
                case R.id.continuing:
                    log("Click continue button");
                    mService.continueUpdate();
                    break;
                case R.id.exit:
                    log("Click exit button");
                    finish();
                    break;
            }
        }
    };

    private void initView() {
        setTitleWidget();
        mVersionCheckingView = findViewById(R.id.system_update_version_checking);
        // Description.
        mTvDescription = (TextView) findViewById(R.id.description);
        mTvDescription.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvDescription.setScrollbarFadingEnabled(false);
        mTvDescription.setText(getString(R.string.check_new_version));
        mTvDescription.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mTvDescription.setBackgroundResource(R.drawable.update_display_version);
                    log("mTvDescription get focus");
                } else {
                    mTvDescription.setBackgroundResource(R.drawable.update_not_display);
                    log("mTvDescription lost focus");
                }
            }
        });

        // Show progress, invisible now.
        mLayoutProgress = (LinearLayout) findViewById(R.id.layout_show_progress);
        mTvProgress = (TextView) findViewById(R.id.current_progress);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Show new version
        mLayoutNewVersionFound = (LinearLayout) findViewById(R.id.layout_new_version_found);

        // Show current version
        mLayoutCurrentVersion = (LinearLayout) findViewById(R.id.layout_current_version);

        // Show no new version
        mLayoutNoNewVersion = (LinearLayout) findViewById(R.id.layout_no_new_version);

        // Show no enough storage space
        mLayoutNoStorage = (LinearLayout) findViewById(R.id.layout_no_enough_storage);

        // Version
        mTvCurrentVersion = (TextView) findViewById(R.id.current_version);
        mTvCurrentVersion.setText(android.os.Build.VERSION.INCREMENTAL);
        mTvNewVersion = (TextView) findViewById(R.id.new_version);
        mTvNewVersion.setText(Constants.VERSION_INVALID);

        // Package size TextView
        mTvPackageSize = (TextView) findViewById(R.id.package_size);

        // Buttons.
        mBtUpdate = (Button) findViewById(R.id.update);
        mBtUpdate.setOnClickListener(mOnClickListener);

        mBtContinue = (Button) findViewById(R.id.continuing);
        mBtContinue.setOnClickListener(mOnClickListener);

        mBtExit = (Button) findViewById(R.id.exit);
        mBtExit.setOnClickListener(mOnClickListener);
    }

    private void setTitleWidget() {
        TitleWidget tw = (TitleWidget) findViewById(R.id.title_widget);
        if (tw != null) {
            tw.setMainTitleText(this.getString(R.string.action_settings));
            tw.setFirstSubTitleText(this.getString(R.string.system_update), false);
            tw.setSecondSubTitleText(this.getString(R.string.system_net_update));
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_VERSION_INFO_AVAILABLE);
        registerReceiver(mReceiver, filter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(mReceiver);
    }

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_net_update);

        initView();

        Intent intent = new Intent(SystemNetUpdateActivity.this, UpdateService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);

        mSnManager = new StreamNetManager(getApplicationContext());
        mManager = new EosDownloadManager(getApplicationContext());
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        unregisterReceiver();
    }

    private void log(String msg) {
        if (Constants.DBG) {
            Log.d(Constants.TAG, "Activity: " + msg);
        }
    }

    private void loge(String msg) {
        Log.e(Constants.TAG, "Activity: " + msg);
    }
}
