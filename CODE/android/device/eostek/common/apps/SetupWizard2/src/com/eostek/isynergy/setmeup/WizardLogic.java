
package com.eostek.isynergy.setmeup;

import android.os.Message;
import android.util.Log;
import android.view.View;

import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.eostek.isynergy.setmeup.network.NetworkFragment;
import com.eostek.isynergy.setmeup.screen.ScreenLogic;
import com.eostek.isynergy.setmeup.utils.Utils;

public class WizardLogic extends StateMachine {
    private static final String TAG = WizardLogic.class.getSimpleName();

    public static final int CMD_UP = 0x001;

    public static final int CMD_DOWN = 0x002;

    public static final int CMD_LEFT = 0x003;

    public static final int CMD_RIGHT = 0x004;

    public static final int CMD_ENTER = 0x005;

    public static final int CMD_ESC = 0x006;
    
    public static final int CMD_LEFT_BTN = 0x007;
    
    public static final int CMD_RIGHT_BTN = 0x008;

    public static final int STATE_SCREEN = 0x101;

    public static final int STATE_SCREEN_MOVING = 0x102;

    public static final int STATE_SCREEN_SCALE = 0x103;

	public static final int CMD_GOTO_LAGPAGE = 0x009;
	
	public static final int CMD_GOTO_SREPAGE = 0x00a;
	
	public static final int CMD_GOTO_NETPAGE = 0x00b;
	
	public static final int CMD_GOTO_TIMPAGE = 0x00c;

    private State mLanguageStatus = new LanguageState();

    State mScreenState = new ScreenState();

    private State mScreenMovingStatue = new ScreenMovingState();

    private State mScreenScaleState = new ScreenScaleState();

    private State mNetworkState = new NetworkState();

    private State mTimeZoneState = new TimeZoneState();

    public static StateMachineActivity mContext;
    
    public static boolean isFirst = true;

    ScreenLogic mScreenMovingLogic;// = new ScreenMovingLogic();

    private boolean bDongle = false;

    public WizardLogic(StateMachineActivity context, String name) {
        super(name);
        bDongle = Utils.get(context, "ro.scifly.platform").equals("dongle");
        this.mContext = context;
        addState(mLanguageStatus);
        if (bDongle) {
            this.mScreenMovingLogic = new ScreenLogic(mContext);
            addState(mScreenState);
            addState(mScreenMovingStatue, mScreenState);
            addState(mScreenScaleState, mScreenState);
        }
        addState(mNetworkState);
        addState(mTimeZoneState);

        setInitialState(mLanguageStatus);
        start();
    }

    private class LanguageState extends State {
        @Override
        public void enter() {
            print("------>Show language View.");
            mContext.getHolder().gotoLanguageFragment();
            super.enter();
        }

        @Override
        public boolean processMessage(Message msg) {

            switch (msg.what) {
                case CMD_LEFT:
                    print("do nothing.");
                    break;

                case CMD_RIGHT_BTN:
                case CMD_RIGHT:
                    if (bDongle) {
                        transitionTo(mScreenState);
                    } else {
                        transitionTo(mNetworkState);
                    }
                    break;

                case CMD_ESC:
                    print("do nothing.");
                    break;
                case CMD_GOTO_SREPAGE:
                	Log.e("test","from language to screen");
                	mContext.mHandler.sendEmptyMessage(mContext.SHOW_LEFT_ARROW);
                	transitionTo(mScreenState);
                	 break;
                case CMD_GOTO_NETPAGE:
                	Log.e("test","from language to network");
                	mContext.mHandler.sendEmptyMessage(mContext.SHOW_LEFT_ARROW);
                	transitionTo(mNetworkState);
                	 break;
                case CMD_GOTO_TIMPAGE:
                	Log.e("test","from language to timezone");
                	mContext.mHandler.sendEmptyMessage(mContext.SHOW_LEFT_ARROW);
                	transitionTo(mTimeZoneState);
                	 break;
                default:
                    return HANDLED;
            }
            return HANDLED;
        }
        
