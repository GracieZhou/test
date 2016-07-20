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

package com.eostek.scifly.ime.cangjie;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.content.Context;
import android.util.Log;

/**
 * Reads a word-dictionary and provides word-suggestions as a list of characters
 * for the specified input.
 */
public abstract class WordDictionary {

    private final CountDownLatch loading = new CountDownLatch(1);

    private final DictionaryLoader loader;

    private final int MAX_COMPOSING = 28;

    public WordDictionary(Context context) {
        loader = null;
    }

    protected WordDictionary(Context context, int dictionaryId, int approxDictionarySize) {
        loader = new DictionaryLoader(context.getResources().openRawResource(dictionaryId), approxDictionarySize,
                loading);
        new Thread(loader).start();
    }

    protected char[][] dictionary() {
        try {
            loading.await();
        } catch (InterruptedException e) {
            Log.e("WordDictionary", "Loading is interrupted: ", e);
        }
        return loader.result();
    }

    /**
     * Returns a string containing words as suggestions for the specified input.
     * 
     * @param input should not be null.
     * @return a concatenated string of characters, or an empty string if there
     *         is no word for that input.
     */

    public abstract List<String> getWords(CharSequence input);

    public abstract boolean isPinyinDictionary();

    // public abstract DecodingInfo getDecodingInfo();

    public void chooseDecodingCandidate(int candIndexInAll) {
    }

    public boolean isCandidatesListEmpty() {
        return false;
    }

    public void preparePredicts(CharSequence cs) {
    }

    public int getOrigianlSplStr() {
        return 0;
    }

    public String getComposingStrForDisplay() {
        return null;
    }
    
    public int getMaxComposing (){
        return MAX_COMPOSING;
    }

}
