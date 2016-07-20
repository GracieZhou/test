
package com.google.tv.eoslauncher.ui.fragment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.EOSWebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.tv.eoslauncher.HomeActivity;
import com.google.tv.eoslauncher.MainViewHolder;
import com.google.tv.eoslauncher.R;
import com.google.tv.eoslauncher.business.MyWebViewClient;
import com.google.tv.eoslauncher.model.MyAD;
import com.google.tv.eoslauncher.model.ViewBean;
import com.google.tv.eoslauncher.ui.app.AppStoreActivity;
import com.google.tv.eoslauncher.util.Constants;
import com.google.tv.eoslauncher.util.FocusView;
import com.google.tv.eoslauncher.util.HistoryRec;
import com.google.tv.eoslauncher.util.GoogleAnalyticsUtil;
import com.google.tv.eoslauncher.util.UIUtil;
import com.google.tv.eoslauncher.util.Utils;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

/**
 * projectName： EosLauncher moduleName： ShopFragment.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2013-12-18 下午2:10:03
 * @Copyright © 2013 Eos Inc.
 */
public class PandoraFragment extends PublicFragment {

    private static final String TAG = "PandoraFragment";

    private HomeActivity mContext;

    private MainViewHolder mHolder;

    // progressbar object
    // private FrameLayout prgBar;

    private LinearLayout mWebViewcontent;

    private LinearLayout mFocusViewcontent;
    
    private AlertDialog.Builder mDlgAlert;

    private AlertDialog mDialog;

    

    private MyWebViewClient mwebviewClien = null;

    private EOSWebView mView_left;

    private EOSWebView mView_top;

    private EOSWebView mView_bottom;

    private ImageButton bt_left;

    private ImageButton bt_top;

    private ImageButton bt_bottom;

    private ProgressBar prBar_left;

    private ProgressBar prBar_top;

    private ProgressBar prBar_bottom;

    private FocusView mFocusView;

    private MyOnFocusChangeListener mFocusChangeListener;

    private ShopOnClickListener mClickListener;

    private ViewBean mViewBean;

    // private EditText userInput;

    private EditText userInput, userInputNew, userInputOld;

    private int position = 0;

    private String tempData = "";

    String change = "";

    String msg = "";

    String redirect = "";

    String day = "";

    int state;

    boolean state_ok = false;

    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public void setmViewBean(ViewBean mViewBean) {
        this.mViewBean = mViewBean;
    }

    private String ua = "Mozilla/5.0 (iPad; U; " + "CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 "
            + "(KHTML, like Gecko) Version/4.0.4 Mobile/7B367 Safari/531.21.10";

    public PandoraFragment() {
        super();
    }

    public PandoraFragment(HomeActivity context, MainViewHolder mHolder) {
        this.mContext = context;
        this.mHolder = mHolder;
        mViewBean = new ViewBean(null, null);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.pandora_main, container, false);
        position = MainViewHolder.pandoraLastPosition;

        mFocusChangeListener = new MyOnFocusChangeListener();
        mClickListener = new ShopOnClickListener();

        mView_left = (EOSWebView) mview.findViewById(R.id.webview_01);
        mView_left.setHorizontalScrollBarEnabled(false);
        mView_left.setVerticalScrollBarEnabled(false);

        mView_top = (EOSWebView) mview.findViewById(R.id.webview_02);
        mView_top.setHorizontalScrollBarEnabled(false);
        mView_top.setVerticalScrollBarEnabled(false);

        mView_bottom = (EOSWebView) mview.findViewById(R.id.webview_03);
        mView_bottom.setHorizontalScrollBarEnabled(false);
        mView_bottom.setVerticalScrollBarEnabled(false);

        userInput = (EditText) mview.findViewById(R.id.editTextDialogUserInput);
        userInputNew = (EditText) mview.findViewById(R.id.editTextDialogUserInputNew);
        userInputOld = (EditText) mview.findViewById(R.id.editTextDialogUserInputOld);

        bt_left = (ImageButton) mview.findViewById(R.id.pandora_bt_01);
        bt_top = (ImageButton) mview.findViewById(R.id.pandora_bt_02);
        bt_bottom = (ImageButton) mview.findViewById(R.id.pandora_bt_03);
        // prgBar = (FrameLayout) mview.findViewById(R.id.prg);
        mWebViewcontent = (LinearLayout) mview.findViewById(R.id.webviewcontent);
        mFocusViewcontent = (LinearLayout) mview.findViewById(R.id.focusviewcontent);
        prBar_left = (ProgressBar) mview.findViewById(R.id.pgb_01);
        prBar_top = (ProgressBar) mview.findViewById(R.id.pgb_02);
        prBar_bottom = (ProgressBar) mview.findViewById(R.id.pgb_03);

