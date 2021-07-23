package com.blokkok.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blokkok.app.adapters.MoveableLibrariesRecyclerView
import com.blokkok.app.managers.libraries.LibraryManager
import com.blokkok.app.managers.projects.ProjectMetadata
import com.blokkok.app.managers.projects.ProjectsManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: 7/22/21 ViewModel when

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
        val addLibrary = findViewById<FloatingActionButton>(R.id.add_library_manage_libraries)

        moveableLibrariesRecyclerView.layoutManager = LinearLayoutManager(this)
        moveableLibrariesRecyclerView.adapter = adapter

        adapter.setLibraries(project.libraries.map { LibraryManager.getLibraryEntry(it)!! })

        addLibrary.setOnClickListener { view ->
            // Show a dialog with all the libraries minus the added libraries
            val libraries = LibraryManager.listLibraries().map { it.name } - getLibraries()

            AlertDialog.Builder(view.context)
                .setTitle("Pick a library to be added")
                .setItems(libraries.toTypedArray()) { dialog, which ->
                    adapter.addLibrary(
                        LibraryManager.getLibraryEntry(libraries[which])!!
                    )

                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun getLibraries(): List<String> = adapter.libraries.map { it.name }

    override fun onDestroy() {
        // Update the libraries for the current project, might not be the best solution
        ProjectsManager
            .modifyMetadata(
                projectId,
                project.copy(libraries = getLibraries())
            )

        super.onDestroy()
    }
}