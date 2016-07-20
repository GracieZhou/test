
package com.eostek.isynergy.setmeup.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.eostek.isynergy.setmeup.R;
import com.eostek.isynergy.setmeup.StateMachineActivity;
import com.eostek.isynergy.setmeup.common.Constants;
import com.eostek.isynergy.setmeup.common.Constants.ACTION_TYPE;
import com.eostek.isynergy.setmeup.config.ServiceManager;
import com.eostek.isynergy.setmeup.config.nickname.DevNameService;
import com.eostek.isynergy.setmeup.service.WifiService;

public class SetmeupMainActivity extends Activity {

    private final String TAG = "SetmeupMainActivity";

    private final String DOWNLOAD_URL = "<u>t.cn/RvDMWRD</u>";

    private final String KEY_DEV_NAME = "DEV_NAME";

    private final String SETUPWIZARD_PACKAGE_NAME = "com.eostek.isynergy.setmeup";

    private final String SETUPWIZARD_CLASS_NAME = "com.eostek.isynergy.setmeup.StateMachineActivity";

    private final String PROP_PLATFORM = "ro.scifly.platform";

    private final String PLATFORM_DONGLE = "dongle";

    private final int SET_DEV_NAME = 0;
    
    private String platform;

    private TextView txtVersion;

    private TextView paircode;

    private TextView txtCurrPhrase;

    private TextView txtUrl;

    private TextView name;

    private HandlerThread devNameThread = new HandlerThread("devNameThread");

    private Handler devNameHandler;

    private DevNameTask task = new DevNameTask();

    private Handler uiHandler = new Handler() {
        public void handleMessage(Message mes) {
            int what = mes.what;
            if (SET_DEV_NAME == what) {
                Bundle bundle = mes.getData();
                String devName = bundle.getString(KEY_DEV_NAME);
                Log.d(TAG, "device name is set to " + devName);
                name.setText(devName);
            } else if (Constants.CONTROLLER_UI_FINISH == what) {
                Log.d(TAG, "finishActivity is done");
                finishActivity();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate.................................................");
        Log.d(TAG, "2015-01-29   Eason.xiang   SVN:17062");

        setContentView(R.layout.setmeup_main);
        initView();
        
        platform = SystemProperties.get(PROP_PLATFORM, PLATFORM_DONGLE);
        
        // only start SetMeup on dongle,or start setupwizard
        if (platform.equalsIgnoreCase(PLATFORM_DONGLE)) {
            startService(new Intent(this, WifiService.class));

            devNameThread.start();
            devNameHandler = new Handler(devNameThread.getLooper());
        } else {

            finishActivity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume.................................................");
        if (platform.equalsIgnoreCase(PLATFORM_DONGLE)) {
        	setVersion();

            devNameHandler.post(task);

            doController(getIntent());
        }
    }


    private void setVersion() {
        String version = "V";
        try {
            version += getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

        } catch (NameNotFoundException e) {
            version = "V0.1";
        }
        txtVersion.setText(version);
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent........................................");
        this.setIntent(intent);
        // doController(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            ServiceManager.getInstance(this).doAction(ACTION_TYPE.WIFI_ENABLED, null);

            finishActivity();
        }
        return false;
    }

    @Override
    protected void onStop() {
    	super.onStop();
    	Log.d(TAG, "onStop...............");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy...............");

        if (devNameThread != null) {
            if (devNameHandler != null) {
                devNameHandler.removeCallbacks(task);
                devNameHandler = null;
            }

            devNameThread.quit();
            devNameThread = null;
        }
    }

    private void doController(Intent intent) {
        Log.d(TAG, "do controller ");
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int actionType = bundle.getInt(Constants.NAME_ACTION_TYPE);
                String paraValue = bundle.getString(Constants.NAME_PARAMETER);
                Log.d(TAG, "actionType = " + actionType + "  paraValue=" + (paraValue == null ? "" : paraValue));
                if (actionType == Constants.ACTION_TYPE.REQ_PAIRING_CODE.getValue()) {
                    txtCurrPhrase.setText(getString(R.string.phrase_req_pairing_code));
                    if (paraValue != null) {
                        paircode.setText(paraValue);
                    }
                } else if (actionType == Constants.ACTION_TYPE.TIMEZONE_SETTING.getValue()) {
                    txtCurrPhrase.setText(getString(R.string.phrase_timezone_setting));
                    if (paraValue != null) {
                        paircode.setText(paraValue);
                    }
                } else if (actionType == Constants.ACTION_TYPE.RESTORE_FACTORY.getValue()) {
                    txtCurrPhrase.setText(getString(R.string.phrase_restoring_factory));
                    if (paraValue != null) {
                        paircode.setText(paraValue);
                    }
                } else if (actionType == Constants.ACTION_TYPE.DEVICE_NAME_SETTING.getValue()) {
                    txtCurrPhrase.setText(getString(R.string.phrase_device_name_setting));
                    if (paraValue != null) {
                        paircode.setText(paraValue);
                    }
                } else if (actionType == Constants.ACTION_TYPE.WIFI_SETTING.getValue()) {
                    txtCurrPhrase.setText(getString(R.string.phrase_wifi_setting));
                    if (paraValue != null) {
                        paircode.setText(paraValue);
                    }
                } else if (actionType == Constants.CONTROLLER_UI_FINISH)// exit
                                                                        // activity
                {
                    String phraseText = txtCurrPhrase.getText().toString();
                    if (getString(R.string.phrase_wifi_setting).equals(phraseText)) {
                        phraseText = paircode.getText().toString();
                        phraseText = parseStatusText(phraseText);
                        phraseText += getString(R.string.phrase_wifi_setting_success);
                        paircode.setText(phraseText);
                    }

                    uiHandler.sendEmptyMessageDelayed(Constants.CONTROLLER_UI_FINISH, 3000);
                } else if (actionType == Constants.CONTROLLER_UI_SHOW) {
                    String phraseText = txtCurrPhrase.getText().toString();
                    if (getString(R.string.phrase_wifi_setting).equals(phraseText)) {
                        phraseText = paircode.getText().toString();
                        phraseText = parseStatusText(phraseText);
                        phraseText += getString(R.string.phrase_wifi_setting_fail);
                        paircode.setText(phraseText);
                    }
                }
            }
        }
    }

