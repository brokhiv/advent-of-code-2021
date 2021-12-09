package days.day09

import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day09"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readLines()
            partOne(text)
            partTwo(text)
        }
}

fun partOne(input: List<String>) {
    val heightMap = input.map { it.map { it - '0' } }

    val lows = mutableListOf<Int>()
    for (i in heightMap.indices) {
        for (j in heightMap[i].indices) {
            if (i != heightMap.lastIndex && heightMap[i][j] >= heightMap[i + 1][j]) continue
            if (i != 0 && heightMap[i][j] >= heightMap[i - 1][j]) continue
            if (j != heightMap[i].lastIndex && heightMap[i][j] >= heightMap[i][j + 1]) continue
            if (j != 0 && heightMap[i][j] >= heightMap[i][j - 1]) continue

            lows.add(heightMap[i][j])
        }
    }

    println("Part 1: ${lows.sumOf { it + 1 }}")
}

fun partTwo(input: List<String>) {
    val heightMap = input.map { it.map { it - '0' } }

    val lows = buildList {
        for (i in heightMap.indices) {
            for (j in heightMap[i].indices) {
                if (i != heightMap.lastIndex && heightMap[i][j] >= heightMap[i + 1][j]) continue
                if (i != 0 && heightMap[i][j] >= heightMap[i - 1][j]) continue
                if (j != heightMap[i].lastIndex && heightMap[i][j] >= heightMap[i][j + 1]) continue
                if (j != 0 && heightMap[i][j] >= heightMap[i][j - 1]) continue

                add(i to j)
            }
        }
    }

    val basins = buildList(lows.size) {
        fun Pair<Int,Int>.getNeighbours() = buildList {
            val (i, j) = this@getNeighbours
            if (i != heightMap.lastIndex && heightMap[i][j] < heightMap[i + 1][j] && heightMap[i + 1][j] != 9)
                add(i + 1 to j)
            if (i != 0 && heightMap[i][j] < heightMap[i - 1][j] && heightMap[i - 1][j] != 9)
                add(i - 1 to j)
            if (j != heightMap[i].lastIndex && heightMap[i][j] < heightMap[i][j + 1] && heightMap[i][j + 1] != 9)
                add(i to j + 1)
            if (j != 0 && heightMap[i][j] < heightMap[i][j - 1] && heightMap[i][j - 1] != 9)
                add(i to j - 1)
        }

        tailrec fun Set<Pair<Int,Int>>.expand(): Set<Pair<Int,Int>> = buildSet {
            val newNeighbours = mutableSetOf<Pair<Int,Int>>()
            for (p in this@expand) {
                newNeighbours.addAll(p.getNeighbours().toSet() - this@expand)
            }
            if (newNeighbours.isEmpty()) {
                addAll(this@expand)
                return@buildSet
            } else {
                return (this@expand + newNeighbours).expand()
            }
        }

        for (l in lows) {
            add(setOf(l).expand())
        }
    }

    println("Part 2: ${basins.sortedByDescending { it.size }.take(3).fold(1) { acc, set -> acc * set.size }}")
}