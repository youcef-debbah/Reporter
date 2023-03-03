tasks.register<Delete>("cleanAll") {
    description = "Delete output directories in the root project and all subprojects"
    delete("out", "build")
    project.subprojects.forEach { subproject ->
        subproject.tasks["clean"]?.let { subtask ->
            this.dependsOn(subtask)
        }
    }
}