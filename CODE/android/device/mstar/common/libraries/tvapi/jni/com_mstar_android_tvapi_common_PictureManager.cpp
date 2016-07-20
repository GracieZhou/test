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

#define LOG_TAG "PictureManager_JNI"
#include <utils/Log.h>

#include <stdio.h>
#include <utils/threads.h>
#include "jni.h"
#include "JNIHelp.h"
#include "android_runtime/AndroidRuntime.h"
#include "picturemanager/PictureManager.h"

using namespace android;

class JNIMSrvListener : public PictureManagerListener {
public:
    JNIMSrvListener(JNIEnv *env, jobject thiz, jobject weak_thiz);
    ~JNIMSrvListener();
    void notify(int msg, int ext1, int ext2);
    void notify(int event, int ext1, int ext2, jobject obj);
    void PostEvent_Template(int32_t nEvt, int32_t ext1, int32_t ext2);
    void PostEvent_SnServiceDeadth(int32_t nEvt, int32_t ext1, int32_t ext2);
    void PostEvent_SetAspectratio(int32_t ext1, int32_t ext2);
    void PostEvent_4K2KPhotoDisablePip(int32_t ext1, int32_t ext2);
    void PostEvent_4K2KPhotoDisablePop(int32_t ext1, int32_t ext2);
    void PostEvent_4K2KPhotoDisableDualview(int32_t ext1, int32_t ext2);
    void PostEvent_4K2KPhotoDisableTravelingmode(int32_t ext1, int32_t ext2);
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
    jmethodID   post_event_setaspectratio;
    jmethodID   post_event_4k2kphotodisablepip;
    jmethodID   post_event_4k2kphotodisablepop;
    jmethodID   post_event_4k2kphotodisabledualview;
    jmethodID   post_event_4k2kphotodisabletravelingmode;
};

static fields_t fields;
static Mutex sLock;

static sp<PictureManager> setPictureManager(JNIEnv *env, jobject thiz, const sp<PictureManager> &srv) {
    Mutex::Autolock l(sLock);
    sp<PictureManager> old = (PictureManager *)env->GetLongField(thiz, fields.context);
    if (srv.get()) {
        srv->incStrong(thiz);
    }
    if (old != 0) {
        old->decStrong(thiz);
    }
    env->SetLongField(thiz, fields.context, (jlong)srv.get());
    return old;
}

static sp<PictureManager> getPictureManager(JNIEnv *env, jobject thiz) {
    //return pictureManager;
    Mutex::Autolock l(sLock);
    PictureManager *const p = (PictureManager *)env->GetLongField(thiz, fields.context);
    return sp<PictureManager>(p);
}

void JNIMSrvListener::PostEvent_Template(int32_t nEvt, int32_t ext1, int32_t ext2) {
    JNIEnv *env = AndroidRuntime::getJNIEnv();
    env->CallStaticVoidMethod(mClass, fields.post_event, mObject, nEvt, ext1, ext2, 0);
}

void JNIMSrvListener::PostEvent_SetAspectratio(int32_t ext1, int32_t ext2) {
    JNIEnv *env = AndroidRuntime::getJNIEnv();
    env->CallStaticVoidMethod(mClass, fields.post_event_setaspectratio, mObject, ext1, ext2);
}

void JNIMSrvListener::PostEvent_4K2KPhotoDisablePip(int32_t ext1, int32_t ext2) {
    JNIEnv *env = AndroidRuntime::getJNIEnv();
    env->CallStaticVoidMethod(mClass, fields.post_event_4k2kphotodisablepip, mObject, ext1, ext2);

}

void JNIMSrvListener::PostEvent_4K2KPhotoDisablePop(int32_t ext1, int32_t ext2) {
    JNIEnv *env = AndroidRuntime::getJNIEnv();
    env->CallStaticVoidMethod(mClass, fields.post_event_4k2kphotodisablepop, mObject, ext1, ext2);
}

void JNIMSrvListener::PostEvent_4K2KPhotoDisableDualview(int32_t ext1, int32_t ext2) {
    JNIEnv *env = AndroidRuntime::getJNIEnv();
    env->CallStaticVoidMethod(mClass, fields.post_event_4k2kphotodisabledualview, mObject, ext1, ext2);
}

void JNIMSrvListener::PostEvent_4K2KPhotoDisableTravelingmode(int32_t ext1, int32_t ext2) {
    JNIEnv *env = AndroidRuntime::getJNIEnv();
    env->CallStaticVoidMethod(mClass, fields.post_event_4k2kphotodisabletravelingmode, mObject, ext1, ext2);
}

// TODO: for callback refactory
void JNIMSrvListener::notify(int event, int ext1, int ext2, jobject obj) {
}

void JNIMSrvListener::notify(int msg, int ext1, int ext2) {}

void JNIMSrvListener::PostEvent_SnServiceDeadth(int32_t nEvt, int32_t ext1, int32_t ext2) {
    /*
    JNIEnv* env = AndroidRuntime::getJNIEnv();
    sp<PictureManager> srv = setPictureManager(env, mObject, 0);
    if (srv != NULL) {
        // this prevents native callbacks after the object is released
        srv->setListener(0);
        srv.clear();

    }
    env->CallStaticVoidMethod(mClass, fields.post_event_snservicedeadth, mObject, ext1, ext2);
    env->DeleteGlobalRef(mObject);
    env->DeleteGlobalRef(mClass);
    //env->CallStaticVoidMethod(mClass, fields.post_event_snservicedeadth, mObject, ext1, ext2);
    */
}

