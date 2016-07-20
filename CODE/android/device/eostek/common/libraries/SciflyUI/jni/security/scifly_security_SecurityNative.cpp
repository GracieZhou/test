/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

#define LOG_TAG "SecurityJNI"

#include <jni.h>
#include <utils/Log.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <dirent.h>
#include <errno.h>
#include <sys/stat.h>
#include <unistd.h>
#include <time.h>
#include <fcntl.h>
#include <pwd.h>

#include <private/android_filesystem_config.h>

#ifndef NELEM
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

static char g_strSignature[256];
static char g_strPkgName[32];

extern void Mstar_DecodeBase64(char *strSource, int nSourceLen, char *strDest);
extern void Mstar_EncodeBase64(char *strSource, int nSourceLen, char *strDest);

int getPkgName(int pid) {
    ALOGD("pid : %d\n", pid);

    char strTmp[128] = "";
    char strPkg[128] = "";
    char strDest[128] = "wuhan#$(^";
    char statline[1024];
    char cmdline[1024];
    char macline[1024];
    struct stat stats;
    int fd, r;
    int length = 0;

    sprintf(statline, "/proc/%d", pid);
    stat(statline, &stats);

    sprintf(statline, "/proc/%d/stat", pid);
    sprintf(cmdline, "/proc/%d/cmdline", pid);
    snprintf(macline, sizeof(macline), "/proc/%d/attr/current", pid);
    fd = open(cmdline, O_RDONLY);
    if (fd == 0) {
        r = 0;
    } else {
        r = read(fd, cmdline, 1023);
        close(fd);
        if (r < 0) {
            r = 0;
        }
    }
    cmdline[r] = 0;

    fd = open(statline, O_RDONLY);
    if (fd == 0) {
        return -1;
    }

    r = read(fd, statline, 1023);
    close(fd);
    if (r < 0) {
        return -1;
    }
    statline[r] = 0;

    strcpy(strPkg, cmdline);
    length = strlen(cmdline);
    if (length > 12) {
        // 把字符串str从索引值2开始到结尾赋给s
        strncpy(strTmp, &strPkg[length - 12], 12);
        strcat(strTmp, "jrm");
        strcat(strDest, strTmp);
    } else {
        strcat(strPkg, "jrm");
        strcat(strDest, strPkg);
    }

    ALOGD("pkg = %s\n", strDest);
    strcpy(g_strPkgName, strDest);

    return 0;
}

static jboolean scifly_security_SecurityNative_checkPermission(JNIEnv *env,
        jobject thiz, jint pid, jstring signature) {
    ALOGD("checkPermission pid : %d\n", pid);

    // save the input signature
    strcpy(g_strSignature,
            (char *) env->GetStringUTFChars(signature, 0));
    if (!(strcmp(g_strSignature, ""))) {
        ALOGD("signature is null!\n");
        return false;
    }

    // get package name according to the pid
    if (getPkgName(pid) != 0) {
        return false;
    }

    char strSignature[128] = "";
    char encData_base64[256] = "";

    // 对正在运行应用的包名做MD5运算
    Mstar_EncodeBase64((char *) g_strPkgName, strlen(g_strPkgName),
            encData_base64);
    strcpy(strSignature, &encData_base64[8]);

    ALOGD("source signature : %s\n", g_strSignature);
    ALOGD("dest signature : %s\n", strSignature);

    if (!strcmp(g_strSignature, strSignature)) {
        return true;
    } else {
        ALOGD("checkPermission failed!\n");
        return false;
    }

    return true;
}

static JNINativeMethod gMethods[] = { { "checkPermission",
        "(ILjava/lang/String;)Z",
        (void*) scifly_security_SecurityNative_checkPermission }, };

static int registerNativeMethods(JNIEnv* env, const char* clsName,
        JNINativeMethod* method, int num) {
    jclass clazz;
    clazz = env->FindClass(clsName);
    if (clazz == NULL) {
        ALOGE("registerNativeMethods, unable to find class '%s'", clsName);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, method, num) < 0) {
        ALOGE("RegisterNatives failed for '%s'", clsName);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

// Returns the JNI version on success, -1 on failure.
jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    jint result = -1;
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        ALOGE("getEnv failed");
        goto bail;
    }

    if (!registerNativeMethods(env, "scifly/security/SecurityNative", gMethods,
            NELEM(gMethods))) {
        ALOGE("register method failed");
        goto bail;
    }

    result = JNI_VERSION_1_4;

    bail: return result;
}
