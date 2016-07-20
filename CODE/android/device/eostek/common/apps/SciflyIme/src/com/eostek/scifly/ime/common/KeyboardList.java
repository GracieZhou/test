
package com.eostek.scifly.ime.common;

import java.util.Locale;

import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import com.eostek.scifly.ime.LatinKeyboard;
import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.arabic.ArabicDictionary;
import com.eostek.scifly.ime.util.Constans;

/**
 * 键盘集合. 根据输入法子类类型创建主键盘,另外创建常用的四种键盘:英文大写/英文小写/中文符号/英文符号.
 * 
 * @author Youpeng
 */
public class KeyboardList {

    private static final String TAG = "KeyboardList";

    private Context mContext;

    private int mChineseKeyboardId;

    public KeyboardList(Context context, int chineseKeyboardId) {
        this.mContext = context;
        this.mChineseKeyboardId = chineseKeyboardId;
    }

    private LatinKeyboard mChineseKeyboard;

    private LatinKeyboard mChineseSymbolKeyboard;

    private LatinKeyboard mEnglishKeyboard;

    private LatinKeyboard mEnglishSymbolKeyboard;

    private LatinKeyboard mCurrentKeyboard;
    
    private LatinKeyboard mArabicSymolKeyboard;
   
    private LatinKeyboard mFrenchSymbolKeyboard;
    
    private LatinKeyboard mPersianSymbolKeyboard;
    
    private LatinKeyboard mKoreanSymbolKeyboard;

    private boolean isLastKeyboardChinese = true;
    

    private boolean wasEnglishToSymbol;
    
    private EditorInfo mEditInfo;

    /**
     * 创建所有键盘. 根据ChineseKeyboardId创建中文键盘,另外创建英文大写、英文小写、英文符号、中文符号键盘. 总计五个键盘.
     * 
     * @param keyboardWidth 键盘宽度.
     */
    public void createAllKeyboards(int keyboardWidth) {
        mChineseKeyboard = new LatinKeyboard(mContext, mChineseKeyboardId);
        mChineseKeyboard.updateLanguageChangeKey(Locale.CHINESE);

        mChineseSymbolKeyboard = new LatinKeyboard(mContext, R.xml.symbols_cn);

        mEnglishKeyboard = new LatinKeyboard(mContext, R.xml.qwerty2);
        mEnglishKeyboard.updateLanguageChangeKey(Locale.ENGLISH);

        mEnglishSymbolKeyboard = new LatinKeyboard(mContext, R.xml.symbols_en);
        
        mFrenchSymbolKeyboard = new LatinKeyboard(mContext, R.xml.symbols_fr);
        
        mArabicSymolKeyboard = new LatinKeyboard(mContext, R.xml.symbols_ar);
        
        mPersianSymbolKeyboard = new LatinKeyboard(mContext, R.xml.symbols_pe);
        
        mKoreanSymbolKeyboard = new LatinKeyboard(mContext,R.xml.symbols_ko);
        
    }

