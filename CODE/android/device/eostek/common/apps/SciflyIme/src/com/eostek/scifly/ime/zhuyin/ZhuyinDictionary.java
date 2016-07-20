/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eostek.scifly.ime.zhuyin;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.cangjie.WordDictionary;

/**
 * Extends WordDictionary to provide zhuyin word-suggestions.
 */
public class ZhuyinDictionary extends WordDictionary {

    private static final int APPROX_DICTIONARY_SIZE = 65536;

    private static final int TONES_COUNT = ZhuyinTable.getTonesCount();

    private final int MAX_COMPOSING = 4;

    public ZhuyinDictionary(Context context) {
        super(context, R.raw.dict_zhuyin, APPROX_DICTIONARY_SIZE);
    }

    @Override
    public List<String> getWords(CharSequence input) {
        List<String> candidates = new ArrayList<String>();

        if (input == null) {
            return candidates;
        }
        // Look up the syllables index; return empty string for invalid
        // syllables.
        String[] pair = ZhuyinTable.stripTones(input.toString());
        int syllablesIndex = (pair != null) ? ZhuyinTable.getSyllablesIndex(pair[0]) : -1;
        if (syllablesIndex < 0) {
            return candidates;
        }

        // [22-initials * 39-finals] syllables array; each syllables entry
        // points to
        // a char[] containing words for that syllables.
        char[][] dictionary = dictionary();
        char[] data = (dictionary != null) ? dictionary[syllablesIndex] : null;
        if (data == null) {
            return candidates;
        }

        // Counts of words for each tone are stored in the array beginning.
        int tone = ZhuyinTable.getTones(pair[1].charAt(0));
        int length = (int) data[tone];
        if (length == 0) {
            return candidates;
        }

        int start = TONES_COUNT;
        for (int i = 0; i < tone; i++) {
            start += (int) data[i];
        }

        String words = String.copyValueOf(data, start, length);

        String word;
        for (int j = 0; j < words.length(); j++) {
            if (j + 1 <= words.length()) {
                word = words.substring(j, j + 1);
                candidates.add(word);
            }
        }

        return candidates;
    }

    @Override
    public boolean isPinyinDictionary() {
        return false;
    }

    @Override
    public int getMaxComposing() {
        return MAX_COMPOSING;
    }

}
