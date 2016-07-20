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
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.mstar.android.tv.TvTimerManager;
import com.mstar.android.tvapi.common.vo.EnumSleepTimeState;
import com.mstar.android.tvapi.common.vo.StandardTime;
import com.mstar.tv.MenuConstants;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.ui.FocusScrollListView;

public class TimeSettingActivity extends BaseActivity {

    private FocusScrollListView mTimeSettingList;

    private List<SettingItem> items;

    private SettingAdapter mAdapter;

    private Animation menu_AnimIn_second;

    private Animation menu_AnimOut_three;

    private Animation menu_AnimOut_forstartActivity;

    private Animation menu_AnimOut_forresumeActivity;

    private boolean mIsPaused = false;

    private int selectItemIndex = 0;

    private TvTimerManager mTvTimerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publiclist);
        ((TextView) findViewById(R.id.setting_title_txt)).setText(getIntent().getStringExtra(
                MenuConstants.TITLE_KEY));

        mTvTimerManager = TvTimerManager.getInstance();

        initItems();

        initAnim();

        setListeners();
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

        // Current Time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        SettingItem currentTime = new SettingItem(this, getResources().getString(
                R.string.time_setting_current_time), new String[] {
            calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE)
        }, 0, MenuConstants.ITEMTYPE_ENUM, false);
        items.add(currentTime);

        // Auto Halt
        boolean haltEnable = mTvTimerManager.isOffTimerEnable();
        SettingItem halt = new SettingItem(this, getResources().getString(
                R.string.time_setting_auto_halt), getResources().getStringArray(
                R.array.time_setting_auto_halt), haltEnable ? 1 : 0, MenuConstants.ITEMTYPE_ENUM,
                true);
        items.add(halt);

        // Halt Time
        StandardTime offTime = mTvTimerManager.getOffTimer();
        SettingItem haltTime = new SettingItem(this, getResources().getString(
                R.string.time_setting_halt_time), new String[] {
            offTime.hour + ":" + offTime.minute
        }, 0, MenuConstants.ITEMTYPE_ENUM, haltEnable);
        items.add(haltTime);

        // Auto Boot
        boolean bootEnable = mTvTimerManager.isOnTimerEnable();
        SettingItem boot = new SettingItem(this, getResources().getString(
                R.string.time_setting_auto_boot), getResources().getStringArray(
                R.array.time_setting_auto_boot), bootEnable ? 1 : 0, MenuConstants.ITEMTYPE_ENUM,
                true);
        items.add(boot);

        // Boot Time
        StandardTime onTime = mTvTimerManager.getOnTimer();
        SettingItem BootTime = new SettingItem(this, getResources().getString(
                R.string.time_setting_boot_time), new String[] {
            onTime.hour + ":" + onTime.minute
        }, 0, MenuConstants.ITEMTYPE_ENUM, bootEnable);
        items.add(BootTime);

        // Sleep Time
        int sleepModeIndex = mTvTimerManager.getSleepMode().ordinal();
        SettingItem SleepTime = new SettingItem(this, getResources().getString(
                R.string.time_setting_sleep_time), getResources().getStringArray(
                R.array.time_setting_sleep_time), sleepModeIndex, MenuConstants.ITEMTYPE_ENUM,
                true);
        items.add(SleepTime);

        mTimeSettingList = (FocusScrollListView) findViewById(R.id.context_lst);
        mTimeSettingList.setFocusBitmap(R.drawable.menu_setting_focus);
        mAdapter = new SettingAdapter(this, items);
        mAdapter.setHasShowValue(true);
        mTimeSettingList.setAdapter(mAdapter);

        // Initial focus
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getFocusable()) {
                mTimeSettingList.setSelection(i);
                selectItemIndex = i;
                break;
            }
        }
    }

    private void setListeners() {
        mTimeSettingList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                selectItemIndex = position;
                if (selectItemIndex == 2 || selectItemIndex == 4)
                    items.get(selectItemIndex).onKeyDown(KeyEvent.KEYCODE_UNKNOWN, null);
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() != KeyEvent.KEYCODE_DPAD_UP
                && event.getKeyCode() != KeyEvent.KEYCODE_DPAD_DOWN)
            return super.dispatchKeyEvent(event);

        // Only interrupt action_up of dpad_up or dpad_down
        if (event.getAction() == KeyEvent.ACTION_UP)
            return super.dispatchKeyEvent(event);

        boolean down = true;
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP)
            down = false;

        int nextIndex = selectItemIndex + (down ? 1 : -1);

        if (down) {
            while (nextIndex != selectItemIndex) {
                if (nextIndex >= items.size())
                    nextIndex = 0;
                if (items.get(nextIndex).getFocusable())
                    break;
                nextIndex++;
            }
        } else {
            while (nextIndex != selectItemIndex) {
                if (nextIndex < 0)
                    nextIndex = items.size() - 1;
                if (items.get(nextIndex).getFocusable())
                    break;
                nextIndex--;
            }
        }

        // let super process to show animation
        if ((nextIndex - selectItemIndex) == (down ? 1 : -1)) {
            selectItemIndex = nextIndex;
            return super.dispatchKeyEvent(event);
        }

        // the next/previous item is not focusable,use setSelection.
        if (nextIndex != selectItemIndex) {
            mTimeSettingList.setSelection(nextIndex);
            selectItemIndex = nextIndex;
            return true;
        } else {
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            selectItemIndex = mTimeSettingList.getSelectedItemPosition();
            switch (selectItemIndex) {
                case 1:
                case 3:
                case 5:
                    return items.get(selectItemIndex).onKeyDown(keyCode, event);
                default:
                    break;
            }
        } else if ((keyCode == KeyEvent.KEYCODE_BACK) || (keyCode == KeyEvent.KEYCODE_MENU)) {
            findViewById(R.id.setting_layout).startAnimation(menu_AnimOut_three);
            handler.sendEmptyMessageDelayed(MenuConstants.DELAYFINIFH, 200);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateTime(int index, Intent data) {
        int hour = data.getIntExtra(TimePickerActivity.PICK_TIME_HOUR, 0);
        int minute = data.getIntExtra(TimePickerActivity.PICK_TIME_MINUTE, 0);
        items.get(index).setValues(new String[] {
            hour + ":" + minute
        });
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    updateTime(requestCode, data);
                    StandardTime offTime = mTvTimerManager.getOffTimer();
                    offTime.hour = data.getIntExtra(TimePickerActivity.PICK_TIME_HOUR, 0);
                    offTime.minute = data.getIntExtra(TimePickerActivity.PICK_TIME_MINUTE, 0);
                    mTvTimerManager.setOffTimer(offTime);
                    // force timer service update off time, due to time
                    // picker only can be triggered iff off timer is enabled.
                    mTvTimerManager.setOffTimerEnable(true);
                    break;
                case 4:
                    updateTime(requestCode, data);
                    StandardTime dateTime = mTvTimerManager.getOnTimer();
                    dateTime.hour = data.getIntExtra(TimePickerActivity.PICK_TIME_HOUR, 0);
                    dateTime.minute = data.getIntExtra(TimePickerActivity.PICK_TIME_MINUTE, 0);
                    mTvTimerManager.setOnTimer(dateTime);
                    // force timer service update on time, due to time
                    // picker only can be triggered iff on timer is enabled.
                    mTvTimerManager.setOnTimerEnable(true);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    void callBack(int resultVaule) {
        if (!items.get(selectItemIndex).getFocusable()) {
            return;
        }
        switch (selectItemIndex) {
        // Select Time
            case 2:
            case 4:
                findViewById(R.id.setting_layout).startAnimation(menu_AnimOut_forstartActivity);
                Intent intent = new Intent(this, TimePickerActivity.class);
                String time = items.get(selectItemIndex).getValues()[items.get(selectItemIndex)
                        .getCurValue()];
                String[] timeSplit = time.split(":");
                try {
                    int hour = Integer.parseInt(timeSplit[0]);
                    int minute = Integer.parseInt(timeSplit[1]);
                    intent.putExtra(TimePickerActivity.PICK_TIME_HOUR, hour);
                    intent.putExtra(TimePickerActivity.PICK_TIME_MINUTE, minute);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, selectItemIndex);
                break;
            // Auto Halt
            case 1:
                ((TextView) mTimeSettingList.getSelectedView().getTag()).setText(items.get(
                        selectItemIndex).getValues()[resultVaule]);
                items.get(2).setFocusable(resultVaule != 0);
                mTvTimerManager.setOffTimerEnable(resultVaule != 0);
                mAdapter.notifyDataSetChanged();
                break;
            // Auto Boot
            case 3:
                ((TextView) mTimeSettingList.getSelectedView().getTag()).setText(items.get(
                        selectItemIndex).getValues()[resultVaule]);
                items.get(4).setFocusable(resultVaule != 0);
                mTvTimerManager.setOnTimerEnable(resultVaule != 0);
                mAdapter.notifyDataSetChanged();
                break;
            // Sleep Time
            case 5:
                ((TextView) mTimeSettingList.getSelectedView().getTag()).setText(items.get(
                        selectItemIndex).getValues()[resultVaule]);
                mAdapter.notifyDataSetChanged();
                mTvTimerManager.setSleepMode(EnumSleepTimeState.values()[resultVaule]);
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

    }

}
