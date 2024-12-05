package com.example.trainer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WorkoutProgramsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutProgramsAdapter: WorkoutProgramsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_programs)

        recyclerView = findViewById(R.id.recyclerView)
        val programs = JsonUtils.loadWorkoutPrograms(this)

        // Адаптер для отображения списка программ
        workoutProgramsAdapter = WorkoutProgramsAdapter(programs) { program ->
            // Выводим Toast с названием выбранной программы
            Toast.makeText(this, "Вы выбрали программу: ${program.name}", Toast.LENGTH_SHORT).show()

            // Создаем Intent для передачи данных в MainActivity
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("timeInSeconds", program.timeInSeconds)  // Передаем время
                putExtra("repetitions", program.repetitions)     // Передаем количество повторений
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
}
