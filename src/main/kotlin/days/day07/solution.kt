package days.day07

import util.countEq
import java.io.File
import kotlin.math.abs

fun main() {
    val today = "src/main/kotlin/days/day07"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readText().split(",")
            partOne(text)
            partTwo(text)
        }
}

fun partOne(input: List<String>) {
    val crabs = input.map(String::toInt)

    val positions = Array(crabs.maxOrNull()!! + 1) { 0 }
    for (i in crabs.minOrNull()!!..crabs.maxOrNull()!!) {
        positions[i] = crabs.countEq(i)
    }

    val costs = positions.mapIndexed { i, _ ->
        positions.foldIndexed(0) { j, acc, count ->
            acc + (abs(j - i) * count)
        }
    }

    println("Part 1: " + costs.minOrNull())
}

fun partTwo(input: List<String>) {
    val crabs = input.map(String::toInt)

    val positions = Array(crabs.maxOrNull()!! + 1) { 0 }
    for (i in crabs.minOrNull()!!..crabs.maxOrNull()!!) {
        positions[i] = crabs.countEq(i)
    }

    val costs = positions.mapIndexed { i, _ ->
        positions.foldIndexed(0) { j, acc, count ->
            acc + (abs(j - i).let { it * (it + 1) / 2 } * count)
        }
    }

    println("Part 2: " + costs.minOrNull())
}