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

public class DatabaseDesk {
    public static enum EN_MS_PICTURE {
        // / picture mode dynamic
        PICTURE_DYNAMIC,
        // / picture mode normal
        PICTURE_NORMAL,
        // / picture mode mild
        PICTURE_SOFT,
        // / picture mode user
        PICTURE_USER,
        // / picture mode vivid
        PICTURE_VIVID,
        // / picture mode natural
        PICTURE_NATURAL,
        // / picture mode sports
        PICTURE_SPORTS,
        // / picture mode number
        PICTURE_NUMS
    }

    /** color temperature */
    public static enum EN_MS_COLOR_TEMP {
        // /color temperature cool
        MS_COLOR_TEMP_COOL,
        // /color temperature medium
        MS_COLOR_TEMP_NATURE,
        // /color temperature warm
        MS_COLOR_TEMP_WARM,
        // /color temperature user
        MS_COLOR_TEMP_USER,
        // /color temperature
        MS_COLOR_TEMP_NUM
    }

    /** define detail setting of picture mode */
    public static class T_MS_PICTURE {
        // / backlight
        public short backlight;

        // / contrast
        public short contrast;

        // / brightness
        public short brightness;

        // / Saturation
        public short saturation;

        // / Sharpness
        public short sharpness;

        // / Hue
        public short hue;

        // / color temperature setting
        public EN_MS_COLOR_TEMP eColorTemp;

        public T_MS_PICTURE(short backlight, short con, short bri, short sat, short sha, short hue,
                EN_MS_COLOR_TEMP colortemp) {
            this.backlight = backlight;
            this.contrast = con;
            this.brightness = bri;
            this.saturation = sat;
            this.sharpness = sha;
            this.hue = hue;
            this.eColorTemp = colortemp;
        }
    } // T_MS_PICTURE

    /** define noise reduction setting */
    public static enum EN_MS_NR {
        // / noise reduction off
        MS_NR_OFF,
        // / noise reduction low
        MS_NR_LOW,
        // / noise reduction middle
        MS_NR_MIDDLE,
        // / noise reduction high
        MS_NR_HIGH,
        // / noise reduction auto
        MS_NR_AUTO,
        // / total noise reduction type number
        MS_NR_NUM,
    }

    /** MPEG noise reduction setting */
    public static enum EN_MS_MPEG_NR {
        // / MPEG noise reduction off
        MS_MPEG_NR_OFF,
        // / MPEG noise reduction low
        MS_MPEG_NR_LOW,
        // / MPEG noise reduction middle
        MS_MPEG_NR_MIDDLE,
        // / MPEG noise reduction high
        MS_MPEG_NR_HIGH,
        // / total mpeg noise reduction type number
        MS_MPEG_NR_NUM,
    }

    /** define enum for noise reduction and mpeg noise reduction */
    public static class T_MS_NR_MODE {
        // / noise reduction setting
        public EN_MS_NR eNR;

        // / MPEG noise reduction setting
        public EN_MS_MPEG_NR eMPEG_NR;

        public T_MS_NR_MODE(EN_MS_NR evalue1, EN_MS_MPEG_NR evalue2) {
            this.eNR = evalue1;
            this.eMPEG_NR = evalue2;
        }
    }

    /** define video setting for */
    // public class VideoParam
    // //{
    // / check sum <<checksum should be put at top of the struct, do not
    // move it to other place>>
    public int CheckSum;

    // / picture mode setting
    public EN_MS_PICTURE ePicture;

    // / picture mode detail setting, 24Byte
    public T_MS_PICTURE astPicture[];

    // / enum for noise reduction and mpeg noise reduction
    public T_MS_NR_MODE eNRMode[];
    // }
}