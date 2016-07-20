
package com.eostek.tv.standby;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eostek.tv.R;

public class CounterDownHolder {
    private Activity mContext;

    private LinearLayout mLinearLayoutPopupOffTime;

    private TextView mTextViewOffTimeSecond;

    public CounterDownHolder(Activity mContext) {
        this.mContext = mContext;
        initViews();
    }

    private void initViews() {
        mContext.setContentView(R.layout.counter_down_activity);
        mLinearLayoutPopupOffTime = (LinearLayout) mContext
                .findViewById(R.id.linear_layout_popup_offtime);
        mTextViewOffTimeSecond = (TextView) mContext.findViewById(R.id.text_view_offsecond);
    }

    public void setLayoutVisible(int visibility) {
        mLinearLayoutPopupOffTime.setVisibility(visibility);
    }

    public int getLayoutVisibility() {
        return mLinearLayoutPopupOffTime.getVisibility();
    }

    public void setOffTime(String time) {
        mTextViewOffTimeSecond.setText(time);
    }
}
