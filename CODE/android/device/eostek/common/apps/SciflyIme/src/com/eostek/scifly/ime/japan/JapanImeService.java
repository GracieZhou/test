
package com.eostek.scifly.ime.japan;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.util.Log;

import com.android.inputmethod.openwnn.ComposingText;
import com.android.inputmethod.openwnn.OpenWnnEngineJAJP;
import com.android.inputmethod.openwnn.Romkan;
import com.android.inputmethod.openwnn.StrSegment;
import com.android.inputmethod.openwnn.WnnWord;
import com.eostek.scifly.ime.AbstractIME;
import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.cangjie.WordDictionary;
import com.eostek.scifly.ime.common.KeyboardList;
import com.eostek.scifly.ime.common.TextEditor;
import com.eostek.scifly.ime.util.Constans;

public class JapanImeService extends AbstractIME {

    private static final String TAG = "JapanImeService";

    private final String mSrviceName = "com.eostek.scifly.ime/.japan.JapanImeService";

    private OpenWnnEngineJAJP mConverterJAJP = new OpenWnnEngineJAJP(null);

    @Override
    public void initialize(Context context) {

    }

    @Override
    public KeyboardList createKeyboardList(Context context) {
        return new KeyboardList(context, R.xml.japan);
    }

    @Override
    public TextEditor createTextEditor() {
        return new JapanEditor();
    }

    @Override
    protected WordDictionary createWordDictionary(Context context) {
        return new JapanDictionary(getApplicationContext());
    }

    @Override
    protected void updateCandidates() {
        List<String> mWords = new ArrayList<String>();

        Constans.print(TAG, "composing.length : " + mTextEditor.composingText().length()
                + " composing " + mTextEditor.composingText());
        String clickable = mTextEditor.composingText().toString();
        if (clickable.length() >= 1) {
            Constans.print(TAG, "clickable" + clickable);
            Log.d(TAG, "clickable-----------------------" + clickable);
            if (mTextEditor.composingText().length() == 0) {
                mCandidatesContainer.setCandidates(null, false, false);
            } else {

                int candidates = mConverterJAJP.predict(backComposingText(), 0, -1);
                if (candidates > 0) {
                    for (int i = 0; i < 100; i++) {
                        WnnWord result = mConverterJAJP.getNextCandidate();
                        if (result != null) {
                            mWords.add(result.candidate);
                        } else {
                            break;
                        }
                    }
                } else {
                    mWords.add(clickable);
                }

            }

        }
        mCandidatesContainer.setCandidates(mWords, false, false);
        mCandidatesContainer.updateCharInputTip(mTextEditor.composingText().toString());
    }

    @Override
    public String getCurrentImeName() {
        return mSrviceName;
    }

}
