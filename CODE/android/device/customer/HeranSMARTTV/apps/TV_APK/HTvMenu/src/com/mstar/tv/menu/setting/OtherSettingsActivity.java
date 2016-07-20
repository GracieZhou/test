//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2012 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>

package com.mstar.tv.menu.setting;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvTimerManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.CecSetting;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.dtv.common.DtvManager;
import com.mstar.android.tvapi.dtv.vo.EnumCardState;
import com.mstar.tv.MenuConstants;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.ui.FocusScrollListView;

public class OtherSettingsActivity extends BaseActivity {

    private FocusScrollListView otherSettingsLst;

    private List<SettingItem> items;

    private String[] otherSettingstitle;

    private int selectItemIndex = 0;

    private SettingAdapter adapter;

    private Animation menu_AnimIn_second;

    private Animation menu_AnimOut_second;

    private CecSetting cecSetting;

    private boolean isSummerTimeon;

    private enum INPUT_TYPE {
        TUNER, OTHERS
    }

    private INPUT_TYPE status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publiclist);
        ((TextView) findViewById(R.id.setting_title_txt)).setText(getIntent().getStringExtra(
                MenuConstants.TITLE_KEY));

        items = new ArrayList<SettingItem>();
        otherSettingsLst = (FocusScrollListView) findViewById(R.id.context_lst);
        otherSettingsLst.setFocusBitmap(R.drawable.menu_setting_focus);
        EnumInputSource curInputSource = TvCommonManager.getInstance().getCurrentInputSource();
        if (curInputSource == EnumInputSource.E_INPUT_SOURCE_ATV
                || curInputSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
            status = INPUT_TYPE.TUNER;
            otherSettingstitle = getResources().getStringArray(R.array.setting_other_tuner);

        } else {
            status = INPUT_TYPE.OTHERS;
            otherSettingstitle = getResources().getStringArray(R.array.setting_other);

        }

        try {
            initData();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        setListener();

        adapter = new SettingAdapter(this, items);
        adapter.setHasShowValue(true);
        otherSettingsLst.setAdapter(adapter);

        menu_AnimIn_second = AnimationUtils.loadAnimation(this, R.anim.menu_anim_in_second);
        menu_AnimIn_second.setFillAfter(true);
        menu_AnimOut_second = AnimationUtils.loadAnimation(this, R.anim.menu_anim_out_second);
        menu_AnimOut_second.setFillAfter(true);
    }

    @Override
    protected void onResume() {
        findViewById(R.id.setting_layout).startAnimation(menu_AnimIn_second);
        super.onResume();
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
    }

    private void initData() throws TvCommonException {
        try {
            cecSetting = TvManager.getInstance().getCecManager().getCecConfiguration();
            isSummerTimeon = TvManager.getInstance().getTimerManager().getDaylightSavingState();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String arcMode = getResources().getString(R.string.arc_mode);
        String summerTime = getResources().getString(R.string.summer_time);

        for (int i = 0; i < otherSettingstitle.length; i++) {
            if (arcMode.equals(otherSettingstitle[i])) {
                items.add(new SettingItem(this, arcMode, getResources().getStringArray(
                        R.array.turnon_off), (cecSetting.arcStatus == 1) ? true : false,
                        MenuConstants.ITEMTYPE_BOOL, true));
            } else if (summerTime.equals(otherSettingstitle[i])) {
                items.add(new SettingItem(this, summerTime, getResources().getStringArray(
                        R.array.turnon_off), isSummerTimeon, MenuConstants.ITEMTYPE_BOOL, true));
            } else {
                items.add(new SettingItem(this, otherSettingstitle[i],
                        MenuConstants.ITEMTYPE_BUTTON, true));
                if (status == INPUT_TYPE.OTHERS
                        && getResources().getString(R.string.changepassword_title).equals(
                                otherSettingstitle[i]))
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_MENU:
                findViewById(R.id.setting_layout).startAnimation(menu_AnimOut_second);
                handler.sendEmptyMessageDelayed(MenuConstants.DELAYFINIFH, 200);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (otherSettingsLst.getSelectedItemPosition() == 0) {
                    otherSettingsLst.setSelection(adapter.getCount() - 1);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (otherSettingsLst.getSelectedItemPosition() == adapter.getCount() - 1) {
                    otherSettingsLst.setSelection(0);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                selectItemIndex = otherSettingsLst.getSelectedItemPosition();
                if (adapter.getHasShowValue()) {
                    return items.get(selectItemIndex).onKeyDown(keyCode, event);
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setListener() {
        otherSettingsLst.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                selectItemIndex = position;
                items.get(selectItemIndex).itemClicked();
            }

        });
    }

    @Override
    public void callBack(int resultVaule) {
    }

    @Override
    public void callBack(Boolean resultVaule) {
        if (selectItemIndex == 1) {
            if (resultVaule) {
                cecSetting.arcStatus = 1;
                cecSetting.audioModeStatus = 1;
                ((TextView) otherSettingsLst.getSelectedView().getTag()).setText(items.get(
                        selectItemIndex).getValues()[1]);
            } else {
                cecSetting.arcStatus = 0;
                cecSetting.audioModeStatus = 0;
                ((TextView) otherSettingsLst.getSelectedView().getTag()).setText(items.get(
                        selectItemIndex).getValues()[0]);
            }

            TvManager.getInstance().getCecManager().setCecConfiguration(cecSetting);
        } else if (selectItemIndex == 3) {
            if (resultVaule) {
                ((TextView) otherSettingsLst.getSelectedView().getTag()).setText(items.get(
                        selectItemIndex).getValues()[1]);
            } else {
                ((TextView) otherSettingsLst.getSelectedView().getTag()).setText(items.get(
                        selectItemIndex).getValues()[0]);
            }
            try {
                TvManager.getInstance().getTimerManager().setDaylightSavingState(resultVaule);
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void callBack() {
        switch (selectItemIndex) {
            case 0:
                gotoTime();
                break;
            case 2:
                if (TvManager.getInstance().getParentalcontrolManager().isSystemLock()) {
                    showPasswordDialog();
                } else {
                }
                break;
            case 4:
                gotoParentControl();
                break;
            case 5:
                if (status == INPUT_TYPE.TUNER) {
                    try {
                        if (DtvManager.getCiManager() != null) {
                            EnumCardState status = EnumCardState.E_NO;
                            try {
                                status = DtvManager.getCiManager().getCardState();
                            } catch (TvCommonException e) {
                                e.printStackTrace();
                            }

                            if (status == EnumCardState.E_NO) {
                                Toast toast = Toast.makeText(
                                        getApplicationContext(),
                                        getApplicationContext().getResources().getString(
                                                R.string.str_cimmi_hint_ci_no_module), 3);
                                toast.show();
                            } else if (status == EnumCardState.E_INITIALIZING) {
                                Toast toast = Toast.makeText(
                                        getApplicationContext(),
                                        getApplicationContext().getResources().getString(
                                                R.string.str_cimmi_hint_ci_try_again), 3);
                                toast.show();
                            } else if (status == EnumCardState.E_READY) {
                                try {
                                    DtvManager.getCiManager().enterMenu();
                                } catch (TvCommonException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }
                }
                break;

            default:
                break;
        }

    }

    private void showPasswordDialog() {
        final Dialog passwordDialog = new Dialog(this, R.style.dialog);
        passwordDialog.setContentView(R.layout.passwordcheck);
        passwordDialog.setOnKeyListener(new OnKeyListener() {

            private void setTextIcon(int index) {
                int id = R.id.textViewPass1;
                switch (index) {
                    case 2:
                        id = R.id.textViewPass2;
                        break;
                    case 3:
                        id = R.id.textViewPass3;
                        break;
                    case 4:
                        id = R.id.textViewPass4;
                        break;
                    default:
                        break;
                }
                ((TextView) passwordDialog.findViewById(id)).setText("*");
            }

            private void clearTextIcon() {
                ((TextView) passwordDialog.findViewById(R.id.textViewPass1)).setText("");
                ((TextView) passwordDialog.findViewById(R.id.textViewPass2)).setText("");
                ((TextView) passwordDialog.findViewById(R.id.textViewPass3)).setText("");
                ((TextView) passwordDialog.findViewById(R.id.textViewPass4)).setText("");
            }

            @Override
            public boolean onKey(DialogInterface arg0, int keycode, KeyEvent keyEvent) {
                switch (keycode) {
                    case KeyEvent.KEYCODE_BACK:
                        passwordDialog.dismiss();
                        break;
                    case KeyEvent.KEYCODE_0:
                    case KeyEvent.KEYCODE_1:
                    case KeyEvent.KEYCODE_2:
                    case KeyEvent.KEYCODE_3:
                    case KeyEvent.KEYCODE_4:
                    case KeyEvent.KEYCODE_5:
                    case KeyEvent.KEYCODE_6:
                    case KeyEvent.KEYCODE_7:
                    case KeyEvent.KEYCODE_8:
                    case KeyEvent.KEYCODE_9:
                        EditText password_Etxt = (EditText) passwordDialog
                                .findViewById(R.id.passwordcheck);
                        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                            String curInputStr = password_Etxt.getText().toString();
                            setTextIcon(curInputStr.length());
                            if (curInputStr.length() == 4) {
                                int curPassword = TvManager.getInstance()
                                        .getParentalcontrolManager().GetParentalPassword();
                                if (Integer.parseInt(curInputStr) == curPassword) {
                                    findViewById(R.id.setting_layout).startAnimation(
                                            menu_AnimOut_second);
                                    if (selectItemIndex == 2) {
                                    }
                                    passwordDialog.dismiss();
                                } else {
                                    Toast.makeText(OtherSettingsActivity.this, R.string.errortip,
                                            Toast.LENGTH_LONG).show();
                                    password_Etxt.setText("");
                                    clearTextIcon();
                                }
                            }
                        }
                        break;
                }

                return false;
            }
        });
        passwordDialog.show();
    }

    private void gotoTime() {
        findViewById(R.id.setting_layout).startAnimation(menu_AnimOut_second);
        Intent intent = new Intent(OtherSettingsActivity.this, TimeSettingActivity.class);
        intent.putExtra(MenuConstants.TITLE_KEY, otherSettingstitle[0]);
        startActivity(intent);
    }

    private void gotoParentControl() {
        findViewById(R.id.setting_layout).startAnimation(menu_AnimOut_second);
        Intent intent = new Intent(OtherSettingsActivity.this, ParentControlActivity.class);
        intent.putExtra(MenuConstants.TITLE_KEY, otherSettingstitle[4]);
        startActivity(intent);
    }
}
