LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
    $(call all-subdir-java-files) \
    $(call all-subdir-Iaidl-files)

LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform
LOCAL_PACKAGE_NAME := SciflyVoiceController
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_STATIC_JAVA_LIBRARIES := \
    Msc

LOCAL_PROGUARD_ENABLED := disabled
#LOCAL_DEX_PREOPT := false

ifneq ($(filter muji monet,$(TARGET_BOARD_PLATFORM)),)
LOCAL_32_BIT_ONLY := true
LOCAL_JNI_SHARED_LIBRARIES_ABI := arm
LOCAL_PREBUILT_JNI_LIBS := \
    libs/armeabi/libmsc.so \
    libs/armeabi/libvoicesearch.so
endif

include $(BUILD_PACKAGE)

##################################################
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    Msc:libs/Msc.jar

include $(BUILD_MULTI_PREBUILT)
