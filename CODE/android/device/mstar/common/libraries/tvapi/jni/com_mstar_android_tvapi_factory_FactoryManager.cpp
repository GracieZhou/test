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

#define LOG_TAG "FactoryManager_JNI"
#include <utils/Log.h>

#include <stdio.h>
#include <utils/threads.h>
#include "jni.h"
#include "JNIHelp.h"
#include "android_runtime/AndroidRuntime.h"
#include "factorymanager/FactoryManager.h"

using namespace android;

class JNIMSrvListener: public FactoryManagerListener {
public:
    JNIMSrvListener(JNIEnv *env, jobject thiz, jobject weak_thiz);
    ~JNIMSrvListener();
    void notify(int32_t msgType, int32_t ext1, int32_t ext2);
    void notify(int event, int ext1, int ext2, jobject obj);
    //void PostEvent_Template(int32_t nEvt,int32_t ext1, int32_t ext2);
    void PostEvent_Template(int32_t ext1, int32_t ext2);
    void PostEvent_SnServiceDeadth(int32_t ext1, int32_t ext2);
private:
    JNIMSrvListener();
    jclass      mClass;     // Reference to MSrv class
    jobject     mObject;    // Weak ref to MSrv Java object to call on
};

struct fields_t {
    jfieldID    context;
    jfieldID    player;
    jmethodID   post_event;
    jmethodID   post_event_snservicedeadth;
};

static fields_t fields;
static Mutex sLock;

static sp<FactoryManager> setFactoryManager(JNIEnv *env, jobject thiz, const sp<FactoryManager> &srv) {
    Mutex::Autolock l(sLock);
    sp<FactoryManager> old = (FactoryManager *)env->GetLongField(thiz, fields.context);
    if (srv.get()) {
        srv->incStrong(thiz);
    }
    if (old != 0) {
        old->decStrong(thiz);
    }
    env->SetLongField(thiz, fields.context, (jlong)srv.get());
    return old;
}

static sp<FactoryManager> getFactoryManager(JNIEnv *env, jobject thiz) {
    //return factoryManager;
    Mutex::Autolock l(sLock);
    FactoryManager *const p = (FactoryManager *)env->GetLongField(thiz, fields.context);
    return sp<FactoryManager>(p);
}

JNIMSrvListener::JNIMSrvListener(JNIEnv *env, jobject thiz, jobject weak_thiz) {
    // Hold onto the MediaPlayer class for use in calling the static method
    // that posts events to the application thread.
    jclass clazz = env->GetObjectClass(thiz);
    if (clazz == NULL) {
        ALOGE("Can't find com/mstar/android/tvapi/factory/FactoryManager");
        jniThrowException(env, "java/lang/Exception", NULL);
        return;
    }
    mClass = (jclass)env->NewGlobalRef(clazz);

    // We use a weak reference so the App object can be garbage collected.
    // The reference is only used as a proxy for callbacks.
    mObject  = env->NewGlobalRef(weak_thiz);
}

JNIMSrvListener::~JNIMSrvListener() {
    // remove global references
    JNIEnv *env = AndroidRuntime::getJNIEnv();
    env->DeleteGlobalRef(mObject);
    env->DeleteGlobalRef(mClass);
}

// TODO: for callback refactory
void JNIMSrvListener::notify(int event, int ext1, int ext2, jobject obj) {
}

void JNIMSrvListener::notify(int32_t msgType, int32_t ext1, int32_t ext2) {
    JNIEnv *env = AndroidRuntime::getJNIEnv();
    env->CallStaticVoidMethod(mClass, fields.post_event, mObject, ext1, ext2, 0);
}

void JNIMSrvListener::PostEvent_Template(int32_t ext1, int32_t ext2) {}

void JNIMSrvListener::PostEvent_SnServiceDeadth(int32_t ext1, int32_t ext2) {
    /*
       JNIEnv* env = AndroidRuntime::getJNIEnv();
       sp<FactoryManager> srv = setFactoryManager(env, mObject, 0);
       if (srv != NULL) {
           // this prevents native callbacks after the object is released
           srv->setListener(0);
           srv.clear();
       }
       env->CallStaticVoidMethod(mClass, fields.post_event_snservicedeadth, mObject, ext1, ext2);
       env->DeleteGlobalRef(mObject);
       env->DeleteGlobalRef(mClass);*/
}

//--------------------------------------------------------------------------------
void com_mstar_android_tvapi_factory_FactoryManager_native_init
(JNIEnv *env) {
    ALOGI("native_init");
    jclass clazz = env->FindClass("com/mstar/android/tvapi/factory/FactoryManager");
    fields.context = env->GetFieldID(clazz, "mNativeContext", "J");
    fields.post_event = env->GetStaticMethodID(clazz, "postEventFromNative", "(Ljava/lang/Object;IIILjava/lang/Object;)V");
    fields.player = env->GetFieldID(clazz, "mFactoryManagerContext", "I");
    fields.post_event_snservicedeadth = env->GetStaticMethodID(clazz, "PostEvent_SnServiceDeadth", "(Ljava/lang/Object;II)V");
}

void com_mstar_android_tvapi_factory_FactoryManager_native_setup
(JNIEnv *env, jobject thiz, jobject weak_this) {
    ALOGI("native_setup");
    sp<FactoryManager> srv = FactoryManager::connect();
    if (srv == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "can't connect to factorymanager server.please check tvapi server");
        return;
    }
    // create new listener and give it to MediaPlayer
    sp<JNIMSrvListener> listener = new JNIMSrvListener(env, thiz, weak_this);
    srv->setListener(listener);

    // Stow our new C++ MSrv in an opaque field in the Java object.
    setFactoryManager(env, thiz, srv);
    /*
        // start event sender thread
        srv->start();
    */
}

