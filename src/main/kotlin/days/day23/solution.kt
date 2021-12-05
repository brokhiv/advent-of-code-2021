package days.day23

import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day23"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readText() // TODO processing
            partOne(text)
            partTwo(text)
        }
}

fun partOne(input: Any) {
    println("Part 1: Not implemented yet")
}

fun partTwo(input: Any) {
    println("Part 2: Not implemented yet")
}