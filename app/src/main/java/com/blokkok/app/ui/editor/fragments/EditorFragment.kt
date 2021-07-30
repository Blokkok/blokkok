package com.blokkok.app.ui.editor.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.blokkok.app.R
import com.blokkok.app.databinding.EditorFragmentBinding
import com.blokkok.app.managers.projects.ProjectMetadata
import com.blokkok.app.ui.compile.CompileActivity
import com.blokkok.app.ui.editor.adapters.EditorPagerAdapter
import com.blokkok.app.ui.editor.viewmodels.EditorViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class EditorFragment : Fragment(R.layout.editor_fragment) {

    val viewModel: EditorViewModel by viewModels()

    private lateinit var editorAdapter: EditorPagerAdapter
    private lateinit var project: ProjectMetadata
    private lateinit var projectId: String

    private val binding by viewBinding(EditorFragmentBinding::bind)

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)

        args?.let {
            projectId = args.getString("project_id")
                ?: throw NullPointerException("The project_id intent extra isn't provided on EditorActivity")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionBar       = binding.toolBar
        val editorViewPager = binding.editorViewpager
        val tabLayout       = binding.tabs
        val compileButton   = binding.compileButton

        actionBar.subtitle = project.name
        actionBar.setOnMenuItemClickListener(::menuClick)

        viewModel.initializeProjectEditor(project, project.edit(requireContext()))

        editorAdapter =
            EditorPagerAdapter(
                requireActivity(),
                viewModel.javaCode, { // Java code callback
                    viewModel.javaCode = it

                }, viewModel.layoutCode, { // Layout code save callback
                    viewModel.layoutCode = it

                }, viewModel.manifestCode, { // Manifest code save callback
                    viewModel.manifestCode = it
                }
            )

        editorViewPager.adapter = editorAdapter

        TabLayoutMediator(tabLayout, editorViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "LAYOUT"
                1 -> tab.text = "CODE"
                2 -> tab.text = "MANIFEST"
            }
        }.attach()

        compileButton.setOnClickListener {
            val intent = Intent(requireContext(), CompileActivity::class.java)
            intent.putExtra("project_id", projectId)
            startActivity(intent)
        }
    }

    private fun menuClick(item: MenuItem): Boolean = when (item.itemId) {
        R.id.em_clear_cache -> {
            viewModel.clearCompileCache(projectId) {
                Toast.makeText(requireContext(), "Cache cleared!", Toast.LENGTH_SHORT).show()
            }

            true
        }

        R.id.em_manage_libraries -> {
            startActivity(
                Intent(requireContext(), ManageProjectLibrariesFragment::class.java).apply {
                    putExtra("project_id", projectId)
                }
            )

            true
        }

        R.id.em_view_source -> {
            Toast.makeText(requireContext(), "Not implemented yet", Toast.LENGTH_SHORT).show()
            true
        }

        R.id.em_export -> {
            Toast.makeText(requireContext(), "Not implemented yet", Toast.LENGTH_SHORT).show()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}