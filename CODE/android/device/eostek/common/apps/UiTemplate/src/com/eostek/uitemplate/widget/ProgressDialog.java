package com.eostek.uitemplate.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eostek.scifly.style.R;


public class ProgressDialog extends Dialog implements android.view.View.OnClickListener {

    private Button mBtnCancle;

    private TextView mTvNotice;

    private TextView mTvTitle;

    private ProgressBar mProgressBar;

    private DialogOnClickListener mDialogOnClickListener;

    public void setTitleMain(String tip) {
        mTvNotice.setText(tip);
    }

    public void setTitleSub(String title) {
        mTvTitle.setText(title);
    }

    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

    public interface DialogOnClickListener {
        public void onCancelClick();

        public void onConfirmClick();
    }

    public void setDialogOnClickListener(DialogOnClickListener mDialogOnClickListener) {
        this.mDialogOnClickListener = mDialogOnClickListener;
    }

    public ProgressDialog(Context context) {
        super(context, R.style.Scifly_Dialog);
        setContentView(R.layout.scifly_progress_dialog_layout);
        this.setCanceledOnTouchOutside(false);
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        initView();
    }

    private void initView() {
        mBtnCancle = (Button) findViewById(R.id.scifly_dialog_progressdialog_btn_cancle);
        mBtnCancle.setOnClickListener(this);
        mBtnCancle.requestFocus();

        mTvNotice = (TextView) findViewById(R.id.scifly_dialog_progressdialog_tv_tip);
        mTvTitle = (TextView) findViewById(R.id.scifly_dialog_progressdialog_tv_title);
        mProgressBar = (ProgressBar) findViewById(R.id.scifly_dialog_progressdialog_pb);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.scifly_dialog_progressdialog_btn_cancle) {
            if (mDialogOnClickListener != null) {
                mDialogOnClickListener.onCancelClick();
            }
            dismiss();
        }
    }
}
