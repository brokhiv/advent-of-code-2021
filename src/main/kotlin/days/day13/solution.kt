package days.day13

import java.io.File
import kotlin.math.max

fun main() {
    val today = "src/main/kotlin/days/day13"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readText() // TODO processing
            partOne(text)
            partTwo(text)
        }
}

data class Sheet(val dots: List<List<Boolean>>) {
    fun foldAlongX(col: Int) = Sheet(dots
        .map { row -> row
            .subList(0, col)
            .mapIndexed { c, d -> d || row[2 * col - c] }
        })

    fun foldAlongY(row: Int) = Sheet(dots
        .subList(0, row)
        .mapIndexed { r, ds -> ds.zip(dots[2 * row - r]) { a, b -> a || b } })

    override fun toString(): String = dots.joinToString("\n") { it.joinToString("") { if (it) "#" else "." } }

    companion object {
        fun valueOf(text: String): Sheet {
            val coords = text
                .split("\n|\r\n".toRegex())
                .map { it.split(",", limit = 2).let { (x, y) -> x.toInt() to y.toInt() } }

            val (maxX, maxY) = coords.reduce { (aX, aY), (pX, pY) -> max(aX, pX) to max(aY, pY) }

            val dots = Array (maxY + 1) { BooleanArray(maxX + 1) }
            coords.forEach { (x, y) ->
                dots[y][x] = true
            }

            return Sheet(dots.map(BooleanArray::toList))
        }
    }
}

data class Fold(val type: FoldType, val pos: Int) {
    enum class FoldType { ALONG_X, ALONG_Y }

    companion object {
        fun valueOf(text: String): Fold {
            val (axis, pos) = """fold along ([xy])=(\d+)""".toRegex().matchEntire(text)!!.destructured
            return when (axis) {
                "x" -> Fold(FoldType.ALONG_X, pos.toInt())
                "y" -> Fold(FoldType.ALONG_Y, pos.toInt())
                else -> error("$axis is not valid")
            }
        }

        fun valuesOf(text: String): List<Fold> = text.split("\r?\n".toRegex()).map(::valueOf)
    }

    fun apply(sheet: Sheet) = when (type) {
        FoldType.ALONG_X -> sheet.foldAlongX(pos)
        FoldType.ALONG_Y -> sheet.foldAlongY(pos)
    }
}

fun partOne(input: String) {
    val (sheet, folds) = input
        .split("(\r?\n){2}".toRegex(), limit = 2)
        .let { (s, f) -> Sheet.valueOf(s) to Fold.valuesOf(f) }

    val sheet1 = folds.first().apply(sheet)

    println("Part 1: ${sheet1.dots.sumOf { it.count { it } }}")
}

fun partTwo(input: String) {
    val (sheet, folds) = input
        .split("(\r?\n){2}".toRegex(), limit = 2)
        .let { (s, f) -> Sheet.valueOf(s) to Fold.valuesOf(f) }

    val final = folds.fold(sheet) { s, f -> f.apply(s)}
    println("Part 2: \n$final")
}