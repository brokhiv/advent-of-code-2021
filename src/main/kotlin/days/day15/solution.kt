package days.day15

import java.io.File
import java.util.*

fun main() {
    val today = "src/main/kotlin/days/day15"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readLines()
            partOne(text)
            partTwo(text)
            println()
        }
}

data class Path(
    val grid: List<String>,
    val points: List<Pair<Int, Int>>
) : Comparable<Path> {
    enum class Direction(val dI: Int, val dJ: Int) {
        UP(-1, 0),
        DOWN(1, 0),
        LEFT(0, -1),
        RIGHT(0, 1);

        operator fun not() = when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }

        companion object {
            fun derive(now: Pair<Int, Int>, prev: Pair<Int, Int>) =
                when (prev.first - now.first to prev.second - now.second) {
                    -1 to 0 -> UP
                    1 to 0 -> DOWN
                    0 to -1 -> LEFT
                    0 to 1 -> RIGHT
                    else -> error("Difference ${now.first - prev.first to now.second - prev.second} illegal")
                }

        }
    }

    val i = points.last().first
    val j = points.last().second

    val distance = grid.lastIndex - i + grid[0].lastIndex - j

    val cost = if (points.size < 2) 0 else points.drop(1).sumOf { (pI, pJ) -> grid[pI][pJ].digitToInt() }

    override fun compareTo(other: Path): Int = (this.cost + this.distance) - (other.cost + other.distance)

    fun Pair<Int, Int>.expand(lastDir: Direction): List<Path> = buildList {
        Direction.values().filter { it != !lastDir && i + it.dI in grid.indices && j + it.dJ in grid[i].indices }
            .forEach { add(Path(grid, points + (i + it.dI to j + it.dJ))) }
    }.filter { it.points.let { it.distinct().size == it.size } }

    fun isFinished() = i == grid.lastIndex && j == grid[i].lastIndex
}

fun partOne(input: List<String>) {
    fun Pair<Int, Int>.expand(lastDir: Path.Direction): List<Path> = buildList {
        Path.Direction.values().filter { it != lastDir && this@expand.first + it.dI in input.indices && this@expand.second + it.dJ in input[this@expand.first].indices }
            .forEach { add(Path(input, listOf(this@expand, (this@expand.first + it.dI to this@expand.second + it.dJ)))) }
    }.filter { it.points.let { it.distinct().size == it.size } }

    val bestPaths = mutableMapOf((0 to 0) to Path(input, listOf(0 to 0)))
    val priorityQueue = PriorityQueue<Pair<Int, Int>>(1, compareBy { with (bestPaths[it]!!) { cost + distance } })
        .apply { add(0 to 0) }
    val bestPath = run {
        while (!priorityQueue.isEmpty()) {
            val point = priorityQueue.remove()
            if (point == input.lastIndex to input[0].lastIndex) return@run bestPaths[point]

            val next = point
                .expand(bestPaths[point]
                    ?.let { if (it.points.size >= 2) Path.Direction.derive(point, it.points[it.points.lastIndex - 1]) else null } ?: Path.Direction.UP)
            next.forEach {
                if (bestPaths[point]!!.cost + input[it.points.last().first][it.points.last().second].digitToInt() < (bestPaths[it.points.last()]?.cost ?: Int.MAX_VALUE)) {
                    bestPaths[it.points.last()] = Path(input, bestPaths[point]!!.points + it.points.last())
                    if (it.points.last() !in priorityQueue) {
                        priorityQueue.add(it.points.last())
                    }
                }
            }
        }
        null
    }

    println("Part 1: ${bestPath?.cost}")
}

fun partTwo(input: List<String>) {
    val grid = buildList {
        for (iO in 0..4) {
            for (iI in input.indices) {
                add(buildList {
                    for (jO in 0..4) {
                        for (jI in input[iI].indices) {
                            add((input[iI][jI].digitToInt() + iO + jO).let { if (it > 9) it - 9 else it } )
                        }
                    }
                }.joinToString(""))
            }
        }
    }

    fun Pair<Int, Int>.expand(lastDir: Path.Direction): List<Path> = buildList {
    Path.Direction.values().filter { it != lastDir && this@expand.first + it.dI in grid.indices && this@expand.second + it.dJ in grid[this@expand.first].indices }
        .forEach { add(Path(grid, listOf(this@expand, (this@expand.first + it.dI to this@expand.second + it.dJ)))) }
}.filter { it.points.let { it.distinct().size == it.size } }

    val bestPaths = mutableMapOf((0 to 0) to Path(grid, listOf(0 to 0)))
    val priorityQueue = PriorityQueue<Pair<Int, Int>>(1, compareBy { with (bestPaths[it]!!) { cost + distance } })
        .apply { add(0 to 0) }
    val bestPath = run {
        while (!priorityQueue.isEmpty()) {
            val point = priorityQueue.remove()
            if (point == grid.lastIndex to grid[0].lastIndex) return@run bestPaths[point]

            val next = point
                .expand(bestPaths[point]
                    ?.let { if (it.points.size >= 2) Path.Direction.derive(point, it.points[it.points.lastIndex - 1]) else null } ?: Path.Direction.UP)
            next.forEach {
                if (bestPaths[point]!!.cost + grid[it.points.last().first][it.points.last().second].digitToInt() < (bestPaths[it.points.last()]?.cost ?: Int.MAX_VALUE)) {
                    bestPaths[it.points.last()] = Path(grid, bestPaths[point]!!.points + it.points.last())
                    if (it.points.last() !in priorityQueue) {
                        priorityQueue.add(it.points.last())
                    }
                }
            }
        }
        null
    }

    println("Part 2: ${bestPath?.cost}")
}