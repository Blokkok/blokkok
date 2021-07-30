package com.blokkok.app.ui.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blokkok.app.managers.projects.ProjectMetadata
import com.blokkok.app.managers.projects.ProjectsManager
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val projectsLiveDataMutable = MutableLiveData<List<ProjectMetadata>>()

    val projectsLiveData = projectsLiveDataMutable

    fun fetchProjects() {
        viewModelScope.launch {
            projectsLiveDataMutable.value = ProjectsManager.listProjects()
        }
    }

    fun createProject(name: String, packageName: String) {
        viewModelScope.launch {
            ProjectsManager.createProject(name, packageName)

            fetchProjects()
        }
    }
}