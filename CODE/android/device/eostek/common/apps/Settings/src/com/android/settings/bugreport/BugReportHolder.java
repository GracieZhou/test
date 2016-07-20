
package com.android.settings.bugreport;

import scifly.device.Device;
import scifly.util.LogUtils;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.settings.widget.TitleWidget;
import com.android.settings.R;

public class BugReportHolder {
    public static final String TAG = "BugReportHolder";

    private static BugReportActivity mActivity;

    private TitleWidget mMainTitle;

    private TextView mDeviceId;
    
    private TextView mCountTextView;

    private EditText mBugreportEditTextView;

    private Button mSubmitBtn;

    private static String mText = "";
    
    private static final int  LIMIT_NUMBER= 200;

    private static final int TOAST_NETWORK_DISCONNECT = -2;

    private static final int TOAST_UPLOADING = 0;

    private static final int TOAST_UPLOAD_SUCCESS = 1;

    private static final int TOAST_UPLOAD_FAILED = -1;
    
    private static final int TOAST_INPUT_NUMBER_LIMIT = 2;

    private static final int TOAST_UPLOAD_EXCEPTION = 99;

    private static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "====msg.what=" + msg.what);
            switch (msg.what) {
                case TOAST_UPLOADING:
                    Toast.makeText(mActivity, mActivity.getString(R.string.bugreport_uploading), Toast.LENGTH_LONG)
                            .show();
                    break;
                case TOAST_UPLOAD_SUCCESS:
                    Toast.makeText(mActivity, mActivity.getString(R.string.bugreport_upload_success), Toast.LENGTH_LONG)
                            .show();
                    break;

                case TOAST_UPLOAD_FAILED:
                    Toast.makeText(mActivity, mActivity.getString(R.string.bugreport_upload_failed), Toast.LENGTH_LONG)
                            .show();
                    break;
                case TOAST_UPLOAD_EXCEPTION:
                    Toast.makeText(mActivity, mActivity.getString(R.string.bugreport_exception), Toast.LENGTH_LONG)
                            .show();
                    break;
                case TOAST_NETWORK_DISCONNECT:
                    Toast.makeText(mActivity, mActivity.getString(R.string.network_disconnected), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case TOAST_INPUT_NUMBER_LIMIT:
                    Toast.makeText(mActivity,  mActivity.getString(R.string.bugreport_limit), Toast.LENGTH_SHORT)
                            .show();
                    break;
                default:
                    break;
            }
        }
    };
    
    TextWatcher mTextWatcher = new TextWatcher(){
        private CharSequence temp;
        private int editStart ;
        private int editEnd ;
        @Override
        public void afterTextChanged(Editable s) {
            editStart = mBugreportEditTextView.getSelectionStart();
            editEnd = mBugreportEditTextView.getSelectionEnd();
            if (temp.length() > LIMIT_NUMBER) {
                mHandler.sendEmptyMessage(TOAST_INPUT_NUMBER_LIMIT);
                s.delete(editStart-1, editEnd);
                int tempSelection = editStart;
                mBugreportEditTextView.setText(s);
                mBugreportEditTextView.setSelection(tempSelection);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            String text = mActivity.getString(R.string.bugreport_surplus_char) + (LIMIT_NUMBER-s.length());
            mCountTextView.setText(text);
        }
    };

    public BugReportHolder(BugReportActivity activity) {
        mActivity = activity;
    }

    public void findViews() {
        mMainTitle = (TitleWidget) mActivity.findViewById(R.id.activity_bugreport_title);
        mDeviceId = (TextView) mActivity.findViewById(R.id.tv_device_id);
        mCountTextView =  (TextView) mActivity.findViewById(R.id.tv_count);
        mBugreportEditTextView = (EditText) mActivity.findViewById(R.id.et_bugport_content);
        mSubmitBtn = (Button) mActivity.findViewById(R.id.btn_bugreport_submit);
    }

    public void initViews() {
        mMainTitle.setMainTitleText(mActivity.getString(R.string.action_settings));
        mMainTitle.setFirstSubTitleText(mActivity.getString(R.string.bugreport_title), true);
        mSubmitBtn.setText(mActivity.getResources().getString(R.string.submit));
        mDeviceId.setText(Device.getBb());
        mCountTextView.setText(mActivity.getString(R.string.bugreport_surplus_char) + LIMIT_NUMBER);
    }

    public void registerListener() {
        mBugreportEditTextView.addTextChangedListener(mTextWatcher);
        mSubmitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivity.isNetworkConnect()) {
                    mHandler.sendEmptyMessage(TOAST_UPLOADING);
                    LogUtils.IResultListener resultListener = new LogUtils.IResultListener() {
                        @Override
                        public void captureResult(boolean result) {
                            Log.d("LogUtils", "capture result:" + result);
                            if (result) {
                                mHandler.sendEmptyMessage(TOAST_UPLOAD_SUCCESS);
                            } else {
                                mHandler.sendEmptyMessage(TOAST_UPLOAD_FAILED);
                            }
                        }
                    };
                    LogUtils.captureLog(mActivity, getText(), resultListener);
                    SystemProperties.set("ctl.start", "bugreport");
                    mActivity.finish();
                    Log.d(TAG, "SystemProperties is done!");
                } else {
                    mHandler.sendEmptyMessage(TOAST_NETWORK_DISCONNECT);
                }
            }
        });
        mSubmitBtn.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
                    mSubmitBtn.setTextColor(Color.rgb(255, 255, 255));
                } else {
                    mSubmitBtn.setTextColor(Color.rgb(0, 0, 0));
                }
            }
        });
    }

    private void setText() {
        String text = "";
        text = mBugreportEditTextView.getText().toString();
        if (text == null || text.equals("")) {
            text = "User suggestion";
        }
        Log.d(TAG, "setText:" + text);
        mText = text;
    }

    private String getText() {
        setText();
        Log.d(TAG, "getText:" + mText);
        return mText;
    }

}
