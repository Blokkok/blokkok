package com.blokkok.app.ui.editor.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blokkok.app.R
import com.blokkok.app.databinding.ManageProjectLibrariesFragmentBinding
import com.blokkok.app.managers.libraries.LibraryManager
import com.blokkok.app.managers.projects.ProjectMetadata
import com.blokkok.app.managers.projects.ProjectsManager
import com.blokkok.app.ui.editor.adapters.MoveableLibrariesRecyclerView
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class ManageProjectLibrariesFragment : Fragment(R.layout.manage_project_libraries_fragment) {

    private val adapter = MoveableLibrariesRecyclerView()
    private lateinit var projectId: String
    private lateinit var project: ProjectMetadata

    private val binding by viewBinding(ManageProjectLibrariesFragmentBinding::bind)

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)

        args?.let {
            projectId = it.getString("project_id")
                            ?: throw IllegalStateException("project_id intent extra is not supplied")

            project = ProjectsManager.getProject(projectId)
                            ?: throw IllegalStateException("The project_id given doesn't exist")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val moveableLibrariesRecyclerView = binding.rvManageLibraries
        val addLibrary = binding.addLibraryManageLibraries

        moveableLibrariesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        moveableLibrariesRecyclerView.adapter = adapter

        adapter.setLibraries(project.libraries.mapNotNull { LibraryManager.getLibraryEntry(it) })

        addLibrary.setOnClickListener {
            // Show a dialog with all the libraries minus the added libraries
            val libraries = LibraryManager.listLibraries().map { it.name } - getLibraries()

            AlertDialog.Builder(requireContext())
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

    override fun onStop() {
        super.onStop()

        // Update the libraries for the current project, might not be the best solution
        ProjectsManager
            .modifyMetadata(
                projectId,
                project.copy(libraries = getLibraries())
            )

        super.onDestroy()
    }
}