package com.blokkok.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blokkok.app.fragments.compiler.CompileFragment
import com.blokkok.app.managers.projects.ProjectsManager

class CompileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compile)

        val projectId = intent.getStringExtra("project_id")
            ?: throw IllegalStateException("The project_id intent extra isn't provided on CompileActivity")

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.compile_fragment_container, CompileFragment(ProjectsManager.getProject(projectId)!!))
            .commit()
    }
}