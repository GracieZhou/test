package com.eostek.uitemplate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.eostek.uitemplate.widget.SciflyToast;


public class ToastActivity extends Activity implements OnClickListener{
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast);
        
        findViewById(R.id.btn_tosat_normal).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_tosat_normal:
                SciflyToast.showLongToast(ToastActivity.this, "提示框展示效果图");
                break;

            default:
                break;
        }
    }
}
