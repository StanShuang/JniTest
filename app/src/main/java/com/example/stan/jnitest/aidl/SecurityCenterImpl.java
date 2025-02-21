package com.example.stan.jnitest.aidl;

import android.os.RemoteException;

/**
 * @Author Stan
 * @Description
 * @Date 2025/2/13 15:29
 */
public class SecurityCenterImpl extends ISecurityCenter.Stub {
    private static final char SECRET_CODE = '^';

    @Override
    public String encrypt(String content) throws RemoteException {
        char[] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] ^= SECRET_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
