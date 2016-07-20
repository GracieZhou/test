
package com.eostek.scifly.ime;

import static com.eostek.scifly.ime.util.Constans.ROW_KEY_NUM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.input.InputManager;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.MetaKeyKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import com.android.inputmethod.openwnn.ComposingText;
import com.android.inputmethod.openwnn.Romkan;
import com.android.inputmethod.openwnn.StrSegment;
import com.eostek.scifly.ime.LatinKeyboard.KeyRow;
import com.eostek.scifly.ime.cangjie.WordDictionary;
import com.eostek.scifly.ime.common.KeyboardList;
import com.eostek.scifly.ime.common.TextEditor;
import com.eostek.scifly.ime.french.HomeWatcher;
import com.eostek.scifly.ime.french.HomeWatcher.OnHomePressedListener;
import com.eostek.scifly.ime.korean.InputTables;
import com.eostek.scifly.ime.korean.KoreanAutomata;
import com.eostek.scifly.ime.pinyin.DecodingInfo;
import com.eostek.scifly.ime.pinyin.ImeState;
import com.eostek.scifly.ime.sync.ISyncManager;
import com.eostek.scifly.ime.sync.words.IWordsChangedListener;
import com.eostek.scifly.ime.sync.words.IWordsSyncManager;
import com.eostek.scifly.ime.ui.CandidatesContainer;
import com.eostek.scifly.ime.ui.ImeSelectDialog;
import com.eostek.scifly.ime.ui.LatinKeyboardView;
import com.eostek.scifly.ime.ui.MoreKeyViewFactory;
import com.eostek.scifly.ime.util.Constans;

