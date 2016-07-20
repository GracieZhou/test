
package com.eostek.scifly.album.ui;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.eostek.scifly.album.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CloudLoginActivity extends Activity {
    @ViewInject(R.id.login_web)
    private WebView mWebView;

    @ViewInject(R.id.login)
    private TextView mLoginView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_login);
        ViewUtils.inject(this);
        if (getIntent() != null) {
            String typeName = getString(getIntent().getIntExtra("type_id", 0));
            mLoginView.setText(getString(R.string.cloud_add) + typeName);
        }
    }
}
