
package com.eostek.scifly.ime.zhuyin;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.eostek.scifly.ime.InputEngineInterface;

public class ZhuyinEngine implements InputEngineInterface {

    private ZhuyinDictionary mZhuyinDictionary;

    private PhraseDictionary mPhraseDictionary;

    public ZhuyinEngine(Context mContext) {
        mZhuyinDictionary = new ZhuyinDictionary(mContext);
        mPhraseDictionary = new PhraseDictionary(mContext);
    }

    @Override
    public void startEngine() {

    }

    @Override
    public List<String> getCandidateList(String spl) {
//        List<String> candidates = new ArrayList<String>();
//        String words = mZhuyinDictionary.getWords(spl);
//        String word;
//        for (int i = 0; i < words.length(); i++) {
//            if (i + 1 <= words.length()) {
//                word = words.substring(i, i + 1);
//                candidates.add(word);
//            }
//        }
        return mZhuyinDictionary.getWords(spl);
    }

    public List<String> getFollowingWords(char c) {
        List<String> candidates = new ArrayList<String>();
        String words = mPhraseDictionary.getFollowingWords(c);
        String word;
        for (int i = 0; i < words.length(); i++) {
            if (i + 1 <= words.length()) {
                word = words.substring(i, i + 1);
                candidates.add(word);
            }
        }
        return candidates;
    }

    @Override
    public void addSplString(String spl) {
    }

}
