
package com.eostek.scifly.ime.pinyin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import android.os.RemoteException;
import android.util.Log;
import android.view.inputmethod.CompletionInfo;

import com.eostek.scifly.ime.util.Constans;

public class DecodingInfo {

    private static String TAG = "DecodingInfo";

    public static ImeState mImeState = ImeState.STATE_IDLE;

    /**
     * Maximum length of the Pinyin string
     */
    private static final int PY_STRING_MAX = Constans.PY_STRING_MAX;

    /**
     * Maximum number of candidates to display in one page.
     */
    private static final int MAX_PAGE_SIZE_DISPLAY = 2000;

    /**
     * Spelling (Pinyin) string.
     */
    private StringBuffer mSurface;

    /**
     * Byte buffer used as the Pinyin string parameter for native function call.
     */
    private byte mPyBuf[];

    /**
     * The length of surface string successfully decoded by engine.
     */
    private int mSurfaceDecodedLen;

    /**
     * Composing string.
     */
    private String mComposingStr;

    /**
     * Length of the active composing string.
     */
    private int mActiveCmpsLen;

    /**
     * Composing string for display, it is copied from mComposingStr, and add
     * spaces between spellings.
     **/
    private String mComposingStrDisplay;

    /**
     * Length of the active composing string for display.
     */
    private int mActiveCmpsDisplayLen;

    /**
     * The first full sentence choice.
     */
    private String mFullSent;

    /**
     * Number of characters which have been fixed.
     */
    private int mFixedLen;

    /**
     * If this flag is true, selection is finished.
     */
    private boolean mFinishSelection;

    /**
     * The starting position for each spelling. The first one is the number of
     * the real starting position elements.
     */
    private int mSplStart[];

    /**
     * Editing cursor in mSurface.
     */
    private int mCursorPos;

    /**
     * Remote Pinyin-to-Hanzi decoding engine service.
     */
    public IPinyinDecoderService mIPinyinDecoderService;

    /**
     * The complication information suggested by application.
     */
    private CompletionInfo[] mAppCompletions;

    /**
     * The total number of choices for display. The list may only contains the
     * first part. If user tries to navigate to next page which is not in the
     * result list, we need to get these items.
     **/
    public int mTotalChoicesNum;

    /**
     * Candidate list. The first one is the full-sentence candidate.
     */
    public List<String> mCandidatesList = new Vector<String>();

    /**
     * Element i stores the starting position of page i.
     */
    public Vector<Integer> mPageStart = new Vector<Integer>();

    /**
     * Element i stores the number of characters to page i.
     */
    public Vector<Integer> mCnToPage = new Vector<Integer>();

    /**
     * The position to delete in Pinyin string. If it is less than 0, IME will
     * do an incremental search, otherwise IME will do a deletion operation. if
     * {@link #mIsPosInSpl} is true, IME will delete the whole string for
     * mPosDelSpl-th spelling, otherwise it will only delete mPosDelSpl-th
     * character in the Pinyin string.
     */
    public int mPosDelSpl = -1;

    /**
     * If {@link #mPosDelSpl} is big than or equal to 0, this member is used to
     * indicate that whether the postion is counted in spelling id or character.
     */
    public boolean mIsPosInSpl;

    public DecodingInfo() {
        DEBUG_I("DecodingInfo");
        mSurface = new StringBuffer();
        mSurfaceDecodedLen = 0;
    }

    public void reset() {
        // DEBUG_I("reset");
        mSurface.delete(0, mSurface.length());
        mSurfaceDecodedLen = 0;
        mCursorPos = 0;
        mFullSent = "";
        mFixedLen = 0;
        mFinishSelection = false;
        mComposingStr = "";
        mComposingStrDisplay = "";
        mActiveCmpsLen = 0;
        mActiveCmpsDisplayLen = 0;

        resetCandidates();
    }

    public boolean isCandidatesListEmpty() {
        DEBUG_I("isCandidatesListEmpty");
        return mCandidatesList.size() == 0;
    }

    public boolean isSplStrFull() {
        DEBUG_I("isSplStrFull");
        if (mSurface.length() >= PY_STRING_MAX - 1)
            return true;
        return false;
    }

    public void addSplChar(char ch, boolean reset) {
        // DEBUG_I("addSplChar:" + ch + " reset:" + reset);
        if (reset) {
            mSurface.delete(0, mSurface.length());
            mSurfaceDecodedLen = 0;
            mCursorPos = 0;
            try {
                mIPinyinDecoderService.imResetSearch();
            } catch (RemoteException e) {
            }
        }
        mSurface.insert(mCursorPos, ch);
        mCursorPos++;
    }

