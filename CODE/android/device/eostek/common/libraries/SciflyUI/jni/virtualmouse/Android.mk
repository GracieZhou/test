LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SHARED_LIBRARIES := \
    libcutils libutils

LOCAL_CERTIFICATE := platform
LOCAL_MODULE    := libscifly_virtualmouse_jni

LOCAL_SRC_FILES := \
    scifly_virtualmouse.cpp

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE_TAGS := optional

include $(BUILD_SHARED_LIBRARY)
