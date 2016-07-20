
package com.android.settings.system.business;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;

import com.android.settings.system.SystemSettingsActivity;
import com.android.settings.userbackup.BackUpData;
import com.android.settings.util.Utils;

public class InputMethodSettingsLogic {
    private static final String TAG = "InputMethodSettingsLogic";

    private SystemSettingsActivity mActivity;

    private String mLastInputMethodId;

    private int mCurrentInputMethodPosition;

    private ArrayList<String> mInputMethodLabelList = new ArrayList<String>();

    public InputMethodSettingsLogic(SystemSettingsActivity activity) {
        this.mActivity = activity;
        Log.i(TAG, "mLastInputMethodId=" + mLastInputMethodId);
        initmCurrentInputMethodPosition();
        initmInputMethodLabelList();
    }

    public ArrayList<String> getInputMethodLabelList(){
        return mInputMethodLabelList;
    }

    /**
     * do the init for mCurrentInputMethodPosition
     */
    public void initmCurrentInputMethodPosition() {
        List<InputMethodInfo> inputMethodInfos = Utils.getSystemInputMethodInfoList(mActivity);
        String defaultInputMethodId = Utils.getDefaultInputMethodId(mActivity);
        int size = inputMethodInfos.size();
        for (int i = 0; i < size; i++) {
            if (inputMethodInfos.get(i).getId().equals(defaultInputMethodId)) {
                mCurrentInputMethodPosition = i;
            }
        }
    }
    /**
     * do the init for mInputMethodLabelList
     */
    public void initmInputMethodLabelList(){
        PackageManager pm = mActivity.getPackageManager();
        List<InputMethodInfo> systemInputMethodlist = Utils.getSystemInputMethodInfoList(mActivity);
        int size = systemInputMethodlist == null ? 0 : systemInputMethodlist.size();
        for (int i = 0; i < size; i++) {
            InputMethodInfo inputMethod = systemInputMethodlist.get(i);
            mInputMethodLabelList.add(inputMethod.loadLabel(pm).toString());
            Log.i(TAG, "mInputMethodLabelList.get(" + i + ")" + ".getId()="
                    + systemInputMethodlist.get(i).getId());
        }
    }
    /**
     * @return the position in the system input method list
     */
    public int getCurrentInputMethodId() {

        return mCurrentInputMethodPosition;
    }

    public void switchInputMethod(String inputMethod) {
        Log.i(TAG, "inputMethod : " + inputMethod);
        for (int i = 0; i < mInputMethodLabelList.size(); i++) {
            if (mInputMethodLabelList.get(i).equals(inputMethod)) {
                Log.i(TAG, "switchInputMethod to " + i);
                mCurrentInputMethodPosition = i;
                mLastInputMethodId = Utils.getDefaultInputMethodId(mActivity);
                List<InputMethodInfo> systemInputMethodlist = Utils.getSystemInputMethodInfoList(mActivity);
                Settings.Secure.putString(mActivity.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD,
                        systemInputMethodlist.get(i).getId());
                BackUpData.backupData("inputmethod", "inputmethod_id", systemInputMethodlist.get(i).getId());
                break;
            }
        }
    }
}
