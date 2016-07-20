LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4 \
    guava \
    Baidu-OAuth-SDK-Android-G-2.0.0 \
    Baidu-PCS-SDK-Android-L2-2.1.0 \
    dropbox-android-sdk-1.6.3 \
    httpmime-4.0.3 \
    microsoft_aad \
    microsoft_services \
    onedrive_sdk \
    universalimageloader \
    xUtils-2.6.14 \
    nineoldanimation \
    gson2 \
    json
    
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_JAVA_LIBRARIES = scifly.android
LOCAL_PACKAGE_NAME := SciflyCloudPhotoAlbum
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

    
##### build static jar
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    gson2:./libs/gson-2.3.1.jar \
    json:./libs/json_simple-1.1.jar \
    Baidu-OAuth-SDK-Android-G-2.0.0:./libs/Baidu-OAuth-SDK-Android-G-2.0.0.jar \
    Baidu-PCS-SDK-Android-L2-2.1.0:./libs/Baidu-PCS-SDK-Android-L2-2.1.0.jar \
    dropbox-android-sdk-1.6.3:./libs/dropbox-android-sdk-1.6.3.jar \
    httpmime-4.0.3:./libs/httpmime-4.0.3.jar \
    microsoft_aad:./libs/microsoft_aad.jar \
    microsoft_services:./libs/microsoft_services.jar \
    onedrive_sdk:./libs/onedrive_sdk.jar \
    universalimageloader:./libs/universalimageloader.jar \
    xUtils-2.6.14:./libs/xUtils-2.6.14.jar \
    
include $(BUILD_MULTI_PREBUILT)

