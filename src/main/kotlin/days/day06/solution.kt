package days.day06

import util.countEq
import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day06"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readText().split(",")
            partOne(text)
            partTwo(text)
        }
}

private fun simulate(start: List<Int>, days: Int): Long {
    var perDay = with(start) {
        arrayOf(
            countEq(0),
            countEq(1),
            countEq(2),
            countEq(3),
            countEq(4),
            countEq(5),
            countEq(6),
            countEq(7),
            countEq(8)
            )
            .map(Int::toLong)
    }

    for (day in 1..days) {
        val next = buildList {
            // Fill next[0:5] with perDay[1:6] respectively
            for (i in 0..5) {
                add(perDay[i + 1])
            }
            // Breeders go back here
            add(perDay[0] + perDay[7])
            // 8 goes to 7
            add(perDay[8])
            // Children of breeders have 8
            add(perDay[0])
        }
        perDay = next
    }

    return perDay.sum()
}

fun partOne(input: List<String>) {
    val numbers = simulate(input.map(String::toInt), 80)

    println("Part 1: $numbers")
}

fun partTwo(input: List<String>) {
    val numbers = simulate(input.map(String::toInt), 256)

    println("Part 2: $numbers")
}