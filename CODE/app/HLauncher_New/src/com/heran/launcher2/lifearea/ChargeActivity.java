
package com.heran.launcher2.lifearea;

import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.MainViewHolder;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.eosweb.MyWebViewActivity;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.Utils;
import com.heran.launcher2.widget.ViewBean;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

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

    private ImageButton left, right01, right02, right03;

    private ChargeActivity mContext;

    private MainViewHolder mHolder;

    private ViewBean mViewBean;

    private FocusView mFocusView;

    private MyOnClickListener myOnClick;

    private MyOnKeyListener myOnKey;

    private MyOnFocusChangeListener myOnFocusChangeListener;

    private final Object bSync = new Object();

    public EnumInputSource toChangeInputSource = EnumInputSource.E_INPUT_SOURCE_NONE;

    private final String TAG = "ChargeActivity";

    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public void setmViewBean(ViewBean mViewBean) {
        this.mViewBean = mViewBean;
    }

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
            Log.d(TAG, "error : " + e.toString());
        }
    }

    private void findViews() {
        left = (ImageButton) this.findViewById(R.id.left);
        right01 = (ImageButton) this.findViewById(R.id.right01);
        right02 = (ImageButton) this.findViewById(R.id.right02);
        right03 = (ImageButton) this.findViewById(R.id.right03);
        mFocusView = (FocusView) this.findViewById(R.id.charage_selector);
    }

    private void initView() {
        Log.d(TAG, "ChargeActivity initView");

        mViewBean.setmFocusObject(mFocusView);

        Log.d(TAG, "myOnClick");
        myOnClick = new MyOnClickListener();
        left.setOnClickListener(myOnClick);
        right01.setOnClickListener(myOnClick);
        right02.setOnClickListener(myOnClick);
        right03.setOnClickListener(myOnClick);

        Log.d(TAG, "myOnFocusChangeListener");
        myOnFocusChangeListener = new MyOnFocusChangeListener();
        left.setOnFocusChangeListener(myOnFocusChangeListener);
        right01.setOnFocusChangeListener(myOnFocusChangeListener);
        right02.setOnFocusChangeListener(myOnFocusChangeListener);
        right03.setOnFocusChangeListener(myOnFocusChangeListener);

        Log.d(TAG, "MyOnKey");
        myOnKey = new MyOnKeyListener();
        left.setOnKeyListener(myOnKey);
        right01.setOnKeyListener(myOnKey);
        right02.setOnKeyListener(myOnKey);
        right03.setOnKeyListener(myOnKey);

        try {
            Log.d(TAG, "PublicFragment");
            PublicFragment pu = new PublicFragment();
            pu.addViewGlobalLayoutListener(left, mViewBean);
            pu.addViewGlobalLayoutListener(right01, mViewBean);
            pu.addViewGlobalLayoutListener(right02, mViewBean);
            pu.addViewGlobalLayoutListener(right03, mViewBean);
        } catch (Exception e) {
            Log.d(TAG, "addViewGlobalLayoutListener error : " + e.toString());
        }

        left.setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.main));
        right01.setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.ad_1));
        right02.setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.btn_2));
        right03.setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.btn_3));

        if (mViewBean.getmCurFocusView() == null) {
            mViewBean.setmCurFocusView(left);
        } else if (mViewBean.getmCurFocusView() == mHolder.home_content) {
            mViewBean.setmCurFocusView(right01);
        }
    }

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
                    } else {

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

    private class MyOnFocusChangeListener implements OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                drawFocus(view);
            }
        }
    }

    private class MyOnKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int arg1, KeyEvent arg2) {
            switch (v.getId()) {
                case R.id.left:
                    if (arg2.getAction() == KeyEvent.ACTION_DOWN && arg1 == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        Log.d(TAG, "left KEYCODE_DPAD_RIGHT");
                        mViewBean.setmCurFocusView(right01);
                        right01.requestFocus();
                        return true;
                    }
                    break;
                default:
                    break;
            }
            return false;
        }

    }

    public void startApk(String pckName, String clsName, Bundle bundle) {
        if (!pckName.equals("com.eostek.tv.player")) {
            setToChangeInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
        }
        ComponentName componentName = new ComponentName(pckName, clsName);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        UIUtil.updateHistory(this, pckName, clsName);
        startActivity(intent);
    }

    public void setToChangeInputSource(EnumInputSource toChangeInputSource) {
        synchronized (bSync) {
            this.toChangeInputSource = toChangeInputSource;
        }
    }

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

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

}
