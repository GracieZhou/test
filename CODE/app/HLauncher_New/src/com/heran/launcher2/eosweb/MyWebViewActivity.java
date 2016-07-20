
package com.heran.launcher2.eosweb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.EOSWebView;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.heran.launcher2.R;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

/*
 * projectName： EosLauncher
 * moduleName： MyWebViewActivity.java
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-1-15 下午1:42:28
 * @Copyright © 2013 Eos Inc.
 */

public class MyWebViewActivity extends Activity {

    private final static String TAG = "MyWebViewActivity";

    private EOSWebView mWebView;

    private ProgressBar prgBar;

    private MyWebViewClient mwebviewClien = null;

    private boolean mIsTvStart = false;

    private String loadURL = "http://www.baidu.com";

    private final String ua = "Mozilla/5.0 (iPad; U; " + "CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 "
            + "(KHTML, like Gecko) Version/4.0.4 Mobile/7B367 Safari/531.21.10";

    private final String ALERT_STRING_NEED_CLEAN_HISTORY = "請登入會員";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.app_store_web);
        Intent intent = getIntent();
        if ((intent != null) && (intent.getExtras() != null)) {
            loadURL = intent.getStringExtra("URL");
            mIsTvStart = intent.getBooleanExtra("tv_tag", false);
            Log.d(TAG, "intent:" + loadURL);
        }
        TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
        loadUrl();
    }

    private void loadUrl() {
        mWebView = (EOSWebView) findViewById(R.id.app_store_webview);
        prgBar = (ProgressBar) findViewById(R.id.app_store_prg);
        Log.d(TAG, "---" + "getResources().getInteger(R.integer.webview_init_scale)");
        if (loadURL == "http://www.jowinwin.com/hertv2msd/index.php?r=activity/initPlay&id=473&movie=0&live=0") {
            mWebView.setInitialScale(150);
        } else {
            mWebView.setInitialScale(100);
        }
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.getSettings().setUserAgentString(ua);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        mwebviewClien = new MyWebViewClient(mWebView, prgBar, this);
        mwebviewClien.setHomePage(loadURL);
        mWebView.setWebViewClient(mwebviewClien);

        MyEOSWebClient eosWebClient = new MyEOSWebClient(this, mWebView);
        mWebView.setEosWebClient(eosWebClient);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if (message != null && message.equals(ALERT_STRING_NEED_CLEAN_HISTORY) && mWebView != null) {
                    mWebView.clearHistory();
                }
                return super.onJsAlert(view, url, message, result);
            }

        });
        mWebView.setVisibility(View.VISIBLE);
        mWebView.setFocusable(true);
        mWebView.requestFocus();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (mIsTvStart) {
            android.util.Log.i("zhu", "lancher startActivity");
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClassName("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            mIsTvStart = false;
        }
        super.onBackPressed();

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
    }

}
