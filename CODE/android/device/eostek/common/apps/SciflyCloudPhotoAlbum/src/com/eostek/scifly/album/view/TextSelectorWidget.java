
package com.eostek.scifly.album.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eostek.scifly.album.R;

/**
 * @ClassName: TextSelectorWidget.
 * @Description:change the value of the user choose.
 * @author: lucky.li.
 * @date: Sep 21, 2015 4:34:51 PM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class TextSelectorWidget extends LinearLayout implements OnClickListener {
    private Context mContext;

    private TextView mValueView;

    private TextView mItemNameView;

    private String[] mValueItems;

    private ImageButton mLeftArrowBtn;

    private ImageButton mRightArrowBtn;

    private int mCurrentValueIndex = 0;

    private ValueChangeListener mListener;

    public static final int INT_VALUE_TYPE = 0;

    public static final int STRING_VALUE_TYPE = 1;

    public static final int STORAGE_DIRECTORY_FLAG = 2;

    public static final int TIME_SECOND_FLAG = 3;

    private int mType = 1;

    private int minValue;

    private int maxValue;

    private int mFlag = -1;

    private final int MSG = 3;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG) {
                mLeftArrowBtn.setImageResource(R.drawable.arrow_left_white);
                mRightArrowBtn.setImageResource(R.drawable.arrow_right_white);
            }
        };
    };

    public void setFlag(int flag) {
        this.mFlag = flag;
    }

    /**
     * @Title: TextSelectorWidget.
     * @Description: constructor.
     * @param: @param context
     * @param: @param attrs.
     * @throws
     */
    public TextSelectorWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initUI();
    }

    /**
     * @Title: TextSelectorWidget.
     * @Description: constructor.
     * @param: @param context
     * @param: @param attrs
     * @param: @param defStyle.
     * @throws
     */
    public TextSelectorWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initUI();
    }

    /**
     * @Title: TextSelectorWidget.
     * @Description: constructor.
     * @param: @param context.
     * @throws
     */
    public TextSelectorWidget(Context context) {
        super(context);
        this.mContext = context;
        initUI();
    }

    private void initUI() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contentView = inflater.inflate(R.layout.widget_selector_layout, null);
        mItemNameView = (TextView) contentView.findViewById(R.id.item_name);
        mValueView = (TextView) contentView.findViewById(R.id.value);
        mLeftArrowBtn = (ImageButton) contentView.findViewById(R.id.left_arrow);
        mRightArrowBtn = (ImageButton) contentView.findViewById(R.id.right_arrow);
        mLeftArrowBtn.setOnClickListener(this);
        mRightArrowBtn.setOnClickListener(this);
        addView(contentView);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            mItemNameView.setTextColor(getResources().getColor(R.color.text_color));
        } else {
            mItemNameView.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    public void setItemName(String itemName) {
        mItemNameView.setText(itemName);
    }

    /**
     * @return
     * @Title: setValueRange.
     * @Description: setValueRange.
     * @param: @param minValue
     * @param: @param maxValue.
     * @return: void.
     * @throws
     */
    public void setValueRange(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.mType = INT_VALUE_TYPE;
    }

    /**
     * @Title: setValueItems.
     * @Description: setValueItems.
     * @param: @param valueItems.
     * @return: void.
     * @throws
     */
    public void setValueItems(String[] valueItems) {
        this.mValueItems = valueItems;
        this.mType = STRING_VALUE_TYPE;
    }

    public void setCurrentValue(int value) {
        mCurrentValueIndex = value;
        if (mType == STRING_VALUE_TYPE) {
            mValueView.setText(mValueItems[value]);
        } else if (mType == INT_VALUE_TYPE) {
            if (mFlag == TIME_SECOND_FLAG) {
                mValueView.setText(value + " S");
            } else {
                mValueView.setText(value + "");
            }
        }
    }

    public void setValueChangeListener(ValueChangeListener listener) {
        this.mListener = listener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && mFlag != STORAGE_DIRECTORY_FLAG) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                mLeftArrowBtn.setImageResource(R.drawable.arrow_left_green);
                changeValue(mLeftArrowBtn);
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mRightArrowBtn.setImageResource(R.drawable.arrow_right_green);
                changeValue(mRightArrowBtn);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                mLeftArrowBtn.setImageResource(R.drawable.arrow_left_white);
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mRightArrowBtn.setImageResource(R.drawable.arrow_right_white);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void changeValue(View view) {
        switch (view.getId()) {
            case R.id.left_arrow:
                if (mType == INT_VALUE_TYPE) {
                    if (mCurrentValueIndex > minValue) {
                        mCurrentValueIndex--;
                    } else {
                        mCurrentValueIndex = maxValue;
                    }
                } else if (mType == STRING_VALUE_TYPE) {
                    if (mCurrentValueIndex > 0) {
                        mCurrentValueIndex--;
                    } else {
                        mCurrentValueIndex = mValueItems.length - 1;
                    }
                }
                break;
            case R.id.right_arrow:
                if (mType == INT_VALUE_TYPE) {
                    if (mCurrentValueIndex < maxValue) {
                        mCurrentValueIndex++;
                    } else {
                        mCurrentValueIndex = minValue;
                    }
                } else if (mType == STRING_VALUE_TYPE) {
                    if (mCurrentValueIndex < mValueItems.length - 1) {
                        mCurrentValueIndex++;
                    } else {
                        mCurrentValueIndex = 0;
                    }
                }
                break;
            default:
                break;
        }
        setCurrentValue(mCurrentValueIndex);
        mListener.onValueChanged(mCurrentValueIndex);
    }

    @Override
    public void onClick(View v) {
        if (mFlag == STORAGE_DIRECTORY_FLAG) {
            return;
        }
        mHandler.removeMessages(MSG);
        switch (v.getId()) {
            case R.id.left_arrow:
                mLeftArrowBtn.setImageResource(R.drawable.arrow_left_green);
                break;
            case R.id.right_arrow:
                mRightArrowBtn.setImageResource(R.drawable.arrow_right_green);
                break;
            default:
                break;
        }
        mHandler.sendEmptyMessageDelayed(MSG, 500);
        changeValue(v);
    }

}
