fun main() {
    val taskManager = TaskManager()

    taskManager.setupPaths()
    taskManager.loadTasks()
    taskManager.executeTasks()
}