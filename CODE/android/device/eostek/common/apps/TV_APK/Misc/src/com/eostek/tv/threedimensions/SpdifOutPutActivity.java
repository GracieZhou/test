
package com.eostek.tv.threedimensions;

import com.mstar.android.tv.TvAudioManager;
import com.mstar.android.tvapi.common.vo.EnumSpdifType;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ListView;
import android.widget.TextView;

public class SpdifOutPutActivity extends Activity {
    private ListView mLst;

    private TextView title;

    private int spdifValue = 0;

    private String[] mValues = new String[1];

    private ThreeDimensionsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three_dimensions);
        mLst = (ListView) findViewById(R.id.menu_lst);
        title = (TextView) findViewById(R.id.title_txt);
        title.setText(R.string.title_spdif);
        String[] title = {
            getResources().getString(R.string.title_spdif)
        };
        spdifValue = TvAudioManager.getInstance().getSpdifOutMode().ordinal();
        mValues[0] = getResources().getStringArray(R.array.setting_spdifoutput_vals)[spdifValue];
        boolean[] mClickable = {
            true
        };
        mAdapter = new ThreeDimensionsAdapter(title, mValues, mClickable, this);
        mLst.setAdapter(mAdapter);
        setListener();
    }

    private void setListener() {
        OnKeyListener listener = new OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int keyCode, KeyEvent event) {
                int currentposition = mLst.getSelectedItemPosition();
                if ((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
                        && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    switch (currentposition) {
                        case 0:
                            if (spdifValue < 2) {
                                spdifValue++;
                            } else {
                                spdifValue = 0;
                            }
                            break;
                    }
                } else if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
                        && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    switch (currentposition) {
                        case 0:
                            if (spdifValue > 0) {
                                spdifValue--;
                            } else {
                                spdifValue = 2;
                            }
                            break;
                    }
                }
                TvAudioManager.getInstance().setSpdifOutMode(EnumSpdifType.values()[spdifValue]);
                mValues[0] = getResources().getStringArray(R.array.setting_spdifoutput_vals)[spdifValue];
                mAdapter.notifyDataSetChanged();
                return false;
            }
        };
        mLst.setOnKeyListener(listener);
    }
}
