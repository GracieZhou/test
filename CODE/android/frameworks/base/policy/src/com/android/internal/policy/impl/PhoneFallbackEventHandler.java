/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.policy.impl;

import android.app.KeyguardManager;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.IAudioService;
import android.media.session.MediaSessionLegacyHelper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.HapticFeedbackConstants;
import android.view.FallbackEventHandler;
import android.view.KeyEvent;

// MStar Android Patch Begin
import com.mstar.android.MKeyEvent;
import com.mstar.android.MIntent;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
// MStar Android Patch End

// EosTek Patch Begin
import scifly.view.KeyEventExtra;
import scifly.device.Device;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
// EosTek Patch End

public class PhoneFallbackEventHandler implements FallbackEventHandler {
    private static String TAG = "PhoneFallbackEventHandler";
    private static final boolean DEBUG = false;

    Context mContext;
    View mView;

    AudioManager mAudioManager;
    KeyguardManager mKeyguardManager;
    SearchManager mSearchManager;
    TelephonyManager mTelephonyManager;

    public PhoneFallbackEventHandler(Context context) {
        mContext = context;
    }

    public void setView(View v) {
        mView = v;
    }

    public void preDispatchKeyEvent(KeyEvent event) {
        getAudioManager().preDispatchKeyEvent(event, AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {

        final int action = event.getAction();
        final int keyCode = event.getKeyCode();

        if (action == KeyEvent.ACTION_DOWN) {
            return onKeyDown(keyCode, event);
        } else {
            return onKeyUp(keyCode, event);
        }
    }

    boolean onKeyDown(int keyCode, KeyEvent event) {
        /* ****************************************************************************
         * HOW TO DECIDE WHERE YOUR KEY HANDLING GOES.
         * See the comment in PhoneWindow.onKeyDown
         * ****************************************************************************/
        final KeyEvent.DispatcherState dispatcher = mView.getKeyDispatcherState();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_MUTE: {
                MediaSessionLegacyHelper.getHelper(mContext).sendVolumeKeyEvent(event, false);
                return true;
            }


            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                /* Suppress PLAY/PAUSE toggle when phone is ringing or in-call
                 * to avoid music playback */
                if (getTelephonyManager().getCallState() != TelephonyManager.CALL_STATE_IDLE) {
                    return true;  // suppress key event
                }
            case KeyEvent.KEYCODE_MUTE:
            case KeyEvent.KEYCODE_HEADSETHOOK:
            case KeyEvent.KEYCODE_MEDIA_STOP:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_RECORD:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_AUDIO_TRACK: {
                handleMediaKeyEvent(event);
                return true;
            }

            case KeyEvent.KEYCODE_CALL: {
                if (getKeyguardManager().inKeyguardRestrictedInputMode() || dispatcher == null) {
                    break;
                }
                if (event.getRepeatCount() == 0) {
                    dispatcher.startTracking(event, this);
                } else if (event.isLongPress() && dispatcher.isTracking(event)) {
                    dispatcher.performedLongPress(event);
                    if (isUserSetupComplete()) {
                        mView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        // launch the VoiceDialer
                        Intent intent = new Intent(Intent.ACTION_VOICE_COMMAND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            sendCloseSystemWindows();
                            mContext.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            startCallActivity();
                        }
                    } else {
                        Log.i(TAG, "Not starting call activity because user "
                                + "setup is in progress.");
                    }
                }
                return true;
            }

            case KeyEvent.KEYCODE_CAMERA: {
                if (getKeyguardManager().inKeyguardRestrictedInputMode() || dispatcher == null) {
                    break;
                }
                if (event.getRepeatCount() == 0) {
                    dispatcher.startTracking(event, this);
                } else if (event.isLongPress() && dispatcher.isTracking(event)) {
                    dispatcher.performedLongPress(event);
                    if (isUserSetupComplete()) {
                        mView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        sendCloseSystemWindows();
                        // Broadcast an intent that the Camera button was longpressed
                        Intent intent = new Intent(Intent.ACTION_CAMERA_BUTTON, null);
                        intent.putExtra(Intent.EXTRA_KEY_EVENT, event);
                        mContext.sendOrderedBroadcastAsUser(intent, UserHandle.CURRENT_OR_SELF,
                                null, null, null, 0, null, null);
                    } else {
                        Log.i(TAG, "Not dispatching CAMERA long press because user "
                                + "setup is in progress.");
                    }
                }
                return true;
            }

            case KeyEvent.KEYCODE_SEARCH: {
                if (getKeyguardManager().inKeyguardRestrictedInputMode() || dispatcher == null) {
                    break;
                }
                if (event.getRepeatCount() == 0) {
                    dispatcher.startTracking(event, this);
                } else if (event.isLongPress() && dispatcher.isTracking(event)) {
                    Configuration config = mContext.getResources().getConfiguration();
                    if (config.keyboard == Configuration.KEYBOARD_NOKEYS
                            || config.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
                        if (isUserSetupComplete()) {
                            // launch the search activity
                            Intent intent = new Intent(Intent.ACTION_SEARCH_LONG_PRESS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                mView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                                sendCloseSystemWindows();
                                getSearchManager().stopSearch();
                                mContext.startActivity(intent);
                                // Only clear this if we successfully start the
                                // activity; otherwise we will allow the normal short
                                // press action to be performed.
                                dispatcher.performedLongPress(event);
                                return true;
                            } catch (ActivityNotFoundException e) {
                                // Ignore
                            }
                        } else {
                            Log.i(TAG, "Not dispatching SEARCH long press because user "
                                    + "setup is in progress.");
                        }
                    }
                }
                break;
            }

            // MStar Android Patch Begin
            case MKeyEvent.KEYCODE_TV_PICTURE_MODE: {
                Intent intent = new Intent(MIntent.ACTION_PICTURE_MODE_BUTTON);
                mContext.sendBroadcast(intent);
                Log.i(TAG, "Send MIntent.ACTION_PICTURE_MODE_BUTTON.");
                return true;
            }

            case MKeyEvent.KEYCODE_TV_SOUND_MODE: {
                Intent intent = new Intent(MIntent.ACTION_SOUND_MODE_BUTTON);
                mContext.sendBroadcast(intent);
                Log.i(TAG, "Send MIntent.ACTION_SOUND_MODE_BUTTON.");
                return true;
            }

            case KeyEvent.KEYCODE_TV_ZOOM_MODE: {
                Intent intent = new Intent(MIntent.ACTION_ASPECT_RATIO_BUTTON);
                mContext.sendBroadcast(intent);
                Log.i(TAG, "Send MIntent.ACTION_ASPECT_RATIO_BUTTON.");
                return true;
            }

            case KeyEvent.KEYCODE_SLEEP: {
                Intent intent = new Intent(MIntent.ACTION_SLEEP_BUTTON);
                mContext.sendBroadcast(intent);
                Log.i(TAG, "Send MIntent.ACTION_SLEEP_BUTTON.");
                return true;
            }

            case MKeyEvent.KEYCODE_TV_SETTING: {
                if("com.android.mslauncher".equals(getCurRunningActivityPackageName()))
                    return false;
                Intent intent = new Intent(MIntent.ACTION_TV_SETTING_BUTTON);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                    Log.i(TAG, "Send MIntent.ACTION_SETTING_BUTTON.");
                }
                return true;
            }
			
			//EosTek Patch Begin
			


            case KeyEvent.KEYCODE_TV_INPUT: {
                Intent intent = new Intent(MIntent.ACTION_TV_INPUT_BUTTON);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                    Log.i(TAG, "Send MIntent.ACTION_TV_INPUT_BUTTON.");
                }
                return true;
            }

			//EosTek Patch End
			

            // MStar Android Patch End
			
			// EosTek Patch Begin 
			case MKeyEvent.KEYCODE_MSTAR_UPDATE: {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                Log.i(TAG, "Send tv.");
                return true;
            }
			
			case KeyEventExtra.KEYCODE_TV_3D: {
				String panel_version = readSysIni();	
		        if(panel_version.equals("0")||panel_version.equals(0)){
		          return false;  
		        }
                Intent intent = new Intent(MIntent.ACTION_TV_SETTING_BUTTON);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
				Log.i(TAG, "Send MIntent.ACTION_SETTING_BUTTON.");
                return true;
            }
			
			case KeyEvent.KEYCODE_PROG_GREEN:
            case KeyEvent.KEYCODE_APP_SWITCH: {
				if(!Device.isVipMode(mContext)){
					return true;
				}
				if (getKeyguardManager().inKeyguardRestrictedInputMode() || dispatcher == null) {
                    break;
                }
				if (event.getRepeatCount() == 0) {
				   dispatcher.startTracking(event, this);
                   Log.i(TAG, "Send DeviceManagerActivity.");
                }else if (event.isLongPress() && dispatcher.isTracking(event)) {
                   dispatcher.performedLongPress(event);
                   mView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                   Intent mTaskSwitchIntent = new Intent();
				   mTaskSwitchIntent.setAction("qingyu.TaskSwitch.launch.recent");
				   mTaskSwitchIntent.setPackage("com.eos.notificationcenter");
				   mContext.startService(mTaskSwitchIntent);
                }
            	
            	return true;
            }
			case MKeyEvent.KEYCODE_TV_REVEAL: {
				if(!Device.isVipMode(mContext)){
					return true;
				}				
            	Log.i(TAG, "start_this_activity");
                Intent intent = new Intent("com.eos.android.intent.action.screenballot");                
				mContext.startActivity(intent);
				Log.i(TAG, "Send intent action com.eos.android.intent.action.screenballot.");              
                return true;
            }
			 case MKeyEvent.KEYCODE_SUBTITLE: {
				if(!Device.isVipMode(mContext)){
					return true;
				}
                Intent intent = new Intent(Intent.ACTION_MAIN, null);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				intent.putExtra("gotolanucher", "heran");
                mContext.startActivity(intent);
                return true;
            }
			 case KeyEventExtra.KEYCODE_KARAOKE_MIC_VOLUME_UP:
			 case KeyEventExtra.KEYCODE_KARAOKE_MIC_VOLUME_DOWN:
			 case KeyEventExtra.KEYCODE_KARAOKE_MIC_KEY_UP:
			 case KeyEventExtra.KEYCODE_KARAOKE_MIC_KEY_DOWN:
			 case KeyEventExtra.KEYCODE_KARAOKE_MIC_ECHO_UP:
			 case KeyEventExtra.KEYCODE_KARAOKE_MIC_ECHO_DOWN:
			 case KeyEventExtra.KEYCODE_KARAOKE_SING_AUTO:
			 case KeyEventExtra.KEYCODE_KARAOKE_SURROUND: {
			     Intent intent = new Intent("com.eostek.scifly.karaoke");
			     intent.putExtra("keyCode", keyCode);
	             //mContext.startService(intent);
			     return true;
             }
			// EosTek Patch End 
			//ChangHong IR Begin 
			case KeyEventExtra.KEYCODE_MSTAR_MODE: {
                if (Settings.System.getInt(mContext.getContentResolver(), "MModel", 0) == 0) {
                	Settings.System.putInt(mContext.getContentResolver(), "MModel", 1);
            	} else if(Settings.System.getInt(mContext.getContentResolver(), "Burn", 0) == 0){
                	Settings.System.putInt(mContext.getContentResolver(), "MModel", 0);
            	}
				Intent intent = new Intent();
				intent.setAction("com.eostek.MKeyEventService");
				intent.setPackage("com.eostek.mkeyeventservice");
                intent.putExtra("keyCode", keyCode);
                mContext.startService(intent);
                return true;
            }
		    case KeyEventExtra.KEYCODE_MSTAR_POWER:
			case KeyEventExtra.KEYCODE_MSTAR_EXIT:
			case KeyEventExtra.KEYCODE_MSTAR_MENU:
			case KeyEventExtra.KEYCODE_MSTAR_SOURCE:
			case KeyEventExtra.KEYCODE_MSTAR_INFO:
			case KeyEventExtra.KEYCODE_MSTAR_INITIAL:
			case KeyEventExtra.KEYCODE_MSTAR_BURN_MODE:
			case KeyEventExtra.KEYCODE_MSTAR_DEFAULT_RESET:
			case KeyEventExtra.KEYCODE_MSTAR_S_MODE:
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
			case KeyEventExtra.KEYCODE_MSTAR_TEXT:
			case KeyEventExtra.KEYCODE_MSTAR_CH_PLUS:
			case KeyEventExtra.KEYCODE_MSTAR_CH_MINUS:
			case KeyEventExtra.KEYCODE_MSTAR_FIFTEEN_PLUS:
			case KeyEventExtra.KEYCODE_MSTAR_FIFTEEN_MINUS:
			case KeyEventExtra.KEYCODE_MSTAR_UP:
			case KeyEventExtra.KEYCODE_MSTAR_DOWN:
			case KeyEventExtra.KEYCODE_MSTAR_LEFTD:
			case KeyEventExtra.KEYCODE_MSTAR_RIGHT:
			case KeyEventExtra.KEYCODE_MSTAR_SELECT:
			case KeyEventExtra.KEYCODE_MSTAR_DTV_SEARCH:
			case KeyEventExtra.KEYCODE_MSTAR_CZEKH_SEARCH:
			case KeyEventExtra.KEYCODE_MSTAR_CHANNEL_PRESET:
			case KeyEventExtra.KEYCODE_MSTAR_SWITCH_ATV:
			case KeyEventExtra.KEYCODE_MSTAR_SWITCH_DTV:
			case KeyEventExtra.KEYCODE_MSTAR_SWITCH_AV:
			case KeyEventExtra.KEYCODE_MSTAR_SWITCH_YPBPR:
			case KeyEventExtra.KEYCODE_MSTAR_SWITCH_VGA:
			case KeyEventExtra.KEYCODE_MSTAR_SWITCH_CI:
			case KeyEventExtra.KEYCODE_MSTAR_SWITCH_USB:
			case KeyEventExtra.KEYCODE_MSTAR_SWITCH_SCART:
			case KeyEventExtra.KEYCODE_MSTAR_SWITCH_DVD:
			case KeyEventExtra.KEYCODE_MSTAR_SWITCH_HDMI:
			case KeyEventExtra.KEYCODE_MSTAR_SWITCH_HDMI1:
			case KeyEventExtra.KEYCODE_MSTAR_SWITCH_HDMI2:
			case KeyEventExtra.KEYCODE_MSTAR_SWITCH_HDMI3:
			case KeyEventExtra.KEYCODE_MSTAR_SWITCH_HDMI4:
			case KeyEventExtra.KEYCODE_MSTAR_3D:
			case KeyEventExtra.KEYCODE_MSTAR_CLONED:
			case KeyEventExtra.KEYCODE_MSTAR_FREQUENCY:
			case KeyEventExtra.KEYCODE_MSTAR_NET:
			case KeyEventExtra.KEYCODE_MSTAR_MONO:
			case KeyEventExtra.KEYCODE_MSTAR_DATA_DISPLAY:
			case KeyEventExtra.KEYCODE_MSTAR_ADC:
			case KeyEventExtra.KEYCODE_MSTAR_NICAM:
			case KeyEventExtra.KEYCODE_MSTAR_VERSION:
			case KeyEventExtra.KEYCODE_MSTAR_LVDS_SSC:
			case KeyEventExtra.KEYCODE_MSTAR_OSD_LANGUAGE:
			case KeyEventExtra.KEYCODE_MSTAR_UPGRADE:
			case KeyEventExtra.KEYCODE_MSTAR_HOME:
			case KeyEventExtra.KEYCODE_MSTAR_APP:
			case KeyEventExtra.KEYCODE_MSTAR_WHITE_BALANCE:
			case KeyEventExtra.KEYCODE_MSTAR_WIFI:
			case KeyEventExtra.KEYCODE_MSTAR_CEC:
			case KeyEventExtra.KEYCODE_MSTAR_F1:
			case KeyEventExtra.KEYCODE_MSTAR_F2:
			case KeyEventExtra.KEYCODE_MSTAR_F3:
			case KeyEventExtra.KEYCODE_MSTAR_F4:{
				Intent intent = new Intent();
				intent.setAction("com.eostek.MKeyEventService");
				intent.setPackage("com.eostek.mkeyeventservice");
				intent.putExtra("keyCode", keyCode);
                mContext.startService(intent);
                return true;
			}
			//ChangHong IR End
		}


