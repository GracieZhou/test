
package com.eostek.scifly.browser.business;

import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.browser.BrowserDownloadListener;
import com.android.browser.BrowserSettings;
import com.android.browser.CrashRecoveryHandler;
import com.android.browser.DataController;
import com.android.browser.DownloadHandler;
import com.android.browser.IntentHandler;
import com.android.browser.IntentHandler.UrlData;
import com.android.browser.MouseControl;
import com.android.browser.NetworkStateHandler;
import com.android.browser.PageProgressView;
import com.android.browser.Performance;
import com.android.browser.UrlHandler;
import com.android.browser.UrlUtils;
import com.android.browser.search.SearchEngine;
import com.eostek.scifly.browser.BrowserActivity;
import com.eostek.scifly.browser.R;
import com.eostek.scifly.browser.ui.MenuDialog;

/**
 * projectName： Browser moduleName： WebViewHelper.java
 * 
 * @author Shirley.jiang
 * @time 2016-1-27 
 */
public class WebViewHelper {

    private final String TAG = "WebViewHelper";

    private static final int PROGRESS_MAX = 100;

    public final String BLANK_URL = "about:blank";

    private BrowserActivity mActivity;

    private NetworkStateHandler mNetworkHandler;

    /**
     * Max of webview.
     */
    public static final int WEBVIEW_LENGTH = 3;

    /**
     * the list to save webview.
     */
    private LinkedList<ScreenShot> mWebViewsList = new LinkedList<ScreenShot>();

    private WebView mCurrentWebView;

    private PageProgressView mProgress;

    private EosWebViewClient mWebViewClient;

    private static WebViewHelper mInstance;

    private CrashRecoveryHandler mCrashRecoveryHandler;

    private MouseControl mMouseControl;

    private UrlHandler mUrlHandler = null;

    private int stepLength = 10;

    private long lastdowntime1 = 0;

    private long lastdowntime2 = 0;

    private float mCurrentCursorX = 0.0f;

    private float mCurrentCursorY = 0.0f;

    private int mScreenWidth = 0;

    private int mScreenHeight = 0;

    private final int SCROLL_SIZE = 150;

    private boolean mInLoad;

    private static final int START_DOWNLOAD_FILE = 202;

    private static final int START_PLAY_FILE = 203;

