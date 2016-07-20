
package com.eostek.scifly.ime.ui;

import java.util.HashMap;
import java.util.Map;

import android.R.integer;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.eostek.scifly.ime.AbstractIME;
import com.eostek.scifly.ime.LatinKeyboard;
import com.eostek.scifly.ime.R;
import com.eostek.scifly.ime.common.KeyboardList;

public class MoreKeyViewFactory {
    
    private static final String TAG = "MorekeyViewFactory";

    private static Map<String, MoreKeysDialog> mMap = new HashMap<String, MoreKeysDialog>();
    
    private static  String[] mStrings = new String[]{ "a","e","i","o","u","y","n","c","A","E","I","O","U","C","N","Y"
        ,"ى","و","ز","ك","ا","ل","ج","ف","ت","ٍ" ,"ㅖ"};
    
//    private  static int[] mInts = new int[]{101,111,113,119,114,116,112};
    
    private  static String[] mKoreanString ={"ㅃ","ㅉ", "ㄸ","ㄲ","ㅆ", "ㅒ","ㅖ"};
    
    public static boolean isKoreanMorekeys = false;

    private static Map<String, MoreKeysDialog> mShiftMap = new HashMap<String, MoreKeysDialog>();
    
    private static MoreKeysDialog mMoreKeysDialog;
    
//    private  static int mKoreanPrimaryCode;
    /**
     * show morekeysboard
     * @param context
     * @param keyCode
     * @param primaryCode
     * @param isShift
     */
    public static void showMoreKeyView(AbstractIME context, int[] keyCode, int primaryCode, 
            boolean isShift) {

        if (!isKeyViewExist(primaryCode)) {
            creatKeyView(context, primaryCode, keyCode, isShift);
        }
        if (!isShift) {
            MoreKeysDialog dialog = mShiftMap.get("" + primaryCode);
            mMoreKeysDialog = dialog;
            dialog.show();
        } else {
            MoreKeysDialog dialog = mMap.get("" + primaryCode);
            mMoreKeysDialog = dialog;
            dialog.show();
        }
    }
    /**
     * 判断相应的键盘是否存在
     * @param primaryCode
     * @return
     */
    private static boolean isKeyViewExist(int primaryCode) {
        if (mMap == null || mMap.size() == 0 || mShiftMap == null || mShiftMap.size() == 0) {
            return false;
        }
        if (mMap.containsKey("" + primaryCode) && mShiftMap.containsKey("" + primaryCode)) {
            return true;
        }
        return false;
    }
    /**
     * create morekeysboard
     * @param context
     * @param primaryCode
     * @param keyCodes
     * @param isShift
     */
    private static void creatKeyView(AbstractIME context, int primaryCode, int[] keyCodes, boolean isShift) {

        MoreKeysDialog dialog = new MoreKeysDialog(context, selectDialog(context, primaryCode, keyCodes, isShift),
                primaryCode);
        if (!isShift) {
            mShiftMap.put("" + primaryCode, dialog);
        } else {
            mMap.put("" + primaryCode, dialog);
        }
    }

    static class MoreKeysDialog extends AlertDialog implements OnClickListener {

        private AbstractIME mContext;

        private int mPrimaryCode;

        private int mResId;

        private int mPrimaryCodes;

        private LinearLayout mRootLayout;

        @Override
        public void onClick(View arg0) {
            Button btn = (Button) arg0;
            CharSequence a = btn.getText();
            mPrimaryCode = a.charAt(0);
          if(LatinKeyboard.isKoreanIME(mContext)){
              if(compareShiftKoreanChar(a.toString())){
                  isKoreanMorekeys = true;
              }
              mContext.handleCharacter(mPrimaryCodes, null);
              
          }else{
              mContext.handleCharacter(mPrimaryCode, null);
          }
            Log.d("raymond", "mPrimaryCode" + mPrimaryCode);
            this.dismiss();

        }

