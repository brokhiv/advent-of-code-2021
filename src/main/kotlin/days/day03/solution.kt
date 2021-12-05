package days.day03

import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day03"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readLines().map { it.map { it.digitToInt() } }
            partOne(text)
            partTwo(text)
        }
}

fun partOne(diagnostics: List<List<Int>>) {
    var gamma = 0
    var epsilon = 0

    diagnostics
        .reduce { acc, list ->
            acc.zip(list).map { (a, l) -> a + l }
        }
        .map { 2 * it / diagnostics.size }
        .forEachIndexed { i, b ->
            gamma += b shl (diagnostics[0].lastIndex - i)
            epsilon += (1 - b) shl (diagnostics[0].lastIndex - i)
        }


    println("Part 1: ${gamma * epsilon}")
}

fun partTwo(diagnostics: List<List<Int>>) {
    var oxygenNumbers: List<List<Int>> = diagnostics
    var co2Numbers: List<List<Int>> = diagnostics
    for (i in diagnostics[0].indices) {
        if (oxygenNumbers.size > 1) {
            oxygenNumbers = oxygenNumbers.filter {
                it[i] == oxygenNumbers.sumOf { it[i] } * 2 / oxygenNumbers.size
            }
        }
        if (co2Numbers.size > 1) {
            co2Numbers = co2Numbers.filter {
                1 - it[i] == co2Numbers.sumOf { it[i] } * 2 / co2Numbers.size
            }
        }
        if ((oxygenNumbers.size == 1 && co2Numbers.size == 1) || i + 1 == diagnostics[0].size) {
            break
        }
    }

    var oxygenRating = 0
    var co2Rating = 0
    for (i in diagnostics[0].indices) {
        val weight = diagnostics[0].lastIndex - i
        oxygenRating += oxygenNumbers.first()[i] shl weight
        co2Rating += co2Numbers.first()[i] shl weight
    }

    println("Part 2: ${oxygenRating * co2Rating}")
}