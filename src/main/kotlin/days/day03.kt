package days

import java.io.File

fun main() {
    val file = File("src/main/kotlin/inputs/day03.txt")

    val example = """
        00100
        11110
        10110
        10111
        10101
        01111
        00111
        11100
        10000
        11001
        00010
        01010
    """.trimIndent().split("\n").map { it.map { it.digitToInt() } }

    val diagnostics = file.readLines().map { it.map { it.digitToInt() } }

    solve(diagnostics)
    partTwo(diagnostics)
}

fun solve(diagnostics: List<List<Int>>) {
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


    println("Part one: ${gamma * epsilon}")
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

    println(oxygenRating * co2Rating)
}