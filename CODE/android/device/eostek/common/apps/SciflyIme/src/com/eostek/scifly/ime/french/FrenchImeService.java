
package com.eostek.scifly.ime.french;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;

import com.android.inputmethod.latin.BinaryDictionary;
import com.android.inputmethod.latin.SuggestedWords.SuggestedWordInfo;
import com.android.inputmethod.latin.WordComposer;
import com.eostek.scifly.ime.AbstractIME;
import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.cangjie.WordDictionary;
import com.eostek.scifly.ime.common.KeyboardList;
import com.eostek.scifly.ime.common.TextEditor;
import com.eostek.scifly.ime.util.Constans;
import com.eostek.scifly.ime.zhuyin.PhraseDictionary;


public class FrenchImeService extends AbstractIME {

    private final String mSrviceName = "com.eostek.scifly.ime/.french.FrenchImeService";

    private static final String TAG = "FrenchImeService";

    private PhraseDictionary mPhraseDictionary;
    
    private final WordComposer mWordComposer = new WordComposer();

    private BinaryDictionary mBinaryDictionary;

    @Override
    public void initialize(Context context) {
        mPhraseDictionary = new PhraseDictionary(getApplicationContext());
    }

    @Override
    public KeyboardList createKeyboardList(Context context) {
        return new KeyboardList(context, R.xml.french);
    }

    @Override
    public TextEditor createTextEditor() {
        return new FrenchEditor();
    }

    @Override
    protected WordDictionary createWordDictionary(Context context) {
        return new FrenchDictionary(context);
    }

    @Override
    public void onCreate() {
        mBinaryDictionary = new BinaryDictionary(getApplicationContext());
        super.onCreate();
    }
 
    @Override
    protected void updateCandidates() {

        List<String> mWords = new ArrayList<String>();

        Constans.print(
                TAG,
                "composing.length : " + mTextEditor.composingText().length() + " composing "
                        + mTextEditor.composingText());
        String clickable = mTextEditor.composingText().toString();
        Constans.print(TAG, "clickable" + clickable);

        if (mTextEditor.composingText().length() == 0) {
            mCandidatesContainer.setCandidates(null, false, false);
        } else {
            Constans.print(TAG, "mInputView.isShifted() " + mInputView.isShifted());
            mWordComposer.setBatchInputWord(mTextEditor.composingText().toString());
            mWordComposer.setComposingWord(mTextEditor.composingText().toString());
            mWords.add(clickable);
            ArrayList<SuggestedWordInfo> candidateList = mBinaryDictionary.getSuggestions(mWordComposer, null);

            if (candidateList.size() == 0) {
                mWords.add(mTextEditor.composingText().toString());
            }
            for (SuggestedWordInfo wordInfo : candidateList) {

                mWords.add(wordInfo.mWord);
            }
            mCandidatesContainer.setCandidates(mWords, false, false);
            mCandidatesContainer.updateCharInputTip(mTextEditor.composingText().toString());
        }

    }

    @Override
    public String getCurrentImeName() {
        return mSrviceName;
    }

}
