package days.day08

import util.ObjectParser
import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day08"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readLines()
            partOne(text)
            partTwo(text)
        }
}

enum class Digit(val segments: String) {
    ZERO("abcefg"),
    ONE("cf"),
    TWO("acdeg"),
    THREE("acdfg"),
    FOUR("bcdf"),
    FIVE("abdfg"),
    SIX("abdefg"),
    SEVEN("acf"),
    EIGHT("abcdefg"),
    NINE("abcdfg");

    fun decimal(): Char = when (this) {
        ZERO -> '0'
        ONE -> '1'
        TWO -> '2'
        THREE -> '3'
        FOUR -> '4'
        FIVE -> '5'
        SIX -> '6'
        SEVEN -> '7'
        EIGHT -> '8'
        NINE -> '9'
    }
}

data class Display(val patterns: List<String>, val output: List<String>) {
    companion object {
        fun valueOf(text: String): Display {
            val (a, b) = text.split(" | ")
            return Display(a.split(" "), b.split(" "))
        }
    }
}

private val uniqueLengths = setOf(Digit.ONE, Digit.FOUR, Digit.SEVEN, Digit.EIGHT)

fun partOne(input: List<String>) {
    val outputs = input.map(Display::valueOf).map { it.output }

    val count = outputs.sumOf { it
        .count { it.length in uniqueLengths.map { it.segments.length } }
    }

    println("Part 1: $count")
}

fun partTwo(input: List<String>) {
    val (patterns, outputs) = input.map(Display::valueOf).let { it.map { it.patterns } to it.map { it.output } }
    val mappings = List(input.size) { emptyMap<Char, Char>().toMutableMap() }

    val solved = Array(input.size) { -1 }

    for (i in input.indices) {
        val foundDigits = Array(10) { "?" }

        // Find 1,4,7,8
        for (p in patterns[i].filter { it.length in uniqueLengths.map { it.segments.length } }) {
            val digit = Digit.values().indexOf(uniqueLengths.single { it.segments.length == p.length })
            foundDigits[digit] = p
        }
        // Find rest of digits
        for (p in patterns[i].filterNot { it in foundDigits }) {
            when (p.length) {
                5 -> when (p.toSet().intersect(foundDigits[1].toSet()).size) {
                    1 -> when (p.toSet().intersect(foundDigits[4].toSet()).size) {
                        2 -> foundDigits[2] = p
                        3 -> foundDigits[5] = p
                    }
                    2 -> foundDigits[3] = p
                }
                6 -> when (p.toSet().intersect(foundDigits[1].toSet()).size) {
                    1 -> foundDigits[6] = p
                    2 -> when (p.toSet().intersect(foundDigits[4].toSet()).size) {
                        3 -> foundDigits[0] = p
                        4 -> foundDigits[9] = p
                    }
                }
            }
        }
        // a = 7 - 1
        mappings[i][(foundDigits[7].toSet() - foundDigits[1].toSet()).single()] = 'a'
        // b = 8 - 1|2
        mappings[i][(foundDigits[8].toSet() - foundDigits[1].toSet().union(foundDigits[2].toSet())).single()] = 'b'
        // c = 1 - 5
        mappings[i][(foundDigits[1].toSet() - foundDigits[5].toSet()).single()] = 'c'
        // d = 8 - 0
        mappings[i][(foundDigits[8].toSet() - foundDigits[0].toSet()).single()] = 'd'
        // e = 8 - 9
        mappings[i][(foundDigits[8].toSet() - foundDigits[9].toSet()).single()] = 'e'
        // f = 1 - 2
        mappings[i][(foundDigits[1].toSet() - foundDigits[2].toSet()).single()] = 'f'
        // g = 9 - 7|4
        mappings[i][(foundDigits[9].toSet() - foundDigits[7].toSet().union(foundDigits[4].toSet())).single()] = 'g'

        solved[i] = outputs[i]
            .map { p ->
                Digit.values().single { d ->
                    d.segments == p
                        .map { mappings[i][it]!! }
                        .sorted()
                        .joinToString("")
                }
            }
            .map(Digit::decimal)
            .joinToString("")
            .toInt()
    }

    check(solved.none { it == -1 })

    println("Part 2: ${solved.sum()}")
}