
package com.heran.launcher2.message;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.MainViewHolder;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class MessageFragment extends PublicFragment {

    private final static String TAG = "MessageFragment";

    // Focus View for mFlipper

    private HomeActivity mContext;

    public TextSwitcher textAd_Txt;

    public MessageFragment() {
        super();
        Log.v(TAG, "public Messagefragment()");
    }

    public MessageFragment(HomeActivity context, MainViewHolder mHolder) {
        super();
        this.mContext = context;
        Log.d(TAG, "(HomeActivity context, MainViewHolder mHolder)");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.message_main, container, false);

        InitView(mview);

        setRetainInstance(true);
        return mview;
    }

    public void InitView(View mview) {

        textAd_Txt = (TextSwitcher) mview.findViewById(R.id.textad_txt);
        textAd_Txt.setFactory(new ViewFactory() {

            @Override
            public View makeView() {
                TextView mScrollTextView = new TextView(getActivity());
                mScrollTextView.setTextColor(Color.WHITE);
                mScrollTextView.setTextSize(20);
                mScrollTextView.setSingleLine();
                mScrollTextView.setEllipsize(TruncateAt.MARQUEE);
                mScrollTextView.setSelected(true);
                return mScrollTextView;
            }
        });

        textAd_Txt.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_in));
        textAd_Txt.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_out));

    }

    @Override
    public void onResume() {
        super.onResume();
        showOSDMessage(mContext.GetAdText());

    }

    public void showOSDMessage(String osdmsg) {
        textAd_Txt.setText(osdmsg);
    }

}
