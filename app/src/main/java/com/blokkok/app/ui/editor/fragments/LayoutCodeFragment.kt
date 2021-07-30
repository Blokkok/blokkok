package com.blokkok.app.ui.editor.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.blokkok.app.R
import com.blokkok.app.databinding.EditorLayoutPageBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class LayoutCodeFragment(
    private val saveCode: (String) -> Unit,
    private val initialCodeValue: String
) : Fragment(R.layout.editor_layout_page) {

    private val binding by viewBinding(EditorLayoutPageBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutCode = binding.layoutCode
        val saveButton = binding.layoutSaveButton

        layoutCode.setText(initialCodeValue)

        saveButton.setOnClickListener {
            saveCode(layoutCode.text.toString())
        }
    }
}