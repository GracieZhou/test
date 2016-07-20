
package com.android.settings;

import java.lang.reflect.InvocationTargetException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.EOSWebClient;
import android.webkit.EOSWebView;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.settings.androidbug.AndroidBug5497Workaround;

public class SimpleSettingsUpgradeActivity extends Activity {

    private final String URL = "http://www.baidu.com/";

    private EOSWebView webView;

    private String ua = "Mozilla/5.0 (iPad; U; " + "CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 "
            + "(KHTML, like Gecko) Version/4.0.4 Mobile/7B367 Safari/531.21.10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_setting_upgrade_layout);
        AndroidBug5497Workaround.assistActivity(this);
        webView = (EOSWebView) findViewById(R.id.web_view);
        webView.setInitialScale(getResources().getInteger(R.integer.webview_init_scale));
        webView.setFocusable(true);
        webView.requestFocus();
        webView.initEosWebview();
        webView.setFocusStyle("#ff6699", 0);
        webView.setEosWebClient(new EOSWebClient() {
            @Override
            public int getHeranFn2(int i, int j, int k) {
                Log.i("debug","i="+i);
                if (i == 1) {
                    SimpleSettingsUpgradeActivity.this.finish();
                    return 1;
                }
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    SimpleSettingsUpgradeActivity.this.finish();
                }
                return super.getHeranFn2(i, j, k);
            }
            
            @Override
            public void cleanWebViewHistory(String s) {
                Log.d("MyEOSWebClient", "cleanWebViewHistory +++");
                if (webView != null) {
                    // all webview method should called in the same thread
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            webView.clearHistory();
                        }
                    });
                }
                super.cleanWebViewHistory(s);
            }
            
            @Override
            public void AppStoreBackEvent() {
                Log.d("MyEOSWebClient", "AppStoreBackEvent +++");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (webView.canGoBack()) {
                            webView.goBack();
                            Log.d("MyEOSWebClient", "AppStoreBackEvent goBack");
                        } else {
                            finish();
                        }
                    }
                });
                super.AppStoreBackEvent();
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        try {
            webView.getClass().getMethod("onResume").invoke(webView, (Object[]) null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        webSettings.setUserAgentString(ua);
        webSettings.setSupportZoom(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setLoadWithOverviewMode(true);

        String url = getStringFromXml(this, "webview_url");
        if (TextUtils.isEmpty(url)) {
            url = URL;
        }
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private String getStringFromXml(Context ctx, String key) {
        SharedPreferences sPreferences = ctx.getSharedPreferences("data", Context.MODE_PRIVATE);
        return sPreferences.getString(key, null);
    }
    
}
