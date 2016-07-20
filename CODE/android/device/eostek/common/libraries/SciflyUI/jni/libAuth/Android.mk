LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_PREBUILT_LIBS := libAuthorizeNative20:libAuthorizeNative20.so

LOCAL_MODULE_TAGS := optional
include $(BUILD_MULTI_PREBUILT)

