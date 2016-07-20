
package com.mstar.tv.menu.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.mstar.android.tvapi.common.TvManager;
import com.mstar.tv.FocusView;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.ui.EosCustomSettingActivity;

public class PasswordCheckDialog extends AlertDialog {
    public static final String LOCK = "lock";

    public static final String UNLOCK = "unlock";

    private Activity mContext;

    private EditText pwdEdt;

    private FocusView focusView;

    private LinearLayout inputPassword_ll;

    private Button sure;

    private Button cancel;

    private String mId = "";

    public PasswordCheckDialog(Activity context, String id) {
        super(context, R.style.dialog);
        mContext = context;
        mId = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EosCustomSettingActivity) mContext).getHandler().removeMessages(
                EosCustomSettingActivity.DELAYFINISH);
        ((EosCustomSettingActivity) mContext).getHandler().sendEmptyMessageDelayed(
                EosCustomSettingActivity.DELAYFINISH, EosCustomSettingActivity.TODIMISSDELAYTIME);
        setContentView(R.layout.eos_password_check);
        pwdEdt = (EditText) findViewById(R.id.inputPassword_editText);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        focusView = (FocusView) findViewById(R.id.focus_selector);
        inputPassword_ll = (LinearLayout) findViewById(R.id.inputPassword_ll);
        sure = (Button) findViewById(R.id.ok_button);
        cancel = (Button) findViewById(R.id.cancel_button);
        setListener();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        ((EosCustomSettingActivity) mContext).getHandler().removeMessages(
                EosCustomSettingActivity.DELAYFINISH);
        ((EosCustomSettingActivity) mContext).getHandler().sendEmptyMessageDelayed(
                EosCustomSettingActivity.DELAYFINISH, EosCustomSettingActivity.TODIMISSDELAYTIME);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_TV_INPUT:
                    if (cancel.hasFocus()) {
                        cancel.performClick();
                    } else if (sure.hasFocus()) {
                        sure.performClick();
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (sure.hasFocus()) {
                        cancel.requestFocus();
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (cancel.hasFocus()) {
                        sure.requestFocus();
                    }
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_UP:
                    if (sure.hasFocus() || cancel.hasFocus()) {
                        inputPassword_ll.requestFocus();
                    }
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                    if (inputPassword_ll.hasFocus()) {
                        sure.requestFocus();
                    }
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
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
                mContext.findViewById(R.id.main).setVisibility(View.VISIBLE);
                ((ListView) mContext.findViewById(R.id.context_lst)).requestFocus();
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
        cancel.setOnFocusChangeListener(focusChangeListener);
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
                    if (mId.equals(ChannelManagerFragment.DIALOGID_AUTOTUNING)) {
                        startDTVAutoTunning();
                    } else if (mId.equals(ChannelManagerFragment.DIALOGID_MANUALTUNING)) {
                        DTVManualTuningDialog dtvdialog = new DTVManualTuningDialog(mContext);
                        dtvdialog.show();
                    }
                    dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                mContext.findViewById(R.id.main).setVisibility(View.VISIBLE);
                ((ListView) mContext.findViewById(R.id.context_lst)).requestFocus();
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

    private void startDTVAutoTunning() {
        Intent intent = new Intent(mContext, AutoTuningActivity.class);
        mContext.startActivity(intent);
        mContext.finish();
    }
}
