#define NDEBUG 0

#include <jni.h>
#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <sys/stat.h>
#include <unistd.h>
#include <stdlib.h>

#include <utils/Log.h>

#define  LOG_TAG    "BootLoaderJNI"

#ifndef NELEM
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

struct bootloader_message {
    char command[32];
    char status[32];
    char recovery[768];

    // The 'recovery' field used to be 1024 bytes.  It has only ever
    // been used to store the recovery command line, so 768 bytes
    // should be plenty.  We carve off the last 256 bytes to store the
    // stage string (for multistage packages) and possible future
    // expansion.
    char stage[32];
    char reserved[224];
};

typedef struct Volume {
    const char *blk_device;
}Volume;

// ------------------------------------
// for misc partitions on block devices
// ------------------------------------

static void wait_for_device(const char* fn) {
    int tries = 0;
    int ret;
    struct stat buf;
    do {
        ++tries;
        ret = stat(fn, &buf);
        if (ret) {
            ALOGD("stat %s try %d: %s\n", fn, tries, strerror(errno));
            sleep(1);
        }
    } while (ret && tries < 10);
    if (ret) {
        ALOGE("failed to stat %s\n", fn);
    }
}

static int get_bootloader_message(struct bootloader_message *out,
                                        const Volume* v) {
    wait_for_device(v->blk_device);
    FILE* f = fopen(v->blk_device, "rb");
    if (f == NULL) {
        ALOGE("Can't open %s\n(%s)\n", v->blk_device, strerror(errno));
        return -1;
    }
    struct bootloader_message temp;
    int count = fread(&temp, sizeof(temp), 1, f);
    if (count != 1) {
        ALOGE("Failed reading %s\n(%s)\n", v->blk_device, strerror(errno));
        return -1;
    }
    if (fclose(f) != 0) {
        ALOGE("Failed closing %s\n(%s)\n", v->blk_device, strerror(errno));
        return -1;
    }
    memcpy(out, &temp, sizeof(temp));
    return 0;
}

static int set_bootloader_message(const struct bootloader_message *in,
                                        const Volume* v) {
    wait_for_device(v->blk_device);
    FILE* f = fopen(v->blk_device, "wb");
    if (f == NULL) {
        ALOGE("Can't open %s\n(%s)\n", v->blk_device, strerror(errno));
        return -1;
    }
    int count = fwrite(in, sizeof(*in), 1, f);
    if (count != 1) {
        ALOGE("Failed writing %s\n(%s)\n", v->blk_device, strerror(errno));
        return -1;
    }
    if (fclose(f) != 0) {
        ALOGE("Failed closing %s\n(%s)\n", v->blk_device, strerror(errno));
        return -1;
    }
    return 0;
}

static jboolean native_set_bootloader_message(JNIEnv *env, jobject thiz,
		jstring dev_path) {

	Volume *v = (Volume*) malloc(sizeof(Volume));
	v->blk_device = strdup((char *) env->GetStringUTFChars(dev_path, 0));

	int ret = -1;
	struct bootloader_message boot_info;

	memset(&boot_info, 0, sizeof(boot_info));
	strcpy(boot_info.command, "boot-recovery");
	strcpy(boot_info.recovery, "recovery\n");
	ret = set_bootloader_message(&boot_info, v);
	if (!ret) {
		ALOGD("set bootload success !\n");
		return true;
	} else {
		ALOGE("set bootload failed:%s\n", strerror(errno));
		return false;
	}
}


static jstring native_get_bootloader_message(JNIEnv *env, jobject thiz,
		jstring dev_path) {

	Volume *v = (Volume*) malloc(sizeof(Volume));
	v->blk_device = strdup((char *) env->GetStringUTFChars(dev_path, 0));

	int ret = -1;
	struct bootloader_message boot_info;

	memset(&boot_info, 0, sizeof(boot_info));
	get_bootloader_message(&boot_info, v);

	ALOGD("boot_info.command:%s\n", boot_info.command);
	ALOGD("boot_info.recovery:%s\n", boot_info.recovery);

	char *message;
	asprintf(&message, "boot_info.command:%s\nboot_info.recovery:%s\n",boot_info.command ,boot_info.recovery);
	return env->NewStringUTF(message);
}

static void native_clear_bootloader_message(JNIEnv *env, jobject thiz,
		jstring dev_path) {

	Volume *v = (Volume*) malloc(sizeof(Volume));
	v->blk_device = strdup((char *) env->GetStringUTFChars(dev_path, 0));

	int ret = -1;
	struct bootloader_message boot_info;

	memset(&boot_info, 0, sizeof(boot_info));
	ret = set_bootloader_message(&boot_info, v);
	if (!ret) {
		ALOGD("clear bootload success !\n");
	} else {
		ALOGE("clear bootload failed:%s\n", strerror(errno));
	}
}


static JNINativeMethod gMethods[] = {
    { "setBootloaderMessage", "(Ljava/lang/String;)Z", (void*) native_set_bootloader_message },
    { "getBootloaderMessage", "(Ljava/lang/String;)Ljava/lang/String;", (void*) native_get_bootloader_message },
    { "clearBootloaderMessage", "(Ljava/lang/String;)V",  (void*) native_clear_bootloader_message },
};

static int registerNativeMethods(JNIEnv* env, const char* className,
        JNINativeMethod* method, int num) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        ALOGE("registerNativeMethods, unable to find class '%s'", className);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, method, num) < 0) {
        ALOGE("RegisterNatives failed for '%s'", className);
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

    if (!registerNativeMethods(env, "com/android/settings/update/ota/BootLoader",
            gMethods, NELEM(gMethods))) {
        ALOGE("register method failed");
        goto bail;
    }
    result = JNI_VERSION_1_4;

bail:
    return result;
}
