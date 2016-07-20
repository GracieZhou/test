/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.android.inputmethod.latin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.android.inputmethod.keyboard.ProximityInfo;
import com.android.inputmethod.latin.SuggestedWords.SuggestedWordInfo;
import com.android.inputmethod.utils.CollectionUtils;
import com.android.inputmethod.utils.JniUtils;
import com.android.inputmethod.utils.StringUtils;
import com.eostek.scifly.ime.R;

/**
 * Implements a static, compacted, binary dictionary of standard words.
 */
// TODO: All methods which should be locked need to have a suffix "Locked".
public final class BinaryDictionary {
    static {
        JniUtils.loadNativeLibrary();
    }

    private static final String TAG = BinaryDictionary.class.getSimpleName();

    public static final int NOT_A_PROBABILITY = -1;

    private static final String RESOURCE_PACKAGE_NAME = R.class.getPackage().getName();

    private static final String MAIN_DICT_PREFIX = "main_";

    // The cutoff returned by native for auto-commit confidence.
    // Must be equal to CONFIDENCE_TO_AUTO_COMMIT in native/jni/src/defines.h
    private static final int CONFIDENCE_TO_AUTO_COMMIT = 1000000;

    private final ReentrantReadWriteLock mLock = new ReentrantReadWriteLock();

    private AssetFileAddress mAssetFileAddress;

    private final NativeSuggestOptions mNativeSuggestOptions = new NativeSuggestOptions();

    private ProximityInfo mProximityInfo = new ProximityInfo();

    // Must be equal to MAX_WORD_LENGTH in native/jni/src/defines.h
    private static final int MAX_WORD_LENGTH = Constants.DICTIONARY_MAX_WORD_LENGTH;

    // Must be equal to MAX_RESULTS in native/jni/src/defines.h
    private static final int MAX_RESULTS = 18;

    // The cutoff returned by native for auto-commit confidence.
    // Must be equal to CONFIDENCE_TO_AUTO_COMMIT in native/jni/src/defines.h
    private final int[] mInputCodePoints = new int[MAX_WORD_LENGTH];

    private final int[] mOutputCodePoints = new int[MAX_WORD_LENGTH * MAX_RESULTS];

    private final int[] mSpaceIndices = new int[MAX_RESULTS];

    private final int[] mOutputScores = new int[MAX_RESULTS];

    private final int[] mOutputTypes = new int[MAX_RESULTS];

    // Only one result is ever used
    private final int[] mOutputAutoCommitFirstWordConfidence = new int[1];

    private long mNativeDict;

    private final SparseArray<DicTraverseSession> mDicTraverseSessions = CollectionUtils.newSparseArray();

    private Locale mLocale = new Locale("fr", "FR");

    // TODO: There should be a way to remove used DicTraverseSession objects
    // from
    // {@code mDicTraverseSessions}.
    private DicTraverseSession getTraverseSession(final int traverseSessionId) {
        synchronized (mDicTraverseSessions) {
            DicTraverseSession traverseSession = mDicTraverseSessions.get(traverseSessionId);
            if (traverseSession == null) {
                traverseSession = mDicTraverseSessions.get(traverseSessionId);
                if (traverseSession == null) {
                    traverseSession = new DicTraverseSession(mLocale, mNativeDict, mAssetFileAddress.mLength);
                    mDicTraverseSessions.put(traverseSessionId, traverseSession);
                }
            }
            return traverseSession;
        }
    }

    private static native boolean createEmptyDictFileNative(String filePath, long dictVersion,
            String[] attributeKeyStringArray, String[] attributeValueStringArray);

    private static native long openNative(String sourceDir, long dictOffset, long dictSize, boolean isUpdatable);

    private static native void flushNative(long dict, String filePath);

    private static native boolean needsToRunGCNative(long dict, boolean mindsBlockByGC);

    private static native void flushWithGCNative(long dict, String filePath);

    private static native void closeNative(long dict);

    private static native int getProbabilityNative(long dict, int[] word);

