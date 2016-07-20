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
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

public class NewPandoraActivity extends Activity {

    private EOSWebView web_pandoraLeft, web_pandoraRight, web_pandoraBottom;

    private ImageButton btn_pandoraLeft, btn_pandoraRight, btn_pandoraBottom;

    private MyOnClickListener myOnClickListener;

    private MyOnKeyListener myOnKeyListener;

    private MyOnFocusChangeListener myOnFocusChangeListener;

    private ViewBean mViewBean;

    private FocusView myFocusView;

    private EditText userInput, userInputNew, userInputOld;

    private String tempData = "";

    private String change = "";

    private String msg = "";

    private String redirect = "";

    private String day = "";

    private int state;

    boolean state_ok = false;

    private final Object bSync = new Object();

    private final NewPandoraActivity mContext;

    public EnumInputSource toChangeInputSource = EnumInputSource.E_INPUT_SOURCE_NONE;

    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public void setmViewBean(ViewBean mViewBean) {
        this.mViewBean = mViewBean;
    }

    public NewPandoraActivity() {
        mContext = this;
        mViewBean = new ViewBean(null, null);
        Log.d("newaa", "NewShopFragment ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("newaa", "NewPandoraFragment---oncreat");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pandora_main);
        Log.d("newaa", "NewPandoraFragment---go to init");
        findViews();
        initView();
    }

    private void findViews() {
        userInput = (EditText) this.findViewById(R.id.editTextDialogUserInput);
        userInputNew = (EditText) this.findViewById(R.id.editTextDialogUserInputNew);
        userInputOld = (EditText) this.findViewById(R.id.editTextDialogUserInputOld);

        web_pandoraLeft = (EOSWebView) this.findViewById(R.id.web_pandora_left);
        web_pandoraRight = (EOSWebView) this.findViewById(R.id.web_pandora_right);
        web_pandoraBottom = (EOSWebView) this.findViewById(R.id.web_pandora_bottom);

        btn_pandoraLeft = (ImageButton) this.findViewById(R.id.btn_pandora_left);
        btn_pandoraRight = (ImageButton) this.findViewById(R.id.btn_pandora_right);
        btn_pandoraBottom = (ImageButton) this.findViewById(R.id.btn_pandora_bottom);

        myFocusView = (FocusView) this.findViewById(R.id.newpandora_selector);
    }

    private void initView() {
        // 用來記錄Pandora左右切換的頁面，新UI沒有左右切換功能

        web_pandoraLeft.setHorizontalScrollBarEnabled(false);
        web_pandoraRight.setHorizontalScrollBarEnabled(false);
        web_pandoraBottom.setHorizontalScrollBarEnabled(false);

        web_pandoraLeft.setVerticalScrollBarEnabled(false);
        web_pandoraRight.setVerticalScrollBarEnabled(false);
        web_pandoraBottom.setVerticalScrollBarEnabled(false);

        myOnClickListener = new MyOnClickListener();
        btn_pandoraLeft.setOnClickListener(myOnClickListener);
        btn_pandoraRight.setOnClickListener(myOnClickListener);
        btn_pandoraBottom.setOnClickListener(myOnClickListener);

        myOnKeyListener = new MyOnKeyListener();
        btn_pandoraLeft.setOnKeyListener(myOnKeyListener);
        btn_pandoraRight.setOnKeyListener(myOnKeyListener);
        btn_pandoraBottom.setOnKeyListener(myOnKeyListener);

        myOnFocusChangeListener = new MyOnFocusChangeListener();
        btn_pandoraLeft.setOnFocusChangeListener(myOnFocusChangeListener);
        btn_pandoraRight.setOnFocusChangeListener(myOnFocusChangeListener);
        btn_pandoraBottom.setOnFocusChangeListener(myOnFocusChangeListener);

        mViewBean.setmFocusObject(myFocusView);
        myFocusView.setVisibility(View.INVISIBLE);

        if (mViewBean.getmCurFocusView() == null) {
            mViewBean.setmCurFocusView(btn_pandoraLeft);
        }

        PublicFragment pu = new PublicFragment();
        pu.addViewGlobalLayoutListener(btn_pandoraLeft, mViewBean);
        pu.addViewGlobalLayoutListener(btn_pandoraRight, mViewBean);
        pu.addViewGlobalLayoutListener(btn_pandoraBottom, mViewBean);

        // setRetainInstance(true);

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

    public String getJson(String url) {
        Log.d("jack", "getJson ");
        String result = "";
        InputStream is = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            state = response.getStatusLine().getStatusCode();
            if (state != 200) {
                state_ok = false;
            } else {
                state_ok = true;
            }
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            Log.d("jack", "error 1!!" + e.toString());
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"), 9999999);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.d("jack", "error 2!!" + e.toString());
        }
        return result;
    }

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
            Log.d("jack", "check_date ");
            String json = "";
            try {
                json = getJson("http://www.jowinwin.com/hertv2msd/index.php?r=pandora/change&mac=" + getEthAddr());
                Log.d("jack", "http://www.jowinwin.com/hertv2msd/index.php?r=pandora/change&mac=" + getEthAddr());
                Log.d("jack", "getJson end ");
            } catch (Exception e) {
                Log.d("jack", "debug 1: " + e.toString());
            }
            try {
                JSONObject obj = new JSONObject(json);
                change = obj.getString("change");
                msg = obj.getString("msg");
                redirect = obj.getString("redirect");
                day = obj.getString("day");

                Log.d("jack", "json :" + json);
                Log.d("jack", "change :" + change);
                Log.d("jack", "msg :" + msg);
                Log.d("jack", "redirect :" + redirect);
                Log.d("jack", "day :" + day);
            } catch (Exception e) {
                Log.d("jack", "debug 2: " + e.toString());
            }
        }
    };

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
                    adinfo.setGln("com.mtech.cube.pandora_Heran,com.mtech.cube.pandora_Heran.activity.MainLayoutActivity" + 0);
                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_PANDORA, adinfo, 1);
                    tempData = HistoryRec.block[4] + ',' + HistoryRec.block5Action[2] + ',' + "" + ','
                            + HistoryRec.getCurrentDateTime();
                    Log.d("rec", "tempData:" + tempData);
                    HistoryRec.writeToFile(tempData);
                    startApk("com.mtech.cube.pandora_Heran",
                            "com.mtech.cube.pandora_Heran.activity.MainLayoutActivity", null);
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

    private class MyOnKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (v.getId()) {
                    case R.id.btn_pandora_left:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            return true;
                        }
                        break;
                    case R.id.btn_pandora_right:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            return true;
                        }
                        break;
                    case R.id.btn_pandora_bottom:
                        break;
                }
            }
            return false;
        }
    }

    private class MyOnFocusChangeListener implements OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                myFocusView.startAnimation(view);
                mViewBean.setmCurFocusView(view);
            }
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
                tempData = HistoryRec.block[4] + ',' + HistoryRec.block5Action[1] + ',' + "" + ','
                        + HistoryRec.getCurrentDateTime();
                HistoryRec.writeToFile(tempData);
                Log.d("rec", "close :" + tempData);
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

    public void msboxpw(int checkPasswordDialogTitle, int checkPasswordDialogContent) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setTitle(checkPasswordDialogTitle);
        dlgAlert.setMessage(checkPasswordDialogContent);
        userInput = new EditText(this);
        userInput.setHint(R.string.enter_pandora_dialog_default_text);
        userInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        dlgAlert.setView(userInput);

        dlgAlert.setPositiveButton(R.string.check_password_dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close current activity
                final String srtcheck = "8888";
                String srt = userInput.getEditableText().toString();

                Log.d("PPasswd",
                        "PPasswd:" + android.provider.Settings.System.getString(getContentResolver(), "PPasswd"));
                Log.d("POldPasswd",
                        "POldPasswd:" + android.provider.Settings.System.getString(getContentResolver(), "POldPasswd"));

                Constants.PPasswd = android.provider.Settings.System.getString(getContentResolver(), "PPasswd");
                Log.d("Constants.POldPasswd", "Constants.POldPasswd:" + Constants.POldPasswd);
                Log.d("Constants.POldPPPasswdasswd", "Constants.PPasswd:" + Constants.PPasswd);

                if (!srt.toString().isEmpty()) {
                    if (srt.toString().equalsIgnoreCase(srtcheck.toString())) {
                        msboxeditNew(R.string.enter_pandora_dialog_superpwd_title,
                                R.string.enter_pandora_dialog_editpwd_editnewtext);
                    } else if (srt.toString().equalsIgnoreCase(Constants.PPasswd.toString())) {
                        Constants.CheckPW18 = true;
                        btn_pandoraLeft.setImageResource(R.drawable.pandora_left);
                        btn_pandoraRight.setImageResource(R.drawable.pandora_right);
                        btn_pandoraBottom.setImageResource(R.drawable.pandora_bottom);
                        if (state_ok) {
                            if (change.equals("0")) {
                                Log.d("jack", "not over!!");
                                return;
                            } else {
                                Log.d("jack", "is over!!");
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle(R.string.prompt);
                                builder.setMessage(msg);
                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        goToWebUrl(redirect);
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
                } else {
                    if (Constants.Preset) {
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
        userInputOld = new EditText(this);
        userInputOld.setHint(R.string.enter_pandora_dialog_current_text);
        userInputOld.setTransformationMethod(PasswordTransformationMethod.getInstance());
        dlgAlert.setView(userInputOld);

        dlgAlert.setPositiveButton(R.string.check_password_dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close current activity
                final String srtcheck = "8888";
                String srt = userInputOld.getEditableText().toString();

                if (!srt.toString().isEmpty()) {
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
                } else {
                    if (Constants.Preset) {
                        msboxeditNew(R.string.enter_pandora_dialog_reset_title,
                                R.string.enter_pandora_dialog_reset_text);
                    } else {
                        msboxedit(R.string.enter_pandora_dialog_editpwd_emptytitle,
                                R.string.enter_pandora_dialog_editpwd_emptytext);
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

    public void msboxeditNew(int editNewPWDTitle, int editNewPWDText) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setTitle(editNewPWDTitle);
        dlgAlert.setMessage(editNewPWDText);

        userInputNew = new EditText(this);
        userInputNew.setHint(R.string.enter_pandora_dialog_new_text);
        userInputNew.setTransformationMethod(PasswordTransformationMethod.getInstance());
        dlgAlert.setView(userInputNew);

        dlgAlert.setPositiveButton(R.string.check_password_dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close current activity
                String srtNew = userInputNew.getEditableText().toString();

                if (!srtNew.toString().isEmpty()) {
                    Log.d("srtNew.toString()", "srtcheck.toString()" + srtNew.toString());

                    Constants.PPasswd = srtNew;
                    Constants.POldPasswd = srtNew;
                    Constants.Preset = false;

                    android.provider.Settings.System.putString(getContentResolver(), "PPasswd", Constants.PPasswd);
                    android.provider.Settings.System
                            .putString(getContentResolver(), "POldPasswd", Constants.POldPasswd);

                    Constants.CheckPW18 = true;
                    btn_pandoraLeft.setImageResource(R.drawable.pandora_left);
                    btn_pandoraRight.setImageResource(R.drawable.pandora_right);
                    btn_pandoraBottom.setImageResource(R.drawable.pandora_bottom);
                    if (state_ok) {
                        if (change.equals("0")) {
                            Log.d("jack", "not over!!");
                            return;
                        } else {
                            Log.d("jack", "is over!!");
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.prompt);
                            builder.setMessage(msg);
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    goToWebUrl(redirect);
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

                    Log.d("putString Constants.PPasswd",
                            "Constants.PPasswd:"
                                    + android.provider.Settings.System.putString(getContentResolver(), "PPasswd",
                                            Constants.PPasswd));
                    Log.d("putString Constants.POldPasswd",
                            "Constants.POldPasswd:"
                                    + android.provider.Settings.System.putString(getContentResolver(), "POldPasswd",
                                            Constants.POldPasswd));

                    Log.d("POldPasswd",
                            "Constants.PPasswd:"
                                    + android.provider.Settings.System.getString(getContentResolver(), "PPasswd"));
                    Log.d("POldPasswd",
                            "Constants.PPasswd:"
                                    + android.provider.Settings.System.getString(getContentResolver(), "POldPasswd"));

                    Log.d("Constants.POldPasswd", "Constants.PPasswd:" + Constants.PPasswd);
                } else {
                    msboxeditNew(R.string.enter_pandora_dialog_editpwd_emptytitle,
                            R.string.enter_pandora_dialog_editpwd_emptytext);
                    // msboxedit("密碼為空白","請牢記，避免造成客服人員負擔!");
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

    public void msboxSkipPandora(int wrongPasswordDialogTitle, int wrongPasswordDialogContent) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setTitle(wrongPasswordDialogTitle);
        dlgAlert.setMessage(wrongPasswordDialogContent);

        dlgAlert.setPositiveButton(R.string.check_password_dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                tempData = HistoryRec.block[4] + ',' + HistoryRec.block5Action[0] + ',' + "" + ','
                        + HistoryRec.getCurrentDateTime();
                Log.d("rec", "tempData:" + tempData);
                HistoryRec.writeToFile(tempData);

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

    public EOSWebView getmWebView() {
        return web_pandoraLeft;
    }

    public ImageButton getImagewebview() {
        return btn_pandoraLeft;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("jack", "onResume");
        change = "";
        msg = "";
        redirect = "";
        day = "";
        state = 0;
        state_ok = false;
        Thread thread = new Thread(chk_date_run);
        thread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        web_pandoraLeft.stopLoading();
        web_pandoraRight.stopLoading();
        web_pandoraBottom.stopLoading();
        Log.d("MyWebViewClient", "onPause:");
        web_pandoraLeft.clearFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        web_pandoraLeft.stopLoading();
        web_pandoraRight.stopLoading();
        web_pandoraBottom.stopLoading();
    }

}
