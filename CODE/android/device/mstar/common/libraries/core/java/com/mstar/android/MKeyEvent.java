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

package com.mstar.android;

import android.view.KeyEvent;

public class MKeyEvent {

    // NOTE: If you add a new keycode here you must also add it to:
    //  isSystem()
    //  isWakeKey()
    //  frameworks/native/include/android/keycodes.h
    //  frameworks/native/include/input/InputEventLabels.h
    //  frameworks/base/core/res/res/values/attrs.xml
    //  emulator?
    //  LAST_KEYCODE
    //
    //  Also Android currently does not reserve code ranges for vendor-
    //  specific key codes.  If you have new key codes to have, you
    //  MUST contribute a patch to the open source project to define
    //  those new codes.  This is intended to maintain a consistent
    //  set of key code definitions across all Android devices.

    // Refer to framework/base/core/java/android/view/KeyEvent.java for last key defined by original Android
    private static final int KEYEVENT_LAST_KEY = KeyEvent.KEYCODE_HELP;

    /** Key code constant: Sound mode key.
     * On TV remotes, corresponds to the sound mode key. */
    public static final int KEYCODE_TV_SOUND_MODE                          = KEYEVENT_LAST_KEY + 1;
    /** Key code constant: Picture mode key.
     * On TV remotes, switches the picture mode for display. */
    public static final int KEYCODE_TV_PICTURE_MODE                        = KEYEVENT_LAST_KEY + 2;
    /** Key code constant: EPG key.
     * On TV remotes, corresponds to the EPG key. */
    public static final int KEYCODE_TV_EPG                                 = KEYEVENT_LAST_KEY + 3;
    /** Key code constant: List key.
     * On TV remotes, show the channel list. */
    public static final int KEYCODE_TV_LIST                                = KEYEVENT_LAST_KEY + 4;
    /** Key code constant: Subtitle key.
     * On TV remotes, corresponds to the Subtitle key. */
    public static final int KEYCODE_TV_SUBTITLE                            = KEYEVENT_LAST_KEY + 5;
    /** Key code constant: Favorite key.
     * On TV remotes, shows the favorite channels. */
    public static final int KEYCODE_TV_FAVORITE                            = KEYEVENT_LAST_KEY + 6;
    /** Key code constant: MTS key.
     * On TV remotes, corresponds to the MTS key. */
    public static final int KEYCODE_TV_MTS                                 = KEYEVENT_LAST_KEY + 7;
    /** Key code constant: Freeze key.
     * On TV remotes, corresponds to the Freeze key. */
    public static final int KEYCODE_TV_FREEZE                              = KEYEVENT_LAST_KEY + 8;
    /** Key code constant: CC key.
     * On TV remotes, corresponds to the CC key. */
    public static final int KEYCODE_TV_CC                                  = KEYEVENT_LAST_KEY + 9;
    /** Key code constant: Ginga back key.
     * On TV remotes, corresponds to the Ginga back key. */
    public static final int KEYCODE_TV_GINGA_BACK                          = KEYEVENT_LAST_KEY + 10;
    /** Key code constant: Balance key.
     * On TV remotes, corresponds to the Balance key. */
    public static final int KEYCODE_TV_BALANCE                             = KEYEVENT_LAST_KEY + 11;
    /** Key code constant: Index key.
     * On TV remotes, corresponds to the Index key. */
    public static final int KEYCODE_TV_INDEX                               = KEYEVENT_LAST_KEY + 12;
    /** Key code constant: Hold key.
     * On TV remotes, corresponds to the Hold key. */
    public static final int KEYCODE_TV_HOLD                                = KEYEVENT_LAST_KEY + 13;
    /** Key code constant: Update key.
     * On TV remotes, corresponds to the Update key. */
    public static final int KEYCODE_TV_UPDATE                              = KEYEVENT_LAST_KEY + 14;
    /** Key code constant: Reveal key.
     * On TV remotes, corresponds to the Reveal key. */
    public static final int KEYCODE_TV_REVEAL                              = KEYEVENT_LAST_KEY + 15;
    /** Key code constant: Subcode key.
     * On TV remotes, corresponds to the Subcode key. */
    public static final int KEYCODE_TV_SUBCODE                             = KEYEVENT_LAST_KEY + 16;
    /** Key code constant: Size key.
     * On TV remotes, corresponds to the Size key. */
    public static final int KEYCODE_TV_SIZE                                = KEYEVENT_LAST_KEY + 17;
    /** Key code constant: Clock key.
     * On TV remotes, corresponds to the Clock key. */
    public static final int KEYCODE_TV_CLOCK                               = KEYEVENT_LAST_KEY + 18;
    /** Key code constant: TV setting key.
     * On TV remotes, corresponds to the TV setting key. */
    public static final int KEYCODE_TV_SETTING                             = KEYEVENT_LAST_KEY + 19;
    /** Key code constant: Screenshot key.
     * On TV remotes, capture the screenshot. */
    public static final int KEYCODE_SCREENSHOT                             = KEYEVENT_LAST_KEY + 20;
    /** Key code constant: Netflix key.
     * On TV remotes, corresponds to the Netflix key.  */
    public static final int KEYCODE_NETFLIX                                = KEYEVENT_LAST_KEY + 21;
    /** Key code constant: Amazon key.
     * On TV remotes, corresponds to the Amazon key.  */
    public static final int KEYCODE_AMAZON                                 = KEYEVENT_LAST_KEY + 22;
	
