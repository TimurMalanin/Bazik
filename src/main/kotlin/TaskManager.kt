import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions

/**
 * The TaskManager class encapsulates the logic for setting up paths, loading tasks from a YAML file,
 * and executing these tasks. It serves to simplify and modularize the main entry point of the application.
 */
class TaskManager {

    private val defaultProjectPath = "${System.getProperty("user.home")}/IdeaProjects/Bazik"
    private val defaultTasksYamlPath = "$defaultProjectPath/src/main/resources/tasks.yaml"
    private val defaultConfigPath = "$defaultProjectPath/src/main/resources/config"
    private val defaultActionsKtPath = "$defaultProjectPath/src/main/kotlin/Actions.kt"

    private var yamlFilePath: String = ""
    private var configFilePath: String = ""
    private var actionsPath: String = ""

    /**
     * Setup paths for the tasks.yaml, config, and Actions.kt files.
     * Defaults are provided, but the user can specify their own paths.
     */
    fun setupPaths() {
        yamlFilePath = requestFilePath(
            prompt = "Please enter the path to tasks.yaml",
            defaultPath = defaultTasksYamlPath
        )

        configFilePath = requestFilePath(
            prompt = "Please enter the path to the config file",
            defaultPath = defaultConfigPath
        )

        actionsPath = requestFilePath(
            prompt = "Please enter the path to the Actions.kt file",
            defaultPath = defaultActionsKtPath
        )

        if (actionsPath != defaultActionsKtPath) {
            copyFileContents(from = actionsPath, to = defaultActionsKtPath)
        }
    }

    private var tasks: MutableList<Node>? = null
    private var lastTaskId: String? = null

    /**
     * Loads tasks from a specified YAML file and associates them with their corresponding actions.
     */
    fun loadTasks() {
        val adaptedActionMap = adaptActions()
        val (loadedTasks, lastTask) = loadTasksFromYaml(yamlFilePath, adaptedActionMap)
        tasks = loadedTasks
        lastTaskId = lastTask
    }

    /**
     * Executes the loaded tasks using a BuildExecutor on a constructed TaskGraph.
     */
    fun executeTasks() {
        val graph = TaskGraph()
        tasks?.let { loadedTasks ->
            graph.dataLoader(loadedTasks, File(configFilePath))
        }

        val executor = BuildExecutor(graph)
        if (lastTaskId != null) {
            executor.execute(lastTaskId!!)
        } else {
            println("No tasks found in the config!")
        }
    }


    /**
     * Prompts the user for a file path or uses the default path if none is provided.
     */
    private fun requestFilePath(prompt: String, defaultPath: String): String {
        println("$prompt (press Enter to use the default value):")
        return readLine()?.takeIf { it.isNotBlank() } ?: defaultPath
    }

    /**
     * Adapts the actions from the Actions object for use with task nodes.
     */
    private fun adaptActions(): Map<String, (List<File>) -> List<File>> {
        val actionMap = Actions::class.functions.associateBy { it.name }
        return actionMap.mapValues { (_, kFunction) -> kFunction.createAdapter() }
    }

    /**
     * Creates an adapter for a KFunction, allowing it to be used as a node action.
     */
    private fun KFunction<*>.createAdapter(): (List<File>) -> List<File> {
        return { files -> this.call(Actions, files) as List<File> }
    }

    /**
     * Loads tasks from a specified YAML file and associates them with their corresponding actions.
     */
    private fun loadTasksFromYaml(yamlFilePath: String, adaptedActionMap: Map<String, (List<File>) -> List<File>>): Pair<MutableList<Node>, String?> {
        val tasks = mutableListOf<Node>()

        val yaml = Yaml()
        val inputStream = FileInputStream(yamlFilePath)
        val data: Map<String, List<Map<String, String>>> = yaml.load(inputStream)

        val actions = data["actions"] ?: emptyList()
        var lastTaskId: String? = null

        for (action in actions) {
            action["id"]?.let { id ->
                lastTaskId = id
                adaptedActionMap[action["actionName"]]?.let { actionFunction ->
                    tasks.add(Node(id, actionFunction))
                }
            }
        }

        return tasks to lastTaskId
    }

    /**
     * Copies the contents of the source file to the destination file.
     */
    private fun copyFileContents(from: String, to: String) {
        val source = Paths.get(from)
        val destination = Paths.get(to)

        if (!Files.exists(source)) {
            println("Provided file does not exist: $from")
            return
        }

        Files.copy(source, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
    }
}