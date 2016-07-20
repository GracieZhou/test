
package com.android.settings.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.settings.R;

/**
 * The common title layout
 * 
 * @author billy.liu
 */
public class TitleWidget extends LinearLayout {
    public static final String TAG = "Settings";

    private Context mContext;

    private TextView mMainTitleTextView;

    private TextView mFirstTitleSymbolTextView;

    private TextView mSecondTitleSymbolTextView;

    private TextView mFirstSubTitleTextView;

    private TextView mSecondSubTitleTextView;

    private TextView mThirdSubTitleTextView;

    public TitleWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        findviews();
    }

    private void findviews() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contentView = inflater.inflate(R.layout.widget_title, this, true);
        mMainTitleTextView = (TextView) contentView.findViewById(R.id.main_title);
        mMainTitleTextView.setText(mContext.getResources().getString(R.string.setting));
        mFirstTitleSymbolTextView = (TextView) contentView.findViewById(R.id.title_symbol_first);
        mSecondTitleSymbolTextView = (TextView) contentView.findViewById(R.id.title_symbol_second);
        mFirstSubTitleTextView = (TextView) contentView.findViewById(R.id.sub_title_first);
        mSecondSubTitleTextView = (TextView) contentView.findViewById(R.id.sub_title_second);
        mThirdSubTitleTextView = (TextView) contentView.findViewById(R.id.sub_title_third);
    }

    /**
     * Set the title name , if it has only one title name
     * 
     * @param firstSubTitle
     */
    public void setSubTitleText(CharSequence firstSubTitle) {
        mFirstSubTitleTextView.setText(firstSubTitle);
    }

    /**
     * Set the title name , if it have tow title names
     * 
     * @param firstSubTitle
     */
    public void setSubTitleText(CharSequence firstSubTitle, CharSequence secondSubTitle) {
        mFirstSubTitleTextView.setText(firstSubTitle);
        if (TextUtils.isEmpty(secondSubTitle)) {
            mFirstTitleSymbolTextView.setVisibility(View.INVISIBLE);
        } else {
            mFirstTitleSymbolTextView.setVisibility(View.VISIBLE);
        }
        mSecondSubTitleTextView.setText(secondSubTitle);
    }

    /**
     * Set the title name , if it have three title names
     * 
     * @param firstSubTitle
     */
    public void setSubTitleText(CharSequence firstSubTitle, CharSequence secondSubTitle, CharSequence thirdSubTitle) {
        mFirstSubTitleTextView.setText(firstSubTitle);
        mFirstTitleSymbolTextView.setVisibility(View.VISIBLE);
        mSecondSubTitleTextView.setText(secondSubTitle);
        mSecondTitleSymbolTextView.setVisibility(View.VISIBLE);
        mThirdSubTitleTextView.setText(thirdSubTitle);
    }

}
