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

#define LOG_TAG "AudioManager_JNI"
#include <utils/Log.h>

#include <stdio.h>
#include <utils/threads.h>
#include <MEvent.h>
#include "jni.h"
#include "JNIHelp.h"
#include "android_runtime/AndroidRuntime.h"
#include "audiomanager/AudioManager.h"

using namespace android;

class JNIMSrvListener : public AudioManagerListener {
public:
    JNIMSrvListener(JNIEnv *env, jobject thiz, jobject weak_thiz);
    ~JNIMSrvListener();
    void notify(int event, int ext1, int ext2, jobject obj);
    // FIXME: old architecture, remove later
    void PostEvent_ApSetVolume(int msg, int ext1, int ext2);
    void PostEvent_Template(int32_t nEvt, int32_t ext1, int32_t ext2);
    void PostEvent_SnServiceDeadth(int32_t nEvt, int32_t ext1, int32_t ext2);
private:
    JNIMSrvListener();
    jclass      mClass;     // Reference to MSrv class
    jobject     mObject;    // Weak ref to MSrv Java object to call on
};

struct fields_t {
    jfieldID    context;
    jfieldID    player;
    jmethodID   post_event;
    jint TVAUDIO_AP_SET_VOLUME;
};

static fields_t fields;
static Mutex sLock;

static sp<AudioManager> setAudioManager(JNIEnv *env, jobject thiz, const sp<AudioManager> &srv) {
    Mutex::Autolock l(sLock);
    sp<AudioManager> old = (AudioManager *)env->GetLongField(thiz, fields.context);
    if (srv.get()) {
        srv->incStrong(thiz);
    }
    if (old != 0) {
        old->decStrong(thiz);
    }
    env->SetLongField(thiz, fields.context, (jlong)srv.get());
    return old;
}

static sp<AudioManager> getAudioManager(JNIEnv *env, jobject thiz) {
    Mutex::Autolock l(sLock);
    AudioManager *const p = (AudioManager *)env->GetLongField(thiz, fields.context);
    return sp<AudioManager>(p);
}

