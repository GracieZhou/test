
package com.android.settings.widget;

import com.android.settings.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextTextWidget extends LinearLayout {
    private Context mContext;

    private TextView mLeftTextView;

    private TextView mRightTextView;

    private int mLeftColor;

    private int mRightColor;

    private int mFocusLeftColor;

    private int mFocusRightColor;

    public TextTextWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initUI();
    }

    private void initUI() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contentView = inflater.inflate(R.layout.widget_text_text, this, true);
        mLeftTextView = (TextView) contentView.findViewById(R.id.left_textview);
        mRightTextView = (TextView) contentView.findViewById(R.id.right_textview);
    }

    public void setText(CharSequence leftText, CharSequence rightText) {
        mLeftTextView.setText(leftText);
        mRightTextView.setText(rightText);
    }

    public void setTextColor(int leftColor, int rightColor) {
        mLeftColor = leftColor;
        mRightColor = rightColor;
    }

    public void setFocusTextColor(int leftColor, int rightColor) {
        mFocusLeftColor = leftColor;
        mFocusRightColor = rightColor;
    }

    public void setLeftTextViewGravity(int gravity) {
        mLeftTextView.setGravity(gravity);
    }

    public void setRightTextViewGravity(int gravity) {
        mRightTextView.setGravity(gravity);
    }

    public TextView getLeftTextView() {
        return mLeftTextView;
    }

    public TextView getRightTextView() {
        return mRightTextView;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        // TODO Auto-generated method stub
        if (gainFocus == true) {
            mLeftTextView.setTextColor(mFocusLeftColor);
            mRightTextView.setTextColor(mFocusRightColor);
        } else {
            mLeftTextView.setTextColor(mLeftColor);
            mRightTextView.setTextColor(mRightColor);
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

}
