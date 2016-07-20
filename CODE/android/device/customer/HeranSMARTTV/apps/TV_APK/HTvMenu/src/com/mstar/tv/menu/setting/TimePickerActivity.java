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

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.mstar.tv.MenuConstants;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.ui.FocusScrollListView;

public class TimePickerActivity extends BaseActivity {

    private static final String DEFAULT_TILTE = "Select Time";

    public static final String PICK_TIME_HOUR = "HOUR";

    public static final String PICK_TIME_MINUTE = "MINUTE";

    private FocusScrollListView mTimePickerList;

    private List<SettingItem> items;

    private SettingAdapter mAdapter;

    private Animation menu_AnimIn_second;

    private Animation menu_AnimOut_three;

    private Animation menu_AnimOut_forstartActivity;

    private Animation menu_AnimOut_forresumeActivity;

    private boolean mIsPaused = false;

    private int selectItemIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publiclist);
        String title = getIntent().getStringExtra(MenuConstants.TITLE_KEY);
        if (TextUtils.isEmpty(title)) {
            title = DEFAULT_TILTE;
        }
        ((TextView) findViewById(R.id.setting_title_txt)).setText(title);

        initAnim();
        initItems();

        mTimePickerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                selectItemIndex = position;
                if (selectItemIndex != 0 && selectItemIndex != 6)
                    items.get(selectItemIndex).itemClicked();
            }
        });
    }

    @Override
    protected void onResume() {
        if (mIsPaused) {
            findViewById(R.id.setting_layout).startAnimation(menu_AnimOut_forresumeActivity);
            mIsPaused = false;
        } else {
            findViewById(R.id.setting_layout).startAnimation(menu_AnimIn_second);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        mIsPaused = true;
        super.onPause();
    }

    private void initAnim() {
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
    }

    private void initItems() {
        items = new ArrayList<SettingItem>();

        int initialHour = getIntent().getIntExtra(PICK_TIME_HOUR, 0);
        int initialMinute = getIntent().getIntExtra(PICK_TIME_MINUTE, 0);

        SettingItem hour = new SettingItem(this, getResources()
                .getString(R.string.time_picker_hour), 0, 23, initialHour,
                MenuConstants.ITEMTYPE_DIGITAL, true, true);
        items.add(hour);
        SettingItem minute = new SettingItem(this, getResources().getString(
                R.string.time_picker_minute), 0, 59, initialMinute,
                MenuConstants.ITEMTYPE_DIGITAL, true, true);
        items.add(minute);

        SettingItem confirm = new SettingItem(this, getResources().getString(
                R.string.time_picker_confirm), MenuConstants.ITEMTYPE_BUTTON, true);
        items.add(confirm);

        mTimePickerList = (FocusScrollListView) findViewById(R.id.context_lst);
        mTimePickerList.setFocusBitmap(R.drawable.menu_setting_focus);
        mAdapter = new SettingAdapter(this, items);
        mAdapter.setHasShowValue(true);
        mTimePickerList.setAdapter(mAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            selectItemIndex = mTimePickerList.getSelectedItemPosition();
            return items.get(selectItemIndex).onKeyDown(keyCode, event);
        } else if ((keyCode == KeyEvent.KEYCODE_BACK) || (keyCode == KeyEvent.KEYCODE_MENU)) {
            findViewById(R.id.setting_layout).startAnimation(menu_AnimOut_three);
            handler.sendEmptyMessageDelayed(MenuConstants.DELAYFINIFH, 200);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    void callBack(int resultVaule) {
        switch (selectItemIndex) {
            case 0:
            case 1:
                ((TextView) mTimePickerList.getSelectedView().getTag()).setText(String
                        .valueOf(resultVaule));
                mAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    void callBack(Boolean resultVaule) {

    }

    @Override
    void callBack() {
        Intent result = new Intent();
        result.putExtra(PICK_TIME_HOUR, items.get(0).getCurValue());
        result.putExtra(PICK_TIME_MINUTE, items.get(1).getCurValue());
        setResult(RESULT_OK, result);
        findViewById(R.id.setting_layout).startAnimation(menu_AnimOut_three);
        handler.sendEmptyMessageDelayed(MenuConstants.DELAYFINIFH, 200);
    }
}