	//EosTek Patch Begin
	public static final int KEYCODE_MSTAR_PVR_BROWSER 					   = 319;

    public static final int KEYCODE_MSTAR_SURROUND_SOUND                   = 320;
	//EosTek Patch End

    /** Notice:
     *  Following keycode definitions are old styles, please use new style above */

    /** @deprecated Use {@link KEYCODE_TV_SOUND_MODE)}. */
    @Deprecated
    public static final int KEYCODE_SOUND_MODE                          = KEYCODE_TV_SOUND_MODE;
    /** @deprecated Use {@link KEYCODE_TV_PICTURE_MODE)}. */
    @Deprecated
    public static final int KEYCODE_PICTURE_MODE                        = KEYCODE_TV_PICTURE_MODE;
    /** @deprecated Use {@link KEYCODE_TV_EPG)}. */
    @Deprecated
    public static final int KEYCODE_EPG                                 = KEYCODE_TV_EPG;
    /** @deprecated Use {@link KEYCODE_TV_LIST)}. */
    @Deprecated
    public static final int KEYCODE_LIST                                = KEYCODE_TV_LIST;
    /** @deprecated Use {@link KEYCODE_TV_SUBTITLE)}. */
    @Deprecated
    public static final int KEYCODE_SUBTITLE                            = KEYCODE_TV_SUBTITLE;
    /** @deprecated Use {@link KEYCODE_TV_FAVORITE)}. */
    @Deprecated
    public static final int KEYCODE_FAVORITE                            = KEYCODE_TV_FAVORITE;
    /** @deprecated Use {@link KEYCODE_TV_MTS)}. */
    @Deprecated
    public static final int KEYCODE_MTS                                 = KEYCODE_TV_MTS;
    /** @deprecated Use {@link KEYCODE_TV_FREEZE)}. */
    @Deprecated
    public static final int KEYCODE_FREEZE                              = KEYCODE_TV_FREEZE;
    /** @deprecated Use {@link KEYCODE_TV_CC)}. */
    @Deprecated
    public static final int KEYCODE_CC                                  = KEYCODE_TV_CC;
    /** @deprecated Use {@link KEYCODE_AMAZON)}. */
    @Deprecated
    public static final int KEYCODE_AMAZONE                             = KEYCODE_AMAZON;
    /** @deprecated Use {@link KEYCODE_TV_BALANCE)}.*/
    @Deprecated
    public static final int KEYCODE_MSTAR_BALANCE                       = KEYCODE_TV_BALANCE;
    /** @deprecated Use {@link KEYCODE_TV_INDEX)}.*/
    @Deprecated
    public static final int KEYCODE_MSTAR_INDEX                         = KEYCODE_TV_INDEX;
    /** @deprecated Use {@link KEYCODE_TV_HOLD)}.*/
    @Deprecated
    public static final int KEYCODE_MSTAR_HOLD                          = KEYCODE_TV_HOLD;
    /** @deprecated Use {@link KEYCODE_TV_UPDATE)}.*/
    @Deprecated
    public static final int KEYCODE_MSTAR_UPDATE                        = KEYCODE_TV_UPDATE;
    /** @deprecated Use {@link KEYCODE_TV_REVEAL)}.*/
    @Deprecated
    public static final int KEYCODE_MSTAR_REVEAL                        = KEYCODE_TV_REVEAL;
    /** @deprecated Use {@link KEYCODE_TV_SUBCODE)}.*/
    @Deprecated
    public static final int KEYCODE_MSTAR_SUBCODE                       = KEYCODE_TV_SUBCODE;
    /** @deprecated Use {@link KEYCODE_TV_SIZE)}.*/
    @Deprecated
    public static final int KEYCODE_MSTAR_SIZE                          = KEYCODE_TV_SIZE;
    /** @deprecated Use {@link KEYCODE_TV_CLOCK)}.*/
    @Deprecated
    public static final int KEYCODE_MSTAR_CLOCK                         = KEYCODE_TV_CLOCK;


    /** Following two keycodes already be defined in KeyEvent.java */
    /** @deprecated Use {@link KeyEvent.KEYCODE_LAST_CHANNEL)}.*/
    @Deprecated
    public static final int KEYCODE_CHANNEL_RETURN                      = KeyEvent.KEYCODE_LAST_CHANNEL;
    /** @deprecated Use {@link KeyEvent.KEYCODE_TV_TELETEXT)}.*/
    @Deprecated
    public static final int KEYCODE_TTX                                 = KeyEvent.KEYCODE_TV_TELETEXT;
    /** @deprecated Use {@link KeyEvent.KEYCODE_TV_ZOOM_MODE)}.*/
    @Deprecated
    public static final int KEYCODE_ASPECT_RATIO                        = KeyEvent.KEYCODE_TV_ZOOM_MODE;
}
