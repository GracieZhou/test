LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# <modulename>:<filename>
#LOCAL_PREBUILT_LIBS := \
#    libxxx:libxxx.so

#LOCAL_PREBUILT_JAVA_LIBRARIES := \
#    com.google.XXX:com.google.XXX.jar

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    libtmc:./libs/cpe/tmc.jar \
    commons-lang3-3.1:./libs/cpe/commons-lang3-3.1.jar \
    cpe_jar:./libs/cpe/cpe_jar.jar \
    java-common-1.0:./libs/cpe/java-common-1.0.jar \
    mina-core:./libs/cpe/mina-core.jar \
    servlet-api-2.5:./libs/cpe/servlet-api-2.5.jar \
    stun-1.0:./libs/cpe/stun-1.0.jar \
    udp-commu-1.0:./libs/cpe/udp-commu-1.0.jar \
    libeosplayer:./libs/media/EosPlayer.jar

LOCAL_MODULE_TAGS := optional

include $(BUILD_MULTI_PREBUILT)

# ==============================================================================
include $(LOCAL_PATH)/scifly.android.mk

# ==============================================================================
include $(call all-makefiles-under,$(LOCAL_PATH))
