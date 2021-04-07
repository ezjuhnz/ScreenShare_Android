# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)
include_directories(
        ${CMAKE_CURRENT_SOURCE_DIR}/libs/include/ #头文件多的话可以继续往下加
        ${CMAKE_CURRENT_SOURCE_DIR}/libs/include/libyuv/
)

#添加自己所需的动态库

add_library( yuv    #库名字 去掉了 lib 与 .a
        SHARED   #必须的
        IMPORTED #必须的
        )

set_target_properties(
        yuv
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${CMAKE_ANDROID_ARCH_ABI}/libyuv.so #库在的路径，以cmakeList.txt所在的路径起
)

add_library( audio_preprocessing    #库名字 去掉了 lib 与 .a
        SHARED   #必须的
        IMPORTED #必须的
        )

set_target_properties(
        audio_preprocessing
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${CMAKE_ANDROID_ARCH_ABI}/libaudio_preprocessing.so #库在的路径，以cmakeList.txt所在的路径起
)

add_library( mediacore    #库名字 去掉了 lib 与 .a
        SHARED   #必须的
        IMPORTED #必须的
        )

set_target_properties(
        mediacore
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${CMAKE_ANDROID_ARCH_ABI}/libmediacore.so #库在的路径，以cmakeList.txt所在的路径起
)

add_library( anychatcore    #库名字 去掉了 lib 与 .a
        SHARED   #必须的
        IMPORTED #必须的
        )

set_target_properties(
        anychatcore
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/libs/${CMAKE_ANDROID_ARCH_ABI}/libanychatcore.so #库在的路径，以cmakeList.txt所在的路径起
)



add_library( # 设置生成的动态库的名字,最终生成的库为libcalUtil.so
             CImageUtil
             #动态库,如果想生成静态库使用STATIC.
             SHARED
             #提供你要编译成库的源文件.
             src/main/cpp/CImageUtil.c )



find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

target_link_libraries( # Specifies the target library.
        CImageUtil
        yuv
        audio_preprocessing
        mediacore
        anychatcore
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

