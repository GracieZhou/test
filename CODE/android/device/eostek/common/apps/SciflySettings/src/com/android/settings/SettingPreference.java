
package com.android.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * A Preference extends {@link Preference}. <br/>
 * There are three types of this preference which are specified by
 * {@link #SINGLE_COLUMN}, {@link #TWO_COLUMN},
 * {@link #TWO_COLUMN_WITH_SELECTOR}. It can be set by {@link #setType(int)} or
 * {@code app:type} attribute in xml, for the second method, you must add
 * {@code xmlns:app="http://schemas.android.com/apk/res/com.android.settings"}
 * at the root element of the xml file.
 * 
 * @author Davis
 * @Date 2015-8-12
 */
public class SettingPreference extends Preference {

    private static final String TAG = SettingPreference.class.getSimpleName();

    /**
     * Preference with only one column, can be set by
     * {@code app:type="single_column"} attribute in xml file.
     */
    public static final int SINGLE_COLUMN = 1;

    /**
     * Preference with two columns, can be set by {@code app:type="two_column"}
     * attribute in xml file.
     */
    public static final int TWO_COLUMN = 2;

    /**
     * Preference with two columns , can be set by
     * {@code app:type="two_column_with_selector"} attribute in xml file.
     */
    public static final int TWO_COLUMN_WITH_SELECTOR = 3;

    /**
     * Preference with two columns , can be set by
     * {@code app:type="single_column_with_button"} attribute in xml file.
     */
    public static final int SINGLE_COLUMN_WITH_BUTTON = 4;

    private Context mContext;

    private int mType;

    private List<CharSequence> mValueList;

    private TextView mRightTextView;

    private ToggleButton mToggleBtn;

    /**
     *  the switch state of toggleBtn.
     */
    private boolean checkState;

    /**
     * rightText whether turn to gray.
     */
    private boolean rightTextState;

    public SettingPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        mContext = context;
        mValueList = new ArrayList<CharSequence>();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingPreference, defStyle, 0);

        // get the app:type and app:valueArray attributes from the xml file.
        mType = a.getInt(R.styleable.SettingPreference_type, TWO_COLUMN);
        CharSequence[] cs = a.getTextArray(R.styleable.SettingPreference_valueArray);
        if (cs != null) {
            mValueList = Arrays.asList(cs);
        }
        a.recycle();
    }

    public SettingPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    public View onCreateView(ViewGroup parent) {

        final LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View layout = layoutInflater.inflate(R.layout.setting_preference_layout, parent, false);

        return layout;
    }

    @SuppressLint("CutPasteId")
    @Override
    public void onBindView(View view) {
        final TextView titleView = (TextView) view.findViewById(android.R.id.title);
        if (titleView != null) {
            final CharSequence title = getTitle();
            if (!TextUtils.isEmpty(title)) {
                titleView.setText(title);
                titleView.setVisibility(View.VISIBLE);
            } else {
                titleView.setVisibility(View.GONE);
            }
        }

        // A little differet from parent, let the summary view show the default
        // value.
        final TextView rightView = (TextView) view.findViewById(android.R.id.summary);
        mRightTextView = (TextView) view.findViewById(android.R.id.summary);
        if (rightView != null) {
            if ("IP_address".equals(getKey()) || "wifi_mask".equals(getKey()) || "wifi_gateway".equals(getKey())
                    || "wifi_dns".equals(getKey())) {
                mRightTextView.setTextColor(rightTextState ? mContext.getResources().getColor(
                        android.R.color.darker_gray) : mContext.getResources().getColor(android.R.color.white));
            }
            if ("wired".equals(getKey()) || "wifi".equals(getKey())||"PPPoE".equals(getKey())) {
                mRightTextView.setTextColor(rightTextState ? mContext.getResources().getColor(R.color.green) : mContext
                        .getResources().getColor(android.R.color.holo_red_light));
            }
            if (mType != TWO_COLUMN_WITH_SELECTOR) {
                CharSequence defaultValue = getDefaultValue();
                // if users do not set a defaultValue, but has a valueArray, we
                // give
                // the first value of vauleArray to the defaultVaule.
                if (TextUtils.isEmpty(defaultValue) && mValueList.size() > 0) {
                    defaultValue = mValueList.get(0);
                    setDefaultValue(defaultValue, false);
                }
                if (!TextUtils.isEmpty(defaultValue)) {
                    rightView.setText(defaultValue);
                    rightView.setVisibility(View.VISIBLE);
                } else {
                    rightView.setVisibility(View.GONE);
                }
            } else {
                int index = getDefaultValueIndex();
                if (mValueList != null && mValueList.size() > 0 && index < mValueList.size()) {
                    if (index == -1) {
                        setDefaultValue(mValueList.get(0), false);
                    } else {
                        rightView.setText(mValueList.get(index));
                        rightView.setVisibility(View.VISIBLE);
                    }
                } else {
                    rightView.setVisibility(View.GONE);
                }
            }
        }

        final ImageButton leftImage = (ImageButton) view.findViewById(R.id.left_arrow);
        final ImageButton rightImage = (ImageButton) view.findViewById(R.id.right_arrow);
        final ToggleButton toggleBtn = (ToggleButton) view.findViewById(R.id.toggle_button);
        final LinearLayout rightLayout = (LinearLayout) view.findViewById(R.id.rightLayout);

        mToggleBtn = (ToggleButton) view.findViewById(R.id.toggle_button);
        switch (mType) {
            case SINGLE_COLUMN:
                LinearLayout.LayoutParams lp = (LayoutParams) titleView.getLayoutParams();
                lp.setMargins(0, 0, 0, 0);
                titleView.setLayoutParams(lp);
                titleView.setGravity(Gravity.CENTER);
                rightLayout.setVisibility(View.GONE);
                toggleBtn.setVisibility(View.GONE);
                break;

            case TWO_COLUMN:
                leftImage.setVisibility(View.GONE);
                rightImage.setVisibility(View.GONE);
                toggleBtn.setVisibility(View.GONE);
                break;

            case TWO_COLUMN_WITH_SELECTOR:
                toggleBtn.setVisibility(View.GONE);
                leftImage.setVisibility(View.VISIBLE);
                leftImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        setRightText(findPreviousValue());
                    }
                });

                rightImage.setVisibility(View.VISIBLE);
                rightImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        setRightText(findNextValue());
                    }
                });
                break;

            case SINGLE_COLUMN_WITH_BUTTON:
                /*
                 * LinearLayout.LayoutParams lp1 = (LayoutParams)
                 * titleView.getLayoutParams(); lp1.setMargins(0, 0, 0, 0);
                 * titleView.setLayoutParams(lp1);
                 */
                leftImage.setVisibility(View.GONE);
                rightImage.setVisibility(View.GONE);
                titleView.setGravity(Gravity.CENTER | Gravity.RIGHT);
                toggleBtn.setVisibility(View.VISIBLE);
                toggleBtn.setChecked(checkState);
                toggleBtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (toggleBtn.isChecked()) {
                            toggleBtn.setChecked(false);
                        } else {
                            toggleBtn.setChecked(true);
                        }
                        SettingPreference.this.getOnPreferenceClickListener().onPreferenceClick(SettingPreference.this);
                    }
                });
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {

            if (mType == TWO_COLUMN_WITH_SELECTOR) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        setRightText(findPreviousValue());
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        setRightText(findNextValue());
                        break;
                    default:
                        break;
                }
            }
        }
        return super.onKey(v, keyCode, event);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (mRightTextView != null) {
            mRightTextView.setEnabled(enabled);
        }
    }

    /**
     * @Title: setRightTextColor.
     * @Description: set the right text color of view.
     * @param: @param state.
     * @return: void.
     * @throws
     */
    public void setRightTextColor(boolean state) {
        this.rightTextState = state;
        if (mRightTextView != null) {
            if ("IP_address".equals(getKey()) || "wifi_mask".equals(getKey()) || "wifi_gateway".equals(getKey())
                    || "wifi_dns".equals(getKey())) {
                mRightTextView.setTextColor(state ? mContext.getResources().getColor(android.R.color.darker_gray)
                        : mContext.getResources().getColor(android.R.color.white));
            } else if ("wired".equals(getKey()) || "wifi".equals(getKey())||"PPPoE".equals(getKey())) {
                mRightTextView.setTextColor(state ? mContext.getResources().getColor(R.color.green) : mContext
                        .getResources().getColor(android.R.color.holo_red_light));
            }
        }
    }

    private String findNextValue() {
        int index = getRightTextIndex();
        if (index == -1) {
            return "";
        }
        if (index == mValueList.size() - 1) {
            return mValueList.get(0).toString();
        }
        return mValueList.get(index + 1).toString();
    }

    private String findPreviousValue() {
        int index = getRightTextIndex();
        if (index == -1) {
            return "";
        }
        if (index == 0) {
            return mValueList.get(mValueList.size() - 1).toString();
        }
        return mValueList.get(index - 1).toString();
    }

    private String getDefaultValue() {
        return getSharedPreferences().getString(getKey(), "");
    }

    private int getDefaultValueIndex() {
        return getSharedPreferences().getInt(getKey(), -1);
    }

    public TextView getRightTextView() {
        return mRightTextView;
    }

    public int getRightTextIndex() {
        return getDefaultValueIndex();
    }

    public void setRightText(String text) {
        if (!TextUtils.isEmpty(text)) {
            setDefaultValue(text);
        }
    }

    public void toggleButton() {
        if (mToggleBtn != null) {
            mToggleBtn.toggle();
            checkState = isChecked();
            notifyChanged();
        }
    }

    public boolean isChecked() {
        return mToggleBtn.isChecked();
    }

    public void setChecked(boolean checked) {
        this.checkState = checked;
        if (mToggleBtn != null) {
            mToggleBtn.setChecked(checked);
            notifyChanged();
        }
    }

    /**
     * @Title: setCheckedChangeListener.
     * @Description: the statu listener of toggleBtn.
     * @param: @param listener.
     * @return: void.
     * @throws
     */
    public void setCheckedChangeListener(OnCheckedChangeListener listener) {
        if (mToggleBtn != null) {
            mToggleBtn.setOnCheckedChangeListener(listener);
        }
    }

    @Override
    public void setDefaultValue(Object value) {
        setDefaultValue(value, true);
    }

    /**
     * set value to the default value and save to the SharedPreference.
     * 
     * @param value the value to set.
     * @param callChangeListener if true, will call onPreferenceChange method of
     *            clients.
     */
    public void setDefaultValue(Object value, boolean callChangeListener) {
        if (value == null) {
            return;
        }
        Log.d(TAG, "setDefaultValue: value==>" + value.toString());
        if (mType != TWO_COLUMN_WITH_SELECTOR) {
            if (!value.equals(getDefaultValue())) {
                super.setDefaultValue(value);
                if (callChangeListener) {
                    // callChangeListener method allows users to handler
                    // onPreferenceChange event before saving the new value.
                    callChangeListener(value);
                }
                persistString(value.toString());
            }
        } else {
            if (getDefaultValueIndex() == -1 || !value.equals(mValueList.get(getDefaultValueIndex()))) {
                super.setDefaultValue(value);
                if (callChangeListener) {
                    // callChangeListener method allows users to handler
                    // onPreferenceChange event before saving the new value.
                    callChangeListener(value);
                }
                setDefaultValueIndex(value.toString());
            }
        }
        notifyChanged();
    }

    /**
     * @Title: setDefaultValueIndex.
     * @Description: the index to save the value of set.
     * @param: .
     * @return: void.
     * @throws
     */
    public void setDefaultValueIndex(String value) {
        if (mValueList != null && mValueList.size() > 0) {
            int index = mValueList.indexOf(value);
            persistInt(index);
        }
    }

    /**
     * Sets the type of preference.
     * 
     * @param type one of the following value: <li>
     *            {@link #SINGLE_COLUMN} <li>
     *            {@link #TWO_COLUMN} <li>
     *            {@link #TWO_COLUMN_WITH_SELECTOR} <li>
     *            {@link #TWO_COLUMN_WITH_SELECTOR}
     */
    public void setType(int type) {
        mType = type;
    }
}