    private BrowserDownloadListener mDownloadListener;
    
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_DOWNLOAD_FILE: {
                    DownloadInfo downloadinfo = (DownloadInfo) msg.obj;
                    doDownloading(downloadinfo.mWebView, downloadinfo.mUrl, downloadinfo.mUserAgent,
                            downloadinfo.mContentDisposition, downloadinfo.mMimetype, downloadinfo.mRefererUrl,
                            downloadinfo.mContentLength);
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.start_download_tip), Toast.LENGTH_SHORT).show();
                }
                    break;
                case START_PLAY_FILE: {
                    DownloadInfo downloadinfo = (DownloadInfo) msg.obj;
                    doPlayFile(downloadinfo.mWebView, downloadinfo.mUrl, downloadinfo.mUserAgent,
                            downloadinfo.mContentDisposition, downloadinfo.mMimetype, downloadinfo.mRefererUrl,
                            downloadinfo.mContentLength);
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.start_download_tip), Toast.LENGTH_SHORT).show();
                }
            }
        };
    };

    /**
     * init WebViewHelper
     * 
     * @param activity
     */
    private WebViewHelper(BrowserActivity activity) {
        mActivity = activity;
        // mCrashRecoveryHandler = CrashRecoveryHandler.initialize(this);
        // mCrashRecoveryHandler.preloadCrashState();
        mMouseControl = new MouseControl();
        mScreenWidth = mActivity.getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = mActivity.getWindowManager().getDefaultDisplay().getHeight();
        mNetworkHandler = new NetworkStateHandler(activity);
    }

    /**
     * getInstance
     * 
     * @param activity
     * @return
     */
    public static WebViewHelper getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new WebViewHelper((BrowserActivity)activity);
        }
        return mInstance;
    }

    /**
     * first in Browser, add a default webview.
     * @param webView
     * @param progress
     */
    public void addWebView(WebView webView, PageProgressView progress) {
        if (mActivity.mHolder.mNineDialog == null) {
            mActivity.mHolder.mNineDialog = new MenuDialog(mActivity);
        }
        // add webivew to list, and add it to multi-pages at same time.
        // and as the current(top) webview.
        mWebViewsList.add(0,
                new ScreenShot(webView, "" + System.currentTimeMillis(), mActivity.mHolder.mNineDialog.createPage()));

        mCurrentWebView = webView;
        mProgress = progress;
        initWebView();
    }

    /**
     * create a new webview.
     */
    public void createWebView() {
        if (mWebViewsList.size() == WEBVIEW_LENGTH) {
            return;
        }
        WebView webView = new WebView(mActivity);
        webView.setId(R.id.webview);
        // add webivew to list, and add it to multi-pages at same time.
        // and as the current(top) webview.
        mWebViewsList.add(0,
                new ScreenShot(webView, "" + System.currentTimeMillis(), mActivity.mHolder.mNineDialog.createPage()));
        mCurrentWebView = webView;
        initWebView();

        LinearLayout webLayout = mActivity.mHolder.mWebviewLayout;
        View view = webLayout.findViewById(R.id.webview);
        if (view != null) {
            webLayout.removeView(view);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        webLayout.addView(webView, params);
    }

    /**
     * show webview.
     * @param position select which webview to show, top is 0, second is 1, bottom is 2.
     */
    public void showWebView(int position) {
        // update current webview.
        mCurrentWebView = mWebViewsList.get(position).mWebView;

        LinearLayout webLayout = mActivity.mHolder.mWebviewLayout;
        View view = webLayout.findViewById(R.id.webview);
        if (view != null) {
            webLayout.removeView(view);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        webLayout.addView(mCurrentWebView, params);

        // update webview title.
        updateTitleView(mCurrentWebView.getTitle(), mCurrentWebView.getUrl());
    }

    /**
     * init webview setting.
     */
    private void initWebView() {
        WebSettings webSettings = mCurrentWebView.getSettings();
        BrowserSettings.getInstance(mActivity).startManagingSettings(webSettings);

        if (mWebViewClient == null) {
            mWebViewClient = new EosWebViewClient();
        }
        mCurrentWebView.setWebViewClient(mWebViewClient);
        mCurrentWebView.setWebChromeClient(mWebChromeClient);
        
        if (mDownloadListener == null) {
            mDownloadListener = new BrowserDownloadListener() {
                
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                        String referer, long contentLength) {
                    downloadStart(mCurrentWebView, url, userAgent, contentDisposition, mimetype, referer, contentLength);
                }
            };
        }
        mCurrentWebView.setDownloadListener(mDownloadListener);

    }

    /**
     * get current webview.
     * @return
     */
    public WebView getCurrentWebView() {
        if (mCurrentWebView == null || mWebViewsList == null || mWebViewsList.size() == 0) {
            createWebView();
        }
        return mCurrentWebView;
    }

    /**
     * set current webview.
     * @param webView
     */
    public void setCurrentWebView(WebView webView) {
        mCurrentWebView = webView;
    }

    /**
     * go Back.
     */
    public void goBack() {
        if (mCurrentWebView != null && mCurrentWebView.canGoBack()) {
            mCurrentWebView.goBack();
        }
    }

    /**
     * go Forward.
     */
    public void goForward() {
        if (mCurrentWebView != null && mCurrentWebView.canGoForward()) {
            mCurrentWebView.goForward();
        }
    }

    private void loadUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        mCurrentWebView.loadUrl(url);
    }

    /**
     * load url.
     * @param url
     */
    public void loadUrlFromContext(String url) {
        if (!TextUtils.isEmpty(url) && mCurrentWebView != null) {
            url = UrlUtils.smartUrlFilter(url);

            Log.d(TAG, "url=" + url);
            updateTitleView(null, url);

            if (mInLoad) {
                mCurrentWebView.stopLoading();
            }
            if (!mWebViewClient.shouldOverrideUrlLoading(mCurrentWebView, url)) {
                loadUrl(url);
            }
        }
    }

    class EosWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (view.isPrivateBrowsingEnabled()) {
                // Don't allow urls to leave the browser app when in
                // private browsing mode
                return false;
            }

            if (url.startsWith(UrlHandler.SCHEME_WTAI)) {
                if (url.startsWith(UrlHandler.SCHEME_WTAI_MC)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(WebView.SCHEME_TEL +
                            url.substring(UrlHandler.SCHEME_WTAI_MC.length())));
                    onNewIntent(intent);
                    return true;
                }
                if (url.startsWith(UrlHandler.SCHEME_WTAI_SD)) {
                    return false;
                }
                if (url.startsWith(UrlHandler.SCHEME_WTAI_AP)) {
                    return false;
                }
            }
            if (url.startsWith("about:")) {
                return false;
            }

            if (mUrlHandler == null) {
                mUrlHandler = new UrlHandler(mActivity);
            }
            if (mUrlHandler.startActivityForUrl(url)) {
                return true;
            }
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "onPageStarted url=" + url);
            CookieSyncManager.getInstance().resetSync();

            if (!mNetworkHandler.isNetworkUp()) {
                view.setNetworkAvailable(false);
            }
            Performance.tracePageStart(url);
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            Log.d(TAG, "doUpdateVisitedHistory url=" + url);
            mActivity.mHolder.mNineDialog.invalidate();
            
            if (TextUtils.isEmpty(url) || url.regionMatches(true, 0, "about:", 0, 6)) {
                return;
            }
            // save or update history.
            DataController.getInstance(mActivity).updateVisitedHistory(url);
            updateTitleView(view.getTitle(), url);
            // mCrashRecoveryHandler.backupState();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.d(TAG, "onReceivedSslError error=" + error);
            handler.proceed();
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            String username = null;
            String password = null;

            boolean reuseHttpAuthUsernamePassword = handler.useHttpAuthUsernamePassword();

            if (reuseHttpAuthUsernamePassword && view != null) {
                String[] credentials = view.getHttpAuthUsernamePassword(host, realm);
                if (credentials != null && credentials.length == 2) {
                    username = credentials[0];
                    password = credentials[1];
                }
            }
            Log.d("", "username=" + username + " , password=" + password);
            if (username != null && password != null) {
                handler.proceed(username, password);
            }
        }

        @Override
        public void onFormResubmission(WebView view, Message dontResend, Message resend) {
            Log.d(TAG, "onFormResubmission error=" + dontResend.toString());
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.d(TAG, "onReceivedError failingUrl=" + failingUrl
                    + " ,onReceivedError errorCode=" + errorCode
                    + " ,onReceivedError description=" + description);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, android.view.KeyEvent event) {
            Log.d(TAG, "shouldOverrideKeyEvent event=" + event.getKeyCode());
            return false;
        }

        @Override
        public void onUnhandledKeyEvent(WebView view, android.view.KeyEvent event) {
            Log.d(TAG, "onUnhandledKeyEvent event=" + event.getKeyCode());
            if (!WebViewHelper.this.onUnhandledKeyEvent(view, event)) {
                super.onUnhandledKeyEvent(view, event);
            }
        }
    }

    private boolean onUnhandledKeyEvent(WebView view, android.view.KeyEvent event) {
        if (!mActivity.isActivityPaused()) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                return mActivity.onKeyDown(event.getKeyCode(), event);
            } else {
                return mActivity.onKeyUp(event.getKeyCode(), event);
            }
        }
        return false;
    }
    private WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            setProgress(newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            Log.d(TAG, "received title=" + title);
            String pageUrl = view.getUrl();
            if (TextUtils.isEmpty(pageUrl) || pageUrl.length() >= SQLiteDatabase.SQLITE_MAX_LIKE_PATTERN_LENGTH) {
                return;
            }
            DataController.getInstance(mActivity).updateHistoryTitle(pageUrl, title);
            updateTitleView(title, pageUrl);
        };

        @Override
        public void onReceivedIcon(WebView view, android.graphics.Bitmap icon) {

        };
    };

    private void setProgress(int newProgress) {
        if (newProgress >= PROGRESS_MAX) {
            mProgress.setProgress(PageProgressView.MAX_PROGRESS);
            mProgress.setVisibility(View.GONE);
            mInLoad = false;
        } else {
            if (!mInLoad) {
                mProgress.setVisibility(View.VISIBLE);
                mInLoad = true;
            }
            mProgress.setProgress(newProgress * PageProgressView.MAX_PROGRESS / PROGRESS_MAX);
        }
    }

    /**
     * stop loading or refresh page.
     */
    public void stopOrRefresh() {
        if (mInLoad) {
            mCurrentWebView.stopLoading();
        } else {
            mCurrentWebView.reload();
        }
    }

    /**
     * handleVirtualMouseKey
     * @param event
     * @return
     */
    public boolean handleVirtualMouseKey(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_PROG_BLUE) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (mMouseControl.getBtnDownState()) {
                    mMouseControl.outDragState();
                } else {
                    mMouseControl.enterDragState();
                }
            }
            return true;
        }
        if ((event.getKeyCode() >= KeyEvent.KEYCODE_DPAD_UP && event.getKeyCode() <= KeyEvent.KEYCODE_DPAD_RIGHT)
                || event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (mCurrentWebView == null) {
                    return false;
                }
                BrowserSettings.getInstance(mActivity).setVirtualMouseEnabled(true);
                TextView titleView = mActivity.mHolder.mWebTitleView;

                long nowdowntime = event.getDownTime();
                long timeinterval = ((nowdowntime - lastdowntime2) + (lastdowntime2 - lastdowntime1)) / 2;
                if (timeinterval > 1500) {
                    stepLength = 10;
                } else if (timeinterval > 0) {
                    stepLength = (int) ((1 / ((float) timeinterval / 1000)) * 15);
                }

                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if (mCurrentCursorY <= titleView.getHeight()) {
                            mCurrentWebView.scrollBy(0, -SCROLL_SIZE);
                            return true;
                        }
                        mMouseControl.moveCursor(0, 0 - stepLength);
                        lastdowntime1 = lastdowntime2;
                        lastdowntime2 = nowdowntime;
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        if (mCurrentCursorY >= mScreenHeight - 5) {
                            mCurrentWebView.scrollBy(0, SCROLL_SIZE);
                            return true;
                        }
                        mMouseControl.moveCursor(0, stepLength);
                        lastdowntime1 = lastdowntime2;
                        lastdowntime2 = nowdowntime;
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        mMouseControl.moveCursor(0 - stepLength, 0);
                        lastdowntime1 = lastdowntime2;
                        lastdowntime2 = nowdowntime;
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        mMouseControl.moveCursor(stepLength, 0);
                        lastdowntime1 = lastdowntime2;
                        lastdowntime2 = nowdowntime;
                        break;
                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                        mMouseControl.mouseLeftClick();
                        break;

                    default:
                        break;
                }
                return true;
            }
        }
        return false;
    }

    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        if (mCurrentWebView == null) {
            return false;
        }
        // w.setVirturlMouseMotionEvent(mVMouseEvent);
        mCurrentCursorX = ev.getX();
        mCurrentCursorY = ev.getY();
        return false;
    }

    /**
     * search url or keyword.
     * @param url
     */
    public void startSearch(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (handleWebSearchRequest(url, null, null)) {
            return;
        }
        loadUrlFromContext(url);
    }

    public void onNewIntent(Intent intent) {
        final String action = intent.getAction();
        final int flags = intent.getFlags();
        if (Intent.ACTION_MAIN.equals(action) ||
                (flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
            // just resume the browser
            return;
        }

        // In case the SearchDialog is open.
        ((SearchManager) mActivity.getSystemService(Context.SEARCH_SERVICE))
                .stopSearch();
        if (Intent.ACTION_VIEW.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)
                || Intent.ACTION_SEARCH.equals(action)
                || MediaStore.INTENT_ACTION_MEDIA_SEARCH.equals(action)
                || Intent.ACTION_WEB_SEARCH.equals(action)) {
            mActivity.mLogic.showWeb();
            // If this was a search request (e.g. search query directly typed into the address bar),
            // pass it on to the default web search provider.
            if (handleWebSearchIntent(intent)) {
                return;
            }

            UrlData urlData = IntentHandler.getUrlDataFromIntent(intent);
            if (urlData.isEmpty()) {
                urlData = new UrlData(BLANK_URL);
            }

            final String appId = intent
                    .getStringExtra(Browser.EXTRA_APPLICATION_ID);
            if (!TextUtils.isEmpty(urlData.mUrl) &&
                    urlData.mUrl.startsWith("javascript:")) {
                // Always open javascript: URIs in new tabs
                Log.d(TAG, "url=" + urlData.mUrl + " ,header=" + urlData.mHeaders);
                updateTitleView(null, urlData.mUrl);
                if (mInLoad) {
                    mCurrentWebView.stopLoading();
                }
                mCurrentWebView.loadUrl(urlData.mUrl, urlData.mHeaders);
                return;
            }
            if (Intent.ACTION_VIEW.equals(action) 
                    && (appId != null) 
                    && appId.startsWith(mActivity.getPackageName())) {
                Log.d(TAG, "url=" + urlData.mUrl + " ,header=" + urlData.mHeaders);
                updateTitleView(null, urlData.mUrl);
                if (mInLoad) {
                    mCurrentWebView.stopLoading();
                }
                mCurrentWebView.loadUrl(urlData.mUrl, urlData.mHeaders);
                return;
            }
            if (Intent.ACTION_VIEW.equals(action)
                     && !mActivity.getPackageName().equals(appId)) {
                Log.d(TAG, "url=" + urlData.mUrl + " ,header=" + urlData.mHeaders);
                if (!urlData.isPreloaded()) {
                    updateTitleView(null, urlData.mUrl);
                    if (mInLoad) {
                        mCurrentWebView.stopLoading();
                    }
                    mCurrentWebView.loadUrl(urlData.mUrl, urlData.mHeaders);
                }
                return;
            } else {
                Log.d(TAG, "url=" + urlData.mUrl + " ,header=" + urlData.mHeaders);
                updateTitleView(null, urlData.mUrl);
                if (mInLoad) {
                    mCurrentWebView.stopLoading();
                }
                mCurrentWebView.loadUrl(urlData.mUrl, urlData.mHeaders);
                return;
            }
        }
    }

    public void handleWebUrlData(UrlData urlData) {
        updateTitleView(null, urlData.mUrl);
        if (mInLoad) {
            mCurrentWebView.stopLoading();
        }
        mCurrentWebView.loadUrl(urlData.mUrl, urlData.mHeaders);
    }
    private boolean handleWebSearchRequest(String inUrl, Bundle appData, String extraData) {
        if (inUrl == null) return false;
        // URLs are handled by the regular flow of control, so
        // return early.
        String url = UrlUtils.fixUrl(inUrl).trim();
        if (TextUtils.isEmpty(url)) return false;

        if (Patterns.WEB_URL.matcher(url).matches() || UrlUtils.ACCEPTED_URI_SCHEMA.matcher(url).matches()) {
            return false;
        }

        final ContentResolver cr = mActivity.getContentResolver();
        final String newUrl = url;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... unused) {
                Browser.addSearchUrl(cr, newUrl);
                return null;
            }
        }.execute();

        SearchEngine searchEngine = BrowserSettings.getInstance(mActivity).getSearchEngine();
        if (searchEngine == null)
            return false;
        searchEngine.startSearch(mActivity, url, appData, extraData);

        return true;
    }

    public boolean handleWebSearchIntent(Intent intent) {
        if (intent == null) return false;

        String url = null;
        final String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri data = intent.getData();
            if (data != null) url = data.toString();
        } else if (Intent.ACTION_SEARCH.equals(action)
                || MediaStore.INTENT_ACTION_MEDIA_SEARCH.equals(action)
                || Intent.ACTION_WEB_SEARCH.equals(action)) {
            url = intent.getStringExtra(SearchManager.QUERY);
        }
        return handleWebSearchRequest(url,
                intent.getBundleExtra(SearchManager.APP_DATA),
                intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
    }

    public LinkedList<ScreenShot> getWebViewsList() {
        return mWebViewsList;
    }

    public class ScreenShot {
        public WebView mWebView;

        /**
         * name the thumbnail with time.
         */
        public String mTime;

        /**
         * view in pagesLayout
         */
        public View mView;

        /**
         * the thumbnail
         */
        public Bitmap mBitmap;

        public ScreenShot(WebView webView, String time, View view) {
            mWebView = webView;
            mTime = time;
            mView = view;
        }
    }
    
    private class DownloadInfo {
        public WebView mWebView;
        public String mRefererUrl;
        public String mUrl;
        public String mUserAgent;
        public String mContentDisposition;
        public String mMimetype;
        public long mContentLength;
    }

    public void downloadStart(WebView webView, String url, String userAgent, String contentDisposition, String mimetype, String referer,
            long contentLength) {
            Log.i(TAG, " onDownloadStart  url:"+url+"  mimmetype:"+mimetype+"  contentDisposition:"+contentDisposition);
            DownloadInfo downloadinfo = new DownloadInfo();
            downloadinfo.mWebView = webView;
            downloadinfo.mRefererUrl = referer;
            downloadinfo.mUrl = url;
            downloadinfo.mUserAgent = userAgent;
            downloadinfo.mContentDisposition = contentDisposition;
            downloadinfo.mMimetype = mimetype;
            downloadinfo.mContentLength = contentLength;
            if (url.toLowerCase().endsWith("m3u8")) {
                String urlData = "<video src="+url+"></video>";
                webView.loadData(urlData,"text/html","utf-8");
            }
            if (mimetype.equals("video/mp4") ||
                mimetype.equals("application/octet-stream")) {
                ShowConfirm(R.string.sure_download, url, downloadinfo, true);
            }
            else {
                ShowConfirm(R.string.sure_download, url, downloadinfo, false);
            }

    }
    
    private boolean ShowConfirm(int titleId, String message, final Object obj, boolean  isVideo) {
        Log.i(TAG, "ShowConfirm  isvideo:"+isVideo);
        Builder dialog = new AlertDialog.Builder(mActivity);
        dialog.setTitle(titleId);
        dialog.setMessage(message);
        dialog.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                mHandler.sendMessage(mHandler.obtainMessage(START_DOWNLOAD_FILE, 0, 0, obj));
             }
        });

        if (isVideo) {
            dialog.setNegativeButton(R.string.sure_play, new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    mHandler.sendMessage(mHandler.obtainMessage(START_PLAY_FILE, 0, 0, obj));
                }
            });
        }

        dialog.setNeutralButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //nothing to do
            }
        });

        dialog.setCancelable(false);
        dialog.create();
        dialog.show();
        return true;
    }
    
    private void doDownloading(WebView webView, String url, String userAgent,
            String contentDisposition, String mimetype, String referer,
            long contentLength) {
        DownloadHandler.onDownloadStart(mActivity, url, userAgent,
                contentDisposition, mimetype, referer, webView.isPrivateBrowsingEnabled());
    }
    
    private void doPlayFile(WebView webView, String url, String userAgent,
            String contentDisposition, String mimetype, String referer,
            long contentLength) {
        String  videoscript = "javascript:"+
                    "var mstar_private_video = document.createElement(\"video\");  "+
                    "mstar_private_video.src = \"" + url+"\";  "+
                    "mstar_private_video.id =\"mstar_private_emb_iosplayer\"; "+
                    "mstar_private_video.autoplay = \"true\"; "+
                    "mstar_private_video.controller = \"true\";  "+
                    "mstar_private_video.autoplay = \"1\";  "+
                    "mstar_private_video.autobuffer = \"true\";  "+
                    "mstar_private_video.controls = \"1\";  "+
                    "mstar_private_video.width = \"100%\"; "+
                    "mstar_private_video.height = \"100%\"; "+
                    "var object = document.body.appendChild(mstar_private_video);";
        Log.i(TAG, "--->doPlayFile :"+url);
        webView.loadUrl(videoscript);
    }

    synchronized private void updateTitleView(String title, String url) {
        title = (title == null ? "" : title);
        url = (url == null ? "" : url);
        if (title.length() > 20) {
            title = title.substring(0, 20) + "...";
        }
        if (url.length() > 40) {
            url = url.substring(0, 40) + "...";
        }
        final String subTitle = title;
        final String subUrl = url;
        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mActivity.mHolder.mWebTitleView.setText(subTitle + "  " + subUrl);
            }
        });
    }

    public void onPause() {
        if (mCurrentWebView != null && !TextUtils.isEmpty(mCurrentWebView.getUrl())) {
            mCurrentWebView.onPause();
            mCurrentWebView.reload();
        }
    }
}
