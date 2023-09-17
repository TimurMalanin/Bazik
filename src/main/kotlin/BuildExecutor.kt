import java.io.File

/**
 * Executes tasks from the provided task graph. Handles task dependencies and avoids executing
 * the same task multiple times.
 *
 * @param graph The task graph to be used for execution.
 */
class BuildExecutor(private val graph: TaskGraph) {

    private val executedTasks: MutableSet<Node> = mutableSetOf()

    /**
     * Executes the task with the given ID. If the task or its dependencies have been
     * executed previously, they won't be executed again.
     *
     * @param taskId The ID of the task to be executed.
     */
    fun execute(taskId: String) {
        val taskNode = graph.getNodeById(taskId)

        taskNode?.let { executeTask(it) } ?: println("Task with id $taskId not found!")
    }

    /**
     * Executes the given task, ensuring all its dependencies are executed first.
     *
     * @param node The task node to be executed.
     * @return A list of files generated by the task execution.
     */
    private fun executeTask(node: Node): List<File> {
        if (node in executedTasks) return emptyList()

        val inputFiles = graph.getDependenciesOf(node).flatMap { executeTask(it) }

        val outputFiles = node.action(inputFiles)

        executedTasks.add(node)

        return outputFiles
    }
}