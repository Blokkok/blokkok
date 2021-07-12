package com.blokkok.app.fragments.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.blokkok.app.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class JavaCodeFragment(
    private val saveCodeCallback: SaveCodeCallback,
    private val initialCodeValue: String
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.editor_java_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val javaCode = view.findViewById<EditText>(R.id.java_code)
        val saveButton = view.findViewById<FloatingActionButton>(R.id.java_save_button)

        javaCode.setText(initialCodeValue)

        saveButton.setOnClickListener {
            saveCodeCallback.onSaved(javaCode.text.toString())
        }
    }
}