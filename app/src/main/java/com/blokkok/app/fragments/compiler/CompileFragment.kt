package com.blokkok.app.fragments.compiler

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.blokkok.app.R
import com.blokkok.app.managers.projects.ProjectMetadata
import com.blokkok.app.viewmodels.compiler.CompileViewModel
import java.io.File
import java.io.FileInputStream


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

    private lateinit var apkFile: File
    private lateinit var saveFile: ActivityResultLauncher<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)

        saveFile = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
            uri?.let {
                val contentResolver = requireContext().contentResolver
                val output = contentResolver
                    .openAssetFileDescriptor(it, "w")!!
                    .createOutputStream()

                Thread { FileInputStream(apkFile).copyTo(output) }.start()
            }

            requireActivity().finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.saveFileLiveData.observe(viewLifecycleOwner) { apkFile ->
            this.apkFile = apkFile
            saveFile.launch(apkFile.name)
        }
    }

    override fun onStart() {
        super.onStart()

        val out = requireView().findViewById<TextView>(R.id.compile_out)

        if (project != null) viewModel.compileProject(project, requireContext())
        if (libraryName != null) viewModel.compileLibrary(libraryName)

        viewModel.outputLiveData.observe(viewLifecycleOwner) { out.text = it }
    }
}