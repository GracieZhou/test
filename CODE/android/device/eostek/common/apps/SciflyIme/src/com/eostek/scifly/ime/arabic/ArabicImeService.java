package com.eostek.scifly.ime.arabic;



import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.android.inputmethod.latin.BinaryDictionary;
import com.android.inputmethod.latin.SuggestedWords.SuggestedWordInfo;
import com.eostek.scifly.ime.AbstractIME;
import com.eostek.scifly.ime.cangjie.WordDictionary;
import com.eostek.scifly.ime.common.KeyboardList;
import com.eostek.scifly.ime.common.TextEditor;
import com.eostek.scifly.ime.util.Constans;
import com.eostek.scifly.ime.zhuyin.PhraseDictionary;
import com.eostek.scifly.ime.R;

public class ArabicImeService extends AbstractIME  {
    
    private final String mSrviceName = "com.eostek.scifly.ime/.arabic.ArabicImeService";
    
    private static final String TAG = "ArabicImeService";
    
    private PhraseDictionary mPhraseDictionary;
    
   
    @Override
    public void initialize(Context context) {
       mPhraseDictionary  =new PhraseDictionary(getApplicationContext());    
    }

    @Override
    public KeyboardList createKeyboardList(Context context) {
        return  new KeyboardList(context, R.xml.arabic);
    }

    @Override
    public TextEditor createTextEditor() {
        // TODO Auto-generated method stub
        return new ArabicEditor();
    }

    @Override
    protected WordDictionary createWordDictionary(Context context) {
      
        return new ArabicDictionary(getApplicationContext());
    }
    
    @Override
    protected void updateCandidates() {

        List<String> mWords = new ArrayList<String>();
        Log.d(TAG, "updateCandidates");
        Constans.print(
                TAG,
                "composing.length : " + mTextEditor.composingText().length() + " composing "
                        + mTextEditor.composingText());
        String clickable = mTextEditor.composingText().toString();
        Constans.print(TAG, "clickable" + clickable);

    
        if (mTextEditor.composingText().length() == 0) {
            mCandidatesContainer.setCandidates(null, false, false);
        } else {
         

           
                mWords.add(mTextEditor.composingText().toString());
       
            mCandidatesContainer.setCandidates(mWords, false, false);
            mCandidatesContainer.updateCharInputTip(mTextEditor.composingText().toString());
        }

    }
    @Override
    public String getCurrentImeName() {
        return mSrviceName;
    }

}
