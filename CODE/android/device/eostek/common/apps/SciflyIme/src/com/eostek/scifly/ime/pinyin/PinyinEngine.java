
package com.eostek.scifly.ime.pinyin;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;

import com.eostek.scifly.ime.InputEngineInterface;
import com.eostek.scifly.ime.util.Constans;

public class PinyinEngine implements InputEngineInterface {

    private static final String TAG = "PinyinEngine";

    private Context mContext;

    private PinyinDecoderServiceConnection mPinyinDecoderServiceConnection;

    private DecodingInfo mDecInfo = new DecodingInfo();

    public PinyinDecoderServiceConnection getmPinyinDecoderServiceConnection() {
        return mPinyinDecoderServiceConnection;
    }

    public DecodingInfo getDecInfo() {
        return mDecInfo;
    }

    public PinyinEngine(Context mContext) {
        this.mContext = mContext;
    }

    public void startEngine() {
        startPinyinDecoderService();
    }

    private boolean startPinyinDecoderService() {
        if (null == mDecInfo.mIPinyinDecoderService) {
            Intent serviceIntent = new Intent();
            serviceIntent.setClass(mContext, PinyinDecoderService.class);

            if (null == mPinyinDecoderServiceConnection) {
                mPinyinDecoderServiceConnection = new PinyinDecoderServiceConnection();
            }

            // Bind service
            if (mContext.bindService(serviceIntent, mPinyinDecoderServiceConnection, Context.BIND_AUTO_CREATE)) {
            	Constans.print(TAG, "bind result:" + true);
                return true;
            } else {
            	Constans.print(TAG, "bind result:" + false);

                return false;
            }
        }
        return true;
    }

    public void addSplString(String spl) {
        if (TextUtils.isEmpty(spl)) {
            return;
        }

        mDecInfo.reset();
        DecodingInfo.mImeState = ImeState.STATE_INPUT;
        for (int i = 0; i < spl.length(); i++) {
            mDecInfo.addSplChar(spl.charAt(i), false);
        }
    }

    @Override
    public List<String> getCandidateList(String spl) {
        
        if (mDecInfo.mImeState == ImeState.STATE_INPUT) {
            addSplString(spl);
        }
        
        mDecInfo.chooseDecodingCandidate(-1);

        String compose = mDecInfo.getComposingStrForDisplay();

//        Constans.print(TAG, compose + " : " + mDecInfo.mCandidatesList.toString());

        return mDecInfo.mCandidatesList;

    }

    private class PinyinDecoderServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder service) {
            // Constans.printE(TAG, "ComponentName" + name + " , service=" + service);
            mDecInfo.mIPinyinDecoderService = IPinyinDecoderService.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName name) {
        }
    }

}
