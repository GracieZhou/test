
package com.mstar.tv.menu.setting.atvfinetuning;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mstar.tv.FocusView;
import com.mstar.tv.menu.R;

public class FineTuningViewHolder {

    private FineTuningDialog mCMiniStrimDialog;

    private Button mSaveBtn;

    private Button mESCBtn;

    private TextView mCurrentChannelNotext;

    private TextView mCurrentFre;

    private ProgressBar mMiniStrimProgress;

    private TextView value;

    private FocusView focusView;

    private LinearLayout llayout;

    public FineTuningViewHolder(FineTuningDialog dialog) {
        this.mCMiniStrimDialog = dialog;
    }

    public void findViews() {
        this.mSaveBtn = (Button) this.mCMiniStrimDialog.findViewById(R.id.save_channel);
        this.mESCBtn = (Button) this.mCMiniStrimDialog.findViewById(R.id.cancel_channel);
        this.mCurrentChannelNotext = (TextView) this.mCMiniStrimDialog
                .findViewById(R.id.current_channel);
        mCurrentFre = (TextView) this.mCMiniStrimDialog.findViewById(R.id.current_fre);
        this.mMiniStrimProgress = (ProgressBar) this.mCMiniStrimDialog
                .findViewById(R.id.ministrim_progress);
        value = (TextView) this.mCMiniStrimDialog.findViewById(R.id.value);
        focusView = (FocusView) this.mCMiniStrimDialog.findViewById(R.id.focus_selector);
        llayout = (LinearLayout) this.mCMiniStrimDialog.findViewById(R.id.llayout);
    }

    public View getViewById(int id) {
        switch (id) {
            case R.id.ministrim_progress:
                return mMiniStrimProgress;
            case R.id.current_channel:
                return mCurrentChannelNotext;
            case R.id.current_fre:
                return mCurrentFre;
            case R.id.save_channel:
                return mSaveBtn;
            case R.id.cancel_channel:
                return mESCBtn;
            case R.id.value:
                return value;
            case R.id.focus_selector:
                return focusView;
            case R.id.llayout:
                return llayout;
            default:
                return null;
        }
    }

    public FineTuningDialog getActivity() {
        return this.mCMiniStrimDialog;
    }
}
