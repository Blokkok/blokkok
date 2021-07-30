package com.blokkok.app.ui.main.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blokkok.app.R
import com.blokkok.app.databinding.FragmentModulesBinding
import com.blokkok.app.ui.main.adapters.ModulesRecyclerViewAdapter
import com.blokkok.app.viewmodels.main.ModulesViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class ModulesFragment : Fragment(R.layout.fragment_modules) {

    val viewModel: ModulesViewModel by viewModels()
    val modulesAdapter = ModulesRecyclerViewAdapter()

    private val binding by viewBinding(FragmentModulesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addModuleFab = binding.addModule
        val modulesRecyclerView = binding.moduleList

        val loadModules = binding.loadModules
        val unloadModules = binding.unloadModules

        val modulesLoadStatus = binding.moduleLoadStatus

        modulesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        modulesRecyclerView.adapter = modulesAdapter

        val importModule = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it == null) return@registerForActivityResult

            viewModel.importModule(requireContext().contentResolver, it)
        }

        addModuleFab.setOnClickListener {
            importModule.launch(arrayOf("application/zip"))
        }

        loadModules.setOnClickListener { viewModel.loadModules() }
        unloadModules.setOnClickListener { viewModel.unloadModules() }

        viewModel.toastAction.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.loadStatus.observe(viewLifecycleOwner) {
            modulesLoadStatus.text = it
        }

        viewModel.loadingModulesStatus.observe(viewLifecycleOwner) {
            loadModules.isEnabled = !it
            unloadModules.isEnabled = !it
        }

        viewModel.modules.observe(viewLifecycleOwner) { modulesAdapter.updateView(it) }
        viewModel.listModules()
    }

    companion object {
        fun newInstance(): ModulesFragment {
            return ModulesFragment()
        }
    }
}