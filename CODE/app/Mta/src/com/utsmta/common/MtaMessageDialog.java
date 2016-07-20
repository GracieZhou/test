package com.utsmta.common;

import com.utsmta.app.R;
import com.utsmta.utils.LogUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MtaMessageDialog extends DialogFragment {
	private final String TAG = "MtaMessageDialog";
	
	public static final int STYLE_NORMAL 	= 0x00;
	
	public static final int STYLE_POSITIVE 	= 0x01;
	
	public static final int STYLE_NEGATIVE 	= 0x02;
	
	public static final int STYLE_NONE = 0x03;
	
	private String message = null;
	
	private String positiveButtonTitle = null;
	
	private String negativeButtonTitle = null;
	
	private int style = STYLE_NORMAL;
	
	private OnDialogButtonClickListener buttonClickListener = null;
	
	public interface OnDialogButtonClickListener{
		public void OnPositiveButtonClick(DialogInterface dialog);
		
		public void OnNegativeButtonClick(DialogInterface dialog);
	}
	
	public MtaMessageDialog(String message, int style, String positiveButtonTitle, String negativeButtonTitle) {
		// TODO Auto-generated constructor stub
		this.message = message;
		this.positiveButtonTitle = positiveButtonTitle;
		this.negativeButtonTitle = negativeButtonTitle;
		this.style = style;
	}
	
	public MtaMessageDialog(String message, int style, String positiveButtonTitle, String negativeButtonTitle,
			OnDialogButtonClickListener buttonClickListener) {
		// TODO Auto-generated constructor stub
		this.message = message;
		this.positiveButtonTitle = positiveButtonTitle;
		this.negativeButtonTitle = negativeButtonTitle;
		this.style = style;
		this.buttonClickListener = buttonClickListener;
	}
	
	public void setButtonClickListener(OnDialogButtonClickListener buttonClickListener){
		this.buttonClickListener = buttonClickListener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onCreateDialog");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  
		
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.message_dialog, null);
		TextView messageTv = (TextView) view.findViewById(R.id.message);		
		messageTv.setText(message);
		messageTv.setTextColor(R.color.red);
		
		builder.setView(view);
		
		if((STYLE_NEGATIVE & style) != STYLE_NEGATIVE){
			builder.setPositiveButton(positiveButtonTitle, 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if(buttonClickListener != null){
								buttonClickListener.OnPositiveButtonClick(dialog);
							}else{
								dismiss();
							}					
						}
					});			
		}
		
		if((STYLE_POSITIVE & style) != STYLE_POSITIVE){
			builder.setNegativeButton(negativeButtonTitle, 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if(buttonClickListener != null){
								buttonClickListener.OnNegativeButtonClick(dialog);
							}else{
								dismiss();
							}					
						}
					});			
		}
		
		return builder.create();
	}
}