    private String parseStatusText(String phraseText) {
        if (phraseText.contains(getString(R.string.phrase_wifi_setting_fail))
                || phraseText.contains(getString(R.string.phrase_wifi_setting_success))) {
            int index = phraseText.lastIndexOf(":");
            if (index > 0) {
                phraseText = phraseText.substring(0, index);
            }
        }
        return phraseText;
    }

    private void initView() {
        txtVersion = (TextView) findViewById(R.id.version);
        name = (TextView) findViewById(R.id.name);
        paircode = (TextView) findViewById(R.id.paircode);
        txtUrl = (TextView) findViewById(R.id.url);
        txtUrl.setText(Html.fromHtml(getString(R.string.downlaod_url_I) + "<font color='#CC3300'>" + DOWNLOAD_URL
                + "</font>"));

        txtCurrPhrase = (TextView) findViewById(R.id.current_phrase);
    }

    private void setDevName() {
        Log.d(TAG, "setDevName start ...");
        DevNameService extDevNameService = ServiceManager.getInstance(getApplicationContext()).getDevNameService();
        String devName = extDevNameService.getDevName();
        Log.d(TAG, "device name is " + devName);
        Message mes = uiHandler.obtainMessage();
        mes.what = SET_DEV_NAME;
        Bundle bundle = mes.getData();
        bundle.putString(KEY_DEV_NAME, devName);
        mes.setData(bundle);
        uiHandler.sendMessage(mes);

        Log.d(TAG, "setDevName end ...");
    }

    class DevNameTask implements Runnable {
        @Override
        public void run() {
            setDevName();
        }
    }

    public void finishActivity() {
    	PackageManager pm = getPackageManager();
        if (PackageManager.COMPONENT_ENABLED_STATE_DISABLED != pm.getComponentEnabledSetting(new ComponentName(this,
                StateMachineActivity.class))) {
            startSetupWizardActivity();
        }else{
        	ComponentName setmeup = new ComponentName(this, SetmeupMainActivity.class);
            pm.setComponentEnabledSetting(setmeup, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
        finish();
    }

	private void startSetupWizardActivity() {
        Intent intent = new Intent();
        intent.setClassName(SETUPWIZARD_PACKAGE_NAME, SETUPWIZARD_CLASS_NAME);
        startActivity(intent);
    }

}