    // Prepare to delete before cursor. We may delete a spelling char if
    // the cursor is in the range of unfixed part, delete a whole spelling
    // if the cursor in inside the range of the fixed part.
    // This function only marks the position used to delete.
    public void prepareDeleteBeforeCursor() {
        DEBUG_I("prepareDeleteBeforeCursor");
        if (mCursorPos > 0) {
            int pos;
            for (pos = 0; pos < mFixedLen; pos++) {
                if (mSplStart[pos + 2] >= mCursorPos && mSplStart[pos + 1] < mCursorPos) {
                    mPosDelSpl = pos;
                    mCursorPos = mSplStart[pos + 1];
                    mIsPosInSpl = true;
                    break;
                }
            }
            if (mPosDelSpl < 0) {
                mPosDelSpl = mCursorPos - 1;
                mCursorPos--;
                mIsPosInSpl = false;
            }
        }
    }

    public int length() {
        // DEBUG_I("length");
        return mSurface.length();
    }

    public char charAt(int index) {
        return mSurface.charAt(index);
    }

    public StringBuffer getOrigianlSplStr() {
        DEBUG_I("getOrigianlSplStr");
        return mSurface;
    }

    public int getSplStrDecodedLen() {
        DEBUG_I("getSplStrDecodedLen");
        return mSurfaceDecodedLen;
    }

    public int[] getSplStart() {
        DEBUG_I("getSplStart");
        return mSplStart;
    }

    public String getComposingStr() {
        DEBUG_I("getComposingStr");
        return mComposingStr;
    }

    public String getComposingStrActivePart() {
        DEBUG_I("getComposingStrActivePart");
        assert (mActiveCmpsLen <= mComposingStr.length());
        return mComposingStr.substring(0, mActiveCmpsLen);
    }

    public int getActiveCmpsLen() {
        DEBUG_I("getActiveCmpsLen");
        return mActiveCmpsLen;
    }

    public String getComposingStrForDisplay() {
        DEBUG_I("getComposingStrForDisplay");
        return mComposingStrDisplay;
    }

    public int getActiveCmpsDisplayLen() {
        DEBUG_I("getActiveCmpsDisplayLen");
        return mActiveCmpsDisplayLen;
    }

    public String getFullSent() {
        DEBUG_I("getFullSent");
        return mFullSent;
    }

    public String getCurrentFullSent(int activeCandPos) {
        DEBUG_I("getCurrentFullSent");
        try {
            String retStr = mFullSent.substring(0, mFixedLen);
            retStr += mCandidatesList.get(activeCandPos);
            return retStr;
        } catch (Exception e) {
            return "";
        }
    }

    public void resetCandidates() {
        DEBUG_I("resetCandidates");
        mCandidatesList.clear();
        mTotalChoicesNum = 0;

        mPageStart.clear();
        mPageStart.add(0);
        mCnToPage.clear();
        mCnToPage.add(0);
    }

    public boolean candidatesFromApp() {
        DEBUG_I("candidatesFromApp");
        return ImeState.STATE_APP_COMPLETION == mImeState;
    }

    public boolean canDoPrediction() {
        DEBUG_I("canDoPrediction");
        return mComposingStr.length() == mFixedLen;
    }

    public boolean selectionFinished() {
        DEBUG_I("selectionFinished");
        return mFinishSelection;
    }

    // After the user chooses a candidate, input method will do a
    // re-decoding and give the new candidate list.
    // If candidate id is less than 0, means user is inputting Pinyin,
    // not selecting any choice.
    public void chooseDecodingCandidate(int candId) {
        DEBUG_I("chooseDecodingCandidate:candId:" + candId);
        if (mImeState != ImeState.STATE_PREDICT) {
            resetCandidates();
            int totalChoicesNum = 0;
            if (candId < 0) {
                if (length() == 0) {
                    totalChoicesNum = 0;
                } else {
                    if (mPyBuf == null)
                        mPyBuf = new byte[PY_STRING_MAX];
                    for (int i = 0; i < length(); i++)
                        mPyBuf[i] = (byte) charAt(i);
                    mPyBuf[length()] = 0;
                    try {
                        if (mPosDelSpl < 0) {
                            totalChoicesNum = mIPinyinDecoderService.imSearch(mPyBuf, length());
                        } else {
                            boolean clear_fixed_this_step = true;
                            if (ImeState.STATE_COMPOSING == mImeState) {
                                clear_fixed_this_step = false;
                            }
                            totalChoicesNum = mIPinyinDecoderService.imDelSearch(mPosDelSpl, mIsPosInSpl,
                                    clear_fixed_this_step);
                            mPosDelSpl = -1;
                        }
                    } catch (Exception e) {
                        Constans.print(TAG, "Engine decoder service not connected yet");
                    }
                }
            }

            updateDecInfoForSearch(totalChoicesNum);
        }
    }

