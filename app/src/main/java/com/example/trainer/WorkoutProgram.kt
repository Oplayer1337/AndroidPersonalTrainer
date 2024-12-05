package com.example.trainer
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutProgram(
    val name: String,
    val emoji: String,
    val briefDescription: String,
    val detailedDescription: String,
    val workoutPlan: String,
    val timeInSeconds: Long,  // Время в секундах
    val repetitions: Int      // Количество подходов
)
