
package com.mstar.tv.menu.setting;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.EnumProgramCountType;
import com.mstar.android.tvapi.common.vo.EnumProgramInfoType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfoQueryCriteria;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.tv.MenuConstants;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.setting.atvfinetuning.FineTuningDialog;

/*
 * @projectName： EOSTVMenu
 * @moduleName： ChannelManagerFragment.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time  2013-12-18
 * @Copyright © 2013 EOSTEK, Inc.
 */
@SuppressLint("ValidFragment")
public class ChannelManagerFragment extends PublicFragement {

    private final static String TAG = ChannelManagerFragment.class.getName();

    private EosSettingItem channelEidt_item = null;

    private EosSettingItem dtvAutoTuning_item = null;

    private EosSettingItem atvAutoTuning_item = null;

    private EosSettingItem dtvManualTuning_item = null;

    private EosSettingItem fineTuning_item = null;

    private String[] channelManager_title;

    private EnumInputSource curSource = EnumInputSource.E_INPUT_SOURCE_NONE;

    public static final String DIALOGID_AUTOTUNING = "DTV_AUTOTUNING";

    public static final String DIALOGID_MANUALTUNING = "DTV_MANUALTUNING";

    @Override
    protected void initItems() {
        setTag("channel");
        mItems = new ArrayList<EosSettingItem>();
        curSource = TvCommonManager.getInstance().getCurrentInputSource();
        if (curSource == EnumInputSource.E_INPUT_SOURCE_STORAGE) {
            curSource = EnumInputSource.values()[queryCurInputSrc()];
            Log.v(TAG, "Source is storage,queryCurInputSrc ,curSource = " + curSource);
        }
        if (curSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
            channelManager_title = getActivity().getResources().getStringArray(R.array.channelmanager_dtv_vals);
            channelEidt_item = new EosSettingItem(this, channelManager_title[0], MenuConstants.ITEMTYPE_BUTTON, true);
            mItems.add(channelEidt_item);
            dtvAutoTuning_item = new EosSettingItem(this, channelManager_title[1], MenuConstants.ITEMTYPE_BUTTON, true);
            mItems.add(dtvAutoTuning_item);
            dtvManualTuning_item = new EosSettingItem(this, channelManager_title[2], MenuConstants.ITEMTYPE_BUTTON,
                    true);
            mItems.add(dtvManualTuning_item);
        } else if (curSource == EnumInputSource.E_INPUT_SOURCE_ATV) {
            channelManager_title = getActivity().getResources().getStringArray(R.array.channelmanager_atv_vals);
            atvAutoTuning_item = new EosSettingItem(this, channelManager_title[0], MenuConstants.ITEMTYPE_BUTTON, true);
            mItems.add(atvAutoTuning_item);
            fineTuning_item = new EosSettingItem(this, channelManager_title[1], MenuConstants.ITEMTYPE_BUTTON, true);
            mItems.add(fineTuning_item);
        }

        mAdapter.setHasShowValue(true);
    }

    @Override
    void callBack(int resultValue, int position) {
    }

    @Override
    void callBack(Boolean resultValue, int position) {
    }

    @Override
    void callBack(int position) {
        getActivity().findViewById(R.id.main).setVisibility(View.GONE);
        switch (position) {
            case 0:
                if (curSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                    Intent intent = new Intent("com.eostek.tv.player.channellistedit");
                    startActivity(intent);
                    getActivity().finish();
                } else if (curSource == EnumInputSource.E_INPUT_SOURCE_ATV) {
                    startATVAutoTunning();
                }
                break;
            case 1:
                if (curSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                    if (getLockChannelCount() > 0) {
                        Toast.makeText(getActivity(), R.string.channellocktip, Toast.LENGTH_LONG).show();
                        new PasswordCheckDialog(getActivity(), DIALOGID_AUTOTUNING).show();
                    } else {
                        startDTVAutoTunning();
                    }
                } else if (curSource == EnumInputSource.E_INPUT_SOURCE_ATV) {
                    FineTuningDialog fineTuningDialog = new FineTuningDialog(getActivity());
                    fineTuningDialog.show();
                }
                break;
            case 2:
                if (curSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                    if (getLockChannelCount() > 0) {
                        Toast.makeText(getActivity(), R.string.channellocktip, Toast.LENGTH_LONG).show();
                        new PasswordCheckDialog(getActivity(), DIALOGID_MANUALTUNING).show();
                    } else {
                        DTVManualTuningDialog dtvdialog = new DTVManualTuningDialog(getActivity());
                        dtvdialog.show();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void initDate() {
    }

    @Override
    boolean doKeyDownOrUp(int keyCode, KeyEvent event) {
        return false;
    }

    private void startDTVAutoTunning() {
        Intent intent = new Intent(getActivity(), AutoTuningActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    private void startATVAutoTunning() {
        Intent intent = new Intent(getActivity(), ATVAutoTuningActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public int getLockChannelCount() {
        int count = 0;
        int indexBase = 0;
        int channelconunt = 0;
        int dataCount = TvChannelManager.getInstance().getProgramCount(EnumProgramCountType.E_COUNT_DTV_DATA);
        channelconunt = TvChannelManager.getInstance().getProgramCount(EnumProgramCountType.E_COUNT_DTV) - dataCount;
        for (int i = indexBase; i < channelconunt; i++) {
            ProgramInfo pi = null;
            pi = getProgramInfoByIndex(i);
            if (pi.isLock) {
                count++;
            }
        }
        return count;
    }

    /**
     * get program information by index in database.
     * 
     * @param programIndex
     * @return
     */
    public ProgramInfo getProgramInfoByIndex(int programIndex) {
        ProgramInfoQueryCriteria qc = new ProgramInfoQueryCriteria();
        qc.queryIndex = programIndex;
        ProgramInfo pi = TvChannelManager.getInstance().getProgramInfo(qc, EnumProgramInfoType.E_INFO_DATABASE_INDEX);
        return pi;
    }

    /**
     * query the current input source
     * 
     * @return InputSourceType
     */
    public int queryCurInputSrc() {
        int value = 0;
        Cursor cursor = getActivity().getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
        }
        cursor.close();
        return value;
    }
}
