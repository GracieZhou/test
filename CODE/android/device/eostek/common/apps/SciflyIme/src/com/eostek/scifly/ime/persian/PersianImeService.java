package com.eostek.scifly.ime.persian;

import android.content.Context;

import com.eostek.scifly.ime.AbstractIME;
import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.cangjie.WordDictionary;
import com.eostek.scifly.ime.common.KeyboardList;
import com.eostek.scifly.ime.common.TextEditor;

public class PersianImeService  extends AbstractIME{

  private final String mSrviceName = "com.eostek.scifly.ime/.persian.PersianImeService";
    
    private static final String TAG = "PersianImeService";
    @Override
    public void initialize(Context context) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public KeyboardList createKeyboardList(Context context) {
        return  new KeyboardList(context, R.xml.persian);
    }

    @Override
    public TextEditor createTextEditor() {
        return  new PersianEditor();
    }

    @Override
    protected WordDictionary createWordDictionary(Context context) {
        return new PersianDictionary(getApplicationContext());
    }

    @Override
    public String getCurrentImeName() {
        return mSrviceName;
    }

}
