package com.example.stan.jnitest.utils.datastore.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

/**
 *@Author Stan
 *@Description
 *@Date 2023/8/18 15:17
 */
//user_preferences 生成的文件名称 user_preferences.preferences_pb
private const val USER_PREFERENCES_NAME = "user_preferences"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)

val EXAMPLE_COUNTER = intPreferencesKey("example_counter")



