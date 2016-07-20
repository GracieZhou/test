
package com.eostek.isynergy.setmeup;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.isynergy.setmeup.language.LanguageFragment;
import com.eostek.isynergy.setmeup.model.TitleModel;
import com.eostek.isynergy.setmeup.network.NetworkFragment;
import com.eostek.isynergy.setmeup.screen.ScreenFragment;
import com.eostek.isynergy.setmeup.screen.moving.ScreenMovingFragment;
import com.eostek.isynergy.setmeup.screen.scale.ScreenScaleFragment;
import com.eostek.isynergy.setmeup.timezone.TimeZoneFragment;
import com.eostek.isynergy.setmeup.utils.Utils;

public class WizardHolder {
    private static final String TAG = WizardHolder.class.getSimpleName();

    private StateMachineActivity mStateMachineActivity;

    private FragmentManager mFragmentManager;

    private FragmentTransaction mFragmentTransaction;

    private LanguageFragment mLanguageFragment;

    private ScreenFragment mScreenFragment;

    private ScreenMovingFragment mScreenMovingFragment;

    private ScreenScaleFragment mScreenScaleFragment;

    private NetworkFragment mNetworkFragment;

    private TimeZoneFragment mTimeZoneFragment;

    private GridView mTitleGridView;

    public static ImageView leftBtn;

    private ImageView rightBtn;

    private boolean bDongle = false;
    
    private int mPosition;
    
    private static final int LANGUAGE_PAGE = 1;
    
    private static final int POSITION_PAGE = 2;
    
    private static final int NETWORK_PAGE = 3;
    
    private static final int TIMEZONE_PAGE = 4;

    private Handler mHandler = new Handler();

    public WizardHolder(StateMachineActivity context) {
        this.mStateMachineActivity = context;
        this.mFragmentManager = mStateMachineActivity.getFragmentManager();
        bDongle = Utils.get(context, "ro.scifly.platform").equals("dongle");
        mPosition = LANGUAGE_PAGE;
        findViews();
    }

