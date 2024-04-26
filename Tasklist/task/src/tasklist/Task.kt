package tasklist

import kotlinx.datetime.*

enum class Tag(val meaning: String, val color: String) {
    C("Critical", "\u001B[101m \u001B[0m"),
    H("High", "\u001B[103m \u001B[0m"),
    N("Normal", "\u001B[102m \u001B[0m"),
    L("Low", "\u001B[104m \u001B[0m"),
    I("In time", "\u001B[102m \u001B[0m"),
    T("Today", "\u001B[103m \u001B[0m"),
    O("Overdue", "\u001B[101m \u001B[0m")
}

class Task() {
    private lateinit var priorityTag: Tag
    private lateinit var dueTag: Tag
    private lateinit var date: String
    private lateinit var time: String
    private var content: MutableList<String> = mutableListOf()
    var isComplete = false

    constructor(pt: Tag, dt: Tag, newDate: String, newTime: String, newContent: MutableList<String>, complete: Boolean) : this() {
        priorityTag = pt
        dueTag = dt
        date = newDate
        time = newTime
        content = newContent
        isComplete = complete
    }


    fun create() {
        setPriorityTag()
        setDate()
        setTime()
        setContent()
        if (content.isNotEmpty()) isComplete = true
    }

    fun setPriorityTag() {
        var tag = println("Input the task priority (C, H, N, L):").run { readln().uppercase() }

        while (!Tag.values().map { it.toString() }.contains(tag)) {
            tag = println("Input the task priority (C, H, N, L):").run { readln().uppercase() }
        }
        priorityTag = Tag.valueOf(tag)
    }

    fun setDate() {
        val dateRegex = Regex("""\d{4}-\d{1,2}-\d{1,2}""")
        var newDate = println("Input the date (yyyy-mm-dd):").run { readln() }
        while (!dateRegex.matches(newDate) || checkDate(newDate)) {
            newDate = println("The input date is invalid\nInput the date (yyyy-mm-dd):").run { readln() }
        }
        setDueTag()
    }

    private fun setDueTag() {
        val currentDate  = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        dueTag = when {
            currentDate.daysUntil(LocalDate.parse(date)) == 0 -> Tag.T
            currentDate.daysUntil(LocalDate.parse(date)) > 0 -> Tag.I
            else -> Tag.O
        }
    }

    private fun checkDate(newDate: String): Boolean {
        try {
            val myDate = newDate.split("-").map { it.toInt()}
            date = LocalDate(myDate[0], myDate[1], myDate[2]).toString()
            return false
        } catch (_: IllegalArgumentException ) {
            return true
        }
    }

    fun setTime() {
        val timeRegex = Regex("""([01]?[0-9]|2[0-3]):([0-5]?[0-9])""")
        var newTime = println("Input the time (hh:mm):").run{ readln() }
        while (!timeRegex.matches(newTime)) {
            newTime = println("The input time is invalid\nInput the time (hh:mm):").run{ readln() }
        }
        time = newTime.split(":").joinToString(":") { String.format("%02d", it.toInt()) }
    }

    fun setContent() {
        content.clear()
        println("Input a new task (enter a blank line to end):")
        while (true) {
            val input = readln().trim()
            if (input.isEmpty()) break else addContent(input)
        }
        if (content.isEmpty()) println("The task is blank")
    }

    private fun addContent(input: String) {
        var newContent = input
        while (newContent.isNotEmpty()) {
            content.add(newContent.take(minOf(44, newContent.length)))
            newContent = newContent.drop(minOf(44, newContent.length))
        }
    }

    fun printTask(number: Int) {
        println("|${String.format(" %-2d ", number)}| $date | $time | ${priorityTag.color} | ${dueTag.color} |${String.format("%-44s", content.first())}|")
        for (line in 1 until content.size) {
            println("|    |            |       |   |   |${String.format("%-44s", content[line])}|")
        }

        println("+----+------------+-------+---+---+--------------------------------------------+")
    }
}