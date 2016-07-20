LOCAL_PATH := $(call my-dir)

# Build the Java library.
# ============================================================
include $(CLEAR_VARS)

LOCAL_MODULE := scifly.android
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := \
    $(call all-java-files-under, src) \
    $(call all-Iaidl-files-under,src)

LOCAL_REQUIRED_MODULES := \
    scifly.android.xml \
    scifly.android_doc \
    libscifly_virtualmouse_jni \
    libscifly_security_jni

LOCAL_STATIC_JAVA_LIBRARIES := \
    libeosplayer \
    commons-lang3-3.1 \
    java-common-1.0 \
    servlet-api-2.5 \
    stun-1.0 \
    udp-commu-1.0 \
    libtmc \
    mina-core \
    cpe_jar

include $(BUILD_JAVA_LIBRARY)

# The documentation
# ============================================================
include $(CLEAR_VARS)

LOCAL_MODULE := scifly.android_doc
LOCAL_DROIDDOC_OPTIONS := scifly.android
LOCAL_MODULE_CLASS := JAVA_LIBRARIES
LOCAL_DROIDDOC_USE_STANDARD_DOCLET := false
LOCAL_SRC_FILES := \
    $(call all-java-files-under, src) \
    $(call all-html-files-under, src)

include $(BUILD_DROIDDOC)

# Install permissions for this shared jar
# ============================================================
include $(CLEAR_VARS)

LOCAL_MODULE := scifly.android.xml
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH := $(TARGET_OUT_ETC)/permissions
LOCAL_SRC_FILES := $(LOCAL_MODULE)

include $(BUILD_PREBUILT)

