package com.example.trainer.com.example.trainer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.trainer.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SettingsFragment : BottomSheetDialogFragment() {

    private lateinit var timeEditText: EditText
    private lateinit var repsEditText: EditText
    private lateinit var startButtonInSettings: Button
    private lateinit var resetButtonInSettings: Button  // Кнопка Reset

    private var onStartTimer: ((Long, Int) -> Unit)? = null
    private var onResetTimer: (() -> Unit)? = null  // Слушатель для сброса

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_menu, container, false)

        timeEditText = view.findViewById(R.id.timeEditText)
        repsEditText = view.findViewById(R.id.repsEditText)
        startButtonInSettings = view.findViewById(R.id.startButtonInSettings)
        resetButtonInSettings = view.findViewById(R.id.resetButtonInSettings)  // Инициализируем кнопку Reset

        startButtonInSettings.setOnClickListener {
            // Получаем введенные значения
            val timeInSeconds = timeEditText.text.toString().toLongOrNull() ?: 30L
            val repetitions = repsEditText.text.toString().toIntOrNull() ?: 5

            // Логируем полученные значения
            Log.d("SettingsFragment", "timeInSeconds: $timeInSeconds, repetitions: $repetitions")

            // Валидация
            if (timeInSeconds > 10 && repetitions > 2) {
                Log.d("SettingsFragment", "Validation passed!")
                onStartTimer?.invoke(timeInSeconds, repetitions)
                dismiss()  // Закрываем меню после нажатия на кнопку
            } else {
                // Показываем ошибку, если данные не прошли валидацию
                Toast.makeText(requireContext(), "Please ensure time > 10 seconds and reps > 2", Toast.LENGTH_SHORT).show()
            }
        }

        resetButtonInSettings.setOnClickListener {
            onResetTimer?.invoke()  // Вызываем слушатель для сброса таймера
            dismiss()  // Закрываем меню после сброса
        }

        return view
    }

    fun setOnStartTimerListener(listener: (Long, Int) -> Unit) {
        onStartTimer = listener
    }

    // Добавляем метод для установки слушателя на сброс
    fun setOnResetTimerListener(listener: () -> Unit) {
        onResetTimer = listener
    }
}
