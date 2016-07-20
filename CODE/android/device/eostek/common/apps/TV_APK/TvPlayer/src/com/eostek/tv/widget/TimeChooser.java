
package com.eostek.tv.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.eostek.tv.R;

public class TimeChooser extends EditText implements TextWatcher {

    final private int hour = 0;

    final private int minute = 1;

    final private int second = 2;

    private boolean isEditBegin = false;

    private int maxHourTime = 50;

    private int currentType = -1;

    private int timeChooseHour = 0;

    private int timeChooseMinute = 0;

    private int timeChooseSecond = 0;

    public TimeChooser(Context context) {
        super(context);
    }

    public TimeChooser(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeChooser);
        currentType = a.getInt(R.styleable.TimeChooser_chooseType, hour);
        if (currentType == hour) {
            maxHourTime = a.getInt(R.styleable.TimeChooser_hourLimit, 50);
        }
        a.recycle();
        this.addTextChangedListener(this);
        this.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) { // edit
                                                                     // begin
                    isEditBegin = true;
                } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editDone();
                }
                return false;
            }
        });
    }

    public TimeChooser(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void editDone() {
        if (!isEditBegin)
            return;
        isEditBegin = false;
        try {
            int editableNum = Integer.parseInt(this.getText().toString());
            if (editableNum < 10) {
                this.setText("0" + editableNum);
                this.setSelection(2);
            } else {
                String text = "" + editableNum;
                this.setText(text);
                this.setSelection(text.length());
            }
        } catch (NumberFormatException e) {
        }
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            editDone();
        }
        return super.dispatchKeyEventPreIme(event);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            int editableNum = Integer.parseInt(s.toString());
            switch (currentType) {
                case hour:
                    if (editableNum >= maxHourTime) {
                        String curHour = maxHourTime - 1 + "";
                        this.setText(curHour);
                        this.setSelection(curHour.length());
                        timeChooseHour = maxHourTime - 1;
                    } else {
                        timeChooseHour = editableNum;
                    }
                    break;
                case minute:
                    if (editableNum >= 60) {
                        this.setText("59");
                        this.setSelection(2);
                        timeChooseMinute = 59;
                    } else {
                        timeChooseMinute = editableNum;
                    }
                    break;
                case second:
                    if (editableNum >= 60) {
                        this.setText("59");
                        this.setSelection(2);
                        timeChooseSecond = 59;
                    } else {
                        timeChooseSecond = editableNum;
                    }
                    break;
            }
        } catch (NumberFormatException e) {
            switch (currentType) {
                case hour:
                    timeChooseHour = 0;
                    break;
                case minute:
                    timeChooseMinute = 0;
                    break;
                case second:
                    timeChooseSecond = 0;
                    break;
            }
            return;
        }
    }

    public int getValue() {
        switch (currentType) {
            case hour:
                return timeChooseHour;
            case minute:
                return timeChooseMinute;
            case second:
                return timeChooseSecond;
        }
        return 0;
    }
}
