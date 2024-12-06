package com.example.trainer

import android.content.Context
import android.util.Log
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

object JsonUtils {

    private val client = OkHttpClient()

    // Функция для загрузки списка программ тренировок с удаленного ресурса
    fun loadWorkoutPrograms(context: Context, url: String): List<WorkoutProgram> {
        val request = Request.Builder()
            .url(url)
            .build()

        try {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                // Преобразуем полученный JSON в объект
                val jsonData = response.body?.string() ?: ""
                return Json.decodeFromString(jsonData)
            } else {
                Log.e("JsonUtils", "Error loading data: ${response.code}")
                return emptyList()
            }
        } catch (e: Exception) {
            Log.e("JsonUtils", "Exception: ${e.message}")
            return emptyList()
        }
    }
}
