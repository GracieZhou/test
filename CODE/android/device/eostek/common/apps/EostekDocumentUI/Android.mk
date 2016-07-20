LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

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

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4 guava universalimageloader_documentui
LOCAL_JAVA_LIBRARIES = scifly.android
LOCAL_PACKAGE_NAME := EostekDocumentUI
LOCAL_CERTIFICATE := platform

LOCAL_OVERRIDES_PACKAGES := DocumentsUI
    
include $(BUILD_PACKAGE)


include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    universalimageloader_documentui:./libs/universalimageloader.jar
include $(BUILD_MULTI_PREBUILT)
