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
#include <string.h>
#include <jni.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/prctl.h>
#include <fcntl.h>
#include <linux/input.h>
#include <linux/uinput.h>
#include <dirent.h>
#include <android/log.h>

#define  LOG_TAG    "VirtualMouseJNI"
#define  LOGV(...)   __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGW(...)   __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)   __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#ifndef NELEM
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

const char *gDeviceName = "Virtual Mouse";
static int gInputFD = -1;
static int gUinputFD = -1;

static int KEYCODE_ENTER = 66;
static int KEYCODE_BACK = 4;

// Setup the mouse device
int initDevice() {
    struct uinput_user_dev uinp;  // uInput device structure

    // Open the input device
    gUinputFD = open("/dev/uinput", O_WRONLY | O_NDELAY);
    if (gUinputFD == 0) {
        LOGE("Unable to open /dev/uinput");
        return -1;
    }

    system("su");
    system("chmod 777 /dev/uinput");

    memset(&uinp, 0x00, sizeof(uinp));
    strncpy(uinp.name, gDeviceName, strlen(gDeviceName));
    uinp.id.version = 1;
    uinp.id.bustype = BUS_USB;

    // Setup the Mouse device
    ioctl(gUinputFD, UI_SET_EVBIT, EV_KEY);
    ioctl(gUinputFD, UI_SET_EVBIT, EV_REL);
    ioctl(gUinputFD, UI_SET_KEYBIT, BTN_MIDDLE);
    ioctl(gUinputFD, UI_SET_KEYBIT, BTN_LEFT);
    ioctl(gUinputFD, UI_SET_KEYBIT, BTN_RIGHT);
    ioctl(gUinputFD, UI_SET_RELBIT, REL_X);
    ioctl(gUinputFD, UI_SET_RELBIT, REL_Y);

    // Create input device into input sub-system
    if (write(gUinputFD, &uinp, sizeof(uinp)) != sizeof(uinp)) {
        LOGE("first write return failed.\n");
        return -1;
    }

    if (ioctl(gUinputFD, UI_DEV_CREATE)) {
        LOGE("ioctl UI_DEV_CREATE return failed.\n");
        return -1;
    }

    return 1;
}

int openInputDevice(const char* inputName) {
    int fd = -1;
    system("su");
    system("busybox chmod 777 /dev/input/*");
    const char *dirname = "/dev/input";
    char devname[PATH_MAX];
    char *filename;
    DIR *dir;
    struct dirent *de;
    dir = opendir(dirname);
    if (dir == NULL)
    {
        return -1;
    }

    strcpy(devname, dirname);
    filename = devname + strlen(devname);
    *filename++ = '/';
    while ((de = readdir(dir))) {
        if (de->d_name[0] == '.'
                && (de->d_name[1] == '\0'
                        || (de->d_name[1] == '.' && de->d_name[2] == '\0')))
        {
            continue;
        }

        strcpy(filename, de->d_name);
        LOGV("devname : %s\n", devname);
        fd = open(devname, O_RDWR);
        if (fd >= 0) {
            char name[80];
            if (ioctl(fd, EVIOCGNAME(sizeof(name) - 1), &name) < 1) {
                name[0] = '\0';
            }
            LOGV("open %s  success! device name : %s\n", devname, name);
            if (!strcmp(name, inputName)) {
                LOGV("device name : %s\n", devname);
                break;
            } else {
                close(fd);
                fd = -1;
            }
        } else {
            LOGE("open %s  failed!\n", devname);
        }
    }
    closedir(dir);
    if (fd < 0) {
        LOGE("couldn't find '%s' input device\n", inputName);
    }

    return fd;
}

static void scifly_tv_VirtualMouse_open(JNIEnv *env, jobject thiz) {
    if (gInputFD == -1) {
        gInputFD = openInputDevice(gDeviceName);

        if (gInputFD == -1) {
            if (-1 != initDevice()) {
                gInputFD = openInputDevice(gDeviceName);
            }
        }
    }
}

static void scifly_tv_VirtualMouse_close(JNIEnv *env, jobject thiz) {
    if (gInputFD == -1) {
        return;
    }

    if (-1 != gUinputFD) {
        ioctl(gUinputFD, UI_DEV_DESTROY);
        close(gUinputFD);
    }

    close(gInputFD);
    gInputFD = -1;
}

static void scifly_tv_VirtualMouse_move(JNIEnv *env, jobject thiz, jint x, jint y) {
    LOGV("scifly_tv_VirtualMouse_move x : %d, y : %d\n", x, y);
    if (gInputFD == -1) {
        LOGE("can not open device\n");
        return;
    }

    struct input_event ievent[10];
    struct timespec now;
    struct timeval tv;
    tv.tv_sec = 0;
    tv.tv_usec = 0;

    clock_gettime(CLOCK_MONOTONIC, &now);
    ievent[0].time.tv_sec = now.tv_sec;
    ievent[0].time.tv_usec = now.tv_nsec / 1000;
    ievent[0].type = EV_REL;
    ievent[0].code = REL_X;
    ievent[0].value = x;

    ievent[1].time.tv_sec = now.tv_sec;
    ievent[1].time.tv_usec = now.tv_nsec / 1000;
    ievent[1].type = EV_REL;
    ievent[1].code = REL_Y;
    ievent[1].value = y;

    ievent[2].time.tv_sec = now.tv_sec;
    ievent[2].time.tv_usec = now.tv_nsec / 1000;
    ievent[2].type = EV_SYN;
    ievent[2].code = SYN_REPORT;
    ievent[2].value = 0;

    write(gInputFD, &ievent[0], sizeof(ievent[0]));
    write(gInputFD, &ievent[1], sizeof(ievent[1]));
    write(gInputFD, &ievent[2], sizeof(ievent[2]));
}

