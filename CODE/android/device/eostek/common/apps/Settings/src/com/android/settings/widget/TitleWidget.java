
package com.android.settings.widget;

import com.android.settings.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TitleWidget extends LinearLayout {
    public static final String LOG_TAG = "Settings";

    private Context mContext;

    private TextView mMainTitleTextView;

    private TextView mTitleSymbolTextView;

    private TextView mFirstSubTitleTextView;

    private TextView mSecondSubTitleTextView;

    public TitleWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initUI();
        Log.d(LOG_TAG, "Name : Settings, Version : 1.0.1, Date : 2015-4-21, Publisher : andy.jiang, Revision : 30608");
    }

    private void initUI() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contentView = inflater.inflate(R.layout.widget_title, this, true);
        mMainTitleTextView = (TextView) contentView.findViewById(R.id.main_title);
        mTitleSymbolTextView = (TextView) contentView.findViewById(R.id.title_symbol);
        mFirstSubTitleTextView = (TextView) contentView.findViewById(R.id.sub_title_first);
        mSecondSubTitleTextView = (TextView) contentView.findViewById(R.id.sub_title_second);
    }

    public void setMainTitleText(CharSequence mainTitle) {
        mMainTitleTextView.setText(mainTitle);
    }

    public void setFirstSubTitleText(CharSequence firstSubTitle, boolean bhasSecondTitle) {
        if (!bhasSecondTitle) {
            mTitleSymbolTextView.setText(mContext.getString(R.string.title_symbol));
        }
        mFirstSubTitleTextView.setText(firstSubTitle);
    }

    public void setSecondSubTitleText(CharSequence secondSubTitle) {
        mSecondSubTitleTextView.setText(secondSubTitle);
    }

    public TextView getMainTitleTextView() {
        return mMainTitleTextView;
    }

    public TextView getFirstTitleTextView() {
        return mFirstSubTitleTextView;
    }

    public TextView getSecondTitleTextView() {
        return mSecondSubTitleTextView;
    }
}
