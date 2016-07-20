LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# UiTemplate为demo，所以设置为tests，此处请根据需求设置
LOCAL_MODULE_TAGS := tests

# 此路径为SciflyStyle的路径，目前外部apk只能引用其中的资源
style_dir := device/eostek/common/apps/LIBRARY-SciflyStyle

src_dirs := src
res_dirs := $(LOCAL_PATH)/res $(style_dir)/res

LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dirs))
LOCAL_RESOURCE_DIR := $(res_dirs)

# 注意如果要引用SciflyStyle的资源，一定要加这几行
LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages com.eostek.scifly.style

LOCAL_PACKAGE_NAME := UiTemplate

# 生成APK的路径，可以自己定义，如果是系统APK，则不要定义该变量
LOCAL_MODULE_PATH := $(LOCAL_PATH)/bin

# 为了从U盘安装方便，所以没有odex化，请根据需求设置
LOCAL_DEX_PREOPT := false

include $(BUILD_PACKAGE)
