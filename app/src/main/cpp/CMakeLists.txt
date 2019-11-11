# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

add_definitions("-DDYNAMIC_ES3")
set(OPENGL_LIB GLESv2)
set(CMAKE_ANDROID_ARCH_ABI armeabi-v7a)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
include_directories(include)

add_library( # Sets the name of the library.
        Grp

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        LibSrc/JNIMain.cpp
        LibSrc/AndroidApp.cpp
        LibSrc/AndroidApp.cpp
        LibSrc/aflOpenGL.cpp
		LibSrc/afl3DBase.cpp
		LibSrc/aflSock.cpp
		LibSrc/afl3DObject.cpp 
		LibSrc/aflStd.cpp 
		LibSrc/aflOpenGLUnit.cpp 
		LibSrc/afl3DWorld.cpp 
		LibSrc/afl3DField.cpp 
		LibSrc/aflInput.cpp
		Main.cpp  Unit.cpp Camera.cpp Effect.cpp
)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib
		android
		# Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
		Grp
		android
		# Links the target library to the log library
		# included in the NDK.
		log
		${OPENGL_LIB}
)