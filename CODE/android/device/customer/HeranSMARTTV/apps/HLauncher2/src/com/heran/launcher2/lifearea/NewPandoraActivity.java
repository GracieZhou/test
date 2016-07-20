/**
 * 新UI的Pandora，全螢幕
 */

package com.heran.launcher2.lifearea;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.advert.MyAD;
import com.heran.launcher2.eosweb.MyWebViewActivity;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.util.GoogleAnalyticsUtil;
import com.heran.launcher2.util.HistoryRec;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.Utils;
import com.heran.launcher2.widget.ViewBean;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.webkit.EOSWebView;
import android.widget.EditText;
import android.widget.ImageButton;

public class NewPandoraActivity extends Activity {

    private static final String TAG = "NewPandoraActivity";

    private EOSWebView mWebPandoraLeft;

    private EOSWebView mWebPandoraRight;

    private EOSWebView mWebPandoraBottom;

    private ImageButton mBtnPandoraLeft;

    private ImageButton mBtnPandoraRight;

    private ImageButton mBtnPandoraBottom;

    private MyOnClickListener myOnClickListener;

    private MyOnKeyListener myOnKeyListener;

    private MyOnFocusChangeListener myOnFocusChangeListener;

    private final ViewBean mViewBean;

    private FocusView myFocusView;

    private EditText mUserInput;

    private EditText mUserInputNew;

    private EditText mUserInputOld;

    private String mTempData = "";

    private String mChange = "";

    private String Mmsg = "";

    private String mRedirect = "";

    private String mDay = "";

    private int mState;

    private boolean mIsStateOk = false;

    private final Object bSync = new Object();

    private final NewPandoraActivity mContext;

    /*
     * InputSource to change
     */
    public int toChangeInputSource = TvCommonManager.INPUT_SOURCE_NONE;

