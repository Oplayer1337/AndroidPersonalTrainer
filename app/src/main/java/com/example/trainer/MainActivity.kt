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
    private lateinit var showWorkoutProgramsButton: Button  // Добавляем переменную для кнопки

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById(R.id.timerTextView)
        repsTextView = findViewById(R.id.repsTextView)
        settingsButton = findViewById(R.id.settingsButton)
        progressBar = findViewById(R.id.circularProgressBar)
        showWorkoutProgramsButton = findViewById(R.id.showWorkoutProgramsButton)  // Инициализация кнопки

        // Скрываем количество подходов и кнопку паузы до старта
        repsTextView.visibility = View.GONE
        progressBar.visibility = View.GONE  // Прогресс-бар скрыт по умолчанию

        settingsButton.setOnClickListener {
            openSettingsDialog()
        }

        // Обработчик клика на кнопку для отображения программ тренировок
        showWorkoutProgramsButton.setOnClickListener {
            pauseTimer()  // Пауза при переходе на программу тренировок
            openWorkoutPrograms()
        }

        // Инициализируем звуки
        approachCompleteSound = MediaPlayer.create(this, R.raw.approach_complete)
        timerCompleteSound = MediaPlayer.create(this, R.raw.timer_complete)

        // Получаем данные из Intent, если они были переданы
        val timeInSeconds = intent.getLongExtra("timeInSeconds", 0)
        val repetitions = intent.getIntExtra("repetitions", 0)

        // Если данные получены, запускаем таймер
        if (timeInSeconds > 0 && repetitions > 0) {
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

        repsTextView.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE // Показываем прогресс-бар

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

                // Если время вышло, завершаем подход
                if (remainingTime <= 0) {
                    onFinish()
                    return
                }

                // Отображаем оставшееся время от начала подхода, а не от конечного
                val timeToDisplay = remainingTime

                // Обновляем прогресс с плавностью
                val progress = ((elapsedTime.toFloat() / totalTime.toFloat()) * 100).toInt()
                progressBar.progress = progress

                // Обновляем отображение времени (секунды)
                val seconds = (timeToDisplay / 1000) % 60
                val minutes = (timeToDisplay / 1000) / 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)

                // Плавное обновление каждые 10 миллисекунд
                handler.postDelayed(this, 10)
            }
        }
    }

    private fun onFinish() {
        // Воспроизводим звук при завершении подхода
        approachCompleteSound?.start()

        currentReps++
        if (currentReps < reps) {
            remainingTime = totalTime
            updateRepsDisplay()
            startTime = System.currentTimeMillis()
            handler.postDelayed(runnable, 0) // Запуск следующего подхода
        } else {
            // Воспроизводим звук при завершении таймера
            timerCompleteSound?.start()
            resetTimer()
        }
    }

    private fun pauseTimer() {
        isRunning = false
        isPaused = true
        handler.removeCallbacks(runnable)
    }

    private fun resumeTimer() {
        isRunning = true
        isPaused = false
        startTime = System.currentTimeMillis() - (totalTime - remainingTime) // Сохраняем оставшееся время
        handler.postDelayed(runnable, 0) // Возобновляем обновление прогресса
    }

    private fun resetTimer() {
        isRunning = false
        isPaused = false
        handler.removeCallbacks(runnable)
        remainingTime = 0
        currentReps = 0
        timerTextView.text = "00:00"
        repsTextView.visibility = View.GONE
        progressBar.visibility = View.GONE // Скрыть прогресс-бар
    }

    private fun updateRepsDisplay() {
        repsTextView.text = "$currentReps/$reps"
    }

    override fun onStop() {
        super.onStop()
        // Освобождаем ресурсы после завершения работы
        approachCompleteSound?.release()
        timerCompleteSound?.release()
    }
}
