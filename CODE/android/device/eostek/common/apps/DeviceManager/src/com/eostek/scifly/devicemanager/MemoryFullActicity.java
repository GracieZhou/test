
package com.eostek.scifly.devicemanager;

import com.eostek.scifly.devicemanager.manage.garbage.GarbageActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MemoryFullActicity extends Activity implements OnClickListener, DialogInterface.OnCancelListener {

    private Button btn_clear;

    private Button btn_check;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_full);
        initViews();
        initValues();
        registerListener();
    }

    @Override
	public void setTheme(int resid) {
		super.setTheme(com.eostek.scifly.style.R.style.Scifly_Dialog);
	}

	private void registerListener() {

        btn_clear.setOnClickListener(this);
        btn_check.setOnClickListener(this);
    }

    private void initValues() {

    }

    private void initViews() {

        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_check = (Button) findViewById(R.id.btn_check);
    }

    @Override
    public void onCancel(DialogInterface arg0) {
        finish();
    }

    @Override
    public void onClick(View arg0) {

        switch (arg0.getId()) {
            case R.id.btn_clear:
                Intent mIntent = new Intent(this, GarbageActivity.class);
                startActivity(mIntent);
                finish();
                break;
            case R.id.btn_check:
                Intent intent = new Intent();
                intent.setClassName("com.android.settings", "com.android.settings.deviceinfo.DeviceInfoActivity");
                intent.putExtra("fragment_name", "StorageInfoFragment");
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
    }

}
