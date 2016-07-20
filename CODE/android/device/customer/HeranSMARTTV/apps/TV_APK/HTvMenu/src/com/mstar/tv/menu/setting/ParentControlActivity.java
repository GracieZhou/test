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
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mstar.android.tvapi.common.TvManager;
import com.mstar.tv.MenuConstants;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.ui.FocusScrollListView;

public class ParentControlActivity extends BaseActivity {
    private FocusScrollListView parentControlLst;

    private List<SettingItem> items;

    private int selectItemIndex = 0;

    private Animation menu_AnimIn_second;

    private Animation menu_AnimOut_three;

    private Animation menu_AnimOut_second;

    private SettingAdapter adapter;

    private String[] titles = null;

    private Animation menu_AnimOut_forstartActivity;

    private Animation menu_AnimOut_forresumeActivity;

    private static boolean HASDOSTART = false;

    private boolean focus = true;

    private boolean flag = false;

    private Context mcontext;

    private boolean isOpenLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publiclist);
        ((TextView) findViewById(R.id.setting_title_txt)).setText(getIntent().getStringExtra(
                MenuConstants.TITLE_KEY));

        items = new ArrayList<SettingItem>();
        parentControlLst = (FocusScrollListView) findViewById(R.id.context_lst);
        parentControlLst.setFocusBitmap(R.drawable.menu_setting_focus);
        titles = getResources().getStringArray(R.array.setting_parentcontrol_setting_titles);

        initItems();
        setListener();

        adapter = new SettingAdapter(this, items);
        adapter.setHasShowValue(true);
        parentControlLst.setAdapter(adapter);

        menu_AnimIn_second = AnimationUtils.loadAnimation(this, R.anim.menu_anim_in_three);
        menu_AnimIn_second.setFillAfter(true);

        menu_AnimOut_three = AnimationUtils.loadAnimation(this, R.anim.menu_anim_out_three);
        menu_AnimOut_three.setFillAfter(true);

        menu_AnimOut_forstartActivity = AnimationUtils.loadAnimation(this,
                R.anim.menu_anim_out_second);
        menu_AnimOut_forstartActivity.setFillAfter(true);

        menu_AnimOut_forresumeActivity = AnimationUtils.loadAnimation(this,
                R.anim.menu_anim_in_second);
        menu_AnimOut_forresumeActivity.setFillAfter(true);

        menu_AnimOut_second = AnimationUtils.loadAnimation(this, R.anim.menu_anim_out_second);
        menu_AnimOut_second.setFillAfter(true);

        HASDOSTART = false;
        mcontext = this;
    }

    @Override
    protected void onResume() {
        this.setVisible(true);
        if (HASDOSTART) {
            findViewById(R.id.setting_layout).startAnimation(menu_AnimOut_forresumeActivity);
            HASDOSTART = false;
        } else {
            findViewById(R.id.setting_layout).startAnimation(menu_AnimIn_second);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.setVisible(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initItems() {
        isOpenLock = TvManager.getInstance().getParentalcontrolManager().isSystemLock();
        String parentLock = getResources().getString(R.string.ParentLock);
        for (int i = 0; i < titles.length; i++) {
            if (parentLock.equals(titles[i])) {
                items.add(new SettingItem(this, parentLock, getResources().getStringArray(
                        R.array.turnon_off), isOpenLock, MenuConstants.ITEMTYPE_BOOL, true));
            } else {
                items.add(new SettingItem(this, titles[i], MenuConstants.ITEMTYPE_BUTTON, true));
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            selectItemIndex = parentControlLst.getSelectedItemPosition();
            return items.get(selectItemIndex).onKeyDown(keyCode, event);
        } else if ((keyCode == KeyEvent.KEYCODE_BACK) || (keyCode == KeyEvent.KEYCODE_MENU)) {
            findViewById(R.id.setting_layout).startAnimation(menu_AnimOut_three);
            handler.sendEmptyMessageDelayed(MenuConstants.DELAYFINIFH, 200);
            return true;
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (focus) {
                    if (parentControlLst.getSelectedItemPosition() == 0) {
                        parentControlLst.setSelection(adapter.getCount() - 1);
                    }
                } else {
                    if (parentControlLst.getSelectedItemPosition() == 0) {
                        parentControlLst.setSelection(adapter.getCount() - 1);
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (parentControlLst.getSelectedItemPosition() == adapter.getCount() - 1) {
                    parentControlLst.setSelection(0);
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setListener() {
        parentControlLst.setOnItemClickListener(new OnItemClickListener() {

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
        if (selectItemIndex == 0) {
            if (flag) {
                if (resultVaule) {
                    ((TextView) parentControlLst.getSelectedView().getTag()).setText(items.get(
                            selectItemIndex).getValues()[1]);
                } else {
                    ((TextView) parentControlLst.getSelectedView().getTag()).setText(items.get(
                            selectItemIndex).getValues()[0]);
                }

                TvManager.getInstance().getParentalcontrolManager().setSystemLock(resultVaule);
            } else {
                showPasswordDialog();
            }

        }

    }

    @Override
    public void callBack() {
        if (!items.get(selectItemIndex).getFocusable()) {
            return;
        }
        switch (selectItemIndex) {
            case 1:
                creatChangePasswordDialog();
                break;
            case 2:
                ParentalGuidanceDialog settingRateDialog = new ParentalGuidanceDialog(mcontext);
                settingRateDialog.show();
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
                                    flag = true;
                                    passwordDialog.dismiss();
                                } else {
                                    Toast.makeText(mcontext, R.string.errortip, Toast.LENGTH_LONG)
                                            .show();
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

    private void creatChangePasswordDialog() {
        final Dialog passwordChangeDialog = new Dialog(this, R.style.dialog);
        passwordChangeDialog.setContentView(R.layout.passwordchange);

        final LinearLayout oldPasswordLayout = (LinearLayout) passwordChangeDialog
                .findViewById(R.id.old_password_layout);
        final LinearLayout newPasswordLayout = (LinearLayout) passwordChangeDialog
                .findViewById(R.id.new_password_layout);
        final LinearLayout confirmPasswordLayout = (LinearLayout) passwordChangeDialog
                .findViewById(R.id.confirm_password_layout);
        final EditText oldPasswordEdt = (EditText) passwordChangeDialog
                .findViewById(R.id.old_password);
        final EditText newPasswordEdt = (EditText) passwordChangeDialog
                .findViewById(R.id.new_password);
        final EditText confirmPasswordEdt = (EditText) passwordChangeDialog
                .findViewById(R.id.confirm_password);

        oldPasswordLayout.setBackgroundResource(R.drawable.menu_setting_focus);

        Button sureBtn = (Button) passwordChangeDialog.findViewById(R.id.sure_reset_btn);
        Button cancleBtn = (Button) passwordChangeDialog.findViewById(R.id.cancle_reset_btn);
        Button cleanBtn = (Button) passwordChangeDialog.findViewById(R.id.clean_reset_btn);

        sureBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int curPassword = TvManager.getInstance().getParentalcontrolManager()
                        .GetParentalPassword();
                Log.v("xpf", "orgin pass==" + curPassword);
                String curInputStr = oldPasswordEdt.getText().toString();
                String newInputStr = newPasswordEdt.getText().toString();
                String confirmInputStr = confirmPasswordEdt.getText().toString();
                if (curInputStr == null || curInputStr.length() < 4) {
                    Toast.makeText(mcontext, R.string.oldpasswordnullorlessthansix,
                            Toast.LENGTH_LONG).show();
                } else if (newInputStr == null || newInputStr.length() < 4) {
                    Toast.makeText(mcontext, R.string.newpasswordnullorlessthansix,
                            Toast.LENGTH_LONG).show();
                } else if (confirmInputStr == null || confirmInputStr.length() < 4) {
                    Toast.makeText(mcontext, R.string.confirmpasswordnullorlessthansix,
                            Toast.LENGTH_LONG).show();
                } else if (!(Integer.parseInt(curInputStr) == curPassword)) {
                    Toast.makeText(mcontext, R.string.originalpassworderr, Toast.LENGTH_LONG)
                            .show();
                } else if (!(newInputStr.equals(confirmInputStr))) {
                    Toast.makeText(mcontext, R.string.newconfirmpassworderr, Toast.LENGTH_LONG)
                            .show();
                } else if ((Integer.parseInt(curInputStr) == curPassword)
                        && newInputStr.equals(confirmInputStr)) {
                    TvManager.getInstance().getParentalcontrolManager()
                            .setParentalPassword(Integer.parseInt(newInputStr));
                    Log.v("xpf", "new pass==" + Integer.parseInt(newInputStr));
                    Toast.makeText(mcontext, R.string.resetsuccess, Toast.LENGTH_LONG).show();
                    passwordChangeDialog.dismiss();

                }
            }
        });
        cancleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordChangeDialog.dismiss();
            }
        });
        cleanBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                oldPasswordEdt.setText("");
                newPasswordEdt.setText("");
                confirmPasswordEdt.setText("");
            }
        });
        final int old_pass_text[] = {
                R.id.old_textViewPass1, R.id.old_textViewPass2, R.id.old_textViewPass3,
                R.id.old_textViewPass4
        };
        final int new_pass_text[] = {
                R.id.new_textViewPass1, R.id.new_textViewPass2, R.id.new_textViewPass3,
                R.id.new_textViewPass4
        };
        final int confirm_pass_text[] = {
                R.id.confirm_textViewPass1, R.id.confirm_textViewPass2, R.id.confirm_textViewPass3,
                R.id.confirm_textViewPass4
        };
        oldPasswordEdt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                int count = arg0.length();
                if (count < 0 || count > 4)
                    return;
                for (int i = 0; i < count; i++) {
                    int id = old_pass_text[i];
                    ((TextView) passwordChangeDialog.findViewById(id)).setText("*");
                }
                for (int i = count; i < 4; i++) {
                    int id = old_pass_text[i];
                    ((TextView) passwordChangeDialog.findViewById(id)).setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
        newPasswordEdt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                int count = arg0.length();
                if (count < 0 || count > 4)
                    return;
                for (int i = 0; i < count; i++) {
                    int id = new_pass_text[i];
                    ((TextView) passwordChangeDialog.findViewById(id)).setText("*");
                }
                for (int i = count; i < 4; i++) {
                    int id = new_pass_text[i];
                    ((TextView) passwordChangeDialog.findViewById(id)).setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
        confirmPasswordEdt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                int count = arg0.length();
                if (count < 0 || count > 4)
                    return;
                for (int i = 0; i < count; i++) {
                    int id = confirm_pass_text[i];
                    ((TextView) passwordChangeDialog.findViewById(id)).setText("*");
                }
                for (int i = count; i < 4; i++) {
                    int id = confirm_pass_text[i];
                    ((TextView) passwordChangeDialog.findViewById(id)).setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
        passwordChangeDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keycode, KeyEvent keyEvent) {
                switch (keycode) {
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                    case KeyEvent.KEYCODE_ENTER:
                        if (oldPasswordEdt.hasFocus() || newPasswordEdt.hasFocus()
                                || confirmPasswordEdt.hasFocus()) {
                            return true;
                        }
                        break;
                    case KeyEvent.KEYCODE_BACK:
                        passwordChangeDialog.dismiss();
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if (oldPasswordEdt.hasFocus()) {
                            oldPasswordLayout.setBackgroundResource(R.drawable.menu_setting_focus);
                            newPasswordLayout.setBackgroundResource(R.drawable.one_px);
                        } else if (newPasswordEdt.hasFocus()) {
                            newPasswordLayout.setBackgroundResource(R.drawable.menu_setting_focus);
                            confirmPasswordLayout.setBackgroundResource(R.drawable.one_px);
                        } else if (confirmPasswordEdt.hasFocus()) {
                            confirmPasswordLayout
                                    .setBackgroundResource(R.drawable.menu_setting_focus);
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        if (newPasswordEdt.hasFocus()) {
                            oldPasswordLayout.setBackgroundResource(R.drawable.one_px);
                            newPasswordLayout.setBackgroundResource(R.drawable.menu_setting_focus);
                        } else if (confirmPasswordEdt.hasFocus()) {
                            newPasswordLayout.setBackgroundResource(R.drawable.one_px);
                            confirmPasswordLayout
                                    .setBackgroundResource(R.drawable.menu_setting_focus);
                        } else {
                            confirmPasswordLayout.setBackgroundResource(R.drawable.one_px);
                        }
                }
                return false;
            }
        });
        passwordChangeDialog.show();
    }
}
