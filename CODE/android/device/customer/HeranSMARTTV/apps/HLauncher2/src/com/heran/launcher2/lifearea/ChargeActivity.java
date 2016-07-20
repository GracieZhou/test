
package com.heran.launcher2.lifearea;

import com.heran.launcher2.MainViewHolder;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.eosweb.MyWebViewActivity;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.util.Utils;
import com.heran.launcher2.widget.ViewBean;
import com.mstar.android.tv.TvCommonManager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.ImageButton;

public class ChargeActivity extends Activity {

    private final String TAG = "ChargeActivity";

    private ImageButton mLeft;

    private ImageButton mRight01;

    private ImageButton mRight02;

    private ImageButton mRight03;

    private final ChargeActivity mContext;

    private MainViewHolder mHolder;

    private final ViewBean mViewBean;

    private FocusView mFocusView;

    private MyOnClickListener myOnClick;

    private MyOnKeyListener myOnKey;

    private MyOnFocusChangeListener myOnFocusChangeListener;

    private final Object mBSync = new Object();

    /*
     * InputSource to change
     */
    public int mToChangeInputSource = TvCommonManager.INPUT_SOURCE_NONE;

    public ChargeActivity() {
        this.mContext = this;
        mViewBean = new ViewBean(null, null);
        Log.d(TAG, "ChargeFragment ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.charge_main);
        Log.d(TAG, "onCreate");
        try {
            findViews();
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findViews() {
        mLeft = (ImageButton) this.findViewById(R.id.left);
        mRight01 = (ImageButton) this.findViewById(R.id.right01);
        mRight02 = (ImageButton) this.findViewById(R.id.right02);
        mRight03 = (ImageButton) this.findViewById(R.id.right03);
        mFocusView = (FocusView) this.findViewById(R.id.charage_selector);
    }

    private void initView() {
        Log.d(TAG, "ChargeActivity initView");

        mViewBean.setmFocusObject(mFocusView);

        Log.d(TAG, "myOnClick");
        myOnClick = new MyOnClickListener();
        mLeft.setOnClickListener(myOnClick);
        mRight01.setOnClickListener(myOnClick);
        mRight02.setOnClickListener(myOnClick);
        mRight03.setOnClickListener(myOnClick);

        Log.d(TAG, "myOnFocusChangeListener");
        myOnFocusChangeListener = new MyOnFocusChangeListener();
        mLeft.setOnFocusChangeListener(myOnFocusChangeListener);
        mRight01.setOnFocusChangeListener(myOnFocusChangeListener);
        mRight02.setOnFocusChangeListener(myOnFocusChangeListener);
        mRight03.setOnFocusChangeListener(myOnFocusChangeListener);

        Log.d(TAG, "MyOnKey");
        myOnKey = new MyOnKeyListener();
        mLeft.setOnKeyListener(myOnKey);
        mRight01.setOnKeyListener(myOnKey);
        mRight02.setOnKeyListener(myOnKey);
        mRight03.setOnKeyListener(myOnKey);

        Log.d(TAG, "PublicFragment");
        PublicFragment pu = new PublicFragment();
        pu.addViewGlobalLayoutListener(mLeft, mViewBean);
        pu.addViewGlobalLayoutListener(mRight01, mViewBean);
        pu.addViewGlobalLayoutListener(mRight02, mViewBean);
        pu.addViewGlobalLayoutListener(mRight03, mViewBean);

        if (mViewBean.getmCurFocusView() == null) {
            mViewBean.setmCurFocusView(mLeft);
        } else if (mViewBean.getmCurFocusView() == mHolder.home_content) {
            mViewBean.setmCurFocusView(mRight01);
        }
    }

    /*
     * handler click for imagebutton
     */
    private class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            String clickUrl = null;
            switch (v.getId()) {
                case R.id.left:
                    boolean isExist = Utils.isAppInstalled(mContext, "com.digiwin.Mobile.AndroidPad.CloudBusiness");
                    if (isExist) {
                        mContext.startApk("com.digiwin.Mobile.AndroidPad.CloudBusiness",
                                "com.digiwin.Mobile.Pad.MM.Activities.LoadingPageActivity", null);
                    }
                    break;
                case R.id.right01:
                    clickUrl = "https://a1.digiwin.biz/action/20151021/index.php";
                    goToWebUrl(clickUrl);
                    break;
                case R.id.right02:
                    clickUrl = "https://a1.digiwin.biz/forum/forum.php";
                    goToWebUrl(clickUrl);
                    break;
                case R.id.right03:
                    clickUrl = "https://a1.digiwin.biz/tips/index.php";
                    goToWebUrl(clickUrl);
                    break;
                default:
                    break;
            }
        }
    }

    private void drawFocus(View view) {
        Log.d(TAG, "drawFocus : " + view);
        mViewBean.setmCurFocusView(view);
        mViewBean.getmFocusObject().startAnimation(view);
    }

    /*
     * handler focus change
     */
    private class MyOnFocusChangeListener implements OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                drawFocus(view);
            }
        }
    }

    /*
     * handler key event
     */
    private class MyOnKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int arg1, KeyEvent arg2) {
            switch (v.getId()) {
                case R.id.left:
                    if (arg2.getAction() == KeyEvent.ACTION_DOWN && arg1 == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        Log.d(TAG, "left KEYCODE_DPAD_RIGHT");
                        mViewBean.setmCurFocusView(mRight01);
                        mRight01.requestFocus();
                        return true;
                    }
                    break;
                default:
                    break;
            }
            return false;
        }

    }

    /*
     * start an application
     * @param pckName PackageName
     * @param clsName ClassName
     */
    public void startApk(String pckName, String clsName, Bundle bundle) {
        if (!pckName.equals(Constants.TVPLAY_PKG)) {
            setToChangeInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);
        }
        ComponentName componentName = new ComponentName(pckName, clsName);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /*
     * set InputSource to change
     */
    public void setToChangeInputSource(int toChangeInputSource) {
        synchronized (mBSync) {
            mToChangeInputSource = toChangeInputSource;
        }
    }

    /*
     * Jump to a specific web page
     * @param url The web page URL
     */
    private void goToWebUrl(String url) {
        Intent intent = new Intent(mContext, MyWebViewActivity.class);
        Bundle bundle = new Bundle();
        if (url == null || url.equals("")) {
            url = Constants.defaultURL;
        }
        bundle.putString("URL", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