    /**
     * 根据输入框类型,选择键盘.
     * 
     * @param info 输入框类型.
     */
    public void startInputView(EditorInfo editInfo) {
    	int info = editInfo.inputType;
    	mEditInfo = editInfo; 

        // TODO Auto-generated method stub
        if (mCurrentKeyboard == null) {
            
          selectIme();
        }

        Constans.print("KeyboardSwitch", "onStartInput inputType = " + info);
        switch (info & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
            case InputType.TYPE_CLASS_PHONE:
                // Numbers, dates, and phones default to the symbol keyboard,
                // with
                // no extra features.
                selectIme();
//                toNumberSymbol();
                break;

            case InputType.TYPE_CLASS_TEXT:
                int variation = info & InputType.TYPE_MASK_VARIATION;
                if ((variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                        || (variation == InputType.TYPE_TEXT_VARIATION_URI)
                        || (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        || (variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) {
                    
                   selectIme();
                   
                } else {
                    // Switch to non-symbol keyboard, either Chinese or English
                    // keyboard,
                    // for other general text editing.
                    toNonSymbols();
                }
                break;

            default:
                // Switch to non-symbol keyboard, either Chinese or English
                // keyboard,
                // for all other input types.
                
              selectIme();
        }
    }

    /**
     * Switches to the number-symbol keyboard and remembers if it was English.
     */
    private void toNumberSymbol() {
        if (!mCurrentKeyboard.isSymbols()) {
            // Remember the current non-symbol keyboard to switch back from
            // symbols.
            wasEnglishToSymbol = mCurrentKeyboard.isEnglish();
        }

        mCurrentKeyboard = mEnglishSymbolKeyboard;
    }

    private void toChinese() {
        mCurrentKeyboard = mChineseKeyboard;
        isLastKeyboardChinese = true;
    }

    private void toChineseSymbol() {
        boolean isFrench = LatinKeyboard.isFrenchIME(mContext);
        boolean isArabic = LatinKeyboard.isArabicIME(mContext);
        boolean isPersian = LatinKeyboard.isPersianIME(mContext);
        boolean isKorean = LatinKeyboard.isKoreanIME(mContext);
        boolean isJapan = LatinKeyboard.isJapanIME(mContext);
        if (isFrench ) {
            mCurrentKeyboard = mFrenchSymbolKeyboard;
//            mCurrentKeyboard.getKeys().get(39).icon = mContext.getResources().getDrawable(R.drawable.french_logo);
        } else if(isArabic) {
           mCurrentKeyboard = mArabicSymolKeyboard;
//           mCurrentKeyboard.getKeys().get(42).icon = mContext.getResources().getDrawable(R.drawable.arabic_logo);
        }else if(isPersian) {
            mCurrentKeyboard = mPersianSymbolKeyboard;
//            mCurrentKeyboard.getKeys().get(42).icon = mContext.getResources().getDrawable(R.drawable.persian_logo);
        }else if(isKorean){
            mCurrentKeyboard = mKoreanSymbolKeyboard;
//            mCurrentKeyboard.getKeys().get(39).icon = mContext.getResources().getDrawable(R.drawable.);
        }else if(isJapan){
            mCurrentKeyboard = mChineseSymbolKeyboard;
            mCurrentKeyboard.getKeys().get(40).icon = mContext.getResources().getDrawable(R.drawable.japan_on);
        }else{
            mCurrentKeyboard = mChineseSymbolKeyboard;
            mCurrentKeyboard.getKeys().get(40).label = mContext.getResources().getString(R.string.language_en);
        }
    }

    private void toEnglish() {
        mCurrentKeyboard = mEnglishKeyboard;
        if (LatinKeyboard.isFrenchIME(mContext)) {
            mCurrentKeyboard.getKeys().get(39).icon = mContext.getResources().getDrawable(R.drawable.english_on_for_french);
        } else if(LatinKeyboard.isArabicIME(mContext)) {
            mCurrentKeyboard.getKeys().get(39).icon = mContext.getResources().getDrawable(R.drawable.english_ar_on);
        }else if(LatinKeyboard.isPersianIME(mContext)){
            mCurrentKeyboard.getKeys().get(39).icon = mContext.getResources().getDrawable(R.drawable.english_pe_on);
        }else if(LatinKeyboard.isKoreanIME(mContext)){
            Log.d(TAG, "LatinKeyboard.isKoreanIME(mContext)"+LatinKeyboard.isKoreanIME(mContext));
            mCurrentKeyboard.getKeys().get(39).icon = mContext.getResources().getDrawable(R.drawable.korean_englis_on);
        }else if (LatinKeyboard.isJapanIME(mContext)){
            mCurrentKeyboard.getKeys().get(39).icon = mContext.getResources().getDrawable(R.drawable.japan_on_english);
        }else{
            mCurrentKeyboard.getKeys().get(39).icon = mContext.getResources().getDrawable(R.drawable.english_on);
            
        }
        isLastKeyboardChinese = false;
    }

    private void toEnglishSymbol() {
        mCurrentKeyboard = mEnglishSymbolKeyboard;
        if (LatinKeyboard.isFrenchIME(mContext)) {
            mCurrentKeyboard.getKeys().get(40).label = mContext.getResources().getString(R.string.language_fr);
         }else if(LatinKeyboard.isArabicIME(mContext)){
             mCurrentKeyboard.getKeys().get(40).icon = mContext.getResources().getDrawable(R.drawable.english_ar_on);
         }else if(LatinKeyboard.isPersianIME(mContext)){
             mCurrentKeyboard.getKeys().get(40).icon = mContext.getResources().getDrawable(R.drawable.english_pe_on);
         }else if(LatinKeyboard.isKoreanIME(mContext)){
             mCurrentKeyboard.getKeys().get(40).icon = mContext.getResources().getDrawable(R.drawable.korean_englis_on);
         }else if (LatinKeyboard.isJapanIME(mContext)){
             
             mCurrentKeyboard.getKeys().get(40).icon = mContext.getResources().getDrawable(R.drawable.japan_on_english);
        }else{
            mCurrentKeyboard.getKeys().get(40).label = mContext.getResources().getString(R.string.language_cn);
        }
    }
    
    /**
     * Switches from symbol (number-symbol or shift-symbol) keyboard, back to
     * the non-symbol (English or Chinese) keyboard.
     */
    private void toNonSymbols() {
        // if (mCurrentKeyboard.isSymbols()) {
        if (wasEnglishToSymbol) {
            toEnglish();
        } else {
            
          selectIme();
          
        }
        // }
    }

    /**
     * 获取当前键盘.
     * 
     * @return 当前键盘.
     */
    public LatinKeyboard getCurrentKeyboard() {
    	if(mCurrentKeyboard!=null){
    		mCurrentKeyboard.setImeOptions(mContext.getResources(), mEditInfo.imeOptions);
    	}
        return mCurrentKeyboard;
    }

    public boolean onKey(int primaryCode) {
        switch (primaryCode) {
        // case Keyboard.KEYCODE_SHIFT:
        // return true;
            case -7:// 中/英文 切换.
                if (mCurrentKeyboard.isSymbols()) {
                    // 如果是符号界面,切换 英符号/中符号.

                    if (mCurrentKeyboard.isShiftSymbol()) {
                        toEnglishSymbol();
                    } else {
                        Log.d(TAG, " !mCurrentKeyboard.isShiftSymbol()");
                            toChineseSymbol();
                    }
                } else {
                    // 如果是输入界面,切换英/中文.
                    Constans.print(TAG, "Chinese/English switch.");
                    if (mCurrentKeyboard.isEnglish()) {
                        toChinese();
                    } else {
                        toEnglish();
                    }
                }

                return true;
            case -8:// 符号切换.
                Constans.print(TAG, "symbol switch.");

                if (mCurrentKeyboard.isSymbols()) {
                    // 如果是符号,回到输入界面.
                         if (isLastKeyboardChinese) {
                         toChinese();
                     } else {
                         toEnglish();
                     }
                } else {
                    // 如果是输入界面,根据当前输入法,切到符号界面.

                    if (mCurrentKeyboard.isEnglish()) {
                        toEnglishSymbol();
                    } else {
                        toChineseSymbol();
                    }
                }

                return true;
        }
        return false;
    }

    private void toDefaultIme() {
        toEnglish();
    }
    /**
     * 判断输入法是法语,波斯,阿拉波语
     */
    private void  selectIme(){
        if(LatinKeyboard.isArabicIME(mContext)||LatinKeyboard.isFrenchIME(mContext)
                ||LatinKeyboard.isPersianIME(mContext)||LatinKeyboard.isKoreanIME(mContext)
                ||LatinKeyboard.isJapanIME(mContext)){
            toChinese();
        }else{
            toDefaultIme();
        }
    }
}
