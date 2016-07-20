LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
 LOCAL_MODULE_TAGS := optional
 LOCAL_MODULE := PrebuiltGmsCore
 LOCAL_MODULE_CLASS := APPS
 LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
 LOCAL_CERTIFICATE := PRESIGNED
 LOCAL_PRIVILEGED_MODULE := true
 LOCAL_DEX_PREOPT := false
 LOCAL_SRC_FILES := $(LOCAL_MODULE)$(COMMON_ANDROID_PACKAGE_SUFFIX)
 LOCAL_32_BIT_ONLY := true
 LOCAL_JNI_SHARED_LIBRARIES_ABI := arm
 LOCAL_PREBUILT_JNI_LIBS := libs/libAppDataSearch.so libs/libconscrypt_gmscore_jni.so libs/libgames_rtmp_jni.so libs/libgcastv2_base.so libs/libgcastv2_support.so libs/libgmscore.so libs/libgms-ocrclient.so libs/libjgcastservice.so libs/libNearbyApp.so libs/libsslwrapper_jni.so libs/libWhisper.so
							
include $(BUILD_PREBUILT)
