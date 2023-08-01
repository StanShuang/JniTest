#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_stan_jnitest_jni_JniNative_stringFromJNI(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_stan_jnitest_jni_JniNative_accessField(JNIEnv *env, jobject instance) {
    //获取类
    jclass jcla = env->GetObjectClass(instance);
    //获取类的成员变量showText的id
    jfieldID jfId = env->GetFieldID(jcla, "showText", "Ljava/lang/String;");
    jstring after = env->NewStringUTF("Hello NDK");
    //修改属性id的值
    env->SetObjectField(instance, jfId, after);
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_stan_jnitest_jni_JniNative_accessMethod(JNIEnv *env, jobject instance) {
    jclass jcla = env->GetObjectClass(instance);
    //获取方法id  第二个参数：方法名  第三个参数：(参数)返回值 的类型描述
    jmethodID methodId = env->GetMethodID(jcla, "getAuthName",
                                          "(Ljava/lang/String;)Ljava/lang/String;");
    jstring res = env->NewStringUTF("Stan");
    jobject objres = env->CallObjectMethod(instance, methodId, res);
    return static_cast<jstring>(objres);

}
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_stan_jnitest_jni_JniNative_accessStaticMethod__I(JNIEnv *env, jobject instance,
                                                                  jint max) {
    jclass jcla = env->GetObjectClass(instance);
    jmethodID methodId = env->GetStaticMethodID(jcla, "getRandomValue", "(I)I");
    jint res = env->CallStaticIntMethod(jcla, methodId, max);
    return res;
}

//数组元素最大值
const jint max = 100;

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_stan_jnitest_jni_JniNative_getIntArray(JNIEnv *env, jobject instance,
                                                        jint length) {
    //创建一个指定大小的数组
    jintArray array = env->NewIntArray(length);

    jint *elementsP = env->GetIntArrayElements(array, nullptr);
    jint *startP = elementsP;
    for (; startP < elementsP + length; startP++) {
        *startP = static_cast<jint>(random() % max);
    }
    env->ReleaseIntArrayElements(array, elementsP, 0);
    return array;
}

int compare(const void *a, const void *b) {
    return *(int *) a - *(int *) b;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_stan_jnitest_jni_JniNative_sortIntArray(JNIEnv *env, jobject instance,
                                                         jintArray array) {
    // 获取数组元素起始位置的指针
    jint *arr = env->GetIntArrayElements(array, nullptr);
    //获取数组长度
    jint len = env->GetArrayLength(array);
    //排序
    qsort(arr, len, sizeof(jint), compare);
    //第三个参数 同步
    //0：Java数组进行更新，并且释放C/C++数组
    //JNI_ABORT：Java数组不进行更新，但是释放C/C++数组
    //JNI_COMMIT：Java数组进行更新，不释放C/C++数组(函数执行完后，数组还是会释放的)
    env->ReleaseIntArrayElements(array, arr, 0);
}