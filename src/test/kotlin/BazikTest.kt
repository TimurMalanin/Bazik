import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * This test class is designed to test the behavior of the build execution tasks.
 */
class BasicTest {

   private val testConfigPath = "${System.getProperty("user.home")}\\IdeaProjects\\Bazik\\src\\test\\resources\\testConfig"

    companion object {
        /** Task name constants */
        const val COMPILE = "compile"
        const val LINK = "link"
        const val RUN = "run"
    }

    /** Defines the 'compile' task */
    private val compileTask = Node(COMPILE) { _ ->
        println("Compiling the source code...")
        listOf(File("objectFile.o"))
    }

    /** Defines the 'link' task */
    private val linkTask = Node(LINK) { files ->
        println("Linking object files: $files...")
        listOf(File("executable.exe"))
    }

    /** Defines the 'run' task */
    private val runTask = Node(RUN) { files ->
        println("Executing the binary: $files")
        emptyList()
    }

    /** Represents the task graph for the build execution */
    private val graph = TaskGraph()

    init {
        graph.dataLoader(listOf(compileTask, linkTask, runTask), File(testConfigPath))
    }

    /** The executor responsible for running tasks */
    private val executor = BuildExecutor(graph)

    /**
     * A helper method to track the execution order of tasks.
     *
     * @param tasks The list of tasks to be tracked.
     * @param orderList A mutable list to record the execution order.
     */
    private fun setExecutionOrderTracker(tasks: List<Node>, orderList: MutableList<String>) {
        tasks.forEach { task ->
            task.action = { _ ->
                orderList.add(task.id)
                emptyList<File>()
            }
        }
    }

    /**
     * A helper method to track how many times each task has been executed.
     *
     * @param tasks The list of tasks to be tracked.
     * @param counter A mutable map to record the execution count for each task.
     */
    private fun setExecutionCounter(tasks: List<Node>, counter: MutableMap<String, Int>) {
        tasks.forEach { task ->
            task.action = { _ ->
                counter[task.id] = counter.getOrDefault(task.id, 0) + 1
                emptyList()
            }
        }
    }

    @Test
    fun `test task execution order`() {
        val executionOrder = mutableListOf<String>()
        setExecutionOrderTracker(listOf(compileTask, linkTask, runTask), executionOrder)

        executor.execute(RUN)
        assertEquals(executionOrder, listOf(COMPILE, LINK, RUN), "Tasks were executed in the wrong order")
    }

    @Test
    fun `test task execution uniqueness`() {
        val executionCounter = mutableMapOf<String, Int>()
        setExecutionCounter(listOf(compileTask, linkTask, runTask), executionCounter)

        executor.execute(RUN)
        executor.execute(LINK)

        assertTrue(executionCounter[COMPILE] == 1, "Task '$COMPILE' was executed more than once")
        assertTrue(executionCounter[LINK] == 1, "Task '$LINK' was executed more than once")
        assertTrue(executionCounter[RUN] == 1, "Task '$RUN' was executed more than once")
    }
}
