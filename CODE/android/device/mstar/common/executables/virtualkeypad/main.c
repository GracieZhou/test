//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2014 MStar Semiconductor, Inc. All rights reserved.
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

#define LOG_TAG "virtualkeypad"
#include <utils/Log.h>

#include <stdio.h>
#include <linux/input.h>
#include <linux/uinput.h>
#include <time.h>
#include <sys/time.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <stdlib.h>
#include <pthread.h>

#include "MsTypes.h"
#include "MsCommon.h"
#include "drvMMIO.h"
#include "drvSAR.h"
#include "drvMIU.h"
#include "drvSERFLASH.h"
#include "drvSYS.h"
#include "keypad.h"
#include "iniparser.h"
#include "tvini.h"
#define SYS_INI_PATH_FILENAME          "/config/sys.ini"

#define MAX_BUFFER 63
typedef unsigned char      BOOL;
#define MAX_KEYPAD_BUFFER 63
static MS_U32 keypad_input_interval = 0; /* polling interval of keypad input*/
static int uinp_fd = -1;
static int loop_count = 0;

pthread_t keypad_EventThreadId = (pthread_t)NULL;

/*
 * declaration of private data
 */
typedef struct {
    ///upper bound
    unsigned char u8UpBnd;
    ///low bound
    unsigned char u8LoBnd;
} SARKpdBndCfg;

typedef struct {
    MS_BOOL bEnable;
    unsigned char u8SARChID;
    SARKpdBndCfg tSARChBnd;
    unsigned char u8KeyLevelNum;  // 0-8 levels
    unsigned char u8KeyThreshold[8];  // each threshold match to one keycode
    unsigned char u8KeyCode[8];
} SARKpdRegCfg;

typedef struct {
    int min_keycode;
    int max_keycode;
} stKeypadDeviceData;

typedef struct {
    MS_U8 u8ReKeycode;
    MS_U8 u8Rekey1;
    MS_U8 u32ReTime1;
    MS_U8 u8Rekey2;
    MS_U8 u32ReTime2;
} stEOSRepeatKey;


static SARKpdRegCfg BOARD_KPD[MAX_KEYPAD_CH] = {
    KPD_TABLE
};

#define MAX_EOS_REPEATKEY_NUM 4

#ifdef KEYPAD_EOS_REPEATKEY_TABLE
static stEOSRepeatKey EOSRepeatKey[MAX_EOS_REPEATKEY_NUM] = {
    KEYPAD_EOS_REPEATKEY_TABLE
};
#else
static stEOSRepeatKey EOSRepeatKey[MAX_EOS_REPEATKEY_NUM] = {
                                                                {0x00, 0x00, 00, 0x00, 00},\
                                                                {0x00, 0x00, 00, 0x00, 00},\
                                                                {0x00, 0x00, 00, 0x00, 00},\
                                                                {0x00, 0x00, 00, 0x00, 00},
                                                            };
#endif

// Function Declare
int setup_uinput_device(stKeypadDeviceData* pKpdDev);
void write_event_to_device(MS_U8 u8KeyCode, MS_U8 u8Repeat);
/*
 * Input thread reading from device.
 * Generates events on incoming data.
 */
