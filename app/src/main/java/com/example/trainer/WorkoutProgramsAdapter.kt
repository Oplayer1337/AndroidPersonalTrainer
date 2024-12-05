package com.example.trainer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkoutProgramsAdapter(
    private val programs: List<WorkoutProgram>,
    private val onProgramClick: (WorkoutProgram) -> Unit
) : RecyclerView.Adapter<WorkoutProgramsAdapter.ProgramViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.program_item, parent, false)
        return ProgramViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        val program = programs[position]
        holder.bind(program)
    }

    override fun getItemCount() = programs.size

    inner class ProgramViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.programNameTextView)
        private val emojiTextView: TextView = view.findViewById(R.id.programEmojiTextView)
        private val descriptionTextView: TextView = view.findViewById(R.id.programDescriptionTextView)

        fun bind(program: WorkoutProgram) {
            nameTextView.text = program.name
            emojiTextView.text = program.emoji
            descriptionTextView.text = program.briefDescription

            itemView.setOnClickListener { onProgramClick(program) }
        }
    }
}
