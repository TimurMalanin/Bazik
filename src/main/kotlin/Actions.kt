import java.io.File

/**
 * This object serves as a location for defining actions for task nodes.
 *
 * Each function within this object represents an individual action that can be bound to a task node.
 * When a task node is executed, the corresponding action function will be invoked with a provided list of files.
 *
 * Example:
 * If you have a task node with an action name of "prepareAction", it will invoke the [prepareAction] function, passing it a list of files.
 *
 * To add a new action:
 * 1. Define a new function within this object.
 * 2. Your function should accept `List<File>` as its parameter and return a `List<File>`.
 * 3. Inside the function, carry out the necessary actions and return the corresponding list of files (if required).
 */
object Actions {

    fun prepareAction(files: List<File>): List<File> {
        println("Preparing source data...")
        return listOf(File("data.prepared"))
    }

    fun compileAAction(files: List<File>): List<File> {
        println("Compiling part A...")
        return listOf(File("a.o"))
    }

    fun compileBAction(files: List<File>): List<File> {
        println("Compiling part B...")
        return listOf(File("b.o"))
    }

    fun linkAction(files: List<File>): List<File> {
        println("Linking files: $files...")
        return listOf(File("program.exe"))
    }

    fun testAction(files: List<File>): List<File> {
        println("Testing the program: $files...")
        return emptyList()
    }

    fun deployAction(files: List<File>): List<File> {
        println("Deploying the program...")
        return emptyList()
    }

}