JNIMSrvListener::JNIMSrvListener(JNIEnv *env, jobject thiz, jobject weak_thiz) {
    // Hold onto the MediaPlayer class for use in calling the static method
    // that posts events to the application thread.
    jclass clazz = env->GetObjectClass(thiz);
    if (clazz == NULL) {
        ALOGE("Can't find com/mstar/android/tvapi/common/PictureManager");
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

//--------------------------------------------------------------------------------
void com_mstar_android_tvapi_common_PictureManager_native_init
(JNIEnv *env, jclass thiz) {
    ALOGI("native_init");
    jclass clazz = env->FindClass("com/mstar/android/tvapi/common/PictureManager");
    fields.context = env->GetFieldID(clazz, "mNativeContext", "J");
    fields.post_event = env->GetStaticMethodID(clazz, "postEventFromNative", "(Ljava/lang/Object;IIILjava/lang/Object;)V");
    fields.player = env->GetFieldID(clazz, "mPictureManagerContext", "I");
    fields.post_event_snservicedeadth = env->GetStaticMethodID(clazz, "PostEvent_SnServiceDeadth", "(Ljava/lang/Object;II)V");
    fields.post_event_setaspectratio = env->GetStaticMethodID(clazz, "PostEvent_SetAspectratio", "(Ljava/lang/Object;II)V");
    fields.post_event_4k2kphotodisablepip = env->GetStaticMethodID(clazz, "PostEvent_4K2KPhotoDisablePip", "(Ljava/lang/Object;II)V");
    fields.post_event_4k2kphotodisablepop = env->GetStaticMethodID(clazz, "PostEvent_4K2KPhotoDisablePop", "(Ljava/lang/Object;II)V");
    fields.post_event_4k2kphotodisabledualview = env->GetStaticMethodID(clazz, "PostEvent_4K2KPhotoDisableDualview", "(Ljava/lang/Object;II)V");
    fields.post_event_4k2kphotodisabletravelingmode = env->GetStaticMethodID(clazz, "PostEvent_4K2KPhotoDisableTravelingmode", "(Ljava/lang/Object;II)V");
}

void com_mstar_android_tvapi_common_PictureManager_native_setup
(JNIEnv *env, jobject thiz, jobject weak_this) {
    ALOGI("native_setup");
    sp<PictureManager> srv = PictureManager::connect();
    if (srv == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "can't connect to picturemanager server.please check tvapi server");
        return;
    }
    // create new listener and give it to MediaPlayer
    sp<JNIMSrvListener> listener = new JNIMSrvListener(env, thiz, weak_this);
    srv->setListener(listener);

    // Stow our new C++ MSrv in an opaque field in the Java object.
    setPictureManager(env, thiz, srv);
}

void com_mstar_android_tvapi_common_PictureManager_native_finalize
(JNIEnv *env, jobject thiz) {
    ALOGI("native_finalize");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->disconnect();
    sp<PictureManager> srv = setPictureManager(env, thiz, 0);
    if (srv != NULL) {
        // this prevents native callbacks after the object is released
        srv->setListener(0);
    }
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    disableDlc
 * Signature: ()V
 */
void com_mstar_android_tvapi_common_PictureManager_disableDlc
(JNIEnv *env, jobject thiz) {
    ALOGI("disableDlc");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->disableDlc();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    disableOverScan
 * Signature: ()V
 */
void com_mstar_android_tvapi_common_PictureManager_disableOverScan
(JNIEnv *env, jobject thiz) {
    ALOGI("disableOverScan");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->disableOverScan();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    enableDlc
 * Signature: ()V
 */
void com_mstar_android_tvapi_common_PictureManager_enableDlc
(JNIEnv *env, jobject thiz) {
    ALOGI("enableDlc");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->enableDlc();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    FreezeImage
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_FreezeImage
(JNIEnv *env, jobject thiz) {
    ALOGI("FreezeImage");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return  ms->freezeImage();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    getDemoMode
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_PictureManager_getDemoMode
(JNIEnv *env, jobject thiz) {
    ALOGI("getDemoMode");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "java/lang/IllegalStateException", NULL);
        return -1;
    }
    return ms->getDemoMode();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:   scaleWindow
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_scaleWindow
(JNIEnv *env, jobject thiz) {
    ALOGI("scaleWindow");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "java/lang/IllegalStateException", NULL);
        return false;
    }
    bool isScale = false;
    isScale = ms->scaleWindow();
    return (jboolean)isScale;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    native_selectWindow
 * Signature: (I)B
 */
jboolean com_mstar_android_tvapi_common_PictureManager_selectWindow
(JNIEnv *env, jobject thiz, jint enWin) {
    ALOGI("selectWindow");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return (jboolean)ms->selectWindow(enWin);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setAspectRatio
 * Signature: (I)V
 */
void com_mstar_android_tvapi_common_PictureManager_setAspectRatio
(JNIEnv *env, jobject thiz, jint enAspectRatioTYpe) {
    ALOGI("setAspectRatio");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setAspectRatio(enAspectRatioTYpe);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    getAspectRatio
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_PictureManager_getAspectRatio
(JNIEnv *env, jobject thiz) {
    ALOGI("getAspectRatio");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getAspectRatio();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setColorTemperature
 * Signature: (Lcom/mstar/android/tvapi/common/vo/ColorTemperatureVO;)V
 */
void com_mstar_android_tvapi_common_PictureManager_setColorTemperature
(JNIEnv *env, jobject thiz, jobject pobjectPQL_COLOR_TEMP_DATA) {
    ALOGI("setColorTemperature");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    if (NULL == pobjectPQL_COLOR_TEMP_DATA) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvCommonException", "object parameter can not be null");
        return;
    }

    jclass objectClass = (env)->GetObjectClass(pobjectPQL_COLOR_TEMP_DATA);

    jfieldID n16BlueGain = env->GetFieldID(objectClass, "blueGain", "I");
    jfieldID n16BlueOffset = env->GetFieldID(objectClass, "blueOffset", "I");
    jfieldID n16GreenGain = env->GetFieldID(objectClass, "greenGain", "I");
    jfieldID n16GreenOffset = env->GetFieldID(objectClass, "greenOffset", "I");
    jfieldID n16RedGain = env->GetFieldID(objectClass, "redGain", "I");
    jfieldID n16RedOffset = env->GetFieldID(objectClass, "redOffset", "I");

    PQL_COLOR_TEMPEX_DATA pPQL_COLOR_TEMPEX_DATA;
    pPQL_COLOR_TEMPEX_DATA.u16RedGain = env->GetIntField(pobjectPQL_COLOR_TEMP_DATA, n16RedGain);
    pPQL_COLOR_TEMPEX_DATA.u16GreenGain =  env->GetIntField(pobjectPQL_COLOR_TEMP_DATA, n16GreenGain);
    pPQL_COLOR_TEMPEX_DATA.u16BlueGain =  env->GetIntField(pobjectPQL_COLOR_TEMP_DATA, n16BlueGain);
    pPQL_COLOR_TEMPEX_DATA.u16RedOffset =  env->GetIntField(pobjectPQL_COLOR_TEMP_DATA, n16RedOffset);
    pPQL_COLOR_TEMPEX_DATA.u16GreenOffset = env->GetIntField(pobjectPQL_COLOR_TEMP_DATA, n16GreenOffset);
    pPQL_COLOR_TEMPEX_DATA.u16BlueOffset =  env->GetIntField(pobjectPQL_COLOR_TEMP_DATA, n16BlueOffset);
    ms->setColorTemperatureEX(pPQL_COLOR_TEMPEX_DATA);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    getPanelWidthHeight
 * Signature: ()Lcom/mstar/android/tvapi/common/vo/PanelProperty;
 */
jobject com_mstar_android_tvapi_common_PictureManager_getPanelWidthHeight
(JNIEnv *env, jobject thiz) {
    ALOGI("getPanelWidthHeight");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }
    PanelProperty panelproperty;
    ms->getPanelWidthHeight(panelproperty);

    jclass objectClass = env->FindClass("com/mstar/android/tvapi/common/vo/PanelProperty");
    jfieldID jfildidwidth = env->GetFieldID(objectClass, "width", "I");
    jfieldID jfildidheight = env->GetFieldID(objectClass, "height", "I");

    jobject pobjectproperty = env->AllocObject(objectClass);
    env->SetIntField(pobjectproperty, jfildidwidth, panelproperty.width);
    env->SetIntField(pobjectproperty, jfildidheight, panelproperty.height);

    return pobjectproperty;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setCropWindow
 * Signature: (Lcom/mstar/android/tvapi/common/vo/VideoWindowType;)V
 */
void com_mstar_android_tvapi_common_PictureManager_SetCropWindow
(JNIEnv *env, jobject thiz, jobject videowindowtype) {
    ALOGI("SetCropWindow");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return ;
    }

    jclass jcls_vdwintype = (env)->GetObjectClass(videowindowtype);
    if (NULL == videowindowtype) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvCommonException", "object parameter can not be null");
        return;
    }

    jfieldID jx = env->GetFieldID(jcls_vdwintype, "x", "I");
    jfieldID jy = env->GetFieldID(jcls_vdwintype, "y", "I");
    jfieldID jwidth = env->GetFieldID(jcls_vdwintype, "width", "I");
    jfieldID jheight = env->GetFieldID(jcls_vdwintype, "height", "I");

    VideoWindowType svideowindowtype;
    svideowindowtype.x = env->GetIntField(videowindowtype, jx);
    svideowindowtype.y = env->GetIntField(videowindowtype, jy);
    svideowindowtype.width = env->GetIntField(videowindowtype, jwidth);
    svideowindowtype.height = env->GetIntField(videowindowtype, jheight);

    ms->setCropWindow(svideowindowtype);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setDisplayWindow
 * Signature:  (Lcom/mstar/android/tvapi/common/vo/VideoWindowType;)V
 */
void com_mstar_android_tvapi_common_PictureManager_SetDisplayWindow
(JNIEnv *env, jobject thiz, jobject videowindowtype) {
    ALOGI("SetDisplayWindow");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    jclass jcls_vdwintype = (env)->GetObjectClass(videowindowtype);
    if (NULL == videowindowtype) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvCommonException", "object parameter can not be null");
        return;
    }

    jfieldID jx = env->GetFieldID(jcls_vdwintype, "x", "I");
    jfieldID jy = env->GetFieldID(jcls_vdwintype, "y", "I");
    jfieldID jwidth = env->GetFieldID(jcls_vdwintype, "width", "I");
    jfieldID jheight = env->GetFieldID(jcls_vdwintype, "height", "I");

    VideoWindowType svideowindowtype;
    svideowindowtype.x = env->GetIntField(videowindowtype, jx);
    svideowindowtype.y = env->GetIntField(videowindowtype, jy);
    svideowindowtype.width = env->GetIntField(videowindowtype, jwidth);
    svideowindowtype.height = env->GetIntField(videowindowtype, jheight);

    ms->setDisplayWindow(svideowindowtype);
    return;
    //ms->SetDisplayWindow( h,  w,  y,  x);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setDemoMode
 * Signature: (I)V
 */
void com_mstar_android_tvapi_common_PictureManager_setDemoMode
(JNIEnv *env, jobject thiz, jint mode) {
    ALOGI("setDemoMode");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setDemoMode(mode);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    Native_setFilm
 * Signature: (I)V
 */
void com_mstar_android_tvapi_common_PictureManager_setFilm
(JNIEnv *env, jobject thiz, jint enFilmMode) {
    ALOGI("Native_setFilm");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setFilm(enFilmMode);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    SetMpegNr
 * Signature: (I)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_SetMpegNoiseReduction
(JNIEnv *env, jobject thiz, jint mpegnr) {
    ALOGI("SetMpegNoiseReduction");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->setMpegNoiseReduction(mpegnr);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setNoiseReduction
 * Signature: (I)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_setNoiseReduction
(JNIEnv *env, jobject thiz, jint numnoise) {
    ALOGI("setNoiseReduction");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->setNoiseReduction(numnoise);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    enableOverScan
 * Signature: ()V
 */
void com_mstar_android_tvapi_common_PictureManager_enableOverScan
(JNIEnv *env, jobject thiz) {
    ALOGI("enableOverScan");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return ;
    }
    ms->enableOverScan();
}


/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    IsImageFreezed
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_IsImageFreezed
(JNIEnv *env, jobject thiz) {
    ALOGI("IsImageFreezed");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return  ms->IsImageFreezed();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    isOverscanEnabled
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_isOverscanEnabled
(JNIEnv *env, jobject thiz) {
    ALOGI("isOverscanEnabled");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return  ms->isOverscanEnabled();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setOutputPattern
 * Signature: (ZIII)V
 */
void com_mstar_android_tvapi_common_PictureManager_setOutputPattern
(JNIEnv *env, jobject thiz, jboolean bEnable, jint u16Red, jint u16Green, jint u16Blue) {
    ALOGI("setOutputPattern");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return ;
    }
    ms->setOutputPattern(bEnable, u16Red, u16Green, u16Blue);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setOverscan
 * Signature: (IIII)V
 */
void com_mstar_android_tvapi_common_PictureManager_setOverscan
(JNIEnv *env, jobject thiz, jint bottom, jint top, jint right, jint left) {
    ALOGI("setOverscan");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return ;
    }
    ms->setOverscan(bottom, top, right, left);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setWindowEnabled
 * Signature: ()V
 */
void com_mstar_android_tvapi_common_PictureManager_setWindowVisible
(JNIEnv *env, jobject thiz) {
    ALOGI("setWindowVisible");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return ;
    }
    ms->setWindowVisible();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setWindowVisibled
 * Signature: ()V
 */
void com_mstar_android_tvapi_common_PictureManager_setWindowInvisible
(JNIEnv *env, jobject thiz) {
    ALOGI("setWindowInvisible");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return ;
    }
    ms->setWindowInvisible();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    unFreezeImage
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_unFreezeImage
(JNIEnv *env, jobject thiz) {
    ALOGI("unFreezeImage");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->unFreezeImage();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setPictureManagerModeBrightness
 * Signature: (S)V
 */
void com_mstar_android_tvapi_common_PictureManager_setPictureModeBrightness
(JNIEnv *env, jobject thiz, jshort brighntum) {
    ALOGI("setPictureModeBrightness");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setPictureModeBrightness(brighntum);
    return;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setPictureModeContrast
 * Signature: (S)V
 */
void com_mstar_android_tvapi_common_PictureManager_setPictureModeContrast
(JNIEnv *env, jobject thiz, jshort contrastnum) {
    ALOGI("setPictureModeContrast");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setPictureModeContrast(contrastnum);
    return;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setPictureModeSharpness
 * Signature: (S)V
 */
void com_mstar_android_tvapi_common_PictureManager_setPictureModeSharpness
(JNIEnv *env, jobject thiz, jshort sharpnessnum) {
    ALOGI("setPictureModeSharpness");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setPictureModeSharpness(sharpnessnum);
    return;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setPictureModeColor
 * Signature: (S)V
 */
void com_mstar_android_tvapi_common_PictureManager_setPictureModeColor
(JNIEnv *env, jobject thiz, jshort sharpnessnum) {
    ALOGI("setPictureModeColor");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setPictureModeColor(sharpnessnum);
    return;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setPictureModeTint
 * Signature: (S)V
 */
void com_mstar_android_tvapi_common_PictureManager_setPictureModeTint
(JNIEnv *env, jobject thiz, jshort sharpnessnum) {
    ALOGI("setPictureModeTint");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setPictureModeTint(sharpnessnum);
    return;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setBacklight
 * Signature: (I)V
 */
void com_mstar_android_tvapi_common_PictureManager_setBacklight
(JNIEnv *env, jobject thiz, jint backlightnum) {
    ALOGI("setBacklight");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setBacklight(backlightnum);
    return;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    getBacklightMinValue
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_PictureManager_getBacklightMinValue
(JNIEnv *env, jobject thiz) {
    ALOGI("getBacklightMinValue");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    int re = -1;
    re = ms->getBacklightMinValue();
    return (jint)re;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:  getBacklightMaxValue
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_PictureManager_getBacklightMaxValue
(JNIEnv *env, jobject thiz) {
    ALOGI("getBacklightMaxValue");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    int re = -1;
    re = ms->getBacklightMaxValue();
    return (jint)re;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:  getBacklight
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_PictureManager_getBacklight
(JNIEnv *env, jobject thiz) {
    ALOGI("getBacklight");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    int re = -1;
    re = ms->getBacklight();
    return (jint)re;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:  setDynamicContrastCurve
 * Signature: ([I[I[I)V
 */
void com_mstar_android_tvapi_common_PictureManager_setDynamicContrastCurve
(JNIEnv *env, jobject thiz, jintArray normalCurve, jintArray lightCurve, jintArray darkCurve) {
    ALOGI("setDynamicContrastCurve");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    int inormalCurve[INPUT_CONTRAST_CURVE_LENGTH];
    int ilightCurve[INPUT_CONTRAST_CURVE_LENGTH];
    int idarkCurve[INPUT_CONTRAST_CURVE_LENGTH];
    memset(inormalCurve, 0, INPUT_CONTRAST_CURVE_LENGTH);
    memset(ilightCurve, 0, INPUT_CONTRAST_CURVE_LENGTH);
    memset(idarkCurve, 0, INPUT_CONTRAST_CURVE_LENGTH);
    int length = 0;
    int i = 0;
    jint *parr = NULL;
    length = env->GetArrayLength(normalCurve);
    if (length > INPUT_CONTRAST_CURVE_LENGTH) {
        length = INPUT_CONTRAST_CURVE_LENGTH;
    }
    parr = env->GetIntArrayElements(normalCurve, NULL);
    for (i = 0; i < length; i++) {
        inormalCurve[i] = parr[i];
    }
    env->ReleaseIntArrayElements(normalCurve, parr, 0);
    parr = NULL;
    length = env->GetArrayLength(lightCurve);
    if (length > INPUT_CONTRAST_CURVE_LENGTH) {
        length = INPUT_CONTRAST_CURVE_LENGTH;
    }

    parr = env->GetIntArrayElements(lightCurve, NULL);
    for (i = 0; i < length; i++) {
        ilightCurve[i] = parr[i];
    }
    env->ReleaseIntArrayElements(lightCurve, parr, 0);
    parr = NULL;
    length = env->GetArrayLength(darkCurve);
    parr = env->GetIntArrayElements(darkCurve, NULL);
    if (length > INPUT_CONTRAST_CURVE_LENGTH) {
        length = INPUT_CONTRAST_CURVE_LENGTH;
    }
    for (i = 0; i < length; i++) {
        idarkCurve[i] = parr[i];
    }
    env->ReleaseIntArrayElements(darkCurve, parr, 0);
    parr = NULL;

    ms->setDynamicContrastCurve(inormalCurve, ilightCurve, idarkCurve);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:  getDynamicContrastCurve
 * Signature: (I)[I
 */
jintArray com_mstar_android_tvapi_common_PictureManager_getDynamicContrastCurve
(JNIEnv *env, jobject thiz) {
    ALOGI("getDynamicContrastCurve");
    sp<PictureManager> ms = getPictureManager(env, thiz);

    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }

    int presult[CONTRAST_CURVE_LENGTH];
    ms->getDynamicContrastCurve(presult);
    jintArray jarray_soucelist = env->NewIntArray(CONTRAST_CURVE_LENGTH);
    env->SetIntArrayRegion(jarray_soucelist, (jsize)0, (jsize)32, (jint *)presult);
    return jarray_soucelist;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setPictureModeBrightness
 * Signature: (II)V
 */
void com_mstar_android_tvapi_common_PictureManager_setPictureModeBrightness_2
(JNIEnv *env, jobject thiz, jint setLocationType, jint brighntum) {
    ALOGI("native_setPictureModeBrightness");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setPictureModeBrightness(setLocationType, brighntum);
}

void com_mstar_android_tvapi_common_PictureManager_setMfc
(JNIEnv *env, jobject thiz, jint mfcMode) {
    ALOGI("setMfc");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setMfc((EN_MFC_MODE)mfcMode);
}

jshort com_mstar_android_tvapi_common_PictureManager_getDlcAverageLuma
(JNIEnv *env, jobject thiz) {
    ALOGI("getDlcAverageLuma");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getDlcAverageLuma();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setDebugMode
 * Signature: (Z)V
 */
void com_mstar_android_tvapi_common_PictureManager_setDebugMode
(JNIEnv *env, jobject thiz, jboolean mfcMode) {
    ALOGI("setDebugMode");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setDebugMode(mfcMode);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    native_disableOsdWindow
 * Signature: (I)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_native_disableOsdWindow
(JNIEnv *env, jobject thiz, jint mfcOsdWindow) {
    ALOGI("native_disableOsdWindow");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return   ms->disableOsdWindow(mfcOsdWindow);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    native_disableAllOsdWindow
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_native_disableAllOsdWindow
(JNIEnv *env, jobject thiz) {
    ALOGI("native_disableAllOsdWindow");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return  ms->disableAllOsdWindow();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    native_setOsdWindow
 * Signature: (IIIII)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_native_setOsdWindow
(JNIEnv *env, jobject thiz, jint mfcOsdWindow, jint startX, jint width, jint startY, jint height) {
    ALOGI("native_disableOsdWindow");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return   ms->setOsdWindow(mfcOsdWindow, startX, width, startY, height);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setColorRange
 * Signature: (Z)V
 */
void com_mstar_android_tvapi_common_PictureManager_setColorRange
(JNIEnv *env, jobject thiz, jboolean colorRange0_255) {
    ALOGI("setColorRange");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setColorRange(colorRange0_255);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    getCustomerPqRuleNumber
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_PictureManager_getCustomerPqRuleNumber
(JNIEnv *env, jobject thiz) {
    ALOGI("getCustomerPqRuleNumber");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    int re = -1;
    re = ms->getCustomerPqRuleNumber();
    return (jint)re;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    getStatusNumberByCustomerPqRule
 * Signature: (I)I
 */
jint com_mstar_android_tvapi_common_PictureManager_getStatusNumberByCustomerPqRule
(JNIEnv *env, jobject thiz, jint ruleType) {
    ALOGI("getStatusNumberByCustomerPqRule");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    int re = -1;
    re = ms->getStatusNumberByCustomerPqRule(ruleType);
    return (jint)re;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setStatusByCustomerPqRule
 * Signature: (II)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_setStatusByCustomerPqRule
(JNIEnv *env, jobject thiz, jint ruleType, jint ruleStatus) {
    ALOGI("setStatusByCustomerPqRule");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return  ms->setStatusByCustomerPqRule(ruleType, ruleStatus);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    moveWindow
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_moveWindow
(JNIEnv *env, jobject thiz) {
    ALOGI("moveWindow");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return  ms->moveWindow();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    enableBacklight
 * Signature: ()Z
 */
void com_mstar_android_tvapi_common_PictureManager_enableBacklight
(JNIEnv *env, jobject thiz) {
    ALOGI("enableBacklight");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return ;
    }
    ms->enableBacklight();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    disableBacklight
 * Signature: ()Z
 */
void com_mstar_android_tvapi_common_PictureManager_disableBacklight
(JNIEnv *env, jobject thiz) {
    ALOGI("disableBacklight");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return ;
    }
    ms->disableBacklight();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    getDlcLumAverageTemporary
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_PictureManager_getDlcLumAverageTemporary
(JNIEnv *env, jobject thiz) {
    ALOGI("getDlcLumAverageTemporary");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getDlcLumAverageTemporary();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    getDlcLumTotalCount
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_PictureManager_getDlcLumTotalCount
(JNIEnv *env, jobject thiz) {
    ALOGI("getDlcLumTotalCount");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return  -1;
    }
    return ms->getDlcLumTotalCount();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    getDlcLumArray
 * Signature: (I)[I
 */
jintArray com_mstar_android_tvapi_common_PictureManager_getDlcLumArray
(JNIEnv *env, jobject thiz, jint lumaArraySize) {
    ALOGI("getDlcLumArray");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return  NULL;
    }

    int presult[lumaArraySize];
    ms->getDlcLumArray(presult, lumaArraySize);
    jintArray jarray_Lum = env->NewIntArray(lumaArraySize);
    env->SetIntArrayRegion(jarray_Lum, (jsize)0, (jsize)lumaArraySize, (jint *)presult);
    return jarray_Lum;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    switchDlcCurve
 * Signature: (S)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_switchDlcCurve
(JNIEnv *env, jobject thiz, jshort switchDlcCurve) {
    ALOGI("switchDlcCurve");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->switchDlcCurve(switchDlcCurve);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    native_getPixelRgb
 * Signature: (ISSI)Lcom/mstar/android/tvapi/common/vo/Rgb_Data;
 */
jobject com_mstar_android_tvapi_common_PictureManager_native_getPixelRgb
(JNIEnv *env, jobject thiz, jint eStage, jshort x, jshort y, jint eWindow) {
    ALOGI("native_getPixelRgb");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return  NULL;
    }
    GET_RGB_DATA rgbData;
    ms->getPixelRgb(eStage,  x,  y,  eWindow, rgbData);

    jclass objectClass = env->FindClass("com/mstar/android/tvapi/common/vo/Rgb_Data");
    jfieldID jfildidr = env->GetFieldID(objectClass, "r", "I");
    jfieldID jfildidg = env->GetFieldID(objectClass, "g", "I");
    jfieldID jfildidb = env->GetFieldID(objectClass, "b", "I");

    jobject pobjectrgbdata = env->AllocObject(objectClass);
    env->SetIntField(pobjectrgbdata, jfildidr, rgbData.u32r);
    env->SetIntField(pobjectrgbdata, jfildidg, rgbData.u32g);
    env->SetIntField(pobjectrgbdata, jfildidb, rgbData.u32b);
    return pobjectrgbdata;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setSwingLevel
 * Signature: (S)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_setSwingLevel
(JNIEnv *env, jobject thiz, jshort swingLevel) {
    ALOGI("setSwingLevel");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->setSwingLevel(swingLevel);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    getDlcHistogramMax
 * Signature: ()S
 */
jshort com_mstar_android_tvapi_common_PictureManager_getDlcHistogramMax
(JNIEnv *env, jobject thiz) {
    ALOGI("getDlcHistogramMax");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return  -1;
    }
    return ms->getDlcHistogramMax();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    getDlcHistogramMin
 * Signature: ()S
 */
jshort com_mstar_android_tvapi_common_PictureManager_getDlcHistogramMin
(JNIEnv *env, jobject thiz) {
    ALOGI("getDlcHistogramMin");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return  -1;
    }
    return ms->getDlcHistogramMin();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    getHDMIColorFormat();
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_PictureManager_getHDMIColorFormat
(JNIEnv *env, jobject thiz) {
    ALOGI("getHDMIColorFormat");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return  -1;
    }
    return ms->getHDMIColorFormat();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    forceFreerun
 * Signature: (ZZ)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_forceFreerun
(JNIEnv *env, jobject thiz, jboolean bEnable, jboolean b3D) {
    ALOGI("forceFreerun");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->forceFreerun(bEnable, b3D);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setHLinearScaling
 * Signature: (ZZI)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_setHLinearScaling
(JNIEnv *env, jobject thiz, jboolean bEnable, jboolean bSign, jint u16Delta) {
    ALOGI("setHLinearScaling");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }

    return ms->setHLinearScaling(bEnable, bSign, u16Delta);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setMEMCMode
 * Signature: (Ljava/lang/String;)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_setMEMCMode
(JNIEnv *env, jobject thiz, jstring tvapiInterfaceCommand) {
    ALOGI("setMEMCMode");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }

    return ms->setMEMCMode(String8(env->GetStringUTFChars(tvapiInterfaceCommand, NULL)));
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    native_setLocalDimmingMode
 * Signature: (I)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_native_setLocalDimmingMode
(JNIEnv *env, jobject thiz, jint localDimingModeNumber) {
    ALOGI("native_setLocalDimmingMode");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->setLocalDimmingMode(localDimingModeNumber);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setLocalDimmingBrightLevel
 * Signature: (S)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_setLocalDimmingBrightLevel
(JNIEnv *env, jobject thiz, jshort localDimingBrightLevel) {
    ALOGI("setLocalDimmingBrightLevel");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->setLocalDimmingBrightLevel(localDimingBrightLevel);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    turnOffLocalDimmingBacklight
 * Signature: (S)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_turnOffLocalDimmingBacklight
(JNIEnv *env, jobject thiz, jboolean bTrunOff) {
    ALOGI("setTurnOffLocalDimmingBacklight");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->turnOffLocalDimmingBacklight(bTrunOff);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    disableAllDualWinMode
 * Signature: ()z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_disableAllDualWinMode
(JNIEnv *env, jobject thiz) {
    ALOGI("disableAllDualWinMode");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->disableAllDualWinMode();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    native_setResolution
 * Signature: (B)V
 */
void com_mstar_android_tvapi_common_PictureManager_native_setResolution
(JNIEnv *env, jobject thiz, jbyte res) {
    ALOGI("native_setResolution");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setResolution((int8_t)res);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    native_getResolution
 * Signature: ()B
 */
jbyte com_mstar_android_tvapi_common_PictureManager_native_getResolution
(JNIEnv *env, jobject thiz) {
    ALOGI("native_getResolution");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return  0;
    }
    return ms->getResolution();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    native_setReproduceRate
 * Signature: (I)V
 */
void com_mstar_android_tvapi_common_PictureManager_native_setReproduceRate
(JNIEnv *env, jobject thiz, jint rate) {
    ALOGI("native_setResolution");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setReproduceRate((int32_t)rate);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    native_getReproduceRate
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_PictureManager_native_getReproduceRate
(JNIEnv *env, jobject thiz) {
    ALOGI("native_getResolution");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return  0;
    }
    return ms->getReproduceRate();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setUltraClear
 * Signature: (Z)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_native_setUltraClear
(JNIEnv *env, jobject thiz, jboolean bEn) {
    ALOGI("native_setUltraClear");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->setUltraClear(bEn);
}
/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    autoHDMIColorRange
 * Signature: ()Z
 */

jboolean com_mstar_android_tvapi_common_PictureManager_autoHDMIColorRange
(JNIEnv *env, jobject thiz) {
    ALOGI("autoHDMIColorRange");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->autoHDMIColorRange();
}
/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    enter4K2KMode
 * Signature: (Z)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_enter4K2KMode
(JNIEnv *env, jobject thiz, jboolean bEn) {
    ALOGI("enter4K2KMode");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->enter4K2KMode(bEn);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    is4K2KMode
 * Signature: (Z)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_is4K2KMode
(JNIEnv *env, jobject thiz, jboolean bEn) {
    ALOGI("is4K2KMode");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }

    return ms->is4K2KMode(bEn);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager_setScalerGammaByIndex
 * Method:    setScalerGammaByIndex
 * Signature: (B)V
 */
void com_mstar_android_tvapi_common_PictureManager_setScalerGammaByIndex
(JNIEnv *env, jobject thiz, jbyte Index) {
    ALOGI("setScalerGammaByIndex");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
    }

    ms->setScalerGammaByIndex(Index);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    native_getPixelInfo
 * Signature: ()Lcom/mstar/android/tvapi/common/vo/ScreenPixelInfo;
 */
jobject com_mstar_android_tvapi_common_PictureManager_native_getPixelInfo
(JNIEnv *env, jobject thiz, jint x, jint y, jint w, jint h) {
    ALOGI("getPixelInfo");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return  NULL;
    }
    Screen_Pixel_Info PixInfo;
    PixInfo.u16XStart = x;
    PixInfo.u16XEnd = w;
    PixInfo.u16YStart = y;
    PixInfo.u16YEnd = h;
    bool ret;
    ret = ms->getPixelInfo(&PixInfo);
    if (!ret) {
        return NULL;
    }

    jclass objectClass = env->FindClass("com/mstar/android/tvapi/common/vo/ScreenPixelInfo");
    jfieldID ju32ReportPixelInfo_Version = env->GetFieldID(objectClass, "u32ReportPixelInfo_Version", "I");
    jfieldID ju16ReportPixelInfo_Length = env->GetFieldID(objectClass, "u16ReportPixelInfo_Length", "S");
    // jfieldID jenStage= env->GetFieldID(objectClass,"enStage","Lcom/mstar/android/tvapi/common/vo/ScreenPixelInfo/EnumPixelRGBStage");
    jfieldID ju16RepWinColor = env->GetFieldID(objectClass, "u16RepWinColor", "S");
    jfieldID ju16XStart = env->GetFieldID(objectClass, "u16XStart", "S");
    jfieldID ju16XEnd = env->GetFieldID(objectClass, "u16XEnd", "S");
    jfieldID ju16YStart = env->GetFieldID(objectClass, "u16YStart", "S");
    jfieldID ju16YEnd = env->GetFieldID(objectClass, "u16YEnd", "S");
    jfieldID ju16RCrMin = env->GetFieldID(objectClass, "u16RCrMin", "S");
    jfieldID ju16GYMin = env->GetFieldID(objectClass, "u16GYMin", "S");
    jfieldID ju16GYMax = env->GetFieldID(objectClass, "u16GYMax", "S");
    jfieldID ju16BCbMin = env->GetFieldID(objectClass, "u16BCbMin", "S");
    jfieldID ju16BCbMax = env->GetFieldID(objectClass, "u16BCbMax", "S");
    jfieldID ju32RCrSum = env->GetFieldID(objectClass, "u32RCrSum", "J");
    jfieldID ju32GYSum = env->GetFieldID(objectClass, "u32GYSum", "J");
    jfieldID ju32BCbSum = env->GetFieldID(objectClass, "u32BCbSum", "J");
    jfieldID jbShowRepWin = env->GetFieldID(objectClass, "bShowRepWin", "Z");
    jfieldID jtmpStage = env->GetFieldID(objectClass, "tmpStage", "I");

    jobject pobjectPixInfo = env->AllocObject(objectClass);
    env->SetIntField(pobjectPixInfo, ju32ReportPixelInfo_Version, PixInfo.u32ReportPixelInfo_Version);
    env->SetShortField(pobjectPixInfo, ju16ReportPixelInfo_Length, PixInfo.u16ReportPixelInfo_Length);
    env->SetIntField(pobjectPixInfo, jtmpStage, (int)PixInfo.enStage); //set tmpStage,in java,it will be converted to enum
    env->SetShortField(pobjectPixInfo, ju16RepWinColor, PixInfo.u16RepWinColor);
    env->SetShortField(pobjectPixInfo, ju16XStart, PixInfo.u16XStart);
    env->SetShortField(pobjectPixInfo, ju16XEnd, PixInfo.u16XEnd);
    env->SetShortField(pobjectPixInfo, ju16YStart, PixInfo.u16YStart);
    env->SetShortField(pobjectPixInfo, ju16YEnd, PixInfo.u16YEnd);
    env->SetShortField(pobjectPixInfo, ju16RCrMin, PixInfo.u16RCrMin);
    env->SetShortField(pobjectPixInfo, ju16GYMin, PixInfo.u16GYMin);
    env->SetShortField(pobjectPixInfo, ju16GYMax, PixInfo.u16GYMax);
    env->SetShortField(pobjectPixInfo, ju16BCbMin, PixInfo.u16BCbMin);
    env->SetShortField(pobjectPixInfo, ju16BCbMax, PixInfo.u16BCbMax);
    env->SetLongField(pobjectPixInfo, ju32RCrSum, PixInfo.u32RCrSum);
    env->SetLongField(pobjectPixInfo, ju32GYSum, PixInfo.u32GYSum);
    env->SetLongField(pobjectPixInfo, ju32BCbSum, PixInfo.u32BCbSum);
    env->SetBooleanField(pobjectPixInfo, jbShowRepWin, PixInfo.bShowRepWin);

    return pobjectPixInfo;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    enableXvyccCompensation
 * Signature: (ZI)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_enableXvyccCompensation
(JNIEnv *env, jobject thiz, jboolean bEn, jint eWin) {
    ALOGI("enableXvyccCompensation");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }

    return ms->enableXvyccCompensation(bEn, eWin);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setxvYCCEnable
 * Signature: (ZI)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_setxvYCCEnable
(JNIEnv *env, jobject thiz, jboolean bEn, jint eMode) {
    ALOGI("setxvYCCEnable");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }

    return ms->setxvYCCEnable(bEn, eMode);
}
/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    lock4K2KMode
 * Signature: (Z)V
 */
void com_mstar_android_tvapi_common_PictureManager_lock4K2KMode
(JNIEnv *env, jobject thiz, jboolean bEn) {
    ALOGI("lock4K2KMode");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
    }

    ms->lock4K2KMode(bEn);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    get4K2KMode
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_PictureManager_get4K2KMode
(JNIEnv *env, jobject thiz) {
    ALOGI("get4K2KMode");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }

    return ms->get4K2KMode();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    set4K2KMode
 * Signature: (II)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_set4K2KMode
(JNIEnv *env, jobject thiz, jint enOutPutTimming, jint enUrsaMode) {
    ALOGI("set4K2KMode");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }

    return ms->set4K2KMode(enOutPutTimming, enUrsaMode);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    keepScalerOutput4k2k
 * Signature: (Z)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_keepScalerOutput4k2k
(JNIEnv *env, jobject thiz, jboolean bEnable) {
    ALOGI("keepScalerOutput4k2k");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }

    return ms->keepScalerOutput4k2k(bEnable);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    is3DTVPlugedIn
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_is3DTVPlugedIn
(JNIEnv *env, jobject thiz) {
    ALOGI("is3DTVPlugedIn");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return  false;
    }

    return ms->is3DTVPlugedIn();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    isSupportedZoom
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_isSupportedZoom
(JNIEnv *env, jobject thiz) {
    ALOGI("isSupportedZoom");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return  false;
    }

    return ms->isSupportedZoom();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    panelInitial
 * Signature: (Ljava/lang/String;)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_panelInitial
(JNIEnv* env, jobject thiz, jstring panelIniName) {
    ALOGI("panelInitial");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    bool re = false;
    String8 pName;
    const char* temp = env->GetStringUTFChars(panelIniName, NULL);
    pName.setTo(temp);
    re = ms->setMEMCMode(pName);
    env->ReleaseStringUTFChars(panelIniName, temp);
    return re;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setCustomerGammaParameter
 * Signature: (II)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_setCustomerGammaParameter
(JNIEnv* env, jobject thiz, jint index, jint value) {
    ALOGI("setCustomerGammaParameter");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->setGammaParameter(index, value);
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    calGammaTable
 * Signature: (Lcom/mstar/android/tvapi/common/vo/GammaTable;I)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_calGammaTable
(JNIEnv* env, jobject thiz, jobject gamma_table, jint MapMode) {
    ALOGI("calCustomerGammaTable");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }

    return ms->calGammaTable((GAMMA_TABLE *)&gamma_table, MapMode);
}

/*
 * [FIXME] bypass this one until Supoernova side is ready
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    setScalerGammaTable
 * Signature: (Lcom/mstar/android/tvapi/common/vo/GammaTable;)Z
 */
jboolean com_mstar_android_tvapi_common_PictureManager_setScalerGammaTable
(JNIEnv* env, jobject thiz, jobject gammaTable) {
    return false;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    getScalerMotion
 * Signature: ()B
 */
jbyte com_mstar_android_tvapi_common_PictureManager_getScalerMotion
(JNIEnv* env, jobject thiz) {
    ALOGI("getScalerMotion");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->getScalerMotion();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    native_getSupportedTimingList
 * Signature: ()[Lcom/mstar/android/tvapi/common/vo/TimingInfo;
 */
jobject com_mstar_android_tvapi_common_PictureManager_native_getSupportedTimingList
(JNIEnv *env, jobject thiz) {
    ALOGI("native_getSupportedTimingList");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return NULL;
    }

    int u16Count = ms->GetSupportedTimingListCount();
    St_Timing_Info *pTimingInfo = new (std::nothrow) St_Timing_Info[u16Count];
    ms->GetSupportedTimingList(pTimingInfo, u16Count);

    jclass objectClass = env->FindClass("com/mstar/android/tvapi/common/vo/TimingInfo");
    jfieldID jfieldidhResolution = env->GetFieldID(objectClass, "hResolution", "I");
    jfieldID jfieldidvResolution = env->GetFieldID(objectClass, "vResolution", "I");
    jfieldID jfieldidframeRate = env->GetFieldID(objectClass, "frameRate", "I");
    jfieldID jfieldidprogressiveMode = env->GetFieldID(objectClass, "progressiveMode", "Z");
    jfieldID jfieldidtimingID = env->GetFieldID(objectClass, "timingID", "I");

    jobjectArray jobjectArray_info = env->NewObjectArray((jsize)u16Count, objectClass, NULL);
    for (int i = 0; i < u16Count; i++) {
        jobject arrayElement_TimingInfo = env->AllocObject(objectClass);
        env->SetIntField(arrayElement_TimingInfo, jfieldidhResolution, pTimingInfo[i].u16HResolution);
        env->SetIntField(arrayElement_TimingInfo, jfieldidvResolution, pTimingInfo[i].u16VResolution);
        env->SetIntField(arrayElement_TimingInfo, jfieldidframeRate, pTimingInfo[i].u16FrameRate);
        env->SetBooleanField(arrayElement_TimingInfo, jfieldidprogressiveMode, pTimingInfo[i].bProgressiveMode);
        env->SetIntField(arrayElement_TimingInfo, jfieldidtimingID, pTimingInfo[i].u16TimingID);
        env->SetObjectArrayElement(jobjectArray_info, (jsize)i, arrayElement_TimingInfo);
        env->DeleteLocalRef(arrayElement_TimingInfo);
        arrayElement_TimingInfo = NULL;
    }
    delete [] pTimingInfo;

    return jobjectArray_info;
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    native_getSupportedTimingListCount
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_PictureManager_native_getSupportedTimingListCount
(JNIEnv *env, jobject thiz) {
    ALOGI("native_getSupportedTimingListCount");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }

    return ms->GetSupportedTimingListCount();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:    native_getCurrentTimingId
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_PictureManager_native_getCurrentTimingId
(JNIEnv *env, jobject thiz) {
    ALOGI("native_getCurrentTimingId");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }

    return ms->getCurrentTimingId();
}

// EosTek Patch Begin
//ashton: for wb adjust
/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:   asGetWbAdjustStar
 * Signature: ()Z
 */
void com_mstar_android_tvapi_common_PictureManager_asGetWbAdjustStar
(JNIEnv *env, jobject thiz) {
    ALOGI("asGetWbAdjustStar");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->asGetWbAdjustStar();
}

/*
 * Class:     com_mstar_android_tvapi_common_PictureManager
 * Method:   asGetWbAdjustExit
 * Signature: ()Z
 */
void com_mstar_android_tvapi_common_PictureManager_asGetWbAdjustExit
(JNIEnv *env, jobject thiz) {
    ALOGI("asGetWbAdjustExit");
    sp<PictureManager> ms = getPictureManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->asGetWbAdjustExit();
}


// EosTek Patch End




//------------------------------------------------------------------------------------
static const char *classPathName = "com/mstar/android/tvapi/common/PictureManager";

static JNINativeMethod methods[] = {
    {"native_init",                   "()V", (void *)com_mstar_android_tvapi_common_PictureManager_native_init},
    {"native_setup",                  "(Ljava/lang/Object;)V", (void *)com_mstar_android_tvapi_common_PictureManager_native_setup},
    {"native_finalize",               "()V", (void *)com_mstar_android_tvapi_common_PictureManager_native_finalize},
    {"disableDlc",                    "()V", (void *)com_mstar_android_tvapi_common_PictureManager_disableDlc},
    {"disableOverScan",               "()V", (void *)com_mstar_android_tvapi_common_PictureManager_disableOverScan},
    {"enableDlc",                     "()V", (void *)com_mstar_android_tvapi_common_PictureManager_enableDlc},
    {"freezeImage",                   "()Z", (void *)com_mstar_android_tvapi_common_PictureManager_FreezeImage},
    {"native_getDemoMode",            "()I", (void *)com_mstar_android_tvapi_common_PictureManager_getDemoMode},
    {"native_selectWindow",           "(I)Z", (void *)com_mstar_android_tvapi_common_PictureManager_selectWindow},
    {"native_setAspectRatio",         "(I)V", (void *)com_mstar_android_tvapi_common_PictureManager_setAspectRatio},
    {"native_getAspectRatio",         "()I", (void *)com_mstar_android_tvapi_common_PictureManager_getAspectRatio},
    {"setColorTemperature",           "(Lcom/mstar/android/tvapi/common/vo/ColorTemperatureExData;)V", (void *)com_mstar_android_tvapi_common_PictureManager_setColorTemperature},
    {"getPanelWidthHeight",           "()Lcom/mstar/android/tvapi/common/vo/PanelProperty;", (void *)com_mstar_android_tvapi_common_PictureManager_getPanelWidthHeight},
    {"setCropWindow",                 "(Lcom/mstar/android/tvapi/common/vo/VideoWindowType;)V", (void *)com_mstar_android_tvapi_common_PictureManager_SetCropWindow},
    {"setDisplayWindow",              "(Lcom/mstar/android/tvapi/common/vo/VideoWindowType;)V", (void *)com_mstar_android_tvapi_common_PictureManager_SetDisplayWindow},
    {"native_setDemoMode",            "(I)V", (void *)com_mstar_android_tvapi_common_PictureManager_setDemoMode},
    {"native_setFilm",                "(I)V", (void *)com_mstar_android_tvapi_common_PictureManager_setFilm},
    {"native_setMpegNoiseReduction",  "(I)Z", (void *)com_mstar_android_tvapi_common_PictureManager_SetMpegNoiseReduction},
    {"native_setNoiseReduction",      "(I)Z", (void *)com_mstar_android_tvapi_common_PictureManager_setNoiseReduction},
    {"enableOverScan",                "()V", (void *)com_mstar_android_tvapi_common_PictureManager_enableOverScan},
    {"isImageFreezed",                "()Z", (void *)com_mstar_android_tvapi_common_PictureManager_IsImageFreezed},
    {"isOverscanEnabled",             "()Z", (void *)com_mstar_android_tvapi_common_PictureManager_isOverscanEnabled},
    {"setOutputPattern",              "(ZIII)V", (void *)com_mstar_android_tvapi_common_PictureManager_setOutputPattern},
    {"setOverscan",                   "(IIII)V", (void *)com_mstar_android_tvapi_common_PictureManager_setOverscan},
    {"setWindowVisible",              "()V", (void *)com_mstar_android_tvapi_common_PictureManager_setWindowVisible},
    {"setWindowInvisible",            "()V", (void *)com_mstar_android_tvapi_common_PictureManager_setWindowInvisible},
    {"unFreezeImage",                 "()Z", (void *)com_mstar_android_tvapi_common_PictureManager_unFreezeImage},
    {"setPictureModeBrightness",      "(S)V", (void *)com_mstar_android_tvapi_common_PictureManager_setPictureModeBrightness},
    {"native_setPictureModeBrightness", "(II)V", (void *)com_mstar_android_tvapi_common_PictureManager_setPictureModeBrightness_2},
    {"native_setMfc",                 "(I)V", (void *)com_mstar_android_tvapi_common_PictureManager_setMfc},
    {"scaleWindow",                   "()Z", (void *)com_mstar_android_tvapi_common_PictureManager_scaleWindow},
    {"setPictureModeContrast",        "(S)V", (void *)com_mstar_android_tvapi_common_PictureManager_setPictureModeContrast},
    {"setPictureModeSharpness",       "(S)V", (void *)com_mstar_android_tvapi_common_PictureManager_setPictureModeSharpness},
    {"setPictureModeColor",           "(S)V", (void *)com_mstar_android_tvapi_common_PictureManager_setPictureModeColor},
    {"setPictureModeTint",            "(S)V", (void *)com_mstar_android_tvapi_common_PictureManager_setPictureModeTint},
    {"setBacklight",                  "(I)V", (void *)com_mstar_android_tvapi_common_PictureManager_setBacklight},
    {"getBacklightMinValue",          "()I", (void *)com_mstar_android_tvapi_common_PictureManager_getBacklightMinValue},
    {"getBacklightMaxValue",          "()I", (void *)com_mstar_android_tvapi_common_PictureManager_getBacklightMaxValue},
    {"getBacklight",                  "()I", (void *)com_mstar_android_tvapi_common_PictureManager_getBacklight},
    {"setDynamicContrastCurve",       "([I[I[I)V", (void *)com_mstar_android_tvapi_common_PictureManager_setDynamicContrastCurve},
    {"getDynamicContrastCurve",       "()[I", (void *)com_mstar_android_tvapi_common_PictureManager_getDynamicContrastCurve},
    {"getDlcAverageLuma",             "()S", (void *)com_mstar_android_tvapi_common_PictureManager_getDlcAverageLuma},
    {"setDebugMode",                  "(Z)V", (void *)com_mstar_android_tvapi_common_PictureManager_setDebugMode},
    {"native_disableOsdWindow",       "(I)Z", (void *)com_mstar_android_tvapi_common_PictureManager_native_disableOsdWindow},
    {"native_disableAllOsdWindow",    "()Z", (void *)com_mstar_android_tvapi_common_PictureManager_native_disableAllOsdWindow},
    {"native_setOsdWindow",           "(IIIII)Z", (void *)com_mstar_android_tvapi_common_PictureManager_native_setOsdWindow},
    {"setColorRange",                 "(Z)V", (void *)com_mstar_android_tvapi_common_PictureManager_setColorRange},
    {"getCustomerPqRuleNumber",       "()I", (void *)com_mstar_android_tvapi_common_PictureManager_getCustomerPqRuleNumber},
    {"getStatusNumberByCustomerPqRule", "(I)I", (void *)com_mstar_android_tvapi_common_PictureManager_getStatusNumberByCustomerPqRule},
    {"setStatusByCustomerPqRule",     "(II)Z", (void *)com_mstar_android_tvapi_common_PictureManager_setStatusByCustomerPqRule},
    {"moveWindow",                    "()Z", (void *)com_mstar_android_tvapi_common_PictureManager_moveWindow},
    {"enableBacklight",               "()V", (void *)com_mstar_android_tvapi_common_PictureManager_enableBacklight},
    {"disableBacklight",              "()V", (void *)com_mstar_android_tvapi_common_PictureManager_disableBacklight},
    {"getDlcLumAverageTemporary",     "()I", (void *)com_mstar_android_tvapi_common_PictureManager_getDlcLumAverageTemporary},
    {"getDlcLumTotalCount",           "()I", (void *)com_mstar_android_tvapi_common_PictureManager_getDlcLumTotalCount},
    {"getDlcLumArray",                "(I)[I", (void *)com_mstar_android_tvapi_common_PictureManager_getDlcLumArray},
    {"switchDlcCurve",                "(S)Z", (void *)com_mstar_android_tvapi_common_PictureManager_switchDlcCurve},
    {"native_getPixelRgb",            "(ISSI)Lcom/mstar/android/tvapi/common/vo/Rgb_Data;", (void *)com_mstar_android_tvapi_common_PictureManager_native_getPixelRgb},
    {"setSwingLevel",                 "(S)Z", (void *)com_mstar_android_tvapi_common_PictureManager_setSwingLevel},
    {"getDlcHistogramMax",            "()S", (void *)com_mstar_android_tvapi_common_PictureManager_getDlcHistogramMax},
    {"getDlcHistogramMin",            "()S", (void *)com_mstar_android_tvapi_common_PictureManager_getDlcHistogramMin},
    {"getHDMIColorFormat",            "()I", (void *)com_mstar_android_tvapi_common_PictureManager_getHDMIColorFormat},
    {"forceFreerun",                  "(ZZ)Z", (void *)com_mstar_android_tvapi_common_PictureManager_forceFreerun},
    {"setHLinearScaling",             "(ZZI)Z", (void *)com_mstar_android_tvapi_common_PictureManager_setHLinearScaling},
    {"setMEMCMode",                   "(Ljava/lang/String;)Z", (void *)com_mstar_android_tvapi_common_PictureManager_setMEMCMode},
    {"native_setLocalDimmingMode",    "(I)Z", (void *)com_mstar_android_tvapi_common_PictureManager_native_setLocalDimmingMode},
    {"setLocalDimmingBrightLevel",    "(S)Z", (void *)com_mstar_android_tvapi_common_PictureManager_setLocalDimmingBrightLevel},
    {"setTurnOffLocalDimmingBacklight", "(Z)Z", (void *)com_mstar_android_tvapi_common_PictureManager_turnOffLocalDimmingBacklight},
    {"turnOffLocalDimmingBacklight",  "(Z)Z", (void *)com_mstar_android_tvapi_common_PictureManager_turnOffLocalDimmingBacklight},
    {"disableAllDualWinMode",         "()Z", (void *)com_mstar_android_tvapi_common_PictureManager_disableAllDualWinMode},
    {"native_getResolution",          "()B", (void *)com_mstar_android_tvapi_common_PictureManager_native_getResolution},
    {"native_setResolution",          "(B)V", (void *)com_mstar_android_tvapi_common_PictureManager_native_setResolution},
    {"native_getReproduceRate",       "()I", (void *)com_mstar_android_tvapi_common_PictureManager_native_getReproduceRate},
    {"native_setReproduceRate",       "(I)V", (void *)com_mstar_android_tvapi_common_PictureManager_native_setReproduceRate},
    {"setUltraClear",                 "(Z)Z", (void *)com_mstar_android_tvapi_common_PictureManager_native_setUltraClear},
    {"autoHDMIColorRange",            "()Z", (void *)com_mstar_android_tvapi_common_PictureManager_autoHDMIColorRange},
    {"enter4K2KMode",                 "(Z)Z", (void *)com_mstar_android_tvapi_common_PictureManager_enter4K2KMode},
    {"is4K2KMode",                    "(Z)Z", (void *)com_mstar_android_tvapi_common_PictureManager_is4K2KMode},
    {"setScalerGammaByIndex",         "(B)V", (void *)com_mstar_android_tvapi_common_PictureManager_setScalerGammaByIndex},
    {"native_getPixelInfo",           "(IIII)Lcom/mstar/android/tvapi/common/vo/ScreenPixelInfo;", (void *)com_mstar_android_tvapi_common_PictureManager_native_getPixelInfo},
    {"enableXvyccCompensation",       "(ZI)Z", (void *)com_mstar_android_tvapi_common_PictureManager_enableXvyccCompensation},
    {"setxvYCCEnable",                "(ZI)Z", (void *)com_mstar_android_tvapi_common_PictureManager_setxvYCCEnable},
    {"lock4K2KMode",                  "(Z)V", (void *)com_mstar_android_tvapi_common_PictureManager_lock4K2KMode},
    {"native_get4K2KMode",            "()I", (void *)com_mstar_android_tvapi_common_PictureManager_get4K2KMode},
    {"native_set4K2KMode",            "(II)Z", (void *)com_mstar_android_tvapi_common_PictureManager_set4K2KMode},
    {"keepScalerOutput4k2k",          "(Z)Z", (void *)com_mstar_android_tvapi_common_PictureManager_keepScalerOutput4k2k},
    {"is3DTVPlugedIn",                "()Z", (void *)com_mstar_android_tvapi_common_PictureManager_is3DTVPlugedIn},
    {"isSupportedZoom",               "()Z", (void *)com_mstar_android_tvapi_common_PictureManager_isSupportedZoom},
    {"panelInitial",                  "(Ljava/lang/String;)Z", (void *)com_mstar_android_tvapi_common_PictureManager_panelInitial},
    {"setCustomerGammaParameter",     "(II)Z", (void *)com_mstar_android_tvapi_common_PictureManager_setCustomerGammaParameter},
    {"calGammaTable",                 "(Lcom/mstar/android/tvapi/common/vo/GammaTable;I)Z", (void *)com_mstar_android_tvapi_common_PictureManager_calGammaTable},
    {"setScalerGammaTable",           "(Lcom/mstar/android/tvapi/common/vo/GammaTable;)Z", (void *)com_mstar_android_tvapi_common_PictureManager_setScalerGammaTable},
    {"getScalerMotion",               "()B", (void *)com_mstar_android_tvapi_common_PictureManager_getScalerMotion},
    {"native_getSupportedTimingList", "()[Lcom/mstar/android/tvapi/common/vo/TimingInfo;", (void *)com_mstar_android_tvapi_common_PictureManager_native_getSupportedTimingList},
    {"native_getSupportedTimingListCount", "()I", (void *)com_mstar_android_tvapi_common_PictureManager_native_getSupportedTimingListCount},
    {"native_getCurrentTimingId",     "()I", (void *)com_mstar_android_tvapi_common_PictureManager_native_getCurrentTimingId},
      // EosTek Patch Begin
     //ashton: for wb adjust
    {"asGetWbAdjustStar",	   "()V", (void *)com_mstar_android_tvapi_common_PictureManager_asGetWbAdjustStar},    
    {"asGetWbAdjustExit",	   "()V", (void *)com_mstar_android_tvapi_common_PictureManager_asGetWbAdjustExit},
    // EosTek Patch End   
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
