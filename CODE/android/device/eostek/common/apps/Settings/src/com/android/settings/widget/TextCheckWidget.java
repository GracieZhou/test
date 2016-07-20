
package com.android.settings.widget;

import com.android.settings.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextCheckWidget extends LinearLayout {
    private Context mContext;

    private ImageView mCheckedImageView;

    private TextView mTextView;

    private boolean bChecked;

    public TextCheckWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initUI(context);
    }

    private void initUI(Context context) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contentView = inflater.inflate(R.layout.widget_text_checked, this, true);
        mTextView = (TextView) contentView.findViewById(R.id.text_view);
        mCheckedImageView = (ImageView) contentView.findViewById(R.id.radio_view);
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setChecked(boolean isChecked) {
        bChecked = isChecked;
        if (isChecked) {
            mCheckedImageView.setImageResource(R.drawable.radio_checked);
        } else {
            mCheckedImageView.setImageResource(R.drawable.radio_checked_disable);
        }
    }

    public boolean getCheckedStatus() {
        return bChecked;
    }
}
