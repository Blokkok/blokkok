package com.blokkok.app.fragments.editor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.blokkok.app.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ManifestCodeFragment(
    private val saveCode: (String) -> Unit,
    private val initialCodeValue: String
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.editor_manifest_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutCode = view.findViewById<EditText>(R.id.manifest_code)
        val saveButton = view.findViewById<FloatingActionButton>(R.id.manifest_save_button)

        layoutCode.setText(initialCodeValue)

        saveButton.setOnClickListener {
            saveCode(layoutCode.text.toString())
        }
    }
}