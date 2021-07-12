package com.blokkok.app

import androidx.test.platform.app.InstrumentationRegistry
import com.blokkok.app.managers.projects.ProjectMetadata
import com.blokkok.app.managers.projects.ProjectsManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import java.io.File

class ProjectsManagerTest {
    @Test
    fun testProjectsListing() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val projectsDir = File(context.filesDir, "projects")

        ProjectsManager.initialize(context)
        ProjectsManager.clearProjects()

        val projectIds = arrayOf("test_id_0", "test_id_1", "test_id_2", "test_id_3")
        projectIds.forEach { addProject(it, projectsDir) }

        val listedProjects = ProjectsManager.listProjects()

        projectIds.forEach { assert(ProjectsManager.exists(it)) }
        listedProjects.forEach { assert(projectIds.contains(it.id)) }
    }

    private fun addProject(id: String, projectsDir: File) {
        val metadata = ProjectMetadata("TestProject", "test.project", id)
        val projectDir = File(projectsDir, id)

        projectDir.mkdir()

        File(projectDir, "android").mkdir()     // Files required by the compiler
        File(projectDir, "data").mkdir()        // Project files
        File(projectDir, "cache").mkdir()       // Cache folder

        File(projectDir, "meta.json").writeText(Json.encodeToString(metadata))
    }

    @Test
    fun testGetProject() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val projectsDir = File(context.filesDir, "projects")

        ProjectsManager.initialize(context)
        ProjectsManager.clearProjects()

        addProject("test_id_0", projectsDir)
        val project = ProjectsManager.getProject("test_id_0")

        assert(project != null)
        assert(project!!.name == "TestProject" && project.packageName == "test.project" && project.id == "test_id_0")
    }

    @Test
    fun testCreateProject() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val projectsDir = File(context.filesDir, "projects")

        ProjectsManager.initialize(context)
        ProjectsManager.clearProjects()

        ProjectsManager.createProject("TestProject", "test.project")
        val listing = ProjectsManager.listProjects()

        assert(listing.size == 1)
        assert(listing[0].name == "TestProject" && listing[0].packageName == "test.project")
        assert(File(projectsDir, listing[0].id).exists() && File(projectsDir, listing[0].id).isDirectory)
    }

    @Test
    fun testRemoveProject() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        ProjectsManager.initialize(context)
        ProjectsManager.clearProjects()

        ProjectsManager.createProject("TestProject", "test.project")
        var listing = ProjectsManager.listProjects()

        assert(ProjectsManager.removeProject(listing[0].id))
        listing = ProjectsManager.listProjects()

        assert(listing.isEmpty())
    }

    @Test
    fun testWriteCode() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        ProjectsManager.initialize(context)
        ProjectsManager.clearProjects()

        val metadata =
            ProjectsManager
                .createProject("TestProject", "test.project")

        val editor = metadata.edit(context)

        editor.java["test.project.HelloWorld"] = "// Content of HelloWorld.java"
        editor.layout["hello_world"] = "<!-- Content of hello_world.xml -->"

        val javaCode =
            File(
                context.applicationInfo.dataDir,
                "projects/${metadata.id}/data/java/com/test/project/HelloWorld.java"
            )

        assert(javaCode.exists())
        assert(javaCode.readText() == "// Content of HelloWorld.java")

        val layoutCode =
            File(
                context.applicationInfo.dataDir,
                "projects/${metadata.id}/data/res/layout/hello_world.xml"
            )

        assert(layoutCode.exists())
        assert(layoutCode.readText() == "<!-- Content of hello_world.xml -->")
    }
}