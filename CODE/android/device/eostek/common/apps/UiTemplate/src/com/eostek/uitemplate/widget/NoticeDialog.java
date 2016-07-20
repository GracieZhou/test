package com.eostek.uitemplate.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.eostek.scifly.style.R;

public class NoticeDialog extends Dialog implements android.view.View.OnClickListener {

    private Button mBtnConfirm;

    private Button mBtnCancle;

    private TextView mTvNotice;

    private TextView mTvTitle;

    private TextView mTvContent;

    private DialogOnClickListener mDialogOnClickListener;

    public void setTitleMain(String tip) {
        mTvNotice.setText(tip);
    }

    public void setTitleSub(String title) {
        mTvTitle.setText(title);
    }

    public void setContent(String content) {
        mTvContent.setText(content);
    }

    public interface DialogOnClickListener {
        public void onCancelClick();

        public void onConfirmClick();
    }

    public void setDialogOnClickListener(DialogOnClickListener mDialogOnClickListener) {
        this.mDialogOnClickListener = mDialogOnClickListener;
    }

    public NoticeDialog(Context context) {
        super(context, R.style.Scifly_Dialog);
        setContentView(R.layout.scifly_notice_dialog_layout);
        this.setCanceledOnTouchOutside(false);
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        initView();
    }

    private void initView() {
        mBtnConfirm = (Button) findViewById(R.id.scifly_dialog_noticedialog_btn_confirm);
        mBtnCancle = (Button) findViewById(R.id.scifly_dialog_noticedialog_btn_cancle);

        mBtnConfirm.setOnClickListener(this);
        mBtnCancle.setOnClickListener(this);
        
        mBtnCancle.requestFocus();

        mTvNotice = (TextView) findViewById(R.id.scifly_dialog_noticedialog_tv_tip);
        mTvTitle = (TextView) findViewById(R.id.scifly_dialog_noticedialog_tv_title);
        mTvContent = (TextView) findViewById(R.id.scifly_dialog_noticedialog_tv_content);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.scifly_dialog_noticedialog_btn_confirm) {
            if (mDialogOnClickListener != null) {
                mDialogOnClickListener.onConfirmClick();
            }
            dismiss();
        } else if(v.getId() == R.id.scifly_dialog_noticedialog_btn_cancle) {
            if (mDialogOnClickListener != null) {
                mDialogOnClickListener.onCancelClick();
            }
            dismiss();
        }
    }
}
