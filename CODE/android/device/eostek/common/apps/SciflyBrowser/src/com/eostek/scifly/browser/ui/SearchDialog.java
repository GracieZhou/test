package com.eostek.scifly.browser.ui;

import com.eostek.scifly.browser.BrowserActivity;
import com.eostek.scifly.browser.R;
import com.eostek.scifly.browser.business.WebViewHelper;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class SearchDialog extends Dialog{

    private BrowserActivity mActivity;

    private WebViewHelper mHelper;

    private EditText mEditText;

    private Button mButton;

    public SearchDialog(BrowserActivity activity) {
        super(activity, android.R.style.Theme_Translucent);
        mActivity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setContentView(R.layout.search_dialog_layout);
        
        mEditText = (EditText) window.findViewById(R.id.search_input);
        mButton = (Button) window.findViewById(R.id.search_btn);

        mHelper = WebViewHelper.getInstance(mActivity);

        setListener();
    }

    private void setListener() {
        mButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (mActivity.mLogic.isShowHomeLayout()) {
                    mActivity.mLogic.showWeb();
                }
                mHelper.startSearch(mEditText.getText().toString());
                SearchDialog.this.dismiss();
            }
        });
    }
}
