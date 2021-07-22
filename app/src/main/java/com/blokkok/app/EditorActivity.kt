package com.blokkok.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
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

    private lateinit var projectId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        projectId = intent.getStringExtra("project_id")
            ?: throw IllegalStateException("The project_id intent extra isn't provided on EditorActivity")

        project = ProjectsManager.getProject(projectId)
            ?: throw IllegalStateException("The project given in EditorActivity doesn't exist")

        projectEditor = project.edit(this)

        val actionBar = findViewById<Toolbar>(R.id.toolBar)
        val editorViewPager = findViewById<ViewPager2>(R.id.editor_viewpager)
        val tabLayout = findViewById<TabLayout>(R.id.tabs)

        val compileButton = findViewById<Button>(R.id.compile_button)

        setSupportActionBar(actionBar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        val initialJavaCode = projectEditor.readJavaCode("${project.packageName}.MainActivity") ?: "package ${project.packageName};\n"
        val initialLayoutCode = projectEditor.readLayoutCode("main") ?: ""
        val initialManifestCode = projectEditor.readManifest() ?: ProjectEditor.generateDefaultManifest(project.name, project.packageName)

        // TODO: 7/14/21 Make a viewmodel for this 
        editorAdapter =
            EditorPagerAdapter(
                this,
                { // Java code save callback
                    projectEditor.writeJavaCode(project.packageName, "MainActivity", it)

                }, initialJavaCode,
                { // Layout code save callback
                    projectEditor.writeLayoutCode("main", it)

                }, initialLayoutCode,
                { // Manifest code save callback
                    projectEditor.writeManifest(it)
                }, initialManifestCode
            )

        editorViewPager.adapter = editorAdapter

        TabLayoutMediator(tabLayout, editorViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "LAYOUT"
                1 -> tab.text = "CODE"
                2 -> tab.text = "MANIFEST"
            }
        }.attach()

        compileButton.setOnClickListener {
            val intent = Intent(this, CompileActivity::class.java)
            intent.putExtra("project_id", projectId)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.editor_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.em_manage_libraries -> {
            startActivity(
                Intent(this, ManageProjectLibrariesActivity::class.java).apply {
                    putExtra("project_id", projectId)
                }
            )
            true
        }

        R.id.em_view_source -> {
            Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show()
            true
        }

        R.id.em_export -> {
            Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}