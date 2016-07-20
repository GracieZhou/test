
package com.google.tv.eoslauncher.ui.ad;

import java.lang.reflect.InvocationTargetException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.EOSWebView;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.tv.eoslauncher.R;
import com.google.tv.eoslauncher.business.MyEOSWebClient;
import com.google.tv.eoslauncher.business.MyWebViewClient;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

/**
 * projectName： EosLauncher moduleName： AdActivity.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2014-1-20 下午12:54:03
 * @Copyright © 2013 Eos Inc.
 */

public class AdActivity extends Activity {
    private static final String LOG_TAG = "AdActivity";

    private EOSWebView mWebView;

    private ProgressBar prgBar;

    private MyWebViewClient mwebviewClien = null;

    private String loadURL = "https://www.youtube.com/watch?v=4vuYDn4B7kc";

    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    private WebChromeClient.CustomViewCallback customViewCallback;

    private View mCustomView;

    protected FrameLayout mFullscreenContainer;

    private myWebChromeClient mWebChromeClient;

    private String ua = "Mozilla/5.0 (iPad; U; "
            + "CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 "
            + "(KHTML, like Gecko) Version/4.0.4 Mobile/7B367 Safari/531.21.10";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad);
        Intent intent = getIntent();
        if ((intent != null) && (intent.getExtras() != null)) {
            loadURL = intent.getStringExtra("URL");
            Log.d(LOG_TAG, "intent:" + loadURL);
        }
        TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
        loadUrl();

        mWebChromeClient = new myWebChromeClient();
        // add WebChromeClient to support play HTML5 video
        mWebView.setWebChromeClient(mWebChromeClient);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadURL = mWebView.getUrl();
        if (this.mCustomView != null) {
            setFullscreen(false);
            FrameLayout decor = (FrameLayout) this.getWindow().getDecorView();
            decor.removeView(this.mFullscreenContainer);
            this.mFullscreenContainer = null;
            this.mCustomView = null;
            this.customViewCallback.onCustomViewHidden();
            // Show the content view.
            this.setRequestedOrientation(getRequestedOrientation());
        }
        if (!mWebView.hasFocus()) {
            mWebView.loadUrl(loadURL);
        }
        Log.d(LOG_TAG, " click on onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * load the content from the url
     */
    private void loadUrl() {
        mWebView = (EOSWebView) findViewById(R.id.webview);
        prgBar = (ProgressBar) findViewById(R.id.prg);

        // init WebSettings options
        mWebView.clearCache(true);
        mWebView.setInitialScale(getResources().getInteger(R.integer.webview_init_scale));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);
        final String USER_AGENT_STRING = mWebView.getSettings().getUserAgentString() + " Rong/2.0";
        mWebView.getSettings().setUserAgentString(USER_AGENT_STRING);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        
        mwebviewClien = new MyWebViewClient(mWebView, prgBar,this);
        mwebviewClien.setHomePage(loadURL);
        mWebView.setWebViewClient(mwebviewClien);
        MyEOSWebClient eosWebClient = new MyEOSWebClient(this, mWebView);
        mWebView.setEosWebClient(eosWebClient);
        mWebView.requestFocus();

        try {
            mWebView.getClass().getMethod("onResume").invoke(mWebView, (Object[]) null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public boolean inCustomView() {
        return (mCustomView != null);
    }

    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }

    @Override
    protected void onStop() {
        Log.v(LOG_TAG, "onstop");
        super.onStop();
        // when the activity is invisible,stop loading and finish the activity
        try {
            mWebView.stopLoading();
            mWebView.getClass().getMethod("onPause").invoke(mWebView, (Object[]) null);
            mWebView.destroy();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // add back key handle
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inCustomView()) {
                hideCustomView();
                mWebView.requestFocus();
                return true;
            }
            if (mWebView != null) {
                mWebView.stopLoading();
                mWebView.onPause();
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * set full screen to play video
     * 
     * @param enabled True to set full screen,false else
     */
    public void setFullscreen(boolean enabled) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (enabled) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
            if (mCustomView != null) {
                mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
        win.setAttributes(winParams);
    }

    static class FullscreenFrameLayout extends FrameLayout {
        public FullscreenFrameLayout(Context ctx) {
            super(ctx);
            setBackgroundColor(Color.BLACK);
        }

        @Override
        public boolean onTouchEvent(MotionEvent paramMotionEvent) {
            return true;
        }
    }

    class myWebChromeClient extends WebChromeClient {
        private View mVideoProgressView;

        /**
         * play the html5 video in the @param view
         */
        @Override
        public void onShowCustomView(View view, int requestedOrientation,
                CustomViewCallback callback) {
            Log.v("zb.wu", "onShowCustomView1");
            // if is full screen play,hide play view
            if (AdActivity.this.mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            // set full screen to play the video
            FrameLayout localFrameLayout = (FrameLayout) AdActivity.this.getWindow().getDecorView();
            AdActivity.this.mFullscreenContainer = new AdActivity.FullscreenFrameLayout(
                    AdActivity.this);
            AdActivity.this.mFullscreenContainer.addView(view, AdActivity.COVER_SCREEN_PARAMS);
            localFrameLayout.addView(AdActivity.this.mFullscreenContainer,
                    AdActivity.COVER_SCREEN_PARAMS);
            AdActivity.this.mCustomView = view;
            AdActivity.this.setFullscreen(true);
            AdActivity.this.customViewCallback = callback;
            AdActivity.this.setRequestedOrientation(requestedOrientation);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            onShowCustomView(view, getRequestedOrientation(), callback);
        }

        @Override
        public View getVideoLoadingProgressView() {
            // set the view when loading video
            if (mVideoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(AdActivity.this);
                View view = inflater.inflate(R.layout.video_progress, null);
                mVideoProgressView = (LinearLayout) view.findViewById(R.id.progress_indicator);
            }
            return mVideoProgressView;
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            }
            // remove the full screen play view
            setFullscreen(false);
            FrameLayout decor = (FrameLayout) AdActivity.this.getWindow().getDecorView();
            decor.removeView(AdActivity.this.mFullscreenContainer);
            AdActivity.this.mFullscreenContainer = null;
            AdActivity.this.mCustomView = null;
            AdActivity.this.customViewCallback.onCustomViewHidden();
            // Show the content view.
            AdActivity.this.setRequestedOrientation(getRequestedOrientation());
        }
    }

}
