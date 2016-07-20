
package com.eostek.mkeyeventservice;

import com.eostek.mkeyeventservice.dialog.BalanceDialog;
import com.eostek.mkeyeventservice.dialog.DataDisplayDialog;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvFactoryManager;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.factory.vo.EnumAcOnPowerOnMode;

import java.util.List;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import scifly.view.KeyEventExtra;

@SuppressWarnings("unused")
public class MKeyEventHolder {
    private WindowManager wManager;

    private WindowManager.LayoutParams mParams;

    static final String TAG = "MKeyEventHolder";

    private AudioManager mAudioManager;

    private static View mView = null;

    private int mCurSource;

    private Context mContext;

    private Handler mHanlder = new Handler() {
        @SuppressWarnings("static-access")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.DO_FAST_RESTORE:
                    if (FastRestore.getInstance(mContext).doRestore()) {
                        Toast.makeText(mContext, "Initialization Success,System Will Reboot!", Toast.LENGTH_SHORT)
                                .show();
                        mHanlder.sendEmptyMessageDelayed(Constants.REBOOT, 1000);
                    } else {
                        Toast.makeText(mContext, "Initialization Failed", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.REBOOT:
                    // FastRestore.getInstance(mContext).mPermission.exec("reboot");
                    break;
            }
        }
    };

    @SuppressWarnings("deprecation")
    public MKeyEventHolder(Context context) {
        mContext = context;
        mCurSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        if (!Utils.getCurrentActivity(mContext).equals("com.eostek.tv.player.PlayerActivity")) {
            startApk("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity", null);
        }
		//new Thread(BurnInThreadRunnable.getInstance(mContext)).start();	
	    TvFactoryManager.getInstance().setPowerOnMode(EnumAcOnPowerOnMode.E_ACON_POWERON_DIRECT);
    }

    public void handlerMKeyEvent(int keyCode) {
        int currentVolume;
        if (Settings.System.getInt(mContext.getContentResolver(), "Burn", 0) == 1
                && (keyCode != KeyEventExtra.KEYCODE_MSTAR_BURN_MODE && keyCode != KeyEventExtra.KEYCODE_MSTAR_POWER)) {
            return;
        }
        switch (keyCode) {
            case KeyEventExtra.KEYCODE_MSTAR_INFO:
            case KeyEventExtra.KEYCODE_MSTAR_POWER:
            case KeyEventExtra.KEYCODE_MSTAR_EXIT:
            case KeyEventExtra.KEYCODE_MSTAR_MENU:
            case KeyEventExtra.KEYCODE_MSTAR_HOME:
            case KeyEventExtra.KEYCODE_MSTAR_SOURCE:
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_1:
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_2:
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_3:
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_4:
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_5:
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_6:
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_7:
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_8:
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_9:
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_0:
            case KeyEventExtra.KEYCODE_MSTAR_CH_PLUS:
            case KeyEventExtra.KEYCODE_MSTAR_CH_MINUS:
            case KeyEventExtra.KEYCODE_MSTAR_UP:
            case KeyEventExtra.KEYCODE_MSTAR_DOWN:
            case KeyEventExtra.KEYCODE_MSTAR_LEFTD:
            case KeyEventExtra.KEYCODE_MSTAR_RIGHT:
            case KeyEventExtra.KEYCODE_MSTAR_SELECT:
                Utils.HandlerVirtualEvent(keyCode);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_FIFTEEN_PLUS: {
                currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
                if ((currentVolume + 15) >= 100) {
                    currentVolume = 100;
                } else {
                    currentVolume = currentVolume + 15;
                }
                mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, currentVolume, AudioManager.FLAG_PLAY_SOUND
                        | AudioManager.FLAG_SHOW_UI);
            }
                break;
            case KeyEventExtra.KEYCODE_MSTAR_FIFTEEN_MINUS: {
                currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
                if ((currentVolume - 15) <= 0) {
                    currentVolume = 0;
                } else {
                    currentVolume = currentVolume - 15;
                }
                mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, currentVolume, AudioManager.FLAG_PLAY_SOUND
                        | AudioManager.FLAG_SHOW_UI);
            }
                break;

            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_ATV:
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_DTV:
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_AV:
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_YPBPR:
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_VGA:
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_HDMI1:
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_HDMI2:
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_HDMI3:
                changeSource(keyCode);
                break;

