
package com.eostek.tv.player.dialog;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eostek.tv.player.PlayerActivity;
import com.eostek.tv.player.R;
import com.eostek.tv.player.channelManager.ChannelListEditActivity;
import com.eostek.tv.player.util.ChannelManagerExt;
import com.eostek.tv.player.util.FocusView;
import com.mstar.android.tv.TvParentalControlManager;
import com.mstar.android.tvapi.common.TvManager;

public class PasswordCheckDialog extends AlertDialog {

    private Context mContext;

    private EditText pwdEdt;

    private FocusView focusView;

    private LinearLayout inputPassword_ll;

    private Button sure;

    private Button cancle;
    
    public PasswordCheckDialog(Context context) {
        super(context, R.style.dialog);
        mContext = context;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eos_password_check);
        pwdEdt = (EditText) findViewById(R.id.inputPassword_editText);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        focusView = (FocusView) findViewById(R.id.focus_selector);
        inputPassword_ll = (LinearLayout) findViewById(R.id.inputPassword_ll);
        sure = (Button) findViewById(R.id.ok_button);
        cancle = (Button) findViewById(R.id.cancel_button);
        setListener();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
                pwdEdt.setText(pwdEdt.getText().toString() + (keyCode - KeyEvent.KEYCODE_0));
                break;
            case KeyEvent.KEYCODE_BACK:
                dismiss();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setListener() {
        FocusChangeListener focusChangeListener = new FocusChangeListener();
        inputPassword_ll.setOnFocusChangeListener(focusChangeListener);
        sure.setOnFocusChangeListener(focusChangeListener);
        cancle.setOnFocusChangeListener(focusChangeListener);
        sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int localPassword = TvManager.getInstance().getParentalcontrolManager()
                        .GetParentalPassword();
                if (pwdEdt.getText().toString().length() == 0
                        || "".equals(pwdEdt.getText().toString())) {
                    Toast.makeText(mContext, R.string.passwordIsNull, Toast.LENGTH_LONG).show();
                    pwdEdt.setText("");
                } else if (pwdEdt.getText().toString().length() != 4
                        || localPassword != Integer.valueOf(pwdEdt.getText().toString())) {
                    Toast.makeText(mContext, R.string.passwordCheckError, Toast.LENGTH_LONG).show();
                    pwdEdt.setText("");
                } else if (localPassword == Integer.valueOf(pwdEdt.getText().toString())) {
                    dismiss();
                    doLock();
                }
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    class FocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                Rect rect = new Rect();
                v.getGlobalVisibleRect(rect);
                focusView.startAnimation(v);
            }
        }
    }
    
    public void doLock() {
        TvParentalControlManager.getInstance().setSystemLock(true);
        if (mContext instanceof ChannelListEditActivity) {
            ((ChannelListEditActivity) mContext).doLock();
        } else if (mContext instanceof PlayerActivity) {
            ((PlayerActivity) mContext).getPlayerActivityHandler().sendEmptyMessage(PlayerActivity.DISMISS);
            ChannelManagerExt.getInstance().unlockChannel();
        }
    }
    
}
