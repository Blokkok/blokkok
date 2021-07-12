package com.blokkok.app.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blokkok.app.EditorActivity
import com.blokkok.app.R
import com.blokkok.app.managers.projects.ProjectMetadata

class ProjectsRecyclerView(
    private var projects: List<ProjectMetadata>

) : RecyclerView.Adapter<ProjectsRecyclerView.ViewHolder>() {

    fun updateView(newProjects: List<ProjectMetadata>) {
        projects = newProjects
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.project_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentProject = projects[position]

        holder.projectName.text = currentProject.name
        holder.projectPackageName.text = currentProject.packageName
        holder.projectId.text = currentProject.id

        holder.root.setOnClickListener {
            val intent = Intent(it.context, EditorActivity::class.java)
            intent.putExtra("project_id", currentProject.id)

            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = projects.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val projectName: TextView           = itemView.findViewById(R.id.project_name)
        val projectPackageName: TextView    = itemView.findViewById(R.id.project_package)
        val projectId: TextView             = itemView.findViewById(R.id.project_id)
        val root: View                      = itemView.findViewById(R.id.root_project_item)
    }
}