public abstract class AbstractIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener,
        CandidatesContainer.PageArrowClickListener {
    private static final String TAG = "AbstractIME";

    protected static final int MAX_CODE_LENGTH = 27;

    protected static final boolean PROCESS_HARD_KEYS = true;
    
    private static boolean ISFRENCHSERVICE = false;

    protected LatinKeyboardView mInputView;

    protected CandidatesContainer mCandidatesContainer;

    protected Keyboard nCurrentKeyboard;

    protected KeyboardList mKeyboardList;

    protected TextEditor mTextEditor;

    protected WordDictionary mWordDictionary;

    private InputMethodManager imm;

    private PackageManager mPkgManager;
    
    private ComposingText mComposingText;
    
    private Romkan mRomkan ;

    // 各输入法实现类做自己的初始化.
    public abstract void initialize(Context context);
    
    

    // 创建输入法键盘对象集合.
    public abstract KeyboardList createKeyboardList(Context context);

    // 创建文字处理类.
    public abstract TextEditor createTextEditor();

    // 创建词典类.
    protected abstract WordDictionary createWordDictionary(Context context);

    // 初始高亮的位置.
    private int mKeyStartIndex = 0;

    protected int nLastKeyIndex = 0;

    protected boolean isIRKeyDown = false;
    
    protected boolean isShifted = true;
    
    protected boolean isHardKeyboard = true ;
    
    protected boolean isShiftKeys = false;

    protected boolean isInCandidatesRang = false;

    protected List<Keyboard.Key> nKeys;

    protected int nCurKeyboardKeyNums;

    protected String mWordSeparators;
    

    protected static final int INVALIDATE_KEY = 0x01;

    protected static final int KEY_BLINK_ANIMATE_STEP1 = 0x04;

    protected static final int KEY_BLINK_ANIMATE_STEP2 = 0X05;

    // Frank Patch Begin
    protected static final int KEY_SET_ALL_UNPRESSED = 0x06;

    // Frank Patch End
    protected boolean mCapsLock = false;

    private boolean mCompletionOn;

    private final String LOCAL_IME_ZHUYIN = "com.eostek.scifly.ime/.zhuyin.ZhuyinImeService";

    private final String LOCAL_IME_CANGJIE = "com.eostek.scifly.ime/.cangjie.CangjieImeService";

    private final String LOCAL_IME_PINYIN = "com.eostek.scifly.ime/.pinyin.PinyinImeService";

    private final String LOCAL_IME_FRENCH = "com.eostek.scifly.ime/.french.FrenchImeService";
    
    private final String  LOCAL_IME_ARABIC = "com.eostek.scifly.ime/.arabic.ArabicImeService";
    
    private final String LOCAL_IME_PERSIAN ="com.eostek.scifly.ime/.persian.PersianImeService";
    
    private final String LOCAL_IME_KOREAN ="com.eostek.scifly.ime/.korean.KoreanImeService";
    
    private final String LOCAL_IME_JAPAN ="com.eostek.scifly.ime/.japan.JapanImeService";

    private ISyncManager mISyncManager;

    private IWordsSyncManager mIWordsSyncManager;
    
    private HomeWatcher mHomeWatcher;

    private String mIsynergyWords;
    
    private KoreanAutomata kauto;

    private String currentEditorWords;

    protected final int timeOut = 500;

    private static boolean mIsTv = false;

    private static boolean mSwitchEnter = false;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            Constans.print(TAG, "bounded with syncService.");

            mISyncManager = ISyncManager.Stub.asInterface(service);
            if (mISyncManager != null) {
                try {
                    mIWordsSyncManager = mISyncManager.getWordsSyncManager();
                    if (mIWordsSyncManager != null) {
                        mIWordsSyncManager.registerWordsChangedListener(new IWordsChangedListener.Stub() {

                            @Override
                            public void socketStatus(boolean isAvailable) throws RemoteException {
                                Constans.print(TAG, "isAvailable:" + isAvailable);
                                AbstractIME.this.mIsAvailable = isAvailable;

                                mHandler.sendEmptyMessage(refreshIsynergyIcon);
                            }

                            @Override
                            public void onWordsChanged(int type, final String words) throws RemoteException {
                                // 1.if editor-txt equals isyn-txt, do nothing.
                                String editorWords = getEditorText();
                                if (editorWords == null) {
                                    Constans.print(TAG, "getEditorText is null.");
                                    return;
                                }

                                // do not synchronize if words not change.
                                if (words.equals(editorWords)) {
                                    Constans.print(TAG, "Device equals iSynergy");
                                    return;
                                }
                                Constans.print(TAG, "edt:" + editorWords + "; iSy:" + words);

                                // 2.clean up editor-txt.
                                mIsynergyWords = words;
                                int length = editorWords.length();
                                getCurrentInputConnection().beginBatchEdit();
                                if (length > 0) {
                                    // clean editText.
                                    getCurrentInputConnection().deleteSurroundingText(length, length);
                                }

                                // 3.reset editor-txt with isyn-txt.
                                getCurrentInputConnection().commitText(words, 1);
                                getCurrentInputConnection().endBatchEdit();

                                // synchronized (iSynergyInput) {
                                // try {
                                // iSynergyInput.wait();
                                // } catch (InterruptedException e) {
                                // e.printStackTrace();
                                // }
                                // }
                            }

                        });
                    }
                } catch (RemoteException e) {
                    // e.printStackTrace();
                    Constans.print(TAG, "RemoteException happended");
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

    private Object mOldEditorWords;

    private long mMetaState;

    /**
     * Deal with the editor reporting movement of its cursor.
     * 
     * @param oldSelStart start position of previous selection position.
     * @param oldSelEnd end position of previous selection position.
     * @param newSelStart start position of current selection position.
     * @param newSelEnd end position of current selection position.
     * @param candidatesStart start position of candidate.
     * @param candidatesEnd end position of candidate.
     */
    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart,
            int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
        // print("onUpdateSelection");
        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.

        InputConnection ic = getCurrentInputConnection();

        if (mTextEditor.composingText().length() > 0 && (newSelStart != candidatesEnd || newSelEnd != candidatesEnd)) {
//            mTextEditor.setLength(0);
            // updateCandidates();
            if (ic != null) {
                ic.finishComposingText();
            }
        }

        // whether send words.
        String tempStr = getEditorText();
        if (tempStr == null) {
            Constans.print(TAG, "getEditorText is null.");
            return;
        } else {
            currentEditorWords = tempStr;
        }
        // 1.manual or isynergy:
        if (mIsynergyWords != null && mIsynergyWords.equals(currentEditorWords)) {
            mOldEditorWords = null;
            // isyn-txt(receivedWords = currentWords)
            Constans.print(TAG, "received from isynergy:" + currentEditorWords);
            return;

        } else {
            mIsynergyWords = null;
            // manual txt.
            if (mOldEditorWords != null && mOldEditorWords.equals(currentEditorWords)) {
                Constans.print(TAG, "manual input and not changed.");
                return;
            }
            // send words to iSynergy.
            // Constans.printE(TAG, "send->" + currentEditorWords);
            if (TextUtils.isEmpty(currentEditorWords)) {
                try {
                    Constans.print(TAG, "send new handler:" + currentEditorWords);
                    if (mIWordsSyncManager != null) {
                        mIWordsSyncManager.sendBySocket(Constans.TYPE_WORDS, "");
                    }
                    return;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            if (mHandler.hasMessages(sendWords2iSynergy)) {
                Constans.print(TAG, "process-ing,");
            } else {
                Constans.print(TAG, "send new handler.");
                mHandler.sendEmptyMessageDelayed(sendWords2iSynergy, timeOut);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String platform = SystemProperties.get("ro.scifly.platform", "tv");
        Log.d(TAG, "plf:" + platform);
        if (platform.equalsIgnoreCase("tv")) {
            mIsTv = true;
        }
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mPkgManager = getPackageManager();
        
         kauto =new KoreanAutomata();
        initialize(this);// 各输入法实现类的初始化.
        mKeyboardList = createKeyboardList(this);// 创建键盘集合.
        nCurrentKeyboard = mKeyboardList.getCurrentKeyboard();

        mTextEditor = createTextEditor();// 创建文字编辑(工具)类.
        mWordDictionary = createWordDictionary(this);// 联想字(词)处理类.

        mWordSeparators = getResources().getString(R.string.word_separators);
        // 同步服务.
        Intent syncService = new Intent(ISyncManager.class.getName());

        startService(syncService);
        bindService(syncService, conn, BIND_AUTO_CREATE);
        mLocalIMEs.put(R.string.french_IME, LOCAL_IME_FRENCH);
        mLocalIMEs.put(R.string.zhuyin_IME, LOCAL_IME_ZHUYIN);
        mLocalIMEs.put(R.string.cangjie_IME, LOCAL_IME_CANGJIE);
        mLocalIMEs.put(R.string.pinyin_IME, LOCAL_IME_PINYIN);
        mLocalIMEs.put(R.string.arabic_IME, LOCAL_IME_ARABIC);
        mLocalIMEs.put(R.string.persian_IME, LOCAL_IME_PERSIAN);    
        mLocalIMEs.put(R.string.korean_IME, LOCAL_IME_KOREAN);
        mLocalIMEs.put(R.string.japan_IME, LOCAL_IME_JAPAN);
       
        // Frank Patch Begin
        mConfig.updateFrom(getResources().getConfiguration());
        // Frank Patch End
       if(LatinKeyboard.isJapanIME(this)){
           mComposingText = new ComposingText();
           mRomkan = new Romkan();
       }
  
        Log.i(TAG, "Name:SciflyIME, Version:2.4.38, Date:2015-09-02, Publisher:Youpeng.Wan,Frank.Zhang, REV:39241");
    }

    @SuppressLint("InflateParams")
    @Override
    // 创建键盘布局(输入法父布局).
    public View onCreateInputView() {
        mInputView = (LatinKeyboardView) getLayoutInflater().inflate(R.layout.input, null);
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setCurrentKeyIndex(-1);
        //mInputView.findViewById(R.id.keyboardView);

        return mInputView;
    }

    @SuppressLint("InflateParams")
    @Override
    // 创建候选词区域.
    public View onCreateCandidatesView() {
        mCandidatesContainer = (CandidatesContainer) getLayoutInflater().inflate(R.layout.candidates, null);
        mCandidatesContainer.setPageArrowClickListener(this);
        mCandidatesContainer.setService(this);
        return mCandidatesContainer;
      
    };

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        Constans.print(TAG, "AbstractIME onStartInput");
        super.onStartInput(attribute, restarting);
        mSwitchEnter = false;
        Log.d(TAG, "--------------------onStartInput");
        // FIXME
        mTextEditor.setLength(0);
        mCompletionOn = false;
        isInCandidatesRang = false;

        if (!restarting) {
            mMetaState = 0;
        }

        // We are now going to initialize our state based on the type of
        // text being edited.
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
                // mCurKeyboard = mPinyinKeyboard;

                debugLog("inputType =NUMBER/DATETIME");
                updateShiftKeyState(attribute);
                break;

            case InputType.TYPE_CLASS_PHONE:
                // mCurKeyboard = mPinyinKeyboard;
                updateShiftKeyState(attribute);
                break;

            case InputType.TYPE_CLASS_TEXT:
                debugLog("TYPE_CLASS_TEXT");
                mSwitchEnter = true;
                // mCurKeyboard = mPinyinKeyboard;

                int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;
                if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD
                        || variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    debugLog("passwords.");
                }

                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        || variation == InputType.TYPE_TEXT_VARIATION_URI
                        || variation == InputType.TYPE_TEXT_VARIATION_FILTER) {
                    // mCurKeyboard = mPinyinSymbolKeyboard;
                }

                if ((attribute.inputType & InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    mCompletionOn = isFullscreenMode();
                }

                updateShiftKeyState(attribute);
                break;

            default:
                debugLog("TYPE_CLASS_default");

                // mCurKeyboard = mPinyinKeyboard;
                updateShiftKeyState(attribute);
        }

    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        // FIXME
        mTextEditor.start(info.inputType);
        
        //监测是否切换到多字符的输入法
        watchTheImeService();
     
        // 显示候选区.
        setCandidatesViewShown(true);

        // 清理候选区.
        clearCandidates();

        // 初始化键盘.
        mKeyboardList.createAllKeyboards(getMaxWidth());

        // 根据输入框类型,选择键盘.
        mKeyboardList.startInputView(info);

        // 更新、显示键盘.
        updateKeyboard();

        final ViewTreeObserver observer = mInputView.getViewTreeObserver();

        // 设置初始高亮的位置.
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (!observer.isAlive()) {
                    ViewTreeObserver observer = mInputView.getViewTreeObserver();
                    observer.removeOnGlobalLayoutListener(this);
                } else {
                    observer.removeOnGlobalLayoutListener(this);
                }
                mInputView.setKeyPressed(mKeyStartIndex, true);
                mInputView.setCurrentKeyIndex(mKeyStartIndex);
                // Constans.print(TAG, " " +
                // mInputView.getKeyboard().getKeys().get(mKeyStartIndex).pressed);
                //
                // Message msg = mHandler.obtainMessage();
                // msg.what = INVALIDATE_KEY;
                // msg.arg1 = mKeyStartIndex;
                // msg.obj = true;
                // mHandler.sendMessageDelayed(msg, 100);
            }
        });

    }

    private void updateKeyboard() {
        if (mInputView != null) {
            mKeyboardList.getCurrentKeyboard().setLogoOn(AbstractIME.this.mIsAvailable);
            mInputView.setKeyboard(mKeyboardList.getCurrentKeyboard());
        }

    }

    private void clearCandidates() {
        mTextEditor.clearComposingText(getCurrentInputConnection());
        mCandidatesContainer.updateCharInputTip("");
        mCandidatesContainer.setCandidates(null, false, false);

    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();
        mTextEditor.clearComposingText(getCurrentInputConnection());
        setCandidatesViewShown(false);
        if (mInputView != null) {
            mInputView.closing();
        }

    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        mTextEditor.clearComposingText(getCurrentInputConnection());
        super.onFinishInputView(finishingInput);
          isShifted = true;
        // Dismiss any pop-ups when the input-view is being finished and
        // hidden.
        if(ISFRENCHSERVICE){
            mHomeWatcher.setOnHomeOrMenuPressedListener(null);
            mHomeWatcher.stopWatch();
            ISFRENCHSERVICE = false;
            }
        mInputView.closing();
        if(mComposingText != null){
            mComposingText.clear();
        }
        if(LatinKeyboard.isKoreanIME(this)){
            kauto.FinishAutomataWithoutInput();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unbindService(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MoreKeyViewFactory.clearMap();
        Constans.print(TAG, "onDestroy");
     
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Constans.print(TAG, "finalize");
    }

    @Override
    public void onFinishCandidatesView(boolean finishingInput) {
        mTextEditor.clearComposingText(getCurrentInputConnection());
        Log.d(TAG, "onFinishCandidatesView");
        super.onFinishCandidatesView(finishingInput);
    }

    public void onUnbindInput() {
        Constans.print(TAG, "onUnbindInput 1 ==>");

        mTextEditor.clearComposingText(getCurrentInputConnection());
        super.onUnbindInput();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mIsTv && mSwitchEnter) {
            debugLog("onKeyUp.need2Send keyCode:23");
            if (getCurrentInputConnection() != null) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_NUMPAD_ENTER:
                        if (mInputView != null && mInputView.isShown()) {
                            Log.i(TAG, "not show ime.");
                        } else {
                            Log.i(TAG, "showing.");
                        }
                        sendKeyUp(KeyEvent.KEYCODE_DPAD_CENTER);
                        return true;
                }
            }

        }
        if (mInputView != null && mInputView.isShown()) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_NUMPAD_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    return true;
                default:
                    break;
            }
        }
        if (PROCESS_HARD_KEYS) {
            mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState, keyCode, event);
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Use this to monitor key events being delivered to the application. We get
     * first crack at them, and can either resume them or let them continue to
     * the app.
     * 
     * @param keyCode keycode.
     * @param event keyEvent.
     * @return processed.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "-------------onkeydown---"+keyCode+ "------"+String.valueOf((char)keyCode));
        if (mInputView == null || !mInputView.isShown()) {

            if (mIsTv && mSwitchEnter) {
                debugLog("onKeyDown.need2Send keyCode:23");
                if (getCurrentInputConnection() != null) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                        case KeyEvent.KEYCODE_NUMPAD_ENTER:
                            sendKeyDown(KeyEvent.KEYCODE_DPAD_CENTER);
                            return true;
                    }
                }
            }

            return super.onKeyDown(keyCode, event);
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                // test code
                nKeys = mInputView.getKeyboard().getKeys();
                for (int i = 0; i < nKeys.size(); i++) {
                    if (nKeys.get(i).pressed) {
                        Log.i(TAG,"pressed i =" + i);
                    }
                }
                Log.i(TAG, "getLastKeyIndex " + mInputView.getLastKeyIndex());
                // Log.i(TAG, "pressed " +
                // nKeys.get(mInputView.getLastKeyIndex()).pressed); // To avoid
                // ArrayIndexOutOfBounds Exception
                LatinKeyboard keyboard = (LatinKeyboard) mInputView.getKeyboard();
                keyboard.setLogoOn(!keyboard.isLogoOn());
                mInputView.invalidateKey(keyboard.mLogoKeyIndex);
                MoreKeyViewFactory.dismissDialog();
                break;
            case KeyEvent.KEYCODE_BACK:
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack()) {
                        return true;
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:

                handleDown();

                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                handleUp();

                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                handleLeft();

                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:

                handleRight();
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                // FIXME
                if (mCandidatesContainer.isShown() && isInCandidatesRang) {//
                    if (mCandidatesContainer.getHighlightIndex() >= 0) {
                        pickSuggestionManually(mCandidatesContainer.getHighlightIndex());
                    
                        
                    }

                } else if (mInputView.isShown()) {
                    setFields();
                    mCandidatesContainer.setHighlightIndex(-1);
                    nLastKeyIndex = mInputView.getLastKeyIndex();
                    if (nLastKeyIndex >= 0) {
                        isIRKeyDown = true;
                        if (nCurrentKeyboard == null) {
                            nCurrentKeyboard = mKeyboardList.getCurrentKeyboard();
                            Constans.print(TAG, "currentKeyboard is null." + nCurrentKeyboard);
                        }
                        Key key = nCurrentKeyboard.getKeys().get(nLastKeyIndex);// youpeng.wan.
                        // mInputView.showPreview(nLastKeyIndex);
                        onKey(key.codes[0], key.codes);
                        Message msg = mHandler.obtainMessage();
                        msg.what = KEY_BLINK_ANIMATE_STEP1;
                        msg.arg1 = nLastKeyIndex;

                        mHandler.sendMessage(msg);

                    }
                }
                return true;
            default:
                break;
        }

        // Frank Patch Begin
        if (hasHardKeyboard()) {
            Log.d(TAG, "------------hasHardKeyboard");
            isHardKeyboard = false;
            if (handleHardKeyBoard(keyCode)) {
                return true;
            }
        }
        // Frank Patch End

        return super.onKeyDown(keyCode, event);
    }

    // Frank Patch Begin
    private int nCurrentKeyIndex = -1;

    public boolean hasHardKeyboard() {
        /*
         * this now work, so skip it, always enable hard keyboard if
         * (mConfig.keyboard == Configuration.KEYBOARD_NOKEYS ||
         * mConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
         * return false; }
         */
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        debugLog("onConfigurationChanged");
        mConfig.updateFrom(newConfig);
        super.onConfigurationChanged(newConfig);
        // should reset ?
    }

    private Configuration mConfig = new Configuration();

    private boolean handleHardKeyBoard(int keycode) {
        if (null == mInputView) {
            return false;
        }
        setFields();
        if(keycode>=144&&keycode<=153){
            nCurrentKeyIndex = mapKey(keycode); 
            if (nCurrentKeyboard == null) {
                nCurrentKeyboard = mKeyboardList.getCurrentKeyboard();
                Constans.print(TAG, "currentKeyboard is null." + nCurrentKeyboard);
            }
            mInputView.setKeyPressed(nCurrentKeyIndex, true);
            refreshKeyBoard(nCurrentKeyIndex, nCurrentKeyIndex);
            nCurrentKeyIndex = -1;
        }else{
            nCurrentKeyIndex = mapKey(keycode);
        }
        
       
        debugLog("keycode=" + keycode + "\t nCurrentKeyIndex=" + nCurrentKeyIndex);
        if (nCurrentKeyIndex >= nKeys.size() || nCurrentKeyIndex < 0) {
            debugLog("Non-hkb key pressed");
            return false;
        }

        debugLog("getCurrentImeName:" + getCurrentImeName());

        mInputView.setCurrentKeyIndex(nCurrentKeyIndex);

        if (mInputView.isShown()) {
            setFields();
            if (nLastKeyIndex >= 0) {
                if (nCurrentKeyboard == null) {
                    nCurrentKeyboard = mKeyboardList.getCurrentKeyboard();
                    Constans.print(TAG, "currentKeyboard is null." + nCurrentKeyboard);
                }
                Key key = nCurrentKeyboard.getKeys().get(nLastKeyIndex);// youpeng.wan.
                onKey(key.codes[0], key.codes);
                isIRKeyDown = false;
                Message msg = mHandler.obtainMessage();
                msg.what = KEY_BLINK_ANIMATE_STEP1;
                msg.arg1 = nLastKeyIndex;

                mHandler.sendMessage(msg);
                return true;
            }
        }
        return false;

    }

    private int mEnglishKeyMap[] = {
            KeyEvent.KEYCODE_Q, KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_E, KeyEvent.KEYCODE_R, KeyEvent.KEYCODE_T, 
            KeyEvent.KEYCODE_Y, KeyEvent.KEYCODE_U, KeyEvent.KEYCODE_I, KeyEvent.KEYCODE_O, KeyEvent.KEYCODE_P,
            KeyEvent.KEYCODE_NUMPAD_7, KeyEvent.KEYCODE_NUMPAD_8, KeyEvent.KEYCODE_NUMPAD_9,// row 1
            KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_S, KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_F, KeyEvent.KEYCODE_G, 
            KeyEvent.KEYCODE_H, KeyEvent.KEYCODE_J, KeyEvent.KEYCODE_K, KeyEvent.KEYCODE_L, KeyEvent.KEYCODE_COMMA, 
            KeyEvent.KEYCODE_NUMPAD_4, KeyEvent.KEYCODE_NUMPAD_5, KeyEvent.KEYCODE_NUMPAD_6,// row 2
            KeyEvent.KEYCODE_CAPS_LOCK, KeyEvent.KEYCODE_Z, KeyEvent.KEYCODE_X, KeyEvent.KEYCODE_C, KeyEvent.KEYCODE_V, 
            KeyEvent.KEYCODE_B, KeyEvent.KEYCODE_N, KeyEvent.KEYCODE_M, KeyEvent.KEYCODE_DEL,
            KeyEvent.KEYCODE_NUMPAD_1, KeyEvent.KEYCODE_NUMPAD_2, KeyEvent.KEYCODE_NUMPAD_3,// row 3
            KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_0, KeyEvent.KEYCODE_NUMPAD_DOT, // row 4
    };

    private int mEnglishSymbolKeyMap[] = {
            KeyEvent.KEYCODE_PERIOD, KeyEvent.KEYCODE_COMMA, KeyEvent.KEYCODE_1,KeyEvent.KEYCODE_2,KeyEvent.KEYCODE_3,KeyEvent.KEYCODE_4,
            KeyEvent.KEYCODE_5,KeyEvent.KEYCODE_7,KeyEvent.KEYCODE_8,KeyEvent.KEYCODE_MINUS,
            KeyEvent.KEYCODE_NUMPAD_7, KeyEvent.KEYCODE_NUMPAD_8, KeyEvent.KEYCODE_NUMPAD_9,// row 1
            KeyEvent.KEYCODE_EQUALS,KeyEvent.KEYCODE_APOSTROPHE,KeyEvent.KEYCODE_9,KeyEvent.KEYCODE_0,KeyEvent.KEYCODE_UNKNOWN, KeyEvent.KEYCODE_SEMICOLON,
            KeyEvent.KEYCODE_SLASH,KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_GRAVE,KeyEvent.KEYCODE_UNKNOWN,
            KeyEvent.KEYCODE_NUMPAD_4, KeyEvent.KEYCODE_NUMPAD_5, KeyEvent.KEYCODE_NUMPAD_6,// row 2
            KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_LEFT_BRACKET,KeyEvent.KEYCODE_RIGHT_BRACKET,KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_UNKNOWN,
            KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_DEL,
            KeyEvent.KEYCODE_NUMPAD_1, KeyEvent.KEYCODE_NUMPAD_2, KeyEvent.KEYCODE_NUMPAD_3,// row 3
            KeyEvent.KEYCODE_CTRL_LEFT,KeyEvent.KEYCODE_TAB,KeyEvent.KEYCODE_ALT_LEFT,KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_UNKNOWN,
            KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_0, KeyEvent.KEYCODE_NUMPAD_DOT, // row 4
    };

    private int mChineseSymbolKeyMap[] = {
            KeyEvent.KEYCODE_COMMA,KeyEvent.KEYCODE_PERIOD, KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_UNKNOWN, KeyEvent.KEYCODE_UNKNOWN,
            KeyEvent.KEYCODE_SEMICOLON,KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_2,KeyEvent.KEYCODE_3,
            KeyEvent.KEYCODE_NUMPAD_7, KeyEvent.KEYCODE_NUMPAD_8, KeyEvent.KEYCODE_NUMPAD_9,// row 1
            KeyEvent.KEYCODE_4,KeyEvent.KEYCODE_5,KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_9,KeyEvent.KEYCODE_0,KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_UNKNOWN,
            KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_LEFT_BRACKET,
            KeyEvent.KEYCODE_NUMPAD_4, KeyEvent.KEYCODE_NUMPAD_5, KeyEvent.KEYCODE_NUMPAD_6,// row 2
            KeyEvent.KEYCODE_RIGHT_BRACKET,KeyEvent.KEYCODE_UNKNOWN, KeyEvent.KEYCODE_UNKNOWN, KeyEvent.KEYCODE_UNKNOWN, KeyEvent.KEYCODE_NUMPAD_MULTIPLY, 
            KeyEvent.KEYCODE_NUMPAD_DIVIDE,KeyEvent.KEYCODE_MINUS,KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_DEL,
            KeyEvent.KEYCODE_NUMPAD_1, KeyEvent.KEYCODE_NUMPAD_2, KeyEvent.KEYCODE_NUMPAD_3,// row 3
            KeyEvent.KEYCODE_CTRL_LEFT,KeyEvent.KEYCODE_TAB,KeyEvent.KEYCODE_ALT_LEFT,KeyEvent.KEYCODE_NUMPAD_ADD,KeyEvent.KEYCODE_NUMPAD_SUBTRACT,KeyEvent.KEYCODE_SLASH,
            KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_0, KeyEvent.KEYCODE_NUMPAD_DOT, // row 4
    };

    private int mPinyinKeyMap[] = {
            KeyEvent.KEYCODE_Q, KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_E, KeyEvent.KEYCODE_R, KeyEvent.KEYCODE_T, 
            KeyEvent.KEYCODE_Y, KeyEvent.KEYCODE_U, KeyEvent.KEYCODE_I, KeyEvent.KEYCODE_O, KeyEvent.KEYCODE_P,
            KeyEvent.KEYCODE_NUMPAD_7, KeyEvent.KEYCODE_NUMPAD_8, KeyEvent.KEYCODE_NUMPAD_9,// row 1
            KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_S, KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_F, KeyEvent.KEYCODE_G, 
            KeyEvent.KEYCODE_H, KeyEvent.KEYCODE_J, KeyEvent.KEYCODE_K, KeyEvent.KEYCODE_L, KeyEvent.KEYCODE_COMMA, 
            KeyEvent.KEYCODE_NUMPAD_4, KeyEvent.KEYCODE_NUMPAD_5, KeyEvent.KEYCODE_NUMPAD_6,// row 2
            KeyEvent.KEYCODE_Z, KeyEvent.KEYCODE_X, KeyEvent.KEYCODE_C, KeyEvent.KEYCODE_V, KeyEvent.KEYCODE_B,
            KeyEvent.KEYCODE_N, KeyEvent.KEYCODE_M, KeyEvent.KEYCODE_PERIOD,KeyEvent.KEYCODE_UNKNOWN,KeyEvent.KEYCODE_DEL,
            KeyEvent.KEYCODE_NUMPAD_1, KeyEvent.KEYCODE_NUMPAD_2, KeyEvent.KEYCODE_NUMPAD_3,// row 3
            KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_0, KeyEvent.KEYCODE_NUMPAD_DOT, // row 4
    };

    private int mCangJieKeyMap[] = {
            KeyEvent.KEYCODE_Q, KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_E, KeyEvent.KEYCODE_R, KeyEvent.KEYCODE_T, 
            KeyEvent.KEYCODE_Y, KeyEvent.KEYCODE_U, KeyEvent.KEYCODE_I, KeyEvent.KEYCODE_O, KeyEvent.KEYCODE_P,
            KeyEvent.KEYCODE_NUMPAD_7, KeyEvent.KEYCODE_NUMPAD_8, KeyEvent.KEYCODE_NUMPAD_9,// row 1
            KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_S, KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_F, KeyEvent.KEYCODE_G, 
            KeyEvent.KEYCODE_H, KeyEvent.KEYCODE_J, KeyEvent.KEYCODE_K, KeyEvent.KEYCODE_L, KeyEvent.KEYCODE_X, 
            KeyEvent.KEYCODE_NUMPAD_4, KeyEvent.KEYCODE_NUMPAD_5, KeyEvent.KEYCODE_NUMPAD_6,// row 2
            KeyEvent.KEYCODE_CAPS_LOCK, KeyEvent.KEYCODE_C, KeyEvent.KEYCODE_V, KeyEvent.KEYCODE_B, KeyEvent.KEYCODE_N,
            KeyEvent.KEYCODE_M, KeyEvent.KEYCODE_COMMA, KeyEvent.KEYCODE_PERIOD, KeyEvent.KEYCODE_DEL,
            KeyEvent.KEYCODE_NUMPAD_1, KeyEvent.KEYCODE_NUMPAD_2, KeyEvent.KEYCODE_NUMPAD_3,// row 3
            KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_0, KeyEvent.KEYCODE_NUMPAD_DOT, // row 4
    };

    private int mZhuYinKeyMap[] = {
            KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_4,
            KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_0,
            KeyEvent.KEYCODE_NUMPAD_7, KeyEvent.KEYCODE_NUMPAD_8, KeyEvent.KEYCODE_NUMPAD_9, // row 0
            KeyEvent.KEYCODE_Q, KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_E, KeyEvent.KEYCODE_R, KeyEvent.KEYCODE_T, 
            KeyEvent.KEYCODE_Y, KeyEvent.KEYCODE_U, KeyEvent.KEYCODE_I, KeyEvent.KEYCODE_O, KeyEvent.KEYCODE_P,
            KeyEvent.KEYCODE_NUMPAD_4, KeyEvent.KEYCODE_NUMPAD_5, KeyEvent.KEYCODE_NUMPAD_6,// row 1
            KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_S, KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_F, KeyEvent.KEYCODE_G, 
            KeyEvent.KEYCODE_H, KeyEvent.KEYCODE_J, KeyEvent.KEYCODE_K, KeyEvent.KEYCODE_L, KeyEvent.KEYCODE_SEMICOLON, 
            KeyEvent.KEYCODE_NUMPAD_1, KeyEvent.KEYCODE_NUMPAD_2, KeyEvent.KEYCODE_NUMPAD_3,// row 2
            KeyEvent.KEYCODE_Z, KeyEvent.KEYCODE_X, KeyEvent.KEYCODE_C, KeyEvent.KEYCODE_V, KeyEvent.KEYCODE_B,
            KeyEvent.KEYCODE_N, KeyEvent.KEYCODE_M, KeyEvent.KEYCODE_COMMA,KeyEvent.KEYCODE_PERIOD, KeyEvent.KEYCODE_SLASH,
            KeyEvent.KEYCODE_NUMPAD_0,KeyEvent.KEYCODE_NUMPAD_DOT,// row 3
            KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_MINUS,
            KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DEL, // row 4
   };

    private int mapKey(int keycode) {
        // remap right shift/ctrl/.
        switch (keycode) {
            case KeyEvent.KEYCODE_CTRL_RIGHT:
                keycode = KeyEvent.KEYCODE_CTRL_LEFT;
                break;
            case KeyEvent.KEYCODE_SHIFT_RIGHT:
                keycode = KeyEvent.KEYCODE_SHIFT_LEFT;
                break;
            case KeyEvent.KEYCODE_ALT_RIGHT:
                keycode = KeyEvent.KEYCODE_ALT_RIGHT;
                break;
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                keycode = KeyEvent.KEYCODE_ENTER;
                break;
            default:
                break;
        }

        if (mKeyboardList.getCurrentKeyboard().isEnglish() || mKeyboardList.getCurrentKeyboard().isNumberSymbol()
                || mKeyboardList.getCurrentKeyboard().isShiftSymbol()) {
            // remap number key to left numpad
            switch (keycode) {
                case KeyEvent.KEYCODE_1:
                    keycode = KeyEvent.KEYCODE_NUMPAD_1;
                    break;
                case KeyEvent.KEYCODE_2:
                    keycode = KeyEvent.KEYCODE_NUMPAD_2;
                    break;
                case KeyEvent.KEYCODE_3:
                    keycode = KeyEvent.KEYCODE_NUMPAD_3;
                    break;
                case KeyEvent.KEYCODE_4:
                    keycode = KeyEvent.KEYCODE_NUMPAD_4;
                    break;
                case KeyEvent.KEYCODE_5:
                    keycode = KeyEvent.KEYCODE_NUMPAD_5;
                    break;
                case KeyEvent.KEYCODE_6:
                    keycode = KeyEvent.KEYCODE_NUMPAD_6;
                    break;
                case KeyEvent.KEYCODE_7:
                    keycode = KeyEvent.KEYCODE_NUMPAD_7;
                    break;
                case KeyEvent.KEYCODE_8:
                    keycode = KeyEvent.KEYCODE_NUMPAD_8;
                    break;
                case KeyEvent.KEYCODE_9:
                    keycode = KeyEvent.KEYCODE_NUMPAD_9;
                    break;
                case KeyEvent.KEYCODE_0:
                    keycode = KeyEvent.KEYCODE_NUMPAD_0;
                    break;
                default:
                    break;
            }
        }

        if (mKeyboardList.getCurrentKeyboard().isNumberSymbol()) {
            debugLog("map english symbol keyboard.");
            for (int i = 0; i < mEnglishSymbolKeyMap.length; i++) {
                if (keycode == (mEnglishSymbolKeyMap[i])) {
                    return i;
                }
            }
            return -1;
        } else if (mKeyboardList.getCurrentKeyboard().isShiftSymbol()) {
            debugLog("map chinese symbol keyboard.");
            for (int i = 0; i < mChineseSymbolKeyMap.length; i++) {
                if (keycode == (mChineseSymbolKeyMap[i])) {
                    return i;
                }
            }
            return -1;
        } else if (mKeyboardList.getCurrentKeyboard().isEnglish()) {
            debugLog("map english keyboard.");
            for (int i = 0; i < mEnglishKeyMap.length; i++) {
                if (keycode == (mEnglishKeyMap[i])) {
                    if(i == 36 && isShiftKeys){
                        isShiftKeys = false;
                        return -1;
                    }else{
                        return i; 
                   }
                }
            }
            return -1;
        }

        if (LOCAL_IME_CANGJIE.equals(getCurrentImeName())) {
            debugLog("map cangjie keyboard.");
            for (int i = 0; i < mCangJieKeyMap.length; i++) {
                if (keycode == (mCangJieKeyMap[i])) {
                    return i;
                }
            }
        } else if (LOCAL_IME_PINYIN.equals(getCurrentImeName())) {
            debugLog("map pinyin keyboard.");
            for (int i = 0; i < mPinyinKeyMap.length; i++) {
                if (keycode == (mPinyinKeyMap[i])) {
                    return i;
                }
            }
        } else if (LOCAL_IME_ZHUYIN.equals(getCurrentImeName())) {
            debugLog("map zhuyin keyboard.");
            for (int i = 0; i < mZhuYinKeyMap.length; i++) {
                if (keycode == (mZhuYinKeyMap[i])) {
                    return i;
                }
            }
            return -1;
        }

        return -1;
    }

    // Frank Patch End

    /**
     * pick suggestion manually.
     * 
     * @param index position of words.
     */
    public void pickSuggestionManually(int index) {
        int size = mCandidatesContainer.getSuggestions().size();
        if (size == 0 || index < 0 || index >= size) {
            return;
        }

        debugLog("pickSuggestionManually index = " + index);

        if (!isInCandidatesRang) {
            isInCandidatesRang = true;
        }

        StringBuilder builder = new StringBuilder(mCandidatesContainer.getSuggestions().get(index));
        mTextEditor.setComposingText(builder);
        if (mTextEditor.composingText().length() > 0) {
            Log.d(TAG, "mTextEditor.composingText()--"+mTextEditor.composingText().toString());
            commitTyped(getCurrentInputConnection());
            
        }
        mWordDictionary.chooseDecodingCandidate(mCandidatesContainer.getCandIndexInAll(index));

        InputConnection ic = getCurrentInputConnection();
        if (null != ic) {
            // If candidates list is empty we go to predict mode using last
            // input chinese word as the histor.
            // When we are already in predict mode ,we still keep predicting
            // unless someone click or choose a key to input so we will go to
            // input mode.
            if (mWordDictionary.isCandidatesListEmpty() || DecodingInfo.mImeState == ImeState.STATE_PREDICT) {
                CharSequence cs = ic.getTextBeforeCursor(1, 0);
                DecodingInfo.mImeState = ImeState.STATE_PREDICT;
                mWordDictionary.preparePredicts(cs);
                mTextEditor.setComposingText(new StringBuilder(cs));
            } else {
                DecodingInfo.mImeState = ImeState.STATE_COMPOSING;
            }
            updateCandidates();
        }

        if (mInputView != null) {
            mInputView.invalidateAllKeys();
        }
    }

    @Override
    public void onNextPageClick(View candidateView) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPreviousPageClick(View candidateView) {
        // TODO Auto-generated method stub

    }

    private void bindKeyboardToInputView() {
        if (mInputView != null) {
            // Bind the selected keyboard to the input view.
            mInputView.setKeyboard(mKeyboardList.getCurrentKeyboard());
            mInputView.setCurrentKeyIndex(mKeyStartIndex);
            // mInputView.setKeyPressed(mKeyStartIndex, true);
            // mInputView.setCurrentKeyIndex(mKeyStartIndex);
            Animation animation = mInputView.getAnimation();
            if (animation != null) {
                Constans.print(TAG, "animation not null");
            }
            final ViewTreeObserver observer = mInputView.getViewTreeObserver();
            // 设置初始高亮的位置.
            observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (!observer.isAlive()) {
                        ViewTreeObserver observer = mInputView.getViewTreeObserver();
                        observer.removeOnGlobalLayoutListener(this);
                    } else {
                        observer.removeOnGlobalLayoutListener(this);
                    }
                    // mInputView.setKeyPressed(mKeyStartIndex, true);

                    mHandler.removeMessages(KEY_BLINK_ANIMATE_STEP1);
                    mHandler.removeMessages(KEY_BLINK_ANIMATE_STEP2);

                    mInputView.setCurrentKeyIndex(mKeyStartIndex);
                    nLastKeyIndex = mKeyStartIndex;
                    Constans.print(TAG, " " + mInputView.getKeyboard().getKeys().get(mKeyStartIndex).pressed);

                    Message msg = mHandler.obtainMessage();
                    msg.what = INVALIDATE_KEY;
                    msg.arg1 = mKeyStartIndex;
                    msg.obj = true;
                    mHandler.sendMessageDelayed(msg, 100);

                }
            });

            mTextEditor.clearComposingText(getCurrentInputConnection());
        }

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        Constans.print(TAG, "isMouseKeyDown " + isIRKeyDown);
        Log.d(TAG, "primaryCode----"+primaryCode);

        if (!isIRKeyDown && primaryCode != LatinKeyboard.KEYCODE_MODE_CHANGE_ZH_EN
                && primaryCode != LatinKeyboard.KEYCODE_MODE_CHANGE_2SYMBOL) {
            onKeyClick(primaryCode, keyCodes);
        }

        // FIXME 特殊按键处理:-7,-8
        if(!isHardKeyboard&&(primaryCode == -7||primaryCode == -8)){
            isHardKeyboard =true;
            isShiftKeys = true;
            Log.d(TAG, "isHardkeyboard----"+isHardKeyboard);
            return;
        }else{
         if(mKeyboardList.onKey(primaryCode)) {
            Log.d(TAG, "isHardkeyboard----"+isHardKeyboard);
            
            isIRKeyDown = false;
            Log.d(TAG, "bindKeyboardToInputView----"+isHardKeyboard);
            bindKeyboardToInputView();
            mCandidatesContainer.setCandidates(null, false, false);
            return;
        
        }
        }
        
        
        if (handleLongStringKey(primaryCode)) {
            isIRKeyDown = false;
            return;
        }
        if (isWordSeparator(primaryCode)) {
            isIRKeyDown = false;
            Log.d(TAG, "------isWordSeparator");
            handleSeparator(primaryCode);
            return;
        }

        // FIXME 特殊字符处理

        // FIXME 常规字符处理.
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                handleBackspace();
                break;
            case Keyboard.KEYCODE_SHIFT:
                if(mKeyboardList.getCurrentKeyboard().isKorean()){
                    break;
                }else{
                    
                    handleShift();
                }
                break;
            case Keyboard.KEYCODE_CANCEL:
                handleClose();
                break;
            case LatinKeyboard.KEYCODE_IME_CHANGE:
                String imeString = getCurrentImeName();
                if(LOCAL_IME_ARABIC.equals(imeString)||LOCAL_IME_PERSIAN.equals(imeString)||
                        LOCAL_IME_FRENCH.equals(imeString)||LOCAL_IME_PINYIN.equals(imeString)
                        ||LOCAL_IME_KOREAN.equals(imeString)||LOCAL_IME_JAPAN.equals(imeString)){
                    break;
                }else{
                    handleIMEChange(primaryCode);
                   
                }
                break;

            case 97:
            case 105:
            case 117:
            case 121:
            case 110:
            case 99:
                if ( mKeyboardList.getCurrentKeyboard().isFrench()) {
                   
                    MoreKeyViewFactory.showMoreKeyView(this, keyCodes,primaryCode,isShifted);
                } else {
                    handleCharacter(primaryCode, keyCodes);
                }
                break;
           case 1609:
           case 1608:
           case 1586:
           case 1603:
           case 1575:
           case 1604:
           case 1576:
           case 1610:
           case 1580:
           case 1601:
           case 1588:
           case 1611:
           case 1578:
                MoreKeyViewFactory.showMoreKeyView(this, keyCodes, primaryCode, isShifted);
                break;
           case 1607:
               if(LatinKeyboard.isArabicIME(this)){
                   MoreKeyViewFactory.showMoreKeyView(this, keyCodes, primaryCode, isShifted);
               }else{
                   handleCharacter(primaryCode, keyCodes);
               }
           case 113:
           case 119: //ㅈ
           case 114: //ㄱ
           case 116://ㅅ
           case 112://ㅔ
               if(mKeyboardList.getCurrentKeyboard().isKorean()){
                   
                   MoreKeyViewFactory.showMoreKeyView(this, keyCodes, primaryCode, isShifted);
               }else{
                   handleCharacter(primaryCode, keyCodes);
               }
               break;
           case 111://ㅐ
           case 101://ㄷ
               if ( mKeyboardList.getCurrentKeyboard().isFrench()) {
                   
                   MoreKeyViewFactory.showMoreKeyView(this, keyCodes,primaryCode,isShifted);
               } else if(mKeyboardList.getCurrentKeyboard().isKorean()){
                   MoreKeyViewFactory.showMoreKeyView(this, keyCodes,primaryCode,isShifted);
               }else{
                   handleCharacter(primaryCode, keyCodes);
                   
               }
               break;
            default:
                handleCharacter(primaryCode, keyCodes);
                break;

        }
        isIRKeyDown = false;

    }

    private boolean handleLongStringKey(int primaryCode) {

        int start = mKeyboardList.getCurrentKeyboard().getResoucesStartIndex();

        if (primaryCode <= start) {

            String str = mKeyboardList.getCurrentKeyboard().getStringByCodes(primaryCode);
            try {
                getCurrentInputConnection().commitText(str, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    protected void updateCandidates() {
        Constans.print(
                TAG,
                "composing.length : " + mTextEditor.composingText().length() + " composing "
                        + mTextEditor.composingText());

        // mCandidatesContainer.updateCharInputTip(mComposing.toString());

        if (DecodingInfo.mImeState == ImeState.STATE_INPUT) {
            if (mTextEditor.composingText().length() == 0) {
                mCandidatesContainer.setCandidates(null, false, false);
            } else {
                List<String> candidateList = mWordDictionary.getWords(mTextEditor.composingText().toString());

                if (mWordDictionary.isPinyinDictionary()
                        && mTextEditor.composingText().length() > mWordDictionary.getOrigianlSplStr()) {
                    mTextEditor.delete(mTextEditor.composingText().length() - 1, mTextEditor.composingText().length());
                }

                if (candidateList.size() == 0) {
                    candidateList.add(mTextEditor.composingText().toString());
                }
                mCandidatesContainer.setCandidates(candidateList, false, false);
                mCandidatesContainer.updateCharInputTip(mWordDictionary.getComposingStrForDisplay());
            }
        } else if (DecodingInfo.mImeState == ImeState.STATE_COMPOSING) {
            List<String> candidateList = mWordDictionary.getWords(null);
            mCandidatesContainer.setCandidates(candidateList, false, false);
            mCandidatesContainer.updateCharInputTip(mWordDictionary.getComposingStrForDisplay());
        } else if (DecodingInfo.mImeState == ImeState.STATE_PREDICT) {
            List<String> candidateList = mWordDictionary.getWords(mTextEditor.composingText().toString());
            mCandidatesContainer.setCandidates(candidateList, false, false);
            mCandidatesContainer.updateCharInputTip(mTextEditor.composingText().toString());
        }

    }
    /**
     * 处理字符
     * @param primaryCode
     * @param keyCodes
     */
  
    public  void handleCharacter(int primaryCode, int[] keyCodes) {
        Constans.print(TAG, "handleCharacter");
        if (isInputViewShown()) {
            if (mInputView.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
        }

        if (isAlphabet(primaryCode)) {

            if (mKeyboardList.getCurrentKeyboard().isChinese()) {
                Log.d(TAG, "mKeyboardList.getCurrentKeyboard().isChinese() = true");

                if (mTextEditor.composingText().length() >= MAX_CODE_LENGTH) {
                    return;
                }
                if (DecodingInfo.mImeState != ImeState.STATE_INPUT) {
                    DecodingInfo.mImeState = ImeState.STATE_INPUT;
                    mTextEditor.setLength(0);
                    mWordDictionary.getWords(" ");
                }

                DecodingInfo.mImeState = ImeState.STATE_INPUT;

                if ((!mWordDictionary.isPinyinDictionary())
                        && mTextEditor.composingText().length() >= mWordDictionary.getMaxComposing()) {
                    return;
                } 
                mTextEditor.append((char) primaryCode);
                if(LatinKeyboard.isJapanIME(this)){
                char[]chars = new char[1];
                chars[0]=(char)primaryCode;
                appendStrSegment(new StrSegment(chars));
                mRomkan.convert(backComposingText());
                }
                updateCandidates();
            } else {
                if(mKeyboardList.getCurrentKeyboard().isKorean()){
                    convertKorean(primaryCode);
                }else{
                    mTextEditor.append((char) primaryCode);
                   commitTyped(getCurrentInputConnection());
                    updateShiftKeyState(getCurrentInputEditorInfo());
                    mCandidatesContainer.setCandidates(null, false, false);
                }

            }
        } else {
            try {
                DecodingInfo.mImeState = ImeState.STATE_IDLE;
                getCurrentInputConnection().commitText(String.valueOf((char) primaryCode), 1);
                updateCandidates();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleShift() {
        debugLog("handleShift");
        if (mInputView == null) {
            return;
        }
        isShifted = mInputView.isShifted();
        Log.d(TAG, "--------------->"+isShifted);
        if (mKeyboardList.getCurrentKeyboard().isEnglish()) {
            LatinKeyboard currentKeyboard = mKeyboardList.getCurrentKeyboard();
            checkToggleCapsLock();
            mInputView.setShifted(mCapsLock || !mInputView.isShifted());
            int index = currentKeyboard.mShiftKeyIndex;
            mInputView.setKeyPressed(index, true);
            mInputView.invalidateKey(index);
            if (!isIRKeyDown) {
                Message msg = mHandler.obtainMessage();
                msg.arg1 = nLastKeyIndex;
                msg.what = INVALIDATE_KEY;
                msg.obj = false;
                mHandler.sendMessage(msg);

            }
            mInputView.getLatinKeyboard().updateShiftLabel();
        } else {
            mInputView.setShifted(!mInputView.isShifted());
            updateCandidates();
        }
    }

    // private long mLastShiftTime = 0;

    private void checkToggleCapsLock() {
        // debugLog("checkToggleCapsLock");
        // long now = System.currentTimeMillis();
        // debugLog("checkToggleCapsLock mCapsLock = " + mCapsLock);
        //
        // if (mLastShiftTime + 800 > now || mCapsLock) {
        // mCapsLock = !mCapsLock;
        // mLastShiftTime = 0;
        // } else {
        // mLastShiftTime = now;
        // }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
        debugLog("updateShiftKeyState");

        // if (attr != null && mInputView != null &&
        // mKeyboardList.getCurrentKeyboard().isEnglish()) {
        // int caps = 0;
        // EditorInfo ei = getCurrentInputEditorInfo();
        // if (ei != null && ei.inputType != InputType.TYPE_NULL) {
        // caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
        // }
        // boolean isShiftedBefore = mInputView.isShifted();
        // mInputView.setShifted(mCapsLock || caps != 0);
        //
        // if (isShiftedBefore && !mInputView.isShifted()) {
        // mInputView.setKeyPressed(nLastKeyIndex, true);
        // mInputView.invalidateAllKeys();
        // }
        //
        // mInputView.getLatinKeyboard().updateShiftLabel();
        //
        // }
    }

    public   static ImeSelectDialog mSwitchingDialog;

    public Map<Integer, String> mLocalIMEs = new HashMap<Integer, String>();

    private void handleIMEChange(int primaryCode) {
        Constans.print(TAG, "handleIMEChanged");
        if (mSwitchingDialog != null && mSwitchingDialog.isShowing()) {
            return;
        }
        mSwitchingDialog = new ImeSelectDialog(this);
        mSwitchingDialog.show();

    }
    private void dismissImeSelectDialog() {
        if (mSwitchingDialog != null && mSwitchingDialog.isShowing()) {
           mSwitchingDialog.dismiss();
        }
    }

    public abstract String getCurrentImeName();

    private void handleClose() {
        debugLog("handleClose");
        try {
            commitTyped(getCurrentInputConnection());
            requestHideSelf(0);
            mInputView.closing();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleBackspace() {
        debugLog("handleBackspace");

        final int length = mTextEditor.composingText().length();
        Log.d(TAG,"composingText---"+ mTextEditor.composingText().toString()+"------lenght----"+length);
        if(mKeyboardList.getCurrentKeyboard().isKorean()){
            deleteKoreanChar();
        }
        if (length >= 1 && !mKeyboardList.getCurrentKeyboard().isKorean()) {
            mTextEditor.delete(length - 1, length);
            // getCurrentInputConnection().setComposingText(mTextEditor.composingText(),
            // 1);
//            if(mComposingText.)
            if(LatinKeyboard.isJapanIME(this)){
            Log.d("raymond", "mString"+mComposingText.toString(1));
            if ((mComposingText.size(ComposingText.LAYER1) == 1)
                    && mComposingText.getCursor(ComposingText.LAYER1) != 0){
                mComposingText.clear();
            }else{
                mComposingText.delete(ComposingText.LAYER1, false);
            }
           Log.d("raymond", "mString2"+mComposingText.toString(1));
            }
            updateCandidates();
        } else {
            if (mCandidatesContainer.isShown()) {
                mCandidatesContainer.setCandidates(null, false, false);
            } else {
                keyDownUp(KeyEvent.KEYCODE_DEL);
            }
        }
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        debugLog("keyDownUp");
        try {
            getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
            getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSeparator(int primaryCode) {
        // if (mTextEditor.composingText().length() > 0) {
        // commitTyped(getCurrentInputConnection());
        // }
        if (mKeyboardList.getCurrentKeyboard().isChinese()) {
            handleChineseSeparator(primaryCode);
        } else {
            if (mTextEditor.composingText().length() > 0) {
                commitTyped(getCurrentInputConnection());
            }
            sendKey(primaryCode);
            updateShiftKeyState(getCurrentInputEditorInfo());
        }
    }

    private void handleChineseSeparator(int primaryCode) {
        if (mCandidatesContainer.isShown() && mCandidatesContainer.getCandidatesLength() > 0) {

            String commitText = "";

            if (mCandidatesContainer.getSuggestions().size() > 0) {
                try {
                    int index = mCandidatesContainer.getHighlightIndex();
                    if (index < mCandidatesContainer.getSuggestions().size() && index >= 0) {
                        commitText = mCandidatesContainer.getSuggestions().get(index);
                    } else {
                        commitText = mCandidatesContainer.getSuggestions().get(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            getCurrentInputConnection().commitText(commitText, 1);
        }

        mTextEditor.setLength(0);

        sendKey(primaryCode);
        updateShiftKeyState(getCurrentInputEditorInfo());
        mCandidatesContainer.updateCharInputTip(null);
        mCandidatesContainer.setCandidates(null, false, false);
    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        debugLog("sendKey");
        switch (keyCode) {
            case '\n':
                if (mCandidatesContainer.isShown()) {// 如果存在候选区,提交候选区内容.
                    pickSuggestionManually(mCandidatesContainer.getHighlightIndex());
                } else if (mTextEditor.treatEnterAsLinkBreak()) {
                    mTextEditor.commitText(getCurrentInputConnection(), "\n");
                } else {// 否则,
                    sendKeyChar('\n');
                    // keyDownUp(KeyEvent.KEYCODE_ENTER);
                }

                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    try {
                        getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * if word is a separator.
     * 
     * @param code code of a word.
     * @return if it's a separator.
     */
    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char) code));
    }

    private String getWordSeparators() {
        if (mWordSeparators == null) {
            mWordSeparators = "\u0020.,;:!?\n()[]*&amp;@{}/&lt;&gt;_+=|&quot;";
        }
        return mWordSeparators;
    }

    private void onKeyClick(int primaryCode, int[] keyCodes) {

        mHandler.removeMessages(KEY_BLINK_ANIMATE_STEP1);
        mHandler.removeMessages(KEY_BLINK_ANIMATE_STEP2);

        if (mInputView == null) {
            return;
        }

        if (isInCandidatesRang) {
            isInCandidatesRang = false;
            mCandidatesContainer.setHighlightIndex(-1);
        }

        int index = -1;

        List<Key> keys = mInputView.getKeyboard().getKeys();
        if (primaryCode == LatinKeyboard.KEYCODE_SPACE) {
            // in case of multiple space key in same one keyboard , we get
            // theirs index by the position we touched.
            index = mInputView.getSpaceIndexByX();
        } else {
            for (int i = 0; i < keys.size(); i++) {
                if (primaryCode == keys.get(i).codes[0]) {
                    index = i;
                    break;
                }
            }
        }

        if (index < 0 || index >= keys.size()) {
            return;
        }

        int lastIndex = nLastKeyIndex;
        nLastKeyIndex = index;
        mInputView.setCurrentKeyIndex(nLastKeyIndex);

        Constans.print(TAG, "lastIndex = " + lastIndex + " nLastKeyCode = " + nLastKeyIndex);

        // keys.get(nLastKeyIndex).onPressed();
        mInputView.setKeyPressed(nLastKeyIndex, true);
        refreshKeyBoard(lastIndex, nLastKeyIndex);
        Message msg = mHandler.obtainMessage();

        msg.arg1 = nLastKeyIndex;
        msg.what = INVALIDATE_KEY;
        msg.obj = false;
        if (lastIndex != nLastKeyIndex || keys.get(nLastKeyIndex).repeatable) {
            mHandler.sendMessage(msg);
        }

        // if (keys.get(nLastKeyIndex).repeatable) {
        // mHandler.sendMessageDelayed(msg, 300);
        // }
    }

    @Override
    public void onPress(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRelease(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onText(CharSequence arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void swipeDown() {
        // TODO Auto-generated method stub

    }

    @Override
    public void swipeLeft() {
        // TODO Auto-generated method stub

    }

    @Override
    public void swipeRight() {
        // TODO Auto-generated method stub

    }

    @Override
    public void swipeUp() {
        // TODO Auto-generated method stub

    }

    private void handleRight() {
        int candidatesIndex;
        setFields();

        if (nLastKeyIndex < 0) {
            mInputView.setCurrentKeyIndex(mKeyStartIndex);
            nKeys.get(mKeyStartIndex).onPressed();
            mInputView.invalidateKey(mKeyStartIndex);
            return;
        }

        if (isInCandidatesRang) {
            if (mCandidatesContainer.isShown()) {
                candidatesIndex = mCandidatesContainer.getHighlightIndex();
                candidatesIndex++;
                if ((candidatesIndex >= Constans.SUGGESTIONS_PER_PAGE)
                        || (candidatesIndex >= mCandidatesContainer.getCandidatesLength())) {

                    if (mCandidatesContainer.hasNextPage()) {
                        mCandidatesContainer.showNextPage(0);
                        mCandidatesContainer.setHighlightIndex(0);
                    }

                } else {
                    mCandidatesContainer.setHighlightIndex(candidatesIndex);
                }
            }
        } else {
            mCandidatesContainer.setHighlightIndex(-1);
            int keyIndex = right(nLastKeyIndex);
            if (keyIndex != -1) {
                if (null == mInputView) {
                    return;
                }
                nKeys.get(keyIndex).onPressed();
                mInputView.setCurrentKeyIndex(keyIndex);
                refreshKeyBoard(nLastKeyIndex, keyIndex);
            } else {
                int lastIndex = nLastKeyIndex;
                nLastKeyIndex++;
                nKeys.get(nLastKeyIndex).onPressed();
                mInputView.setCurrentKeyIndex(nLastKeyIndex);
                refreshKeyBoard(lastIndex, nLastKeyIndex);
            }
        }
    }

    private void handleLeft() {
        int candidatesIndex;
        setFields();

        // the first time key pressed ,resurrect the selector.
        if (nLastKeyIndex < 0) {
            mInputView.setCurrentKeyIndex(mKeyStartIndex);
            nKeys.get(mKeyStartIndex).onPressed();
            mInputView.invalidateKey(mKeyStartIndex);
            return;
        }

        // the focus is in the candidate view
        if (isInCandidatesRang) {
            if (mCandidatesContainer.isShown()) {
                candidatesIndex = mCandidatesContainer.getHighlightIndex();
                candidatesIndex--;
                if (candidatesIndex >= 0) {
                    mCandidatesContainer.setHighlightIndex(candidatesIndex);
                } else {
                    if (mCandidatesContainer.hasPrePage()) {
                        mCandidatesContainer.showPrePage(0);
                        mCandidatesContainer.setHighlightIndex(mCandidatesContainer.getCandidatesLength() - 1);
                    }
                }
            }
        } else {
            // the focus is in the inputview
            mCandidatesContainer.setHighlightIndex(-1);
            // come to the last key when selector at position 0 and key left
            // pressed
            int keyIndex = left(nLastKeyIndex);
            if (keyIndex != -1) {
                if (null == mInputView) {
                    return;
                }
                nKeys.get(keyIndex).onPressed();
                mInputView.setCurrentKeyIndex(keyIndex);
                refreshKeyBoard(nLastKeyIndex, keyIndex);
            } else {
                int lastIndex = nLastKeyIndex;
                nLastKeyIndex--;
                nKeys.get(nLastKeyIndex).onPressed();
                mInputView.setCurrentKeyIndex(nLastKeyIndex);
                refreshKeyBoard(lastIndex, nLastKeyIndex);
            }
        }
    }

    private void handleUp() {
        setFields();
        // focus in the candidate ,do nothing
        if (isInCandidatesRang) {
            return;
        }

        debugLog("mInputView.getLastKeyIndex() = " + mInputView.getLastKeyIndex());
        debugLog("nLastKeyIndex = " + nLastKeyIndex);

        // the first time key pressed ,resurrect the selector.
        if (nLastKeyIndex < 0) {
            mInputView.setCurrentKeyIndex(mKeyStartIndex);
            nKeys.get(mKeyStartIndex).onPressed();
            mInputView.invalidateKey(mKeyStartIndex);
            return;
        }

        if ((nLastKeyIndex < ROW_KEY_NUM) && (mCandidatesContainer.getCandidatesLength() > 0) && mCandidatesContainer.getIsCandidateViewShow()) {
            // focus goes to candidate view
            isInCandidatesRang = true;

            int index = mInputView.getLastKeyIndex();

            while (index >= 0 && index >= mCandidatesContainer.getSuggestions().size()) {
                index--;
            }

            mCandidatesContainer.setHighlightIndex(index);
            mInputView.invalidateKey(mInputView.getLastKeyIndex());
            mInputView.setinCandidatesRang(isInCandidatesRang);

        } else {
            // go to last key
            mCandidatesContainer.setHighlightIndex(-1);
            if (nLastKeyIndex <= 0) {
                if (null == mInputView) {
                    return;
                }
                nKeys.get(nCurKeyboardKeyNums - 1).onPressed();
                mInputView.setCurrentKeyIndex(nCurKeyboardKeyNums - 1);
                refreshKeyBoard(0, nCurKeyboardKeyNums - 1);
            } else {
                // go to up side key postion
                int[] nearestKeyIndices = nCurrentKeyboard.getNearestKeys(nKeys.get(nLastKeyIndex).x,
                        nKeys.get(nLastKeyIndex).y);
                for (int i = nearestKeyIndices.length - 1; i >= 0; i--) {
                    int index = nearestKeyIndices[i];
                    if (nLastKeyIndex > index) {
                        Key nearKey = nKeys.get(index);// get the next
                        // key
                        Key nextNearKey = nKeys.get(index + 1);
                        Key lastKey = nKeys.get(nLastKeyIndex);// get
                        // current
                        // displayed
                        int nearWidth = nearKey.width - Constans.KEY_WIDTH_OFFSET;
                        if (((lastKey.x >= nearKey.x) && (lastKey.x < (nearKey.x + nearWidth)) && (((lastKey.x + lastKey.width) <= (nextNearKey.x + nextNearKey.width)) || ((lastKey.x + lastKey.width) > nextNearKey.x)))) {
                            int lastIndex = nLastKeyIndex;
                            nKeys.get(index).onPressed();
                            mInputView.setCurrentKeyIndex(index);
                            nLastKeyIndex = index;
                            refreshKeyBoard(lastIndex, nLastKeyIndex);
                            break;
                        }
                    }
                }// end for loop
            }
        }
    }

    private void debugLog(String string) {
        Log.i(TAG, string);
    }

    private void handleDown() {
        setFields();

        if (nLastKeyIndex < 0) {
            mInputView.setCurrentKeyIndex(mKeyStartIndex);
            nKeys.get(mKeyStartIndex).onPressed();
            mInputView.invalidateKey(mKeyStartIndex);
            return;
        }

        if (isInCandidatesRang) {
            int index = mCandidatesContainer.getHighlightIndex();
            isInCandidatesRang = false;
            mCandidatesContainer.setHighlightIndex(-1);
            mInputView.setinCandidatesRang(isInCandidatesRang);

            if (index < 0 || index >= mInputView.getKeyboard().getKeys().size()) {
                index = mKeyStartIndex;
            }

            nKeys.get(index).onPressed();
            mInputView.setCurrentKeyIndex(index);
            mInputView.invalidateKey(index);

        } else {
            mCandidatesContainer.setHighlightIndex(-1);
            if (nLastKeyIndex >= nCurKeyboardKeyNums - 1) {
                if (null == mInputView) {
                    return;
                }
                nKeys.get(0).onPressed();
                mInputView.setCurrentKeyIndex(0);
                refreshKeyBoard(nCurKeyboardKeyNums - 1, 0);
            } else {
                int[] nearestKeyIndices = nCurrentKeyboard.getNearestKeys(nKeys.get(nLastKeyIndex).x,
                        nKeys.get(nLastKeyIndex).y);
                for (int index : nearestKeyIndices) {
                    if (nLastKeyIndex < index) {
                        Key nearKey = nKeys.get(index);
                        Key lastKey = nKeys.get(nLastKeyIndex);
                        int nearWidth = nearKey.width - Constans.KEY_WIDTH_OFFSET;
                        if (((lastKey.x >= nearKey.x) // left side
                                                      // compare
                                && (lastKey.x < (nearKey.x + nearWidth)))
                                || (((lastKey.x + nearWidth) > nearKey.x) // right
                                        // side
                                        // compare
                                        && ((lastKey.x + nearWidth) <= (nearKey.x + nearWidth)) && nearKey.y > lastKey.y)) {
                            int lastIndex = nLastKeyIndex;
                            nKeys.get(index).onPressed();
                            mInputView.setCurrentKeyIndex(index);
                            nLastKeyIndex = index;
                            refreshKeyBoard(lastIndex, nLastKeyIndex);
                            break;
                        }
                    }
                }// end for loop
            }

        }
    }

    private void setFields() {
        if (null == mInputView) {
            return;
        }

        nCurrentKeyboard = mInputView.getKeyboard();
        nKeys = nCurrentKeyboard.getKeys();
        nCurKeyboardKeyNums = nKeys.size();
        nLastKeyIndex = mInputView.getLastKeyIndex();

        debugLog("nLastKeyIndex ==>" + nLastKeyIndex);
    }

    protected void refreshKeyBoard(int lastIndex, int currentIndex) {

        if (mInputView != null) {
            mInputView.invalidateAllKeys();
            mInputView.setKeyPressed(lastIndex, false);
            debugLog("refreshKeyBoard INDEX= " + lastIndex);
        }

    }

    protected final int sendWords2iSynergy = 0x02;

    protected final int refreshIsynergyIcon = 0x03;

    protected boolean mIsAvailable = false;

    @SuppressLint("HandlerLeak")
    protected Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case INVALIDATE_KEY:
                    debugLog("set unpress");
                    boolean pressed = (Boolean) msg.obj;
                    mInputView.setKeyPressed(msg.arg1, pressed);
                    debugLog("handleMessage INDEX= " + msg.arg1);
                    mInputView.invalidateAllKeys();
                    break;
                case KEY_BLINK_ANIMATE_STEP1:
                    int index = msg.arg1;
                    refreshKeyBoard(index, -1);

                    Message msgStep2 = this.obtainMessage();
                    msgStep2.arg1 = index;
                    msgStep2.what = KEY_SET_ALL_UNPRESSED;
                    this.sendMessage(msgStep2);

                    break;
                case KEY_SET_ALL_UNPRESSED:
                    setFields();
                    if (nKeys.size() > 0) {
                        for (int i = 0; i < nKeys.size(); i++) {
                            mInputView.setKeyPressed(i, false);
                        }
                    }

                    Message msgStep3 = this.obtainMessage();
                    msgStep3.arg1 = msg.arg1;
                    msgStep3.what = KEY_BLINK_ANIMATE_STEP2;
                    this.sendMessage(msgStep3);

                    break;
                case KEY_BLINK_ANIMATE_STEP2:

                    index = msg.arg1;
                    mInputView.setCurrentKeyIndex(index);
                    nKeys.get(index).onPressed();
                    mInputView.invalidateAllKeys();
                    break;
                case sendWords2iSynergy:
                    Constans.print(TAG, "send:" + currentEditorWords);
                    try {
                        if (!TextUtils.isEmpty(currentEditorWords)) {
                            if (mIWordsSyncManager != null) {
                                mIWordsSyncManager.sendBySocket(Constans.TYPE_WORDS, currentEditorWords);
                            } else {
                                Constans.print(TAG, "mIWordsSyncManager is null.");
                            }
                        }
                    } catch (RemoteException e) {
                        // e.printStackTrace();
                        Constans.print(TAG, "RemoteException happended");
                    }

                    mOldEditorWords = currentEditorWords;
                    break;
                case refreshIsynergyIcon:
                    if (mInputView != null && mInputView.getKeyboard() != null) {

                        LatinKeyboard keyboard = (LatinKeyboard) mInputView.getKeyboard();
                        if (keyboard.isLogoOn() == mIsAvailable) {
                            return;
                        }
                        keyboard.setLogoOn(mIsAvailable);
                        mInputView.invalidateKey(keyboard.mLogoKeyIndex);

                        if (mIsAvailable) {

                            // testingCode:
                            String tempStr = getEditorText();
                            try {
                                if (mIWordsSyncManager != null && (!TextUtils.isEmpty(tempStr))) {
                                    Constans.print(TAG, "words2Isynergy:" + tempStr);
                                    mIWordsSyncManager.sendBySocket(Constans.TYPE_WORDS, tempStr);

                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }

                        break;
                    }
                default:
                    break;
            }

        }
    };

    private ExtractedTextRequest mExtractedRequest;

    protected String getEditorText() {
        if (mExtractedRequest == null) {
            mExtractedRequest = new ExtractedTextRequest();
            mExtractedRequest.flags = 0;
        }
        // ExtractedText txt =
        // getCurrentInputConnection().getExtractedText(mExtractedRequest,
        // InputConnection.GET_EXTRACTED_TEXT_MONITOR);
        try {
            ExtractedText txt = getCurrentInputConnection().getExtractedText(mExtractedRequest, 0);
            return txt.text.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    protected void commitTyped(InputConnection inputConnection) {
        debugLog("commitTyped");
        Constans.print(TAG, "it's new code in linux");
        if (mTextEditor.composingText().length() > 0) {
            if (inputConnection != null) {
                Log.d(TAG, "length--"+mTextEditor.composingText().length() 
                        +"composting---"+mTextEditor.composingText().toString());
                inputConnection.commitText(mTextEditor.composingText(), 1);

//                if (mKeyboardList.getCurrentKeyboard().isEnglish()) {
//                }
               mTextEditor.setLength(0);
                if(mComposingText != null){
                    mComposingText.clear();
                }

            }
//            updateCandidates();
        }
    }

    /**
     * Helper to determine if a given character code is alphabetic.
     */
    protected boolean isAlphabet(int code) {
        debugLog("isAlphabet");
        // if (Character.isLetter(code)) {
        // return true;
        // } else {
        // return false;
        // }

        return Character.isLetter(code);
    }

    /**
     * not enter full screen mode.
     * 
     * @return is full screen or not.
     */
    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }

    public void switchIme(String ime) {
        this.switchInputMethod(ime);
    }
    /**
     * 设置切换输入法弹窗位置坐标
     * @return
     */
    public int[] getPosition() {
        Key key = mKeyboardList.getCurrentKeyboard().getKeys().get(0);

        return new int[] {
                (int) getResources().getDimension(R.dimen.inputview_padding)+(int) getResources().getDimension(R.dimen.selectime_w), 
                key.height + (int)getResources().getDimension(R.dimen.selectime_h)
        // FIXME magic number.
        };
    }
   
    /**
     * set  morekeysboard position
     * @param primarycode
     * @return
     */
    public int[] getMoreKeysPosition( int primarycode) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayHeight = dm.heightPixels;
        List<Key> keys = mKeyboardList.getCurrentKeyboard().getKeys();
          switch (primarycode) {
            case 97: //a
                Key key1 =  keys.get(0);
                return new int[] {
                        mInputView.getPaddingLeft() + key1.x - (int) getResources().getDimension(R.dimen.morekeys_width)-Constans.dip2px(getApplicationContext(), 4),
                        // 屏幕高度 - 键盘的高度 + 按键距离上键盘距离 - 一个Dialog里键的高度 - Dialog里键的内边距 - 每个key的一半的间距
                        displayHeight - mInputView.getHeight() + mInputView.getPaddingTop() - (int) getResources().getDimension(R.dimen.morkeys_height) - Constans.dip2px(getApplicationContext(), 6+ 4)
                };
            case 101://e
                if(mKeyboardList.getCurrentKeyboard().isKorean()){
                    Key key25 = keys.get(2);
                    return new int[]{
                            mInputView.getPaddingLeft() + key25.x,
                            displayHeight + key25.y -mInputView.getHeight()+mInputView.getPaddingTop()
                    };
                }else{
                Key key2 =  keys.get(1);
                return new int[] {
                        mInputView.getPaddingLeft() + key2.x ,
                        displayHeight - mInputView.getHeight() + mInputView.getPaddingTop() - (int) getResources().getDimension(R.dimen.morkeys_height) - Constans.dip2px(getApplicationContext(), 6 + 4)
                };
                }
            case 105://i
                Key key3 = keys.get(6);
                return new int[] {
                        mInputView.getPaddingLeft() + key3.x +Constans.dip2px(getApplicationContext(), 0) / 2,
                        displayHeight - mInputView.getHeight() + mInputView.getPaddingTop() - (int) getResources().getDimension(R.dimen.morkeys_height) - Constans.dip2px(getApplicationContext(), 6 + 4)
                };
            case 111://o
                if(mKeyboardList.getCurrentKeyboard().isKorean()){
                    Key key28 = keys.get(8);
                    return new int[]{
                            mInputView.getPaddingLeft() + key28.x,
                            displayHeight + key28.y -mInputView.getHeight()+mInputView.getPaddingTop()
                    };
                }else{
                Key key4 =  keys.get(6);
                return new int[] {
                        mInputView.getPaddingLeft() + key4.x -Constans.dip2px(getApplicationContext(), 2) / 2,
                        displayHeight - mInputView.getHeight() + mInputView.getPaddingTop() - (int) getResources().getDimension(R.dimen.morkeys_height) - Constans.dip2px(getApplicationContext(), 6+ 4)
                };
                }
                
            case 117://u
                Key key5 =  keys.get(5);
                return new int[] {
                        mInputView.getPaddingLeft() + key5.x - Constans.dip2px(getApplicationContext(), 1) / 2,
                        displayHeight - mInputView.getHeight() + mInputView.getPaddingTop() - Constans.dip2px(getApplicationContext(),  2)
                };
            case 121://y
                Key key6 =  keys.get(4);
                return new int[] {
                        mInputView.getPaddingLeft() + key6.x-Constans.dip2px(getApplicationContext(), 1) / 2,
                        displayHeight - mInputView.getHeight() + mInputView.getPaddingTop() - Constans.dip2px(getApplicationContext(), 2 + 4)
                };
            case 99://c
                Key key7 =  keys.get(16);//f
                return new int[] {
                        mInputView.getPaddingLeft() + key7.x-Constans.dip2px(getApplicationContext(),  0),
                        displayHeight - mInputView.getHeight() + mInputView.getPaddingTop() + key7.y - Constans.dip2px(getApplicationContext(), 4) / 2
                };
            case 110 ://n
                Key key8 =  keys.get(31);
                return new int[] {
                        mInputView.getPaddingLeft() + key8.x,
                        displayHeight + key8.y - mInputView.getHeight() + mInputView.getPaddingTop() - Constans.dip2px(getApplicationContext(), 1+ 4)
                
                };
                case 1609://ى
                if(LatinKeyboard.isArabicIME(this)) {
                Key key9 =  keys.get(35);
                return new int[]{
                        mInputView.getPaddingLeft() + key9.x,
                        displayHeight +key9.y-mInputView.getHeight() + mInputView.getPaddingTop() - Constans.dip2px(getApplicationContext(), 1+4)
                };
                } else{
                    Key key_pe_9 =  keys.get(15);
                    return new int[]{
                            mInputView.getPaddingLeft() + key_pe_9.x,
                            displayHeight +key_pe_9.y-mInputView.getHeight() + mInputView.getPaddingTop() - Constans.dip2px(getApplicationContext(), 1+4)
                };
                }
                case 1608://و
                Key key10 =  keys.get(34);
                return new int[]{
                        mInputView.getPaddingLeft() + key10.x,
                        displayHeight +key10.y-mInputView.getHeight() + mInputView.getPaddingTop() - Constans.dip2px(getApplicationContext(), 1+4)
                };
                case 1586://ز
                  if(LatinKeyboard.isArabicIME(this)){
                Key  key11 =  keys.get(32);
                return new int[]{
                          mInputView.getPaddingLeft() + key11.x,
                          displayHeight + key11.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 1+4)
                };
                  }else{
                      Key  key_pe_11 =  keys.get(29);
                      return new int[]{
                                mInputView.getPaddingLeft() + key_pe_11.x,
                                displayHeight + key_pe_11.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 1+4)
                      };  
                  }
                case 1603://ك
                Key key12 =  keys.get(22);
                return new int[]{
                        mInputView.getPaddingLeft() + key12.x,
                        displayHeight + key12.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 1+4)
              };
                case 1575://ا
                if(LatinKeyboard.isArabicIME(this)){
                Key key13 =  keys.get(18);
                return new int[]{
                        mInputView.getPaddingLeft() + key13.x,
                        displayHeight + key13.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 1)
              };
                }else{
                    Key key_pe_13 =  keys.get(18);
                    return new int[]{
                            mInputView.getPaddingLeft() + key_pe_13.x-Constans.dip2px(getApplicationContext(), 2),
                            displayHeight + key_pe_13.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 1)
                  };
                }
                case 1604://ل
                Key key14 =  keys.get(3);
                return new int[]{
                        mInputView.getPaddingLeft() + key14.x,
                        displayHeight + key14.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 1)
              };
                case 1576://ب
                    Key key15 =  keys.get(17);
                    return new int[]{
                            mInputView.getPaddingLeft() + key15.x,
                            displayHeight + key15.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 1+4)
                  };
                case 1610://ي
                    Key key16 =  keys.get(16);
                    return new int[]{
                            mInputView.getPaddingLeft() + key16.x,
                            displayHeight + key16.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 1+4)
                  };
                case 1607://ه
                    Key key17 =  keys.get(7);
                    return new int[]{
                            mInputView.getPaddingLeft() + key17.x,
                            displayHeight + key17.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 1+4)
                  };
                case 1580://ج
                    Key key18 =  keys.get(9);
                    return new int[]{
                            mInputView.getPaddingLeft() + key18.x,
                            displayHeight + key18.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 1+4)
                  };
                case 1601://ف
                 if(LatinKeyboard.isArabicIME(this)){
                    Key key19 =  keys.get(4);
                    return new int[]{
                            mInputView.getPaddingLeft() + key19.x-Constans.dip2px(getApplicationContext(), 4),
                            displayHeight + key19.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 2)
                  };
                 }else{
                     Key key_pe_19 = keys.get(3);
                     return new int[]{
                             mInputView.getPaddingLeft() + key_pe_19.x,
                             displayHeight + key_pe_19.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 2)
                   };
                 }
                case 1588://ش
                    Key key20 =  keys.get(14);
                    return new int[]{
                            mInputView.getPaddingLeft() + key20.x,
                            displayHeight + key20.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 1+4)
                  };
                case 1611:
                    Key key21 =  keys.get(7);
                    return new int[]{
                            mInputView.getPaddingLeft() + key21.x,
                            displayHeight + key21.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 1+4)
                  };
                case 1578://ت
                 Key key22 =  keys.get(19);
                 return new int[]{
                         mInputView.getPaddingLeft() + key22.x,
                         displayHeight + key22.y -mInputView.getHeight() +mInputView.getPaddingTop() -Constans.dip2px(getApplicationContext(), 1+4)
               };
                case 113: //ㅂ
                    Key key23 = keys.get(0);
                    return new int[]{
                      mInputView.getPaddingLeft() +key23.x,
                      displayHeight + key23.y -mInputView.getHeight()+mInputView.getPaddingTop()
                    };
                case 119://ㅈ
                    Key key24 = keys.get(1);
                    return new int[]{
                            mInputView.getPaddingLeft() + key24.x,
                            displayHeight + key24.y -mInputView.getHeight()+mInputView.getPaddingTop()
                    };
                case 114: //ㄱ
                    Key key26 = keys.get(3);
                    return new int[]{
                            mInputView.getPaddingLeft() + key26.x,
                            displayHeight + key26.y -mInputView.getHeight()+mInputView.getPaddingTop()
                    };
                case 116://ㅅ
                    Key key27 = keys.get(4);
                    return new int[]{
                            mInputView.getPaddingLeft() + key27.x,
                            displayHeight + key27.y -mInputView.getHeight()+mInputView.getPaddingTop()
                    };
                case 112://ㅔ
                    Key key29 = keys.get(8);
                    return new int[]{
                            mInputView.getPaddingLeft() + key29.x,
                            displayHeight + key29.y -mInputView.getHeight()+mInputView.getPaddingTop()
                    };
                default:
                break;
             
        }
          return new int[]{};
       
    }
    

    public int getData(int data) {
        return (int)getResources().getDimension(data);
    }

    public int[] getLayoutSize() {
        Key key = mKeyboardList.getCurrentKeyboard().getKeys().get(0);
        int width = key.width;
        int height = key.height;

        int tWidth = (int) ((int) width * 3.5);// FIXME magic number
        int tHeight = (int) (height * 3);

        return new int[] {
                tWidth, tHeight
        };
    }
    /**
     * 判断是否是本地输入法
     * @param serviceId
     * @return
     */
    public boolean isLocalIME(String serviceId) {
        if (TextUtils.isEmpty(serviceId)) {
            return false;
        }
        if (serviceId.equals(LOCAL_IME_ZHUYIN) || serviceId.equals(LOCAL_IME_CANGJIE)){
            return true;
        }
        return false;

    }

    // private void print(String string) {
    // Constans.print(TAG, string);
    // }

    public int getDest(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void sendKeyDown(int keyCode) {
        long now = SystemClock.uptimeMillis();
        injectKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
                0, InputDevice.SOURCE_UNKNOWN));
    }
    
    private void sendKeyUp(int keyCode) {
        long now = SystemClock.uptimeMillis();
        injectKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
                0, InputDevice.SOURCE_UNKNOWN));
    }
    
    

    private void injectKeyEvent(KeyEvent event) {
        Log.i(TAG, "injectKeyEvent: " + event);
        InputManager.getInstance().injectInputEvent(event, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }
    
    private int right(int index) {
        LatinKeyboard currentKeyboard = mKeyboardList.getCurrentKeyboard();
        int rowNum = currentKeyboard.getRowNum();
        List<KeyRow> list = currentKeyboard.getKeyRows();
        
        Map map = new HashMap();
//        int[] rightIndex = new int[rowNum];
        int sum = 0;
        for (int i = 0; i < rowNum; i++) {
            map.put(sum + list.get(i).mSoftKeys.size() - 1, sum);
            sum += list.get(i).mSoftKeys.size();
        }

        if (map.containsKey(index)) {
            return (Integer)map.get(index);
        }
        return -1;
    }
    
    private int left(int index) {
        LatinKeyboard currentKeyboard = mKeyboardList.getCurrentKeyboard();
        int rowNum = currentKeyboard.getRowNum();
        List<KeyRow> list = currentKeyboard.getKeyRows();
        
        Map map = new HashMap();
      //  int[] leftIndex = new int[rowNum];
        int sum = 0;
        for (int i = 0; i < rowNum; i++) {
            map.put(sum, sum + list.get(i).mSoftKeys.size() - 1);
            sum += list.get(i).mSoftKeys.size();
        }

        if (map.containsKey(index)) {
            return (Integer)map.get(index);
        }
        return -1;
    }
    private void watchTheImeService() {
        
            ISFRENCHSERVICE = true;
            mHomeWatcher = new HomeWatcher(this);
            mHomeWatcher.setOnHomeOrMenuPressedListener(new OnHomePressedListener() {
                
                @Override
                public void onHomePressed() {
                    MoreKeyViewFactory.dismissDialog();
                    dismissImeSelectDialog();
                }
                
                @Override
                public void onHomeLongPressed() {
                    MoreKeyViewFactory.dismissDialog();
                    
                }

                @Override
                public void onMenuPressed() {
                MoreKeyViewFactory.dismissDialog();
                dismissImeSelectDialog();
                mInputView.closing();
                }
            });
            mHomeWatcher.startWatch();
    }
    public ComposingText backComposingText(){
        Log.d("raymond", "backComposingText()"+mComposingText.toString(1));
        return mComposingText;
    }
    private void appendStrSegment(StrSegment str) {

      if (mComposingText.size(ComposingText.LAYER1) >= 30) {
          return; /* do nothing */
      }
      mComposingText.insertStrSegment(ComposingText.LAYER0, ComposingText.LAYER1, str);
      return;
  }
    private void convertKorean(int primaryCode){
        StringBuilder mComposing = mTextEditor.composingText();
        int keyState = InputTables.KEYSTATE_NONE;
        if(MoreKeyViewFactory.isKoreanMorekeys){
            keyState |= InputTables.KEYSTATE_SHIFT;
            MoreKeyViewFactory.isKoreanMorekeys = false;       
        }
            kauto.ToggleMode();
            int ret = kauto.DoAutomata((char )primaryCode, keyState);
            if (ret < 0)
            {
                 // Log.v(TAG,"handleCharacter() - DoAutomata() call failed. primaryCode = " + primaryCode + " keyStete = " + keyState);
                if (kauto.IsKoreanMode())
                    kauto.ToggleMode();
            }
            else 
            {
                // debug block..
//                  Log.v(TAG, "handleCharacter - After calling DoAutomata()");
//                  Log.v(TAG, "   KoreanMode = [" + (kauto.IsKoreanMode()? "true" : "false") + "]");
//                  Log.v(TAG, "   CompleteString = [" + kauto.GetCompleteString() + "]");
//                  Log.v(TAG, "   CompositionString = [" + kauto.GetCompositionString() + "]");
//                  Log.v(TAG, "   State = [" + kauto.GetState() + "]");
//                  Log.v(TAG, "   ret = [" + ret + "]");
//               
                if ((ret & KoreanAutomata.ACTION_UPDATE_COMPLETESTR) != 0)
                {
                    Log.d(TAG, "(ret & KoreanAutomata.ACTION_UPDATE_COMPLETESTR) != 0");
                    if (mComposing.length() > 0){
                        mComposing.replace(mComposing.length()-1, mComposing.length(), kauto.GetCompleteString());
                        Log.d(TAG ,"mComposing.length() > 0"+kauto.GetCompleteString() );
                    }else 
                        mComposing.append(kauto.GetCompleteString());
                    Log.d(TAG,"mComposing.length() > 0else"+kauto.GetCompleteString() );
                    if (mComposing.length() > 0) {
                        getCurrentInputConnection().setComposingText(mComposing, 1);
                        // commitTyped(getCurrentInputConnection());
                    }
                }
                if ((ret & KoreanAutomata.ACTION_UPDATE_COMPOSITIONSTR) != 0)
                {
                    Log.d(TAG, "mComposing.length()"+mComposing.length());
                    if ((mComposing.length() > 0) && ((ret & KoreanAutomata.ACTION_UPDATE_COMPLETESTR) == 0) && ((ret & KoreanAutomata.ACTION_APPEND) == 0)){
                        mComposing.replace(mComposing.length()-1, mComposing.length(), kauto.GetCompositionString());
                          Log.d(TAG, "kauto.GetCompositionString()"+kauto.GetCompositionString()+"mComposing"
                        +mComposing.toString()+"mComposing.length"+mComposing.length());
                    }else 
                        mComposing.append(kauto.GetCompositionString());
                        
                    getCurrentInputConnection().setComposingText(mComposing, 1);
                }
            }
            if ((ret & KoreanAutomata.ACTION_USE_INPUT_AS_RESULT) != 0)
            {
                // Log.v(TAG, "--- USE_INPUT_AS_RESULT");
                mComposing.append((char) primaryCode);
                getCurrentInputConnection().setComposingText(mComposing, 1);
            }
            updateShiftKeyState(getCurrentInputEditorInfo());
    }
    private void deleteKoreanChar(){

        StringBuilder mComposing = mTextEditor.composingText();
        int ret = kauto.DoBackSpace();
        if (ret == KoreanAutomata.ACTION_ERROR)
        {
            // Log.v(TAG, "handleBackspace() - calling DoBackSpace() failed.");
            //// Log.v(TAG, "  mCompositionString = [" + kauto.GetCompositionString()+"]");
            // Log.v(TAG, "  mState = " + kauto.GetState());
            updateShiftKeyState(getCurrentInputEditorInfo());
            return;
        }
        if ((ret & KoreanAutomata.ACTION_UPDATE_COMPOSITIONSTR) != 0)
        {
            if (kauto.GetCompositionString() != "")
            {
                // mComposing.setLength(0);
                if (mComposing.length() > 0)
                {
                    mComposing.replace(mComposing.length() -1, mComposing.length(), kauto.GetCompositionString());
                    getCurrentInputConnection().setComposingText(mComposing, 1);
                }
                // mComposing.append(kauto.GetCompositionString());
                
                updateShiftKeyState(getCurrentInputEditorInfo());
                return;
            }
        }
        // otherwise, just leave it.
        final int length = mComposing.length();
        if (length >= 1) {
            mComposing.delete(length - 1, length);
//            getCurrentInputConnection().setComposingText(mComposing, 1);
            // updateCandidates();
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            // updateCandidates();
        } else {
//            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
    }
}
