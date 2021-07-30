package com.blokkok.app.ui.editor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blokkok.app.R
import com.blokkok.app.databinding.ActivityEditorBinding
import com.blokkok.app.ui.editor.fragments.EditorFragment

class EditorActivity : AppCompatActivity() {

    private lateinit var projectId: String

    private lateinit var binding: ActivityEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        projectId = intent.getStringExtra("project_id")
            ?: throw NullPointerException("The project_id intent extra isn't provided on EditorActivity")

        val editorArgs = Bundle().apply {
            putString("project_id", projectId)
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.editor_fragment_container, EditorFragment::class.java, editorArgs)
            .commit()
    }
}