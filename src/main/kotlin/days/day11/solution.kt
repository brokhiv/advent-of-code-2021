package days.day11

import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day11"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readLines().map { it.map(Char::digitToInt).toTypedArray() }.toTypedArray()
            partOne(text)
            partTwo(text)
        }
}

private fun step(octopus: Array<Array<Int>>): Pair<Int, Array<Array<Int>>> {
    fun flash(octopuses: Array<Array<Int>>, flashing: Array<Array<Boolean>>, i: Int, j: Int): Int {
        var flashes = 0
        val neighbours = buildList {
            if (i > 0) {
                if (j > 0) add(i - 1 to j - 1)
                add(i - 1 to j)
                if (j < octopuses[i].lastIndex) add(i - 1 to j + 1)
            }
            if (i < octopuses.lastIndex) {
                if (j > 0) add(i + 1 to j - 1)
                add(i + 1 to j)
                if (j < octopuses[i].lastIndex) add(i + 1 to j + 1)
            }
            if (j > 0) add(i to j - 1)
            if (j < octopuses[i].lastIndex) add(i to j + 1)
        }
        for ((r, c) in neighbours) {
            if (++octopuses[r][c] > 9 && !flashing[r][c]) {
                flashes++
                flashing[r][c] = true
                flashes += flash(octopuses, flashing, r, c)
            }
        }
        return flashes
    }

    val octopuses = octopus.map { row -> row.map { it + 1 }.toTypedArray() }.toTypedArray()
    var flashes = 0
    val flashing = Array(octopus.size) { Array(octopus[0].size) { false } }
    for (i in octopuses.indices) {
        for (j in octopuses.indices) {
            if (octopuses[i][j] > 9 && !flashing[i][j]) {
                flashes++
                flashing[i][j] = true
                flashes += flash(octopuses, flashing, i, j)
            }
        }
    }
    octopuses.forEachIndexed { i, row -> row.forEachIndexed { j, col -> if (col > 9) octopuses[i][j] = 0 } }
    return flashes to octopuses
}

fun partOne(input: Array<Array<Int>>) {
    var flashes = 0
    var lastStep = input
    repeat(100) {
        val (f, s) = step(lastStep)
        flashes += f
        lastStep = s
    }

    println("Part 1: $flashes")
}

fun partTwo(input: Array<Array<Int>>) {
    var lastStep = 0 to input
    var i = 0
    while (lastStep.first != lastStep.second.sumOf { it.size }) {
        lastStep = step(lastStep.second)
        i++
    }
    println("Part 2: $i")
}