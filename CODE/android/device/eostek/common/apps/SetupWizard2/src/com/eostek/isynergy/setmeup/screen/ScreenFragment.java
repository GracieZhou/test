
package com.eostek.isynergy.setmeup.screen;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.eostek.isynergy.setmeup.R;
import com.eostek.isynergy.setmeup.StateMachineActivity;
import com.eostek.isynergy.setmeup.WizardLogic;

public class ScreenFragment extends Fragment {

    private Button mScreenMovingBtn;

    private Button mScreenScaleBtn;

    private int mPosition;

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_fragment_screen, container, false);

        mScreenMovingBtn = (Button) v.findViewById(R.id.new_btn_screen_position);
        mScreenScaleBtn = (Button) v.findViewById(R.id.new_btn_screen_size);

        mScreenMovingBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((StateMachineActivity) ScreenFragment.this.getActivity()).gotoState(WizardLogic.STATE_SCREEN_MOVING);
                System.out.println("onClick()1");
            }
        });

        mScreenScaleBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((StateMachineActivity) ScreenFragment.this.getActivity()).gotoState(WizardLogic.STATE_SCREEN_SCALE);
                System.out.println("onClick()2");
            }
        });

        v.requestFocus();

        return v;
    }

    /**
     * get focused item id
     * @return 0 move, 1 scale
     */
    public int getFocusedItem() {
        if (mScreenMovingBtn.isFocused()) {
            return 0;
        } else if (mScreenScaleBtn.isFocused()) {
            return 1;
        }
        return -1;
    }

}