        mFocusView = (FocusView) mview.findViewById(R.id.home_selector);
        mViewBean.setmFocusObject(mFocusView);
        mFocusView.setVisibility(View.INVISIBLE);

        if (mViewBean.getmCurFocusView() == null) {
            mViewBean.setmCurFocusView(bt_left);
        } else if (mViewBean.getmCurFocusView() == mHolder.home_content) {
            mViewBean.setmCurFocusView(bt_top);
        }

        initButtonListener();

        addViewGlobalLayoutListener(bt_left, mViewBean);
        addViewGlobalLayoutListener(bt_top, mViewBean);
        addViewGlobalLayoutListener(bt_bottom, mViewBean);

        setRetainInstance(true);
        if (android.provider.Settings.System.getString(mContext.getContentResolver(), "PPasswd") == null) {
            android.provider.Settings.System.putString(mContext.getContentResolver(), "PPasswd", Constants.PPasswd);
            Constants.Preset = true;
        }

        if (android.provider.Settings.System.getString(mContext.getContentResolver(), "POldPasswd") == null) {
            android.provider.Settings.System.putString(mContext.getContentResolver(), "POldPasswd",
                    Constants.POldPasswd);
            Constants.Preset = true;
        }

        msbox(R.string.enter_pandora_dialog_title, R.string.enter_pandora_dialog_content);

