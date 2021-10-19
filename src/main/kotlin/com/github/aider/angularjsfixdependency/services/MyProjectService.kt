package com.github.aider.angularjsfixdependency.services

import com.intellij.openapi.project.Project
import com.github.aider.angularjsfixdependency.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
