LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := sdl_image

LOCAL_C_INCLUDES := $(LOCAL_PATH) $(LOCAL_PATH)/../jpeg $(LOCAL_PATH)/../png $(LOCAL_PATH)/../sdl/include

LOCAL_CFLAGS := $(LOCAL_C_INCLUDES:%=-I%) \
	-DLOAD_PNG -DLOAD_JPG -DLOAD_GIF -DLOAD_BMP

LOCAL_CPP_EXTENSION := .cpp

# Note this simple makefile var substitution, you can find even simpler examples in different Android projects
LOCAL_SRC_FILES := $(notdir $(wildcard $(LOCAL_PATH)/*.c))

LOCAL_STATIC_LIBRARIES := png jpeg

LOCAL_SHARED_LIBRARIES := sdl

LOCAL_LDLIBS := -lz

include $(BUILD_SHARED_LIBRARY)

