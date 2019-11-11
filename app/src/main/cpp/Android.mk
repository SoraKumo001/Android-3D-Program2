LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LIBSRC_PATH		:= LibSrc/
LOCAL_CPPFLAGS  := -std=c++11
LOCAL_MODULE    := Grp
LOCAL_SRC_FILES := $(LIBSRC_PATH)JNIMain.cpp  $(LIBSRC_PATH)AndroidApp.cpp $(LIBSRC_PATH)aflOpenGL.cpp $(LIBSRC_PATH)afl3DBase.cpp $(LIBSRC_PATH)aflSock.cpp $(LIBSRC_PATH)afl3DObject.cpp $(LIBSRC_PATH)aflStd.cpp $(LIBSRC_PATH)aflOpenGLUnit.cpp $(LIBSRC_PATH)afl3DWorld.cpp $(LIBSRC_PATH)afl3DField.cpp $(LIBSRC_PATH)aflInput.cpp
LOCAL_SRC_FILES += Main.cpp  Unit.cpp Camera.cpp Effect.cpp
LOCAL_LDLIBS:=-llog -lGLESv2 -lOpenSLES -landroid
LOCAL_LDFLAG := -fuse-ld=mcld
LOCAL_C_INCLUDES += ./jni/include
ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
	LOCAL_ARM_NEON := true
endif
include $(BUILD_SHARED_LIBRARY)
