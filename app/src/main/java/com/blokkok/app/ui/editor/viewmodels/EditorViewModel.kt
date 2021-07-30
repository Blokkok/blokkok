package com.blokkok.app.ui.editor.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blokkok.app.managers.projects.ProjectEditor
import com.blokkok.app.managers.projects.ProjectMetadata
import com.blokkok.app.managers.projects.ProjectsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class EditorViewModel : ViewModel() {

    private lateinit var projectEditor: ProjectEditor
    private lateinit var project: ProjectMetadata

    var javaCode: String by Delegates.observable("") { _, _, new: String ->
        viewModelScope.launch(Dispatchers.IO) {
            projectEditor.writeJavaCode(project.packageName, "MainActivity", new)
        }
    }

    var layoutCode: String by Delegates.observable("") { _, _, new: String ->
        viewModelScope.launch(Dispatchers.IO) {
            projectEditor.writeLayoutCode("main", new)
        }
    }

    var manifestCode: String by Delegates.observable("") { _, _, new: String ->
        viewModelScope.launch(Dispatchers.IO) {
            projectEditor.writeManifest(new)
        }
    }

    fun initializeProjectEditor(projectMeta: ProjectMetadata, editor: ProjectEditor) {
        projectEditor = editor
        project = projectMeta

        javaCode = projectEditor.readJavaCode("${project.packageName}.MainActivity")
            ?: "package ${project.packageName};\n"

        layoutCode = projectEditor.readLayoutCode("main")
            ?: ""

        manifestCode = projectEditor.readManifest()
            ?: ProjectEditor.generateDefaultManifest(project.name, project.packageName)
    }

    fun clearCompileCache(projectId: String, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            ProjectsManager.clearCompileCache(projectId)

            withContext(Dispatchers.Main) { callback() }
        }
    }
}