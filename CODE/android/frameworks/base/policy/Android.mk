LOCAL_PATH:= $(call my-dir)

# the library
# ============================================================
include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-java-files-under, src)
            
LOCAL_MODULE := android.policy

# MStar Android Patch Begin
LOCAL_JAVA_LIBRARIES := com.mstar.android
# MStar Android Patch End

# EosTek Patch Begin
LOCAL_JAVA_LIBRARIES += scifly.android
# EosTek Patch End
include $(BUILD_JAVA_LIBRARY)

# additionally, build unit tests in a separate .apk
include $(call all-makefiles-under,$(LOCAL_PATH))
