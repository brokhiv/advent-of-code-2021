package days.day05

import util.Parser
import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day05"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readLines()
            partOne(text)
            partTwo(text)
        }
}

data class VentLine(val start: Pair<Int, Int>, val end: Pair<Int, Int>) {

    fun range(): List<Pair<Int, Int>> {
        val (x1, y1) = start
        val (x2, y2) = end

        if (x1 == x2) {
            return if (y1 <= y2) (y1..y2).toList().map { Pair(x1, it) } else (y2..y1).toList().map { Pair(x1, it) }
        }
        if (y1 == y2) {
            return if (x1 <= x2) (x1..x2).toList().map { Pair(it, y1) } else (x2..x1).toList().map { Pair(it, y1) }
        }
        if (x1 - x2 == y1 - y2) {
            return if (x1 <= x2) (x1..x2).toList().zip((y1..y2).toList()) else (x2..x1).toList().zip((y2..y1).toList())
        }
        if (x1 - x2 == -(y1 - y2)) {
            return if (x1 <= x2) (x1..x2).toList().zip((y1 downTo y2).toList()) else (x2..x1).toList().zip((y2 downTo y1).toList())
        }
        error("$this is not diagonal")
    }

    companion object {
        private val parser = Parser<VentLine>(",", " -> ", ",")

        fun valueOf(text: String): VentLine =
            parser.parse(text) { (x1, y1, x2, y2) ->
                VentLine(Pair(x1.toInt(), y1.toInt()), Pair(x2.toInt(), y2.toInt()))
            }

    }
}

// Quick debugging function, prints a 10x10 grid like the example
fun show(it: Array<Array<Int>>) = it
    .map { it.joinToString("", "", "", limit = 10, truncated = "") }
    .joinToString("\n", limit = 10, truncated = "")
    .replace('0', '.')

fun partOne(lines: List<String>) {
    val straightLines = lines
        .map { VentLine.valueOf(it) }
        .filter { (p1, p2) -> p1.first == p2.first || p1.second == p2.second }

    solve(straightLines)
}

fun partTwo(lines: List<String>) {
    val allLines = lines.map { VentLine.valueOf(it) }

    solve(allLines)
}

private fun solve(allLines: List<VentLine>) {
    val field = Array(1000) { Array(1000) { 0 } }

    for (line in allLines) {
        for ((x, y) in line.range()) field[x][y]++
    }

    println(field.sumOf { it.count { it >= 2 } })
}