            case KeyEventExtra.KEYCODE_MSTAR_BURN_MODE:
			Log.d(TAG,"KeyEventExtra.KEYCODE_MSTAR_BURN_MODE");
                if (Settings.System.getInt(mContext.getContentResolver(), "Burn", 0) == 0) {
                    Settings.System.putInt(mContext.getContentResolver(), "Burn", 1);
                    BurnInThreadRunnable.getInstance(mContext).onThreadResume();
                } else {
                    Settings.System.putInt(mContext.getContentResolver(), "Burn", 0);
                    BurnInThreadRunnable.getInstance(mContext).onThreadPause();
                    Utils.setTestPattern(0);
					if(TvCommonManager.getInstance().getCurrentTvInputSource() == TvCommonManager.INPUT_SOURCE_ATV){
						Log.d(TAG,"in ATV exit Burn ");
					    ProgramInfo info = TvChannelManager.getInstance().getCurrentProgramInfo();
						TvChannelManager.getInstance().selectProgram(info.number, info.serviceType);	
					}
                }
                break;

            case KeyEventExtra.KEYCODE_MSTAR_VERSION:
            case KeyEventExtra.KEYCODE_MSTAR_OSD_LANGUAGE:
            case KeyEventExtra.KEYCODE_MSTAR_UPGRADE:
            case KeyEventExtra.KEYCODE_MSTAR_NET:
            case KeyEventExtra.KEYCODE_MSTAR_WIFI:
                goToSettings(keyCode);
                break;