        return mview;
    }

    @Override
    public void onStart() {
        super.onStart();

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

    private Runnable chk_date_run = new Runnable() {
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
                if (obj != null) {
                    change = obj.getString("change");
                    msg = obj.getString("msg");
                    redirect = obj.getString("redirect");
                    day = obj.getString("day");
                }

            } catch (Exception e) {
                Log.d("jack", "debug 2: " + e.toString());
            }
        }
    };

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
        mDialog.dismiss();
        mView_left.stopLoading();
        mView_top.stopLoading();
        mView_bottom.stopLoading();
        Log.d("MyWebViewClient", "onPause:");
        mView_left.clearFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mView_left.stopLoading();
        mView_top.stopLoading();
        mView_bottom.stopLoading();
    }

    /**
     * EOSWebView obj
     * 
     * @return
     */
    public EOSWebView getmWebView() {
        return mView_left;
    }

    /**
     * ImageButton obj
     * 
     * @return
     */
    public ImageButton getImagewebview() {
        return bt_left;
    }

    private void initButtonListener() {
        bt_left.setOnFocusChangeListener(mFocusChangeListener);
        bt_top.setOnFocusChangeListener(mFocusChangeListener);
        bt_bottom.setOnFocusChangeListener(mFocusChangeListener);

        bt_left.setOnClickListener(mClickListener);
        bt_top.setOnClickListener(mClickListener);
        bt_bottom.setOnClickListener(mClickListener);

        bt_left.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                // if the fragment animation is running,return true
                if (isRunning) {
                    return true;
                }
                if (arg2.getAction() == KeyEvent.ACTION_DOWN && arg1 == KeyEvent.KEYCODE_DPAD_LEFT) {
                    // mContext.playVoice(KeyEvent.KEYCODE_DPAD_LEFT);
                    // mViewBean.setmCurFocusView(bt_left);
                    mContext.showViews(Constants.MEDIAVIEW);
                    return true;
                }
                if (arg1 == KeyEvent.KEYCODE_DPAD_DOWN) {
                    return true;
                }
                return false;
            }
        });

        bt_top.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                // if the fragment animation is running,return true
                if (isRunning) {
                    return true;
                }
                if (arg2.getAction() == KeyEvent.ACTION_DOWN && arg1 == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    // mContext.playVoice(KeyEvent.KEYCODE_DPAD_RIGHT);
                    mViewBean.setmCurFocusView(bt_top);
                    mContext.showViews(Constants.SHOPWEBVIEW);
                    return true;
                }
                return false;
            }
        });

        bt_bottom.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                // if the fragment animation is running,return true
                if (isRunning) {
                    return true;
                }
                if (arg2.getAction() == KeyEvent.ACTION_DOWN && arg1 == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    // mContext.playVoice(KeyEvent.KEYCODE_DPAD_RIGHT);
                    mViewBean.setmCurFocusView(bt_bottom);
                    mContext.showViews(Constants.SHOPWEBVIEW);
                    return true;
                }
                if (arg1 == KeyEvent.KEYCODE_DPAD_DOWN) {
                    return true;
                }
                return false;
            }
        });

    }

    private void goToWebUrl(String url) {
        Intent intent = new Intent(mContext, AppStoreActivity.class);
        Bundle bundle = new Bundle();
        if (url == null || url.equals("")) {
            url = Constants.defaultURL;
        }
        bundle.putString("URL", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    class MyOnFocusChangeListener implements OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                mFocusView.startAnimation(view);
                mViewBean.setmCurFocusView(view);
            }
        }
    }

    class ShopOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            String clickUrl = null;
            if (!Utils.isNetworkState) {
                UIUtil.toastShow(R.string.shop_no_network, getActivity());
                // UIUitl.toastShow(R.string.shop_no_network, getActivity());
                return;
            }
            MyAD adinfo = new MyAD();
            switch (view.getId()) {
                case R.id.pandora_bt_01:
                    adinfo.setGln("com.mtech.cube.pandora_Heran,com.mtech.cube.pandora_Heran.activity.MainLayoutActivity" + 0);
                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_PANDORA, adinfo, 1);
					tempData = HistoryRec.block[4] + ',' + HistoryRec.block5Action[2] + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                    Log.d("rec", "tempData:" + tempData);
                    HistoryRec.writeToFile(tempData);
                    mContext.startApk("com.mtech.cube.pandora_Heran",
                            "com.mtech.cube.pandora_Heran.activity.MainLayoutActivity", null);
                    break;
                case R.id.pandora_bt_02:
                    clickUrl = "http://www.jowinwin.com/hertv2msd/index.php?r=pandora/pandora";

                    adinfo.setGln(clickUrl);
                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_PANDORA, adinfo, 1);

                    goToWebUrl(clickUrl);
                    break;
                case R.id.pandora_bt_03:
                    break;
                default:
                    break;
            }
        }
    }

    public void msbox(int enterPandoraDialogTitle, int enterPandoraDialogContent) {
        mDlgAlert = new AlertDialog.Builder(mContext);
        mDlgAlert.setTitle(enterPandoraDialogTitle);
        mDlgAlert.setMessage(enterPandoraDialogContent);

        mDlgAlert.setPositiveButton(R.string.enter_pandora_dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                msboxpw(R.string.check_password_dialog_title, R.string.check_password_dialog_content);
            }
        }).setNeutralButton(R.string.enter_pandora_dialog_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
            	tempData = HistoryRec.block[4] + ',' + HistoryRec.block5Action[1] + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                HistoryRec.writeToFile(tempData);
                Log.d("rec", "close :" + tempData);
                dialog.cancel();
                Log.i(TAG, "position = " + position);
                if (position == Constants.MEDIAVIEW) {
                    mContext.showViews(Constants.SHOPWEBVIEW);
                } else if (position == Constants.SHOPWEBVIEW) {
                    mContext.showViews(Constants.MEDIAVIEW);
                }
            }
        }).setNegativeButton(R.string.skip_pandora_dialog_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                msboxSkipPandora(R.string.skip_pandora_dialog_defaultedit_title,
                        R.string.skip_pandora_dialog_defaultedit_text);
            }
        });
        // });
        mDlgAlert.setCancelable(false);
        mDialog = mDlgAlert.create();
        mDialog.show();
    }

    public void msboxpw(int checkPasswordDialogTitle, int checkPasswordDialogContent) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(mContext);
        dlgAlert.setTitle(checkPasswordDialogTitle);
        dlgAlert.setMessage(checkPasswordDialogContent);
        userInput = new EditText(mContext);
        userInput.setHint(R.string.enter_pandora_dialog_default_text);
        userInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        dlgAlert.setView(userInput);

        dlgAlert.setPositiveButton(R.string.check_password_dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close current activity
                final String srtcheck = "8888";
                String srt = userInput.getEditableText().toString();

                Log.d("PPasswd",
                        "PPasswd:"
                                + android.provider.Settings.System.getString(mContext.getContentResolver(), "PPasswd"));
                Log.d("POldPasswd",
                        "POldPasswd:"
                                + android.provider.Settings.System.getString(mContext.getContentResolver(),
                                        "POldPasswd"));

                Constants.PPasswd = android.provider.Settings.System.getString(mContext.getContentResolver(), "PPasswd");
                Log.d("Constants.POldPasswd", "Constants.POldPasswd:" + Constants.POldPasswd);
                Log.d("Constants.POldPPPasswdasswd", "Constants.PPasswd:" + Constants.PPasswd);

                if (!srt.toString().isEmpty()) {
                    if (srt.toString().equalsIgnoreCase(srtcheck.toString())) {
                        msboxeditNew(R.string.enter_pandora_dialog_superpwd_title,
                                R.string.enter_pandora_dialog_editpwd_editnewtext);
                    } else if (srt.toString().equalsIgnoreCase(Constants.PPasswd.toString())) {
                        // if(srt.toString().equalsIgnoreCase(Constants.POldPasswd.toString())){
                        Constants.CheckPW18 = true;
                        bt_left.setImageResource(R.drawable.shop_left_p);
                        bt_top.setImageResource(R.drawable.shop_right_up_p);
                        bt_bottom.setImageResource(R.drawable.shop_right_down_p);
                        if (state_ok) {
                            if (change.equals("0")) {
                                Log.d("jack", "not over!!");
                                return;
                            } else {
                                Log.d("jack", "is over!!");
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle("提示");
                                builder.setMessage(msg);
                                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        goToWebUrl(redirect);
                                    }
                                });
                                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {

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
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                // Constants.pay=false;
                dialog.cancel();
                if (position == Constants.MEDIAVIEW) {
                    mContext.showViews(Constants.SHOPWEBVIEW);
                } else if (position == Constants.SHOPWEBVIEW) {
                    mContext.showViews(Constants.MEDIAVIEW);
                }
            }
        }).setNegativeButton(R.string.enter_pandora_dialog_defaultedit_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                // msboxedit("請輸入新密碼","請牢記，避免造成客服人員負擔!");
                msboxedit(R.string.enter_pandora_dialog_editpwd_editnewtitle,
                        R.string.enter_pandora_dialog_current_text);

            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    public void msboxrepeat(int wrongPasswordDialogTitle, int wrongPasswordDialogContent) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(mContext);
        dlgAlert.setTitle(wrongPasswordDialogTitle);
        dlgAlert.setMessage(wrongPasswordDialogContent);

        dlgAlert.setPositiveButton(R.string.wrong_password_dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                msboxpw(R.string.check_password_dialog_title, R.string.check_password_dialog_content);
            }
        }).setNeutralButton(R.string.check_password_dialog_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                dialog.cancel();
                if (position == Constants.MEDIAVIEW) {
                    mContext.showViews(Constants.SHOPWEBVIEW);
                } else if (position == Constants.SHOPWEBVIEW) {
                    mContext.showViews(Constants.MEDIAVIEW);
                }
            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    // public void msboxedit(String str,String str2)
    public void msboxedit(int editPWDTitle, int editPWDText) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(mContext);
        dlgAlert.setTitle(editPWDTitle);
        dlgAlert.setMessage(editPWDText);
        userInputOld = new EditText(mContext);
        userInputOld.setHint(R.string.enter_pandora_dialog_current_text);
        userInputOld.setTransformationMethod(PasswordTransformationMethod.getInstance());
        dlgAlert.setView(userInputOld);

        // dlgAlert.setPositiveButton("[確定]",new
        // DialogInterface.OnClickListener() {
        dlgAlert.setPositiveButton(R.string.check_password_dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close current activity
                // final String srtcheck ="0000";
                final String srtcheck = "8888";
                String srt = userInputOld.getEditableText().toString();

                if (!srt.toString().isEmpty()) {
                    // if(srt.toString().equalsIgnoreCase(srtcheck.toString())){
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
                        String srtold = android.provider.Settings.System.getString(mContext.getContentResolver(),
                                "POldPasswd");

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
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                // Constants.pay=false;
                dialog.cancel();
                if (position == Constants.MEDIAVIEW) {
                    mContext.showViews(Constants.SHOPWEBVIEW);
                } else if (position == Constants.SHOPWEBVIEW) {
                    mContext.showViews(Constants.MEDIAVIEW);
                }
            }
        }).setNegativeButton(R.string.enter_pandora_dialog_passwd_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                msboxpw(R.string.check_password_dialog_title, R.string.check_password_dialog_content);

            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    // public void msboxeditNew(String str,String str2)
    public void msboxeditNew(int editNewPWDTitle, int editNewPWDText) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(mContext);
        dlgAlert.setTitle(editNewPWDTitle);
        dlgAlert.setMessage(editNewPWDText);

        userInputNew = new EditText(mContext);
        userInputNew.setHint(R.string.enter_pandora_dialog_new_text);
        userInputNew.setTransformationMethod(PasswordTransformationMethod.getInstance());
        dlgAlert.setView(userInputNew);

        // dlgAlert.setPositiveButton("[確定]",new
        // DialogInterface.OnClickListener() {
        dlgAlert.setPositiveButton(R.string.check_password_dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close current activity
                String srtNew = userInputNew.getEditableText().toString();

                if (!srtNew.toString().isEmpty()) {
                    Log.d("srtNew.toString()", "srtcheck.toString()" + srtNew.toString());

                    Constants.PPasswd = srtNew;
                    Constants.POldPasswd = srtNew;
                    Constants.Preset = false;

                    android.provider.Settings.System.putString(mContext.getContentResolver(), "PPasswd",
                            Constants.PPasswd);
                    android.provider.Settings.System.putString(mContext.getContentResolver(), "POldPasswd",
                            Constants.POldPasswd);

                    Constants.CheckPW18 = true;
                    bt_left.setImageResource(R.drawable.shop_left_p);
                    bt_top.setImageResource(R.drawable.shop_right_up_p);
                    bt_bottom.setImageResource(R.drawable.shop_right_down_p);
                    if (state_ok) {
                        if (change.equals("0")) {
                            Log.d("jack", "not over!!");
                            return;
                        } else {
                            Log.d("jack", "is over!!");
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("提示");
                            builder.setMessage(msg);
                            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    goToWebUrl(redirect);
                                }
                            });
                            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {

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
                                    + android.provider.Settings.System.putString(mContext.getContentResolver(),
                                            "PPasswd", Constants.PPasswd));
                    Log.d("putString Constants.POldPasswd",
                            "Constants.POldPasswd:"
                                    + android.provider.Settings.System.putString(mContext.getContentResolver(),
                                            "POldPasswd", Constants.POldPasswd));

                    Log.d("POldPasswd",
                            "Constants.PPasswd:"
                                    + android.provider.Settings.System.getString(mContext.getContentResolver(),
                                            "PPasswd"));
                    Log.d("POldPasswd",
                            "Constants.PPasswd:"
                                    + android.provider.Settings.System.getString(mContext.getContentResolver(),
                                            "POldPasswd"));

                    Log.d("Constants.POldPasswd", "Constants.PPasswd:" + Constants.PPasswd);

                    // android.provider.Settings.System.getString(mContext.getContentResolver(),
                    // "PPasswd");

                } else {
                    msboxeditNew(R.string.enter_pandora_dialog_editpwd_emptytitle,
                            R.string.enter_pandora_dialog_editpwd_emptytext);
                    // msboxedit("密碼為空白","請牢記，避免造成客服人員負擔!");
                }

            }
        }).setNeutralButton(R.string.check_password_dialog_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                // Constants.pay=false;
                dialog.cancel();
                if (position == Constants.MEDIAVIEW) {
                    mContext.showViews(Constants.SHOPWEBVIEW);
                } else if (position == Constants.SHOPWEBVIEW) {
                    mContext.showViews(Constants.MEDIAVIEW);
                }
            }
        }).setNegativeButton(R.string.enter_pandora_dialog_passwd_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                msboxpw(R.string.check_password_dialog_title, R.string.check_password_dialog_content);

            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    public void msboxSkipPandora(int wrongPasswordDialogTitle, int wrongPasswordDialogContent) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(mContext);
        dlgAlert.setTitle(wrongPasswordDialogTitle);
        dlgAlert.setMessage(wrongPasswordDialogContent);

        dlgAlert.setPositiveButton(R.string.check_password_dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	
                tempData = HistoryRec.block[4] + ',' + HistoryRec.block5Action[0] + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                Log.d("rec", "tempData:" + tempData);
                HistoryRec.writeToFile(tempData);

                Constants.SkipPandora = "1";
                android.provider.Settings.System.putString(mContext.getContentResolver(), "SkipPandora",
                        Constants.SkipPandora);
                if (position == Constants.MEDIAVIEW) {
                    mContext.showViews(Constants.SHOPWEBVIEW);
                } else if (position == Constants.SHOPWEBVIEW) {
                    mContext.showViews(Constants.MEDIAVIEW);
                }
            }
        }).setNeutralButton(R.string.check_password_dialog_no, new DialogInterface.OnClickListener() {
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
}
