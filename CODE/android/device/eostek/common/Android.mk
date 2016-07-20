
LOCAL_PATH := $(call my-dir)

common_dirs := apps executables libraries

include $(call all-named-subdir-makefiles,$(common_dirs))

