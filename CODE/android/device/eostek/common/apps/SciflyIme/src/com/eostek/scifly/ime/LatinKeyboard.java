
package com.eostek.scifly.ime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParserException;

import android.R.bool;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.util.SparseArray;
import android.view.inputmethod.EditorInfo;

import com.eostek.scifly.ime.util.Constans;

/**
 * Keyboard of ime.
 */
public class LatinKeyboard extends Keyboard {

    private static String TAG = "LatinKeyboard";

    /** Attribute tag of key type. */
    private static final String XMLATTR_KEY_TYPE = "key_type";

    /** Attribute tag of key width. */
    private static final String XMLATTR_KEY_WIDTH = "width";

    /** Attribute tag of key height. */
    private static final String XMLATTR_KEY_HEIGHT = "height";

    /** Attribute tag of the key's repeating ability. */
    private static final String XMLATTR_KEY_REPEAT = "repeat";

    /** Attribute tag of the key's behavior for balloon. */
    private static final String XMLATTR_KEY_BALLOON = "balloon";

    protected static final String XMLATTR_ROW_ID = "row_id";

    private static final String XMLATTR_KEY_SIZE = "size";

    /** The tag used to indicate the row element in the xml file. */
    protected static final String XMLTAG_ROW = "Row";

    /** The tag used to indicate the key element in the xml file. */
    protected static final String XMLTAG_KEY = "Key";

    private static final String XMLTAG_SMALL_TEXT = "isSmallText";

    private static final String XMLTAG_TEXT_SIZE = "textSize";

    private static final String XMLATTR_IS_LOGO = "isLogo";

    private final int id;

    public static final int KEYCODE_IME_CHANGE = -9;

    /** The tag used to indicate the resources element in the xml file. */
    private static final String XMLTAG_RESOURCES = "resources";

    /** The tag used to indicate the string element in the xml file. */
    private static final String XMLTAG_STRING = "string";

    private static final String XMLATTR_RESOURCES_START_CODES = "start_codes";

    /** The tag used to indicate the codes attribute in the xml file. */
    private static final String XMLATTR_CODES = "codes";

    private static final int NO_RESOUCES_CODES = -32768;

    private SparseArray<String> mStringResourcesArray = new SparseArray<String>();

    public static final int KEYCODE_MODE_CHANGE_ZH_EN = -7;

    public static final int KEYCODE_MODE_CHANGE_2SYMBOL = -8;

    public int mModeChangeZhCnIndex = -1;

    public int mModeChange2SymbolIndex = -1;

    public int mResoucesStartIndex = NO_RESOUCES_CODES;

    public final String INPUT_LOCALE_CN = "cn";

    public final String INPUT_LOCALE_EN = "en";

    /**
     * Rows in this soft keyboard. Each row has a id. Only matched rows will be
     * enabled.
     */
    private List<KeyRow> mKeyRows;

    private LatinKey mEnterKey;

    private LatinKey mSpaceKey;

    protected Context mContext;

    protected int mXmlEventType;

    protected int keyCount = 0;

    /**
     * index.
     */
    public int mShiftKeyIndex = -1;

    public int mLogoKeyIndex = -1;

    /**
     * indexes.
     */
    public List<Integer> mSpaceKeyIndex = new ArrayList<Integer>();

    /**
     * KeyCode space.
     */
    public static final int KEYCODE_SPACE = 32;

//    protected String mShiftUppercase;

    protected String mShiftLowercase;

    protected boolean isResized = false;

    private boolean isLogoOn = false;

    /**
     * Constructor.
     * 
     * @param context context.
     * @param xmlLayoutResId resource id of xml.
     */
    public LatinKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
        mContext = context;
        id = xmlLayoutResId;

//        mShiftUppercase = mContext.getResources().getString(R.string.label_uppercase);
//
//        mShiftLowercase = mContext.getResources().getString(R.string.label_lowercase);

        XmlResourceParser xrp = mContext.getResources().getXml(xmlLayoutResId);

