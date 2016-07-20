
package com.eostek.scifly.advertising;

import java.net.URISyntaxException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

public class ADBrowserActivity extends Activity {

    private static final String TAG = "ADBrowserActivity";

    private WebView mWebView;

    private RelativeLayout mLayout;

    private Intent targetIntent;
    
    private String mUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String url = intent.getExtras().getString("url");
        try {
            targetIntent = Intent.parseUri(intent.getExtras().getString("targetIntent"), 0);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "url : " + url);

        mUrl = url;
//        shouWebView(url);
        mHandler.post(runnable);
    }

    private void shouWebView(String url) {
        setContentView(R.layout.browser);
        mWebView = (WebView) findViewById(R.id.webView);
        mLayout = (RelativeLayout) findViewById(R.id.layout);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginsEnabled(true);
        mWebView.getSettings().setPluginState(PluginState.ON);
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_ESCAPE) {
            finish();
            if (targetIntent != null) {
                targetIntent.putExtra("fromAD", true);
                startActivity(targetIntent);
            }
        }
        return true;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            switch (keyCode) {
//                case KeyEvent.KEYCODE_BACK:
//                    if (mWebView.canGoBack()) {
//                        mWebView.goBack();
//                    } else {
//                        finish();
//                        if (targetIntent != null) {
//                            targetIntent.putExtra("fromAD", true);
//                            startActivity(targetIntent);
//                        }
//                    }
//                    return true;
//                default:
//                    break;
//            }
//        }
//        return true;
//    }

    @Override
    protected void onStop() {
        System.exit(0);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mWebView.removeAllViews();
        mWebView.destroy();
        mLayout.removeView(mWebView);
        super.onDestroy();
    }

    private Runnable runnable = new Runnable() {
        
        @Override
        public void run() {
            shouWebView(mUrl);
        }
    };
    
    Handler mHandler = new Handler(){
      public void handleMessage(Message msg) {

      };  
    };
}
