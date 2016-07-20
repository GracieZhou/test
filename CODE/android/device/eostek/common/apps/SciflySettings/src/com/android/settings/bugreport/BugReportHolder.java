
package com.android.settings.bugreport;

import scifly.device.Device;
import scifly.util.LogUtils;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.util.Utils;
import com.android.settings.widget.TitleWidget;

/**
 * To initialize the views and add button click listener
 * 
 * @author billy.liu
 */
public class BugReportHolder {
    public static final String TAG = "BugReportHolder";

    private static BugReportActivity mActivity;

    private TitleWidget mMainTitle;

    private TextView mDeviceId;

    private TextView mCountTextView;

    private EditText mBugreportEditTextView;

    private TextView mShortupTextView;

    private Button mSubmitBtn;

    /**
     * FeedbackRecord
     */
    private Button mFeedbackRecordBtn;

    /**
     * shortcutFeedback action
     */
    private final String shortcutFeedbackActionString = "android.settings.action.SHORTCUTFEEDBACKACTIVITY";

    /**
     * feedbackRecord action
     */
    private final String feedbackRecordActionString = "android.settings.action.FEEDBACKRECORDACTIVITY";

    private static final int LIMIT_NUMBER = 200;

    private static final int TOAST_NETWORK_DISCONNECT = -2;

    private static final int TOAST_UPLOAD_FAILED = -1;

    private static final int TOAST_UPLOADING = 0;

    private static final int TOAST_UPLOAD_SUCCESS = 1;

    private static final int TOAST_INPUT_NUMBER_LIMIT = 2;

    private static final int TOAST_UPLOAD_EXCEPTION = 99;

    /**
     * To hand the message of upload log
     */
    private static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "msg.what" + msg.what);
            switch (msg.what) {
                case TOAST_UPLOADING:
                    Utils.showToast(mActivity, R.string.bugreport_uploading);
                    break;
                case TOAST_UPLOAD_SUCCESS:
                    Utils.showToast(mActivity, R.string.bugreport_upload_success);
                    break;
                case TOAST_UPLOAD_FAILED:
                    Utils.showToast(mActivity, R.string.bugreport_upload_failed);
                    break;
                case TOAST_UPLOAD_EXCEPTION:
                    Utils.showToast(mActivity, R.string.bugreport_exception);
                    break;
                case TOAST_NETWORK_DISCONNECT:
                    Utils.showToast(mActivity, R.string.network_disconnected);
                    break;
                case TOAST_INPUT_NUMBER_LIMIT:
                    Utils.showToast(mActivity, R.string.bugreport_limit);
                    break;
                default:
                    break;
            }
        }
    };

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;

        private int editStart;

        private int editEnd;

        @Override
        public void afterTextChanged(Editable s) {
            editStart = mBugreportEditTextView.getSelectionStart();
            editEnd = mBugreportEditTextView.getSelectionEnd();
            if (temp.length() > LIMIT_NUMBER) {
                mHandler.sendEmptyMessage(TOAST_INPUT_NUMBER_LIMIT);
                s.delete(editStart - 1, editEnd);
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
            String text = mActivity.getString(R.string.bugreport_surplus_char) + (LIMIT_NUMBER - s.length());
            mCountTextView.setText(text);
        }
    };

    public BugReportHolder(BugReportActivity activity) {
        mActivity = activity;
    }

    /**
     * To find views
     */
    public void findViews() {
        mMainTitle = (TitleWidget) mActivity.findViewById(R.id.activity_bugreport_title);
        mDeviceId = (TextView) mActivity.findViewById(R.id.tv_device_id);
        mCountTextView = (TextView) mActivity.findViewById(R.id.tv_count);
        mBugreportEditTextView = (EditText) mActivity.findViewById(R.id.et_bugport_content);
        mSubmitBtn = (Button) mActivity.findViewById(R.id.btn_bugreport_submit);
        mFeedbackRecordBtn = (Button) mActivity.findViewById(R.id.btn_bugreport_record);
        mShortupTextView = (TextView) mActivity.findViewById(R.id.shortcut_tips);
        mShortupTextView.requestFocus();
    }

    /**
     * To initialize views
     */
    public void initViews() {
        mMainTitle.setSubTitleText(mActivity.getString(R.string.bugreport_title));
        mSubmitBtn.setText(mActivity.getResources().getString(R.string.submit));
        mDeviceId.setText(Device.getBb());
        mCountTextView.setText(mActivity.getString(R.string.bugreport_surplus_char) + LIMIT_NUMBER);
    }

    /**
     * To set up button click event
     */
    public void registerListener() {
        mBugreportEditTextView.addTextChangedListener(mTextWatcher);
        mSubmitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mBugreportEditTextView.getText().toString())) {
                    Utils.showToast(mActivity, R.string.bugreport_empty);
                    return;
                }
                if (Utils.isNetworkAvailable(mActivity)) {
                    mHandler.sendEmptyMessage(TOAST_UPLOADING);
                    LogUtils.IResultListener resultListener = new LogUtils.IResultListener() {
                        @Override
                        public void captureResult(boolean result) {
                            if (result) {
                                mHandler.sendEmptyMessage(TOAST_UPLOAD_SUCCESS);
                            } else {
                                mHandler.sendEmptyMessage(TOAST_UPLOAD_FAILED);
                            }
                        }
                    };
                    LogUtils.captureLog(mActivity, mBugreportEditTextView.getText().toString(), resultListener,
                            LogUtils.TYPE_MANUAL_UPLOAD_LOG);
                    // SystemProperties.set("ctl.start", "bugreport");
                    mActivity.finish();
                    Log.d(TAG, "SystemProperties is done!");
                } else {
                    mHandler.sendEmptyMessage(TOAST_NETWORK_DISCONNECT);
                }
            }
        });
        mFeedbackRecordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(feedbackRecordActionString);
                mActivity.startActivity(mIntent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        mShortupTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(shortcutFeedbackActionString);
                mActivity.startActivityForResult(mIntent, 0);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    /**
     * @Title: setTextToEditText
     * @Description: Set the edit box into the user's choice of quick feedback
     * @param: @param data
     * @return: void
     * @throws
     */
    public void setTextToEditText(Intent data) {
        if (!TextUtils.isEmpty(mBugreportEditTextView.getText().toString())) {
            mBugreportEditTextView.getText().clear();
        }
        mBugreportEditTextView.setText(data.getStringExtra("shortcutFeedback"));
        mSubmitBtn.requestFocus();
    }
}
