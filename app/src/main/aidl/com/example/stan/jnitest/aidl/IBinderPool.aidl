// IBinderPool.aidl
package com.example.stan.jnitest.aidl;

// Declare any non-default types here with import statements

interface IBinderPool {
    IBinder queryBinder(int binderCode);
}