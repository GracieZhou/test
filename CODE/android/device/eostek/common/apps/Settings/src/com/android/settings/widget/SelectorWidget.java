
package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R;

public class SelectorWidget extends LinearLayout {
    private Context mContext;

    private static final int VALUE_PLUS = 0;

    private static final int VALUE_MINUS = 1;

    private ImageButton mMinusImg;

    private ImageButton mPlusImg;

    private TextView mSelectorTextView;

    private ValueChangeListener mValueChangedListener;

    private int mMaxValue;

    private int mMinValue;

    private int mValue;

    public SelectorWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initUI();
    }

    private void initUI() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contentView = inflater.inflate(R.layout.widget_selector, this, true);
        mPlusImg = (ImageButton) contentView.findViewById(R.id.plus);
        mSelectorTextView = (TextView) contentView.findViewById(R.id.selector);
        mMinusImg = (ImageButton) contentView.findViewById(R.id.minus);
    }

    public void setValueChangeListener(ValueChangeListener valueChangeListener) {
        this.mValueChangedListener = valueChangeListener;
    }

    public void setOnClickListener(OnClickListener lListener, OnClickListener rListener) {
        mMinusImg.setOnClickListener(lListener);
        mPlusImg.setOnClickListener(rListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                mMinusImg.setImageResource(R.drawable.arrow_left_pressed);
                calculateValue(VALUE_MINUS);
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mPlusImg.setImageResource(R.drawable.arrow_right_pressed);
                calculateValue(VALUE_PLUS);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                mMinusImg.setImageResource(R.drawable.arrow_left_selector);
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mPlusImg.setImageResource(R.drawable.arrow_right_selector);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public void setText(CharSequence text) {
        mSelectorTextView.setText(text);
    }

    public void calculateValue(int direction) {
        if (direction == VALUE_PLUS) {
            mValue = mValue + 1;
            if (mValue > mMaxValue) {
                mValue = mMinValue;
            }
            mValueChangedListener.onValueChanged(mValue);

        } else if (direction == VALUE_MINUS) {
            mValue = mValue - 1;
            if (mValue < mMinValue) {
                mValue = mMaxValue;
            }
            mValueChangedListener.onValueChanged(mValue);

        }
    }

    public void setValue(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    public void setMaxValue(int max) {
        mMaxValue = max;
    }

    public void setMinValue(int min) {
        mMinValue = min;
    }

    public TextView getTextView() {
        return mSelectorTextView;
    }
}