        @Override
        public void exit() {
            super.exit();
            print("<-------destroy language View.");
            mContext.getHolder().releaseLanguageFragment();
        }
    }

    private class ScreenState extends State {
        @Override
        public void enter() {
            print("------>Show screen View.");
            mContext.getHolder().gotoScreenFragment();
            super.enter();
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
            	case CMD_LEFT_BTN:
                case CMD_LEFT:
                case CMD_ESC:
                    transitionTo(mLanguageStatus);
                    break;
                case CMD_ENTER:
                    // if() // screen_moving.
                    int position = mContext.getHolder().getFocusedScreenItem();
                    if (position == 0) {
                        transitionTo(mScreenMovingStatue);
                    } else if (position == 1) {
                        transitionTo(mScreenScaleState);
                    }

                    // else // screen scale.
                    break;

                case CMD_RIGHT_BTN:
                case CMD_RIGHT:
                    transitionTo(mNetworkState);
                    break;
                case STATE_SCREEN_MOVING:
                    transitionTo(mScreenMovingStatue);
                    break;
                case STATE_SCREEN_SCALE:
                    transitionTo(mScreenScaleState);
                    break;
                case CMD_GOTO_LAGPAGE:
                	Log.e("test","from screen to language");
                	transitionTo(mLanguageStatus);
                	break;
                case CMD_GOTO_NETPAGE:
                	Log.e("test","from screen to network");
                	transitionTo(mNetworkState);
                	 break;
                case CMD_GOTO_TIMPAGE:
                	Log.e("test","from screen to timezone");
                	transitionTo(mTimeZoneState);
                	 break;
                default:
                    return NOT_HANDLED;
            }
            return HANDLED;
        }

