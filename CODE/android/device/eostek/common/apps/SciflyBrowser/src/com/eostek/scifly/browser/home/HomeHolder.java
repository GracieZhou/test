package com.eostek.scifly.browser.home;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.eostek.scifly.browser.BrowserActivity;
import com.eostek.scifly.browser.R;
import com.eostek.scifly.browser.business.WebViewHelper;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayAdapterView.OnItemClickListener;
import com.jess.ui.TwoWayGridView;

/**
 * projectName： Browser moduleName： HomeHolder.java
 * 
 * @author Shirley.jiang & Ahri.chen
 * @time 2016-1-27 
 */
public class HomeHolder {

    private final String TAG = "HomeHolder";

    private BrowserActivity mActivity;

    private HomeLogic mLogic;

    private EditText mSearchInput;

    private Button mSearchBtn;

    private TwoWayGridView mGridView;

    public HomeHolder (BrowserActivity activity) {
        mActivity = activity;
    }

    public void setLogic(HomeLogic logic) {
        mLogic = logic;
    }

    public TwoWayGridView getGridView () {
        return mGridView;
    }

    /**
     * init view.
     * @param view
     */
    public void findView(View view) {
        mSearchInput = (EditText)view.findViewById(R.id.search_input);
        mSearchBtn = (Button)view.findViewById(R.id.search_btn);
        mGridView = (TwoWayGridView)view.findViewById(R.id.gridview);
        mGridView.setSmoothScrollbarEnabled(true);
        mGridView.setSelector(R.drawable.transparent_selector);
    }

    /**
     * setListener.
     */
    public void setListener() {
        mSearchBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
                mLogic.closeIME();

                String url = mSearchInput.getText().toString();
                mActivity.mLogic.showWeb();
                Log.d(TAG, "url=" + url);
                WebViewHelper.getInstance(mActivity).startSearch(url);
            }
        });

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
                mActivity.mLogic.showWeb();
                WebViewHelper.getInstance(mActivity).loadUrlFromContext(mLogic.getList().get(position).mUrl);
            }
        });

        mSearchInput.setOnEditorActionListener(new OnEditorActionListener() {
            
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.i(TAG, "" + actionId);
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_SEND) {
                    mLogic.closeIME();

                    // start to search
                    String url = mSearchInput.getText().toString();
                    mActivity.mLogic.showWeb();
                    Log.d(TAG, "url=" + url);
                    WebViewHelper.getInstance(mActivity).startSearch(url);
                    return true;
                } else {
                    return false;
                }
            };

        });
    }
}
