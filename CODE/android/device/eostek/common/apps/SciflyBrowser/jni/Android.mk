LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := libbrowser2util_jni
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    browserUtil.cpp

LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/../../../executables/browser/

LOCAL_SHARED_LIBRARIES := \
    libutils \
    libbinder \
    liblog \
    libbrowserservice

LOCAL_REQUIRED_MODULES := libbrowserservice

include $(BUILD_SHARED_LIBRARY)