static void scifly_tv_VirtualMouse_click(JNIEnv *env, jobject thiz,
        jint keyCode) {
    LOGV("scifly_tv_VirtualMouse_click keyCode : %d\n", keyCode);
    if (gInputFD == -1) {
        LOGE("can not open device\n");
        return;
    }
    struct input_event ievent[3];
    struct timespec now;
    struct timeval tv;
    tv.tv_sec = 0;
    tv.tv_usec = 0;

    if (keyCode == KEYCODE_ENTER) {
        clock_gettime(CLOCK_MONOTONIC, &now);
        memset(ievent, 0, sizeof(ievent));

        ievent[0].time.tv_sec = now.tv_sec;
        ievent[0].time.tv_usec = now.tv_nsec / 1000;
        ievent[0].type = EV_KEY;
        ievent[0].code = BTN_LEFT;
        ievent[0].value = 1;

        ievent[1].time.tv_sec = now.tv_sec;
        ievent[1].time.tv_usec = now.tv_nsec / 1000;
        ievent[1].type = EV_SYN;
        ievent[1].code = SYN_REPORT;
        ievent[1].value = 0;

        write(gInputFD, &ievent[0], sizeof(ievent[0]));
        write(gInputFD, &ievent[1], sizeof(ievent[1]));

        clock_gettime(CLOCK_MONOTONIC, &now);
        memset(ievent, 0, sizeof(ievent));

        ievent[0].time.tv_sec = now.tv_sec;
        ievent[0].time.tv_usec = now.tv_nsec / 1000;
        ievent[0].type = EV_KEY;
        ievent[0].code = BTN_LEFT;
        ievent[0].value = 0;

        ievent[1].time.tv_sec = now.tv_sec;
        ievent[1].time.tv_usec = now.tv_nsec / 1000;
        ievent[1].type = EV_SYN;
        ievent[1].code = SYN_REPORT;
        ievent[1].value = 0;

        write(gInputFD, &ievent[0], sizeof(ievent[0]));
        write(gInputFD, &ievent[1], sizeof(ievent[1]));
    }
    else if (keyCode == KEYCODE_BACK) {
        clock_gettime(CLOCK_MONOTONIC, &now);
        memset(ievent, 0, sizeof(ievent));

        ievent[0].time.tv_sec = now.tv_sec;
        ievent[0].time.tv_usec = now.tv_nsec / 1000;
        ievent[0].type = EV_KEY;
        ievent[0].code = BTN_RIGHT;
        ievent[0].value = 1;

        ievent[1].time.tv_sec = now.tv_sec;
        ievent[1].time.tv_usec = now.tv_nsec / 1000;
        ievent[1].type = EV_SYN;
        ievent[1].code = SYN_REPORT;
        ievent[1].value = 0;

        write(gInputFD, &ievent[0], sizeof(ievent[0]));
        write(gInputFD, &ievent[1], sizeof(ievent[1]));

        clock_gettime(CLOCK_MONOTONIC, &now);
        memset(ievent, 0, sizeof(ievent));

        ievent[0].time.tv_sec = now.tv_sec;
        ievent[0].time.tv_usec = now.tv_nsec / 1000;
        ievent[0].type = EV_KEY;
        ievent[0].code = BTN_RIGHT;
        ievent[0].value = 0;

        ievent[1].time.tv_sec = now.tv_sec;
        ievent[1].time.tv_usec = now.tv_nsec / 1000;
        ievent[1].type = EV_SYN;
        ievent[1].code = SYN_REPORT;
        ievent[1].value = 0;

        write(gInputFD, &ievent[0], sizeof(ievent[0]));
        write(gInputFD, &ievent[1], sizeof(ievent[1]));
    }
}

static JNINativeMethod gMethods[] = {
    { "native_open", "()V", (void*) scifly_tv_VirtualMouse_open },
    { "native_close", "()V", (void*) scifly_tv_VirtualMouse_close },
    { "native_move", "(II)V",  (void*) scifly_tv_VirtualMouse_move },
    { "native_click", "(I)V", (void*) scifly_tv_VirtualMouse_click },
};

static int registerNativeMethods(JNIEnv* env, const char* className,
        JNINativeMethod* method, int num) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        LOGE("registerNativeMethods, unable to find class '%s'", className);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, method, num) < 0) {
        LOGE("RegisterNatives failed for '%s'", className);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

// Returns the JNI version on success, -1 on failure.
jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    jint result = -1;
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("getEnv failed");
        goto bail;
    }

    if (!registerNativeMethods(env, "scifly/virtualmouse/VirtualMouseNative",
            gMethods, NELEM(gMethods))) {
        LOGE("register method failed");
        goto bail;
    }
    result = JNI_VERSION_1_4;

    bail: return result;
}