    private static native int getBigramProbabilityNative(long dict, int[] word0, int[] word1);

    private static native int getSuggestionsNative(long dict, long proximityInfo, long traverseSession,
            int[] xCoordinates, int[] yCoordinates, int[] times, int[] pointerIds, int[] inputCodePoints,
            int inputSize, int commitPoint, int[] suggestOptions, int[] prevWordCodePointArray, int[] outputCodePoints,
            int[] outputScores, int[] outputIndices, int[] outputTypes, int[] outputAutoCommitFirstWordConfidence);

    private static native float calcNormalizedScoreNative(int[] before, int[] after, int score);

    private static native int editDistanceNative(int[] before, int[] after);

    private static native void addUnigramWordNative(long dict, int[] word, int probability);

    private static native void addBigramWordsNative(long dict, int[] word0, int[] word1, int probability);

    private static native void removeBigramWordsNative(long dict, int[] word0, int[] word1);

    private static native int calculateProbabilityNative(long dict, int unigramProbability, int bigramProbability);

    private static native String getPropertyNative(long dict, String query);

    public BinaryDictionary(Context context) {
        int fallbackResId = getMainDictionaryResourceId(context.getResources(), mLocale);
        mAssetFileAddress = loadFallbackResource(context, fallbackResId);
        loadDictionary(mAssetFileAddress.mFilename, mAssetFileAddress.mOffset, mAssetFileAddress.mLength, false);
    }

    // //////////////////////////////////////////////////////////////////////////
    // API Begin

    public int getFrequency(final String word) {
        if (word == null)
            return NOT_A_PROBABILITY;

        if (mLock.readLock().tryLock()) {
            try {
                int[] codePoints = StringUtils.toCodePointArray(word);
                return getProbabilityNative(mNativeDict, codePoints);
            } finally {
                mLock.readLock().unlock();
            }
        }
        return NOT_A_PROBABILITY;
    }

    public boolean isValidWord(final String word) {
        return getFrequency(word) != NOT_A_PROBABILITY;
    }

    public ArrayList<SuggestedWordInfo> getSuggestions(final WordComposer composer, final String prevWord) {

        return getSuggestionsWithSessionId(composer, prevWord, mProximityInfo, false, null, 0 /* sessionId */);
    }

