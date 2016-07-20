
package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R;

public class TextSelectorWidget extends LinearLayout {
    private Context mContext;

    private TextView mTextView;

    private SelectorWidget mSelectorWidget;

    public TextSelectorWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initUI();
    }

    private void initUI() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contentView = inflater.inflate(R.layout.widget_text_selector, this, true);
        mTextView = (TextView) contentView.findViewById(R.id.selector_text);
        mSelectorWidget = (SelectorWidget) contentView.findViewById(R.id.seletor_widget);
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setSelectorText(CharSequence text) {
        mSelectorWidget.setText(text);
    }

    public void setValue(int initValue, int minValue, int maxValue) {
        mSelectorWidget.setValue(initValue);
        mSelectorWidget.setMinValue(minValue);
        mSelectorWidget.setMaxValue(maxValue);
    }

    public int getValue() {
        return mSelectorWidget.getValue();
    }

    public void setValueChangeListener(ValueChangeListener valueChangeListener) {
        mSelectorWidget.setValueChangeListener(valueChangeListener);
    }

    public void setOnSelectWidgetClickedListener(OnClickListener lListener,OnClickListener rListener){
        mSelectorWidget.setOnClickListener(lListener, rListener);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        mSelectorWidget.onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        mSelectorWidget.onKeyUp(keyCode, event);
        return super.onKeyUp(keyCode, event);
    }

    public SelectorWidget getSelectorWidget() {
        return mSelectorWidget;
    }

    public TextView getTextView() {
        return mTextView;
    }

}
