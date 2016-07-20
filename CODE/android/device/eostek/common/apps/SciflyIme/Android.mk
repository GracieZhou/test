LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)


LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    $(call all-java-files-under, src) \
    $(call all-subdir-Iaidl-files)
    
LOCAL_JNI_SHARED_LIBRARIES := libjni_sciflyime libjni_latinime_scifly libWnnJpnDic_scifly libwnndict_scifly
ifeq ($(TARGET_ARCH), arm64)
LOCAL_32_BIT_ONLY := true
endif
LOCAL_REQUIRED_MODULES := libjni_sciflyime libjni_latinime_scifly libWnnJpnDic_scifly libwnndict_scifly

LOCAL_PACKAGE_NAME := SciflyIme
LOCAL_CERTIFICATE := platform
LOCAL_OVERRIDES_PACKAGES := MLatinIME MPinyinIME

LOCAL_JAVA_LIBRARIES := \
    scifly.android

# Make sure our dictionary file is not compressed, so we can read it with
# a raw file descriptor.
LOCAL_AAPT_FLAGS := -0 .dat
LOCAL_AAPT_FLAGS += -0 .dict

LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)

MY_PATH := $(LOCAL_PATH)

include $(MY_PATH)/jni/Android.mk

