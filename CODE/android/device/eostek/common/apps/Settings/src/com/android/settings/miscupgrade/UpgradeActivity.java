
package com.android.settings.miscupgrade;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.R;

public class UpgradeActivity extends Activity {
    public static final int LAYOUT_TOUPGRADE = 1;

    public static final int LAYOUT_UPGRADING = 2;

    public static final int LAYOUT_UPGRADE_SUCCESS = 3;

    public static final int LAYOUT_TOUPGRADE_FAIL = 4;

    protected static final String TAG = "UpgradeActivity";

    private Button cancel;

    private Button bt_upgrade;

    private LinearLayout bootAnim;

    private LinearLayout bootVideo;

    private LinearLayout launcher;

    private UpgradeHelper mUpgradeHelper = UpgradeHelper.geInstance(this);

    private String[] mUpgradeFiles = Util.files;

    private String mExternalMediaRootPath = Util.path;

    private ProgressBar progressBar;

    private TextView updatingText;

    private TextView progressText;

    private Button retryButton;

    private TextView videoText;

    private TextView animText;

    private TextView launcherText;

    private TextView title;

    final int UPDATING_VIDEO_MSG = 1;

    final int UPDATING_ANIM_MSG = 2;

    final int UPDATING_LAUNCHER_MSG = 3;

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATING_VIDEO_MSG:
                    updatingText.setText(R.string.videoUpgrading);
                    break;
                case UPDATING_ANIM_MSG:
                    updatingText.setText(R.string.animUpgrading);
                    break;
                case UPDATING_LAUNCHER_MSG:
                    updatingText.setText(R.string.lancherUpgrading);
                    break;