    public ArrayList<SuggestedWordInfo> getSuggestionsWithSessionId(final WordComposer composer, final String prevWord,
            final ProximityInfo proximityInfo, final boolean blockOffensiveWords,
            final int[] additionalFeaturesOptions, final int sessionId) {
        if (!isValidDictionary())
            return null;

        Arrays.fill(mInputCodePoints, Constants.NOT_A_CODE);
        // TODO: toLowerCase in the native code
        final int[] prevWordCodePointArray = (null == prevWord) ? null : StringUtils.toCodePointArray(prevWord);
        final int composerSize = composer.size();

        final boolean isGesture = composer.isBatchMode();
        if (composerSize <= 1 || !isGesture) {
            if (composerSize > MAX_WORD_LENGTH - 1)
                return null;
            for (int i = 0; i < composerSize; i++) {
                mInputCodePoints[i] = composer.getCodeAt(i);
            }
        }

        final InputPointers ips = composer.getInputPointers();
        final int inputSize = isGesture ? ips.getPointerSize() : composerSize;
        mNativeSuggestOptions.setIsGesture(isGesture);
        mNativeSuggestOptions.setAdditionalFeaturesOptions(additionalFeaturesOptions);
        // proximityInfo and/or prevWordForBigrams may not be null.
        Log.d(TAG, "---> proximityInfo.getNativeProximityInfo=" + proximityInfo.getNativeProximityInfo());
        Log.d(TAG, "InputPointers:: " + ips.toString());
        final int count = getSuggestionsNative(mNativeDict, proximityInfo.getNativeProximityInfo(),
                getTraverseSession(sessionId).getSession(), ips.getXCoordinates(), ips.getYCoordinates(),
                ips.getTimes(), ips.getPointerIds(), mInputCodePoints, inputSize, 0 /* commitPoint */,
                mNativeSuggestOptions.getOptions(), prevWordCodePointArray, mOutputCodePoints, mOutputScores,
                mSpaceIndices, mOutputTypes, mOutputAutoCommitFirstWordConfidence);
        final ArrayList<SuggestedWordInfo> suggestions = CollectionUtils.newArrayList();
        for (int j = 0; j < count; ++j) {
            final int start = j * MAX_WORD_LENGTH;
            int len = 0;
            while (len < MAX_WORD_LENGTH && mOutputCodePoints[start + len] != 0) {
                ++len;
            }
            if (len > 0) {
                final int flags = mOutputTypes[j] & SuggestedWordInfo.KIND_MASK_FLAGS;
                if (blockOffensiveWords && 0 != (flags & SuggestedWordInfo.KIND_FLAG_POSSIBLY_OFFENSIVE)
                        && 0 == (flags & SuggestedWordInfo.KIND_FLAG_EXACT_MATCH)) {
                    // If we block potentially offensive words, and if the word
                    // is possibly
                    // offensive, then we don't output it unless it's also an
                    // exact match.
                    continue;
                }
                final int kind = mOutputTypes[j] & SuggestedWordInfo.KIND_MASK_KIND;
                final int score = SuggestedWordInfo.KIND_WHITELIST == kind ? SuggestedWordInfo.MAX_SCORE
                        : mOutputScores[j];
                // TODO: check that all users of the `kind' parameter are ready
                // to accept
                // flags too and pass mOutputTypes[j] instead of kind
                suggestions.add(new SuggestedWordInfo(new String(mOutputCodePoints, start, len), score, kind,
                        this /* sourceDict */, mSpaceIndices[j] /* indexOfTouchPointOfSecondWord */,
                        mOutputAutoCommitFirstWordConfidence[0]));
            }
        }
        return suggestions;
    }

    public boolean isValidDictionary() {
        return mNativeDict != 0;
    }

    // TODO: Add a batch process version (isValidBigramMultiple?) to avoid
    // excessive numbers of jni
    // calls when checking for changes in an entire dictionary.
    public boolean isValidBigram(final String word0, final String word1) {
        return getBigramProbability(word0, word1) != NOT_A_PROBABILITY;
    }

    public int getBigramProbability(final String word0, final String word1) {
        if (TextUtils.isEmpty(word0) || TextUtils.isEmpty(word1))
            return NOT_A_PROBABILITY;
        final int[] codePoints0 = StringUtils.toCodePointArray(word0);
        final int[] codePoints1 = StringUtils.toCodePointArray(word1);
        return getBigramProbabilityNative(mNativeDict, codePoints0, codePoints1);
    }

    // Add a unigram entry to binary dictionary in native code.
    public void addUnigramWord(final String word, final int probability) {
        if (TextUtils.isEmpty(word)) {
            return;
        }
        final int[] codePoints = StringUtils.toCodePointArray(word);
        addUnigramWordNative(mNativeDict, codePoints, probability);
    }

    // Add a bigram entry to binary dictionary in native code.
    public void addBigramWords(final String word0, final String word1, final int probability) {
        if (TextUtils.isEmpty(word0) || TextUtils.isEmpty(word1)) {
            return;
        }
        final int[] codePoints0 = StringUtils.toCodePointArray(word0);
        final int[] codePoints1 = StringUtils.toCodePointArray(word1);
        addBigramWordsNative(mNativeDict, codePoints0, codePoints1, probability);
    }

    // Remove a bigram entry form binary dictionary in native code.
    public void removeBigramWords(final String word0, final String word1) {
        if (TextUtils.isEmpty(word0) || TextUtils.isEmpty(word1)) {
            return;
        }
        final int[] codePoints0 = StringUtils.toCodePointArray(word0);
        final int[] codePoints1 = StringUtils.toCodePointArray(word1);
        removeBigramWordsNative(mNativeDict, codePoints0, codePoints1);
    }

