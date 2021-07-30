package com.blokkok.app.ui.editor.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.blokkok.app.databinding.EditorManifestPageBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class ManifestCodeFragment(
    private val saveCode: (String) -> Unit,
    private val initialCodeValue: String
) : Fragment() {

    private val binding by viewBinding(EditorManifestPageBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutCode = binding.manifestCode
        val saveButton = binding.manifestSaveButton

        layoutCode.setText(initialCodeValue)

        saveButton.setOnClickListener {
            saveCode(layoutCode.text.toString())
        }
    }
}