            case KeyEventExtra.KEYCODE_MSTAR_APP:
                if (Utils.getCurrentActivity(mContext).equals("com.eostek.tv.player.PlayerActivity")) {
                    startByAction("eos.intent.action.ALL_ACTIVITY");
                } else {
                    startApk("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity", null);
                }
                break;
            case KeyEventExtra.KEYCODE_MSTAR_DEFAULT_RESET:
                Intent intent = new Intent();
                intent.setClassName("com.mstar.tv.menu",
                        "com.mstar.tv.menu.setting.restore.SystemRestoreFactoryActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("isResetFactoryDB", false);
                mContext.startActivity(intent);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_ADC:
                if (Utils.getCurrentActivity(mContext).equals("com.eostek.tv.player.PlayerActivity")) {
                    if (mCurSource == TvCommonManager.INPUT_SOURCE_VGA
                            || mCurSource == TvCommonManager.INPUT_SOURCE_YPBPR) {
                        startByAction("mstar.tvsetting.factory.intent.action.FactorymenuActivity.adcAdjust");
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.adc_toast), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    startApk("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity", null);
                }

                break;
            case KeyEventExtra.KEYCODE_MSTAR_WHITE_BALANCE:
                startApk("org.wb.app", "org.wb.app.SurfaceViewPlayVideo", null);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_S_MODE:
                if (Utils.getCurrentActivity(mContext).equals("com.eostek.tv.player.PlayerActivity")) {
                    startByAction("mstar.tvsetting.factory.intent.action.MainmenuActivity");
                } else {
                    startApk("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity", null);
                }
                break;
            case KeyEventExtra.KEYCODE_MSTAR_DATA_DISPLAY:
                showDialog(new DataDisplayDialog(mContext, R.style.dialog), keyCode);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_USB:
                if (Utils.getCurrentActivity(mContext).equals("com.eostek.tv.player.PlayerActivity")) {
                    startApk("com.hrtvbic.usb.S6A918", "com.hrtvbic.usb.S6A918.MainActivity", null);
                } else {
                    startApk("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity", null);
                }
                break;

            case KeyEventExtra.KEYCODE_MSTAR_MONO:
                showDialog(new BalanceDialog(mContext, R.style.dialog), keyCode);
                break;

            case KeyEventExtra.KEYCODE_MSTAR_INITIAL:
                // mHanlder.sendEmptyMessage(Constants.DO_FAST_RESTORE);
                // break;

            case KeyEventExtra.KEYCODE_MSTAR_DTV_SEARCH:
            case KeyEventExtra.KEYCODE_MSTAR_CHANNEL_PRESET:
            case KeyEventExtra.KEYCODE_MSTAR_CZEKH_SEARCH:
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_CI:
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_SCART:
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_DVD:
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_HDMI4:
            case KeyEventExtra.KEYCODE_MSTAR_3D:
            case KeyEventExtra.KEYCODE_MSTAR_CLONED:
            case KeyEventExtra.KEYCODE_MSTAR_FREQUENCY:
            case KeyEventExtra.KEYCODE_MSTAR_NICAM:
            case KeyEventExtra.KEYCODE_MSTAR_LVDS_SSC:
            case KeyEventExtra.KEYCODE_MSTAR_CEC:
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_HDMI:
            case KeyEventExtra.KEYCODE_MSTAR_F1:
            case KeyEventExtra.KEYCODE_MSTAR_F2:
            case KeyEventExtra.KEYCODE_MSTAR_F3:
            case KeyEventExtra.KEYCODE_MSTAR_F4:
                Toast.makeText(mContext, "This function is not implemented", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    private void changeSource(int keyCode) {
        int source = TvCommonManager.INPUT_SOURCE_ATV;
        switch (keyCode) {
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_HDMI1:
                source = TvCommonManager.INPUT_SOURCE_HDMI;
                break;
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_HDMI2:
                source = TvCommonManager.INPUT_SOURCE_HDMI2;
                break;
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_HDMI3:
                source = TvCommonManager.INPUT_SOURCE_HDMI3;
                break;
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_VGA:
                source = TvCommonManager.INPUT_SOURCE_VGA;
                break;
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_ATV:
                source = TvCommonManager.INPUT_SOURCE_ATV;
                break;
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_DTV:
                source = TvCommonManager.INPUT_SOURCE_DTV;
                break;
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_YPBPR:
                source = TvCommonManager.INPUT_SOURCE_YPBPR;
                break;
            case KeyEventExtra.KEYCODE_MSTAR_SWITCH_AV:
                source = TvCommonManager.INPUT_SOURCE_CVBS;
                break;
            default:
                break;
        }
        if (mCurSource != source) {
            TvCommonManager.getInstance().setInputSource(source);
        }
        if (source == TvCommonManager.INPUT_SOURCE_ATV || source == TvCommonManager.INPUT_SOURCE_DTV) {
            ProgramInfo info = TvChannelManager.getInstance().getCurrentProgramInfo();
            TvChannelManager.getInstance().selectProgram(info.number, info.serviceType);
        }
        mCurSource = source;
        if (Utils.getCurrentActivity(mContext).equals("com.eostek.tv.player.PlayerActivity")) {
            return;
        } else {
            startApk("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity", null);
        }
    }

    private void goToSettings(int keyCode) {
        String calssName = null;
        switch (keyCode) {
            case KeyEventExtra.KEYCODE_MSTAR_WIFI:
                calssName = "com.android.settings.network.NetworkSettingActivity";
                break;
            case KeyEventExtra.KEYCODE_MSTAR_NET:
                calssName = "com.android.settings.network.NetworkSettingActivity";
                break;
            case KeyEventExtra.KEYCODE_MSTAR_OSD_LANGUAGE:
                calssName = "com.android.settings.system.SystemSettingsActivity";
                break;
            case KeyEventExtra.KEYCODE_MSTAR_UPGRADE:
                calssName = "com.android.settings.update.SystemUpdateActivity";
                break;
            case KeyEventExtra.KEYCODE_MSTAR_VERSION:
                calssName = "com.android.settings.deviceinfo.DeviceInfoActivity";
            default:
                break;
        }
        if (Utils.getCurrentActivity(mContext).equals("com.eostek.tv.player.PlayerActivity")) {
            startApk("com.android.settings", calssName, null);
        } else {
            startApk("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity", null);
        }
    }

    public void showLogo() {
        if (mView == null) {
            mView = View.inflate(mContext, R.layout.mmodel, null);
            wManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            mParams = new WindowManager.LayoutParams();
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            mParams.format = PixelFormat.TRANSLUCENT;
            mParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mParams.gravity = Gravity.LEFT | Gravity.TOP;
            mParams.width = LayoutParams.WRAP_CONTENT;
            mParams.height = LayoutParams.WRAP_CONTENT;

            wManager.addView(mView, mParams);
        }
        return;
    }

    public void removeLogo() {
		if(mView != null && wManager != null){
			wManager.removeView(mView);	
		}
        mView = null;
    }

    /**
     * start an application
     * 
     * @param pckName PackageName
     * @param clsName ClassName
     * @param bundle additional parameters, options
     */
    private void startApk(String pckName, String clsName, Bundle bundle) {
        ComponentName componentName = new ComponentName(pckName, clsName);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
		List<ResolveInfo> list = mContext.getPackageManager().queryIntentActivities(intent, 0);
        if (list.size() > 0) {
            mContext.startActivity(intent);
        } else {
                Log.d("tag", pckName + " is not found");
            }
        
    }

    private void startByAction(String action) {
        Intent intent = new Intent(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private void showDialog(Dialog dialog, int keycode) {

        Window dialogWindow = dialog.getWindow();

        if (keycode == KeyEventExtra.KEYCODE_MSTAR_MONO) {
            dialogWindow.setGravity(Gravity.BOTTOM);
        }

        WindowManager.LayoutParams param = dialogWindow.getAttributes();

        param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        dialogWindow.setAttributes(param);

        dialog.show();
    }

}