        @Override
        public void exit() {
            super.exit();
            print("<-------destroy screen View.");
            mContext.getHolder().releaseScreenFragment();
        }
    }

    private class ScreenMovingState extends State {
        @Override
        public void enter() {
            print("------>Show screenMoving View.");
            mContext.getHolder().gotoScreenMovingFragment();
            super.enter();
        }

        @Override
        public boolean processMessage(Message msg) {
            mContext.getHolder().getScreenMovingFragment().showArrow(msg.what);
            switch (msg.what) {
                case CMD_ESC:
                    transitionTo(mScreenState);
                    break;

                case CMD_UP:
                    Utils.print(TAG, "screen moving up");
                    mScreenMovingLogic.moving(msg.what);
                    break;

                case CMD_DOWN:
                    Utils.print(TAG, "screen moving down");
                    mScreenMovingLogic.moving(msg.what);
                    break;

                case CMD_LEFT:
                    Utils.print(TAG, "screen moving left");
                    mScreenMovingLogic.moving(msg.what);
                    break;

                case CMD_RIGHT:
                    Utils.print(TAG, "screen moving right");
                    mScreenMovingLogic.moving(msg.what);
                    break;

                default:
                    return NOT_HANDLED;
            }
            return HANDLED;
        }

        @Override
        public void exit() {
            super.exit();
            print("<-------destroy screenMoving View.");
        }
    }

    private class ScreenScaleState extends State {
        @Override
        public void enter() {
            print("------>Show screenScale View.");
            mContext.getHolder().gotoScreenScaleFragment();
            Log.e("test", "val =" + mScreenMovingLogic.getCurrentScale());
            mContext.getHolder().getScreenScaleFrag().setProgress(mScreenMovingLogic.getCurrentScale());
            super.enter();
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case CMD_ESC:
                    transitionTo(mScreenState);
                    break;
                case CMD_LEFT:
                    int seekBarProgress = mContext.getHolder().getScreenScaleFrag().getProgress();
                    mContext.getHolder().getScreenScaleFrag().setProgress(--seekBarProgress);
                    int percent = seekBarProgress + 80;

                    mScreenMovingLogic.scale(percent);
                    break;

                case CMD_RIGHT:
                    seekBarProgress = mContext.getHolder().getScreenScaleFrag().getProgress();
                    mContext.getHolder().getScreenScaleFrag().setProgress(++seekBarProgress);
                    percent = seekBarProgress + 80;

                    mScreenMovingLogic.scale(percent);
                    break;

                default:
                    return NOT_HANDLED;
            }
            return HANDLED;
        }

        @Override
        public void exit() {
            super.exit();
            print("<-------destroy screenScale View.");
        }
    };

    private class NetworkState extends State {
        @Override
        public void enter() {
            print("------>Show network View.");
            mContext.getHolder().gotoNetworkFragment();
            super.enter();
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
            	case CMD_LEFT_BTN:
                case CMD_LEFT:
                case CMD_ESC:
//                    if (!mContext.getHolder().getNetworkFragment().onBackPressed()
//                            && !mContext.getHolder().getNetworkFragment().bConnecting) {
                	NetworkFragment.autoScan = true;
                	if (!mContext.getHolder().getNetworkFragment().onBackPressed()){
                        if (bDongle) {
                            transitionTo(mScreenState);
                        } else {
                            transitionTo(mLanguageStatus);
                        }
                    }
                    break;
                case CMD_RIGHT_BTN:
                case CMD_RIGHT:
//                    if (!mContext.getHolder().getNetworkFragment().bConnecting) {
                	NetworkFragment.autoScan = true;
                    transitionTo(mTimeZoneState);
//                    }
                    break;
                case CMD_GOTO_LAGPAGE:
                	Log.e("test","from network to language");
                	NetworkFragment.autoScan = true;
                	transitionTo(mLanguageStatus);
                	break;
                case CMD_GOTO_SREPAGE:
                	Log.e("test","from network to screen");
                	NetworkFragment.autoScan = true;
                	transitionTo(mScreenState);
                	break;
                case CMD_GOTO_TIMPAGE:
                	Log.e("test","from network to timezone");
                	NetworkFragment.autoScan = true;
                	transitionTo(mTimeZoneState);
                	break;
                default:
                    return NOT_HANDLED;
            }
            return HANDLED;
        }

        @Override
        public void exit() {
            super.exit();
            print("<-------destroy network View.");
            mContext.getHolder().releaseNetworkFragment();
        }
    };

    private class TimeZoneState extends State {
        @Override
        public void enter() {
            print("------>Show timezone View.");
            mContext.getHolder().gotoTimeZoneFragment();
            super.enter();
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
            	case CMD_LEFT_BTN:
                case CMD_LEFT:
                case CMD_ESC:
                    transitionTo(mNetworkState);
                    break;
                case CMD_RIGHT_BTN:
                case CMD_RIGHT:
                	WizardLogic.this.quit();
                    ((StateMachineActivity) WizardLogic.this.mContext).finishActivity();
                    // ((StateMachineActivity)
                    // WizardLogic.this.mContext).finish();eMachineActivity) WizardLogic.this.mContext).startActivity(intent);
                    break;
                case CMD_GOTO_LAGPAGE:
                	Log.e("test","from timezone to language");
                	transitionTo(mLanguageStatus);
                	break;
                case CMD_GOTO_SREPAGE:
                	Log.e("test","from timezone to screen");
                	transitionTo(mScreenState);
                	break;
                case CMD_GOTO_NETPAGE:
                	Log.e("test","from timezone to network");
                	transitionTo(mNetworkState);
                	break;
                default:
                    return NOT_HANDLED;
            }
            return HANDLED;
        }

        @Override
        public void exit() {
            super.exit();
            print("<-------destroy timezone View.");
            mContext.getHolder().releaseTimeZoneFragment();
        }
    };

    void print(String str) {
        Log.i(TAG, "" + str);
    }

    /**
     * sendMessage to switch the state
     * @param direction
     */
    public void gotoState(int direction) {
        sendMessage(direction, 0, 0);
    }

}