void com_mstar_android_tvapi_factory_FactoryManager_native_finalize
(JNIEnv *env, jobject thiz) {
    ALOGI("native_finalize");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->disconnect();
    sp<FactoryManager> srv = setFactoryManager(env, thiz, 0);
    if (srv != NULL) {
        // this prevents native callbacks after the object is released
        srv->setListener(0);
    }
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    resetDisplayResolution
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_resetDisplayResolution
(JNIEnv *env, jobject thiz) {
    ALOGI("resetDisplayResolution");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->resetDisplayResolution();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    native_getDisplayResolution
 * Signature: ()I
 */
jint com_mstar_android_tvapi_factory_FactoryManager_getDisplayResolution
(JNIEnv *env, jobject thiz) {
    ALOGI("native_getDisplayResolution");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getDisplayResolution();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    native_setVideoTestPattern
 * Signature: (I)V
 */
void com_mstar_android_tvapi_factory_FactoryManager_setVideoTestPattern
(JNIEnv *env, jobject thiz, jint enColor) {
    ALOGI("native_setVideoTestPattern");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setVideoTestPattern(enColor);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager_setVideoMuteColor
 * Method:    setVideoMuteColor
 * Signature: (I)Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_setVideoMuteColor
(JNIEnv *env, jobject thiz, jint enColor) {
    ALOGI("setVideoMuteColor");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->setVideoMuteColor(enColor);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setAdcGainOffset
 * Signature: (IILcom/mstar/android/tvapi/factory/vo/PqlCalibrationData;)V
 */
void com_mstar_android_tvapi_factory_FactoryManager_native_setAdcGainOffset
(JNIEnv *env, jobject thiz, jint enWin, jint eAdcIndex, jobject stADCGainOffset) {
    ALOGI("setAdcGainOffset");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    if (NULL == stADCGainOffset) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvCommonException", "object parameter can not be null");
        return;
    }

    jclass jclass_PqlCalibrationData = env->FindClass("com/mstar/android/tvapi/factory/vo/PqlCalibrationData");
    jfieldID jclass_PqlCalibrationData_redGain = env->GetFieldID(jclass_PqlCalibrationData, "redGain", "I");
    jfieldID jclass_PqlCalibrationData_greenGain = env->GetFieldID(jclass_PqlCalibrationData, "greenGain", "I");
    jfieldID jclass_PqlCalibrationData_blueGain = env->GetFieldID(jclass_PqlCalibrationData, "blueGain", "I");
    jfieldID jclass_PqlCalibrationData_redOffset = env->GetFieldID(jclass_PqlCalibrationData, "redOffset", "I");
    jfieldID jclass_PqlCalibrationData_greenOffset = env->GetFieldID(jclass_PqlCalibrationData, "greenOffset", "I");
    jfieldID jclass_PqlCalibrationData_blueOffset = env->GetFieldID(jclass_PqlCalibrationData, "blueOffset", "I");

    PqlCalibrationData stADCGainOffset_binder;
    stADCGainOffset_binder.blueGain = env->GetIntField(stADCGainOffset, jclass_PqlCalibrationData_blueGain);
    stADCGainOffset_binder.blueOffset = env->GetIntField(stADCGainOffset, jclass_PqlCalibrationData_blueOffset);
    stADCGainOffset_binder.greenGain = env->GetIntField(stADCGainOffset, jclass_PqlCalibrationData_greenGain);
    stADCGainOffset_binder.greenOffset = env->GetIntField(stADCGainOffset, jclass_PqlCalibrationData_greenOffset);
    stADCGainOffset_binder.redGain = env->GetIntField(stADCGainOffset, jclass_PqlCalibrationData_redGain);
    stADCGainOffset_binder.redOffset = env->GetIntField(stADCGainOffset, jclass_PqlCalibrationData_redOffset);
    ms->setAdcGainOffset(enWin, eAdcIndex, stADCGainOffset_binder);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getAdcGainOffset
 * Signature: (II)Lcom/mstar/android/tvapi/factory/vo/PqlCalibrationData;
 */
jobject com_mstar_android_tvapi_factory_FactoryManager_native_getAdcGainOffset
(JNIEnv *env, jobject thiz, jint enWin, jint eAdcIndex) {
    ALOGI("getAdcGainOffset");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }

    jclass jclass_PqlCalibrationData = env->FindClass("com/mstar/android/tvapi/factory/vo/PqlCalibrationData");
    jfieldID jclass_PqlCalibrationData_redGain = env->GetFieldID(jclass_PqlCalibrationData, "redGain", "I");
    jfieldID jclass_PqlCalibrationData_greenGain = env->GetFieldID(jclass_PqlCalibrationData, "greenGain", "I");
    jfieldID jclass_PqlCalibrationData_blueGain = env->GetFieldID(jclass_PqlCalibrationData, "blueGain", "I");
    jfieldID jclass_PqlCalibrationData_redOffset = env->GetFieldID(jclass_PqlCalibrationData, "redOffset", "I");
    jfieldID jclass_PqlCalibrationData_greenOffset = env->GetFieldID(jclass_PqlCalibrationData, "greenOffset", "I");
    jfieldID jclass_PqlCalibrationData_blueOffset = env->GetFieldID(jclass_PqlCalibrationData, "blueOffset", "I");

    PqlCalibrationData stADCGainOffset_binder;
    ms->getAdcGainOffset(enWin, eAdcIndex, stADCGainOffset_binder);
    jobject stADCGainOffset = env->AllocObject(jclass_PqlCalibrationData);
    env->SetIntField(stADCGainOffset, jclass_PqlCalibrationData_blueGain, stADCGainOffset_binder.blueGain);
    env->SetIntField(stADCGainOffset, jclass_PqlCalibrationData_blueOffset, stADCGainOffset_binder.blueOffset);
    env->SetIntField(stADCGainOffset, jclass_PqlCalibrationData_greenGain, stADCGainOffset_binder.greenGain);
    env->SetIntField(stADCGainOffset, jclass_PqlCalibrationData_greenOffset, stADCGainOffset_binder.greenOffset);
    env->SetIntField(stADCGainOffset, jclass_PqlCalibrationData_redGain, stADCGainOffset_binder.redGain);
    env->SetIntField(stADCGainOffset, jclass_PqlCalibrationData_redOffset, stADCGainOffset_binder.redOffset);
    return stADCGainOffset;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    autoAdc
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_autoAdc
(JNIEnv *env, jobject thiz) {
    ALOGI("autoAdc");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->autoAdc();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setBrightness
 * Signature: (S)Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_setBrightness
(JNIEnv *env, jobject thiz, jshort subBrightness) {
    ALOGI("setBrightness");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->setBrightness(subBrightness);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setContrast
 * Signature: (S)Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_setContrast
(JNIEnv *env, jobject thiz, jshort subContrast) {
    ALOGI("setContrast");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "java/lang/IllegalStateException", NULL);
        return 0;
    }
    return ms->setContrast(subContrast);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setSaturation
 * Signature: (S)Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_setSaturation
(JNIEnv *env, jobject thiz, jshort saturation) {
    ALOGI("setSaturation");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->setSaturation(saturation);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setSharpness
 * Signature: (S)Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_setSharpness
(JNIEnv *env, jobject thiz, jshort sharpness) {
    ALOGI("setSharpness");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->setSharpness(sharpness);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setHue
 * Signature: (S)Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_setHue
(JNIEnv *env, jobject thiz, jshort hue) {
    ALOGI("setHue");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->setHue(hue);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getPictureModeValue
 * Signature: ()Lcom/mstar/android/tvapi/factory/vo/PictureModeValue
 */
jobject com_mstar_android_tvapi_factory_FactoryManager_getPictureModeValue
(JNIEnv *env, jobject thiz) {
    ALOGI("setHue");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }
    PictureModeValue stPictureModeValue;
    ms->getPictureModeValue(stPictureModeValue);

    jclass jclass_PictureModeValue = env->FindClass("com/mstar/android/tvapi/factory/vo/PictureModeValue");
    jfieldID jclass_PictureModeValue_brightness = env->GetFieldID(jclass_PictureModeValue, "brightness", "S");
    jfieldID jclass_PictureModeValue_contrast = env->GetFieldID(jclass_PictureModeValue, "contrast", "S");
    jfieldID jclass_PictureModeValue_saturation = env->GetFieldID(jclass_PictureModeValue, "saturation", "S");
    jfieldID jclass_PictureModeValue_sharpness = env->GetFieldID(jclass_PictureModeValue, "sharpness", "S");
    jfieldID jclass_PictureModeValue_hue = env->GetFieldID(jclass_PictureModeValue, "hue", "S");

    jobject jobject_PictureModeValue = env->AllocObject(jclass_PictureModeValue);
    env->SetShortField(jobject_PictureModeValue, jclass_PictureModeValue_brightness, stPictureModeValue.brightness);
    env->SetShortField(jobject_PictureModeValue, jclass_PictureModeValue_contrast, stPictureModeValue.contrast);
    env->SetShortField(jobject_PictureModeValue, jclass_PictureModeValue_saturation, stPictureModeValue.saturation);
    env->SetShortField(jobject_PictureModeValue, jclass_PictureModeValue_sharpness, stPictureModeValue.sharpness);
    env->SetShortField(jobject_PictureModeValue, jclass_PictureModeValue_hue, stPictureModeValue.hue);
    return jobject_PictureModeValue;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    copySubColorDataToAllSource
 * Signature: ()V
 */
void com_mstar_android_tvapi_factory_FactoryManager_copySubColorDataToAllSource
(JNIEnv *env, jobject thiz) {
    ALOGI("copySubColorDataToAllSource");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->copySubColorDataToAllSource();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setWbGainOffset
 * Signature: (ISSSSSS)V
 */
void com_mstar_android_tvapi_factory_FactoryManager_native_setWbGainOffset
(JNIEnv *env, jobject thiz, jint eColorTemp, jshort redGain, jshort greenGain, \
 jshort blueGain, jshort redOffset, jshort greenOffset, jshort blueOffset) {
    ALOGI("setWbGainOffset");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setWbGainOffset(eColorTemp, redGain, greenGain, blueGain, redOffset, greenOffset, blueOffset);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setWbGainOffsetEx
 * Signature: (IIIIIIII)V
 */
void com_mstar_android_tvapi_factory_FactoryManager_native_setWbGainOffsetEx
(JNIEnv *env, jobject thiz, jint eColorTemp, jint redGain, jint greenGain, \
 jint blueGain, jint redOffset, jint greenOffset, jint blueOffset, jint enSrcType) {
    ALOGI("setWbGainOffsetEx");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setWbGainOffsetEx(eColorTemp, redGain, greenGain, blueGain, redOffset, greenOffset, blueOffset, enSrcType);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getWbGainOffset
 * Signature: (I)Lcom/mstar/android/tvapi/factory/vo/WbGainOffset
 */
jobject com_mstar_android_tvapi_factory_FactoryManager_naitve_getWbGainOffset
(JNIEnv *env, jobject thiz, jint eColorTemp) {
    ALOGI("getWBGainOffset");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }
    WbGainOffset GainOffset;
    ms->getWbGainOffset(eColorTemp, GainOffset);

    jclass jclass_WbGainOffset = env->FindClass("com/mstar/android/tvapi/factory/vo/WbGainOffset");
    jfieldID jclass_WbGainOffset_redGain = env->GetFieldID(jclass_WbGainOffset, "redGain", "S");
    jfieldID jclass_WbGainOffset_greenGain = env->GetFieldID(jclass_WbGainOffset, "greenGain", "S");
    jfieldID jclass_WbGainOffset_blueGain = env->GetFieldID(jclass_WbGainOffset, "blueGain", "S");
    jfieldID jclass_WbGainOffset_redOffset = env->GetFieldID(jclass_WbGainOffset, "redOffset", "S");
    jfieldID jclass_WbGainOffset_greenOffset = env->GetFieldID(jclass_WbGainOffset, "greenOffset", "S");
    jfieldID jclass_WbGainOffset_blueOffset = env->GetFieldID(jclass_WbGainOffset, "blueOffset", "S");

    jobject jobject_WbGainOffset = env->AllocObject(jclass_WbGainOffset);
    env->SetShortField(jobject_WbGainOffset, jclass_WbGainOffset_redGain, GainOffset.redGain);
    env->SetShortField(jobject_WbGainOffset, jclass_WbGainOffset_greenGain, GainOffset.greenGain);
    env->SetShortField(jobject_WbGainOffset, jclass_WbGainOffset_blueGain, GainOffset.blueGain);
    env->SetShortField(jobject_WbGainOffset, jclass_WbGainOffset_redOffset, GainOffset.redOffset);
    env->SetShortField(jobject_WbGainOffset, jclass_WbGainOffset_greenOffset, GainOffset.greenOffset);
    env->SetShortField(jobject_WbGainOffset, jclass_WbGainOffset_blueOffset, GainOffset.blueOffset);
    return jobject_WbGainOffset;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getWbGainOffsetEx
 * Signature: (II)Lcom/mstar/android/tvapi/factory/vo/WbGainOffsetEx
 */
jobject com_mstar_android_tvapi_factory_FactoryManager_native_getWbGainOffsetEx
(JNIEnv *env, jobject thiz, jint eColorTemp, jint enSrcType) {
    ALOGI("getWbGainOffsetEx");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }
    WbGainOffsetEx GainOffset;
    ms->getWbGainOffsetEx(eColorTemp, enSrcType, GainOffset);

    jclass jclass_WbGainOffsetEx = env->FindClass("com/mstar/android/tvapi/factory/vo/WbGainOffsetEx");
    jfieldID jclass_WbGainOffsetEx_redGain = env->GetFieldID(jclass_WbGainOffsetEx, "redGain", "I");
    jfieldID jclass_WbGainOffsetEx_greenGain = env->GetFieldID(jclass_WbGainOffsetEx, "greenGain", "I");
    jfieldID jclass_WbGainOffsetEx_blueGain = env->GetFieldID(jclass_WbGainOffsetEx, "blueGain", "I");
    jfieldID jclass_WbGainOffsetEx_redOffset = env->GetFieldID(jclass_WbGainOffsetEx, "redOffset", "I");
    jfieldID jclass_WbGainOffsetEx_greenOffset = env->GetFieldID(jclass_WbGainOffsetEx, "greenOffset", "I");
    jfieldID jclass_WbGainOffsetEx_blueOffset = env->GetFieldID(jclass_WbGainOffsetEx, "blueOffset", "I");

    jobject jobject_WbGainOffsetEx = env->AllocObject(jclass_WbGainOffsetEx);
    env->SetIntField(jobject_WbGainOffsetEx, jclass_WbGainOffsetEx_redGain, GainOffset.redGain);
    env->SetIntField(jobject_WbGainOffsetEx, jclass_WbGainOffsetEx_greenGain, GainOffset.greenGain);
    env->SetIntField(jobject_WbGainOffsetEx, jclass_WbGainOffsetEx_blueGain, GainOffset.blueGain);
    env->SetIntField(jobject_WbGainOffsetEx, jclass_WbGainOffsetEx_redOffset, GainOffset.redOffset);
    env->SetIntField(jobject_WbGainOffsetEx, jclass_WbGainOffsetEx_greenOffset, GainOffset.greenOffset);
    env->SetIntField(jobject_WbGainOffsetEx, jclass_WbGainOffsetEx_blueOffset, GainOffset.blueOffset);
    return jobject_WbGainOffsetEx;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    copyWhiteBalanceSettingToAllSource
 * Signature: ()V
 */
void com_mstar_android_tvapi_factory_FactoryManager_copyWhiteBalanceSettingToAllSource
(JNIEnv *env, jobject thiz) {
    ALOGI("copyWhiteBalanceSettingToAllSource");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->copyWhiteBalanceSettingToAllSource();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getQmapIpNum
 * Signature: ()I
 */
jint com_mstar_android_tvapi_factory_FactoryManager_getQmapIpNum
(JNIEnv *env, jobject thiz) {
    ALOGI("getQmapIpNum");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getQmapIpNum();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getQmapTableNum
 * Signature: (S)I
 */
jint com_mstar_android_tvapi_factory_FactoryManager_getQmapTableNum
(JNIEnv *env, jobject thiz, jshort ipIndex) {
    ALOGI("getQmapTableNum");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getQmapTableNum(ipIndex);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getQmapCurrentTableIdx
 * Signature: (S)I
 */
jint com_mstar_android_tvapi_factory_FactoryManager_getQmapCurrentTableIdx
(JNIEnv *env, jobject thiz, jshort ipIndex) {
    ALOGI("getQmapCurrentTableIdx");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getQmapCurrentTableIdx(ipIndex);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    native_getPQVersion
 * Signature: (I)Ljava/lang/String;
 */
jstring com_mstar_android_tvapi_factory_FactoryManager_native_getPQVersion
(JNIEnv *env, jobject thiz, jint escalerwindow) {
    ALOGI("getPQVersion");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }
    String8 name = ms->getPQVersion(escalerwindow);
    jstring jstring_name = env->NewStringUTF(name.string());
    return jstring_name;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getQmapIpName
 * Signature: (S)Ljava/lang/String;
 */
jstring com_mstar_android_tvapi_factory_FactoryManager_getQmapIpName
(JNIEnv *env, jobject thiz, jshort ipIndex) {
    ALOGI("getQmapIpName");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }
    String8 name = ms->getQmapIpName(ipIndex);
    jstring jstring_name = env->NewStringUTF(name.string());
    return jstring_name;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getQmapTableName
 * Signature: (SS)Ljava/lang/String;
 */
jstring com_mstar_android_tvapi_factory_FactoryManager_getQmapTableName
(JNIEnv *env, jobject thiz, jshort ipIndex, jshort tableIndex) {
    ALOGI("getQmapTableName");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }
    String8 name = ms->getQmapTableName(ipIndex, tableIndex);
    jstring jstring_name = env->NewStringUTF(name.string());
    return jstring_name;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    loadPqTable
 * Signature: (II)V
 */
void com_mstar_android_tvapi_factory_FactoryManager_loadPqTable
(JNIEnv *env, jobject thiz, jint tableIndex, jint ipIndex) {
    ALOGI("loadPqTable");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->loadPqTable(tableIndex, ipIndex);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    isWdtOn
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_isWdtOn
(JNIEnv *env, jobject thiz) {
    ALOGI("isWdtOn");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->isWdtOn();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    enableWdt
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_enableWdt
(JNIEnv *env, jobject thiz) {
    ALOGI("enableWdt");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->enableWdt();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    disableWdt
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_disableWdt
(JNIEnv *env, jobject thiz) {
    ALOGI("disableWdt");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->disableWdt();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    isUartOn
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_isUartOn
(JNIEnv *env, jobject thiz) {
    ALOGI("isUartOn");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->isUartOn();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    isAgingModeOn
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_isAgingModeOn
(JNIEnv *env, jobject thiz) {
    ALOGI("isAgingModeOn");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->isAgingModeOn();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    enableUart
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_enableUart
(JNIEnv *env, jobject thiz) {
    ALOGI("enableUart");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->enableUart();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    disableUart
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_disableUart
(JNIEnv *env, jobject thiz) {
    ALOGI("disableUart");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->disableUart();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    storeDbToUsb
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_storeDbToUsb
(JNIEnv *env, jobject thiz) {
    ALOGI("storeDbToUsb");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->storeDbToUsb();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    restoreDbFromUsb
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_restoreDbFromUsb
(JNIEnv *env, jobject thiz) {
    ALOGI("restoreDbFromUsb");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->restoreDbFromUsb();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getUartEnv
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_getUartEnv
(JNIEnv *env, jobject thiz) {
    ALOGI("getUartEnv");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->getUartEnv();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setUartEnv
 * Signature: (Z)V
 */
void com_mstar_android_tvapi_factory_FactoryManager_setUartEnv
(JNIEnv *env, jobject thiz, jboolean on) {
    ALOGI("setUartEnv");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setUartEnv(on);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setFactoryVdParameter
 * Signature: (Lcom/mstar/android/tvapi/factory/vo/FactoryNsVdSet;)V
 */
void com_mstar_android_tvapi_factory_FactoryManager_setFactoryVdParameter
(JNIEnv *env, jobject thiz, jobject factoryNsVdSetVo) {
    ALOGI("setFactoryVdParameter");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    if (NULL == factoryNsVdSetVo) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvCommonException", "object parameter can not be null");
        return;
    }

    jclass jclass_FactoryNsVdSet = env->FindClass("com/mstar/android/tvapi/factory/vo/FactoryNsVdSet");
    jfieldID jclass_FactoryNsVdSet_aFEC_D4 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_D4", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_D8_Bit3210 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_D8_Bit3210", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_D5_Bit2 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_D5_Bit2", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_D7_LOW_BOUND = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_D7_LOW_BOUND", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_D7_HIGH_BOUND = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_D7_HIGH_BOUND", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_D9_Bit0 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_D9_Bit0", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_A0 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_A0", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_A1 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_A1", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_66_Bit76 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_66_Bit76", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_6E_Bit7654 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_6E_Bit7654", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_6E_Bit3210 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_6E_Bit3210", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_43 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_43", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_44 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_44", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_CB = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_CB", "S");

    jfieldID jclass_FactoryNsVdSet_aFEC_CF_Bit2_ATV = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_CF_Bit2_ATV", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_CF_Bit2_AV = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_CF_Bit2_AV", "S");

    FactoryNsVdSet stfactoryNsVdSetVo;
    stfactoryNsVdSetVo.aFEC_43 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_43);
    stfactoryNsVdSetVo.aFEC_44 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_44);
    stfactoryNsVdSetVo.aFEC_66_Bit76 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_66_Bit76);
    stfactoryNsVdSetVo.aFEC_6E_Bit3210 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_6E_Bit3210);
    stfactoryNsVdSetVo.aFEC_6E_Bit7654 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_6E_Bit7654);
    stfactoryNsVdSetVo.aFEC_A0 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_A0);
    stfactoryNsVdSetVo.aFEC_A1 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_A1);
    stfactoryNsVdSetVo.aFEC_CB = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_CB);
    stfactoryNsVdSetVo.aFEC_D4 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_D4);
    stfactoryNsVdSetVo.aFEC_D5_Bit2 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_D5_Bit2);
    stfactoryNsVdSetVo.aFEC_D7_HIGH_BOUND = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_D7_HIGH_BOUND);
    stfactoryNsVdSetVo.aFEC_D7_LOW_BOUND = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_D7_LOW_BOUND);
    stfactoryNsVdSetVo.aFEC_D8_Bit3210 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_D8_Bit3210);
    stfactoryNsVdSetVo.aFEC_D9_Bit0 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_D9_Bit0);
    stfactoryNsVdSetVo.aFEC_CF_Bit2_ATV = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_CF_Bit2_ATV);
    stfactoryNsVdSetVo.aFEC_CF_Bit2_AV = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_CF_Bit2_AV);

    ms->setFactoryVDParameter(stfactoryNsVdSetVo);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setFactoryVdInitParameter
 * Signature: (Lcom/mstar/android/tvapi/factory/vo/FactoryNsVdSet;)V
 */
void com_mstar_android_tvapi_factory_FactoryManager_setFactoryVdInitParameter
(JNIEnv *env, jobject thiz, jobject factoryNsVdSetVo) {
    ALOGI("setFactoryVdInitParameter");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    if (NULL == factoryNsVdSetVo) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvCommonException", "object parameter can not be null");
        return;
    }

    jclass jclass_FactoryNsVdSet = env->FindClass("com/mstar/android/tvapi/factory/vo/FactoryNsVdSet");
    jfieldID jclass_FactoryNsVdSet_aFEC_D4 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_D4", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_D8_Bit3210 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_D8_Bit3210", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_D5_Bit2 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_D5_Bit2", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_D7_LOW_BOUND = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_D7_LOW_BOUND", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_D7_HIGH_BOUND = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_D7_HIGH_BOUND", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_D9_Bit0 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_D9_Bit0", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_A0 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_A0", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_A1 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_A1", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_66_Bit76 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_66_Bit76", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_6E_Bit7654 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_6E_Bit7654", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_6E_Bit3210 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_6E_Bit3210", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_43 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_43", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_44 = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_44", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_CB = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_CB", "S");

    jfieldID jclass_FactoryNsVdSet_aFEC_CF_Bit2_ATV = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_CF_Bit2_ATV", "S");
    jfieldID jclass_FactoryNsVdSet_aFEC_CF_Bit2_AV = env->GetFieldID(jclass_FactoryNsVdSet, "aFEC_CF_Bit2_AV", "S");

    FactoryNsVdSet stfactoryNsVdSetVo;
    stfactoryNsVdSetVo.aFEC_43 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_43);
    stfactoryNsVdSetVo.aFEC_44 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_44);
    stfactoryNsVdSetVo.aFEC_66_Bit76 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_66_Bit76);
    stfactoryNsVdSetVo.aFEC_6E_Bit3210 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_6E_Bit3210);
    stfactoryNsVdSetVo.aFEC_6E_Bit7654 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_6E_Bit7654);
    stfactoryNsVdSetVo.aFEC_A0 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_A0);
    stfactoryNsVdSetVo.aFEC_A1 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_A1);
    stfactoryNsVdSetVo.aFEC_CB = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_CB);
    stfactoryNsVdSetVo.aFEC_D4 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_D4);
    stfactoryNsVdSetVo.aFEC_D5_Bit2 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_D5_Bit2);
    stfactoryNsVdSetVo.aFEC_D7_HIGH_BOUND = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_D7_HIGH_BOUND);
    stfactoryNsVdSetVo.aFEC_D7_LOW_BOUND = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_D7_LOW_BOUND);
    stfactoryNsVdSetVo.aFEC_D8_Bit3210 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_D8_Bit3210);
    stfactoryNsVdSetVo.aFEC_D9_Bit0 = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_D9_Bit0);

    stfactoryNsVdSetVo.aFEC_CF_Bit2_ATV = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_CF_Bit2_ATV);
    stfactoryNsVdSetVo.aFEC_CF_Bit2_AV = env->GetShortField(factoryNsVdSetVo, jclass_FactoryNsVdSet_aFEC_CF_Bit2_AV);

    ms->setFactoryVdInitParameter(stfactoryNsVdSetVo);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    updateSscParameter
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_updateSscParameter
(JNIEnv *env, jobject thiz) {
    ALOGI("updateSscParameter");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->updateSscParameter();

}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getFwVersion
 * Signature: (I)I
 */