static void* keypad_EventThread(void *driver_data) {
    SAR_KpdResult ret;
    MS_U8 u8Keycode, u8Repeat;
    MS_U8 repeatKey = NULL_KEYVALUE;
    MS_U32 u32RepeatCnt;
    int i,j;
    
    u32RepeatCnt = 0;
    while (1) {
        ret = E_SAR_KPD_FAIL;
        u8Keycode = NULL_KEYVALUE;
        u8Repeat = 0;
        /* sleep an interval time */
        usleep(keypad_input_interval);

        ret = MDrv_SAR_Kpd_GetKeyCode(&u8Keycode, &u8Repeat);

        //printf("keycode = %d\n", u8Keycode);

        /* check the get keycode successfully */
        if (ret != E_SAR_KPD_OK) {
            loop_count += 1;
            if (loop_count == SLOW_POLLING_BOUNDARY)
                keypad_input_interval = KEYPAD_INPUT_SLOW_POLLING;

            if (repeatKey != NULL_KEYVALUE) {
                write_event_to_device(repeatKey, 0);
                repeatKey = NULL_KEYVALUE;
            }
#ifdef KEYPAD_EOS_REPEATKEY_TABLE
            u32RepeatCnt = 0;
#endif
            continue;
        }

        /* check the keypad is vaild? */
        if (u8Keycode == NULL_KEYVALUE) {
            loop_count += 1;
            if (loop_count == SLOW_POLLING_BOUNDARY)
                keypad_input_interval = KEYPAD_INPUT_SLOW_POLLING;
            continue;
        }

        /* fill event to uinput device. */
        if (u8Repeat == 1) {
            repeatKey = u8Keycode;
#ifdef KEYPAD_EOS_REPEATKEY_TABLE
            u32RepeatCnt++;
            if (u8Keycode != NULL_KEYVALUE)
            {
                for (j=0;j<MAX_EOS_REPEATKEY_NUM;j++)
                {
                    if (u8Keycode == EOSRepeatKey[j].u8ReKeycode)
                    {
                        if (u32RepeatCnt == EOSRepeatKey[j].u32ReTime2)
                        {
                            write_event_to_device(EOSRepeatKey[j].u8Rekey2, u8Repeat);
                            write_event_to_device(EOSRepeatKey[j].u8Rekey2, 0);
                        }
                        else if (u32RepeatCnt == EOSRepeatKey[j].u32ReTime1)
                        {
                            write_event_to_device(EOSRepeatKey[j].u8Rekey1, u8Repeat);
                            write_event_to_device(EOSRepeatKey[j].u8Rekey1, 0);
                        }
                        else
                        {
                        }
                        printf ("keypad u8Keycode %d u32RepeatCnt %d\n", u8Keycode, u32RepeatCnt);
                        u8Keycode = NULL_KEYVALUE;
                        repeatKey = u8Keycode;
                        continue;
                    }
                }
            }
#endif
        }

        if (u8Keycode == NULL_KEYVALUE)
        {
            continue;
        }
        write_event_to_device(u8Keycode, u8Repeat);
        loop_count = 0;
        keypad_input_interval = KEYPAD_INPUT_NORMAL_POLLING;
    }

    printf ("keypad keypad thread died\n");
    pthread_exit(NULL);
    return NULL;
}

int setup_uinput_device(stKeypadDeviceData* pKpdDev) {
    struct uinput_user_dev uinp; // uInput device structure
    int i;

    // Open the input device
    uinp_fd = open("/dev/uinput", O_WRONLY | O_NDELAY);
    if (uinp_fd == 0) {
        printf("Unable to open /dev/uinput\n");
        return -1;
    }

    // Intialize the uInput device to NULL
    memset(&uinp, 0x00, sizeof(uinp));
    strncpy(uinp.name, KEYPAD_NAME, sizeof(uinp.name)-1);
    uinp.id.vendor = KEYPAD_VENDOR_ID;
    uinp.id.product = 0x0002;
    uinp.id.bustype = BUS_VIRTUAL;

    // Keyboard
    ioctl(uinp_fd, UI_SET_EVBIT, EV_KEY);
    for (i = pKpdDev->min_keycode; i < pKpdDev->max_keycode; i++) {
        ioctl(uinp_fd, UI_SET_KEYBIT, i);
    }

    // Create input device into input sub-system
    if (write(uinp_fd, &uinp, sizeof(uinp)) != sizeof(uinp)) {
        printf("First write returned fail.\n");
        return -1;
    }

    if (ioctl(uinp_fd, UI_DEV_CREATE)) {
        printf("ioctl UI_DEV_CREATE returned fail.\n");
        return -1;
    }

    return 1;
}

void write_event_to_device(MS_U8 u8KeyCode, MS_U8 u8Repeat) {
    struct input_event event; // Input device structure
    struct timespec s;
    s.tv_nsec = 5000000L;
    s.tv_sec = 0;

    memset(&event, 0x00, sizeof(event));
    gettimeofday(&event.time, NULL);
    event.type = EV_KEY;
    event.code = u8KeyCode;
    event.value = 1;
    write(uinp_fd, &event, sizeof(event));

    if (u8Repeat == 0) {
        memset(&event, 0x00, sizeof(event));
        gettimeofday(&event.time, NULL);
        event.type = EV_KEY;
        event.code = u8KeyCode;
        event.value = 0;
        write(uinp_fd, &event, sizeof(event));
    }

    memset(&event, 0x00, sizeof(event));
    gettimeofday(&event.time, NULL);
    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;
    write(uinp_fd, &event, sizeof(event));
}

