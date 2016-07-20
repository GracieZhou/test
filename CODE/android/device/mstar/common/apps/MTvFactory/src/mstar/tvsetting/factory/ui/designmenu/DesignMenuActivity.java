//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2015 MStar Semiconductor, Inc. All rights reserved.
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

package mstar.tvsetting.factory.ui.designmenu;

import mstar.factorymenu.ui.R;
import mstar.tvsetting.factory.desk.FactoryDB;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

@TargetApi(12)
public class DesignMenuActivity extends Activity {
    private static final String TAG = "DesignMenuActivity";

    private ViewFlipper viewFlipper;

    private IFactoryDesk factoryDesk;

    private DesignMenuViewHolder designViewHolder;

    private NonLinearAdjustViewHolder nonLinearViewHolder;

    private NonStandardAdjustViewHolder nonStandardViewHolder;

    private PEQAdjustViewHolder peqViewHolder;

    private PictureModeAdjustViewHolder picModeViewHolder;

    private SSCAdjustViewHolder sscViewHolder;

    private UrsaInfoViewHolder ursaViewHolder;

    private PanelInfoViewHolder panelViewHolder;

    private OtherOptionAdjustViewHolder otheroptionViewHolder;

    private IPEnableMappingViewHolder ipenablemappingViewHolder;

    private CISettingViewHolder ciViewHolder;

    private final int MAIN_PAGE = 0;

    private final int PICTURE_MODE_PAGE = 1;

    private final int NON_LINER_PAGE = 2;

    private final int NON_STANDARD_PAGE = 3;

    private final int SSC_PAGE = 4;

    private final int PEQ_PAGE = 5;

    private final int URSA_INFO_PAGE = 6;

    private final int PANEL_INFO_PAGE = 7;

    private final int OTHER_OPTION_PAGE = 8;

    private final int IP_ENALE_MAPPING_PAGE = 9;

    private final int CI_SETTING_PAGE = 10;

    // VIEWINDEX TO BUTTON
    private final int BUTTON_POSITION_MAIN_PAGE = 0;

    private final int BUTTON_POSITION_PICTURE_MODE_PAGE = 1;

    private final int BUTTON_POSITION_NON_LINER_PAGE = 2;

    private final int BUTTON_POSITION_NON_STANDARD_PAGE = 3;

    private final int BUTTON_POSITION_SSC_PAGE = 4;

    private final int BUTTON_POSITION_PEQ_PAGE = 5;

    private final int BUTTON_POSITION_URSA_INFO_PAGE = 9;

    private final int BUTTON_POSITION_PANEL_INFO_PAGE = 10;

    private final int BUTTON_POSITION_OTHER_OPTION_PAGE = 11;

    private final int BUTTON_POSITION_IP_ENALE_MAPPING_PAGE = 12;

    private final int BUTTON_POSITION_CI_SETTING_PAGE = 13;

    private boolean isFirst = true;

    private int currentPage = MAIN_PAGE;