                default:
                    break;
            }

        };
    };

    // EosTek Patch Begin
    private BroadcastReceiver mUnmountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_MEDIA_EJECT.equals(intent.getAction())) {
                StorageVolume storage = (StorageVolume)intent.getParcelableExtra(
                        StorageVolume.EXTRA_STORAGE_VOLUME);
                
                if(null != storage && storage.getPath().equals(mExternalMediaRootPath)) {
                    Log.d(TAG, storage.getPath() + " ejected, dismiss activity now !");
                    finish();
                }
            }
        }
    };
    // EosTek Patch End
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initUi(LAYOUT_TOUPGRADE);
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_MEDIA_EJECT);
        iFilter.addDataScheme("file");
        registerReceiver(mUnmountReceiver, iFilter);
    }

    private void initUi(int layoutParam) {

        switch (layoutParam) {
            case LAYOUT_TOUPGRADE:
                setContentView(R.layout.upgrade);
                cancel = (Button) findViewById(R.id.btn_cancel);
                bt_upgrade = (Button) findViewById(R.id.btn_upgrade);
                bt_upgrade.requestFocus();
                cancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                        System.exit(1);
                    }
                });
                bt_upgrade.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (Util.ifNeedUpgrade()) {
                            initUi(LAYOUT_UPGRADING);
                        } else {
                            Toast.makeText(getApplicationContext(), "不需要更新", Toast.LENGTH_LONG).show();
                        }
                        ;
                    }
                });
                title = (TextView) findViewById(R.id.textView1);
                title.setText(R.string.checkUpdateFile);
                bootVideo = (LinearLayout) findViewById(R.id.LinearLayout2_1);
                bootAnim = (LinearLayout) findViewById(R.id.LinearLayout2_2);
                launcher = (LinearLayout) findViewById(R.id.LinearLayout2_3);
                videoText = (TextView) findViewById(R.id.textView2_1);
                animText = (TextView) findViewById(R.id.textView2_2);
                launcherText = (TextView) findViewById(R.id.textView2_3);

                if (Util.hasVideoFile == true) {
                    bootVideo.setVisibility(View.VISIBLE);
                }
                if (Util.hasAnimFile == true) {
                    bootAnim.setVisibility(View.VISIBLE);
                }
                if (Util.hasLauncherFile == true) {
                    launcher.setVisibility(View.VISIBLE);
                }

                break;
            case LAYOUT_UPGRADING:
                setContentView(R.layout.upgrading);
                progressBar = (ProgressBar) findViewById(R.id.progressBar1);
                updatingText = (TextView) findViewById(R.id.textView1);
                progressText = (TextView) findViewById(R.id.TextView02);
                new UpgradingTask().execute(1);
                break;
            case LAYOUT_UPGRADE_SUCCESS:
                setContentView(R.layout.upgrade_success);
                title = (TextView) findViewById(R.id.textView1);
                title.setText(R.string.success);

                cancel = (Button) findViewById(R.id.btn_exit);
                cancel.requestFocus();
                cancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                        System.exit(1);
                    }
                });
                bootVideo = (LinearLayout) findViewById(R.id.LinearLayout2_1);
                bootAnim = (LinearLayout) findViewById(R.id.LinearLayout2_2);
                launcher = (LinearLayout) findViewById(R.id.LinearLayout2_3);
                videoText = (TextView) findViewById(R.id.textView2_1);
                animText = (TextView) findViewById(R.id.textView2_2);
                launcherText = (TextView) findViewById(R.id.textView2_3);
                if (Util.videoUpdateResult) {
                    bootVideo.setVisibility(View.VISIBLE);

                }
                if (Util.animUpdateResult) {
                    bootAnim.setVisibility(View.VISIBLE);

                }
                if (Util.launchUpdateResult) {
                    launcher.setVisibility(View.VISIBLE);
                }
                videoText.setText(R.string.bootVideo);
                animText.setText(R.string.bootAnim);
                launcherText.setText(R.string.launcher);
                break;
            case LAYOUT_TOUPGRADE_FAIL:
                setContentView(R.layout.upgrade_fail);
                title = (TextView) findViewById(R.id.textView1);
                title.setText(R.string.fail);
                title.setTextColor(getResources().getColor(R.color.red));
                cancel = (Button) findViewById(R.id.btn_exit);
                cancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                        System.exit(1);
                    }
                });
                retryButton = (Button) findViewById(R.id.btn_retry);
                retryButton.requestFocus();
                retryButton.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        initUi(LAYOUT_UPGRADING);
                    }
                });
                bootVideo = (LinearLayout) findViewById(R.id.LinearLayout2_1);
                bootAnim = (LinearLayout) findViewById(R.id.LinearLayout2_2);
                launcher = (LinearLayout) findViewById(R.id.LinearLayout2_3);
                videoText = (TextView) findViewById(R.id.textView2_1);
                animText = (TextView) findViewById(R.id.textView2_2);
                launcherText = (TextView) findViewById(R.id.textView2_3);
                if (!Util.videoUpdateResult && Util.hasVideoFile) {
                    bootVideo.setVisibility(View.VISIBLE);
                }
                if (!Util.animUpdateResult && Util.hasAnimFile) {
                    bootAnim.setVisibility(View.VISIBLE);
                }
                if (!Util.launchUpdateResult && Util.hasLauncherFile) {
                    launcher.setVisibility(View.VISIBLE);
                }
                videoText.setText(R.string.bootVideoFail);
                animText.setText(R.string.bootAnimFail);
                launcherText.setText(R.string.launcherFail);
                break;

            default:
                break;
        }

    }

    class UpgradingTask extends AsyncTask<Integer, Integer, Boolean> {
        private UpgradeResult videoResult;

        private UpgradeResult animResult;

        private UpgradeResult launchResult;

        @Override
        protected Boolean doInBackground(Integer... params) {
            int i = 0;
            for (String filename : mUpgradeFiles) {
                i++;
                if (UpgradeConstants.USER_BOOTVIDEO_NAME.equals(filename) && Util.hasVideoFile) {
                    Message msg = new Message();
                    msg.what = UPDATING_VIDEO_MSG;
                    mHandler.sendMessage(msg);
                    videoResult = mUpgradeHelper.upgrade(mExternalMediaRootPath + UpgradeConstants.PARENT_DIR
                            + filename, UpgradeConstants.USER_BOOTVIDEO_PATH);
                    Log.e(UpgradeConstants.TAG, videoResult.name());
                } else if (UpgradeConstants.USER_BOOTANIMATION_NAME.equals(filename) && Util.hasAnimFile) {
                    Message msg = new Message();
                    msg.what = UPDATING_ANIM_MSG;
                    mHandler.sendMessage(msg);
                    animResult = mUpgradeHelper.upgrade(
                            mExternalMediaRootPath + UpgradeConstants.PARENT_DIR + filename,
                            UpgradeConstants.USER_BOOTANIMATION_PATH);
                    Log.e(UpgradeConstants.TAG, animResult.name());
                } else if (UpgradeConstants.USER_LAUNCHER_NAME.equals(filename) && Util.hasLauncherFile) {
                    Message msg = new Message();
                    msg.what = UPDATING_LAUNCHER_MSG;
                    mHandler.sendMessage(msg);
                    launchResult = mUpgradeHelper.upgrade(mExternalMediaRootPath + UpgradeConstants.PARENT_DIR
                            + filename);
                    Log.e(UpgradeConstants.TAG, launchResult.name());
                }
                publishProgress(i * 100 / mUpgradeFiles.length);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }
        
        protected void onProgressUpdate(Integer[] progresses) {
            progressBar.setProgress(progresses[0]);
            progressText.setText("" + progresses[0] + "%");
        }

        protected void onPostExecute(Boolean res) {
            if (Util.checkResult(videoResult)) {
                Util.videoUpdateResult = true;
            }
            if (Util.checkResult(animResult)) {
                Util.animUpdateResult = true;
            }
            if (Util.checkResult(launchResult)) {
                Util.launchUpdateResult = true;
            }
            if (Util.launchUpdateResult || Util.animUpdateResult || Util.videoUpdateResult) {
                initUi(LAYOUT_UPGRADE_SUCCESS);
            } else {
                initUi(LAYOUT_TOUPGRADE_FAIL);
            }
        };
    }
}
