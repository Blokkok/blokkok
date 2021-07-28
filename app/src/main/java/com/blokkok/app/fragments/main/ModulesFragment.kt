package com.blokkok.app.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.blokkok.app.R
import com.blokkok.app.viewmodels.main.ModulesViewModel
import com.blokkok.modsys.ModuleManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.zip.ZipInputStream

class ModulesFragment : Fragment() {
    val viewModel: ModulesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_modules, container, false)
        val addModuleFab = root.findViewById<FloatingActionButton>(R.id.addModule)

        val importModule = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it == null) return@registerForActivityResult

            viewModel.importModule(requireContext().contentResolver, it)
        }

        addModuleFab.setOnClickListener {
            importModule.launch(arrayOf("application/zip"))
        }

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.toastAction.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance(): ModulesFragment {
            return ModulesFragment()
        }
    }
}