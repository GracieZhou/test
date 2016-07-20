
package com.eostek.scifly.ime.cangjie;

import java.util.List;

import android.content.Context;

import com.eostek.scifly.ime.InputEngineInterface;

public class CangjieEngine implements InputEngineInterface {

    private static final String TAG = "CangjieEngine";

    private CangjieDictionary mCangjieDictionary;

    private Context mContext;

    public CangjieEngine(Context context) {
        mContext = context;
        mCangjieDictionary = new CangjieDictionary(mContext);
    }

    @Override
    public void startEngine() {
    }

    @Override
    public List<String> getCandidateList(String spl) {
//        List<String> candidates = new ArrayList<String>();
//        String words = mCangjieDictionary.getWords(spl);
//        Constans.print(TAG, "words = " + words + " words.length() " + words.length());
//        String word;
//        for (int i = 0; i < words.length(); i++) {
//            if (i + 1 <= words.length()) {
//                word = words.substring(i, i + 1);
//                candidates.add(word);
//            }
//        }
        return mCangjieDictionary.getWords(spl);
    }

    @Override
    public void addSplString(String spl) {
    }

    public void setSimplified(boolean simplified) {
        this.mCangjieDictionary.setSimplified(simplified);
    }
    

}
