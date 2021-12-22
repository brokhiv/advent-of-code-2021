package days.day22

import util.Quad
import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {

    val today = "src/main/kotlin/days/day22"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readLines().map(Instruction.Companion::valueOf)
            partOne(text)
            partTwo(text)
        }
}

data class Instruction(val type: Int, val rx: IntRange, val ry: IntRange, val rz: IntRange, val corrector: Long = 0L) {
    val volume = if (rx.isEmpty() || ry.isEmpty() || rz.isEmpty()) 0L else type *
            (rx.last - rx.first + 1L) *
            (ry.last - ry.first + 1L) *
            (rz.last - rz.first + 1L) +
            corrector

    fun withCorrector(_corrector: Long) = Instruction(type, rx, ry, rz, _corrector)

    companion object {
        fun valueOf(text: String): Instruction =
            """(on|off) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)"""
                .toRegex()
                .matchEntire(text)
                ?.destructured
                ?.let { (t, xMin, xMax, yMin, yMax, zMin, zMax) -> Instruction(
                    if (t == "on") 1 else 0,
                    xMin.toInt()..xMax.toInt(),
                    yMin.toInt()..yMax.toInt(),
                    zMin.toInt()..zMax.toInt()
                    )
                }
                ?: error("Parsing failed")
    }
}

fun partOne(input: List<Instruction>) {
    val cubes = List(101) { List(101) { BooleanArray(101) } } // indices offset by 50

    for ((t, xRange, yRange, zRange) in input.map { (t, xRange, yRange, zRange) ->
        Instruction(t,
            max(xRange.first, -50)..min(xRange.last, 50),
            max(yRange.first, -50)..min(yRange.last, 50),
            max(zRange.first, -50)..min(zRange.last, 50))
        }
    ) {
        for (x in xRange) {
            for (y in yRange) {
                for (z in zRange) {
                    cubes[x + 50][y + 50][z + 50] = t == 1
                }
            }
        }
    }

    println("Part 1: ${cubes.sumOf { it.sumOf { it.count { it } } }}")
}

fun overlaps(instrs: List<Instruction>, inverted: Boolean = false): List<Instruction> = instrs.mapIndexed { i, instr ->
        var overlap = buildList {
            for (j in 0 until i) {
                add(
                    Instruction(
                        -instrs[j].type,
                        max(instr.rx.first, instrs[j].rx.first)..min(instr.rx.last, instrs[j].rx.last),
                        max(instr.ry.first, instrs[j].ry.first)..min(instr.ry.last, instrs[j].ry.last),
                        max(instr.rz.first, instrs[j].rz.first)..min(instr.rz.last, instrs[j].rz.last)
                    )
                )
            }
        }
        overlap = overlap.filterNot { it.rx.isEmpty() || it.ry.isEmpty() || it.rz.isEmpty() }
        val corrector = if (overlap.isEmpty()) (if (inverted) 1 else -1) * overlap.reduced() else overlaps(overlap, !inverted).reduced()
        instr.withCorrector(corrector)
    }

private fun List<Instruction>.reduced() = this
    .sumOf { it.volume }


fun partTwo(input: List<Instruction>) {
    val overlaps = overlaps(input)

    val count = overlaps.reduced()

    println("Part 2: $count")
}