//-----------------------------------------------------------------------------
// Function: ParseHexSetToArray
// Describion: Parse the hex set to an array
//    For example:
//    char * HexSet = "{0x01, 0x02, 0x03}";
//    parse to
//    U8 array[3] = {0x01, 0x02, 0x03};
//-----------------------------------------------------------------------------
BOOL ParseHexSetToArray(char *aHexSet, int iArrayNum, unsigned char *u8OutArray) {
    char *pFirst = NULL;

    pFirst = strchr(aHexSet, '{');

    /* check the first character '{' is found */
    if (pFirst == NULL)
        return FALSE;

    pFirst++;

    char *pStep = NULL;
    int iParseNum = 0;

    pStep = strtok(pFirst, ",");
    while ( (iParseNum < iArrayNum) && (pStep) ) {
        /* e.g. convert "0xA0" to integer */
        u8OutArray[iParseNum] = (unsigned char)strtol(pStep, NULL, 0);
        iParseNum++;
        pStep = strtok(NULL, ",");
    }

    /* check the parsing number is matched */
    if (iParseNum != iArrayNum)
        return FALSE;

    return TRUE;
}

//-------------------------------------------------------------------------------------------------
/// Load Keypad information from ini file. . It is called by SetKEYPADCfg.
/// @param  pKeypadInfo     Keypad configuration table
/// @param  pDict   INI parser pointer
/// @param iExtNum  Select external keypad no. (1~4)
/// @return                 \b OUT: true or false
//-------------------------------------------------------------------------------------------------
BOOL LoadKEYPADInfo(SARKpdRegCfg *pKeypadInfo, void *pDict, int iExtNum) {
    int i = 0;
    int iCh = 0;
    /* support MAX extend number is 4 */
    if (iExtNum > 4)
        return FALSE;

    dictionary *pIni = (dictionary *)pDict;

    char aIniKey[MAX_KEYPAD_BUFFER + 1];

    for (i = 0; i < (MAX_KEYPAD_BUFFER + 1); i++)
        aIniKey[i] = 0;

    /* start to load Keypad Info for all channels */
    for (i = 0; i < MAX_KEYPAD_CH; i++) {
        /* the channel index is equal to i + 1 */
        iCh = i + 1;

        /* reset the pKeypadInfo[i] data */
        memset(&pKeypadInfo[i], 0, sizeof(SARKpdRegCfg));

        snprintf(aIniKey, MAX_KEYPAD_BUFFER, "Keypad_Ext_%d:bEnable_%d", iExtNum, iCh);
        pKeypadInfo[i].bEnable = iniparser_getboolean(pIni, aIniKey, -1);

        snprintf(aIniKey, MAX_KEYPAD_BUFFER, "Keypad_Ext_%d:u8SARChID_%d", iExtNum, iCh);
        pKeypadInfo[i].u8SARChID = iniparser_getint(pIni, aIniKey, -1);

        snprintf(aIniKey, MAX_KEYPAD_BUFFER, "Keypad_Ext_%d:u8SARChUpBnd_%d", iExtNum, iCh);
        pKeypadInfo[i].tSARChBnd.u8UpBnd = iniparser_getint(pIni, aIniKey, -1);

        snprintf(aIniKey, MAX_KEYPAD_BUFFER, "Keypad_Ext_%d:u8SARChLoBnd_%d", iExtNum, iCh);
        pKeypadInfo[i].tSARChBnd.u8LoBnd = iniparser_getint(pIni, aIniKey, -1);

        snprintf(aIniKey, MAX_KEYPAD_BUFFER, "Keypad_Ext_%d:u8KeyLevelNum_%d", iExtNum, iCh);
        pKeypadInfo[i].u8KeyLevelNum = iniparser_getint(pIni, aIniKey, -1);

        snprintf(aIniKey, MAX_KEYPAD_BUFFER, "Keypad_Ext_%d:u8KeyThreshold_%d", iExtNum, iCh);
        char *pKeyThreshold = iniparser_getstring(pIni, aIniKey, NULL);


        if (pKeypadInfo[i].bEnable != FALSE) {//check
            /* check the hex set (KeyThreshold info) */
            if ((pKeyThreshold == NULL) || (strlen(pKeyThreshold) == 0)) {
                ALOGE("fuck Get KeyThreshold error!\n");
                ALOGE("i=%d!\n",i);
                return 0;
            } else {
                /* parse the hex set to KeyThreshold[] */
                if (ParseHexSetToArray(pKeyThreshold, 8, pKeypadInfo[i].u8KeyThreshold) == FALSE) {
                    ALOGE("Parse KeyThreshold error!\n");
                    return 0;
                }
            }

            snprintf(aIniKey, MAX_KEYPAD_BUFFER, "Keypad_Ext_%d:u8KeyCode_%d", iExtNum, iCh);
            char *pKeyCode = iniparser_getstring(pIni, aIniKey, NULL);

            /* check the hex set (KeyCode info) */
            if ((pKeyCode == NULL) || ((strlen(pKeyCode)) == 0)) {
                ALOGE("Get KeyCode error!\n");
                return 0;
            } else {
                /* parse the hex set to KeyCode[] */
                if (ParseHexSetToArray(pKeyCode, 8, pKeypadInfo[i].u8KeyCode) == FALSE) {
                    ALOGE("Parse KeyCode error!\n");
                    return 0;
                }
            }
        }
    }

    return TRUE;
}

