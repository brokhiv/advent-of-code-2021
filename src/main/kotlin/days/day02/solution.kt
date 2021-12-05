package days.day02

import util.Parser
import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day02"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readLines()
            partOne(text)
            partTwo(text)

            partOneParsed(text)
            partTwoParsed(text)
        }
}

fun partOne(directions: List<String>) {
    var depth = 0
    var horizontal = 0

    for (dir in directions.map { it.split(" ")}) {
        when (dir[0]) {
            "up" -> depth -= dir[1].toInt()
            "down" -> depth += dir[1].toInt()
            "forward" -> horizontal += dir[1].toInt()
        }
    }
    println("Part 1: ${horizontal * depth}")
}

fun partTwo(directions: List<String>) {
    var depth = 0
    var horizontal = 0
    var aim = 0

    for (dir in directions.map { it.split(" ")}) {
        when (dir[0]) {
            "up" -> {
                aim -= dir[1].toInt()
            }
            "down" -> {
                aim += dir[1].toInt()
            }
            "forward" -> {
                horizontal += dir[1].toInt()
                depth += aim * dir[1].toInt()
            }
        }
    }
    println("Part 2: ${horizontal * depth}")
}

enum class CommandType {
    UP,
    DOWN,
    FORWARD
}

data class SubmarineCommand(val type: CommandType, val value: Int) {
    companion object {
        private val parser = Parser<SubmarineCommand>(" ")

        fun valueOf(text: String) = parser.parse(text) {
            SubmarineCommand(CommandType.valueOf(it[0].uppercase()), it[1].toInt())
        }
    }
}

data class SubmarinePosition(
    val horizontal: Int = 0,
    val depth: Int = 0,
    val aim: Int = 0
) {
    val result = depth * horizontal

    fun transform(_horizontal: Int = 0, _depth: Int = 0, _aim: Int = 0) =
        SubmarinePosition(
            horizontal + _horizontal,
            depth + _depth,
            aim + _aim
        )
}

fun partOneParsed(directions: List<String>) {
    val finalPosition = directions
        .map(SubmarineCommand::valueOf)
        .fold(SubmarinePosition()) { acc, command ->
            when (command.type) {
                CommandType.UP -> acc.transform(_depth = -command.value)
                CommandType.DOWN -> acc.transform(_depth = command.value)
                CommandType.FORWARD -> acc.transform(_horizontal = command.value)
            }
        }
    println("Part 1 parsed: ${finalPosition.result}")
}

fun partTwoParsed(directions: List<String>) {
    val finalPosition = directions
        .map(SubmarineCommand::valueOf)
        .fold(SubmarinePosition()) { acc, command ->
            when (command.type) {
                CommandType.UP -> acc.transform(_aim = -command.value)
                CommandType.DOWN -> acc.transform(_aim = command.value)
                CommandType.FORWARD -> acc.transform(_horizontal = command.value, _depth = command.value * acc.aim)
            }
        }
    println("Part 2 parsed: ${finalPosition.result}")
}