package com.example.trainer

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.trainer.com.example.trainer.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var repsTextView: TextView
    private lateinit var settingsButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var showWorkoutProgramsButton: Button
    private lateinit var workoutNameTextView: TextView
    private lateinit var setupMessageTextView: TextView  // Новое поле для сообщения

    private var totalTime: Long = 0
    private var remainingTime: Long = 0
    private var reps: Int = 0
    private var currentReps: Int = 0
    private var isRunning = false
    private var startTime: Long = 0
    private val handler = Handler()

    // Добавляем переменные для звука
    private var approachCompleteSound: MediaPlayer? = null
    private var timerCompleteSound: MediaPlayer? = null

    // Добавляем флаг для паузы
    private var isPaused = false

    // Переменная для хранения названия программы
    private var currentWorkoutName: String = "test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById(R.id.timerTextView)
        repsTextView = findViewById(R.id.repsTextView)
        progressBar = findViewById(R.id.circularProgressBar)
        settingsButton = findViewById(R.id.settingsButton)
        showWorkoutProgramsButton = findViewById(R.id.showWorkoutProgramsButton)
        workoutNameTextView = findViewById(R.id.workoutNameTextView)
        setupMessageTextView = findViewById(R.id.setupMessageTextView)  // Инициализация нового TextView

        // Скрываем количество подходов, прогресс-бар и таймер до старта
        repsTextView.visibility = View.GONE
        progressBar.visibility = View.GONE  // Прогресс-бар скрыт по умолчанию
        timerTextView.visibility = View.GONE // Таймер скрыт до старта

        // Устанавливаем начальный текст
        setupMessageTextView.visibility = View.VISIBLE  // Показываем сообщение по умолчанию
        setupMessageTextView.text = "Please Setup Workout Program"

        settingsButton.setOnClickListener {
            openSettingsDialog()
        }

        showWorkoutProgramsButton.setOnClickListener {
            pauseTimer()  // Пауза при переходе на программу тренировок
            openWorkoutPrograms()
        }

        // Получаем данные из Intent, если они были переданы
        val timeInSeconds = intent.getLongExtra("timeInSeconds", 0)
        val repetitions = intent.getIntExtra("repetitions", 0)
        val workoutProgramName = intent.getStringExtra("workoutProgramName") ?: "Workout"

        // Если данные получены, начинаем таймер
        if (timeInSeconds > 0 && repetitions > 0) {
            currentWorkoutName = workoutProgramName
            workoutNameTextView.text = currentWorkoutName ?: "Workout"
            startTimer(timeInSeconds, repetitions)
        }

        // Восстановление работы таймера при нажатии на текст
        timerTextView.setOnClickListener {
            if (isRunning) {
                pauseTimer()  // Пауза
            } else {
                resumeTimer()  // Продолжение
            }
        }
    }

    private fun openSettingsDialog() {
        val settingsFragment = SettingsFragment()

        // Устанавливаем слушатель для старта таймера
        settingsFragment.setOnStartTimerListener { timeInSeconds, repetitions ->
            currentWorkoutName = "Custom Workout"  // Название программы
            workoutNameTextView.text = currentWorkoutName  // Обновляем название
            startTimer(timeInSeconds, repetitions)
        }

        // Устанавливаем слушатель для сброса таймера
        settingsFragment.setOnResetTimerListener {
            resetTimer()  // Сбрасываем таймер
        }

        settingsFragment.show(supportFragmentManager, "settings")
    }

    // Новый метод для открытия экрана с программами тренировок
    private fun openWorkoutPrograms() {
        val intent = Intent(this, WorkoutProgramsActivity::class.java)
        startActivity(intent)
    }

    private fun startTimer(timeInSeconds: Long, repetitions: Int) {
        totalTime = timeInSeconds * 1000
        remainingTime = totalTime
        reps = repetitions
        currentReps = 0

        // Скрываем сообщение, показываем таймер и прогресс-бар
        setupMessageTextView.visibility = View.GONE  // Скрываем сообщение
        workoutNameTextView.text = currentWorkoutName
        workoutNameTextView.visibility = View.VISIBLE  // Показываем название программы

        timerTextView.visibility = View.VISIBLE
        repsTextView.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        updateRepsDisplay()

        startTime = System.currentTimeMillis()
        isRunning = true
        isPaused = false  // Таймер не в паузе
        handler.postDelayed(runnable, 0) // Начинаем обновление прогресса
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                val elapsedTime = System.currentTimeMillis() - startTime
                remainingTime = totalTime - elapsedTime

                if (remainingTime <= 0) {
                    onFinish()
                    return
                }

                val timeToDisplay = remainingTime

                val progress = ((elapsedTime.toFloat() / totalTime.toFloat()) * 100).toInt()
                progressBar.progress = progress

                val seconds = (timeToDisplay / 1000) % 60
                val minutes = (timeToDisplay / 1000) / 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)

                handler.postDelayed(this, 10)
            }
        }
    }

    private fun onFinish() {
        approachCompleteSound?.start()

        currentReps++
        if (currentReps < reps) {
            remainingTime = totalTime
            updateRepsDisplay()
            startTime = System.currentTimeMillis()
            handler.postDelayed(runnable, 0)
        } else {
            timerCompleteSound?.start()
            resetTimer()
        }
    }

    private fun pauseTimer() {
        isRunning = false
        isPaused = true
        handler.removeCallbacks(runnable)
        timerTextView.text = "II"  // Пауза отображается как II
    }

    private fun resumeTimer() {
        isRunning = true
        isPaused = false
        startTime = System.currentTimeMillis() - (totalTime - remainingTime)
        handler.postDelayed(runnable, 0)
        updateTimerDisplay()  // Восстанавливаем отображение времени
    }

    private fun resetTimer() {
        isRunning = false
        setupMessageTextView.visibility = View.VISIBLE  // Показываем сообщение снова
        setupMessageTextView.text = "Please Setup Workout Program"  // Текст по умолчанию
        timerTextView.visibility = View.GONE
        repsTextView.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    // Обновление отображения времени при возобновлении таймера
    private fun updateTimerDisplay() {
        val timeToDisplay = remainingTime
        val seconds = (timeToDisplay / 1000) % 60
        val minutes = (timeToDisplay / 1000) / 60
        timerTextView.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateRepsDisplay() {
        repsTextView.text = "$currentReps/$reps"
    }
}
