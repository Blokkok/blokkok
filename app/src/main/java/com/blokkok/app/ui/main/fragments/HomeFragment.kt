package com.blokkok.app.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blokkok.app.R
import com.blokkok.app.ui.main.adapters.ProjectsRecyclerView
import com.blokkok.app.viewmodels.main.HomeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var projectsAdapter: ProjectsRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val newProjectFab = root.findViewById<FloatingActionButton>(R.id.newProject)
        val projectsRecyclerView = root.findViewById<RecyclerView>(R.id.projectList)

        projectsAdapter = ProjectsRecyclerView(emptyList())

        projectsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        projectsRecyclerView.adapter = projectsAdapter

        newProjectFab.setOnClickListener {
            val context = requireContext()
            val builder = AlertDialog.Builder(context)
            val layout = LinearLayout(context)

            layout.orientation = LinearLayout.VERTICAL

            val projectName = EditText(context)
            projectName.hint = "Project Name"
            layout.addView(projectName)

            val projectPackage = EditText(context)
            projectPackage.hint = "Project Package"
            layout.addView(projectPackage)

            builder
                .setView(layout)
                .setTitle("New Project")
                .setPositiveButton("Create") { _, _ ->
                    viewModel.createProject(projectName.text.toString(), projectPackage.text.toString())
                    // TODO: 7/12/21 Launch the editor activity after ViewModel finished creating the project
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchProjects()
        viewModel.projectsLiveData.observe(viewLifecycleOwner) { projects ->
            projectsAdapter.updateView(projects)
        }
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}