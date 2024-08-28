@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.example.stan.jnitest.utils.datastore.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.stan.jnitest.MyApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.lang.IllegalArgumentException


/**
 *@Author Stan
 *@Description
 *@Date 2023/8/22 10:58
 */
object EasyDataStore {
    //user_preferences 生成的文件名称 user_preferences.preferences_pb
    private const val USER_PREFERENCES_NAME = "user_preferences"
    private val MyApplication.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)

    //dataStore变量
    val dataStore = MyApplication.instance.dataStore

    private suspend fun putIntData(key: String, value: Int) = dataStore.edit {
        it[intPreferencesKey(key)] = value
    }

    private suspend fun putStringData(key: String, value: String) = dataStore.edit {
        it[stringPreferencesKey(key)] = value
    }

    private suspend fun putLongData(key: String, value: Long) = dataStore.edit {
        it[longPreferencesKey(key)] = value
    }

    private suspend fun putBooleanData(key: String, value: Boolean) = dataStore.edit {
        it[booleanPreferencesKey(key)] = value
    }

    private suspend fun putFloatData(key: String, value: Float) = dataStore.edit {
        it[floatPreferencesKey(key)] = value
    }

    private suspend fun putDoubleData(key: String, value: Double) = dataStore.edit {
        it[doublePreferencesKey(key)] = value
    }

    private suspend fun putSetData(key: String, value: Any?) = dataStore.edit {
        it[stringSetPreferencesKey(key)] = value as Set<String>
    }

    private fun getIntData(key: String, default: Int) = runBlocking {
        return@runBlocking dataStore.data.map {
            it[intPreferencesKey(key)] ?: default
        }.first()
    }

    private fun getStringData(key: String, default: String) = runBlocking {
        return@runBlocking dataStore.data.map {
            it[stringPreferencesKey(key)] ?: default
        }.first()
    }

    private fun getLongData(key: String, default: Long = 0): Long = runBlocking {
        return@runBlocking dataStore.data.map {
            it[longPreferencesKey(key)] ?: default
        }.first()
    }

    private fun getBooleanData(key: String, default: Boolean = false): Boolean = runBlocking {
        return@runBlocking dataStore.data.map {
            it[booleanPreferencesKey(key)] ?: default
        }.first()
    }

    private fun getFloatData(key: String, default: Float = 0.0f): Float = runBlocking {
        return@runBlocking dataStore.data.map {
            it[floatPreferencesKey(key)] ?: default
        }.first()
    }

    private fun getDoubleData(key: String, default: Double = 0.00): Double = runBlocking {
        return@runBlocking dataStore.data.map {
            it[doublePreferencesKey(key)] ?: default
        }.first()
    }

    private fun getSetData(key: String): Set<String> = runBlocking {
        return@runBlocking dataStore.data.map {
            it[stringSetPreferencesKey(key)]
        }.first()!!
    }

    /**
     * 存数据
     */
    fun <T> putData(key: String, value: T) {
        runBlocking {
            when (value) {
                is Int -> putIntData(key, value)
                is Long -> putLongData(key, value)
                is String -> putStringData(key, value)
                is Boolean -> putBooleanData(key, value)
                is Float -> putFloatData(key, value)
                is Double -> putDoubleData(key, value)
                is Set<*> -> putSetData(key, value)
                else -> throw IllegalArgumentException("This type cannot be saved to the Data Store")
            }

        }
    }

    /**
     * 取数据
     */
    fun <T> getData(key: String, defaultValue: T): T {
        val data = when (defaultValue) {
            is Int -> getIntData(key, defaultValue)
            is Long -> getLongData(key, defaultValue)
            is String -> getStringData(key, defaultValue)
            is Boolean -> getBooleanData(key, defaultValue)
            is Float -> getFloatData(key, defaultValue)
            is Double -> getDoubleData(key, defaultValue)
            is Set<*> -> getSetData(key)
            else -> throw IllegalArgumentException("This type cannot be saved to the Data Store")
        }
        return data as T
    }

    /**
     * 清空数据
     */
    fun clearData() = runBlocking { dataStore.edit { it.clear() } }

    /**
     * remove
     */
    fun <T> removeData(key: Preferences.Key<T>) = runBlocking { dataStore.edit { it.remove(key) } }
}