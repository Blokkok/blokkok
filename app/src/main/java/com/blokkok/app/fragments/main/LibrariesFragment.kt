package com.blokkok.app.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blokkok.app.R
import com.blokkok.app.adapters.LibrariesRecyclerView
import com.blokkok.app.managers.libraries.LibraryManager
import com.blokkok.app.viewmodels.main.LibrariesViewModel

class LibrariesFragment : Fragment() {

    private val librariesAdapter = LibrariesRecyclerView(emptyArray())
    private val viewModel: LibrariesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_licenses, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val librariesRecyclerView: RecyclerView = view.findViewById(R.id.rv_libraries)

        librariesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        librariesRecyclerView.adapter = librariesAdapter

        viewModel.loadLibraries()
        viewModel.libraries.observe(viewLifecycleOwner) { librariesAdapter.updateView(it) }
    }
}