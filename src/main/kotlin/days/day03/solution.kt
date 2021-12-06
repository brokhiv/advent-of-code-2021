package days.day03

import util.countEq
import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day03"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readLines()
            partOne(text)
            partTwo(text)
            println()
            partOneBetter(text)
            partTwoBetter(text)
            println()
        }
}

fun partOne(diagnostics: List<String>) {
    var gamma = 0
    var epsilon = 0

    diagnostics
        .map { it.map { it.digitToInt() } }
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

fun partTwo(diagnostics: List<String>) {
    var oxygenNumbers: List<List<Int>> = diagnostics.map { it.map { it.digitToInt() } }
    var co2Numbers: List<List<Int>> = diagnostics.map { it.map { it.digitToInt() } }
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
        if ((oxygenNumbers.size == 1 && co2Numbers.size == 1) || i + 1 == diagnostics[0].length) {
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

fun partOneBetter(diagnostics: List<String>) {
    val (gamma, epsilon) = diagnostics
        .fold(List(diagnostics[0].length) { 0 }) { acc, s ->
            acc.zip(s.toList()).map { (a, c) -> a + if (c == '1') 1 else 0 }
        }
        .map { Pair((2 * it / diagnostics.size).toString(), (1 - 2 * it / diagnostics.size).toString()) }
        .reduce { acc, pair -> Pair(acc.first + pair.first, acc.second + pair.second) }

    println("Part 1 better: ${gamma.toInt(2) * epsilon.toInt(2)}")
}

private fun analyze(diagnostics: List<String>): Pair<String, String> {
     tailrec fun go(
        mostCommon: List<String>,
        leastCommon: List<String> = mostCommon,
        i: Int = 0
    ): Triple<List<String>, List<String>, Int> {
        if (i >= mostCommon[0].length) return Triple(mostCommon, leastCommon, mostCommon[0].length)
        return if (mostCommon.size == 1) {
            if (leastCommon.size == 1) Triple(mostCommon, leastCommon, mostCommon[0].length)
            else { // Process leastCommon
                val lcb = leastCommon.countEq('1') * 2 <= leastCommon.size
                go(mostCommon, leastCommon.filter { it[i] == if (lcb) '1' else '0' }, i + 1)
            }
        } else { // Process mostCommon
            val mcb = mostCommon.countEq('1') * 2 >= mostCommon.size
            if (leastCommon.size == 1) {
                go(mostCommon.filter { it[i] == if (mcb) '1' else '0' }, leastCommon, i + 1)
            } else {
                val lcb = leastCommon.countEq('1') * 2 < leastCommon.size
                go (
                    mostCommon.filter { it[i] == if (mcb) '1' else '0' },
                    leastCommon.filter { it[i] == if (lcb) '1' else '0' },
                    i + 1
                )
            }
        }
    }

    val (most, least, _) = go(diagnostics)
    return Pair(most[0], least[0])
}

fun partTwoBetter(diagnostics: List<String>) {
    val (mostCommonBits, leastCommonBits) = analyze(diagnostics)

    val (oxygen, co2) = Pair(
        diagnostics.reduce { acc, el ->
            if (mostCommonBits.commonPrefixWith(acc).length > mostCommonBits.commonPrefixWith(el).length) acc else el
        }, diagnostics.reduce { acc, el ->
            if (leastCommonBits.commonPrefixWith(acc).length > leastCommonBits.commonPrefixWith(el).length) acc else el
        }
    )

    println("Part 2 better: ${oxygen.toInt(2) * co2.toInt(2)}")
}