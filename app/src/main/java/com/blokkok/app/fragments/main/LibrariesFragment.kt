package com.blokkok.app.fragments.main

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blokkok.app.R
import com.blokkok.app.adapters.LibrariesRecyclerView
import com.blokkok.app.managers.libraries.LibraryManager
import com.blokkok.app.viewmodels.main.LibrariesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class LibrariesFragment : Fragment() {

    private val librariesAdapter = LibrariesRecyclerView(emptyArray())
    private val viewModel: LibrariesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_libraries, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val librariesRecyclerView: RecyclerView = view.findViewById(R.id.rv_libraries)
        val addLibraries: FloatingActionButton  = view.findViewById(R.id.libraries_add)

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