int main()
{
    //read keypad form Customer_x.ini
    //Customer_x.ini is related to model_x (x=project-id)
    dictionary *sysINI = NULL, *modelINI = NULL, *panelINI = NULL;
    MDrv_SYS_GlobalInit();
    mdrv_gpio_init();

    int ret = 0;
    ret = tvini_init(&sysINI, &modelINI, &panelINI);
    if (ret == -1) {
        ALOGE("tvini_init: get ini fail!!!\n");
        return -1;
    }

    int iKeypadSelect = iniparser_getint(modelINI, "KEYPAD:keypadSelect", -1);
    //iKeypadSelect =1 ;//test
    if (iKeypadSelect > 0) {
        if (LoadKEYPADInfo(BOARD_KPD, modelINI, iKeypadSelect) != TRUE) {//test
            ALOGE("Load KeyPad failed!\n");
        }
    }
    tvini_free(sysINI, modelINI, panelINI);
    //   MDrv_MMIO_Init();
    MDrv_MIU_SetIOMapBase();
    SAR_KpdRegCfg SARCfg;
    MDrv_SAR_Kpd_Init();

    keypad_input_interval = KEYPAD_INPUT_NORMAL_POLLING;

    int i, j;
    for (i = 0; i < MAX_KEYPAD_CH; i++) {
        if (BOARD_KPD[i].bEnable == false) {
            continue;

        }
        SARCfg.u8SARChID = BOARD_KPD[i].u8SARChID;
        SARCfg.tSARChBnd.u8UpBnd = BOARD_KPD[i].tSARChBnd.u8UpBnd;
        SARCfg.tSARChBnd.u8LoBnd = BOARD_KPD[i].tSARChBnd.u8LoBnd;
        SARCfg.u8KeyLevelNum = BOARD_KPD[i].u8KeyLevelNum;
        for (j = 0; j < 8; j++) {
            SARCfg.u8KeyThreshold[j] = BOARD_KPD[i].u8KeyThreshold[j];
            SARCfg.u8KeyCode[j] = BOARD_KPD[i].u8KeyCode[j];
        }
        if (MDrv_SAR_Kpd_SetChInfo(&SARCfg) == E_SAR_KPD_FAIL) {
            ALOGV("DFBInfo MDrv_SAR_Config: CH_%d fails\n", i);
            return FALSE;
        }
    }

    stKeypadDeviceData *pKpdData = (stKeypadDeviceData*) malloc(sizeof(stKeypadDeviceData));
    pKpdData->min_keycode = MIN_KEYPAD_VALUE;
    pKpdData->max_keycode = MAX_KEYPAD_VALUE;

    if (setup_uinput_device(pKpdData) < 0) {
        printf("Unable to find uInput device\n");
        free(pKpdData);
        return -1;
    }

    pthread_attr_t attr;
    pthread_attr_init(&attr);
    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
    if (0 != pthread_create(&keypad_EventThreadId, &attr, keypad_EventThread, (void *)NULL)) {
        printf("Create KeypadEventThread Failed!!\n");
        exit(1);
    }

    // Coverity server need set to ignore this.
    while (1) {
        usleep(1000000);  // sleep 1 second
    }

    free(pKpdData);
    pKpdData = NULL;

    // Destroy the device
    ioctl(uinp_fd, UI_DEV_DESTROY);

    close(uinp_fd);
    return 0;
}
