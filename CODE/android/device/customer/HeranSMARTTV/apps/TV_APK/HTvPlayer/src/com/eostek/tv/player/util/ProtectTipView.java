
package com.eostek.tv.player.util;

import com.eostek.tv.player.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProtectTipView extends RelativeLayout {

    private static final String TAG = "ProtectTipView";

    private View view;

    private LinearLayout protect_tip_Layout;

    private TextView protectTipsTxt;

    public ProtectTipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public ProtectTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProtectTipView(Context context) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.eos_protect_tips, this, true);
        protect_tip_Layout = (LinearLayout) view.findViewById(R.id.protect_tip);

        protectTipsTxt = (TextView) view.findViewById(R.id.protect_tip_txt);
        protectTipsTxt.requestFocus();
        protect_tip_Layout.setVisibility(GONE);
    }

    public void showTipView() {
        Log.d(TAG, "------- to showTip");
        protect_tip_Layout.setVisibility(VISIBLE);
    }

    public void dismissTipView() {
        Log.d(TAG, "------- dismissTipView");
        protect_tip_Layout.setVisibility(GONE);
    }

    public boolean isShow() {
        return protect_tip_Layout.getVisibility() == View.VISIBLE;
    }

}
