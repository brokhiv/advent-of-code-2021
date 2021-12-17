package days.day17

import java.io.File
import kotlin.math.max

fun main() {
    val today = "src/main/kotlin/days/day17"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readText()
            partOne(text)
            partTwo(text)
        }
}

typealias TargetArea = Pair<IntRange, IntRange>

data class Probe(val xPos: Int = 0, val yPos: Int = 0, val xVel: Int, val yVel: Int) {

    fun step() = Probe(xPos + xVel, yPos + yVel, max(0, xVel - 1), yVel - 1)

    fun inTargetArea(area: TargetArea) = area.let { (xRange, yRange) -> xPos in xRange && yPos in yRange }
}

fun partOne(input: String) {
    val (xRange, yRange) = "target area: x=(\\d+)..(\\d+), y=(-?\\d+)..(-?\\d+)"
        .toRegex()
        .matchEntire(input)!!
        .destructured
        .let { (xMin, xMax, yMin, yMax) -> xMin.toInt()..xMax.toInt() to yMin.toInt()..yMax.toInt() }

    var best = yRange.first
    for (y in yRange.first..-yRange.first) {
        xy@for (x in 0..xRange.last) {
            var last = Probe(xVel = x, yVel = y)
            var maxY = yRange.first
            while (!last.inTargetArea(TargetArea(xRange, yRange))) {
                maxY = max(maxY, last.yPos)
                last = last.step()
                if (last.yPos < yRange.first) {
                    continue@xy
                }
            }
            best = max(best, maxY)
        }
    }

    println("Part 1: $best")
}

fun partTwo(input: String) {
    val (xRange, yRange) = "target area: x=(\\d+)..(\\d+), y=(-?\\d+)..(-?\\d+)"
        .toRegex()
        .matchEntire(input)!!
        .destructured
        .let { (xMin, xMax, yMin, yMax) -> xMin.toInt()..xMax.toInt() to yMin.toInt()..yMax.toInt() }

    var count = 0
    for (y in yRange.first..-yRange.first) {
        xy@for (x in 0..xRange.last) {
            var last = Probe(xVel = x, yVel = y)
            while (!last.inTargetArea(TargetArea(xRange, yRange))) {
                last = last.step()
                if (last.yPos < yRange.first) {
                    continue@xy
                }
            }
            count++
        }
    }

    println("Part 2: $count")
}