    public NewPandoraActivity() {
        mContext = this;
        mViewBean = new ViewBean(null, null);
        Log.d(TAG, "NewShopFragment ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "NewPandoraFragment---oncreat");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pandora_main);
        Log.d(TAG, "NewPandoraFragment---go to init");
        findViews();
        initView();
    }

    private void findViews() {
        mUserInput = (EditText) findViewById(R.id.editTextDialogUserInput);
        mUserInputNew = (EditText) findViewById(R.id.editTextDialogUserInputNew);
        mUserInputOld = (EditText) findViewById(R.id.editTextDialogUserInputOld);

        mWebPandoraLeft = (EOSWebView) findViewById(R.id.web_pandora_left);
        mWebPandoraRight = (EOSWebView) findViewById(R.id.web_pandora_right);
        mWebPandoraBottom = (EOSWebView) findViewById(R.id.web_pandora_bottom);

        mBtnPandoraLeft = (ImageButton) findViewById(R.id.btn_pandora_left);
        mBtnPandoraRight = (ImageButton) findViewById(R.id.btn_pandora_right);
        mBtnPandoraBottom = (ImageButton) findViewById(R.id.btn_pandora_bottom);

        myFocusView = (FocusView) findViewById(R.id.newpandora_selector);
    }

    private void initView() {
        // 用來記錄Pandora左右切換的頁面，新UI沒有左右切換功能

        mWebPandoraLeft.setHorizontalScrollBarEnabled(false);
        mWebPandoraRight.setHorizontalScrollBarEnabled(false);
        mWebPandoraBottom.setHorizontalScrollBarEnabled(false);

        mWebPandoraLeft.setVerticalScrollBarEnabled(false);
        mWebPandoraRight.setVerticalScrollBarEnabled(false);
        mWebPandoraBottom.setVerticalScrollBarEnabled(false);

        myOnClickListener = new MyOnClickListener();
        mBtnPandoraLeft.setOnClickListener(myOnClickListener);
        mBtnPandoraRight.setOnClickListener(myOnClickListener);
        mBtnPandoraBottom.setOnClickListener(myOnClickListener);

        myOnKeyListener = new MyOnKeyListener();
        mBtnPandoraLeft.setOnKeyListener(myOnKeyListener);
        mBtnPandoraRight.setOnKeyListener(myOnKeyListener);
        mBtnPandoraBottom.setOnKeyListener(myOnKeyListener);

        myOnFocusChangeListener = new MyOnFocusChangeListener();
        mBtnPandoraLeft.setOnFocusChangeListener(myOnFocusChangeListener);
        mBtnPandoraRight.setOnFocusChangeListener(myOnFocusChangeListener);
        mBtnPandoraBottom.setOnFocusChangeListener(myOnFocusChangeListener);

        mViewBean.setmFocusObject(myFocusView);
        myFocusView.setVisibility(View.INVISIBLE);

        if (mViewBean.getmCurFocusView() == null) {
            mViewBean.setmCurFocusView(mBtnPandoraLeft);
        }

        PublicFragment pu = new PublicFragment();
        pu.addViewGlobalLayoutListener(mBtnPandoraLeft, mViewBean);
        pu.addViewGlobalLayoutListener(mBtnPandoraRight, mViewBean);
        pu.addViewGlobalLayoutListener(mBtnPandoraBottom, mViewBean);

        if (android.provider.Settings.System.getString(getContentResolver(), "PPasswd") == null) {
            android.provider.Settings.System.putString(getContentResolver(), "PPasswd", Constants.PPasswd);
            Constants.Preset = true;
        }

        if (android.provider.Settings.System.getString(getContentResolver(), "POldPasswd") == null) {
            android.provider.Settings.System.putString(getContentResolver(), "POldPasswd", Constants.POldPasswd);
            Constants.Preset = true;
        }

        msbox(R.string.enter_pandora_dialog_title, R.string.enter_pandora_dialog_content);
    }

    /*
     * get Json
     */
    public String getJson(String url) {
        Log.d(TAG, "getJson ");
        String result = "";
        InputStream is = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            mState = response.getStatusLine().getStatusCode();
            mIsStateOk = (mState == 200);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"), 9999999);
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            if (is != null) {
                is.close();
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * get eth address
     */
    public String getEthAddr() {
        String mac = "00:88:88:00:00:01";
        try {
            mac = TvManager.getInstance().getEnvironment("ethaddr");
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return mac;
    }

    private final Runnable chk_date_run = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "check_date ");
            String json = "";
            try {
                json = getJson("http://www.jowinwin.com/hertv2msd/index.php?r=pandora/change&mac=" + getEthAddr());
                Log.d(TAG, "http://www.jowinwin.com/hertv2msd/index.php?r=pandora/change&mac=" + getEthAddr());
                Log.d(TAG, "getJson end ");

                JSONObject obj = new JSONObject(json);
                mChange = obj.getString("change");
                Mmsg = obj.getString("msg");
                mRedirect = obj.getString("redirect");
                mDay = obj.getString("day");

                Log.d(TAG, "json :" + json);
                Log.d(TAG, "change :" + mChange);
                Log.d(TAG, "msg :" + Mmsg);
                Log.d(TAG, "redirect :" + mRedirect);
                Log.d(TAG, "day :" + mDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /*
     * handler click for imageButton
     */
    private class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            String clickUrl = null;
            if (!Utils.isNetworkState) {
                UIUtil.toastShow(R.string.shop_no_network, getApplicationContext());
                return;
            }
            MyAD adinfo = new MyAD();
            switch (view.getId()) {
                case R.id.btn_pandora_left:
                    adinfo.setGln(
                            "com.mtech.cube.pandora_Heran,com.mtech.cube.pandora_Heran.activity.MainLayoutActivity"
                                    + 0);
                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_PANDORA, adinfo, 1);
                    mTempData = HistoryRec.block[4] + ',' + HistoryRec.block5Action[2] + ',' + "" + ','
                            + HistoryRec.getCurrentDateTime();
                    Log.d(TAG, "tempData:" + mTempData);
                    HistoryRec.writeToFile(mTempData);
                    startApk("com.mtech.cube.pandora_Heran", "com.mtech.cube.pandora_Heran.activity.MainLayoutActivity",
                            null);
                    break;
                case R.id.btn_pandora_right:
                    clickUrl = "http://www.jowinwin.com/hertv2msd/index.php?r=pandora/pandora";
                    adinfo.setGln(clickUrl);
                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_PANDORA, adinfo, 1);
                    goToWebUrl(clickUrl);
                    break;
                case R.id.btn_pandora_bottom:
                    break;
                default:
                    break;
            }
        }
    }

    /*
     * handler key event
     */
    private class MyOnKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (v.getId()) {
                    case R.id.btn_pandora_left:
                    case R.id.btn_pandora_right:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            return true;
                        }
                        break;
                    case R.id.btn_pandora_bottom:
                        break;
                    default:
                        break;
                }
            }
            return false;
        }
    }

    /*
     * handler focus change
     */
    private class MyOnFocusChangeListener implements OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                myFocusView.startAnimation(view);
                mViewBean.setmCurFocusView(view);
            }
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
    public void setToChangeInputSource(int changeInputSource) {
        synchronized (bSync) {
            toChangeInputSource = changeInputSource;
        }
    }

    private void goToWebUrl(String url) {
        Intent intent = new Intent(this, MyWebViewActivity.class);
        Bundle bundle = new Bundle();
        if (url == null || url.equals("")) {
            url = Constants.defaultURL;
        }
        bundle.putString("URL", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 以下為一開始進入Pandora之對話框動作
     */
    public void msbox(int enterPandoraDialogTitle, int enterPandoraDialogContent) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setTitle(enterPandoraDialogTitle);
        dlgAlert.setMessage(enterPandoraDialogContent);

        dlgAlert.setPositiveButton(R.string.enter_pandora_dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                msboxpw(R.string.check_password_dialog_title, R.string.check_password_dialog_content);
            }
        }).setNeutralButton(R.string.enter_pandora_dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                mTempData = HistoryRec.block[4] + ',' + HistoryRec.block5Action[1] + ',' + "" + ','
                        + HistoryRec.getCurrentDateTime();
                HistoryRec.writeToFile(mTempData);
                Log.d(TAG, "close :" + mTempData);
                finish();
                dialog.cancel();
            }
        }).setNegativeButton(R.string.skip_pandora_dialog_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // 永久關閉
                // if this button is clicked, just close
                // the dialog box and do nothing
                msboxSkipPandora(R.string.skip_pandora_dialog_defaultedit_title,
                        R.string.skip_pandora_dialog_defaultedit_text);
            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    /*
     * the msboxpw dialog
     */
    public void msboxpw(int checkPasswordDialogTitle, int checkPasswordDialogContent) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setTitle(checkPasswordDialogTitle);
        dlgAlert.setMessage(checkPasswordDialogContent);
        mUserInput = new EditText(this);
        mUserInput.setHint(R.string.enter_pandora_dialog_default_text);
        mUserInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        dlgAlert.setView(mUserInput);

        dlgAlert.setPositiveButton(R.string.check_password_dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close current activity
                final String srtcheck = "8888";
                String srt = mUserInput.getEditableText().toString();

                Constants.PPasswd = android.provider.Settings.System.getString(getContentResolver(), "PPasswd");
                Log.d(TAG, "Constants.POldPasswd:" + Constants.POldPasswd);
                Log.d(TAG, "Constants.PPasswd:" + Constants.PPasswd);

                if (srt.toString().isEmpty()) {
                    if (Constants.Preset) {
                        msboxeditNew(R.string.enter_pandora_dialog_reset_title,
                                R.string.enter_pandora_dialog_reset_text);
                    } else {
                        msboxrepeat(R.string.wrong_password_dialog_title, R.string.wrong_password_dialog_content);
                    }
                } else {
                    if (srt.toString().equalsIgnoreCase(srtcheck.toString())) {
                        msboxeditNew(R.string.enter_pandora_dialog_superpwd_title,
                                R.string.enter_pandora_dialog_editpwd_editnewtext);
                    } else if (srt.toString().equalsIgnoreCase(Constants.PPasswd.toString())) {
                        Constants.CheckPW18 = true;
                        mBtnPandoraLeft.setImageResource(R.drawable.pandora_left);
                        mBtnPandoraRight.setImageResource(R.drawable.pandora_right);
                        mBtnPandoraBottom.setImageResource(R.drawable.pandora_bottom);
                        if (mIsStateOk) {
                            if (mChange.equals("0")) {
                                Log.d(TAG, "not over!!");
                                return;
                            } else {
                                Log.d(TAG, "is over!!");
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle(R.string.prompt);
                                builder.setMessage(Mmsg);
                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        goToWebUrl(mRedirect);
                                    }
                                });
                                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        return;
                                    }
                                });
                                builder.setCancelable(false);
                                builder.create().show();
                            }
                        }
                    } else if (Constants.Preset) {
                        msboxeditNew(R.string.enter_pandora_dialog_reset_title,
                                R.string.enter_pandora_dialog_reset_text);
                    } else {
                        msboxrepeat(R.string.wrong_password_dialog_title, R.string.wrong_password_dialog_content);
                    }
                }
            }
        }).setNeutralButton(R.string.check_password_dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                finish();
                dialog.cancel();
            }
        }).setNegativeButton(R.string.enter_pandora_dialog_defaultedit_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                msboxedit(R.string.enter_pandora_dialog_editpwd_editnewtitle,
                        R.string.enter_pandora_dialog_current_text);
            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    /*
     * the msboxrepeat dialog
     */
    public void msboxrepeat(int wrongPasswordDialogTitle, int wrongPasswordDialogContent) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setTitle(wrongPasswordDialogTitle);
        dlgAlert.setMessage(wrongPasswordDialogContent);

        dlgAlert.setPositiveButton(R.string.wrong_password_dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                msboxpw(R.string.check_password_dialog_title, R.string.check_password_dialog_content);
            }
        }).setNeutralButton(R.string.check_password_dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                finish();
                dialog.cancel();

            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    // public void msboxedit(String str,String str2)
    public void msboxedit(int editPWDTitle, int editPWDText) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setTitle(editPWDTitle);
        dlgAlert.setMessage(editPWDText);
        mUserInputOld = new EditText(this);
        mUserInputOld.setHint(R.string.enter_pandora_dialog_current_text);
        mUserInputOld.setTransformationMethod(PasswordTransformationMethod.getInstance());
        dlgAlert.setView(mUserInputOld);

        dlgAlert.setPositiveButton(R.string.check_password_dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close current activity
                final String srtcheck = "8888";
                String srt = mUserInputOld.getEditableText().toString();

                if (srt.toString().isEmpty()) {
                    if (Constants.Preset) {
                        msboxeditNew(R.string.enter_pandora_dialog_reset_title,
                                R.string.enter_pandora_dialog_reset_text);
                    } else {
                        msboxedit(R.string.enter_pandora_dialog_editpwd_emptytitle,
                                R.string.enter_pandora_dialog_editpwd_emptytext);
                    }
                } else {
                    if (srt.toString().equalsIgnoreCase(srtcheck.toString())) {
                        msboxeditNew(R.string.enter_pandora_dialog_superpwd_title,
                                R.string.enter_pandora_dialog_editpwd_editnewtext);
                    } else if (srt.toString().equalsIgnoreCase(Constants.PPasswd.toString())) {
                        msboxeditNew(R.string.enter_pandora_dialog_editpwd_editnewtitle,
                                R.string.enter_pandora_dialog_editpwd_editnewtext);
                    } else if (Constants.Preset) {
                        msboxeditNew(R.string.enter_pandora_dialog_reset_title,
                                R.string.enter_pandora_dialog_reset_text);
                    } else {
                        String srtold = android.provider.Settings.System.getString(getContentResolver(), "POldPasswd");

                        if (srt.toString().equalsIgnoreCase(srtold.toString())) {
                            msboxeditNew(R.string.enter_pandora_dialog_editpwd_editnewtitle,
                                    R.string.enter_pandora_dialog_editpwd_editnewtext);
                        } else {
                            msboxedit(R.string.enter_pandora_dialog_editpwd_title,
                                    R.string.wrong_password_dialog_content);
                        }
                    }
                }

            }
        }).setNeutralButton(R.string.check_password_dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                finish();
                dialog.cancel();
            }
        }).setNegativeButton(R.string.enter_pandora_dialog_passwd_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                msboxpw(R.string.check_password_dialog_title, R.string.check_password_dialog_content);
            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    /*
     * the msboxeditNew dialog
     */
    public void msboxeditNew(int editNewPWDTitle, int editNewPWDText) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setTitle(editNewPWDTitle);
        dlgAlert.setMessage(editNewPWDText);

        mUserInputNew = new EditText(this);
        mUserInputNew.setHint(R.string.enter_pandora_dialog_new_text);
        mUserInputNew.setTransformationMethod(PasswordTransformationMethod.getInstance());
        dlgAlert.setView(mUserInputNew);

        dlgAlert.setPositiveButton(R.string.check_password_dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close current activity
                String srtNew = mUserInputNew.getEditableText().toString();

                if (srtNew.toString().isEmpty()) {
                    msboxeditNew(R.string.enter_pandora_dialog_editpwd_emptytitle,
                            R.string.enter_pandora_dialog_editpwd_emptytext);
                } else {
                    Log.d(TAG, "srtcheck.toString()" + srtNew.toString());

                    Constants.PPasswd = srtNew;
                    Constants.POldPasswd = srtNew;
                    Constants.Preset = false;

                    android.provider.Settings.System.putString(getContentResolver(), "PPasswd", Constants.PPasswd);
                    android.provider.Settings.System.putString(getContentResolver(), "POldPasswd",
                            Constants.POldPasswd);

                    Constants.CheckPW18 = true;
                    mBtnPandoraLeft.setImageResource(R.drawable.pandora_left);
                    mBtnPandoraRight.setImageResource(R.drawable.pandora_right);
                    mBtnPandoraBottom.setImageResource(R.drawable.pandora_bottom);
                    if (mIsStateOk) {
                        if (mChange.equals("0")) {
                            return;
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.prompt);
                            builder.setMessage(Mmsg);
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    goToWebUrl(mRedirect);
                                }
                            });
                            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    return;
                                }
                            });
                            builder.setCancelable(false);
                            builder.create().show();

                        }
                    }
                }
            }
        }).setNeutralButton(R.string.check_password_dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                finish();
                dialog.cancel();
            }
        }).setNegativeButton(R.string.enter_pandora_dialog_passwd_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                msboxpw(R.string.check_password_dialog_title, R.string.check_password_dialog_content);
            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    /*
     * msboxSkip dialog for Pandora
     */
    public void msboxSkipPandora(int wrongPasswordDialogTitle, int wrongPasswordDialogContent) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setTitle(wrongPasswordDialogTitle);
        dlgAlert.setMessage(wrongPasswordDialogContent);

        dlgAlert.setPositiveButton(R.string.check_password_dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                mTempData = HistoryRec.block[4] + ',' + HistoryRec.block5Action[0] + ',' + "" + ','
                        + HistoryRec.getCurrentDateTime();
                Log.d(TAG, "tempData:" + mTempData);
                HistoryRec.writeToFile(mTempData);

                Constants.SkipPandora = "1";
                android.provider.Settings.System.putString(getContentResolver(), "SkipPandora", Constants.SkipPandora);
                finish();
            }
        }).setNeutralButton(R.string.check_password_dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                dialog.cancel();
                msbox(R.string.enter_pandora_dialog_title, R.string.enter_pandora_dialog_content);
            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mChange = "";
        Mmsg = "";
        mRedirect = "";
        mDay = "";
        mState = 0;
        mIsStateOk = false;
        Thread thread = new Thread(chk_date_run);
        thread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebPandoraLeft.stopLoading();
        mWebPandoraRight.stopLoading();
        mWebPandoraBottom.stopLoading();
        mWebPandoraLeft.clearFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebPandoraLeft.stopLoading();
        mWebPandoraRight.stopLoading();
        mWebPandoraBottom.stopLoading();
    }

}
