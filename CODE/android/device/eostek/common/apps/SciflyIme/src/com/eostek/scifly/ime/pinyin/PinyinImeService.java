
package com.eostek.scifly.ime.pinyin;

import android.content.Context;

import com.eostek.scifly.ime.AbstractIME;
import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.cangjie.WordDictionary;
import com.eostek.scifly.ime.common.KeyboardList;
import com.eostek.scifly.ime.common.TextEditor;

public class PinyinImeService extends AbstractIME {
    
    private final String serviceName = "com.eostek.scifly.ime/.pinyin.PinyinImeService"; 

    @Override
    public void initialize(Context context) {
        // TODO Auto-generated method stub

    }

    @Override
    public KeyboardList createKeyboardList(Context context) {
        return new KeyboardList(context, R.xml.chinese);
    }

    @Override
    public TextEditor createTextEditor() {
        return new PinyinEditor();
    }

    @Override
    protected WordDictionary createWordDictionary(Context context) {
        return new PinyinDictionary(context);
    }

    @Override
    public String getCurrentImeName() {
        return serviceName;
    }

}