    private void reopen() {
        close();
        mNativeDict = openNative(mAssetFileAddress.mFilename, mAssetFileAddress.mOffset /* startOffset */,
                mAssetFileAddress.mLength, true /* isUpdatable */);
    }

    public void flush() {
        if (!isValidDictionary())
            return;
        flushNative(mNativeDict, mAssetFileAddress.mFilename);
        reopen();
    }

    public void flushWithGC() {
        if (!isValidDictionary())
            return;
        flushWithGCNative(mNativeDict, mAssetFileAddress.mFilename);
        reopen();
    }

    /**
     * Checks whether GC is needed to run or not.
     * 
     * @param mindsBlockByGC Whether to mind operations blocked by GC. We don't
     *            need to care about the blocking in some situations such as in
     *            idle time or just before closing.
     * @return whether GC is needed to run or not.
     */
    public boolean needsToRunGC(final boolean mindsBlockByGC) {
        if (!isValidDictionary())
            return false;
        return needsToRunGCNative(mNativeDict, mindsBlockByGC);
    }

    public boolean shouldAutoCommit(final SuggestedWordInfo candidate) {
        return candidate.mAutoCommitFirstWordConfidence > CONFIDENCE_TO_AUTO_COMMIT;
    }

    public void close() {
        synchronized (mDicTraverseSessions) {
            final int sessionsSize = mDicTraverseSessions.size();
            for (int index = 0; index < sessionsSize; ++index) {
                final DicTraverseSession traverseSession = mDicTraverseSessions.valueAt(index);
                if (traverseSession != null) {
                    traverseSession.close();
                }
            }
            mDicTraverseSessions.clear();
        }
        closeInternalLocked();
    }

    private synchronized void closeInternalLocked() {
        if (mNativeDict != 0) {
            closeNative(mNativeDict);
            mNativeDict = 0;
        }
    }

    // TODO: Manage BinaryDictionary instances without using WeakReference or
    // something.
    @Override
    protected void finalize() throws Throwable {
        try {
            closeInternalLocked();
        } finally {
            super.finalize();
        }
    }

    // API End

    // TODO: Move native dict into session
    private final void loadDictionary(final String path, final long startOffset, final long length,
            final boolean isUpdatable) {
        Log.d(TAG, "loadDictionary : " + path + ", " + startOffset + ", " + length);
        mNativeDict = openNative(path, startOffset, length, isUpdatable);
    }

    /**
     * Returns a file address from a resource, or null if it cannot be opened.
     */
    public static AssetFileAddress loadFallbackResource(final Context context, final int fallbackResId) {
        final AssetFileDescriptor afd = context.getResources().openRawResourceFd(fallbackResId);
        if (afd == null) {
            Log.e(TAG, "Found the resource but cannot read it. Is it compressed? resId=" + fallbackResId);
            return null;
        }
        try {
            return AssetFileAddress.makeFromFileNameAndOffset(context.getApplicationInfo().sourceDir,
                    afd.getStartOffset(), afd.getLength());
        } finally {
            try {
                afd.close();
            } catch (IOException e) {
                // Ignored
            }
        }
    }

    public static int getMainDictionaryResourceId(final Resources res, final Locale locale) {
        int resId;
        // Try to find main_language_country dictionary.
        if (!locale.getCountry().isEmpty()) {
            final String dictLanguageCountry = MAIN_DICT_PREFIX + locale.toString().toLowerCase(Locale.ROOT);
            if ((resId = res.getIdentifier(dictLanguageCountry, "raw", RESOURCE_PACKAGE_NAME)) != 0) {
                return resId;
            }
        }

        // Try to find main_language dictionary.
        final String dictLanguage = MAIN_DICT_PREFIX + locale.getLanguage();
        if ((resId = res.getIdentifier(dictLanguage, "raw", RESOURCE_PACKAGE_NAME)) != 0) {
            return resId;
        }

        // Not found, return 0
        return 0;
    }

}