    private void findViews() {

        leftBtn = (ImageView) mStateMachineActivity.findViewById(R.id.iv_left);
        rightBtn = (ImageView) mStateMachineActivity.findViewById(R.id.iv_right);

        mTitleGridView = (GridView) mStateMachineActivity.findViewById(R.id.gv_top_bar);

        List<TitleModel> titles = initTitles();
        TitleAdapter adapter = new TitleAdapter(mStateMachineActivity, titles);
        mTitleGridView.setAdapter(adapter);
        mTitleGridView.setNumColumns(titles.size());

        mTitleGridView.setFocusable(false);
//        mTitleGridView.setClickable(false);

        int index = 0;
        this.mLanguageFragment = new LanguageFragment();
        this.mLanguageFragment.setPosition(index);
        if (bDongle) {
            this.mScreenFragment = new ScreenFragment();
            this.mScreenFragment.setPosition(++index);

            this.mScreenMovingFragment = new ScreenMovingFragment();
            this.mScreenScaleFragment = new ScreenScaleFragment();
        }
        this.mNetworkFragment = new NetworkFragment();
        this.mNetworkFragment.setPosition(++index);

        this.mTimeZoneFragment = new TimeZoneFragment();
        this.mTimeZoneFragment.setPosition(++index);
        
        mTitleGridView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(bDongle){
					Log.e("test", "position =" + position);
					switch (position) {
						case LANGUAGE_PAGE - 1:
							mStateMachineActivity.mMyStateMachine.gotoState(WizardLogic.CMD_GOTO_LAGPAGE);
							break;
						case POSITION_PAGE - 1:
							mStateMachineActivity.mMyStateMachine.gotoState(WizardLogic.CMD_GOTO_SREPAGE);
							break;
						case NETWORK_PAGE - 1:
							mStateMachineActivity.mMyStateMachine.gotoState(WizardLogic.CMD_GOTO_NETPAGE);
							break;
						case TIMEZONE_PAGE - 1:
							mStateMachineActivity.mMyStateMachine.gotoState(WizardLogic.CMD_GOTO_TIMPAGE);
							break;

						default:
							break;
					}
				}else{
					switch (position) {
					case LANGUAGE_PAGE - 1:
						mStateMachineActivity.mMyStateMachine.gotoState(WizardLogic.CMD_GOTO_LAGPAGE);
						break;
					case NETWORK_PAGE - 2:
						mStateMachineActivity.mMyStateMachine.gotoState(WizardLogic.CMD_GOTO_NETPAGE);
						break;
					case TIMEZONE_PAGE - 2:
						mStateMachineActivity.mMyStateMachine.gotoState(WizardLogic.CMD_GOTO_TIMPAGE);
						break;

					default:
						break;
					}
				}
			}
        	
        });
        
        leftBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch (mPosition) {
				case LANGUAGE_PAGE:
					showKeyDown(true);
					mStateMachineActivity.mMyStateMachine.gotoState(WizardLogic.CMD_LEFT_BTN);
					mPosition = LANGUAGE_PAGE;
					break;
				case POSITION_PAGE:
					showKeyDown(true);
					mPosition = LANGUAGE_PAGE;
					break;
				case NETWORK_PAGE:
					showKeyDown(true);
					if(bDongle){
						mPosition = POSITION_PAGE;
					}else{
						mPosition = LANGUAGE_PAGE;
					}
					break;
				case TIMEZONE_PAGE:
					showKeyDown(true);
					mPosition = NETWORK_PAGE;
					break;
				default:
					break;
				}
				mStateMachineActivity.mMyStateMachine.gotoState(WizardLogic.CMD_LEFT_BTN);
				Log.e("test", "mPosition =" + mPosition);
			}
		});
        
        rightBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				switch (mPosition) {
				case LANGUAGE_PAGE:
					showKeyDown(false);
					if(bDongle){
						mPosition = POSITION_PAGE;
					}else{
						mPosition = NETWORK_PAGE;
					}
					break;
				case POSITION_PAGE:
					showKeyDown(false);
					mPosition = NETWORK_PAGE;
					break;
				case NETWORK_PAGE:
					showKeyDown(false);
					mPosition = TIMEZONE_PAGE;
					break;
				case TIMEZONE_PAGE:
					showKeyDown(false);
					break;
				default:
					break;
				}
				mStateMachineActivity.mMyStateMachine.gotoState(WizardLogic.CMD_RIGHT_BTN);
				Log.e("test", "mPosition =" + mPosition);
			}
		});

    }

    private List<TitleModel> initTitles() {
        List<TitleModel> titles = new ArrayList<TitleModel>();

        titles.add(new TitleModel(R.drawable.language_unfocus, mStateMachineActivity.getString(R.string.title_language)));
        if (bDongle) {
            titles.add(new TitleModel(R.drawable.screen_unfocus, mStateMachineActivity.getString(R.string.title_screen)));
        }
        titles.add(new TitleModel(R.drawable.network_unfocus, mStateMachineActivity.getString(R.string.title_network)));
        titles.add(new TitleModel(R.drawable.timezone_unfocus, mStateMachineActivity.getString(R.string.title_timezone)));

        return titles;
    }

    /**
     * goto language fragment
     */
    public void gotoLanguageFragment() {
        this.mFragmentTransaction = mFragmentManager.beginTransaction();
        this.mFragmentTransaction.replace(R.id.fl_item, mLanguageFragment);
        this.mFragmentTransaction.commit();

        View selectedView = (View) mTitleGridView.getChildAt(mLanguageFragment.getPosition());
        resetTitle(selectedView, R.drawable.language_focused, R.color.green);
    }

    /**
     * release language fragment
     */
    public void releaseLanguageFragment() {
        View selectedView = (View) mTitleGridView.getChildAt(mLanguageFragment.getPosition());
        resetTitle(selectedView, R.drawable.language_unfocus, R.color.white);
    }

    /**
     * goto network fragment
     */
    public void gotoNetworkFragment() {
        this.mFragmentTransaction = mFragmentManager.beginTransaction();
        this.mFragmentTransaction.replace(R.id.fl_item, mNetworkFragment);
        this.mFragmentTransaction.commit();

        View selectedView = (View) mTitleGridView.getChildAt(mNetworkFragment.getPosition());
        resetTitle(selectedView, R.drawable.network_focused, R.color.green);
    }

    /**
     * release Network Fragment
     */
    public void releaseNetworkFragment() {
        View selectedView = (View) mTitleGridView.getChildAt(mNetworkFragment.getPosition());
        resetTitle(selectedView, R.drawable.network_unfocus, R.color.white);
    }

    /**
     * goto Screen Fragment
     */
    public void gotoScreenFragment() {
        this.mFragmentTransaction = mFragmentManager.beginTransaction();
        this.mFragmentTransaction.replace(R.id.fl_item, mScreenFragment);
        this.mFragmentTransaction.commit();

        View selectedView = (View) mTitleGridView.getChildAt(mScreenFragment.getPosition());
        resetTitle(selectedView, R.drawable.screen_focused, R.color.green);
    }

    /**
     * release Screen Fragment
     */
    public void releaseScreenFragment() {
        View selectedView = (View) mTitleGridView.getChildAt(mScreenFragment.getPosition());
        resetTitle(selectedView, R.drawable.screen_unfocus, R.color.white);
    }

    /**
     * goto TimeZone Fragment
     */
    public void gotoTimeZoneFragment() {
        this.mFragmentTransaction = mFragmentManager.beginTransaction();
        this.mFragmentTransaction.replace(R.id.fl_item, mTimeZoneFragment);
        this.mFragmentTransaction.commit();

        View selectedView = (View) mTitleGridView.getChildAt(mTimeZoneFragment.getPosition());
        resetTitle(selectedView, R.drawable.timezone_focused, R.color.green);
    }

    /**
     * release TimeZone Fragment
     */
    public void releaseTimeZoneFragment() {
        View selectedView = (View) mTitleGridView.getChildAt(mTimeZoneFragment.getPosition());
        resetTitle(selectedView, R.drawable.timezone_unfocus, R.color.white);
    }

    /**
     * goto Screen Moving Fragment
     */
    public void gotoScreenMovingFragment() {
        this.mFragmentTransaction = mFragmentManager.beginTransaction();
        this.mFragmentTransaction.replace(R.id.fl_item, mScreenMovingFragment);
        this.mFragmentTransaction.commit();
    }

    /**
     * goto Screen Scale Fragment
     */
    public void gotoScreenScaleFragment() {
        this.mFragmentTransaction = mFragmentManager.beginTransaction();
        this.mFragmentTransaction.replace(R.id.fl_item, mScreenScaleFragment);
        this.mFragmentTransaction.commit();

    }

    /**
     * get Focused Screen Item
     * 
     * @return the item index
     */
    public int getFocusedScreenItem() {
        return mScreenFragment.getFocusedItem();
    }

    private void resetTitle(final View selectedView, final int icon, final int color) {
        if (selectedView == null) {
            Utils.print(TAG, "selectedView is null");
            return;
        }
        mStateMachineActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ImageView img = (ImageView) selectedView.findViewById(R.id.iv_gridview_item);
                if (img != null) {
                    img.setImageResource(icon);
                }

                TextView textView = (TextView) selectedView.findViewById(R.id.tv_gridview_item);
                if (textView != null) {
                    textView.setTextColor(mStateMachineActivity.getResources().getColor(color));
                }

            }
        });
    }

    /**
     * get ScreenScaleFragment
     * 
     * @return ScreenScaleFragment
     */
    public ScreenScaleFragment getScreenScaleFrag() {
        return mScreenScaleFragment;
    }

    /**
     * get NetworkFragment
     * 
     * @return NetworkFragment
     */
    public NetworkFragment getNetworkFragment() {
        return mNetworkFragment;
    }

    /**
     * get ScreenMovingFragment
     * 
     * @return ScreenMovingFragment
     */
    public ScreenMovingFragment getScreenMovingFragment() {
        return mScreenMovingFragment;
    }

    public void showKeyDown(boolean bLeft) {
        if ((null != mScreenMovingFragment && mScreenMovingFragment.isVisible())
                || (null != mScreenScaleFragment && mScreenScaleFragment.isVisible())) {
            return;
        }
        if (bLeft) {
            leftBtn.setImageResource(R.drawable.left_pressed);
            mHandler.postDelayed(leftDown, 300);
        } else {
            rightBtn.setImageResource(R.drawable.right_pressed);
            mHandler.postDelayed(rightDown, 300);
        }
    }

    private Runnable leftDown = new Runnable() {
        public void run() {
            leftBtn.setVisibility(mLanguageFragment.isVisible() ? View.INVISIBLE : View.VISIBLE);
            rightBtn.setVisibility(View.VISIBLE);
            leftBtn.setImageResource(R.drawable.left_unpress);
        }
    };

    private Runnable rightDown = new Runnable() {
        public void run() {
            leftBtn.setVisibility(View.VISIBLE);
            rightBtn.setVisibility(View.VISIBLE);
            rightBtn.setImageResource(R.drawable.right_unpress);
        }
    };
}
