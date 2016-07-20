LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# <modulename>:<filename>
#LOCAL_PREBUILT_EXECUTABLES := \
#    XXX:XXX

LOCAL_MODULE_TAGS := optional
include $(BUILD_MULTI_PREBUILT)

# ==============================================================================
include $(call all-makefiles-under,$(LOCAL_PATH))
