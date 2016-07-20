
package com.eostek.tv.launcher.ui;

import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.HomeActivity;
import com.eostek.tv.launcher.business.DownloadManager;
import com.eostek.tv.launcher.util.FastBlur;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

/*
 * projectName： TVLauncher
 * moduleName： BlurActivity.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-8-25 下午6:48:17
 * @Copyright © 2014 Eos Inc.
 */
/**
 * The backgroud of this activity is the blur picture from current screen shot
 **/
public class BlurActivity extends Activity {

    private final String TAG = BlurActivity.class.getSimpleName();

    private String mDownloadUrl;

    private Dialog mDialog;

    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blur_dialog);
        layout = (RelativeLayout) findViewById(R.id.dialog_rl);

        Intent intent = getIntent();
        if (intent != null) {
            mDownloadUrl = intent.getStringExtra("DownloadUrl");
        }

        showDialog(mDownloadUrl);

        applyBlur();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private void applyBlur() {
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                long time = System.currentTimeMillis();
                Bitmap bitmap = HomeActivity.getCurrentBackground();
                blur(bitmap, layout);
                Log.v(TAG, "onGlobalLayout applyBlur time = " + (System.currentTimeMillis() - time) + "; id = "
                        + bitmap.toString());
            }
        });
    }

    /**
     * create blur bitmap for the given view
     * 
     * @param bkg
     * @param view
     */
    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        // the value set to blur bitmap
        float scaleFactor = 8;
        float radius = 2;

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
        Log.v(TAG, "blur time = " + (System.currentTimeMillis() - startMs + "ms"));
    }

    private void showDialog(final String downloadUrl) {
        final Resources resource = this.getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(resource.getString(R.string.download_tips));
        builder.setMessage(resource.getString(R.string.download_msg));
        builder.setPositiveButton(resource.getString(R.string.download_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str;
                if (downloadUrl == null || downloadUrl.isEmpty()) {
                    Log.e(TAG, "illeagle download url " + downloadUrl);
                    str = resource.getString(R.string.download_illegal_url);
                } else {
                    DownloadManager dManager = DownloadManager.getDownloadManagerInstance(BlurActivity.this);
                    dManager.startDownload(downloadUrl);
                    str = resource.getString(R.string.download_start_downloading);
                    Log.v(TAG, "Download url = " + downloadUrl);
                }
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
                finish();
            }
        });
        builder.setNegativeButton(resource.getString(R.string.download_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    SharedPreferences downloadSP = getApplication().getSharedPreferences("downloadstatus", Context.MODE_PRIVATE);
                    downloadSP.edit().putString("kalaokuri", " ").commit();
                    dialog.dismiss();
                    finish();
                }
            }
        });
        mDialog = builder.show();
    }

}
