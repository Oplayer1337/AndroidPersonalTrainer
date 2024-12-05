package com.example.trainer

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProgramDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_program_detail)

        val program = intent.getSerializableExtra("program") as WorkoutProgram

        val nameTextView: TextView = findViewById(R.id.programDetailName)
        val planTextView: TextView = findViewById(R.id.programDetailPlan)
        val descriptionTextView: TextView = findViewById(R.id.programDetailDescription)

        nameTextView.text = program.name
        planTextView.text = program.workoutPlan
        descriptionTextView.text = program.detailedDescription
    }
}
