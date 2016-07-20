
package com.eostek.scifly.ime.korean;

import android.content.Context;

import com.eostek.scifly.ime.AbstractIME;
import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.cangjie.WordDictionary;
import com.eostek.scifly.ime.common.KeyboardList;
import com.eostek.scifly.ime.common.TextEditor;
import com.eostek.scifly.ime.persian.PersianDictionary;
import com.eostek.scifly.ime.persian.PersianEditor;

public class KoreanImeService extends AbstractIME {
    private final String mSrviceName = "com.eostek.scifly.ime/.korean.KoreanImeService";

    private static final String TAG = "KoreanImeService";

    @Override
    public void initialize(Context context) {
        // TODO Auto-generated method stub

    }

    @Override
    public KeyboardList createKeyboardList(Context context) {
        return new KeyboardList(context, R.xml.korean);
    }

    @Override
    public TextEditor createTextEditor() {
        return new PersianEditor();
    }

    @Override
    protected WordDictionary createWordDictionary(Context context) {
        return new KoreanDictionary(getApplicationContext());
    }

    @Override
    public String getCurrentImeName() {
        return mSrviceName;
    }

}
