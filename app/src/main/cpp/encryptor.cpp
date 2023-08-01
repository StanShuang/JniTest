//
// Created by YangShuang on 2022/3/9.
//
#include <cstdio>
#include <jni.h>
#include <android/log.h>
#include <cstring>
#include <malloc.h>
#include <dirent.h>
#include "LogUtils.h"
#include <android/bitmap.h>

#define LOGE(FORMAT, ...)  __android_log_print(ANDROID_LOG_ERROR,"Encryptor",##__VA_ARGS__);

extern "C"
JNIEXPORT void JNICALL
Java_com_example_stan_jnitest_jni_Encryptor_createFile(JNIEnv *env, jobject instance,
                                                       jstring normalPath_) {
    //获取字符串保存到jvm内存中
    const char *normalPath = env->GetStringUTFChars(normalPath_, nullptr);
    //打开normalPath,wb:打开或者新建一个二进制文件，只允许写数据
    FILE *fp = fopen(normalPath, "wb");
    //把字符串写到指定的流stream中，但不包括空字符
    fputs("Hi, this file is created by JNI, and my name is 103style", fp);
    //关闭流fp，释放所有缓冲区
    fclose(fp);
    //释放JVM保存的字符串内存
    env->ReleaseStringUTFChars(normalPath_, normalPath);

}

char passWord[] = "103style";

