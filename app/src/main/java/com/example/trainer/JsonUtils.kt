import android.content.Context
import android.util.Log
import com.example.trainer.R
import com.example.trainer.WorkoutProgram
import kotlinx.serialization.decodeFromString
import java.io.InputStreamReader

import kotlinx.serialization.json.Json

object JsonUtils {

    fun loadWorkoutPrograms(context: Context): List<WorkoutProgram> {
        val json = context.resources.openRawResource(R.raw.workout_programs)
            .bufferedReader()
            .use { it.readText() }

        return try {
            val programs = Json.decodeFromString<List<WorkoutProgram>>(json)
            programs
        } catch (e: Exception) {
            Log.e("JsonUtils", "Error loading JSON", e)
            emptyList()
        }
    }
}

