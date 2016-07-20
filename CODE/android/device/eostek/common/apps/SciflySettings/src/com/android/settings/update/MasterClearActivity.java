
package com.android.settings.update;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;

import com.android.internal.os.storage.ExternalStorageFormatter;
import com.android.settings.R;
import com.android.settings.util.Utils;

public class MasterClearActivity extends Activity {

    private static final String TAG = "MasterClearDialog";

    private CheckBox isEraseDataCheckBox;

    private Button confirmButton;

    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_clear_dialog);
        findViews();
        registerListener();
    }

    private void registerListener() {
        confirmButton.setOnClickListener(new ResetOnClickListener());
        cancelButton.setOnClickListener(new ResetOnClickListener());
        confirmButton.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
                    confirmButton.setTextColor(Color.rgb(255, 255, 255));
                } else {
                    confirmButton.setTextColor(Color.rgb(0, 0, 0));
                }
            }
        });
        cancelButton.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
                    cancelButton.setTextColor(Color.rgb(255, 255, 255));
                } else {
                    cancelButton.setTextColor(Color.rgb(0, 0, 0));
                }
            }
        });
    }

    private void findViews() {
        isEraseDataCheckBox = (CheckBox) findViewById(R.id.erase_data_checkbox);
        confirmButton = (Button) findViewById(R.id.reset_confirm_button);
        cancelButton = (Button) findViewById(R.id.reset_cancel_button);

    }

    private class ResetOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.reset_confirm_button:
                    dataReset(isEraseDataCheckBox.isChecked());
                    break;
                case R.id.reset_cancel_button:
                    finish();
                    break;
            }
        }
    }

    private void dataReset(boolean checked) {
        if (Utils.isMonkeyRunning()) {
            return;
        }
        boolean emulated = Environment.isExternalStorageEmulated();
        Log.d(TAG, "EraseSdCard=" + checked + ", isExternalStorageEmulated=" + emulated);
        if (emulated) {
            // If external storage is emulated, just delete all files in it.
            // This will be done by Recovery system.
            Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
            intent.putExtra("mEraseSdCard", checked);
            sendBroadcast(intent);
        } else {
            if (checked) {
                // If external storage is not emulated, formatter it.
                Intent intent = new Intent(ExternalStorageFormatter.FORMAT_AND_FACTORY_RESET);
                intent.putExtra("android.intent.extra.REASON", "MasterClearDialog");
                intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
                startService(intent);
            } else {
                Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
                intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                intent.putExtra("android.intent.extra.REASON", "MasterClearDialog");
                sendBroadcast(intent);
            }
        }
        finish();
    }

}
