package com.example.stan.jnitest.aidl;

import android.os.RemoteException;

/**
 * @Author Stan
 * @Description
 * @Date 2025/2/13 15:30
 */
public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