    private void updateDecInfoForSearch(int totalChoicesNum) {
        DEBUG_I("updateDecInfoForSearch:" + totalChoicesNum);
        mTotalChoicesNum = totalChoicesNum;
        if (mTotalChoicesNum < 0) {
            mTotalChoicesNum = 0;
            return;
        }

        try {
            String pyStr;

            mSplStart = mIPinyinDecoderService.imGetSplStart();
            pyStr = mIPinyinDecoderService.imGetPyStr(false);
            mSurfaceDecodedLen = mIPinyinDecoderService.imGetPyStrLen(true);
            assert (mSurfaceDecodedLen <= pyStr.length());

            mFullSent = mIPinyinDecoderService.imGetChoice(0);
            mFixedLen = mIPinyinDecoderService.imGetFixedLen();

            // Update the surface string to the one kept by engine.
            mSurface.replace(0, mSurface.length(), pyStr);

            if (mCursorPos > mSurface.length())
                mCursorPos = mSurface.length();
            mComposingStr = mFullSent.substring(0, mFixedLen) + mSurface.substring(mSplStart[mFixedLen + 1]);

            mActiveCmpsLen = mComposingStr.length();
            if (mSurfaceDecodedLen > 0) {
                mActiveCmpsLen = mActiveCmpsLen - (mSurface.length() - mSurfaceDecodedLen);
            }

            // Prepare the display string.
            if (0 == mSurfaceDecodedLen) {
                mComposingStrDisplay = mComposingStr;
                mActiveCmpsDisplayLen = mComposingStr.length();
            } else {
                mComposingStrDisplay = mFullSent.substring(0, mFixedLen);
                for (int pos = mFixedLen + 1; pos < mSplStart.length - 1; pos++) {
                    mComposingStrDisplay += mSurface.substring(mSplStart[pos], mSplStart[pos + 1]);
                    if (mSplStart[pos + 1] < mSurfaceDecodedLen) {
                        mComposingStrDisplay += " ";
                    }
                }
                mActiveCmpsDisplayLen = mComposingStrDisplay.length();
                if (mSurfaceDecodedLen < mSurface.length()) {
                    mComposingStrDisplay += mSurface.substring(mSurfaceDecodedLen);
                }
            }

            if (mSplStart.length == mFixedLen + 2) {
                mFinishSelection = true;
            } else {
                mFinishSelection = false;
            }
        } catch (RemoteException e) {
            Log.w(TAG, "PinyinDecoderService died", e);
        } catch (Exception e) {
            mTotalChoicesNum = 0;
            mComposingStr = "";
        }
        // Prepare page 0.
        if (!mFinishSelection) {
            preparePage(0);
        }
    }

    private void choosePredictChoice(int choiceId) {
        DEBUG_I("choosePredictChoice:" + choiceId);
        if (ImeState.STATE_PREDICT != mImeState || choiceId < 0 || choiceId >= mTotalChoicesNum) {
            return;
        }

        String tmp = mCandidatesList.get(choiceId);

        resetCandidates();

        mCandidatesList.add(tmp);
        mTotalChoicesNum = 1;

        mSurface.replace(0, mSurface.length(), "");
        mCursorPos = 0;
        mFullSent = tmp;
        mFixedLen = tmp.length();
        mComposingStr = mFullSent;
        mActiveCmpsLen = mFixedLen;

        mFinishSelection = true;
    }

    public String getCandidate(int candId) {
        DEBUG_I("getCandidate:" + candId);
        // Only loaded items can be gotten, so we use mCandidatesList.size()
        // instead mTotalChoiceNum.
        if (candId < 0 || candId > mCandidatesList.size()) {
            return null;
        }
        return mCandidatesList.get(candId);
    }

