package com.blokkok.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.blokkok.app.adapters.EditorPagerAdapter
import com.blokkok.app.managers.projects.ProjectEditor
import com.blokkok.app.managers.projects.ProjectMetadata
import com.blokkok.app.managers.projects.ProjectsManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class EditorActivity : AppCompatActivity() {

    private lateinit var editorAdapter: EditorPagerAdapter
    private lateinit var projectEditor: ProjectEditor
    private lateinit var project: ProjectMetadata

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val projectId = intent.getStringExtra("project_id")
            ?: throw IllegalStateException("The project_id intent extra isn't provided on EditorActivity")

        project = ProjectsManager.getProject(projectId)
            ?: throw IllegalStateException("The project given in EditorActivity doesn't exist")

        projectEditor = project.edit(this)

        val actionBar = findViewById<Toolbar>(R.id.toolBar)
        val editorViewPager = findViewById<ViewPager2>(R.id.editor_viewpager)
        val tabLayout = findViewById<TabLayout>(R.id.tabs)

        setSupportActionBar(actionBar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        val initialJavaCode = projectEditor.java["${project.packageName}.MainActivity"] ?: ""
        val initialLayoutCode = projectEditor.layout["main"] ?: ""

        editorAdapter =
            EditorPagerAdapter(
                this,
                { // Java code save callback
                    projectEditor.java["${project.packageName}.MainActivity"] = it

                }, initialJavaCode,
                { // Layout code save callback
                    projectEditor.layout["main"] = it

                }, initialLayoutCode
            )

        editorViewPager.adapter = editorAdapter

        TabLayoutMediator(tabLayout, editorViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "LAYOUT"
                1 -> tab.text = "CODE"
            }
        }.attach()
    }
}