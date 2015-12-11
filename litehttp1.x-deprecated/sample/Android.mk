LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_STATIC_JAVA_LIBRARIES := litehttp
LOCAL_MODULE_TAGS := optional
#LOCAL_JAVA_LIBRARIES := telephony-common 
LOCAL_SRC_FILES := $(call all-java-files-under, $(src)) 
#LOCAL_JNI_SHARED_LIBRARIES := 
LOCAL_PACKAGE_NAME := SampleLiteHttp 
#LOCAL_CERTIFICATE := platform
#LOCAL_PRIVILEGED_MODULE := true
LOCAL_PROGUARD_ENABLED := disabled
include $(BUILD_PACKAGE)
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags
####################################
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := litehttp:libs/litehttp.jar 
include $(BUILD_MULTI_PREBUILT)

