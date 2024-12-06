package com.example.trainer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.io.InputStream

class WorkoutProgramsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutProgramsAdapter: WorkoutProgramsAdapter

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_programs)

        recyclerView = findViewById(R.id.recyclerView)

        // Запуск корутины для загрузки данных
        loadWorkoutPrograms()
    }

    private fun loadWorkoutPrograms() {
        // URL для получения данных
        val programsUrl = "https://cdf0356d-1972-46e4-bb0a-63a32f898d83.selstorage.ru/workout_programs.json"

        // Проверяем доступность интернета перед выполнением запроса
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Нет доступа к интернету, загружаем локальные данные.", Toast.LENGTH_SHORT).show()
            loadProgramsFromLocal()
            return
        }

        // Запускаем корутину для асинхронной загрузки данных
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Создаем запрос
                val request = Request.Builder().url(programsUrl).build()

                // Выполняем запрос
                val response: Response = client.newCall(request).execute()

                // Если ответ успешный, парсим JSON
                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string() ?: ""
                    val programs = parseProgramsJson(jsonResponse)

                    // Если данные успешно загружены
                    withContext(Dispatchers.Main) {
                        if (programs.isEmpty()) {
                            Toast.makeText(this@WorkoutProgramsActivity, "Сервер вернул пустой файл, загружаем локальные данные.", Toast.LENGTH_SHORT).show()
                            loadProgramsFromLocal()
                        } else {
                            showPrograms(programs)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@WorkoutProgramsActivity, "Не удалось загрузить данные с сервера, загружаем локальные данные.", Toast.LENGTH_SHORT).show()
                        loadProgramsFromLocal()
                    }
                }
            } catch (e: IOException) {
                // Обработка ошибок
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@WorkoutProgramsActivity, "Ошибка сети: ${e.message}, загружаем локальные данные.", Toast.LENGTH_SHORT).show()
                    loadProgramsFromLocal()
                }
            }
        }
    }

    // Функция для парсинга JSON в список программ
    private fun parseProgramsJson(json: String): List<WorkoutProgram> {
        return try {
            // Используем kotlinx.serialization для парсинга
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Функция для загрузки программ из локального файла
    private fun loadProgramsFromLocal() {
        val localPrograms = try {
            val inputStream: InputStream = assets.open("workout_programs.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            parseProgramsJson(json)
        } catch (e: Exception) {
            emptyList<WorkoutProgram>()
        }

        if (localPrograms.isEmpty()) {
            Toast.makeText(this, "Локальные данные тоже отсутствуют!", Toast.LENGTH_SHORT).show()
        } else {
            showPrograms(localPrograms)
        }
    }

    // Функция для отображения программ
    private fun showPrograms(programs: List<WorkoutProgram>) {
        // Изменяем код в WorkoutProgramsActivity
        workoutProgramsAdapter = WorkoutProgramsAdapter(programs) { program ->
            // Выводим Toast с названием выбранной программы
            Toast.makeText(this, "Вы выбрали программу: ${program.name}", Toast.LENGTH_SHORT).show()

            // Создаем Intent для передачи данных в MainActivity
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("timeInSeconds", program.timeInSeconds)  // Передаем время
                putExtra("repetitions", program.repetitions)     // Передаем количество повторений
                putExtra("workoutProgramName", program.name)     // Передаем название программы
            }

            // Запускаем MainActivity с передачей данных
            startActivity(intent)

            // Закрываем текущую активность (WorkoutProgramsActivity)
            finish()
        }


        // Настройка RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = workoutProgramsAdapter
    }

    // Функция для проверки доступности сети
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