    private void getCandiagtesForCache() {
        DEBUG_I("getCandiagtesForCache");
        int fetchStart = mCandidatesList.size();
        int fetchSize = mTotalChoicesNum - fetchStart;
        if (fetchSize > MAX_PAGE_SIZE_DISPLAY) {
            fetchSize = MAX_PAGE_SIZE_DISPLAY;
        }
        try {
            List<String> newList = null;
            if (ImeState.STATE_INPUT == mImeState || ImeState.STATE_IDLE == mImeState
                    || ImeState.STATE_COMPOSING == mImeState) {
                newList = mIPinyinDecoderService.imGetChoiceList(fetchStart, fetchSize, mFixedLen);
            } else if (ImeState.STATE_PREDICT == mImeState) {
                newList = mIPinyinDecoderService.imGetPredictList(fetchStart, fetchSize);
            } else if (ImeState.STATE_APP_COMPLETION == mImeState) {
                newList = new ArrayList<String>();
                if (null != mAppCompletions) {
                    for (int pos = fetchStart; pos < fetchSize; pos++) {
                        CompletionInfo ci = mAppCompletions[pos];
                        if (null != ci) {
                            CharSequence s = ci.getText();
                            if (null != s)
                                newList.add(s.toString());
                        }
                    }
                }
            }
            mCandidatesList.addAll(newList);
            // for (String str : mCandidatesList) {
            // Constans.printE(TAG, "" + str);
            // }
        } catch (RemoteException e) {
            Log.w(TAG, "PinyinDecoderService died", e);
        }
    }

    public boolean pageReady(int pageNo) {
        DEBUG_I("pageReady:pageNO=" + pageNo);
        // If the page number is less than 0, return false
        if (pageNo < 0)
            return false;

        // Page pageNo's ending information is not ready.
        if (mPageStart.size() <= pageNo + 1) {
            return false;
        }

        return true;
    }

    public boolean preparePage(int pageNo) {
        DEBUG_I("preparePage:pageNO=" + pageNo);
        // If the page number is less than 0, return false
        if (pageNo < 0)
            return false;

        // Make sure the starting information for page pageNo is ready.
        if (mPageStart.size() <= pageNo) {
            return false;
        }

        // Page pageNo's ending information is also ready.
        if (mPageStart.size() > pageNo + 1) {
            return true;
        }

        // If cached items is enough for page pageNo.
        if (mCandidatesList.size() - mPageStart.elementAt(pageNo) >= MAX_PAGE_SIZE_DISPLAY) {
            return true;
        }

        // Try to get more items from engine
        getCandiagtesForCache();

        // Try to find if there are available new items to display.
        // If no new item, return false;
        if (mPageStart.elementAt(pageNo) >= mCandidatesList.size()) {
            return false;
        }

        // If there are new items, return true;
        return true;
    }

    public void preparePredicts(CharSequence history) {
        DEBUG_I("preparePredicts:history=" + history);
        if (null == history)
            return;

        resetCandidates();
        
        Settings.setPrediction(true);
        if (Settings.getPrediction()) {
            String preEdit = history.toString();
            int predictNum = 0;
            if (null != preEdit) {
                try {
                    mTotalChoicesNum = mIPinyinDecoderService.imGetPredictsNum(preEdit);
                } catch (RemoteException e) {
                    return;
                }
            }
        }

        preparePage(0);
        mFinishSelection = false;
    }

    private void prepareAppCompletions(CompletionInfo completions[]) {
        DEBUG_I("prepareAppCompletions=" + completions.toString());
        resetCandidates();
        mAppCompletions = completions;
        mTotalChoicesNum = completions.length;
        preparePage(0);
        mFinishSelection = false;
        return;
    }

    public int getCurrentPageSize(int currentPage) {
        DEBUG_I("getCurrentPageSize:currentPage=" + currentPage);
        if (mPageStart.size() <= currentPage + 1)
            return 0;
        return mPageStart.elementAt(currentPage + 1) - mPageStart.elementAt(currentPage);
    }

    public int getCurrentPageStart(int currentPage) {
        DEBUG_I("getCurrentPageStart:currentPage=" + currentPage);
        if (mPageStart.size() < currentPage + 1)
            return mTotalChoicesNum;
        return mPageStart.elementAt(currentPage);
    }

    public boolean pageForwardable(int currentPage) {
        DEBUG_I("pageForwardable:currentPage=" + currentPage);
        if (mPageStart.size() <= currentPage + 1)
            return false;
        if (mPageStart.elementAt(currentPage + 1) >= mTotalChoicesNum) {
            return false;
        }
        return true;
    }