        try {

            mXmlEventType = xrp.next();
            while (mXmlEventType != XmlResourceParser.END_DOCUMENT) {
                if (mXmlEventType == XmlResourceParser.START_TAG) {
                    String attr = xrp.getName();
                    if (XMLTAG_ROW.equals(attr)) {
                        int rowId = getInteger(xrp, XMLATTR_ROW_ID, KeyRow.ALWAYS_SHOW_ROW_ID);
                        beginNewRow(rowId, keyCount);
                    } else if (XMLTAG_RESOURCES.equals(attr)) {
                        mResoucesStartIndex = getInteger(xrp, XMLATTR_RESOURCES_START_CODES, NO_RESOUCES_CODES);
                    } else if (XMLTAG_STRING.equals(attr)) {
                        int key = getInteger(xrp, XMLATTR_CODES, 0);
                        String value = xrp.nextText();
                        mStringResourcesArray.put(key, value);
                    } else if (XMLTAG_KEY.equals(attr)) {
                        int keySize = getInteger(xrp, XMLATTR_KEY_SIZE, LatinKey.DEFAULT_KEY_SIZE);
                        int textSize = getInteger(xrp, XMLTAG_TEXT_SIZE, LatinKey.TEXT_SIZE_NOT_SETED);
                        boolean isSmallText = getBoolean(xrp, XMLTAG_SMALL_TEXT, false);
                        boolean isLogo = getBoolean(xrp, XMLATTR_IS_LOGO, false);
                        LatinKey key = (LatinKey) getKeys().get(keyCount);
                        int heightOffset = Constans.PREVIEW_Y_OFFSET;

                        key.mKeySize = keySize;
                        key.isSmallText = isSmallText;
                        key.textSizeInDp = textSize;
                        key.textSizeInPx = dip2px(mContext, textSize);

                        if (key.codes[0] == KEYCODE_SHIFT) {
                            mShiftKeyIndex = keyCount;
                        } else if (key.codes[0] == KEYCODE_SPACE) {
                            mSpaceKeyIndex.add(keyCount);
                        } else if (key.codes[0] == KEYCODE_MODE_CHANGE_ZH_EN) {
                            mModeChangeZhCnIndex = keyCount;
                        } else if (key.codes[0] == KEYCODE_MODE_CHANGE_2SYMBOL) {
                            mModeChange2SymbolIndex = keyCount;
                        } else if (key.codes[0] == -5) {
                            // add delete key icon preview.
                            key.iconPreview = mContext.getResources().getDrawable(R.drawable.delete_icon_popup);
                            key.iconPreview.setBounds(0, heightOffset, key.iconPreview.getIntrinsicWidth(),
                                    key.iconPreview.getIntrinsicHeight() + heightOffset);
                        }

                        /** save the index of logo key */
                        if (isLogo) {
                            Constans.print(TAG, "logo key in " + keyCount);
                            mLogoKeyIndex = keyCount;
                        }

                        keyCount++;
                    }
                }
                mXmlEventType = xrp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getRows();

    }

    /**
     * Every keyboard need to be resized.
     * 
     * @return See whether this keyboard is resized.
     */
    public boolean isResized() {
        return isResized;
    }

    /** Set resized. */
    public void setResized(boolean isResized) {
        this.isResized = isResized;
    }

    /** update Shift key's Label. */
    public void updateShiftLabel() {

        if (mShiftKeyIndex < 0 || mShiftKeyIndex >= getKeys().size()) {
            return;
        }

        Key shiftKey = getKeys().get(mShiftKeyIndex);
        if (isShifted()) {
//        	shiftKey.label = mShiftUppercase;
            shiftKey.icon = mContext.getResources().getDrawable(R.drawable.sym_keyboard_shifted);
        } else {
//            shiftKey.label = mShiftLowercase;
            shiftKey.icon = mContext.getResources().getDrawable(R.drawable.sym_keyboard_shift);
        }
    }

    /**
     * Constructor.
     * 
     * @param context Context.
     * @param layoutTemplateResId id of xml.
     * @param characters unknown.
     * @param columns unknown.
     * @param horizontalPadding unknown.
     */
    public LatinKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns,
            int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
        mContext = context;
        id = layoutTemplateResId;
    }

    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y, XmlResourceParser parser) {
        Key key = new LatinKey(res, parent, x, y, parser);

        if (key.codes[0] == 10) {
            mEnterKey = (LatinKey) key;
        } else if (key.codes[0] == ' ') {
            mSpaceKey = (LatinKey) key;
        }

        return key;
    }

    /**
     * get rows.
     * 
     * @return rows.
     */
    public List<KeyRow> getKeyRows() {
        return mKeyRows;
    }

    private List<KeyRow> getRows() {
        List<Key> keys = getKeys();

        if (null == keys || mKeyRows.size() == 0) {
            return null;
        }

        KeyRow keyRow;
        KeyRow nextKeyRow;
        if (mKeyRows.size() == 1) {
            keyRow = mKeyRows.get(0);
            keyRow.mSoftKeys = keys;
            return mKeyRows;
        }

        int start, end;
        for (int i = 0; i < mKeyRows.size(); i++) {
            keyRow = mKeyRows.get(i);
            start = keyRow.mStartIndex;

            if (i != mKeyRows.size() - 1) {
                nextKeyRow = mKeyRows.get(i + 1);
                end = nextKeyRow.mStartIndex;
            } else {
                end = keys.size();
            }

            if (start < keys.size() && end <= keys.size()) {
                keyRow.mSoftKeys.addAll(keys.subList(start, end));
            }
        }

        return mKeyRows;
    }

    /**
     * begin new Row.
     * 
     * @param rowId id of the row.
     * @param startIndex index.
     */
    public void beginNewRow(int rowId, int startIndex) {
        if (null == mKeyRows) {
            mKeyRows = new ArrayList<KeyRow>();
        }
        KeyRow keyRow = new KeyRow();
        keyRow.mRowId = rowId;
        keyRow.mTopF = -1;
        keyRow.mBottomF = -1;
        keyRow.mStartIndex = startIndex;
        keyRow.mSoftKeys = new ArrayList<Key>();
        mKeyRows.add(keyRow);
    }

    /**
     * if it is number keyboard.
     * 
     * @return is number keyboard or not.
     */
    public boolean isNumberSymbol() {
        return id == R.xml.symbols_en;
    }

    /**
     * if it is shifted keyboard.
     * 
     * @return is shifted keyboard or not.
     */
    public boolean isShiftSymbol() {
        return id == R.xml.symbols_cn || id == R.xml.symbols_fr|| id == R.xml.symbols_ar||id ==R.xml.symbols_pe
               || id == R.xml.symbols_ko;
    }
    
    public boolean isFrenchSymbol(){
    	return id ==R.xml.symbols_fr;
    }

    /**
     * Returns {@code true} if the current keyboard is the symbol (number-symbol
     * or shift-symbol) keyboard; otherwise returns {@code false}.
     * 
     * @return is symbols.
     */
    public boolean isSymbols() {
        return isNumberSymbol() || isShiftSymbol() || isFrenchSymbol();
    }

    /**
     * Updates the on/off status of sticky keys (symbol-key and shift-key).
     */
    public void updateStickyKeys() {
        if (isSymbols()) {
            setShifted(isShiftSymbol());
        }

        // if (symbolKey != null) {
        // symbolKey.on = isSymbols();
        // }
    }

    /**
     * This looks at the ime options given by the current editor, to set the
     * appropriate label on the keyboard's enter key (if it has one).
     */
    public void setImeOptions(Resources res, int options) {
        if (mEnterKey == null) {
            return;
        }

        switch (options & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
            case EditorInfo.IME_ACTION_GO:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_go_key);
                break;
            case EditorInfo.IME_ACTION_SEND:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_send_key);
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_search);
                mEnterKey.label = null;
                mEnterKey.iconPreview = mContext.getResources().getDrawable(R.drawable.sym_keyboard_search_popup);
                mEnterKey.iconPreview.setBounds(0, 0, mEnterKey.iconPreview.getIntrinsicWidth(),
                        mEnterKey.iconPreview.getIntrinsicHeight());
                break;
            case EditorInfo.IME_ACTION_NEXT:
                mEnterKey.label = res.getString(R.string.label_next_key);
