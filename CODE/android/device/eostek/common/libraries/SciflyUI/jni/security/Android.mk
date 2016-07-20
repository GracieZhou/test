LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SHARED_LIBRARIES := \
    libcutils libutils

LOCAL_CERTIFICATE := platform
LOCAL_MODULE    := libscifly_security_jni

LOCAL_SRC_FILES := \
    scifly_security_SecurityNative.cpp \
    Mstar_base64.cpp

LOCAL_MODULE_TAGS := optional

include $(BUILD_SHARED_LIBRARY)
