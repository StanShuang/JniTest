//
// Created by YangShuang on 2023/2/8.
//
#include <jni.h>
#include <string>
#include <cstring>
#include <android/bitmap.h>
#include <android/log.h>
#include "LogUtils.h"

#define LOGE(FORMAT, ...)  __android_log_print(ANDROID_LOG_ERROR,"Encryptor",##__VA_ARGS__);

#define MAKE_RGB565(r, g, b) ((((r)  >> 3) << 11) | (((g)   >> 2) << 5) | ((b)   >> 3))
#define MAKE_ARGB(a, r, g, b) ((a&0xff)<<24) | ((r&0xff)<<16) | ((g&0xff)<<8) | (b&0xff)

#define RGB565_R(p) ((((p) & 0xF800)    >>11) << 3)
#define RGB565_G(p) ((((p) & 0x7E0 )    >>5) << 2)
#define RGB565_B(p) ( ((p) & 0x1F )    << 3)

#define RGB8888_A(p) (p & (0xff<<24) >> 24 )
#define RGB8888_R(p) (p & (0xff<<16) >> 16 )
#define RGB8888_G(p) (p & (0xff<<8)  >> 8 )
#define RGB8888_B(p) (p & (0xff) )

extern "C"
JNIEXPORT void JNICALL
Java_com_example_stan_jnitest_jni_JniBitmapAction_nativeProcessBitmap(JNIEnv *env, jobject thiz,
                                                                      jobject bitmap) {
    if (bitmap == NULL) {
        LOGE("s%", "bitmap is null..")
        return;
    }
    AndroidBitmapInfo bitmapInfo;
    //初始化函数，作用是将某一块内存中的全部设置为指定的值。
    memset(&bitmapInfo, 0, sizeof(bitmapInfo));
    // Need add "jnigraphics" into target_link_libraries in CMakeLists.txt
    AndroidBitmap_getInfo(env, bitmap, &bitmapInfo);


    // Lock the bitmap to get the buffer
    void *pixels = NULL;
    int res = AndroidBitmap_lockPixels(env, bitmap, &pixels);

    // From top to bottom
    int x = 0;
    int y = 0;
    for (y = 0; y < bitmapInfo.height; ++y) {
        // From left to right
        for (x = 0; x < bitmapInfo.width; ++x) {
            int a = 0, r = 0, g = 0, b = 0;
            void *pixel = NULL;
            // Get each pixel by format
            if (bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
                pixel = ((uint32_t *) pixels) + y * bitmapInfo.width + x;
                int r, g, b;
                uint32_t v = *((uint32_t *) pixel);
                r = RGB8888_R(v);
                g = RGB8888_G(v);
                b = RGB8888_B(v);
                int sum = r + g + b;
                *((uint32_t *) pixel) = MAKE_ARGB(0xff, sum / 3, sum / 3, sum / 3);
            } else if (bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGB_565) {
                pixel = ((uint16_t *) pixels) + y * bitmapInfo.width + x;
                int r, g, b;
                uint16_t v = *((uint16_t *) pixel);
                r = RGB565_R(v);
                g = RGB565_G(v);
                b = RGB565_B(v);
                int sum = r + g + b;
                *((uint16_t *) pixel) = MAKE_RGB565(sum / 3, sum / 3, sum / 3);
            }
        }
    }
    AndroidBitmap_unlockPixels(env, bitmap);

}