
package com.eostek.scifly.ime.cangjie;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.eostek.scifly.ime.AbstractIME;
import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.common.KeyboardList;
import com.eostek.scifly.ime.common.TextEditor;
import com.eostek.scifly.ime.ui.CandidatesContainer;
import com.eostek.scifly.ime.util.Constans;
import com.eostek.scifly.ime.zhuyin.PhraseDictionary;

/**
 * input-method service.
 */
public class CangjieImeService extends AbstractIME implements CandidatesContainer.PageArrowClickListener {
    public final String serviceName = "com.eostek.scifly.ime/.cangjie.CangjieImeService"; 

    private static final String TAG = "CangjieImeService";

    private PhraseDictionary mPhraseDictionary;

    @Override
    public void initialize(Context context) {
        mPhraseDictionary = new PhraseDictionary(getApplicationContext());
    }

    @Override
    public KeyboardList createKeyboardList(Context context) {
        return new KeyboardList(context, R.xml.cangjie);
    }

    @Override
    public TextEditor createTextEditor() {
        return new CangjieEditor();
    }

    @Override
    protected WordDictionary createWordDictionary(Context context) {
        return new CangjieDictionary(context);
    }

    @Override
    public void pickSuggestionManually(int index) {
        int size = mCandidatesContainer.getSuggestions().size();
        if (size == 0 || index < 0 || index >= size) {
            return;
        }

        if (!isInCandidatesRang) {
            isInCandidatesRang = true;
        }

        StringBuilder builder = new StringBuilder(mCandidatesContainer.getSuggestions().get(index));
        mTextEditor.setComposingText(builder);
        if (builder.length() > 0) {
            commitTyped(getCurrentInputConnection());
        }
        /**
         * Method commitTyped() will clear composingText ,but we need it later
         * ,so we set it again.
         */
        mTextEditor.setComposingText(builder);

        List<String> candidates = new ArrayList<String>();

        if (mTextEditor.composingText().length() > 0) {
            String followingStr = mPhraseDictionary.getFollowingWords(mTextEditor.composingText().charAt(0));
            Constans.print(TAG, "words = " + followingStr + " words.length() " + followingStr.length());
            String word;
            for (int i = 0; i < followingStr.length(); i++) {
                if (i + 1 <= followingStr.length()) {
                    word = followingStr.substring(i, i + 1);
                    candidates.add(word);
                }
            }

            mCandidatesContainer.setCandidates(candidates, false, false);
            mCandidatesContainer.updateCharInputTip(builder.toString());
        }

        if (mInputView != null) {
            mInputView.invalidateAllKeys();
        }
        mTextEditor.setLength(0);
    }

    @Override
    protected void updateCandidates() {
        Constans.print(TAG,
                "composing.length : " + mTextEditor.composingText().length() + " composing "
                        + mTextEditor.composingText());

        mCandidatesContainer.updateCharInputTip(mTextEditor.composingText().toString());
        if (mTextEditor.composingText().length() == 0) {
            mCandidatesContainer.setCandidates(null, false, false);
        } else {
            Constans.print(TAG, "mInputView.isShifted() " + mInputView.isShifted());
            ((CangjieDictionary) mWordDictionary).setSimplified(mInputView.isShifted());
            List<String> candidateList = mWordDictionary.getWords(mTextEditor.composingText());

            if (candidateList.size() == 0) {
                candidateList.add(mTextEditor.composingText().toString());
            }
            mCandidatesContainer.setCandidates(candidateList, false, false);
        }
    }

    @Override
    public String getCurrentImeName() {
        return serviceName;
    }

}
