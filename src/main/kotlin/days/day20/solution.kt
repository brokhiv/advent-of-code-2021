package days.day20

import util.countEq
import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day20"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readText().split("(\\r?\\n){2}".toRegex()).let { it[0] to it[1].lines() }
            partOne(text)
            partTwo(text)
        }
}

typealias Image = List<String>

fun Image.getIndex(i: Int, j: Int, flip: Boolean = false): Int {
    var bits = ""
    for (di in -1..1) {
        for (dj in -1..1) {
            bits +=
                if (i + di in this.indices && j + dj in this[i + di].indices)
                    if (this[i + di][j + dj] == '#') '1' else '0'
                else if (flip) '1' else '0'
        }
    }
    return bits.toInt(2)
}

fun Image.enlarged(pad: Char = '.'): Image = listOf(String(CharArray(this[0].length + 2) { pad })) +
        this.map { "$pad$it$pad" } +
        listOf(String(CharArray(this[0].length + 2) { pad }))

fun Image.enhance(algorithm: String, iteration: Int = 1): Image = this
    .enlarged(if (algorithm[0] == '#' && iteration % 2 == 0) '#' else '.').let { it
        .mapIndexed { i, row -> row
            .mapIndexed { j, _ -> algorithm[it.getIndex(i, j, algorithm[0] == '#' && iteration % 2 == 0)] }
            .joinToString("")
        }
    }

fun partOne(input: Pair<String, Image>) {
    val (algo, image) = input
    val res = image.enhance(algo).enhance(algo, 2)

    println("Part 1: ${res.sumOf { it.countEq('#') }}")
}

fun partTwo(input: Pair<String, List<String>>) {
    val (algo, image) = input
    val res = (1..50).fold(image) { acc, i -> acc.enhance(algo, i) }

    println("Part 2: ${res.sumOf { it.countEq('#') }}")
}