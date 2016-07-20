
package com.mstar.tv.menu.setting.atvfinetuning;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;

import com.mstar.tv.ExTvChannelManager;
import com.mstar.tv.FocusView;
import com.mstar.tv.menu.R;

public class FineTuningListeners {

    private FineTuningViewHolder mCMiniStrimViewHolder;

    public FineTuningListeners(FineTuningViewHolder viewHolder) {
        super();
        this.mCMiniStrimViewHolder = viewHolder;
    }

    public void setListeners() {
        MiniStrimButtonClick onClick = new MiniStrimButtonClick();
        this.mCMiniStrimViewHolder.getViewById(R.id.save_channel).setOnClickListener(onClick);
        this.mCMiniStrimViewHolder.getViewById(R.id.cancel_channel).setOnClickListener(onClick);
        FocusChangeListener focusChangeListener = new FocusChangeListener();
        mCMiniStrimViewHolder.getViewById(R.id.llayout).setOnFocusChangeListener(
                focusChangeListener);
        mCMiniStrimViewHolder.getViewById(R.id.save_channel).setOnFocusChangeListener(
                focusChangeListener);
        mCMiniStrimViewHolder.getViewById(R.id.cancel_channel).setOnFocusChangeListener(
                focusChangeListener);
    }

    class FocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                Rect rect = new Rect();
                v.getGlobalVisibleRect(rect);
                ((FocusView) mCMiniStrimViewHolder.getViewById(R.id.focus_selector))
                        .startAnimation(v);
            }
        }
    }

    private class MiniStrimButtonClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save_channel:
                    FineTuningListeners.this.mCMiniStrimViewHolder.getActivity()
                            .channelMiniStrimStop();
                    ExTvChannelManager.getInstance().saveAtvProgram(
                            ExTvChannelManager.getInstance().getCurrentChannelNumber());
                    FineTuningListeners.this.mCMiniStrimViewHolder.getActivity().dismiss();
                    break;
                case R.id.cancel_channel:
                    FineTuningListeners.this.mCMiniStrimViewHolder.getActivity()
                            .channelMiniStrimStop();
                    FineTuningListeners.this.mCMiniStrimViewHolder.getActivity().dismiss();
                    break;
            }
        }
    }
}
