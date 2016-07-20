
package com.google.tv.eoslauncher.business;

import android.content.Context;
import android.net.http.SslError;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.tv.eoslauncher.R;
import com.google.tv.eoslauncher.util.Utils;

public class MyWebViewClient extends WebViewClient {

    private WebView mWebView = null;

    private View prgBar = null;

    private Context mContext = null;

    public MyWebViewClient(WebView mWebView, View prgBar, Context mContext) {
        this.mWebView = mWebView;
        this.prgBar = prgBar;
        this.mContext = mContext;

    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        Log.d("", "======>oldScale = " + oldScale + "newScale = " + newScale);
        view.setInitialScale(mContext.getResources().getInteger(R.integer.webview_init_scale));
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.d("MyWebViewClient", "onPageFinished:");
        prgBar.setVisibility(View.GONE);
        mWebView.setVisibility(View.VISIBLE);
        view.setFocusable(true);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
        view.setFocusable(false);
    }

    public void setHomePage(String url) {
        if (!Utils.isNetworkState) {
            mWebView.loadUrl("file:///android_asset/error.html");
        } else {
            mWebView.loadUrl(url);
        }
    }
}