    private static UsbManager usbman;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
        viewFlipper = (ViewFlipper) findViewById(R.id.factory_view_flipper);
        usbman = (UsbManager) getApplicationContext().getSystemService(Context.USB_SERVICE);
        LinearLayout ursa_test = (LinearLayout) findViewById(R.id.linearlayout_factory_ursa_test);
        ursa_test.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DesignMenuActivity.this, UrsaTestActivity.class);
                startActivity(intent);
            }
        });
        factoryDesk = FactoryDeskImpl.getInstance(this);
        FactoryDB.getInstance(this).openDB();
        factoryDesk.loadEssentialDataFromDB();

        designViewHolder = new DesignMenuViewHolder(DesignMenuActivity.this);
        designViewHolder.findView();
        registerListeners();
        isFirst = false;

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v(TAG, "onNewIntent to finish");
        finish();
    }

    @Override
    protected void onResume() {
        if (!isFirst) {
            FactoryDB.getInstance(this).openDB();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (factoryDesk != null) {
            FactoryDB.getInstance(this).closeDB();
        }
        Intent intent = new Intent("factoryDirty");
        sendBroadcast(intent);
        super.onPause();
    }

    private void registerListeners() {
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.linearlayout_factorymenu:
                        Intent intent = new Intent();
                        ComponentName componentName = new ComponentName("mstar.factorymenu.ui",
                                "mstar.tvsetting.factory.ui.factorymenu.FactoryMenuActivity");
                        intent.setComponent(componentName);
                        startActivity(intent);
                        break;
                    case R.id.linearlayout_factory_picturemode:
                        currentPage = PICTURE_MODE_PAGE;
                        viewFlipper.setDisplayedChild(PICTURE_MODE_PAGE);
                        picModeViewHolder = new PictureModeAdjustViewHolder(DesignMenuActivity.this,
                                factoryDesk);
                        picModeViewHolder.findView();
                        picModeViewHolder.onCreate();
                        break;
                    case R.id.linearlayout_factory_non_linear:
                        currentPage = NON_LINER_PAGE;
                        viewFlipper.setDisplayedChild(NON_LINER_PAGE);
                        nonLinearViewHolder = new NonLinearAdjustViewHolder(DesignMenuActivity.this,
                                factoryDesk);
                        nonLinearViewHolder.findView();
                        nonLinearViewHolder.onCreate();
                        break;
                    case R.id.linearlayout_factory_nonstandard:
                        currentPage = NON_STANDARD_PAGE;
                        viewFlipper.setDisplayedChild(NON_STANDARD_PAGE);
                        nonStandardViewHolder = new NonStandardAdjustViewHolder(
                                DesignMenuActivity.this, factoryDesk);
                        // nonStandardViewHolder.findView();
                        // nonStandardViewHolder.onCreate();
                        break;
                    case R.id.linearlayout_factory_ssc:
                        currentPage = SSC_PAGE;
                        viewFlipper.setDisplayedChild(SSC_PAGE);
                        sscViewHolder = new SSCAdjustViewHolder(DesignMenuActivity.this, factoryDesk);
                        sscViewHolder.findView();
                        sscViewHolder.onCreate();
                        break;
                    case R.id.linearlayout_factory_peq:
                        currentPage = PEQ_PAGE;
                        viewFlipper.setDisplayedChild(PEQ_PAGE);
                        peqViewHolder = new PEQAdjustViewHolder(DesignMenuActivity.this);
                        peqViewHolder.findView();
                        peqViewHolder.onCreate();
                        break;
                    case R.id.linearlayout_ursa_info:
                        currentPage = URSA_INFO_PAGE;
                        viewFlipper.setDisplayedChild(URSA_INFO_PAGE);
                        ursaViewHolder = new UrsaInfoViewHolder(DesignMenuActivity.this);
                        ursaViewHolder.findView();
                        ursaViewHolder.onCreate();
                        break;
                    case R.id.linearlayout_panel_info:
                        Log.i(TAG, "---listener panel info---");
                        currentPage = PANEL_INFO_PAGE;
                        viewFlipper.setDisplayedChild(PANEL_INFO_PAGE);
                        panelViewHolder = new PanelInfoViewHolder(DesignMenuActivity.this);
                        panelViewHolder.findView();
                        panelViewHolder.onCreate();
                        break;
                    case R.id.linearlayout_design_otheroption:
                        currentPage = OTHER_OPTION_PAGE;
                        viewFlipper.setDisplayedChild(OTHER_OPTION_PAGE);
                        otheroptionViewHolder = new OtherOptionAdjustViewHolder(
                                DesignMenuActivity.this, factoryDesk);
                        otheroptionViewHolder.findView();
                        otheroptionViewHolder.onCreate();
                        break;
                    case R.id.linearlayout_IP_Enable_Mapping:
                        currentPage = IP_ENALE_MAPPING_PAGE;
                        viewFlipper.setDisplayedChild(IP_ENALE_MAPPING_PAGE);
                        ipenablemappingViewHolder = new IPEnableMappingViewHolder(
                                DesignMenuActivity.this, factoryDesk);
                        ipenablemappingViewHolder.onCreate();
                        break;
                     case R.id.linearlayout_factory_ci_setting:
                        currentPage = CI_SETTING_PAGE;
                        viewFlipper.setDisplayedChild(CI_SETTING_PAGE);
                        ciViewHolder = new CISettingViewHolder(DesignMenuActivity.this, factoryDesk);
                        ciViewHolder.findView();
                        ciViewHolder.onCreate();
                        break;
                    default:
                        break;
                }

                if (null == getCurrentFocus()) {
                    View currentView = (View)viewFlipper.getCurrentView();
                    View focusForwad = (View)currentView.focusSearch(View.FOCUS_FORWARD);
                    focusForwad.requestFocus();
                }
            }
        };

        designViewHolder.linear_factory_menu.setOnClickListener(listener);
        designViewHolder.linear_factory_picturemode.setOnClickListener(listener);
        designViewHolder.linear_factory_nonstandard.setOnClickListener(listener);
        designViewHolder.linear_factory_non_linear.setOnClickListener(listener);
        designViewHolder.linear_factory_ssc.setOnClickListener(listener);
        designViewHolder.linear_factory_peq.setOnClickListener(listener);
        designViewHolder.linear_factory_ursa_info.setOnClickListener(listener);
        designViewHolder.linear_factory_panel_info.setOnClickListener(listener);
        designViewHolder.linear_design_otheroption.setOnClickListener(listener);
        designViewHolder.linear_design_ipenablemapping.setOnClickListener(listener);
        designViewHolder.linear_factory_ci_setting.setOnClickListener(listener);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean bRet = false;

        currentPage = viewFlipper.getDisplayedChild();
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (currentPage == MAIN_PAGE) {
                finish();
            } else {
                returnRoot(currentPage);
            }
            return true;
        }

        switch (currentPage) {
            case MAIN_PAGE:
                View view = this.getCurrentFocus();
                if (null != view) {
                    int currentid = view.getId();
                    if (currentid == R.id.linearlayout_factory_mountconfig
                            || currentid == R.id.linearlayout_factory_pqtableupdate) {
                        bRet = designViewHolder.onKeyDown(keyCode, event);
                    }
                }
                break;
            case PICTURE_MODE_PAGE:
                bRet = picModeViewHolder.onKeyDown(keyCode, event);
                break;
            case NON_LINER_PAGE:
                bRet = nonLinearViewHolder.onKeyDown(keyCode, event);
                break;
            case SSC_PAGE:
                bRet = sscViewHolder.onKeyDown(keyCode, event);
                break;
            case PEQ_PAGE:
                bRet = peqViewHolder.onKeyDown(keyCode, event);
                break;
            case OTHER_OPTION_PAGE:
                bRet = otheroptionViewHolder.onKeyDown(keyCode, event);
                break;
            case CI_SETTING_PAGE:
                bRet = ciViewHolder.onKeyDown(keyCode, event);
                break;
            default:
                break;
        }
        if (bRet == false) {
            bRet = super.onKeyDown(keyCode, event);
        }
        return bRet;
    }

    public void returnRoot(int pageIdx) {
        currentPage = MAIN_PAGE;
        viewFlipper.setDisplayedChild(MAIN_PAGE);
        LinearLayout container = (LinearLayout) findViewById(R.id.linearlayout_factory_menu);
        LinearLayout focusedLayout = (LinearLayout) container.getChildAt(pageToBtnPos(pageIdx));
        focusedLayout.setFocusable(true);
        focusedLayout.requestFocus();
        focusedLayout.setFocusableInTouchMode(true);
    }

    public static boolean CheckUsbIsExist() {
        boolean ret = false;
        if (usbman.getDeviceList().isEmpty() == false) {
            ret = true;
        }
        return ret;
    }

    private int pageToBtnPos(int pageIdx) {
        switch (pageIdx) {
            case MAIN_PAGE:
                return BUTTON_POSITION_MAIN_PAGE;
            case PICTURE_MODE_PAGE:
                return BUTTON_POSITION_PICTURE_MODE_PAGE;
            case NON_LINER_PAGE:
                return BUTTON_POSITION_NON_LINER_PAGE;
            case NON_STANDARD_PAGE:
                return BUTTON_POSITION_NON_STANDARD_PAGE;
            case SSC_PAGE:
                return BUTTON_POSITION_SSC_PAGE;
            case PEQ_PAGE:
                return BUTTON_POSITION_PEQ_PAGE;
            case OTHER_OPTION_PAGE:
                return BUTTON_POSITION_OTHER_OPTION_PAGE;
            case URSA_INFO_PAGE:
                return BUTTON_POSITION_URSA_INFO_PAGE;
            case PANEL_INFO_PAGE:
                return BUTTON_POSITION_PANEL_INFO_PAGE;
            case IP_ENALE_MAPPING_PAGE:
                return BUTTON_POSITION_IP_ENALE_MAPPING_PAGE;
            case CI_SETTING_PAGE:
                return BUTTON_POSITION_CI_SETTING_PAGE;
            default:
                return BUTTON_POSITION_MAIN_PAGE;
        }

    }
}
