package days

import days.VentLine.Companion.valueOf
import util.Parser
import java.io.File

fun main() {
    val file = File("src/main/kotlin/inputs/day05.txt")
    val example = """
        0,9 -> 5,9
        8,0 -> 0,8
        9,4 -> 3,4
        2,2 -> 2,1
        7,0 -> 7,4
        6,4 -> 2,0
        0,9 -> 2,9
        3,4 -> 1,4
        0,0 -> 8,8
        5,5 -> 8,2
    """.trimIndent().lines()

    print("Should be 5: "); part5One(example)
    print("Part 1 solution: "); part5One(file.readLines())
    print("Should be 12: "); part5Two(example)
    print("Part 2 solution: "); part5Two(file.readLines())
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

        fun String.valueOf(): VentLine =
            parser.parse(this) { (x1, y1, x2, y2) ->
                VentLine(Pair(x1.toInt(), y1.toInt()), Pair(x2.toInt(), y2.toInt()))
            }

    }
}

fun show(it: Array<Array<Int>>) = it
    .map { it.joinToString("", "", "", limit = 10, truncated = "") }
    .joinToString("\n", limit = 10, truncated = "")
    .replace('0', '.')


fun part5One(lines: List<String>) {
    val straightLines = lines
        .map { it.valueOf() }
        .filter { (p1, p2) -> p1.first == p2.first || p1.second == p2.second }

    val field = Array(1000) {Array(1000) { 0 } }

    for (line in straightLines) {
        for ((x,y) in line.range()) field[x][y]++
    }

    println(
        field.sumOf { it.count { it >= 2 } }
    )
}

fun part5Two(lines: List<String>) {
    val allLines = lines.map { it.valueOf() }

    val field = Array(1000) {Array(1000) { 0 } }

    for (line in allLines) {
        for ((x,y) in line.range()) field[x][y]++
    }

    println(field.sumOf { it.count { it >= 2} })
}