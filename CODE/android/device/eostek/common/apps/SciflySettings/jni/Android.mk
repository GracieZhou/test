
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libjni_bootloader_scifly

LOCAL_FORCE_STATIC_EXECUTABLE := true

LOCAL_CERTIFICATE := platform
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    com_android_settings_update_ota_BootLoader.cpp

LOCAL_SHARED_LIBRARIES := \
    libcutils libutils

include $(BUILD_SHARED_LIBRARY)
