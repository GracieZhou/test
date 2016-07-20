
package scifly.provider.metadata;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.eostek.scifly.messagecenter.R;

/**
 * Activity for showing WebView.
 * 
 * @author Charles.tai
 * @date 2014-12-24
 * @category Merry Christmas Eve
 */
public class WebViewActivity extends Activity {

    private static final String TAG = MsgService.TAG;

    private WebView mWebView;

    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle bundle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        setContentView(R.layout.cmd_webview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        Intent intent = getIntent();
        String url = intent.getStringExtra("URL");
        Log.d(TAG, "url from getIntent : " + url);
        show(url);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            finish();
            mWebView.loadUrl("about:blank");
            mWebView.stopLoading();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mWebView != null) {
//            mWebView.loadUrl("about:blank");
//            mWebView.stopLoading();
//
//        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void show(String url) {
        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        mWebView.loadUrl(url);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "----onPageStarted----" + " url : " + url + " favicon : " + favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "----onPageFinished----" + " url : " + url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                Log.d(TAG, "----onReceivedSslError----" + " SslErrorHandler : " + handler + " SslError " + error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.d(TAG, "----onReceivedError----" + " errorCode : " + errorCode + " description " + description
                        + " failingUrl" + failingUrl);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d(TAG, " newProgress : " + newProgress);
                if (newProgress == 0) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}
