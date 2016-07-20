package com.eostek.scifly.ime.persian;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.cangjie.WordDictionary;

public class PersianDictionary extends WordDictionary {

    private final String TAG = "persianDictionary";
    
    private static final int APPROX_DICTIONARY_SIZE = 65536;
    public PersianDictionary(Context context) {
        super(context, R.raw.main_fr, APPROX_DICTIONARY_SIZE);
    
    }

    @Override
    public List<String> getWords(CharSequence input) {
        ArrayList<String> candidates = new ArrayList<String>();
//        list.add("À");
        Log.d(TAG, "input:" + String.valueOf(input));
        if (input == null) {
            return candidates;
        }

        char[][] dictionary = dictionary();
        if (dictionary == null || dictionary.length < 0) {
            Log.d(TAG, "dictionary.length < 0");
            return candidates;
        }
        
        String word = "";
        for (int i = 0; i < dictionary.length; i++) {
            word = "";
            for (int j = 0; j < dictionary[i].length; j++) {
                word += String.valueOf(dictionary[i][j]);
            }
            Log.d(TAG, "word:" + word);
            candidates.add(word);
        }
        return candidates;
    }

    @Override
    public boolean isPinyinDictionary() {
        // TODO Auto-generated method stub
        return false;
    }

}
