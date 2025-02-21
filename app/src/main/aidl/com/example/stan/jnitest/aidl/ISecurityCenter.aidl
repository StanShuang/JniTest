// ISecurityCenter.aidl
package com.example.stan.jnitest.aidl;

// Declare any non-default types here with import statements

interface ISecurityCenter {
    String encrypt(String content);
    String decypt(String password);
}