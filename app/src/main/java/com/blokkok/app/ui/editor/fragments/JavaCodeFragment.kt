package com.blokkok.app.ui.editor.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.blokkok.app.R
import com.blokkok.app.databinding.EditorJavaPageBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class JavaCodeFragment(
    private val saveCode: (String) -> Unit,
    private val initialCodeValue: String
) : Fragment(R.layout.editor_java_page) {

    private val binding by viewBinding(EditorJavaPageBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val javaCode = binding.javaCode
        val saveButton = binding.javaSaveButton

        javaCode.setText(initialCodeValue)

        saveButton.setOnClickListener {
            saveCode(javaCode.text.toString())
        }
    }
}