JNIMSrvListener::JNIMSrvListener(JNIEnv *env, jobject thiz, jobject weak_thiz) {

    // Hold onto the MediaPlayer class for use in calling the static method
    // that posts events to the application thread.
    jclass clazz = env->GetObjectClass(thiz);
    if (clazz == NULL) {
        ALOGE("Can't find com/mstar/android/tvapi/common/AudioManagermanager");
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

void JNIMSrvListener::notify(int event, int ext1, int ext2, jobject obj) {
    JNIEnv *env = AndroidRuntime::getJNIEnv();
    jint msg;

    switch (event) {
        case EV_AP_SETVOLUME_EVENT:
            msg = fields.TVAUDIO_AP_SET_VOLUME;
            break;
        default:
            ALOGE("Unknown event '%#x'", event);
            return;
    }
    env->CallStaticVoidMethod(mClass, fields.post_event, mObject,
            msg, ext1, ext2, obj);

}

void JNIMSrvListener::PostEvent_ApSetVolume(int msg, int ext1, int ext2) {
    notify(msg, ext1, ext2, NULL);

}

// FIXME: old architecture, remove after tvos invoke default event by notify()
void JNIMSrvListener::PostEvent_Template(int32_t nEvt, int32_t ext1, int32_t ext2) {
    notify(nEvt, ext1, ext2, NULL);
}

void JNIMSrvListener::PostEvent_SnServiceDeadth(int32_t nEvt, int32_t ext1, int32_t ext2) {}

//--------------------------------------------------------------------------------
void com_mstar_android_tvapi_common_AudioManager_native_init
(JNIEnv *env, jclass thiz) {
    ALOGD("native_init");
    jclass clazz = env->FindClass("com/mstar/android/tvapi/common/AudioManager");
    fields.context = env->GetFieldID(clazz, "mNativeContext", "J");
    fields.post_event = env->GetStaticMethodID(clazz, "postEventFromNative", "(Ljava/lang/Object;IIILjava/lang/Object;)V");
    fields.player = env->GetFieldID(clazz, "mAudioManagerContext", "I");
    fields.TVAUDIO_AP_SET_VOLUME = env->GetStaticIntField(clazz,
                env->GetStaticFieldID(clazz, "TVAUDIO_AP_SET_VOLUME", "I"));
}

void com_mstar_android_tvapi_common_AudioManager_native_setup
(JNIEnv *env, jobject thiz, jobject weak_this) {
    ALOGD("native_setup");
    sp<AudioManager> srv = AudioManager::connect();
    if (srv == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "can't connect to audiomanager server.please check tvapi server");
        return;
    }
    // create new listener and give it to MediaPlayer
    sp<JNIMSrvListener> listener = new JNIMSrvListener(env, thiz, weak_this);
    srv->setListener(listener);
    // Stow our new C++ MSrv in an opaque field in the Java object.
    setAudioManager(env, thiz, srv);
}

void com_mstar_android_tvapi_common_AudioManager_native_finalize
(JNIEnv *env, jobject thiz) {
    ALOGD("native_finalize");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }

    ms->disconnect();

    sp<AudioManager> srv = setAudioManager(env, thiz, 0);
    if (srv != NULL) {
        // this prevents native callbacks after the object is released
        srv->setListener(0);
    }
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setVolume
 * Signature: (B)V
 */
void com_mstar_android_tvapi_common_AudioManager_native_setAudioVolume
(JNIEnv *env, jobject thiz, jint enSoundPath, jbyte vol) {
    ALOGD("native_setVolume");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }

    ms->setAudioVolume((int32_t)enSoundPath, (int8_t)vol);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_getAudioVolume
 * Signature: ()B
 */
jbyte com_mstar_android_tvapi_common_AudioManager_native_getAudioVolume
(JNIEnv *env, jobject thiz, jint volSrcType) {
    ALOGD("native_getAudioVolume");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return  0;
    }

    return ms->getAudioVolume(volSrcType);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_enableMute
 * Signature: ()Z
 */
jint com_mstar_android_tvapi_common_AudioManager_native_enableMute
(JNIEnv *env, jobject thiz, jint mutetype) {
    ALOGD("enableMute");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return  -1;
    }
    return ms->enableMute(mutetype);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_disableMute
 * Signature: (I)Z
 */
jint com_mstar_android_tvapi_common_AudioManager_native_disableMute
(JNIEnv *env, jobject thiz, jint enMuteType) {
    ALOGD("native_disableMute ");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->disableMute(enMuteType);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_isMuteEnabled
 * Signature: (I)Ljava/lang/Boolean;
 */
jboolean com_mstar_android_tvapi_common_AudioManager_native_isMuteEnabled
(JNIEnv *env, jobject thiz, jint enMuteType) {
    ALOGD("native_isMuteEnabled has delete by client");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->isMuteEnabled(enMuteType);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setAtvMtsMode
 * Signature: (I)I
 */
jint com_mstar_android_tvapi_common_AudioManager_native_setAtvMtsMode
(JNIEnv *env, jobject thiz, jint mode) {
    ALOGD("native_setAtvMtsMode");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->setAtvMtsMode(mode);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_getAtvMtsMode
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_AudioManager_native_getAtvMtsMode
(JNIEnv *env, jobject thiz) {
    ALOGD("native_getAtvMtsMode ");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getAtvMtsMode();
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setToNextAtvMtsMode
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_AudioManager_native_setToNextAtvMtsMode
(JNIEnv *env, jobject thiz) {
    ALOGD("native_setToNextAtvMtsMode");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->setToNextAtvMtsMode();
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setAtvSoundSystem
 * Signature: (I)Z
 */
jboolean com_mstar_android_tvapi_common_AudioManager_native_setAtvSoundSystem
(JNIEnv *env, jobject thiz, jint SoundSystem) {
    ALOGD("native_setAtvSoundSystem");

    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->setAtvSoundSystem(SoundSystem);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_getAtvSoundSystem
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_AudioManager_native_getAtvSoundSystem
(JNIEnv *env, jobject thiz) {
    ALOGD("native_getAtvSoundSystem");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getAtvSoundSystem();
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setDtvOutputMode
 * Signature: (I)V
 */
void com_mstar_android_tvapi_common_AudioManager_native_setDtvOutputMode
(JNIEnv *env, jobject thiz, jint outputmode) {
    ALOGD("native_setDtvOutputMode ");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setDtvOutputMode(outputmode);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_getDtvOutputMode
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_AudioManager_native_getDtvOutputMode
(JNIEnv *env, jobject thiz) {
    ALOGD("native_getDtvOutputMode ");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getDtvOutputMode();
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setBasicSoundEffect
 * Signature:(Lcom/mstar/android/tvapi/common/vo/DtvSoundEffect;)I
 */
jint com_mstar_android_tvapi_common_AudioManager_native_setBasicSoundEffect
(JNIEnv *env, jobject thiz, jint soundeffecttype, jobject dtvSoundEffectVO) {
    ALOGD("native_setBasicSoundEffect");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    if (NULL == dtvSoundEffectVO) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvCommonException", "object dtvSoundEffectVO can not be null");
        return -1;
    }
    DtvSoundEffect dtvSoundeffct_t;

    jclass jclass_dtvSoundEffectVO = env->GetObjectClass(dtvSoundEffectVO);
    jfieldID preScale = env->GetFieldID(jclass_dtvSoundEffectVO, "preScale", "I");
    jfieldID bass = env->GetFieldID(jclass_dtvSoundEffectVO, "bass", "I");
    jfieldID balance = env->GetFieldID(jclass_dtvSoundEffectVO, "balance", "I");
    jfieldID eqBandNumber = env->GetFieldID(jclass_dtvSoundEffectVO, "eqBandNumber", "S");
    jfieldID peqBandNumber = env->GetFieldID(jclass_dtvSoundEffectVO, "peqBandNumber", "S");
    jfieldID soundParameterEqs = env->GetFieldID(jclass_dtvSoundEffectVO, "soundParameterEqs", "[Lcom/mstar/android/tvapi/common/vo/SoundParameterEq;");
    jfieldID SoundParameterPeq = env->GetFieldID(jclass_dtvSoundEffectVO, "soundParameterPeqs", "[Lcom/mstar/android/tvapi/common/vo/SoundParameterPeq;");
    jfieldID avcThreshold = env->GetFieldID(jclass_dtvSoundEffectVO, "avcThreshold", "I");
    jfieldID avcAttachTime = env->GetFieldID(jclass_dtvSoundEffectVO, "avcAttachTime", "I");
    jfieldID avcReleaseTime = env->GetFieldID(jclass_dtvSoundEffectVO, "avcReleaseTime", "I");
    jfieldID surroundXaValue = env->GetFieldID(jclass_dtvSoundEffectVO, "surroundXaValue", "I");
    jfieldID surroundXbValue = env->GetFieldID(jclass_dtvSoundEffectVO, "surroundXbValue", "I");
    jfieldID surroundXkValue = env->GetFieldID(jclass_dtvSoundEffectVO, "surroundXkValue", "I");
    jfieldID soundDrcThreshold = env->GetFieldID(jclass_dtvSoundEffectVO, "soundDrcThreshold", "I");
    jfieldID noiseReductionThreshold = env->GetFieldID(jclass_dtvSoundEffectVO, "noiseReductionThreshold", "I");
    jfieldID echoTime = env->GetFieldID(jclass_dtvSoundEffectVO, "echoTime", "I");
    jfieldID treble = env->GetFieldID(jclass_dtvSoundEffectVO, "treble", "I");

    jclass jclassarr_soundParameterEqs = env->FindClass("com/mstar/android/tvapi/common/vo/SoundParameterEq");
    jfieldID fid_eqLevels = env->GetFieldID(jclassarr_soundParameterEqs, "eqLevel", "I");

    jclass jclassarr_SoundParameterPeq = env->FindClass("com/mstar/android/tvapi/common/vo/SoundParameterPeq");
    jfieldID fid_peqGain = env->GetFieldID(jclassarr_SoundParameterPeq, "peqGain", "I");
    jfieldID fid_peqGc = env->GetFieldID(jclassarr_SoundParameterPeq, "peqGc", "I");
    jfieldID fid_peqQvalue = env->GetFieldID(jclassarr_SoundParameterPeq, "peqQvalue", "I");

    dtvSoundeffct_t.preScale = env->GetIntField(dtvSoundEffectVO, preScale);
    dtvSoundeffct_t.bass = env->GetIntField(dtvSoundEffectVO, bass);
    dtvSoundeffct_t.balance = env->GetIntField(dtvSoundEffectVO, balance);
    dtvSoundeffct_t.eqBandNumber = env->GetShortField(dtvSoundEffectVO, eqBandNumber);
    dtvSoundeffct_t.peqBandNumber = env->GetShortField(dtvSoundEffectVO, peqBandNumber);
    dtvSoundeffct_t.avcThreshold = env->GetIntField(dtvSoundEffectVO, avcThreshold);
    dtvSoundeffct_t.avcAttachTime = env->GetIntField(dtvSoundEffectVO, avcAttachTime);
    dtvSoundeffct_t.avcReleaseTime = env->GetIntField(dtvSoundEffectVO, avcReleaseTime);
    dtvSoundeffct_t.surroundXaValue = env->GetIntField(dtvSoundEffectVO, surroundXaValue);
    dtvSoundeffct_t.surroundXbValue = env->GetIntField(dtvSoundEffectVO, surroundXbValue);
    dtvSoundeffct_t.surroundXkValue = env->GetIntField(dtvSoundEffectVO, surroundXkValue);
    dtvSoundeffct_t.soundDrcThreshold = env->GetIntField(dtvSoundEffectVO, soundDrcThreshold);
    dtvSoundeffct_t.noiseReductionThreshold = env->GetIntField(dtvSoundEffectVO, noiseReductionThreshold);
    dtvSoundeffct_t.echoTime = env->GetIntField(dtvSoundEffectVO, echoTime);
    dtvSoundeffct_t.treble = env->GetIntField(dtvSoundEffectVO, treble);

    jobjectArray arr_soundParameterEqs = (jobjectArray)env->GetObjectField(dtvSoundEffectVO, soundParameterEqs);
    for (int i = 0; i < MAXEQNAD; i++) {
        jobject objarr_soundParameterEqs = env->GetObjectArrayElement(arr_soundParameterEqs, i);
        if (objarr_soundParameterEqs == NULL) {
            ALOGD("objarr_soundParameterEqs  is NULL ");
            break;
        }
        dtvSoundeffct_t.soundParameterEqs[i].eqLevel = env->GetIntField(objarr_soundParameterEqs, fid_eqLevels);
        env->DeleteLocalRef(objarr_soundParameterEqs);
    }

    jobjectArray arr_SoundParameterPeq = (jobjectArray)env->GetObjectField(dtvSoundEffectVO, SoundParameterPeq);
    for (int i = 0; i < MAXEQNAD; i++) {
        jobject objarr_SoundParameterPeq = env->GetObjectArrayElement(arr_SoundParameterPeq, i);
        if (objarr_SoundParameterPeq == NULL) {
            ALOGD("arr_SoundParameterPeq  is NULL ");
            break;
        }
        dtvSoundeffct_t.soundParameterPeqs[i].peqGain = env->GetIntField(objarr_SoundParameterPeq, fid_peqGain);
        dtvSoundeffct_t.soundParameterPeqs[i].peqGc = env->GetIntField(objarr_SoundParameterPeq, fid_peqGc);
        dtvSoundeffct_t.soundParameterPeqs[i].peqQvalue = env->GetIntField(objarr_SoundParameterPeq, fid_peqQvalue);

        env->DeleteLocalRef(objarr_SoundParameterPeq);
    }
    return ms->setBasicSoundEffect(soundeffecttype, dtvSoundeffct_t);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_getBasicSoundEffect
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_AudioManager_native_getBasicSoundEffect
(JNIEnv *env, jobject thiz, jint effecttype) {
    ALOGD("native_getBasicSoundEffect");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getBasicSoundEffect(effecttype);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setInputLevel
 * Signature: (IS)V
 */
void com_mstar_android_tvapi_common_AudioManager_native_setInputLevel
(JNIEnv *env, jobject thiz, jint enAudioInputSource, jshort inputlevel) {
    ALOGD("native_setInputLevel");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setInputLevel(enAudioInputSource, inputlevel);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_getInputLevel
 * Signature: (I)I
 */
jshort com_mstar_android_tvapi_common_AudioManager_native_getInputLevel
(JNIEnv *env, jobject thiz, jint enAudioInputSource) {
    ALOGD("native_getInputLevel ");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getInputLevel(enAudioInputSource);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setDigitalOut
 * Signature: (I)V
 */

void com_mstar_android_tvapi_common_AudioManager_native_setDigitalOut
(JNIEnv *env, jobject thiz, jint spidifmode) {
    ALOGD("native_setDigitalOut");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setDigitalOut(spidifmode);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setInputSource
 * Signature: (I)V
 */
void com_mstar_android_tvapi_common_AudioManager_native_setInputSource
(JNIEnv *env, jobject thiz, jint inputsource) {
    ALOGD("native_setInputSource");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setInputSource(inputsource);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_getInputSource
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_AudioManager_native_getInputSource
(JNIEnv *env, jobject thiz) {
    ALOGD("native_getInputSource");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getInputSource();
}

jint com_mstar_android_tvapi_common_AudioManager_native_setAudioOutput
(JNIEnv *env, jobject thiz, jint audioOutType, jobject parameter) {
    ALOGD("setAudioOutput");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    if (NULL == parameter) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    jclass jclass_parameter = env->GetObjectClass(parameter);
    if (NULL == jclass_parameter) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvCommonException", "object setAudioOutput class can not be null");
        return -1;
    }
    jfieldID speakerDelayTime = env->GetFieldID(jclass_parameter, "speakerDelayTime", "I");
    jfieldID spdifDelayTime = env->GetFieldID(jclass_parameter, "spdifDelayTime", "I");
    jfieldID spdifOutModeInUi = env->GetFieldID(jclass_parameter, "spdifOutModeInUi", "I");
    jfieldID spidfOutModActive = env->GetFieldID(jclass_parameter, "spidfOutModActive", "I");
    AudioOutParameter param;
    param.spdifDelayTime = env->GetIntField(parameter, spdifDelayTime);
    param.speakerDelayTime = env->GetIntField(parameter, speakerDelayTime);
    param.spdifOutmodInUi = env->GetIntField(parameter, spdifOutModeInUi);
    param.spdifOutmodActive = env->GetIntField(parameter, spidfOutModActive);

    return ms->setAudioOutput(audioOutType, param);
}

jint com_mstar_android_tvapi_common_AudioManager_native_checkAtvSoundSystem
(JNIEnv *env, jobject thiz) {
    ALOGD("checkAtvSoundSystem");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->checkAtvSoundSystem();
}

jint com_mstar_android_tvapi_common_AudioManager_native_setAtvInfo
(JNIEnv *env, jobject thiz, jint infotype, jint atvInfoConfig) {
    ALOGD("setAtvInfo");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->setAtvInfo(infotype, atvInfoConfig);
}

jint com_mstar_android_tvapi_common_AudioManager_native_getAtvInfo
(JNIEnv *env, jobject thiz) {
    ALOGD("getAtvInfo");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getAtvInfo();
}

jint com_mstar_android_tvapi_common_AudioManager_native_enableBasicSoundEffect
(JNIEnv *env, jobject thiz, jint soundType, jboolean enable) {
    ALOGD("enableBasicSoundEffect");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->enableBasicSoundEffect(soundType, enable);
}

jint com_mstar_android_tvapi_common_AudioManager_native_getAtvSoundMode
(JNIEnv *env, jobject thiz) {
    ALOGD("native_getAtvSoundMode");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getAtvSoundMode();
}

/*
 * Class:     com_mstar_android_tvapi_common_TvManager
 * Method:    native_setMuteStatus
 * Signature: (II)Z
 */
jboolean com_mstar_android_tvapi_common_AudioManager_native_setMuteStatus
(JNIEnv *env, jobject thiz, jint screenUnMuteTime, jint eSrcType) {
    ALOGD("native_setMuteStatus");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->setMuteStatus((int)screenUnMuteTime, (int)eSrcType);
}

/*
 * Class:     com_mstar_android_tvapi_common_TvManager
 * Method:    setDebugMode
 * Signature: (Z)V
 */
void com_mstar_android_tvapi_common_AudioManager_setDebugMode
(JNIEnv *env, jobject thiz, jboolean mode) {
    ALOGD("setDebugMode");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setDebugMode(mode);
}

jint com_mstar_android_tvapi_common_AudioManager_native_enableAdvancedSoundEffect
(JNIEnv *env, jobject thiz, jint soundType, jint subProcessType) {
    ALOGD("native_enableAdvancedSoundEffect");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->enableAdvancedSoundEffect(soundType, subProcessType);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setAdvancedSoundEffect
 * Signature:(ILcom/mstar/android/tvapi/common/vo/AdvancedSoundParameter;)I
 */
jint com_mstar_android_tvapi_common_AudioManager_native_setAdvancedSoundEffect
(JNIEnv *env, jobject thiz, jint advancedSoundParamType , jobject advancedSoundParameterVo) {
    ALOGD("native_setAdvancedSoundEffect");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    if (NULL == advancedSoundParameterVo) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvCommonException", "object advancedSoundParameterVo can not be null");
        return -1;
    }
    AdvancedSoundParam advancedSoundParameterVo_t;

    jclass jclass_AdvancedSoundParameter = env->FindClass("com/mstar/android/tvapi/common/vo/AdvancedSoundParameter");
    jfieldID jfieldID_AdvancedSoundParameter_paramDolbyPl2vdpkSmod = env->GetFieldID(jclass_AdvancedSoundParameter, "paramDolbyPl2vdpkSmod", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramDolbyPl2vdpkWmod = env->GetFieldID(jclass_AdvancedSoundParameter, "paramDolbyPl2vdpkWmod", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTsxtSetInputGain = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTsxtSetInputGain", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTsxtSetDcGain = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTsxtSetDcGain", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTsxtSetTrubassGain = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTsxtSetTrubassGain", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTsxtSetSpeakerSize = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTsxtSetSpeakerSize", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTsxtSetInputMode = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTsxtSetInputMode", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTsxtSetOutputGain = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTsxtSetOutputGain", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTshdSetInputMode = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTshdSetInputMode", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTshdSetOutputMode = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTshdSetOutputMode", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTshdSetSpeakerSize = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTshdSetSpeakerSize", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTshdSetTrubassControl = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTshdSetTrubassControl", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTshdSetDefinitionControl = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTshdSetDefinitionControl", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTshdSetDcControl = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTshdSetDcControl", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTshdSetSurroundLevel = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTshdSetSurroundLevel", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTshdSetInputGain = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTshdSetInputGain", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTshdSetWowSpaceControl = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTshdSetWowSpaceControl", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTshdSetWowCenterControl = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTshdSetWowCenterControl", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTshdSetWowHdSrs3dMode = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTshdSetWowHdSrs3dMode", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTshdSetLimiterControl = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTshdSetLimiterControl", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTshdSetOutputGain = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTshdSetOutputGain", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundInputGain = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundInputGain", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundDefinitionControl = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundDefinitionControl", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundDcControl = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundDcControl", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTrubassControl = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundTrubassControl", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundSpeakerSize = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundSpeakerSize", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundHardLimiterLevel = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundHardLimiterLevel", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundHardLimiterBoostGain = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundHardLimiterBoostGain", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundHeadRoomGain = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundHeadRoomGain", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTruVolumeMode = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundTruVolumeMode", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTruVolumeRefLevel = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundTruVolumeRefLevel", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTruVolumeMaxGain = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundTruVolumeMaxGain", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTruVolumeNoiseMngrThld = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundTruVolumeNoiseMngrThld", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTruVolumeCalibrate = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundTruVolumeCalibrate", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTruVolumeInputGain = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundTruVolumeInputGain", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTruVolumeOutputGain = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundTruVolumeOutputGain", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundHpfFc = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterSoundHpfFc", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramDtsUltraTvEvoMonoInput = env->GetFieldID(jclass_AdvancedSoundParameter, "paramDtsUltraTvEvoMonoInput", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramDtsUltraTvEvoWideningon = env->GetFieldID(jclass_AdvancedSoundParameter, "paramDtsUltraTvEvoWideningon", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramDtsUltraTvEvoAdd3dBon = env->GetFieldID(jclass_AdvancedSoundParameter, "paramDtsUltraTvEvoAdd3dBon", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramDtsUltraTvEvoPceLevel = env->GetFieldID(jclass_AdvancedSoundParameter, "paramDtsUltraTvEvoPceLevel", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramDtsUltraTvEvoVlfeLevel = env->GetFieldID(jclass_AdvancedSoundParameter, "paramDtsUltraTvEvoVlfeLevel", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramDtsUltraTvSymDefault = env->GetFieldID(jclass_AdvancedSoundParameter, "paramDtsUltraTvSymDefault", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramDtsUltraTvSymMode = env->GetFieldID(jclass_AdvancedSoundParameter, "paramDtsUltraTvSymMode", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramDtsUltraTvSymLevel = env->GetFieldID(jclass_AdvancedSoundParameter, "paramDtsUltraTvSymLevel", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramDtsUltraTvSymReset = env->GetFieldID(jclass_AdvancedSoundParameter, "paramDtsUltraTvSymReset", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramAudysseyDynamicVolCompressMode = env->GetFieldID(jclass_AdvancedSoundParameter, "paramAudysseyDynamicVolCompressMode", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramAudysseyDynamicVolGc = env->GetFieldID(jclass_AdvancedSoundParameter, "paramAudysseyDynamicVolGc", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramAudysseyDynamicVolVolSetting = env->GetFieldID(jclass_AdvancedSoundParameter, "paramAudysseyDynamicVolVolSetting", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramAudysseyDynamicEqEqOffset = env->GetFieldID(jclass_AdvancedSoundParameter, "paramAudysseyDynamicEqEqOffset", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramAudysseyAbxGwet = env->GetFieldID(jclass_AdvancedSoundParameter, "paramAudysseyAbxGwet", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramAudysseyAbxGdry = env->GetFieldID(jclass_AdvancedSoundParameter, "paramAudysseyAbxGdry", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramAudysseyAbxFilset = env->GetFieldID(jclass_AdvancedSoundParameter, "paramAudysseyAbxFilset", "I");

    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterasoundTshdInputGain = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterasoundTshdInputGain", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterasoundTshdOnputGain = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterasoundTshdOnputGain", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterasoundSurrLevelControl = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterasoundSurrLevelControl", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterasoundTrubassCompressorControl = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterasoundTrubassCompressorControl", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterasoundTrubassProcessMode = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterasoundTrubassProcessMode", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterasoundTrubassSpeakerAudio = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterasoundTrubassSpeakerAudio", "I");
    jfieldID jfieldID_AdvancedSoundParameter_paramSrsTheaterasoundTrubassSpeakerAnalysis = env->GetFieldID(jclass_AdvancedSoundParameter, "paramSrsTheaterasoundTrubassSpeakerAnalysis", "I");

    advancedSoundParameterVo_t.paramDolbyPl2vdpkSmod = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramDolbyPl2vdpkSmod);
    advancedSoundParameterVo_t.paramDolbyPl2vdpkWmod = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramDolbyPl2vdpkWmod);
    advancedSoundParameterVo_t.paramSrsTsxtSetInputGain = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTsxtSetInputGain);
    advancedSoundParameterVo_t.paramSrsTsxtSetDcGain = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTsxtSetDcGain);
    advancedSoundParameterVo_t.paramSrsTsxtSetTrubassGain = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTsxtSetTrubassGain);
    advancedSoundParameterVo_t.paramSrsTsxtSetSpeakerSize = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTsxtSetSpeakerSize);
    advancedSoundParameterVo_t.paramSrsTsxtSetInputMode = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTsxtSetInputMode);
    advancedSoundParameterVo_t.paramSrsTsxtSetOutputGain = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTsxtSetOutputGain);
    advancedSoundParameterVo_t.paramSrsTshdSetInputMode = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTshdSetInputMode);
    advancedSoundParameterVo_t.paramSrsTshdSetOutputMode = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTshdSetOutputMode);
    advancedSoundParameterVo_t.paramSrsTshdSetSpeakerSize = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTshdSetSpeakerSize);
    advancedSoundParameterVo_t.paramSrsTshdSetTrubassControl = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTshdSetTrubassControl);
    advancedSoundParameterVo_t.paramSrsTshdSetDefinitionControl = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTshdSetDefinitionControl);
    advancedSoundParameterVo_t.paramSrsTshdSetDcControl = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTshdSetDcControl);
    advancedSoundParameterVo_t.paramSrsTshdSetSurroundLevel = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTshdSetSurroundLevel);
    advancedSoundParameterVo_t.paramSrsTshdSetInputGain = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTshdSetInputGain);
    advancedSoundParameterVo_t.paramSrsTshdSetWowSpaceControl = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTshdSetWowSpaceControl);
    advancedSoundParameterVo_t.paramSrsTshdSetWowCenterControl = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTshdSetWowCenterControl);
    advancedSoundParameterVo_t.paramSrsTshdSetWowHdSrs3dMode = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTshdSetWowHdSrs3dMode);
    advancedSoundParameterVo_t.paramSrsTshdSetLimiterControl = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTshdSetLimiterControl);
    advancedSoundParameterVo_t.paramSrsTshdSetOutputGain = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTshdSetOutputGain);
    advancedSoundParameterVo_t.paramSrsTheaterSoundInputGain = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundInputGain);
    advancedSoundParameterVo_t.paramSrsTheaterSoundDefinitionControl = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundDefinitionControl);
    advancedSoundParameterVo_t.paramSrsTheaterSoundDcControl = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundDcControl);
    advancedSoundParameterVo_t.paramSrsTheaterSoundTrubassControl = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTrubassControl);
    advancedSoundParameterVo_t.paramSrsTheaterSoundSpeakerSize = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundSpeakerSize);
    advancedSoundParameterVo_t.paramSrsTheaterSoundHardLimiterLevel = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundHardLimiterLevel);
    advancedSoundParameterVo_t.paramSrsTheaterSoundHardLimiterBoostGain = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundHardLimiterBoostGain);
    advancedSoundParameterVo_t.paramSrsTheaterSoundHeadRoomGain = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundHeadRoomGain);
    advancedSoundParameterVo_t.paramSrsTheaterSoundTruVolumeMode = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTruVolumeMode);
    advancedSoundParameterVo_t.paramSrsTheaterSoundTruVolumeRefLevel = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTruVolumeRefLevel);
    advancedSoundParameterVo_t.paramSrsTheaterSoundTruVolumeMaxGain = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTruVolumeMaxGain);
    advancedSoundParameterVo_t.paramSrsTheaterSoundTruVolumeNoiseMngrThld = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTruVolumeNoiseMngrThld);
    advancedSoundParameterVo_t.paramSrsTheaterSoundTruVolumeCalibrate = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTruVolumeCalibrate);
    advancedSoundParameterVo_t.paramSrsTheaterSoundTruVolumeInputGain = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTruVolumeInputGain);
    advancedSoundParameterVo_t.paramSrsTheaterSoundTruVolumeOutputGain = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundTruVolumeOutputGain);
    advancedSoundParameterVo_t.paramSrsTheaterSoundHpfFc = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterSoundHpfFc);
    advancedSoundParameterVo_t.paramDtsUltraTvEvoMonoInput = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramDtsUltraTvEvoMonoInput);
    advancedSoundParameterVo_t.paramDtsUltraTvEvoWideningon = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramDtsUltraTvEvoWideningon);
    advancedSoundParameterVo_t.paramDtsUltraTvEvoAdd3dBon = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramDtsUltraTvEvoAdd3dBon);
    advancedSoundParameterVo_t.paramDtsUltraTvEvoPceLevel = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramDtsUltraTvEvoPceLevel);
    advancedSoundParameterVo_t.paramDtsUltraTvEvoVlfeLevel = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramDtsUltraTvEvoVlfeLevel);
    advancedSoundParameterVo_t.paramDtsUltraTvSymDefault = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramDtsUltraTvSymDefault);
    advancedSoundParameterVo_t.paramDtsUltraTvSymMode = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramDtsUltraTvSymMode);
    advancedSoundParameterVo_t.paramDtsUltraTvSymLevel = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramDtsUltraTvSymLevel);
    advancedSoundParameterVo_t.paramDtsUltraTvSymReset = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramDtsUltraTvSymReset);
    advancedSoundParameterVo_t.paramAudysseyDynamicVolCompressMode = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramAudysseyDynamicVolCompressMode);
    advancedSoundParameterVo_t.paramAudysseyDynamicVolGc = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramAudysseyDynamicVolGc);
    advancedSoundParameterVo_t.paramAudysseyDynamicVolVolSetting = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramAudysseyDynamicVolVolSetting);
    advancedSoundParameterVo_t.paramAudysseyDynamicEqEqOffset = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramAudysseyDynamicEqEqOffset);
    advancedSoundParameterVo_t.paramAudysseyAbxGwet = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramAudysseyAbxGwet);
    advancedSoundParameterVo_t.paramAudysseyAbxGdry = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramAudysseyAbxGdry);
    advancedSoundParameterVo_t.paramAudysseyAbxFilset = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramAudysseyAbxFilset);
    advancedSoundParameterVo_t.paramSrsTheaterSoundTshdInputGain = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterasoundTshdInputGain);
    advancedSoundParameterVo_t.paramSrsTheaterSoundTshdutputGain = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterasoundTshdOnputGain);
    advancedSoundParameterVo_t.paramSrsTheaterSoundSurrLevelControl = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterasoundSurrLevelControl);
    advancedSoundParameterVo_t.paramSrsTheaterSoundTrubassCompressorControl = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterasoundTrubassCompressorControl);
    advancedSoundParameterVo_t.paramSrsTheaterSoundTrubassProcessMode = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterasoundTrubassProcessMode);
    advancedSoundParameterVo_t.paramSrsTheaterSoundTrubassSpeakerAudio = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterasoundTrubassSpeakerAudio);
    advancedSoundParameterVo_t.paramSrsTheaterSoundTrubassSpeakerAnalysis = env->GetIntField(advancedSoundParameterVo, jfieldID_AdvancedSoundParameter_paramSrsTheaterasoundTrubassSpeakerAnalysis);
    return ms->setAdvancedSoundEffect(advancedSoundParamType, advancedSoundParameterVo_t);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_getAdvancedSoundEffect
 * Signature: (I)I
 */