        return false;
    }

    boolean onKeyUp(int keyCode, KeyEvent event) {
        if (DEBUG) {
            Log.d(TAG, "up " + keyCode);
        }
        final KeyEvent.DispatcherState dispatcher = mView.getKeyDispatcherState();
        if (dispatcher != null) {
            dispatcher.handleUpEvent(event);
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_MUTE: {
                if (!event.isCanceled()) {
                    MediaSessionLegacyHelper.getHelper(mContext).sendVolumeKeyEvent(event, false);
                }
                return true;
            }

            case KeyEvent.KEYCODE_HEADSETHOOK:
            case KeyEvent.KEYCODE_MUTE:
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_STOP:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_RECORD:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_AUDIO_TRACK: {
                handleMediaKeyEvent(event);
                return true;
            }

            case KeyEvent.KEYCODE_CAMERA: {
                if (getKeyguardManager().inKeyguardRestrictedInputMode()) {
                    break;
                }
                if (event.isTracking() && !event.isCanceled()) {
                    // Add short press behavior here if desired
                }
                return true;
            }

            case KeyEvent.KEYCODE_CALL: {
                if (getKeyguardManager().inKeyguardRestrictedInputMode()) {
                    break;
                }
                if (event.isTracking() && !event.isCanceled()) {
                    if (isUserSetupComplete()) {
                        startCallActivity();
                    } else {
                        Log.i(TAG, "Not starting call activity because user "
                                + "setup is in progress.");
                    }
                }
                return true;
            }
			
		   // EosTek Patch Begin 
		   case KeyEvent.KEYCODE_PROG_GREEN: {
                if (getKeyguardManager().inKeyguardRestrictedInputMode()) {
                    break;
                }
                if (event.isTracking() && !event.isCanceled()) {
                   Intent intent = new Intent(Intent.ACTION_MAIN);
				   intent.setClassName("com.eostek.scifly.devicemanager", "com.eostek.scifly.devicemanager.DeviceManagerActivity");
                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   mContext.startActivity(intent);
                }
                return true;
            }
			/*
			case KeyEvent.KEYCODE_TV_INPUT: {
                Intent intent = new Intent(MIntent.ACTION_TV_INPUT_BUTTON);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                    Log.i(TAG, "Send MIntent.ACTION_TV_INPUT_BUTTON.");
                }
                return true;
            }
			*/
		   // EosTek Patch End 
		   
        }
        return false;
    }

    void startCallActivity() {
        sendCloseSystemWindows();
        Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "No activity found for android.intent.action.CALL_BUTTON.");
        }
    }

    SearchManager getSearchManager() {
        if (mSearchManager == null) {
            mSearchManager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);
        }
        return mSearchManager;
    }

    TelephonyManager getTelephonyManager() {
        if (mTelephonyManager == null) {
            mTelephonyManager = (TelephonyManager)mContext.getSystemService(
                    Context.TELEPHONY_SERVICE);
        }
        return mTelephonyManager;
    }

    KeyguardManager getKeyguardManager() {
        if (mKeyguardManager == null) {
            mKeyguardManager = (KeyguardManager)mContext.getSystemService(Context.KEYGUARD_SERVICE);
        }
        return mKeyguardManager;
    }

    AudioManager getAudioManager() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        }
        return mAudioManager;
    }

    void sendCloseSystemWindows() {
        PhoneWindowManager.sendCloseSystemWindows(mContext, null);
    }

    private void handleMediaKeyEvent(KeyEvent keyEvent) {
        MediaSessionLegacyHelper.getHelper(mContext).sendMediaButtonEvent(keyEvent, false);
    }
    private boolean isUserSetupComplete() {
        return Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.USER_SETUP_COMPLETE, 0) != 0;
    }

    // MStar Android Patch Begin
    private String getCurRunningActivityPackageName() {
        ActivityManager mgr = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        RunningTaskInfo info = null;
        try {
            info = mgr.getRunningTasks(1).get(0);//launcher has the permission
        } catch (Exception e) {
            Log.d("PhoneFallbackEventHandler","have no permission to getCurRunningActivityPackageName");
        }

        if (info == null)
            return "";
        else
            return info.topActivity.getPackageName();
    }
    // MStar Android Patch End
	
	//EosTek Patch Begin
    //0 not support 3D , 1 Support 3D
    private String readSysIni() {
        String line = null;
        String panel_version = "0";
        try {
            FileInputStream mStream = new FileInputStream(new File("config/sys.ini"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(mStream));
            while ((line = reader.readLine()) != null) {
                // System.out.println(line);
                if (line.startsWith("project_panel_3D")) {
                    int position = line.indexOf(";");
                    String[] tmpStrings = line.subSequence(0, position).toString().split("=");
                    panel_version = tmpStrings[1].trim();
                }
            }
            reader.close();
            mStream.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
       Log.w(TAG,"readSysIni:"+panel_version );
        return panel_version;
    }
   //EosTek Patch End 

}

