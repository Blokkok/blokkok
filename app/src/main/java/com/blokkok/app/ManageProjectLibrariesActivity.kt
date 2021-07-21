package com.blokkok.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blokkok.app.adapters.MoveableLibrariesRecyclerView
import com.blokkok.app.managers.libraries.Library
import com.blokkok.app.managers.libraries.LibraryManager
import com.blokkok.app.managers.projects.ProjectMetadata
import com.blokkok.app.managers.projects.ProjectsManager

class ManageProjectLibrariesActivity : AppCompatActivity() {

    private val adapter = MoveableLibrariesRecyclerView()
    private lateinit var projectId: String
    private lateinit var project: ProjectMetadata

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_project_libraries)

        projectId = intent.getStringExtra("project_id")
            ?: throw IllegalStateException("project_id intent extra is not supplied")

        project = ProjectsManager.getProject(projectId)
            ?: throw IllegalStateException("The project_id given doesn't exists")

        val moveableLibrariesRecyclerView = findViewById<RecyclerView>(R.id.rv_manage_libraries)

        moveableLibrariesRecyclerView.layoutManager = LinearLayoutManager(this)
        moveableLibrariesRecyclerView.adapter = adapter

        adapter.setLibraries(project.libraries.map { LibraryManager.findLibrary(it)!! })
    }

    override fun onDestroy() {
        // Update the libraries for the current project, might not be the best solution
        ProjectsManager
            .modifyMetadata(
                projectId,
                project.copy(libraries = adapter.libraries.map { it.name })
            )

        super.onDestroy()
    }
}