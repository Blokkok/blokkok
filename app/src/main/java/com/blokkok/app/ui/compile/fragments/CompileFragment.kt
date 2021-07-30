package com.blokkok.app.ui.compile.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.blokkok.app.databinding.CompileFragmentBinding
import com.blokkok.app.managers.projects.ProjectMetadata
import com.blokkok.app.viewmodels.compiler.CompileViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import java.io.File
import java.io.FileInputStream

// TODO: 7/13/21 ForegroundService would be cool

class CompileFragment(
    private val project: ProjectMetadata? = null,
    private val libraryName: String? = null,
) : Fragment() {

    private val viewModel: CompileViewModel by viewModels()

    private lateinit var apkFile: File
    private lateinit var saveFile: ActivityResultLauncher<String>

    private val binding: CompileFragmentBinding by viewBinding(CompileFragmentBinding::bind)

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

        val out = binding.compileOut
        val vscroll = binding.logVscroll

        viewModel.outputLiveData.observe(viewLifecycleOwner) {
            out.append(it)
            vscroll.fullScroll(View.FOCUS_DOWN)
        }

        if (project != null) viewModel.compileProject(project, requireContext())
        else if (libraryName != null) viewModel.compileLibrary(libraryName)
    }
}