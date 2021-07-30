package com.blokkok.app.ui.compile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blokkok.app.R
import com.blokkok.app.databinding.ActivityCompileBinding
import com.blokkok.app.managers.projects.ProjectsManager
import com.blokkok.app.ui.compile.fragments.CompileFragment

class CompileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCompileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("project_id")) {
            val projectId = intent.getStringExtra("project_id")!!

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.activity_compile_root, CompileFragment(project = ProjectsManager.getProject(projectId)!!))
                .commit()

        } else {
            val libraryName = intent.getStringExtra("library_name")!!

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.activity_compile_root, CompileFragment(libraryName = libraryName))
                .commit()
        }
    }
}