jint com_mstar_android_tvapi_common_AudioManager_native_getAdvancedSoundEffect
(JNIEnv *env, jobject thiz, jint effectType) {
    ALOGD("native_getBasicSoundEffect");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->getAdvancedSoundEffect(effectType);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    setSubWooferVolume
 * Signature: (ZS)S
 */
jshort com_mstar_android_tvapi_common_AudioManager_setSubWooferVolume
(JNIEnv *env, jobject thiz, jboolean mute, jshort value) {
    ALOGD("setSubWooferVolume");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->setSubWooferVolume(mute, value);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    executeAmplifierExtendedCommand
 * Signature: (SII[I)S
 */
jshort com_mstar_android_tvapi_common_AudioManager_executeAmplifierExtendedCommand
(JNIEnv *env, jobject thiz, jshort subCmd, jint param1, jint param2, jintArray Param3) {
    ALOGD("executeAmplifierExtendedCommand");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }

    jsize size = env->GetArrayLength(Param3);
    jint *pdata = env->GetIntArrayElements(Param3, NULL);
    int *buffer = new int[size];
    for (int i = 0; i < size; i++) {
        buffer[i] =  pdata[i];
    }
    env->ReleaseIntArrayElements(Param3, pdata, 0);

    short returnvalue =  ms->exectueAmplifierExtendedCommand(subCmd, param1, param2, buffer, size);
    free(buffer);
    return returnvalue;
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    setAmplifierMute
 * Signature: (Z)Z
 */
jboolean com_mstar_android_tvapi_common_AudioManager_setAmplifierMute
(JNIEnv *env, jobject thiz, jboolean mute) {
    ALOGD("setAmplifierMute");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->setAmplifierMute(mute);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setAmplifierEqualizerByMode
 * Signature: (I)Z
 */
void com_mstar_android_tvapi_common_AudioManager_native_setAmplifierEqualizerByMode
(JNIEnv *env, jobject thiz, jint equalizertype) {
    ALOGD("native_setAmplifierEqualizerByMode");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setAmplifierEqualizerByMode(equalizertype);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_disableKtvMixModeMute
 * Signature: (I)S
 */
jshort com_mstar_android_tvapi_common_AudioManager_native_disableKtvMixModeMute
(JNIEnv *env, jobject thiz, jint VolType) {
    ALOGD("native_disableKtvMixModeMute");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->disableKtvMixModeMute(VolType);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_enableKtvMixModeMute
 * Signature: (I)S
 */
jshort com_mstar_android_tvapi_common_AudioManager_native_enableKtvMixModeMute
(JNIEnv *env, jobject thiz, jint VolType) {
    ALOGD("native_enableKtvMixModeMute");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return  ms->enableKtvMixModeMute(VolType);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    setSpeakerDelay
 * Signature: (I)S
 */
jshort com_mstar_android_tvapi_common_AudioManager_setSpeakerDelay
(JNIEnv *env, jobject thiz, jint speakerdelay) {
    ALOGD("setSpeakerDelay");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return  ms->setSpeakerDelay(speakerdelay);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    setSpdifDelay
 * Signature: (I)S
 */
jshort com_mstar_android_tvapi_common_AudioManager_setSpdifDelay
(JNIEnv *env, jobject thiz, jint speakerdelay) {
    ALOGD("setSpdifDelay");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return  ms->setSpdifDelay(speakerdelay);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setKtvMixModeVolume
 * Signature: (ISS)S
 */
jshort com_mstar_android_tvapi_common_AudioManager_native_setKtvMixModeVolume
(JNIEnv *env, jobject thiz, jint ktvMixVolumeType, jshort volume1, jshort volume2) {
    ALOGD("native_setKtvMixModeVolume");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return  ms->setKtvMixModeVolume(ktvMixVolumeType, volume1, volume2);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_getSoundParameter
 * Signature: (II)I
 */
jint com_mstar_android_tvapi_common_AudioManager_native_getSoundParameter
(JNIEnv *env, jobject thiz, jint soundParamType, jint param1) {
    ALOGD("native_getSoundParameter");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return  ms->getSoundParameter(soundParamType, param1);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_SetSoundParameter
 * Signature: (III)I
 */
jshort com_mstar_android_tvapi_common_AudioManager_native_SetSoundParameter
(JNIEnv *env, jobject thiz, jint soundSetParamType, jint param1, jint param2) {
    ALOGD("native_SetSoundParameter");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return  ms->setSoundParameter(soundSetParamType, param1, param2);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setAudioCaptureSource
 * Signature: (II)S
 */
jshort com_mstar_android_tvapi_common_AudioManager_native_setAudioCaptureSource
(JNIEnv *env, jobject thiz, jint audioCaptureDeviceType, jint audiosource) {
    ALOGD("native_setAudioCaptureSource");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return  ms->setAudioCaptureSource(audioCaptureDeviceType, audiosource);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method: native_setOutputSourceInfo
 * Signature: (II)S
 */
jshort com_mstar_android_tvapi_common_AudioManager_native_setOutputSourceInfo
(JNIEnv* env, jobject thiz,jint audiopath, jint audiosource)
{
    ALOGD("native_setOutputSourceInfo");
    sp<AudioManager> ms = getAudioManager(env,thiz);
    if(ms == NULL){
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->setOutputSourceInfo(audiopath , audiosource);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_getKtvSoundInfo
 * Signature: (I)S
 */
jint com_mstar_android_tvapi_common_AudioManager_native_getKtvSoundInfo
(JNIEnv *env, jobject thiz, jint ktvInfoType) {
    ALOGD("native_getKtvSoundInfo");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return  ms->getKtvSoundInfo(ktvInfoType);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setKtvSoundInfo
 * Signature: (IIII)S
 */
jshort com_mstar_android_tvapi_common_AudioManager_native_setKtvSoundInfo
(JNIEnv *env, jobject thiz, jint ktvInfoType, jint param1, jint param2) {
    ALOGD("native_setKtvSoundInfo");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return  ms->setKtvSoundInfo(ktvInfoType, param1, param2);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setKtvSoundTrack
 * Signature: (I)I
 */
jint com_mstar_android_tvapi_common_AudioManager_native_setKtvSoundTrack
(JNIEnv *env, jobject thiz, jint ktvsoundmode) {
    ALOGD("native_setKtvSoundTrack");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return  ms->setKtvSoundTrack(ktvsoundmode);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setCommonAudioInfo
 * Signature: (III)Z
 */
jboolean com_mstar_android_tvapi_common_AudioManager_native_setCommonAudioInfo
(JNIEnv *env, jobject thiz, jint audioinfoType, jint param1, jint param2) {
    ALOGD("native_setCommonAudioInfo");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return  ms->setCommonAudioInfo(audioinfoType, param1, param2);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setAudioLanguage1
 * Signature: (I)V
 */
void com_mstar_android_tvapi_common_AudioManager_native_setAudioLanguage1
(JNIEnv *env, jobject thiz, jint enLanguage) {
    ALOGD("native_setAudioLanguage1");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setAudioLanguage1(enLanguage);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setAudioLanguage2
 * Signature: (I)V
 */
void com_mstar_android_tvapi_common_AudioManager_native_setAudioLanguage2
(JNIEnv *env, jobject thiz, jint enLanguage) {
    ALOGD("native_setAudioLanguage2");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setAudioLanguage2(enLanguage);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_getAudioLanguage1
 * Signature: ()I
 */
jint com_mstar_android_tvapi_common_AudioManager_native_getAudioLanguage1
(JNIEnv *env, jobject thiz) {
    ALOGD("native_getAudioLanguage1");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->getAudioLanguage1();
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_getAudioLanguage2
 * Signature: (I)V
 */
jint com_mstar_android_tvapi_common_AudioManager_native_getAudioLanguage2
(JNIEnv *env, jobject thiz) {
    ALOGD("native_getAudioLanguage2");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->getAudioLanguage2();
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setAudioSource
 * Signature: (II)I
 */
jint com_mstar_android_tvapi_common_AudioManager_native_setAudioSource
(JNIEnv *env, jobject thiz, jint eInputSrc, jint eAudioProcessType) {
    ALOGD("native_setAudioSource");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return  ms->setAudioSource(eInputSrc, eAudioProcessType);

}

void com_mstar_android_tvapi_common_AudioManager_native_setADEnable
(JNIEnv *env, jobject thiz, jboolean enable) {
    ALOGD("native_setADEnable");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setADEnable(enable);
}

void com_mstar_android_tvapi_common_AudioManager_native_setADAbsoluteVolume
(JNIEnv *env, jobject thiz, jint volume) {
    ALOGD("native_setADAbsoluteVolume");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setADAbsoluteVolume(volume);
}

void com_mstar_android_tvapi_common_AudioManager_native_setAutoHOHEnable
(JNIEnv *env, jobject thiz, jboolean enable) {
    ALOGD("native_setAutoHOHEnable");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setAutoHOHEnable(enable);

}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    setAutoVolume
 * Signature: (Z)V
 */
void com_mstar_android_tvapi_common_AudioManager_setAutoVolume
(JNIEnv *env, jobject thiz, jboolean enable) {
    ALOGD("setAutoVolume");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setAutoVolume(enable);
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    getAutoVolume
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_common_AudioManager_getAutoVolume
(JNIEnv *env, jobject thiz) {
    ALOGD("getAutoVolume");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->getAutoVolume();
}

/*
 * Class: com_mstar_android_tvapi_common_AudioManager
 * Method: setHDMITx_HDBypass
 * Signature: (Z)V
 */
void com_mstar_android_tvapi_common_AudioManager_setHDMITx_HDBypass
(JNIEnv *env, jobject thiz, jboolean enable) {
    ALOGD("setHDMITx_HDBypass");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setAudioHDMITx_HDBypass(enable);
}

/*
 * Class: com_mstar_android_tvapi_common_AudioManager
 * Method: getHDMITx_HDBypass
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_common_AudioManager_getHDMITx_HDBypass
(JNIEnv *env, jobject thiz) {
    ALOGD("getHDMITx_HDBypass");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->getAudioHDMITx_HDBypass();
}

/*
 * Class: com_mstar_android_tvapi_common_AudioManager
 * Method: getHDMITx_HDBypass_Capability
 * Signature: ()Z
 */
jboolean com_mstar_android_tvapi_common_AudioManager_getHDMITx_HDBypass_Capability
(JNIEnv *env, jobject thiz) {
    ALOGD("getHDMITx_HDBypass_Capability");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return false;
    }
    return ms->getAudioHDMITx_HDBypass_Capability();
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    getSoundAC3PlusInfo
 * Signature: (I)I
 */
jint com_mstar_android_tvapi_common_AudioManager_getSoundAC3PlusInfo
(JNIEnv *env, jobject thiz, jint infoType) {
    ALOGD("getSoundAC3PlusInfo");
    jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvUnsupportedException", "Unsupported tvapi");
    return -1;
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    setSoundAC3PlusInfo
 * Signature: (III)Z
 */
jboolean com_mstar_android_tvapi_common_AudioManager_setSoundAC3PlusInfo
(JNIEnv *env, jobject thiz, jint infoType, jint param1, jint param2) {
    ALOGD("setSoundAC3PlusInfo");
    jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvUnsupportedException", "Unsupported tvapi");
    return -1;
}

/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    setOutputSourceInfo
 * Signature: (II)S
 */
jshort com_mstar_android_tvapi_common_AudioManager_setOutputSourceInfo
(JNIEnv *env, jobject thiz, jint eAudioPath, jint eSource) {
    ALOGD("setOutputSourceInfo");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return -1;
    }
    return ms->setOutputSourceInfo(eAudioPath, eSource);
}
/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setMicSSound
 * Signature: (I)V
 */
void com_mstar_android_tvapi_common_AudioManager_setMicSSound
(JNIEnv *env, jobject thiz, jint Value) {
    ALOGD("setMicSSound");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setMicSSound(Value);
}
/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_setMicEcho
 * Signature: (I)V
 */
void com_mstar_android_tvapi_common_AudioManager_setMicEcho
(JNIEnv *env, jobject thiz, jint Value) {
    ALOGD("setMicEcho");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return;
    }
    ms->setMicEcho(Value);
}
/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_getAudioLanguage2
 * Signature: (I)V
 */
jint com_mstar_android_tvapi_common_AudioManager_getMicSSound
(JNIEnv *env, jobject thiz) {
    ALOGD("getMicSSound");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->getMicSSound();
}
/*
 * Class:     com_mstar_android_tvapi_common_AudioManager
 * Method:    native_getMicEcho
 * Signature: (I)V
 */
jint com_mstar_android_tvapi_common_AudioManager_getMicEcho
(JNIEnv *env, jobject thiz) {
    ALOGD("getMicEcho");
    sp<AudioManager> ms = getAudioManager(env, thiz);
    if (ms == NULL) {
        jniThrowException(env, "com/mstar/android/tvapi/common/exception/TvIpcException", "can not connect to server");
        return 0;
    }
    return ms->getMicEcho();
}

//------------------------------------------------------------------------------------
static const char *classPathName = "com/mstar/android/tvapi/common/AudioManager";

static JNINativeMethod methods[] = {
    {"native_init",               "()V", (void *)com_mstar_android_tvapi_common_AudioManager_native_init},
    {"native_setup",              "(Ljava/lang/Object;)V", (void *)com_mstar_android_tvapi_common_AudioManager_native_setup},
    {"native_finalize",           "()V", (void *)com_mstar_android_tvapi_common_AudioManager_native_finalize},
    {"native_setADEnable",        "(Z)V", (void *)com_mstar_android_tvapi_common_AudioManager_native_setADEnable},
    {"native_setADAbsoluteVolume", "(I)V", (void *)com_mstar_android_tvapi_common_AudioManager_native_setADAbsoluteVolume},
    {"native_setAutoHOHEnable",   "(Z)V", (void *)com_mstar_android_tvapi_common_AudioManager_native_setAutoHOHEnable},
    {"native_getAudioVolume",     "(I)B", (void *)com_mstar_android_tvapi_common_AudioManager_native_getAudioVolume},
    {"native_setAudioVolume",     "(IB)V", (void *)com_mstar_android_tvapi_common_AudioManager_native_setAudioVolume},
    {"native_enableMute",         "(I)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_enableMute},
    {"native_disableMute",        "(I)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_disableMute},
    {"native_isMuteEnabled",      "(I)Z", (void *)com_mstar_android_tvapi_common_AudioManager_native_isMuteEnabled},
    {"native_setAtvMtsMode",      "(I)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_setAtvMtsMode},
    {"native_getAtvMtsMode",      "()I", (void *)com_mstar_android_tvapi_common_AudioManager_native_getAtvMtsMode},
    {"native_setToNextAtvMtsMode", "()I", (void *)com_mstar_android_tvapi_common_AudioManager_native_setToNextAtvMtsMode},
    {"native_setAtvSoundSystem",  "(I)Z", (void *)com_mstar_android_tvapi_common_AudioManager_native_setAtvSoundSystem},
    {"native_getAtvSoundSystem",  "()I", (void *)com_mstar_android_tvapi_common_AudioManager_native_getAtvSoundSystem},
    {"native_setDtvOutputMode",   "(I)V", (void *)com_mstar_android_tvapi_common_AudioManager_native_setDtvOutputMode},
    {"native_getDtvOutputMode",   "()I", (void *)com_mstar_android_tvapi_common_AudioManager_native_getDtvOutputMode},
    {"native_setBasicSoundEffect", "(ILcom/mstar/android/tvapi/common/vo/DtvSoundEffect;)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_setBasicSoundEffect},
    {"native_getBasicSoundEffect", "(I)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_getBasicSoundEffect},
    {"native_setInputLevel",      "(IS)V", (void *)com_mstar_android_tvapi_common_AudioManager_native_setInputLevel},
    {"native_getInputLevel",      "(I)S", (void *)com_mstar_android_tvapi_common_AudioManager_native_getInputLevel},
    {"native_setDigitalOut",      "(I)V", (void *)com_mstar_android_tvapi_common_AudioManager_native_setDigitalOut},
    {"native_setInputSource",     "(I)V", (void *)com_mstar_android_tvapi_common_AudioManager_native_setInputSource},
    {"native_getInputSource",     "()I", (void *)com_mstar_android_tvapi_common_AudioManager_native_getInputSource},
    {"native_setAudioOutput",     "(ILcom/mstar/android/tvapi/common/vo/AudioOutParameter;)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_setAudioOutput},
    {"native_checkAtvSoundSystem", "()I", (void *)com_mstar_android_tvapi_common_AudioManager_native_checkAtvSoundSystem},
    {"native_setAtvInfo",         "(II)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_setAtvInfo},
    {"native_getAtvInfo",         "()I", (void *)com_mstar_android_tvapi_common_AudioManager_native_getAtvInfo},
    {"native_enableBasicSoundEffect", "(IZ)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_enableBasicSoundEffect},
    {"native_getAtvSoundMode",    "()I", (void *)com_mstar_android_tvapi_common_AudioManager_native_getAtvSoundMode},
    {"native_setMuteStatus",      "(II)Z", (void *)com_mstar_android_tvapi_common_AudioManager_native_setMuteStatus},
    {"setDebugMode",              "(Z)V", (void *)com_mstar_android_tvapi_common_AudioManager_setDebugMode},
    {"native_enableAdvancedSoundEffect", "(II)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_enableAdvancedSoundEffect},
    {"native_setAdvancedSoundEffect", "(ILcom/mstar/android/tvapi/common/vo/AdvancedSoundParameter;)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_setAdvancedSoundEffect},
    {"native_getAdvancedSoundEffect", "(I)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_getAdvancedSoundEffect},
    {"setSubWooferVolume",        "(ZS)S", (void *)com_mstar_android_tvapi_common_AudioManager_setSubWooferVolume},
    {"executeAmplifierExtendedCommand", "(SII[I)S", (void *)com_mstar_android_tvapi_common_AudioManager_executeAmplifierExtendedCommand},
    {"setAmplifierMute",          "(Z)Z", (void *)com_mstar_android_tvapi_common_AudioManager_setAmplifierMute},
    {"native_setAmplifierEqualizerByMode", "(I)V", (void *)com_mstar_android_tvapi_common_AudioManager_native_setAmplifierEqualizerByMode},
    {"native_disableKtvMixModeMute", "(I)S", (void *)com_mstar_android_tvapi_common_AudioManager_native_disableKtvMixModeMute},
    {"native_enableKtvMixModeMute", "(I)S", (void *)com_mstar_android_tvapi_common_AudioManager_native_enableKtvMixModeMute},
    {"setSoundSpeakerDelay",      "(I)S", (void *)com_mstar_android_tvapi_common_AudioManager_setSpeakerDelay},
    {"setSpeakerDelay",           "(I)S", (void *)com_mstar_android_tvapi_common_AudioManager_setSpeakerDelay},
    {"setSoundSpdifDelay",        "(I)S", (void *)com_mstar_android_tvapi_common_AudioManager_setSpdifDelay},
    {"setSpdifDelay",             "(I)S", (void *)com_mstar_android_tvapi_common_AudioManager_setSpdifDelay},
    {"native_setKtvMixModeVolume", "(ISS)S", (void *)com_mstar_android_tvapi_common_AudioManager_native_setKtvMixModeVolume},
    {"native_getSoundParameter",  "(II)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_getSoundParameter},
    {"native_SetSoundParameter",  "(III)S", (void *)com_mstar_android_tvapi_common_AudioManager_native_SetSoundParameter},
    {"native_setAudioCaptureSource", "(II)S", (void *)com_mstar_android_tvapi_common_AudioManager_native_setAudioCaptureSource},
    {"native_setOutputSourceInfo","(II)S",      (void *)com_mstar_android_tvapi_common_AudioManager_native_setOutputSourceInfo},
    {"native_setKtvSoundInfo",    "(III)S", (void *)com_mstar_android_tvapi_common_AudioManager_native_setKtvSoundInfo},
    {"native_getKtvSoundInfo",    "(I)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_getKtvSoundInfo},
    {"native_setKtvSoundTrack",   "(I)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_setKtvSoundTrack},
    {"native_setCommonAudioInfo", "(III)Z", (void *)com_mstar_android_tvapi_common_AudioManager_native_setCommonAudioInfo},
    {"native_setAudioSource",     "(II)I", (void *)com_mstar_android_tvapi_common_AudioManager_native_setAudioSource},
    {"native_setAudioLanguage1", "(I)V", (void *)com_mstar_android_tvapi_common_AudioManager_native_setAudioLanguage1},
    {"native_setAudioLanguage2", "(I)V", (void *)com_mstar_android_tvapi_common_AudioManager_native_setAudioLanguage2},
    {"native_getAudioLanguage1", "()I", (void *)com_mstar_android_tvapi_common_AudioManager_native_getAudioLanguage1},
    {"native_getAudioLanguage2", "()I", (void *)com_mstar_android_tvapi_common_AudioManager_native_getAudioLanguage2},
    {"setAutoVolume",             "(Z)V", (void *)com_mstar_android_tvapi_common_AudioManager_setAutoVolume},
    {"getAutoVolume",             "()Z", (void *)com_mstar_android_tvapi_common_AudioManager_getAutoVolume},
    {"getSoundAC3PlusInfo",             "(I)I", (void *)com_mstar_android_tvapi_common_AudioManager_getSoundAC3PlusInfo},
    {"setSoundAC3PlusInfo",             "(III)Z", (void *)com_mstar_android_tvapi_common_AudioManager_setSoundAC3PlusInfo},
    {"setOutputSourceInfo",       "(II)S", (void *)com_mstar_android_tvapi_common_AudioManager_setOutputSourceInfo},
    {"setHDMITx_HDBypass",             "(Z)V", (void *)com_mstar_android_tvapi_common_AudioManager_setHDMITx_HDBypass},
    {"getHDMITx_HDBypass",             "()Z", (void *)com_mstar_android_tvapi_common_AudioManager_getHDMITx_HDBypass},
    {"getHDMITx_HDBypass_Capability",  "()Z", (void *)com_mstar_android_tvapi_common_AudioManager_getHDMITx_HDBypass_Capability},
    {"setMicSSound",     "(I)V", (void *)com_mstar_android_tvapi_common_AudioManager_setMicSSound},
    {"setMicEcho",     "(I)V", (void *)com_mstar_android_tvapi_common_AudioManager_setMicEcho},
    {"getMicSSound",     "()I", (void *)com_mstar_android_tvapi_common_AudioManager_getMicSSound},
    {"getMicEcho",     "()I", (void *)com_mstar_android_tvapi_common_AudioManager_getMicEcho},
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

    ALOGD("JNI_OnLoad");

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
