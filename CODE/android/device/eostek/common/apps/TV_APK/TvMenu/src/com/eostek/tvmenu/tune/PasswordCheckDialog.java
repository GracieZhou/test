
package com.eostek.tvmenu.tune;

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
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.TvMenuActivity;
import com.eostek.tvmenu.TvMenuHolder;
import com.mstar.android.tvapi.common.TvManager;

public class PasswordCheckDialog extends AlertDialog {
    public static final String LOCK = "lock";

    public static final String UNLOCK = "unlock";

    private Activity mContext;
    
    private TextView mTitleText;

    private EditText pwdEdt;

    private LinearLayout inputPassword_ll;

    private Button sureBtn;

    private Button cancelBtn;

    private String mId = "";

    public PasswordCheckDialog(Activity context, String id) {
        super(context, R.style.dialog);
        mContext = context;
        mId = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TvMenuActivity) mContext).getHandler().removeMessages(
        		TvMenuHolder.FINISH);
        ((TvMenuActivity) mContext).getHandler().sendEmptyMessageDelayed(
        		TvMenuHolder.FINISH, TvMenuActivity.DIMISS_DELAY_TIME);
        setContentView(R.layout.password_check);
        
        mTitleText = (TextView) findViewById(R.id.inputPassword_title_text);
        pwdEdt = (EditText) findViewById(R.id.inputPassword_editText);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        inputPassword_ll = (LinearLayout) findViewById(R.id.inputPassword_ll);
        sureBtn = (Button) findViewById(R.id.ok_button);
        cancelBtn = (Button) findViewById(R.id.cancel_button);
        setListener();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        ((TvMenuActivity) mContext).getHandler().removeMessages(
        		TvMenuHolder.FINISH);
        ((TvMenuActivity) mContext).getHandler().sendEmptyMessageDelayed(
        		TvMenuHolder.FINISH, TvMenuActivity.DIMISS_DELAY_TIME);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_TV_INPUT:
                    if (cancelBtn.hasFocus()) {
                        cancelBtn.performClick();
                    } else if (sureBtn.hasFocus()) {
                        sureBtn.performClick();
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (sureBtn.hasFocus()) {
                        cancelBtn.requestFocus();
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (cancelBtn.hasFocus()) {
                        sureBtn.requestFocus();
                    }
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_UP:
                    if (sureBtn.hasFocus() || cancelBtn.hasFocus()) {
                        inputPassword_ll.requestFocus();
                    }
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                    if (inputPassword_ll.hasFocus()) {
                        sureBtn.requestFocus();
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
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setListener() {
    	
    	OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean haiFocus) {

				switch (view.getId()) {
				case R.id.inputPassword_ll: {
					if (haiFocus) {
						mTitleText.setTextColor(0xff0ab6a8);
						pwdEdt.setBackgroundResource(R.drawable.enumbar1_bg);
					} else {
						mTitleText.setTextColor(android.graphics.Color.WHITE);
						pwdEdt.setBackgroundResource(R.drawable.enumbar1_bg);
					}
				}
					break;
				case R.id.ok_button: {
					if (haiFocus) {
						sureBtn.setBackgroundResource(R.drawable.bar_bg_btn_cyan);
					} else {
						sureBtn.setBackgroundResource(R.drawable.bar_bg_btn_grey);
					}
				}
					break;
				case R.id.cancel_button: {
					if (haiFocus) {
						cancelBtn.setBackgroundResource(R.drawable.bar_bg_btn_cyan);
					} else {
						cancelBtn.setBackgroundResource(R.drawable.bar_bg_btn_grey);
					}
				}
					break;
				}
			}
    	};
    	
    	//set  OnFocusChangeListener
    	inputPassword_ll.setOnFocusChangeListener(onFocusChangeListener);
    	sureBtn.setOnFocusChangeListener(onFocusChangeListener);
    	cancelBtn.setOnFocusChangeListener(onFocusChangeListener);
        
    	//set  OnClickListener
        sureBtn.setOnClickListener(new View.OnClickListener() {
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
                        DtvManualTuningDialog dtvdialog = new DtvManualTuningDialog(mContext);
                        dtvdialog.show();
                    }
                    dismiss();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                mContext.findViewById(R.id.main).setVisibility(View.VISIBLE);
            }
        });
    }

    //start DTVAutoTunning Activity
    private void startDTVAutoTunning() {
        Intent intent = new Intent(mContext, DtvAutoTuningActivity.class);
        mContext.startActivity(intent);
        mContext.finish();
    }
}
