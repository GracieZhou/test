LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_JAVA_LIBRARIES := \
	scifly.android \
	com.mstar.android
			
LOCAL_PACKAGE_NAME := HLauncher2
LOCAL_CERTIFICATE := platform
#LOCAL_PRIVILEGED_MODULE := true
LOCAL_OVERRIDES_PACKAGES := Launcher3 Gallery Gallery2 Galaxy4 SoundRecorder Email DeskClock Calendar Calculator \
    HTMLViewer MLocalMM2 MTvTest Music MWidiDemo MLatinIME LatinIME PinyinIME DMR DMP PhotoTable BasicDreams Exchange2 VideoEditor QuickSearchBox \
    HoloSpiralWallpaper LiveWallpapers LiveWallpapersPicker MagicSmokeWallpapers NoiseField MSetupWizard \
    PacProcessor PhaseBeam PhotoTable TelephonyProvider VisualizationWallpapers Camera2 MTvPlayer MTvHotkey MTvMisc  Settings\
    WAPPushManager BackupRestoreConfirmation CalendarProvider Contacts ContactsProvider TeleService WallpaperCropper DMS MTvBoot\
    MDummyAPK
    
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_DEX_PREOPT := false

LOCAL_STATIC_JAVA_LIBRARIES := \
	Android-query-full \
	Glide \
	HttpCoreH638 \
	HttpMimeH638 \
	SupportV4 \
	GooglePlayServicesH638 \
	HttpClientH638 \
	GsonH638 \
	ExtreamaxH638 \
	
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
	Android-query-full:jars/android-query-full.0.25.10.jar \
	Glide:jars/glide-3.6.1.jar \
	HttpClientH638:jars/httpclient.jar \
	HttpCoreH638:jars/httpcore-4.3.2.jar \
	HttpMimeH638:jars/httpmime-4.3.5.jar \
	GooglePlayServicesH638:jars/google-play-services.jar \
    SupportV4:jars/android-support-v4.jar \
    GsonH638:jars/gson-2.3.1.jar \
    ExtreamaxH638:jars/ExtreamaxAPI-1.1.3.jar
	
include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))