        public MoreKeysDialog(AbstractIME context, int resId, int primaryCode) {
            super(context, android.R.style.Theme_Translucent);
            mContext = context;
            mResId = resId;
            mPrimaryCodes = primaryCode;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            setContentView(mResId);

            mRootLayout = (LinearLayout) findViewById(R.id.more_keys_layout);
            initButton();

            try {
                int[] postion;
                Window window = getWindow();
                LayoutParams params = window.getAttributes();
                params.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
                params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                postion = mContext.getMoreKeysPosition(mPrimaryCodes);
                params.gravity = Gravity.LEFT | Gravity.TOP;
                params.x = postion[0];
                params.y = postion[1];
                window.setAttributes(params);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void initButton() {
            for (int i = 0; i < mRootLayout.getChildCount(); i++) {
                LinearLayout rowParent = (LinearLayout) mRootLayout.getChildAt(i);
                for (int j = 0; j < rowParent.getChildCount(); j++) {
                    Button btn = (Button) rowParent.getChildAt(j);
                    for (int k= 0; k < mStrings.length; k++) {
                        if (mStrings[k].contains(btn.getText().toString())){
                            btn.requestFocus();
                        }
                    }
                    btn.setOnClickListener(this);
                }
            }
        }

    }
    /**
     * 选择每个多字符的布局文件
     * @param context
     * @param primaryCode
     * @param keyCodes
     * @param isShift
     * @return
     */
    public static int selectDialog(AbstractIME context, int primaryCode, int[] keyCodes, boolean isShift) {
        if (!isShift) {
            switch (primaryCode) {
                case 97: // a
                    return R.layout.morekeys_shifted_a;
                case 101: // e
                    return R.layout.morekeys_shifted_e;
                case 105: // i
                    return R.layout.morekeys_shifted_i;
                case 111: // o
                    return R.layout.morekeys_shifted_o;
                case 117: // u
                    return R.layout.morekeys_shifted_u;
                case 121: // y
                    return R.layout.morekeys_shifted_y;
                case 110: // n
                    return R.layout.morekeys_shifted_n;
                case 99: // c
                    return R.layout.morekeys_shifted_c;
                default:
                    break;
            }
        } else {
            switch (primaryCode) {
                case 97: // a
                    return R.layout.morekeys_a;
                case 101: // e
                    if(LatinKeyboard.isKoreanIME(context)){
                        return R.layout.morekeys_ko_3;
                    }else{
                        
                        return R.layout.morekeys_e;
                    }
                case 105: // i
                    return R.layout.morekeys_i;
                case 111: // o
                    if(LatinKeyboard.isKoreanIME(context)){
                        return R.layout.morekeys_ko_6;
                    }else{
                        
                        return R.layout.morekeys_o;
                    }
                case 117: // u
                    return R.layout.morekeys_u;
                case 121: // y
                    return R.layout.morekeys_y;
                case 110: // n
                    return R.layout.morekey_n;
                case 99: // c
                    return R.layout.morekeys_c;
                    //Arabic IME
                 case 1609://ى
                     if (LatinKeyboard.isArabicIME(context)){
                         return R.layout.morekeys_ar_1; 
                     } else {
                     return R.layout.morekeys_pe_1;
                     }
                 case 1608://و
                     return R.layout.morekeys_ar_2;
                 case 1586: //ز
                     return R.layout.morekeys_ar_3;
                 case 1603 : //ك
                     return R.layout.morekeys_ar_4;
                 case 1575://ا
                     if (LatinKeyboard.isArabicIME(context)){
                         return R.layout.morekeys_ar_5;
                     } else {
                     return R.layout.morekeys_pe_2;
                     }
                 case 1604://ل
                     return R.layout.morekeys_ar_6;
                 case 1576://ب
                     return R.layout.morekeys_ar_7;
                 case 1610://ي
                     return R.layout.morekeys_ar_8;
                 case 1607://ه
                     return R.layout.morekeys_ar_9;
                 case 1580://ج
                     return R.layout.morekeys_ar_10;
                 case 1601://ف
                     if (LatinKeyboard.isArabicIME(context)){
                         return R.layout.morekeys_ar_11;
                     } else {
                         return R.layout.morekeys_pe_4;
                    }
                 case 1588://ش
                     return R.layout.morekeys_ar_12;
                 case 1611:
                     return R.layout.morekeys_ar_13;
                 case 1578://ت
                     return R.layout.morekeys_pe_3;
                 case 113://ㅂ
                     return R.layout.morekeys_ko_1;
                 case 119: //ㅈ
                     return R.layout.morekeys_ko_2;
                 case 114: //ㄱ
                     return R.layout.morekeys_ko_4;
                 case 116://ㅅ
                     return R.layout.morekeys_ko_5;
                 case 112://ㅔ
                     return R.layout.morekeys_ko_7;
                 default:
                    break;
            }

        }
        return 0;
    }
    /**
     * clear morekeysboard
     */
    public static void clearMap() {
        mMap.clear();
        mShiftMap.clear();
    }
    /**
     * close dialog
     */
    public static void  dismissDialog() {
        if (mMoreKeysDialog != null){
        mMoreKeysDialog.dismiss();
        }
    }
    private  static boolean compareShiftKoreanChar(String string){
        for(int i=0;i<mKoreanString.length;i++){
            if(  mKoreanString[i].contains(string)){
                return true;
            }
          
        }
        return false;
    }
}
