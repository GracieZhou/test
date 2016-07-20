
package com.eostek.tvmenu.advance;


import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.TvMenuActivity;
import com.eostek.tvmenu.TvMenuHolder;
import com.eostek.tvmenu.ui.FocusView;
import com.mstar.android.tvapi.common.TvManager;


public class AdvanceSettingFragment extends Fragment {


    private Button mSureBtn,mCancleBtn,mCleanBtn;
    
    private TextView mOldPasswordTv,mNewPasswordTv,mConfirmPasswordTv;
    
    public AdvanceSettingHolder mHolder;
    public AdvanceSettingLogic mLogic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.advance_setting_fragment, null);
        
        
        mHolder = new AdvanceSettingHolder(this);
        mLogic = new AdvanceSettingLogic(this);
        mHolder.initView(view);
        mHolder.initData();
        mHolder.setListener();
        return view;
    }
	
	/**
	 * when click passwordSetting button,show ChangePassword Dialog
	 */
	void creatChangePasswordDialog() {
		  ((TvMenuActivity) getActivity()).getHandler().removeMessages(
		          TvMenuHolder.FINISH);
		  final Dialog passwordChangeDialog = new Dialog(getActivity(), R.style.dialog);
		  passwordChangeDialog.setContentView(R.layout.password_change);
		
		  final LinearLayout oldPasswordLayout = (LinearLayout) passwordChangeDialog
		          .findViewById(R.id.old_password_layout);
		  final LinearLayout newPasswordLayout = (LinearLayout) passwordChangeDialog
		          .findViewById(R.id.new_password_layout);
		  final LinearLayout confirmPasswordLayout = (LinearLayout) passwordChangeDialog
		          .findViewById(R.id.confirm_password_layout);
		  final FocusView focusView = (FocusView) passwordChangeDialog
		          .findViewById(R.id.focus_selector);
		  final EditText oldPasswordEdt = (EditText) passwordChangeDialog
		          .findViewById(R.id.old_password);
		  final EditText newPasswordEdt = (EditText) passwordChangeDialog
		          .findViewById(R.id.new_password);
		  final EditText confirmPasswordEdt = (EditText) passwordChangeDialog
		          .findViewById(R.id.confirm_password);
		  mOldPasswordTv = (TextView)passwordChangeDialog.findViewById(R.id.old_password_tv);
		  mNewPasswordTv = (TextView)passwordChangeDialog.findViewById(R.id.new_password_tv);
		  mConfirmPasswordTv = (TextView)passwordChangeDialog.findViewById(R.id.confirm_password_tv);
		  mSureBtn = (Button) passwordChangeDialog.findViewById(R.id.sure_reset_btn);
		  mCancleBtn = (Button) passwordChangeDialog.findViewById(R.id.cancle_reset_btn);
		  mCleanBtn = (Button) passwordChangeDialog.findViewById(R.id.clean_reset_btn);
		
		  //let old password editText get focus
		  oldPasswordEdt.setFocusable(true);
		  oldPasswordEdt.requestFocus();
		  mOldPasswordTv.setTextColor(0xff0ab6a8);
	      oldPasswordEdt.setBackgroundResource(R.drawable.enumbar2_bg);
		  
		  //set OnFocusChangeListener
		  OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View view, boolean hasFocus) {

					switch (view.getId()) {
					case R.id.sure_reset_btn: {
						if (hasFocus) {
							//if has focused,change ui
							mSureBtn.setBackgroundResource(R.drawable.bar_bg_btn_cyan); 
						} else {
							//if does not have focused,change ui
							mSureBtn.setBackgroundResource(R.drawable.bar_bg_btn_grey); 
						}
					}
						break;
					case R.id.cancle_reset_btn: {
						if (hasFocus) {
							mCancleBtn.setBackgroundResource(R.drawable.bar_bg_btn_cyan);  
						} else {
							mCancleBtn.setBackgroundResource(R.drawable.bar_bg_btn_grey);  
						}
					}
						break;
					case R.id.clean_reset_btn: {
						if (hasFocus) {
							mCleanBtn.setBackgroundResource(R.drawable.bar_bg_btn_cyan);  	
						} else {
							mCleanBtn.setBackgroundResource(R.drawable.bar_bg_btn_grey);  	
						}
					}
						break;
					case R.id.old_password: {
						if (hasFocus) {
							mOldPasswordTv.setTextColor(0xff0ab6a8);
					    	oldPasswordEdt.setBackgroundResource(R.drawable.enumbar2_bg);
						} else {
							mOldPasswordTv.setTextColor(android.graphics.Color.WHITE); 
					    	oldPasswordEdt.setBackgroundResource(R.drawable.enumbar1_bg);
						}
					}
						break;
					case R.id.new_password: {
						if (hasFocus) {
							mNewPasswordTv.setTextColor(0xff0ab6a8); 	
   				    	    newPasswordEdt.setBackgroundResource(R.drawable.enumbar2_bg);
						} else {
							mNewPasswordTv.setTextColor(android.graphics.Color.WHITE); 
					    	newPasswordEdt.setBackgroundResource(R.drawable.enumbar1_bg);
						}
					}
						break;
					case R.id.confirm_password: {
						if (hasFocus) {
							mConfirmPasswordTv.setTextColor(0xff0ab6a8);  
					    	confirmPasswordEdt.setBackgroundResource(R.drawable.enumbar2_bg);
						} else {
							mConfirmPasswordTv.setTextColor(android.graphics.Color.WHITE);  
					    	confirmPasswordEdt.setBackgroundResource(R.drawable.enumbar1_bg);
						}
					}
					    break;

					}
				}
			};
		  
		  //set Items OnFocusChangeListener
		  mSureBtn.setOnFocusChangeListener(onFocusChangeListener);
		  mCancleBtn.setOnFocusChangeListener(onFocusChangeListener);
		  mCleanBtn.setOnFocusChangeListener(onFocusChangeListener);
		  oldPasswordEdt.setOnFocusChangeListener(onFocusChangeListener);
		  newPasswordEdt.setOnFocusChangeListener(onFocusChangeListener);
		  confirmPasswordEdt.setOnFocusChangeListener(onFocusChangeListener);
		  
		  
		  mSureBtn.setOnClickListener(new OnClickListener() {
		      @Override
		      public void onClick(View view) {
		          int curPassword = TvManager.getInstance().getParentalcontrolManager()
		                  .GetParentalPassword();
		          String curInputStr = oldPasswordEdt.getText().toString();
		          String newInputStr = newPasswordEdt.getText().toString();
		          String confirmInputStr = confirmPasswordEdt.getText().toString();
		          if (curInputStr == null || curInputStr.length() < 4) {
		              Toast.makeText(getActivity(), R.string.oldpasswordnullorlessthansix,
		                      Toast.LENGTH_LONG).show();
		          } else if (newInputStr == null || newInputStr.length() < 4) {
		              Toast.makeText(getActivity(), R.string.newpasswordnullorlessthansix,
		                      Toast.LENGTH_LONG).show();
		          } else if (confirmInputStr == null || confirmInputStr.length() < 4) {
		              Toast.makeText(getActivity(), R.string.confirmpasswordnullorlessthansix,
		                      Toast.LENGTH_LONG).show();
		          } else if (!(Integer.parseInt(curInputStr) == curPassword)) {
		              Toast.makeText(getActivity(), R.string.originalpassworderr, Toast.LENGTH_LONG)
		                      .show();
		          } else if (!(newInputStr.equals(confirmInputStr))) {
		              Toast.makeText(getActivity(), R.string.newconfirmpassworderr, Toast.LENGTH_LONG)
		                      .show();
		          } else if ((Integer.parseInt(curInputStr) == curPassword)
		                  && newInputStr.equals(confirmInputStr)) {
		              TvManager.getInstance().getParentalcontrolManager()
		                      .setParentalPassword(Integer.parseInt(newInputStr));
		              Toast.makeText(getActivity(), R.string.resetsuccess, Toast.LENGTH_LONG).show();
		              passwordChangeDialog.dismiss();
		              getActivity().findViewById(R.id.main).setVisibility(View.VISIBLE);
		              ((TvMenuActivity) getActivity()).getHandler()
		                      .sendEmptyMessageDelayed(TvMenuHolder.FINISH,
		                              TvMenuActivity.DIMISS_DELAY_TIME);
		          }
		      }
		  });
		  mCancleBtn.setOnClickListener(new OnClickListener() {
		      @Override
		      public void onClick(View view) {
		          passwordChangeDialog.dismiss();
		          getActivity().findViewById(R.id.main).setVisibility(View.VISIBLE);
		          ((TvMenuActivity) getActivity()).getHandler().sendEmptyMessageDelayed(
		                  TvMenuHolder.FINISH,
		                  TvMenuActivity.DIMISS_DELAY_TIME);
		      }
		  });
		  mCleanBtn.setOnClickListener(new OnClickListener() {
		      @Override
		      public void onClick(View view) {
		          oldPasswordEdt.setText("");
		          newPasswordEdt.setText("");
		          confirmPasswordEdt.setText("");
		      }
		  });
		  passwordChangeDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
		      public boolean onKey(DialogInterface arg0, int keycode, KeyEvent keyEvent) {
		          if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
		              if (keycode == KeyEvent.KEYCODE_BACK || keycode == KeyEvent.KEYCODE_MENU) {
		                  passwordChangeDialog.dismiss();
		                  getActivity().findViewById(R.id.main).setVisibility(View.VISIBLE);
		                  ((TvMenuActivity) getActivity()).getHandler()
		                  .sendEmptyMessageDelayed(TvMenuHolder.FINISH,
		                          TvMenuActivity.DIMISS_DELAY_TIME);
		              }
		          }
		          return false;
		      }
		  });
		  getActivity().findViewById(R.id.main).setVisibility(View.GONE);
		  passwordChangeDialog.show();
	}
}
