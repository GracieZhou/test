LOCAL_PATH:= $(call my-dir)

common_src_files := \
	Mstar_base64.cpp \
	eshell.cpp

common_shared_libraries := \
	libcutils
	
common_static_libraries := \
	libc \
	liblog

include $(CLEAR_VARS)

LOCAL_FORCE_STATIC_EXECUTABLE := true
LOCAL_SRC_FILES:= $(common_src_files)
LOCAL_SHARED_LIBRARIES := $(common_shared_libraries)
LOCAL_STATIC_LIBRARIES := $(common_static_libraries)

LOCAL_MODULE:= eshell

LOCAL_MODULE_PATH := $(TARGET_OUT_OPTIONAL_EXECUTABLES)
LOCAL_MODULE_TAGS := optional

include $(BUILD_EXECUTABLE)