    public boolean pageBackwardable(int currentPage) {
        DEBUG_I("pageBackwardable:currentPage=" + currentPage);
        if (currentPage > 0)
            return true;
        return false;
    }

    public boolean charBeforeCursorIsSeparator() {
        DEBUG_I("charBeforeCursorIsSeparator");
        int len = mSurface.length();
        if (mCursorPos > len)
            return false;
        if (mCursorPos > 0 && mSurface.charAt(mCursorPos - 1) == '\'') {
            return true;
        }
        return false;
    }

    public int getCursorPos() {
        DEBUG_I("getCursorPos");
        return mCursorPos;
    }

    public int getCursorPosInCmps() {
        DEBUG_I("getCursorPosInCmps");
        int cursorPos = mCursorPos;
        int fixedLen = 0;

        for (int hzPos = 0; hzPos < mFixedLen; hzPos++) {
            if (mCursorPos >= mSplStart[hzPos + 2]) {
                cursorPos -= mSplStart[hzPos + 2] - mSplStart[hzPos + 1];
                cursorPos += 1;
            }
        }
        return cursorPos;
    }

    public int getCursorPosInCmpsDisplay() {
        DEBUG_I("getCursorPosInCmpsDisplay");
        int cursorPos = getCursorPosInCmps();
        // +2 is because: one for mSplStart[0], which is used for other
        // purpose(The length of the segmentation string), and another
        // for the first spelling which does not need a space before it.
        for (int pos = mFixedLen + 2; pos < mSplStart.length - 1; pos++) {
            if (mCursorPos <= mSplStart[pos]) {
                break;
            } else {
                cursorPos++;
            }
        }
        return cursorPos;
    }

    public void moveCursorToEdge(boolean left) {
        DEBUG_I("moveCursorToEdge:left=" + left);
        if (left)
            mCursorPos = 0;
        else
            mCursorPos = mSurface.length();
    }

    // Move cursor. If offset is 0, this function can be used to adjust
    // the cursor into the bounds of the string.
    public void moveCursor(int offset) {
        DEBUG_I("moveCursor:offset=" + offset);
        if (offset > 1 || offset < -1)
            return;

        if (offset != 0) {
            int hzPos = 0;
            for (hzPos = 0; hzPos <= mFixedLen; hzPos++) {
                if (mCursorPos == mSplStart[hzPos + 1]) {
                    if (offset < 0) {
                        if (hzPos > 0) {
                            offset = mSplStart[hzPos] - mSplStart[hzPos + 1];
                        }
                    } else {
                        if (hzPos < mFixedLen) {
                            offset = mSplStart[hzPos + 2] - mSplStart[hzPos + 1];
                        }
                    }
                    break;
                }
            }
        }
        mCursorPos += offset;
        if (mCursorPos < 0) {
            mCursorPos = 0;
        } else if (mCursorPos > mSurface.length()) {
            mCursorPos = mSurface.length();
        }
    }

    public int getSplNum() {
        DEBUG_I("getSplNum");
        
        if (mSplStart == null) {
            return 0;
        }

        return mSplStart[0];
    }

    public int getFixedLen() {
        DEBUG_I("getFixedLen");
        return mFixedLen;
    }

    private static void DEBUG_I(String info) {
        if (false) {
            Constans.print(TAG, info);
        }
    }

    @Override
    public String toString() {
        return "DecodingInfo [mSurface=" + mSurface + ", mPyBuf=" + Arrays.toString(mPyBuf) + ", mSurfaceDecodedLen="
                + mSurfaceDecodedLen + ", mComposingStr=" + mComposingStr + ", mActiveCmpsLen=" + mActiveCmpsLen
                + ", mComposingStrDisplay=" + mComposingStrDisplay + ", mActiveCmpsDisplayLen=" + mActiveCmpsDisplayLen
                + ", mFullSent=" + mFullSent + ", mFixedLen=" + mFixedLen + ", mFinishSelection=" + mFinishSelection
                + ", mSplStart=" + Arrays.toString(mSplStart) + ", mCursorPos=" + mCursorPos
                + ", mIPinyinDecoderService=" + mIPinyinDecoderService + ", mAppCompletions="
                + Arrays.toString(mAppCompletions) + ", mTotalChoicesNum=" + mTotalChoicesNum + ", mCandidatesList="
                + mCandidatesList + ", mPageStart=" + mPageStart + ", mCnToPage=" + mCnToPage + ", mPosDelSpl="
                + mPosDelSpl + ", mIsPosInSpl=" + mIsPosInSpl + "]";
    }
    
    
}
