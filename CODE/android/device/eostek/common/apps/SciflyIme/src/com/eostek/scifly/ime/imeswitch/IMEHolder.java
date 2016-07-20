
package com.eostek.scifly.ime.imeswitch;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.util.Constans;

public class IMEHolder {

    private TextView mTitle;

    private TextView mHorizon;

//    private RadioButton mItem_pinyin;
//
//    private RadioButton mItem_zhuyin;
//
//    private RadioButton mItem_cangjie;

    private RadioGroup mGroup;
    
    private Activity mActivity;

    public IMEHolder(Activity mActivity) {
        super();
        this.mActivity = mActivity;
    }

    public void getView() {
        mTitle = (TextView) mActivity.findViewById(R.id.title);
        mHorizon = (TextView) mActivity.findViewById(R.id.horizon);
//        mItem_pinyin = (RadioButton) mActivity.findViewById(R.id.item_pinyin);
//        mItem_zhuyin = (RadioButton) mActivity.findViewById(R.id.item_zhuyin);
//        mItem_cangjie = (RadioButton) mActivity.findViewById(R.id.item_cangjie);
        mGroup = (RadioGroup) mActivity.findViewById(R.id.item_group);
    }

    public void itemChangeListner() {
        
        mGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int arg1) {
                
                RadioButton radio = (RadioButton)mActivity.findViewById(group.getCheckedRadioButtonId());
                Constans.print("test", radio.getText().toString());
            }
            
        });
        
    }
    
    public void addRadioItem(String... radioText){
        
        for (String s : radioText){
           
            RadioButton radioButton = new RadioButton(mActivity);
            radioButton.setText(s);
            radioButton.setTextColor(mActivity.getResources().getColor(R.color.content_color));
            radioButton.setId(mGroup.getChildCount());
            mGroup.addView((View)radioButton, LinearLayout.LayoutParams.MATCH_PARENT, 30);
            
        }
        
    }
    
    public TextView getmTitle() {
        return mTitle;
    }

    public void setmTitle(TextView mTitle) {
        this.mTitle = mTitle;
    }

    public TextView getmHorizon() {
        return mHorizon;
    }

    public void setmHorizon(TextView mHorizon) {
        this.mHorizon = mHorizon;
    }

//    public RadioButton getmItem_pinyin() {
//        return mItem_pinyin;
//    }
//
//    public void setmItem_pinyin(RadioButton mItem_pinyin) {
//        this.mItem_pinyin = mItem_pinyin;
//    }
//
//    public RadioButton getmItem_zhuyin() {
//        return mItem_zhuyin;
//    }
//
//    public void setmItem_zhuyin(RadioButton mItem_zhuyin) {
//        this.mItem_zhuyin = mItem_zhuyin;
//    }
//
//    public RadioButton getmItem_cangjie() {
//        return mItem_cangjie;
//    }
//
//    public void setmItem_cangjie(RadioButton mItem_cangjie) {
//        this.mItem_cangjie = mItem_cangjie;
//    }

    public RadioGroup getmGroup() {
        return mGroup;
    }

    public void setmGroup(RadioGroup mGroup) {
        this.mGroup = mGroup;
    }

}
