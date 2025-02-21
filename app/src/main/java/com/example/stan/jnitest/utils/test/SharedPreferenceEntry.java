package com.example.stan.jnitest.utils.test;

import java.util.Calendar;

/**
 * @Author Stan
 * @Description
 * @Date 2025/2/21 14:19
 */
public class SharedPreferenceEntry {
    // Name of the user.
    private final String mName;

    // Date of Birth of the user.
    private final Calendar mDateOfBirth;

    // Email address of the user.
    private final String mEmail;

    public SharedPreferenceEntry(String mName, Calendar mDateOfBirth, String mEmail) {
        this.mName = mName;
        this.mDateOfBirth = mDateOfBirth;
        this.mEmail = mEmail;
    }

    public String getName() {
        return mName;
    }

    public Calendar getDateOfBirth() {
        return mDateOfBirth;
    }

    public String getEmail() {
        return mEmail;
    }
}