jint com_mstar_android_tvapi_factory_FactoryManager_native_getFwVersion
(JNIEnv *env, jobject thiz, jint mode) {
    ALOGI("getFwVersion");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getFwVersion(mode);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setDebugMode
 * Signature: (Z)V
 */
void com_mstar_android_tvapi_factory_FactoryManager_setDebugMode
(JNIEnv *env, jobject thiz, jboolean mode) {
    ALOGI("setDebugMode");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setDebugMode(mode);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getSoftwareVersion
 * Signature: (S)Ljava/lang/String;
 */
jstring com_mstar_android_tvapi_factory_FactoryManager_getSoftwareVersion
(JNIEnv *env, jobject thiz) {
    ALOGI("getSoftwareVersion");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }
    String8 version;
    ms->getSoftwareVersion(version);
    jstring jstring_version = env->NewStringUTF(version.string());
    return jstring_version;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    stopTvService
 * Signature: ()V;
 */
void com_mstar_android_tvapi_factory_FactoryManager_stopTvService
(JNIEnv *env, jobject thiz) {
    ALOGI("stopTvService");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->stopTvService();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    restoreFactoryAtvProgramTable
 * Signature: (S)V;
 */
void com_mstar_android_tvapi_factory_FactoryManager_restoreFactoryAtvProgramTable
(JNIEnv *env, jobject thiz, jshort cityIndex) {
    ALOGI("restoreFactoryAtvProgramTable");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->restoreFactoryAtvProgramTable(cityIndex);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    restoreFactoryDtvProgramTable
 * Signature: (S)V;
 */
void com_mstar_android_tvapi_factory_FactoryManager_restoreFactoryDtvProgramTable
(JNIEnv *env, jobject thiz, jshort cityIndex) {
    ALOGI("restoreFactoryAtvProgramTable");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->restoreFactoryDtvProgramTable(cityIndex);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setPQParameterViaUsbKey
 * Signature: (V)V;
 */
void com_mstar_android_tvapi_factory_FactoryManager_setPQParameterViaUsbKey
(JNIEnv *env, jobject thiz) {
    ALOGI("setPQParameterViaUsbKey");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setPQParameterViaUsbKey();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setHDCPKeyViaUsbKey
 * Signature: (V)V;
 */
void com_mstar_android_tvapi_factory_FactoryManager_setHDCPKeyViaUsbKey
(JNIEnv *env, jobject thiz) {
    ALOGI("setHDCPKeyViaUsbKey");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setHDCPKeyViaUsbKey();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setCIPlusKeyViaUsbKey
 * Signature: (V)V;
 */
void com_mstar_android_tvapi_factory_FactoryManager_setCIPlusKeyViaUsbKey
(JNIEnv *env, jobject thiz) {
    ALOGI("setCIPlusKeyViaUsbKey");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setCIPlusKeyViaUsbKey();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setMACAddrViaUsbKey
 * Signature: (V)V;
 */
void com_mstar_android_tvapi_factory_FactoryManager_setMACAddrViaUsbKey
(JNIEnv *env, jobject thiz) {
    ALOGI("setMACAddrViaUsbKey");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setMACAddrViaUsbKey();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getMACAddrString
 * Signature: ()Ljava/lang/String;
 */
jstring com_mstar_android_tvapi_factory_FactoryManager_getMACAddrString
(JNIEnv *env, jobject thiz) {
    ALOGI("getMACAddrString");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }
    String8 mac;
    ms->getMACAddrString(mac);
    return env->NewStringUTF(mac.string());
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    startUartDebug
 * Signature: ()Z;
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_startUartDebug
(JNIEnv *env, jobject thiz) {
    ALOGI("enableUartDebug");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->startUartDebug();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    uartSwitch
 * Signature: ()Z;
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_uartSwitch
(JNIEnv *env, jobject thiz) {
    ALOGI("switchUart");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return   ms->uartSwitch();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getResolutionMappingIndex
 * Signature: (I)S;
 */
jshort com_mstar_android_tvapi_factory_FactoryManager_native_getResolutionMappingIndex
(JNIEnv *env, jobject thiz, jint inputsourcenumber) {
    ALOGI("native_getResolutionMappingIndex");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getResolutionMappingIndex(inputsourcenumber);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    native_setEnvironmentPowerMode
 * Signature: (I)Z;
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_native_setEnvironmentPowerMode
(JNIEnv *env, jobject thiz, jint powerMode) {
    ALOGI("native_setEnvironmentPowerMode");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->setEnvironmentPowerMode(powerMode);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    native_getEnvironmentPowerMode
 * Signature: ()I;
 */
jint com_mstar_android_tvapi_factory_FactoryManager_native_getEnvironmentPowerMode
(JNIEnv *env, jobject thiz) {
    ALOGI("native_getEnvironmentPowerMode");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getEnvironmentPowerMode();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setEnvironmentPowerOnMusicVolume
 * Signature: (S)Z;
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_setEnvironmentPowerOnMusicVolume
(JNIEnv *env, jobject thiz, jshort volume) {
    ALOGI("setEnvironmentPowerOnMusicVolume");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->setEnvironmentPowerOnMusicVolume(volume);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getEnvironmentPowerOnMusicVolume
 * Signature: (S)Z;
 */
jshort com_mstar_android_tvapi_factory_FactoryManager_getEnvironmentPowerOnMusicVolume
(JNIEnv *env, jobject thiz) {
    ALOGI("getEnvironmentPowerOnMusicVolume");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getEnvironmentPowerOnMusicVolume();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    readBytesFromI2C
 * Signature: (I[SS)[S
 */
jobject com_mstar_android_tvapi_factory_FactoryManager_readBytesFromI2C
(JNIEnv *env, jobject thiz, jint deviceindex, jshortArray addr, jshort datasize) {
    ALOGI("readBytesFromI2C");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }

    jsize addrsize = env->GetArrayLength(addr);
    jshort *paddr = env->GetShortArrayElements(addr, NULL);
    uint8_t *addrbuffer = new uint8_t[addrsize];
    for (int i = 0; i < addrsize; i++) {
        addrbuffer[i] =  paddr[i];
    }
    env->ReleaseShortArrayElements(addr, paddr, 0);

    uint8_t *databuffer = new uint8_t[datasize];
    ms->readBytesFromI2C(deviceindex, addrsize, addrbuffer, datasize, databuffer);

    jshortArray  data = env->NewShortArray(datasize);
    jshort *pdata = env->GetShortArrayElements(data, NULL);
    for (int i = 0; i < datasize; i++) {
        pdata[i] = databuffer[i];
    }
    env->ReleaseShortArrayElements(data, pdata, 0);

    free(addrbuffer);
    free(databuffer);
    return data;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    writeBytesToI2C
 * Signature: (I[S[S)Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_writeBytesToI2C
(JNIEnv *env, jobject thiz, jint deviceindex , jshortArray addr, jshortArray data) {
    ALOGI("writeBytesToI2C");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    if ((NULL == data) || (NULL == addr)) {
        //jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvCommonException", "object parameter can not be null");
        //return false;
    }
    bool returnvalue;
    jsize datasize = 0;
    jshort *pdata = NULL;
    uint8_t *databuffer = NULL;
    jsize addrsize = 0;
    jshort *paddr = NULL;
    uint8_t *addrbuffer = NULL;
    if (NULL != data) {
        datasize = env->GetArrayLength(data);
        pdata = env->GetShortArrayElements(data, NULL);
        databuffer = new uint8_t[datasize];
        for (int i = 0; i < datasize; i++) {
            databuffer[i] =  pdata[i];
        }
        env->ReleaseShortArrayElements(data, pdata, 0);
    }

    if (NULL != addr) {
        addrsize = env->GetArrayLength(addr);
        paddr = env->GetShortArrayElements(addr, NULL);
        addrbuffer = new uint8_t[addrsize];
        for (int i = 0; i < addrsize; i++) {
            addrbuffer[i] =  paddr[i];
        }
        env->ReleaseShortArrayElements(addr, paddr, 0);
    }
    returnvalue =  ms->writeBytesToI2C(deviceindex, addrsize, addrbuffer, datasize, databuffer);
    if (databuffer != NULL) {
        free(databuffer);
    }
    if (addrbuffer != NULL) {
        free(addrbuffer);
    }
    return returnvalue;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getUpdatePqFilePath
 * Signature: (I)Ljava/lang/String;
 */
jstring com_mstar_android_tvapi_factory_FactoryManager_getUpdatePqFilePath
(JNIEnv *env, jobject thiz, jint pqUpdateFile) {
    ALOGI("getUpdatePqFilePath");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }
    String8 filepath;
    if (true != ms->getUpdatePQFilePath(pqUpdateFile, filepath)) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "get failed");
        return NULL;
    }
    jstring jstring_filepath = env->NewStringUTF(filepath.string());
    return jstring_filepath;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    UpdatePqIniFiles
 * Signature: ()V
 */
void com_mstar_android_tvapi_factory_FactoryManager_UpdatePqIniFiles
(JNIEnv *env, jobject thiz) {
    ALOGI("UpdatePqIniFiles");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->updatePQiniFiles();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    disablePVRRecordAll
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_DisablePVRRecordAll
(JNIEnv *env, jobject thiz) {
    ALOGI("DisAblePVRRecordAll");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->disablePVRRecordAll();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    enablePVRRecordAll
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_EnablePVRRecordAll
(JNIEnv *env, jobject thiz) {
    ALOGI("EnablePVRRecordAll");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }

    return ms->enablePVRRecordAll();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    isPVRRecordAllOn
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_IsPVRRecordAllOn
(JNIEnv *env, jobject thiz) {
    ALOGI("IsPVRRecordAllOn");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }

    return ms->isPVRRecordAllOn();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    ursaGetVersionInfo
 * Signature: ()Lcom/mstar/android/tvapi/factory/vo/UrsaVersionInfo;
 */
jobject com_mstar_android_tvapi_factory_FactoryManager_ursaGetVersionInfo
(JNIEnv *env, jobject thiz) {
    ALOGI("ursaGetVersionInfo");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }

    Ursa_Version_Info info;
    memset(&info, 0, sizeof(Ursa_Version_Info));
    bool ret;
    ret = ms->UrsaGetVersionInfo(&info);

    jclass jclass_UrsaVersionInfo = env->FindClass("com/mstar/android/tvapi/factory/vo/UrsaVersionInfo");
    jfieldID jfield_u16Version = env->GetFieldID(jclass_UrsaVersionInfo, "u16Version", "I");
    jfieldID jfield_u32Changelist = env->GetFieldID(jclass_UrsaVersionInfo, "u32Changelist", "J");

    jobject jobject_UrsaVersionInfo = env->AllocObject(jclass_UrsaVersionInfo);
    env->SetIntField(jobject_UrsaVersionInfo, jfield_u16Version, info.u16Version);
    env->SetLongField(jobject_UrsaVersionInfo, jfield_u32Changelist, info.u32Changelist);
    return jobject_UrsaVersionInfo;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    panelGetVersionInfo
 * Signature: ()Lcom/mstar/android/tvapi/factory/vo/PanelVersionInfo;
 */
jobject com_mstar_android_tvapi_factory_FactoryManager_panelGetVersionInfo
(JNIEnv *env, jobject thiz) {
    ALOGI("panelGetVersionInfo");
    jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvUnsupportedException", "Unsupported tvapi");
    return NULL;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setWOLEnableStatus
 * Signature: (Z)V
 */
void com_mstar_android_tvapi_factory_FactoryManager_setWOLEnableStatus
(JNIEnv *env, jobject thiz, jboolean flag) {
    ALOGI("setWOLEnableStatus");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }

    ms->setWOLEnableStatus(flag);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getWOLEnableStatus
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_getWOLEnableStatus
(JNIEnv *env, jobject thiz) {
    ALOGI("getWOLEnableStatus");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }

    return ms->getWOLEnableStatus();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setXvyccDataFromPanel
 * Signature: (FFFFFFFFI)Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_setXvyccDataFromPanel
(JNIEnv *env, jobject thiz, jfloat fRedX, jfloat fRedY, jfloat fGreenX, jfloat fGreenY, jfloat fBlueX, jfloat fBlueY, jfloat fWhiteX, jfloat fWhiteY, jint eWin) {
    ALOGI("setXvyccDataFromPanel");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }

    return ms->setXvyccDataFromPanel(fRedX, fRedY, fGreenX, fGreenY, fBlueX, fBlueY, fWhiteX, fWhiteY, eWin);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getEnableIPInfo
 * Signature: ()[B
 */
jbyteArray com_mstar_android_tvapi_factory_FactoryManager_getEnableIPInfo
(JNIEnv *env, jobject thiz) {
    ALOGI("getEnableIPInfo");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }

    jbyte buf[128];
    ms->getEnableIPInfo((uint8_t*)buf, sizeof(buf));
    jbyteArray jarray_ipinfo = env->NewByteArray(sizeof(buf));
    env->SetByteArrayRegion(jarray_ipinfo, 0, sizeof(buf), buf);
    return jarray_ipinfo;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getAutoFineGain
 * Signature: ()B
 */
jbyte com_mstar_android_tvapi_factory_FactoryManager_getAutoFineGain
(JNIEnv* env, jobject thiz){
    ALOGI("getAutoFineGain");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->getAutoFineGain();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setFixedFineGain
 * Signature: (B)Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_setFixedFineGain
(JNIEnv* env, jobject thiz, jbyte fineGain){
    ALOGI("setFixedFineGain");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->setFixedFineGain(fineGain);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    getAutoRFGain
 * Signature: ()B
 */
jbyte com_mstar_android_tvapi_factory_FactoryManager_getAutoRFGain
(JNIEnv* env, jobject thiz){
    ALOGI("getAutoRFGain");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->getAutoRFGain();
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setRFGain
 * Signature: (B)Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_setRFGain
(JNIEnv* env, jobject thiz, jbyte rfGain){
    ALOGI("setRFGain");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->setRFGain(rfGain);
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    EosSetHDCPKey
 * Signature: ([SZ)Z
 */
jboolean com_mstar_android_tvapi_factory_FactoryManager_EosSetHDCPKey
(JNIEnv *env, jobject thiz, jshortArray data, jboolean miraflag) {
    ALOGI("EosSetHDCPKey");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    if (NULL == data) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvCommonException", "object parameter can not be null");
        return false;
    }
    jsize size = env->GetArrayLength(data);
    jshort *pdata = env->GetShortArrayElements(data, NULL);
    uint8_t *buffer = new uint8_t[size];
    for (int i = 0; i < size; i++) {
        buffer[i] =  pdata[i];
    }
    env->ReleaseShortArrayElements(data, pdata, 0);

    bool re = ms->EosSetHDCPKey(buffer, size, miraflag);
    free(buffer);
    return re;
}

/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    EosGetHDCPKey
 * Signature: (IZ)[S
 */
jobject com_mstar_android_tvapi_factory_FactoryManager_EosGetHDCPKey
(JNIEnv *env, jobject thiz, jint size, jboolean miraflag) {
    ALOGI("EosGetHDCPKey");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }

    uint8_t *buffer = new uint8_t[size];
    bool re = ms->EosGetHDCPKey(buffer, size, miraflag);

    jshortArray  data = env->NewShortArray(size);
    jshort *pdata = env->GetShortArrayElements(data, NULL);
    for (int i = 0; i < size; i++) {
        pdata[i] = buffer[i];
    }
    env->ReleaseShortArrayElements(data, pdata, 0);
    free(buffer);
    return data;
}
/*
 * Class:     com_mstar_android_tvapi_factory_FactoryManager
 * Method:    setMicAssistVoice
 * Signature: ()Z
 */

jboolean com_mstar_android_tvapi_factory_FactoryManager_getTunerStatus
(JNIEnv* env, jobject thiz)
{
    ALOGI("is getTunerStatus");
    sp<FactoryManager> ms = getFactoryManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
   return ms->getTunerStatus();
}
//------------------------------------------------------------------------------------
static const char *classPathName = "com/mstar/android/tvapi/factory/FactoryManager";

static JNINativeMethod methods[] = {
    {"native_init",                   "()V", (void *)com_mstar_android_tvapi_factory_FactoryManager_native_init},
    {"native_setup",                  "(Ljava/lang/Object;)V", (void *)com_mstar_android_tvapi_factory_FactoryManager_native_setup},
    {"native_finalize",               "()V", (void *)com_mstar_android_tvapi_factory_FactoryManager_native_finalize},
    {"resetDisplayResolution",        "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_resetDisplayResolution},
    {"native_getDisplayResolution",   "()I", (void *)com_mstar_android_tvapi_factory_FactoryManager_getDisplayResolution},
    {"native_setVideoTestPattern",    "(I)V", (void *)com_mstar_android_tvapi_factory_FactoryManager_setVideoTestPattern},
    {"setVideoMuteColor",             "(I)Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_setVideoMuteColor},
    {"native_setAdcGainOffset",       "(IILcom/mstar/android/tvapi/factory/vo/PqlCalibrationData;)V", (void *)com_mstar_android_tvapi_factory_FactoryManager_native_setAdcGainOffset},
    {"native_getAdcGainOffset",       "(II)Lcom/mstar/android/tvapi/factory/vo/PqlCalibrationData;", (void *)com_mstar_android_tvapi_factory_FactoryManager_native_getAdcGainOffset},
    {"autoAdc",                       "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_autoAdc},
    {"setBrightness",                 "(S)Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_setBrightness},
    {"setContrast",                   "(S)Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_setContrast},
    {"setSaturation",                 "(S)Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_setSaturation},
    {"setSharpness",                  "(S)Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_setSharpness},
    {"setHue",                        "(S)Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_setHue},
    {"getPictureModeValue",           "()Lcom/mstar/android/tvapi/factory/vo/PictureModeValue;", (void *)com_mstar_android_tvapi_factory_FactoryManager_getPictureModeValue},
    {"copySubColorDataToAllSource",   "()V", (void *)com_mstar_android_tvapi_factory_FactoryManager_copySubColorDataToAllSource},
    {"native_setWbGainOffset",        "(ISSSSSS)V", (void *)com_mstar_android_tvapi_factory_FactoryManager_native_setWbGainOffset},
    {"native_setWbGainOffsetEx",      "(IIIIIIII)V", (void *)com_mstar_android_tvapi_factory_FactoryManager_native_setWbGainOffsetEx},
    {"naitve_getWbGainOffset",        "(I)Lcom/mstar/android/tvapi/factory/vo/WbGainOffset;", (void *)com_mstar_android_tvapi_factory_FactoryManager_naitve_getWbGainOffset},
    {"native_getWbGainOffsetEx",      "(II)Lcom/mstar/android/tvapi/factory/vo/WbGainOffsetEx;", (void *)com_mstar_android_tvapi_factory_FactoryManager_native_getWbGainOffsetEx},
    {"copyWhiteBalanceSettingToAllSource", "()V", (void *)com_mstar_android_tvapi_factory_FactoryManager_copyWhiteBalanceSettingToAllSource},
    {"getQmapIpNum",                  "()I", (void *)com_mstar_android_tvapi_factory_FactoryManager_getQmapIpNum},
    {"getQmapTableNum",               "(S)I", (void *)com_mstar_android_tvapi_factory_FactoryManager_getQmapTableNum},
    {"getQmapCurrentTableIdx",        "(S)I", (void *)com_mstar_android_tvapi_factory_FactoryManager_getQmapCurrentTableIdx},
    {"native_getPQVersion",           "(I)Ljava/lang/String;", (void *)com_mstar_android_tvapi_factory_FactoryManager_native_getPQVersion},
    {"getQmapIpName",                 "(S)Ljava/lang/String;", (void *)com_mstar_android_tvapi_factory_FactoryManager_getQmapIpName},
    {"getQmapTableName",              "(SS)Ljava/lang/String;", (void *)com_mstar_android_tvapi_factory_FactoryManager_getQmapTableName},
    {"loadPqTable",                   "(II)V", (void *)com_mstar_android_tvapi_factory_FactoryManager_loadPqTable},
    {"isWdtOn",                       "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_isWdtOn},
    {"enableWdt",                     "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_enableWdt},
    {"disableWdt",                    "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_disableWdt},
    {"isUartOn",                      "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_isUartOn},
    {"isAgingModeOn",                 "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_isAgingModeOn},
    {"enableUart",                    "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_enableUart},
    {"disableUart",                   "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_disableUart},
    {"storeDbToUsb",                  "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_storeDbToUsb},
    {"restoreDbFromUsb",              "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_restoreDbFromUsb},
    {"setFactoryVdParameter",         "(Lcom/mstar/android/tvapi/factory/vo/FactoryNsVdSet;)V", (void *)com_mstar_android_tvapi_factory_FactoryManager_setFactoryVdParameter},
    {"setFactoryVdInitParameter",     "(Lcom/mstar/android/tvapi/factory/vo/FactoryNsVdSet;)V", (void *)com_mstar_android_tvapi_factory_FactoryManager_setFactoryVdInitParameter},
    {"setUartEnv",                    "(Z)V", (void *)com_mstar_android_tvapi_factory_FactoryManager_setUartEnv},
    {"getUartEnv",                    "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_getUartEnv},
    {"updateSscParameter",            "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_updateSscParameter},
    {"native_getFwVersion",           "(I)I", (void *)com_mstar_android_tvapi_factory_FactoryManager_native_getFwVersion},
    {"setDebugMode",                  "(Z)V", (void *)com_mstar_android_tvapi_factory_FactoryManager_setDebugMode},
    {"getSoftwareVersion",            "()Ljava/lang/String;", (void *)com_mstar_android_tvapi_factory_FactoryManager_getSoftwareVersion},
    {"stopTvService",                 "()V", (void *)com_mstar_android_tvapi_factory_FactoryManager_stopTvService},
    {"restoreFactoryAtvProgramTable", "(S)V", (void *)com_mstar_android_tvapi_factory_FactoryManager_restoreFactoryAtvProgramTable},
    {"restoreFactoryDtvProgramTable", "(S)V", (void *)com_mstar_android_tvapi_factory_FactoryManager_restoreFactoryDtvProgramTable},
    {"setPQParameterViaUsbKey",       "()V", (void *)com_mstar_android_tvapi_factory_FactoryManager_setPQParameterViaUsbKey},
    {"setHDCPKeyViaUsbKey",           "()V", (void *)com_mstar_android_tvapi_factory_FactoryManager_setHDCPKeyViaUsbKey},
    {"setCIPlusKeyViaUsbKey",         "()V", (void *)com_mstar_android_tvapi_factory_FactoryManager_setCIPlusKeyViaUsbKey},
    {"setMACAddrViaUsbKey",           "()V", (void *)com_mstar_android_tvapi_factory_FactoryManager_setMACAddrViaUsbKey},
    {"getMACAddrString",              "()Ljava/lang/String;", (void *)com_mstar_android_tvapi_factory_FactoryManager_getMACAddrString},
    {"enableUartDebug",               "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_startUartDebug},
    {"startUartDebug",                "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_startUartDebug},
    {"switchUart",                    "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_uartSwitch},
    {"uartSwitch",                    "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_uartSwitch},
    {"native_getResolutionMappingIndex", "(I)S", (void *)com_mstar_android_tvapi_factory_FactoryManager_native_getResolutionMappingIndex},
    {"native_setEnvironmentPowerMode", "(I)Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_native_setEnvironmentPowerMode},
    {"native_getEnvironmentPowerMode", "()I", (void *)com_mstar_android_tvapi_factory_FactoryManager_native_getEnvironmentPowerMode},
    {"setEnvironmentPowerOnMusicVolume", "(S)Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_setEnvironmentPowerOnMusicVolume},
    {"getEnvironmentPowerOnMusicVolume", "()S", (void *)com_mstar_android_tvapi_factory_FactoryManager_getEnvironmentPowerOnMusicVolume},
    {"readBytesFromI2C",              "(I[SS)[S", (void *)com_mstar_android_tvapi_factory_FactoryManager_readBytesFromI2C},
    {"writeBytesToI2C",               "(I[S[S)Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_writeBytesToI2C},
    {"getUpdatePqFilePath",           "(I)Ljava/lang/String;", (void *)com_mstar_android_tvapi_factory_FactoryManager_getUpdatePqFilePath},
    {"updatePqIniFiles",              "()V", (void *)com_mstar_android_tvapi_factory_FactoryManager_UpdatePqIniFiles},
    {"disablePVRRecordAll",           "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_DisablePVRRecordAll},
    {"enablePVRRecordAll",            "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_EnablePVRRecordAll},
    {"isPVRRecordAllOn",              "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_IsPVRRecordAllOn},
    {"ursaGetVersionInfo",            "()Lcom/mstar/android/tvapi/factory/vo/UrsaVersionInfo;", (void *)com_mstar_android_tvapi_factory_FactoryManager_ursaGetVersionInfo},
    {"panelGetVersionInfo",           "()Lcom/mstar/android/tvapi/factory/vo/PanelVersionInfo;", (void *)com_mstar_android_tvapi_factory_FactoryManager_panelGetVersionInfo},
    {"setWOLEnableStatus",            "(Z)V", (void *)com_mstar_android_tvapi_factory_FactoryManager_setWOLEnableStatus},
    {"getWOLEnableStatus",            "()Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_getWOLEnableStatus},
    {"setXvyccDataFromPanel",         "(FFFFFFFFI)Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_setXvyccDataFromPanel},
    {"getEnableIPInfo",               "()[B", (void *)com_mstar_android_tvapi_factory_FactoryManager_getEnableIPInfo},
    {"getAutoFineGain",               "()B", (void *)com_mstar_android_tvapi_factory_FactoryManager_getAutoFineGain},
    {"setFixedFineGain",              "(B)Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_setFixedFineGain},
    {"getAutoRFGain",                 "()B", (void *)com_mstar_android_tvapi_factory_FactoryManager_getAutoRFGain},
    {"setRFGain",                     "(B)Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_setRFGain},
    {"EosSetHDCPKey",                 "([SZ)Z", (void *)com_mstar_android_tvapi_factory_FactoryManager_EosSetHDCPKey},
    {"EosGetHDCPKey",                 "(IZ)[S", (void *)com_mstar_android_tvapi_factory_FactoryManager_EosGetHDCPKey},
    {"getTunerStatus",                   "()Z",                (void *)com_mstar_android_tvapi_factory_FactoryManager_getTunerStatus},
};

/*
 * Register several native methods for one class.
 */
static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;

    clazz = env->FindClass(className);
    if (clazz == NULL) {
        ALOGE("Native registration unable to find class '%s'", className);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        ALOGE("RegisterNatives failed for '%s'", className);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

/*
 * Register native methods for all classes we know about.
 *
 * returns JNI_TRUE on success.
 */
static int registerNatives(JNIEnv *env) {
    if (!registerNativeMethods(env, classPathName, methods, sizeof(methods) / sizeof(methods[0]))) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

/*
 * This is called by the VM when the shared library is first loaded.
 */
typedef union {
    JNIEnv *env;
    void *venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    UnionJNIEnvToVoid uenv;
    uenv.venv = NULL;
    jint result = -1;
    JNIEnv *env = NULL;

    ALOGI("JNI_OnLoad");

    if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
        ALOGE("ERROR: GetEnv failed");
        goto bail;
    }
    env = uenv.env;

    if (registerNatives(env) != JNI_TRUE) {
        ALOGE("ERROR: registerNatives failed");
        goto bail;
    }

    result = JNI_VERSION_1_4;

bail:
    return result;
}
