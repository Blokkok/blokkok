package com.blokkok.app.fragments.compiler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.blokkok.app.R
import com.blokkok.app.managers.projects.ProjectMetadata
import com.blokkok.app.viewmodels.compiler.CompileViewModel

// TODO: 7/13/21 ForegroundService would be cool 

class CompileFragment(
    private val project: ProjectMetadata? = null,
    private val libraryName: String? = null,
) : Fragment() {

    private val viewModel: CompileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.compile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val out = view.findViewById<TextView>(R.id.compile_out)

        if (project != null) viewModel.compileProject(project, requireContext())
        if (libraryName != null) viewModel.compileLibrary(libraryName)

        viewModel.outputLiveData.observe(viewLifecycleOwner) { out.text = it }
    }
}