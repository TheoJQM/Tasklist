package tasklist

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

class Tasklist {
    private val tasks = mutableListOf<Task>()
    private var end = false
    private val file = File("./tasklist.json")

    init {
        if (file.exists() && file.readText().isNotEmpty()) import()
    }

    fun use() {
        while (!end) {
            val action = println("Input an action (add, print, edit, delete, end):").run { readln().lowercase().trim() }
            when (action) {
                "add" -> add()
                "print" -> printTasks()
                "edit" -> editTask()
                "delete" -> delete()
                "end" -> end = true
                else -> println("The input action is invalid")
            }
        }
        println("Tasklist exiting!")
        export()
    }

    private fun add() {
        val newTask = Task()
        newTask.create()
        if (newTask.isComplete) tasks.add(newTask)
    }

    private fun printTasks(): Boolean {
        if (tasks.isEmpty()) {
            println("No tasks have been input")
            return false
        } else {
            println("""
            +----+------------+-------+---+---+--------------------------------------------+
            | N  |    Date    | Time  | P | D |                   Task                     |
            +----+------------+-------+---+---+--------------------------------------------+
            """.trimIndent())
            tasks.forEachIndexed { index, task ->
                task.printTask(index + 1)
            }
            println()
            return true
        }
    }

    private fun editTask() {
        if (printTasks()) {
            val index = getTaskIndex()
            var field: String
            while (true) {
                field = println("Input a field to edit (priority, date, time, task):").run { readln().trim() }
                when (field) {
                    "priority" -> { tasks[index].setPriorityTag() ; break }
                    "date" -> { tasks[index].setDate() ; break }
                    "time" -> { tasks[index].setTime() ; break }
                    "task" -> { tasks[index].setContent() ; break  }
                    else -> println("Invalid field")
                }
            }
            println("The task is changed")
        }
    }

    private fun delete() {
        if (printTasks()) {
            val index = getTaskIndex()
            tasks.removeAt(index)
            println("The task is deleted")
        }
    }

    private fun getTaskIndex(): Int {
        val maxIndex = tasks.size
        var index: String

        while (true) {
            index = println("Input the task number (1-$maxIndex):").run { readln().trim() }
            if (index in "1"..maxIndex.toString()) break
            else println("Invalid task number")
        }
        return index.toInt() - 1
    }

    private fun import() {
        val content = file.readText()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val taskAdapter = moshi.adapter<List<Task>>(Types.newParameterizedType(List::class.java, Task::class.java)).indent("  ")
        tasks.addAll(taskAdapter.fromJson(content)!!)
    }

    private fun export() {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val taskAdapter = moshi.adapter<List<Task>>(Types.newParameterizedType(List::class.java, Task::class.java)).indent("  ")
        file.writeText(taskAdapter.toJson(tasks))
    }
}

fun main() {
    Tasklist().use()
}