
package com.eostek.scifly.ime.ui;

import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.eostek.scifly.ime.AbstractIME;
import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.util.Constans;

public class ImeSelectDialog extends AlertDialog {
    private static final String TAG = "ImeSelectDialog";

    private AbstractIME mContext;

    private InputMethodManager imm;

    private PackageManager mPkgManager;
    
    private  List<InputMethodInfo> imes ;
    
    public ImeSelectDialog(AbstractIME context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mPkgManager = mContext.getPackageManager();

        // LayoutInflater inflater = (LayoutInflater)
        // mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // final View view = inflater.inflate(R.layout.ime_switch, null);
        setContentView(R.layout.ime_switch);
        RadioGroup mGroup = (RadioGroup) findViewById(R.id.item_group);

       // List<InputMethodInfo> imes = imm.getInputMethodList();
       
       String platfrom = Build.DEVICE;
        if(platfrom.equals("BenQ_i500w")){
             imes = imm.getEnabledInputMethodList();
        }else {
            imes = imm.getInputMethodList();
        }
        
        for (InputMethodInfo info : imes) {
            String serviceId = info.getId();
           Log.d(TAG,"--------------"+serviceId);

            RadioButton radioButton = new RadioButton(mContext);
            String label = info.loadLabel(mPkgManager).toString();

            radioButton.setTextSize(mContext.getResources().getDimension(R.dimen.ime_switch_small));// FIXME
            radioButton.setTextColor(mContext.getResources().getColor(R.color.content_color));
            radioButton.setText(label);
            int id = mGroup.getChildCount();
            radioButton.setId(mGroup.getChildCount());
            String serviceName = info.getId();
            if (serviceName.equals(mContext.getCurrentImeName())) {
                radioButton.setChecked(true);
                radioButton.requestFocus();
            }

            if (mContext.isLocalIME(serviceId)) {
            	Constans.print(TAG, "serviceId:" + serviceId);
                radioButton.setClickable(true);
                mGroup.addView((View) radioButton, LinearLayout.LayoutParams.MATCH_PARENT, (int) mContext.getResources()
                        .getDimension(R.dimen.padding_gap));
                mContext.mLocalIMEs.put(id, info.getId());
            } else {
                radioButton.setClickable(false);
                radioButton.setEnabled(false);
                radioButton.setTextColor(mContext.getResources().getColor(R.color.content_color_g));
            }
            mGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int arg1) {

                    String serviceName = mContext.mLocalIMEs.get(group.getCheckedRadioButtonId());
                    if (!TextUtils.isEmpty(serviceName)) {
                        mContext.switchIme(serviceName);
                        dismiss();
                    }

                }

            });

            try {

                Window window = getWindow();
                LayoutParams param = window.getAttributes();
                param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                // param.privateFlags |=
                // WindowManager.LayoutParams.PRIVATE_FLAG_SHOW_FOR_ALL_USERS;

                param.gravity = Gravity.LEFT | Gravity.BOTTOM;
                int[] size = mContext.getLayoutSize();
                param.width = size[0];
                param.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;// size[1];

                int[] position = mContext.getPosition();
                param.x = position[0];
                param.y = position[1];

                window.setAttributes(param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
