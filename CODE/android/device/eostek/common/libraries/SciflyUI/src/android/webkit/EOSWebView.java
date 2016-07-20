package android.webkit;

import android.annotation.Widget;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.AbsoluteLayout;

import java.io.BufferedWriter;
import java.io.File;
import java.util.Map;

public class EOSWebView extends WebView {

    private final static String TAG = "====>EOSWebView";

    private EOSWebClient mEosClient = null;

    private EOSUtility mUtility = null;

    /**
     * Constructs a new WebView with a Context object.
     * 
     * @param context
     *            a Context object used to access application assets
     */
    public EOSWebView(Context context) {
        super(context, null);
    }

    /**
     * Constructs a new WebView with layout parameters.
     * 
     * @param context
     *            a Context object used to access application assets
     * @param attrs
     *            an AttributeSet passed to our parent
     */
    public EOSWebView(Context context, AttributeSet attrs) {
        super(context, attrs, com.android.internal.R.attr.webViewStyle);
    }

    /**
     * Constructs a new WebView with layout parameters and a default style.
     * 
     * @param context
     *            a Context object used to access application assets
     * @param attrs
     *            an AttributeSet passed to our parent
     * @param defStyle
     *            the default style resource ID
     */
    public EOSWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle, false);
    }

    /**
     * Constructs a new WebView with layout parameters and a default style.
     * 
     * @param context
     *            a Context object used to access application assets
     * @param attrs
     *            an AttributeSet passed to our parent
     * @param defStyle
     *            the default style resource ID
     * @param privateBrowsing
     *            whether this WebView will be initialized in private mode
     * 
     * @deprecated Private browsing is no longer supported directly via WebView
     *             and will be removed in a future release. Prefer using
     *             {@link WebSettings}, {@link WebViewDatabase},
     *             {@link CookieManager} and {@link WebStorage} for fine-grained
     *             control of privacy data.
     */
    @Deprecated
    public EOSWebView(Context context, AttributeSet attrs, int defStyle,
            boolean privateBrowsing) {
        super(context, attrs, defStyle, null, privateBrowsing);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown");

        if (null != mEosClient) {
            if (!mEosClient.handerKey(keyCode, event)) {
                Log.d(TAG, "hander key to client");
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp");

        if (null != mEosClient) {
            if (!mEosClient.handerKey(keyCode, event)) {
                Log.d(TAG, "hander key to client");
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);

    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        Log.d(TAG, "onKeyMultiple");
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent");
        return super.dispatchKeyEvent(event);
    }

    public void initEosWebview() {
        Log.d(TAG, "initEosWebview init EOSUtility");
        // this.clearCache(true);
        this.getSettings().setJavaScriptEnabled(true);
        mUtility = new EOSUtility(this);
        this.addJavascriptInterface(mUtility, "EOSUtility");
    }

    public void setEosWebClient(EOSWebClient clent) {
        mEosClient = clent;
        if (null != mUtility) {
            mUtility.setEOSClient(clent);
        }
    }

    public boolean setFocusStyle(String color, int width) {
        return false;
    }
}
