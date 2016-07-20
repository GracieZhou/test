package com.eostek.tv.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.tv.PlayerActivity;
import com.eostek.tv.R;
import com.eostek.tv.channel.ChannelListEditActivity;
import com.eostek.tv.utils.ChannelManagerExt;
import com.eostek.tv.utils.Constants;
import com.mstar.android.tv.TvParentalControlManager;
import com.mstar.android.tvapi.common.TvManager;

public class PasswordCheckDialog extends Dialog {
	public static final String LOCK = "lock";

	public static final String UNLOCK = "unlock";

	private Context mContext;

	private TextView mTitleText;

	private EditText pwdEdt;

	private LinearLayout inputPassword_ll;

	private Button sureBtn;

	private Button cancleBtn;

	private String mId = "";

	private Handler mHandler;

	public PasswordCheckDialog(Context context, String id, Handler handler) {
		super(context, R.style.dialog);
		mContext = context;
		mId = id;
		mHandler = handler;
	}

	public PasswordCheckDialog(Context context, String id) {
		super(context, R.style.dialog);
		mContext = context;
		mId = id;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_check);

		mTitleText = (TextView) findViewById(R.id.inputPassword_title_text);
		pwdEdt = (EditText) findViewById(R.id.inputPassword_editText);
		pwdEdt.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_DATETIME_VARIATION_NORMAL);

		// getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
		inputPassword_ll = (LinearLayout) findViewById(R.id.inputPassword_ll);
		sureBtn = (Button) findViewById(R.id.ok_button);
		cancleBtn = (Button) findViewById(R.id.cancel_button);
		setListener();
	}

	//
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
			pwdEdt.setTextSize(24);
			pwdEdt.setText(pwdEdt.getText().toString()
					+ (keyCode - KeyEvent.KEYCODE_0));
			break;
		case KeyEvent.KEYCODE_BACK:
			dismiss();
			return true;

			// case KeyEvent.KEYCODE_DPAD_CENTER:
			// Log.v("debug", "---->KEYCODE_DPAD_CENTER");
			// pwdEdt.setFocusable(true);
			// pwdEdt.setFocusableInTouchMode(true);
			// pwdEdt.requestFocus();
			// showSoftKeyboard();
			//
			// case KeyEvent.KEYCODE_ENTER:
			// Log.v("debug", "---->KEYCODE_ENTER");
			// case KeyEvent.KEYCODE_NUMPAD_ENTER:
			// Log.v("debug", "---->KEYCODE_NUMPAD_ENTER");
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showSoftKeyboard() {

		InputMethodManager imm = (InputMethodManager) getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.showSoftInput(pwdEdt, InputMethodManager.SHOW_FORCED);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);

	}

	private void setListener() {

		OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean haiFocus) {

				switch (view.getId()) {
				case R.id.inputPassword_editText: {
					Log.v("debug", "edittext------>" + haiFocus);

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
					Log.v("debug", "ok_button------>" + haiFocus);
					if (haiFocus) {
						sureBtn.setBackgroundResource(R.drawable.buttonbar2_bg);
					} else {
						sureBtn.setBackgroundResource(R.drawable.buttonbar1_bg);
					}
				}
					break;
				case R.id.cancel_button: {
					Log.v("debug", "cancel_button------>" + haiFocus);
					if (haiFocus) {
						cancleBtn
								.setBackgroundResource(R.drawable.buttonbar2_bg);
					} else {
						cancleBtn
								.setBackgroundResource(R.drawable.buttonbar1_bg);
					}
				}
					break;
				}
			}
		};

		// set OnFocusChangeListener
		// inputPassword_ll.setOnFocusChangeListener(onFocusChangeListener);
		pwdEdt.setOnFocusChangeListener(onFocusChangeListener);
		sureBtn.setOnFocusChangeListener(onFocusChangeListener);
		cancleBtn.setOnFocusChangeListener(onFocusChangeListener);

//		pwdEdt.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				showSoftKeyboard();
//
//			}
//		});

		sureBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int localPassword = TvManager.getInstance()
						.getParentalcontrolManager().GetParentalPassword();
				if (pwdEdt.getText().toString().length() == 0
						|| "".equals(pwdEdt.getText().toString())) {
					Toast.makeText(mContext, R.string.passwordIsNull,
							Toast.LENGTH_LONG).show();
					pwdEdt.setText("");
				} else if (pwdEdt.getText().toString().length() != 4
						|| localPassword != Integer.valueOf(pwdEdt.getText()
								.toString())) {
					Toast.makeText(mContext, R.string.passwordCheckError,
							Toast.LENGTH_LONG).show();
					pwdEdt.setText("");
				} else if (localPassword == Integer.valueOf(pwdEdt.getText()
						.toString())) {
					dismiss();
					TvParentalControlManager.getInstance().setSystemLock(true);
					if (mId.equals("lock")) {
						((ChannelListEditActivity) mContext).changeLockStatus();
					} else if (mId.equals("unlock")) {
						/* set unlock program TEMPORARY_UNLOCK to 0 */
						Settings.System.putInt(mContext.getContentResolver(),
								Constants.TEMPORARY_UNLOCK, 0);
						mHandler.sendEmptyMessage(PlayerActivity.PASSWORDTIPDISMISS);
						ChannelManagerExt.getInstance().unlockChannel();
					}
				}
			}
		});
		cancleBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
	}

}
