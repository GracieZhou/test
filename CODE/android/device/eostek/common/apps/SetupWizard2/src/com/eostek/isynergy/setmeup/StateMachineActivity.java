
package com.eostek.isynergy.setmeup;

import com.eostek.isynergy.setmeup.ui.SetmeupMainActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class StateMachineActivity extends Activity {

    private static final String TAG = StateMachineActivity.class.getSimpleName();

    WizardLogic mMyStateMachine;

    WizardHolder mHolder;

    public static final int SHOW_LEFT_ARROW = 0;

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == SHOW_LEFT_ARROW) {
                mHolder.leftBtn.setVisibility(View.VISIBLE);
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.statemachine_activity);
        getWindow().setBackgroundDrawable(null);
        this.mHolder = new WizardHolder(this);
        this.mMyStateMachine = new WizardLogic(this, "setupWizard");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent arg1) {
        Log.e(TAG, "onKeyDown:" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                mMyStateMachine.gotoState(WizardLogic.CMD_UP);
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                mMyStateMachine.gotoState(WizardLogic.CMD_DOWN);
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                mMyStateMachine.gotoState(WizardLogic.CMD_LEFT);
                mHolder.showKeyDown(true);
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mMyStateMachine.gotoState(WizardLogic.CMD_RIGHT);
                mHolder.showKeyDown(false);
                break;

            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                mMyStateMachine.gotoState(WizardLogic.CMD_ENTER);
                break;

            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_ESCAPE:
                mMyStateMachine.gotoState(WizardLogic.CMD_ESC);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, arg1);
    }

    /**
     * goto the state NetworkState ScreenScaleState ScreenMovingState
     * TimeZoneState LanguageState ScreenState
     * 
     * @param state
     */
    public void gotoState(int state) {
        mMyStateMachine.gotoState(state);
    }

    /**
     * get WizardHolder
     * 
     * @return
     */
    public WizardHolder getHolder() {
        return mHolder;
    }

    @SuppressLint("NewApi")
    public void finishActivity() {
        PackageManager pm = getPackageManager();
        ComponentName name = new ComponentName(this, StateMachineActivity.class);
        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        ComponentName setmeup = new ComponentName(this, SetmeupMainActivity.class);
        pm.setComponentEnabledSetting(setmeup, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        // Add a persistent setting to allow other apps to know the
        // device has been provisioned.
        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
        Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);
        finish();
    }

}
