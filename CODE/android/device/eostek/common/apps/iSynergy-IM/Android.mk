LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
 LOCAL_MODULE_TAGS := optional
 LOCAL_MODULE := iSynergy-IM
 LOCAL_MODULE_CLASS := APPS
 LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
 LOCAL_CERTIFICATE := platform
 LOCAL_SRC_FILES := $(LOCAL_MODULE)$(COMMON_ANDROID_PACKAGE_SUFFIX)
 LOCAL_32_BIT_ONLY := true
 LOCAL_JNI_SHARED_LIBRARIES_ABI := arm
 LOCAL_PREBUILT_JNI_LIBS := libs/libjingle_soapclient_jni.so libs/libcaptetown.so libs/libchinalink.so libs/libsystem.so
include $(BUILD_PREBUILT)
