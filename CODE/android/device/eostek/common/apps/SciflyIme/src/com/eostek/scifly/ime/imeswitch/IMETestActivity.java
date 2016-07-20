package com.eostek.scifly.ime.imeswitch;

import android.app.Activity;
import android.os.Bundle;

import com.eostek.scifly.ime.R;

public class IMETestActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.ime_switch);
        IMEHolder holder = new IMEHolder(this);
        holder.getView();
        holder.itemChangeListner();
        holder.addRadioItem("搜狗输入法", "五笔输入法");
        
    }
    
}
