package com.eostek.scifly.ime.pinyin;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.eostek.scifly.ime.InputEngineInterface;
import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.cangjie.WordDictionary;

public class PinyinDictionary extends WordDictionary {
	private InputMethodManager mInputMethodManager;

	private String mWordSeparators;

	private InputEngineInterface mPinyinEngine;

	private DecodingInfo mDecodingInfo;

	public PinyinDictionary(Context context) {
		super(context);
		mInputMethodManager = (InputMethodManager) context
				.getSystemService(Service.INPUT_METHOD_SERVICE);
		mWordSeparators = context.getResources().getString(
				R.string.word_separators);

		mPinyinEngine = new PinyinEngine(context.getApplicationContext());

		mPinyinEngine.startEngine();

		mDecodingInfo = ((PinyinEngine) mPinyinEngine).getDecInfo();
	}

	/**
	 * 是否拼音输入法.
	 * 
	 * @return 是.
	 */
	public boolean isPinyinDictionary() {
		return true;
	}

	/**
	 * 获取解码信息.
	 * 
	 * @return 解码信息.
	 */
	public DecodingInfo getDecodingInfo() {
		return mDecodingInfo;
	}

	/**
	 * 获取候选词.
	 * 
	 * @param composing
	 *            关键字.
	 * @return 结果集.
	 */
	public List<String> getWords(CharSequence input) {

		if (input == null) {
			return mPinyinEngine.getCandidateList("");
		}

		return mPinyinEngine.getCandidateList(input.toString());
	}

	@Override
	public void chooseDecodingCandidate(int candIndexInAll) {
		// TODO Auto-generated method stub
		if (mDecodingInfo != null) {
			mDecodingInfo.chooseDecodingCandidate(candIndexInAll);
		}
	}

	@Override
	public boolean isCandidatesListEmpty() {
		if (mDecodingInfo != null) {
			return mDecodingInfo.isCandidatesListEmpty();
		}
		return false;
	}

	@Override
	public void preparePredicts(CharSequence cs) {
		// TODO Auto-generated method stub
		if (mDecodingInfo != null) {
			mDecodingInfo.preparePredicts(cs);
		}
	}

	@Override
	public int getOrigianlSplStr() {
		if (mDecodingInfo != null) {
			return mDecodingInfo.getOrigianlSplStr().length();
		}
		return 0;
	}

	@Override
	public String getComposingStrForDisplay() {
		if (mDecodingInfo != null) {
			return mDecodingInfo.getComposingStrForDisplay();
		}
		return null;
	}

}
