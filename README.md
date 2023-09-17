# Bazik

Bazik is a dynamic task execution system designed to efficiently manage and execute tasks based on a specified configuration. Using a combination of YAML and Kotlin, you can easily set up tasks, define their behavior, and establish dependencies.

## Getting Started

### Prerequisites

- Kotlin Compiler
- Java Development Kit (JDK)

### Installation

1. **Clone the Repository**:
   ```
   git clone https://github.com/TimurMalanin/Bazik.git
   ```
  
3. **Navigate to the Project Directory**:
   ```
   cd Bazik
   ```
5. **Run the Main Method**: 
To execute the program, run the `main` method of the project using your preferred method (IDE or command line).

## Project Structure

By default, Bazik is structured with the following key files:

- **Actions.kt**: Contains Kotlin functions that define the behavior of each task.
- **tasks.yaml**: Describes each task using a YAML format.
- **config.txt**: Specifies the dependencies of each task.

You can use these default files to test Bazik's functionalities right out of the box. During execution, if you simply press `Enter` when prompted for paths, Bazik will use these default files.

## Configuration

### 1. Defining Tasks: `tasks.yaml`

Tasks should be listed under the `actions` key. Each task has an `id` and an associated `actionName`, which corresponds to a function in `Actions.kt`.

```yaml
actions:
- id: prepare
 actionName: prepareFunction
- id: compileA
 actionName: compileFunctionA
...
```
### 2. Defining Config: `config.txt`
Specify how tasks depend on each other. The format is:

```txt
task prepare : {}
task compileA : {prepare}
task compileB : {prepare}
task link : {compileA,compileB}
task test : {link}
task deploy : {test}
```
### 3. Task Behavior: Actions.kt

Inside the Actions object, define how each task behaves using Kotlin functions, function should accept `List<File>` as its parameter and return a `List<File>`.
Inside the function, carry out the necessary actions and return the corresponding list of files (if required):

```kotlin
object Actions {
    fun prepareFunction(files: List<File>): List<File> {
        // Implementation for 'prepare' task
    }

    fun compileFunctionA(files: List<File>): List<File> {
        // Implementation for 'compileA' task
    }
    ...
}
```
