import java.io.File

/**
 * Represents a graph of tasks, where each node is a task and each edge indicates
 * a dependency between tasks.
 */
class TaskGraph {

    private val nodes: MutableMap<String, Node> = mutableMapOf()
    private val edges: MutableList<Edge> = mutableListOf()

    /**
     * Fetches a node by its identifier.
     */
    fun getNodeById(id: String): Node? = nodes[id]

    /**
     * Retrieves the list of nodes on which the given node depends.
     */
    fun getDependenciesOf(node: Node): List<Node> = edges.filter { it.to == node }.map { it.from }

    /**
     * Loads the provided nodes and the configuration from the given file.
     */
    fun dataLoader(nodes: List<Node>, configFile: File) {
        nodes.forEach { this.nodes[it.id] = it }
        parseAndLoadConfig(configFile)
    }

    /**
     * Parses the configuration file to determine task dependencies.
     */
    private fun parseAndLoadConfig(configFile: File) {
        configFile.readLines().forEach { line ->
            val (taskName, dependencies) = parseTaskConfiguration(line)
            dependencies.forEach { dependency ->
                nodes[dependency]?.let { fromNode ->
                    nodes[taskName]?.let { toNode ->
                        edges.add(Edge(fromNode, toNode))
                    } ?: println("Error: Failed to find node for $taskName!")
                } ?: println("Error: Failed to find node for $dependency!")
            }
        }
    }

    /**
     * Parses a single task configuration line and returns the task name and its dependencies.
     */
    private fun parseTaskConfiguration(line: String): Pair<String, List<String>> {
        val parts = line.split(":")
        val taskName = parts[0].split(" ")[1].trim()
        val dependencies = parts[1]
            .replace("{", "")
            .replace("}", "")
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        return taskName to dependencies
    }
}

/**
 * Represents a task with an associated action.
 */
data class Node(val id: String, var action: (List<File>) -> List<File>)

/**
 * Represents a directed edge (dependency) between two nodes (tasks).
 */
data class Edge(val from: Node, val to: Node)
