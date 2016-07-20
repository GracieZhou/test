package com.eostek.scifly.devicemanager.ui;

import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.eostek.scifly.style.R;

public class ConfirmDialog extends Dialog implements android.view.View.OnClickListener {

    private TextView mTvTitle;

    private Button mBtnConfirm;

    private Button mBtnCancle;

    private DialogOnClickListener mDialogOnClickListener;

    public interface DialogOnClickListener {
        public void onCancelClick();

        public void onConfirmClick();
    }

    public void setDialogOnClickListener(DialogOnClickListener mDialogOnClickListener) {
        this.mDialogOnClickListener = mDialogOnClickListener;
    }

    public ConfirmDialog(Context context) {
        super(context, R.style.Scifly_Dialog);
        setContentView(R.layout.scifly_confirm_dialog_layout);
        this.setCanceledOnTouchOutside(false);
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        initView();
    }

    public void setTitleMain(String title) {
        mTvTitle.setText(title);
    }

    private void initView() {
        mTvTitle = (TextView) findViewById(R.id.scifly_dialog_confirmdialog_tv_title);
        mBtnCancle = (Button) findViewById(R.id.scifly_dialog_confirmdialog_btn_confirm);
        mBtnConfirm = (Button) findViewById(R.id.scifly_dialog_confirmdialog_btn_cancle);

        mTvTitle.setPadding(0, 30, 0, 0);
        mTvTitle.setTextSize(30);
        mBtnCancle.setTextSize(24);
        mBtnConfirm.setTextSize(24);
        mBtnCancle.setHeight(70);
        mBtnConfirm.setHeight(70);
        
	    mBtnCancle.setText(com.eostek.scifly.devicemanager.R.string.act_bigfile_dialog_btn_cancel);
	    mBtnConfirm.setText(com.eostek.scifly.devicemanager.R.string.act_bigfile_dialog_btn_confirm);
        
        mBtnConfirm.setOnClickListener(this);
        mBtnCancle.setOnClickListener(this);
        
        mBtnConfirm.requestFocus();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.scifly_dialog_confirmdialog_btn_cancle) {
            if (mDialogOnClickListener != null) {
                mDialogOnClickListener.onConfirmClick();
            }
            dismiss();
        } else if(v.getId() == R.id.scifly_dialog_confirmdialog_btn_confirm) {
            if (mDialogOnClickListener != null) {
                mDialogOnClickListener.onCancelClick();
            }
            dismiss();
        }
    }
}