extern "C"
JNIEXPORT void JNICALL
Java_com_example_stan_jnitest_jni_Encryptor_encryption(JNIEnv *env, jobject instance,
                                                       jstring normalPath_, jstring encryptPath_) {
    //获取字符串保存到jvm内存中
    const char *normalPath = env->GetStringUTFChars(normalPath_, nullptr);
    const char *encryptPath = env->GetStringUTFChars(encryptPath_, nullptr);

    LOGE("normalPath = %s,encryptPath = %s", normalPath, encryptPath);

    //rb:只读打开一个二进制文件，允许读数据
    //wb:只写打开或者新建一个二进制文件，允许写数据
    FILE *normal_fp = fopen(normalPath, "rb");
    FILE *encrypt_fp = fopen(encryptPath, "wb");
    if (normal_fp == nullptr) {
        LOGE("%s", "file open filed..");
        return;
    }

    //一次只读一个字符
    int ch = 0;
    int i = 0;
    size_t pwd_length = strlen(passWord);
    while ((ch = fgetc(normal_fp)) != EOF) {//End of File
        //写入（亦或运算）
        fputc(ch ^ passWord[i % pwd_length], encrypt_fp);
        i++;
    }
    //关闭流，释放缓存区
    fclose(normal_fp);
    fclose(encrypt_fp);

    //释放JCM保存的字符串内存
    env->ReleaseStringUTFChars(normalPath_, normalPath);
    env->ReleaseStringUTFChars(encryptPath_, encryptPath);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_stan_jnitest_jni_Encryptor_decryption(JNIEnv *env, jobject instance,
                                                       jstring encryptPath_, jstring decryptPath_) {
    const char *encryptPath = env->GetStringUTFChars(encryptPath_, nullptr);
    const char *decryptPath = env->GetStringUTFChars(decryptPath_, nullptr);

    LOGE("encryptPath = %s,decryptPath = %s", encryptPath, decryptPath);

    FILE *encrypt_fp = fopen(encryptPath, "rb");
    FILE *decrypt_fp = fopen(decryptPath, "wb");

    if (encrypt_fp == nullptr) {
        LOGE("%s", "encrypt_file open filed..");
        return;
    }

    int ch;
    int i = 0;
    size_t pwd_length = strlen(passWord);
    while ((ch = fgetc(encrypt_fp)) != EOF) {
        fputc(ch ^ passWord[i % pwd_length], decrypt_fp);
        i++;
    }
    fclose(encrypt_fp);
    fclose(decrypt_fp);

    env->ReleaseStringUTFChars(encryptPath_, encryptPath);
    env->ReleaseStringUTFChars(decryptPath_, decryptPath);

}

long get_file_size(const char *path) {
    //rb：只读打开一个二进制文件，允许读数据
    //使用给定的模式 "rb" 打开 path 所指向的文件
    FILE *fp = fopen(path, "rb");
    if (fp == nullptr) {
        LOGE("%s", "文件打开失败");
        return 0;
    }
    //SEEK_SET	文件的开头
    //SEEK_CUR	文件指针的当前位置
    //SEEK_END	文件的末尾
    //设置流 fp 的文件位置为 0， 0 意味着从给定的 SEEK_END 位置查找的字节数。
    fseek(fp, 0, SEEK_END);
    //返回给定流 fp 的当前文件位置。
    return ftell(fp);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_stan_jnitest_jni_Encryptor_split(JNIEnv *env, jobject instance, jstring path,
                                                  jstring path_pattern, jint split_count) {
    const char *pathFile = env->GetStringUTFChars(path, nullptr);
    const char *pathPattern = env->GetStringUTFChars(path_pattern, nullptr);

    //分配所需的内存空间，并返回一个指向它的指针
    char **patches = new char *[split_count];
    //获取文件长度
    long fileSize = get_file_size(pathFile);

    //获取单个文件的长度
    long per_size = fileSize / split_count;
    //设置每个子文件的路径
    for (int i = 0; i < split_count; i++) {
        patches[i] = new char[256];
        sprintf(patches[i], pathPattern, i);
        LOGE("%s", "%s", patches[i]);
    }
    //创建fp流读取path对应的文件
    FILE *fp = fopen(pathFile, "rb");
    if (fp == nullptr) {
        LOGE("%s", "文件打开失败");
        return;
    }
    //读取分割文件的流
    FILE *index_fp = nullptr;
    int index = 0;
    for (int i = 0; i < fileSize; i++) {
        //注意在split_count之外的字符
        if (i % per_size == 0 && i + (fileSize % split_count) < fileSize) {
            if (index_fp != nullptr) {
                fclose(index_fp);
            }
            index_fp = fopen(patches[index], "wb");
            index++;
            if (index_fp == nullptr) {
                LOGE("文件%s打开失败", "%s", patches[index]);
                return;
            }

        }
        fputc(fgetc(fp), index_fp);
        //读完之后释放流
        if (i + 1 == fileSize) {
            fclose(index_fp);
        }
    }
    fclose(fp);
    //释放内存
    for (int i = 0; i < split_count; i++) {
        free(patches[i]);
    }
    free(patches);
    env->ReleaseStringUTFChars(path, pathFile);
    env->ReleaseStringUTFChars(path_pattern, pathPattern);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_stan_jnitest_jni_Encryptor_merge(JNIEnv *env, jobject instance, jstring path_merge,
                                                  jstring path_pattern, jint count) {
    const char *pathMerge = env->GetStringUTFChars(path_merge, nullptr);
    const char *pathPattern = env->GetStringUTFChars(path_pattern, nullptr);
    //创建合并文件的写流
    FILE *fp = fopen(pathMerge, "wb");
    for (int i = 0; i < count; i++) {
        char *index = new char[256];
        sprintf(index, pathPattern, i);
        //读取每个分割文件
        FILE *index_fp = fopen(index, "rb");
        if (index_fp == nullptr) {
            LOGE("文件%s读取失败", "%s", index)
            return;
        }
        //依次写入合并文件
        int ch;
        while ((ch = fgetc(index_fp)) != EOF) {
            fputc(ch, fp);
        }
        //关闭当前的分割文件流
        fclose(index_fp);
        //释放拆分文件名的内存
        free(index);
    }
    fclose(fp);
    env->ReleaseStringUTFChars(path_merge, pathMerge);
    env->ReleaseStringUTFChars(path_pattern, pathPattern);

}

const int PATH_MAX_LENGTH = 256;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_stan_jnitest_jni_Encryptor_listDirAllFile(JNIEnv *env, jobject instance,
                                                           jstring dir_path) {
    if (dir_path == nullptr) {
        LOGD("dirPath is null!");
        return;
    }
    const char *dirPath = env->GetStringUTFChars(dir_path, nullptr);
    if (strlen(dirPath) == 0) {
        LOGD("dirPath length is 0!");
        return;
    }
    //打开文件夹读取流
    DIR *dir = opendir(dirPath);
    if (nullptr == dir) {
        LOGD("can not open dir,please check path or permission!");
        return;
    }

    struct dirent *file;
    while ((file = readdir(dir)) != nullptr) {
        //判断是不是 . 或者 .. 文件夹
        if (strcmp(file->d_name, ".") == 0 || strcmp(file->d_name, "..") == 0) {
            LOGV("ignore . and .. path is %s", file->d_name)
            continue;
        }
        if (file->d_type == DT_DIR) {
            //是文件夹则遍历
            //构建文件夹路径
            char *path = new char[PATH_MAX_LENGTH];
            memset(path, 0, PATH_MAX_LENGTH);
            strcpy(path, dirPath);
            strcat(path, "/");
            strcat(path, file->d_name);
            jstring tDir = env->NewStringUTF(path);
            //读取指定文件夹
            Java_com_example_stan_jnitest_jni_Encryptor_listDirAllFile(env, instance, tDir);
            //释放文件路径
            free(path);
        } else {
            //打印文件名
            LOGD("%s/%s", dirPath, file->d_name);
        }

    }
    closedir(dir);
    env->ReleaseStringUTFChars(dir_path, dirPath);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_stan_jnitest_jni_Encryptor_passBitmap(JNIEnv *env, jobject instance,
                                                       jobject bitmap) {
    if (bitmap == nullptr) {
        LOGD("bitmap is null..")
    }
    AndroidBitmapInfo info;
    int result;
    //获取图片信息
    result = AndroidBitmap_getInfo(env, bitmap, &info);
    if (result != ANDROID_BITMAP_RESULT_SUCCESS) {
        LOGD("AndroidBitmap_getInfo failed, result: %d", result)
        return;
    }
    LOGD("bitmap width: %d, height: %d, format: %d, stride: %d", info.width, info.height,
         info.format, info.stride)

    unsigned char *addrPtr;
    //获取像素信息
    result = AndroidBitmap_lockPixels(env, bitmap, reinterpret_cast<void **>(&addrPtr));

    if (result != ANDROID_BITMAP_RESULT_SUCCESS) {
        LOGD("AndroidBitmap_lockPixels failed, result: %d", result)
        return;
    }
    // 执行图片操作的逻辑
    int length = info.stride * info.height;
    for (int i = 0; i < length; ++i) {
        LOGD("value: %x", addrPtr[i])
    }

    // 像素信息不再使用后需要解除锁定
    result = AndroidBitmap_unlockPixels(env, bitmap);
    if (result != ANDROID_BITMAP_RESULT_SUCCESS) {
        LOGD("AndroidBitmap_unlockPixels failed, result: %d", result);
    }

}