package com.example.stan.jnitest.utils.datastore.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 *@Author Stan
 *@Description
 *@Date 2023/8/18 15:42
 */
object PDSUtils {

    suspend fun incrementCounter(context: Context) {
        context.dataStore.edit {
            val currentCounterValue = it[EXAMPLE_COUNTER] ?: 0
            it[EXAMPLE_COUNTER] = currentCounterValue + 1
            Log.d("preferences", "save success..")
        }
    }

    /**
     * 获取数据，collect是一个挂起函数，只要name的值发生变化，collect就会回调
     */
    suspend fun exampleCounterFlow(context: Context): Int {

        val flow = context.dataStore.data.catch {
            if (it is IOException) {
                Log.e("preferences", "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[EXAMPLE_COUNTER] ?: 0
        }
        return flow.first()
    }
}