//                mEnterKey.iconPreview = mContext.getResources().getDrawable(R.drawable.sym_keyboard_search_popup);
//                mEnterKey.iconPreview.setBounds(0, 0, mEnterKey.iconPreview.getIntrinsicWidth(),
//                        mEnterKey.iconPreview.getIntrinsicHeight());
            	break;
            default:
            	mEnterKey.label = res.getString(R.string.label_go_key);
                mEnterKey.icon = null;//res.getDrawable(R.drawable.sym_keyboard_return);
//                mEnterKey.label = null;
                mEnterKey.label = res.getString(R.string.label_go_key);
                mEnterKey.iconPreview = mContext.getResources().getDrawable(R.drawable.enter_popup_icon);
                mEnterKey.iconPreview.setBounds(0, Constans.PREVIEW_Y_OFFSET_ENTER,
                        mEnterKey.iconPreview.getIntrinsicWidth(), mEnterKey.iconPreview.getIntrinsicHeight()
                                + Constans.PREVIEW_Y_OFFSET_ENTER);
                break;
        }
    }

    /** Update the language change key's icon preview drawable. */
    public void updateLanguageChangeKey(Locale locale) {
        Key key = getKeys().get(mModeChangeZhCnIndex);
        int chineseOnId = R.drawable.chinese_on_popup;
        int englishOnId = R.drawable.english_on_popup;
        int frenchOnId = R.drawable.english_on_for_french;
        if (locale.getLanguage() == Locale.CHINESE.getLanguage()) {
            key.iconPreview = mContext.getResources().getDrawable(chineseOnId);
            key.iconPreview.setBounds(0, Constans.PREVIEW_Y_OFFSET_LANGUAGE, key.iconPreview.getIntrinsicWidth(),
                    key.iconPreview.getIntrinsicHeight() + Constans.PREVIEW_Y_OFFSET_LANGUAGE);
        } else if (locale.getLanguage() == Locale.ENGLISH.getLanguage()) {
            if (!isFrenchIME(mContext)) {
                key.iconPreview = mContext.getResources().getDrawable(englishOnId);
            } else {
                key.iconPreview = mContext.getResources().getDrawable(frenchOnId);
            }
            key.iconPreview.setBounds(0, Constans.PREVIEW_Y_OFFSET_LANGUAGE, key.iconPreview.getIntrinsicHeight(), 
                    key.iconPreview.getIntrinsicHeight()+Constans.PREVIEW_Y_OFFSET_LANGUAGE);
        }

    }

    /**
     * set space icon.
     * 
     * @param dr icon.
     */
    public void setSpaceIcon(final Drawable dr) {
        if (mSpaceKey != null) {
            mSpaceKey.icon = dr;
        }
    }

    /**
     * set space icon.
     * 
     * @param res resources.
     */
    public void setSpacePro(Resources res) {
        if (mSpaceKey != null) {
            mSpaceKey.icon = res.getDrawable(R.drawable.key5);
        }
    }

    /**
     * single key of keyboard.
     */
    public static class LatinKey extends Keyboard.Key {

        public static int TEXT_SIZE_NOT_SETED = 0;

        public int textSizeInPx = TEXT_SIZE_NOT_SETED;

        /**
         * size of key.
         */
        public static final int DEFAULT_KEY_SIZE = 1;

        /**
         * actual size of key.
         */
        public int mKeySize = 1;

        public int textSizeInDp = TEXT_SIZE_NOT_SETED;

        /**
         * 
         */
        public int mSameKeyRank = 1;

        public boolean isSmallText = false;

        /**
         * Constructor.
         * 
         * @param res resources.
         * @param parent in which row.
         * @param x x position.
         * @param y y position.
         * @param parser xml parser.
         */
        public LatinKey(Resources res, Keyboard.Row parent, int x, int y, XmlResourceParser parser) {
            super(res, parent, x, y, parser);
        }

        /**
         * Overriding this method so that we can reduce the target area for the
         * key that closes the keyboard.
         * 
         * @param x position.
         * @param y position.
         * @return if clicked inside.
         */
        @Override
        public boolean isInside(int x, int y) {
            return super.isInside(x, codes[0] == KEYCODE_CANCEL ? y - 10 : y);
        }

    }

    /**
     * java bean to holder keys of a row.
     */
    public class KeyRow {
        public static final int ALWAYS_SHOW_ROW_ID = -1;

        static final int DEFAULT_ROW_ID = 0;

        static final int DEFAULT_KEY_SIZE = 1;

        /**
         * keys of in one row.
         */
        public List<Key> mSoftKeys;

        /**
         * start index of row.
         */
        public int mStartIndex;

        /**
         * If the row id is {@link #ALWAYS_SHOW_ROW_ID}, this row will always be
         * enabled.
         */
        int mRowId;

        float mTopF;

        float mBottomF;

        int mTop;

        int mBottom;
    }

    /**
     * common parameters of Key.
     */
    class KeyCommonAttributes {
        XmlResourceParser mXrp;

        int keyType;

        float keyWidth;

        float keyHeight;

        boolean repeat;

        boolean balloon;

        KeyCommonAttributes(XmlResourceParser xrp) {
            mXrp = xrp;
            balloon = true;
        }

        // Make sure the default object is not null.
        boolean getAttributes(KeyCommonAttributes defAttr) {
            keyType = getInteger(mXrp, XMLATTR_KEY_TYPE, defAttr.keyType);
            keyWidth = getFloat(mXrp, XMLATTR_KEY_WIDTH, defAttr.keyWidth);
            keyHeight = getFloat(mXrp, XMLATTR_KEY_HEIGHT, defAttr.keyHeight);
            repeat = getBoolean(mXrp, XMLATTR_KEY_REPEAT, defAttr.repeat);
            balloon = getBoolean(mXrp, XMLATTR_KEY_BALLOON, defAttr.balloon);
            if (keyType < 0 || keyWidth <= 0 || keyHeight <= 0) {
                return false;
            }
            return true;
        }
    }

    protected int getInteger(XmlResourceParser xrp, String name, int defValue) {
        int resId = xrp.getAttributeResourceValue(null, name, 0);
        String s;
        if (resId == 0) {
            s = xrp.getAttributeValue(null, name);
            if (null == s) {
                return defValue;
            }
            try {
                int ret = Integer.valueOf(s);
                return ret;
            } catch (NumberFormatException e) {
                return defValue;
            }
        } else {
            return Integer.parseInt(mContext.getResources().getString(resId));
        }
    }

    private float getFloat(XmlResourceParser xrp, String name, float defValue) {
        int resId = xrp.getAttributeResourceValue(null, name, 0);
        if (resId == 0) {
            String s = xrp.getAttributeValue(null, name);
            if (null == s) {
                return defValue;
            }
            try {
                float ret;
                if (s.endsWith("%p")) {
                    ret = Float.parseFloat(s.substring(0, s.length() - 2)) / 100;
                } else {
                    ret = Float.parseFloat(s);
                }
                return ret;
            } catch (NumberFormatException e) {
                return defValue;
            }
        } else {
            return mContext.getResources().getDimension(resId);
        }
    }

    private boolean getBoolean(XmlResourceParser xrp, String name, boolean defValue) {
        String s = xrp.getAttributeValue(null, name);
        if (null == s) {
            return defValue;
        }
        try {
            boolean ret = Boolean.parseBoolean(s);
            return ret;
        } catch (NumberFormatException e) {
            return defValue;
        }
    }

    /**
     * get current row.
     * 
     * @return current row.
     */
    public int getRowNum() {
        return mKeyRows.size();
    }

    // FIXME
    public boolean isEnglish() {
        return id == R.xml.qwerty2 || id == R.xml.symbols_en;
    }

    public boolean isChinese() {
        return id == R.xml.chinese || id == R.xml.cangjie || id == R.xml.zhuyin ||id == R.xml.french
            || id == R.xml.arabic || id == R.xml.persian  || id == R.xml.japan;
    }
    public boolean isFrench(){
    	return id ==R.xml.french;
    }
    
    public boolean isKorean(){
        return id == R.xml.korean;
    }
    
    public static  boolean isFrenchIME(Context context){
        return context.getClass().toString().trim().contains("FrenchImeService");
    }
    
    public static boolean isArabicIME(Context context){
        return context.getClass().toString().trim().contains("ArabicImeService");
    }
    
    public static boolean isPersianIME(Context context){
        return context.getClass().toString().trim().contains("PersianImeService");
    }
    public  static boolean isKoreanIME(Context context){
        return context.getClass().toString().trim().contains("KoreanImeService");
    }
    public  static boolean isJapanIME(Context context){
        return context.getClass().toString().trim().contains("JapanImeService");
    }
    

    /**
     * Get resources string start index defined in xml file.
     * 
     * @return Resources string start index.
     */
    public int getResoucesStartIndex() {
        return mResoucesStartIndex;
    }

    /**
     * Get string by codes.
     * 
     * @param codes Codes
     * @return String resouce.
     */
    public String getStringByCodes(int codes) {
        return mStringResourcesArray.get(codes, "");
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /** See whether iSynergy is connected or not. */
    public boolean isLogoOn() {
        return isLogoOn;
    }

    /** Set whether iSynergy is connected or not. */
    public void setLogoOn(boolean isLogoOn) {
        this.isLogoOn = isLogoOn;
    }

}
