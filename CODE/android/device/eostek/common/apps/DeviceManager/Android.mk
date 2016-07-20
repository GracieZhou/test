LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# 此路径为SciflyStyle的路径，目前外部apk只能引用其中的资源
style_dir := device/eostek/common/apps/LIBRARY-SciflyStyle

res_dirs := $(LOCAL_PATH)/res $(style_dir)/res

LOCAL_SRC_FILES := \
    $(call all-subdir-java-files) \
    $(call all-subdir-Iaidl-files)
    
LOCAL_RESOURCE_DIR := $(res_dirs)

# 注意如果要引用SciflyStyle的资源，一定要加这几行
LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages com.eostek.scifly.style

LOCAL_PACKAGE_NAME := DeviceManager
LOCAL_CERTIFICATE := platform
LOCAL_MODULE_TAGS := optional
LOCAL_DEX_PREOPT := false

LOCAL_JAVA_LIBRARIES := \
    scifly.android

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \
    sciflydatacache \
    nineoldanimation 

include $(BUILD_PACKAGE)


include $(call all-makefiles-under,$(LOCAL_PATH))
