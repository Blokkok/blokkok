package com.blokkok.app.ui.main.fragments

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blokkok.app.R
import com.blokkok.app.databinding.FragmentLibrariesBinding
import com.blokkok.app.ui.main.adapters.LibrariesRecyclerView
import com.blokkok.app.viewmodels.main.LibrariesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class LibrariesFragment : Fragment(R.layout.fragment_libraries) {

    private val librariesAdapter = LibrariesRecyclerView(emptyArray())
    private val viewModel: LibrariesViewModel by viewModels()

    private val binding by viewBinding(FragmentLibrariesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val librariesRecyclerView: RecyclerView = binding.rvLibraries
        val addLibraries: FloatingActionButton  = binding.librariesAdd

        librariesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        librariesRecyclerView.adapter = librariesAdapter

        viewModel.loadLibraries()
        viewModel.libraries.observe(viewLifecycleOwner) { librariesAdapter.updateView(it) }

        val contentResolver = requireContext().contentResolver

        val pickAar = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri == null) return@registerForActivityResult
            val name = uri.getFileName(contentResolver)

            viewModel.addAARLibrary(contentResolver.openInputStream(uri)!!, name!!)
        }

        val pickPrecompiled = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri == null) return@registerForActivityResult

            viewModel.addPrecompiledLibrary(contentResolver.openInputStream(uri)!!)
        }

        addLibraries.setOnClickListener {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(it.context)
            alertDialog.setTitle("Import a library")
            val items = arrayOf("Import an .aar file", "Import a precompiled library")

            alertDialog.setItems(items) { dialog, which ->
                when (which) {
                    0 -> pickAar.launch(arrayOf("*/*"))
                    1 -> pickPrecompiled.launch(arrayOf("application/zip"))
                }

                dialog.dismiss()
            }

            val alert: AlertDialog = alertDialog.create()
            alert.show()
        }
    }
}

// https://stackoverflow.com/a/25005243/9613353
private fun Uri.getFileName(contentResolver: ContentResolver): String? {
    var result: String? = null
    if (scheme == "content") {
        contentResolver
            .query(this, null, null, null, null)
            .use { cursor ->
                if (cursor!!.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
    }

    if (result == null) {
        result = path
        val cut = result!!.lastIndexOf('/')
        if (cut != -1) result = result!!.substring(cut + 1)
    }

    return result
}