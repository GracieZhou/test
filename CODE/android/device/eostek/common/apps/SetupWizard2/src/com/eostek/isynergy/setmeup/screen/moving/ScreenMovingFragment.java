
package com.eostek.isynergy.setmeup.screen.moving;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.eostek.isynergy.setmeup.R;
import com.eostek.isynergy.setmeup.WizardLogic;
import com.eostek.isynergy.setmeup.screen.ScreenLogic;
import com.eostek.isynergy.setmeup.utils.Utils;

public class ScreenMovingFragment extends Fragment {

    private static final String TAG = ScreenMovingFragment.class.getSimpleName();

    private ImageView mScreenPosLeftIv;

    private ImageView mScreenPosRightIv;

    private ImageView mScreenPosUpIv;

    private ImageView mScreenPosDownIv;
    
    private FrameLayout.LayoutParams mLParams;
    
    private FrameLayout.LayoutParams mRParams;
    
    private ScreenLogic mScreenLogic;

    private Handler mHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_fragment_screen_moving, container, false);
        v.requestFocus();
        
        mScreenLogic = new ScreenLogic(WizardLogic.mContext);
        
        mScreenPosLeftIv = (ImageView) v.findViewById(R.id.iv_position_left);
        mScreenPosRightIv = (ImageView) v.findViewById(R.id.iv_position_right);
        mScreenPosUpIv = (ImageView) v.findViewById(R.id.iv_position_up);
        mScreenPosDownIv = (ImageView) v.findViewById(R.id.iv_position_down);
        
        mLParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        mLParams.gravity = Gravity.CENTER_HORIZONTAL;
        
        mRParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        mRParams.gravity = Gravity.CENTER_HORIZONTAL;
        
        String language = getActivity().getResources().getConfiguration().locale + "";
        
        Log.e("test", "language =" + language);
        if (language.equals("en_US")||language.equals("fr_FR")) {
        	mLParams.leftMargin = -138;
        	mLParams.topMargin = 160;
        	mLParams.width = 25;
        	mLParams.height = 80;
        	mScreenPosLeftIv.setLayoutParams(mLParams);
        	
        	mRParams.leftMargin = 140;
        	mRParams.topMargin = 160;
        	mRParams.width = 25;
        	mRParams.height = 80;
        	mScreenPosRightIv.setLayoutParams(mRParams);
        }
        
        mScreenPosLeftIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mScreenLogic.moving(WizardLogic.CMD_LEFT);
				mScreenPosLeftIv.setImageResource(R.drawable.position_left_selected);

				mHandler.postDelayed(leftSelect, 300);
			}
		});
        mScreenPosRightIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mScreenLogic.moving(WizardLogic.CMD_RIGHT);
				mScreenPosRightIv.setImageResource(R.drawable.position_right_selected);
				mHandler.postDelayed(rightSelect, 300);
			}
		});
        mScreenPosUpIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mScreenLogic.moving(WizardLogic.CMD_UP);
				mScreenPosUpIv.setImageResource(R.drawable.position_up_selected);
				mHandler.postDelayed(upSelect, 300);
			}
		});
        mScreenPosDownIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mScreenLogic.moving(WizardLogic.CMD_DOWN);
				mScreenPosDownIv.setImageResource(R.drawable.position_down_selected);
				mHandler.postDelayed(downSelect, 300);
			}
		});
        return v;
    }

    /**
     * show the arrow when click up,down,left,right
     * @param what
     */
    public void showArrow(final int what) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                switch (what) {
                    case WizardLogic.CMD_UP:
                        Utils.print(TAG, "screen moving up");
                        mScreenPosLeftIv.setImageResource(R.drawable.position_left_unselected);
                        mScreenPosRightIv.setImageResource(R.drawable.position_right_unselected);
                        mScreenPosUpIv.setImageResource(R.drawable.position_up_selected);
                        mScreenPosDownIv.setImageResource(R.drawable.position_down_unselected);
                        mHandler.postDelayed(upSelect, 300);
                        break;

                    case WizardLogic.CMD_DOWN:
                        Utils.print(TAG, "screen moving down");
                        mScreenPosLeftIv.setImageResource(R.drawable.position_left_unselected);
                        mScreenPosRightIv.setImageResource(R.drawable.position_right_unselected);
                        mScreenPosUpIv.setImageResource(R.drawable.position_up_unselected);
                        mScreenPosDownIv.setImageResource(R.drawable.position_down_selected);
                        mHandler.postDelayed(downSelect, 300);
                        break;

                    case WizardLogic.CMD_LEFT:
                        Utils.print(TAG, "screen moving left");
                        mScreenPosLeftIv.setImageResource(R.drawable.position_left_selected);
                        mScreenPosRightIv.setImageResource(R.drawable.position_right_unselected);
                        mScreenPosUpIv.setImageResource(R.drawable.position_up_unselected);
                        mScreenPosDownIv.setImageResource(R.drawable.position_down_unselected);
                        mHandler.postDelayed(leftSelect, 300);
                        break;

                    case WizardLogic.CMD_RIGHT:
                        Utils.print(TAG, "screen moving right");
                        mScreenPosLeftIv.setImageResource(R.drawable.position_left_unselected);
                        mScreenPosRightIv.setImageResource(R.drawable.position_right_selected);
                        mScreenPosUpIv.setImageResource(R.drawable.position_up_unselected);
                        mScreenPosDownIv.setImageResource(R.drawable.position_down_unselected);
                        mHandler.postDelayed(rightSelect, 300);
                        break;
                }
            }
        });

    }

    private Runnable leftSelect = new Runnable() {
        public void run() {
            mScreenPosLeftIv.setImageResource(R.drawable.position_left_unselected);

        }
    };

    private Runnable rightSelect = new Runnable() {
        public void run() {
            mScreenPosRightIv.setImageResource(R.drawable.position_right_unselected);
        }
    };

    private Runnable upSelect = new Runnable() {
        public void run() {
            mScreenPosUpIv.setImageResource(R.drawable.position_up_unselected);
        }
    };

    private Runnable downSelect = new Runnable() {
        public void run() {
            mScreenPosDownIv.setImageResource(R.drawable.position_down_unselected);
        }
    };

}
