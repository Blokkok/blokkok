package com.blokkok.app.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blokkok.app.R
import com.blokkok.app.adapters.ModulesRecyclerViewAdapter
import com.blokkok.app.viewmodels.main.ModulesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ModulesFragment : Fragment() {

    val viewModel: ModulesViewModel by viewModels()
    val modulesAdapter = ModulesRecyclerViewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_modules, container, false)
        val addModuleFab = root.findViewById<FloatingActionButton>(R.id.addModule)
        val modulesRecyclerView = root.findViewById<RecyclerView>(R.id.moduleList)

        modulesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        modulesRecyclerView.adapter = modulesAdapter

        val importModule = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it == null) return@registerForActivityResult

            viewModel.importModule(requireContext().contentResolver, it)
        }

        addModuleFab.setOnClickListener {
            importModule.launch(arrayOf("application/zip"))
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.toastAction.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.modules.observe(viewLifecycleOwner) { modulesAdapter.updateView(it) }
        viewModel.loadModules()
    }

    companion object {
        fun newInstance(): ModulesFragment {
            return ModulesFragment()
        }
    }
}