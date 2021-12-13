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
            println("\n")
            partOneSet(text)
            partTwoSet(text)
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

data class SetSheet(val dots: Set<Pair<Int, Int>>) {
    override fun toString(): String = this.toSheet().toString()

    fun foldX(col: Int) = SetSheet(dots
        .partition { (x, _) -> x < col }
        .let { (same, other) -> same.toSet() + other.map { (x, y) -> (2 * col - x) to y } }
    )

    fun foldY(row: Int) = SetSheet(dots
        .partition { (_, y) -> y < row }
        .let { (same, other) -> same.toSet() + other.map { (x, y) -> x to (2 * row - y) } }
    )

    private fun toSheet(): Sheet {
        val (maxX, maxY) = this.dots.reduce { (aX, aY), (pX, pY) -> max(aX, pX) to max(aY, pY) }

        val dots = Array (maxY + 1) { BooleanArray(maxX + 1) }
        this.dots.forEach { (x, y) ->
            dots[y][x] = true
        }

        return Sheet(dots.map(BooleanArray::toList))
    }

    companion object {
        enum class FoldAxis { X, Y }

        private fun String.toFoldAxis() = when (this) {
            "x" -> FoldAxis.X
            "y" -> FoldAxis.Y
            else -> error("$this is not a valid FoldAxis")
        }

        private fun makeFold(axis: FoldAxis, pos: Int): SetFold = { sheet ->
            when (axis) {
            FoldAxis.X -> sheet.foldX(pos)
            FoldAxis.Y -> sheet.foldY(pos)
            }
        }

        fun valueOf(text: String): SetSheet =
            SetSheet(text
                .split("\r?\n".toRegex())
                .map { it.split(",").map(String::toInt).let { (x, y) -> x to y } }.toSet())

        fun valueOfAndFolds(text: String): Pair<SetSheet, List<SetFold>> = text
            .split("(\r?\n){2}".toRegex())
            .let { (dots, folds) -> valueOf(dots) to
                    folds
                        .split("\r?\n".toRegex())
                        .map { """fold along ([xy])=(\d+)""".toRegex()
                            .matchEntire(it)!!
                            .destructured
                            .let { (a, p) -> makeFold(a.toFoldAxis(), p.toInt()) } } }
    }

    fun apply(folds: List<SetFold>) = folds.fold(this) { s, f -> f(s) }
}

typealias SetFold = (SetSheet) -> SetSheet

fun partOneSet(input: String) {
    println("Part 1 with set: ${SetSheet.valueOfAndFolds(input).let { (s, f) -> s.apply(f.subList(0,1)) }.dots.size}")
}

fun partTwoSet(input: String) {
    println("Part 2 with set:\n${SetSheet.valueOfAndFolds(input).let { (s, f) -> s.apply(f) }}")
}