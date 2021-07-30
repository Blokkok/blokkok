package com.blokkok.app.ui.editor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blokkok.app.R
import com.blokkok.app.managers.projects.ProjectEditor
import com.blokkok.app.managers.projects.ProjectMetadata
import com.blokkok.app.ui.editor.adapters.EditorPagerAdapter
import com.blokkok.app.ui.editor.fragments.EditorFragment

class EditorActivity : AppCompatActivity() {

    private lateinit var projectId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

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