
package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.settings.R;

public class TextSwitchWidget extends LinearLayout {
    private Context mContext;

    private TextView mTextView;

    private ImageView mImageView;

    private SwitchChangeListener mSwitchChangedListener;

    private boolean bOn = false;

    public TextSwitchWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initUI();
    }

    private void initUI() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contentView = inflater.inflate(R.layout.widget_text_switcher, this, true);
        mTextView = (TextView) contentView.findViewById(R.id.text_view);
        mImageView = (ImageView) contentView.findViewById(R.id.image_view);
    }

    public boolean getStatus() {
        return bOn;
    }

    public void setStatus(boolean status) {
        if (status) {
            mImageView.setImageResource(R.drawable.check_on);
            bOn = true;
        } else {
            mImageView.setImageResource(R.drawable.check_off);
            bOn = false;
        }
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setSwitchChangeListener(SwitchChangeListener switchChangeListener) {
        mSwitchChangedListener = switchChangeListener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (bOn) {
                mSwitchChangedListener.onSwitchChanged(false);
            } else {
                mSwitchChangedListener.onSwitchChanged(true);
            }
//            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setOnClickListener(OnClickListener listener){
        this.mImageView.setOnClickListener(listener);
    }
}
