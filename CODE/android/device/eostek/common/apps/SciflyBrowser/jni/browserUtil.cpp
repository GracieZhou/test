/*
** Copyright (c) 2010-2011 MStar Semiconductor, Inc.
** All rights reserved.
**
** Unless otherwise stipulated in writing, any and all information contained
** herein regardless in any format shall remain the sole proprietary of
** MStar Semiconductor Inc. and be kept in strict confidence
** ("MStar Confidential Information") by the recipient.
** Any unauthorized act including without limitation unauthorized disclosure,
** copying, use, reproduction, sale, distribution, modification, disassembling,
** reverse engineering and compiling of the contents of MStar Confidential
** Information is unlawful and strictly prohibited. MStar hereby reserves the
** rights to any and all damages, losses, costs and expenses resulting therefrom.
*/

#define LOG_TAG "BrowserUtilJni"
#include <utils/Log.h>

#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/prctl.h>
#include <fcntl.h>
#include <linux/input.h>
#include <linux/uinput.h>
#include <dirent.h>
#include <binder/ProcessState.h>
#include <binder/IPCThreadState.h>
#include <binder/IServiceManager.h>
#include <IBrowserService.h>

using namespace android;

static sp<IBrowserService> sBrowserService = NULL;
static bool sBrowserServiceLive = true;

const sp<IBrowserService>& getBrowserService() {
    if (sBrowserService != NULL) {
        return sBrowserService;
    }
    ALOGI("[Client Process]getBrowserService");
    sp<IServiceManager> sm = defaultServiceManager();
    sp<IBinder> binder = NULL;
    uint32_t retry = 0;

    do {
        binder = sm->getService(String16("mstar.browser"));
        if ((binder != NULL)) {
            break;
        }
        if (retry >= 1) {
            sBrowserServiceLive = false;
            break;
        }
        ALOGI("BrowserService not published, waiting...");
        usleep(500000);
        retry++;
    } while (true);

    sBrowserService =  interface_cast<IBrowserService>(binder);
    if (sBrowserService == NULL) {
        ALOGI("Can't get mstar.Browser service! client exit");
    }

    return sBrowserService;
}

static void jni_MouseMove(JNIEnv *env, jobject thiz, jint x, jint y) {
    if (sBrowserServiceLive == false) {
        ALOGI("browser server is dead?");
        return;
    }

    ALOGI("jni_MouseMove be called.  X:%d,Y:%d\n",x,y);
    const sp<IBrowserService>& service = getBrowserService();
    if (service != NULL) {
        ALOGI("start call RPC  function......");
        int ret = service->moveCursor(x, y);
        ALOGI("end of call RPC function. ret=%d\n", ret);
    }
}

static void jni_MouseLeftClick(JNIEnv *env, jobject thiz) {
    if (sBrowserServiceLive == false) {
        ALOGI("browser server is dead?");
        return;
    }

    ALOGI("jni_MouseLeftClick  be  called.....");
    const sp<IBrowserService>& service = getBrowserService();
    if (service != NULL) {
        ALOGI("start call RPC  function......");
        int ret = service->mouseLeftClick();
        ALOGI("end of call RPC function. ret=%d\n", ret);
    }
}

static void jni_MouseLeftDown(JNIEnv *env, jobject thiz) {
    if (sBrowserServiceLive == false) {
        ALOGI("browser server is dead?");
        return;
    }

    ALOGI("jni_MouseLeftDown  be  called.....");
    const sp<IBrowserService>& service = getBrowserService();
    if (service != NULL) {
        ALOGI("start call RPC  function......");
        int ret = service->mouseLeftDown();
        ALOGI("end of call RPC function. ret=%d\n", ret);
    }
}

static void jni_MouseLeftUp(JNIEnv *env, jobject thiz) {
    if (sBrowserServiceLive == false) {
        ALOGI("browser server is dead?");
        return;
    }

    ALOGI("jni_MouseLeftUp  be  called.....");
    const sp<IBrowserService>& service = getBrowserService();
    if (service != NULL) {
        ALOGI("start call RPC  function......");
        int ret = service->mouseLeftUp();
        ALOGI("end of call RPC function. ret=%d\n", ret);
    }
}

static void jni_closeInput(JNIEnv *env, jobject thiz) {
    if (sBrowserServiceLive == false) {
        ALOGI("browser server is dead?");
        return;
    }

    ALOGI("jni_closeInput be called.....");
    const sp<IBrowserService>& service = getBrowserService();
    if (service != NULL) {
        ALOGI("start call RPC  function......");
        int ret = service->closeInputDevice();
        ALOGI("end of call RPC function. ret=%d\n", ret);
    }
}

static const char *classPathName = "com/android/browser/BrowserUtil";

static JNINativeMethod methods[] = {
    {"nativeMoveCursor" , "(II)V", (void*)jni_MouseMove},
    {"nativeMouseLeftClick" , "()V", (void*)jni_MouseLeftClick},
    {"nativeMouseLeftDown" , "()V", (void*)jni_MouseLeftDown},
    {"nativeMouseLeftUp" , "()V", (void*)jni_MouseLeftUp},
    {"nativeCloseInput" , "()V", (void*)jni_closeInput}
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
 * JNI Initialization
 */
jint JNI_OnLoad(JavaVM* vm, void* reserved) {

    JNIEnv* env = NULL;
    jint result = -1;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        ALOGE("GetEnv failed");
        return JNI_ERR;
    }
    ALOG_ASSERT(env, "Could not retrieve the env!");

    if (registerNatives(env) != JNI_TRUE) {
        ALOGE("registerNatives failed");
        return JNI_ERR;
    }
   return JNI_VERSION_1_4;
}

