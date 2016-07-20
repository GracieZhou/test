
package com.android.settings.network.downloadspeed;

import com.android.settings.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class DownloadSpeedActivity extends Activity {
    private static final String TAG = "NetworkDiag";

    private DownloadSpeedLogic mDownloadSpeedLogic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        Log.i(TAG, ">>>>Enter into initUI");
        mDownloadSpeedLogic = new DownloadSpeedLogic(this);
        new DownloadSpeedHolder(this, mDownloadSpeedLogic);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDownloadSpeedLogic.mDonwloadCountTimer != null) {
                Log.i("mDownloadSpeedLogic", "mDownloadSpeedLogic: " + mDownloadSpeedLogic);
                mDownloadSpeedLogic.stopDownload();
                mDownloadSpeedLogic.mDonwloadCountTimer.cancel();
            }
            this.finish();
            overridePendingTransition(R.anim.fade_out_left, R.anim.fade_out);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDownloadSpeedLogic.deleteTasks();
    }
}
