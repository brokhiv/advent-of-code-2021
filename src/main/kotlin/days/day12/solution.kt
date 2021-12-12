package days.day12

import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day12"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readLines()
            partOne(text)
            partTwo(text)
        }
}

open class Cave(val isBig: Boolean, val name: String) {
    object Start: Cave(false, "start")
    object End: Cave(false, "end")

    companion object {
        fun valueOf(text: String) = when (text) {
            "start" -> Start
            "end" -> End
            else -> Cave(text[0].isUpperCase(), text)
        }
    }

    override fun equals(other: Any?): Boolean = other is Cave && other.isBig == isBig && other.name == name

    override fun toString(): String = name
}

data class Route(val points: MutableList<Cave>) {
    fun isValid(maxDupes: Int = 0): Boolean = points
        .filterNot { it.isBig || it == Cave.Start || it == Cave.End }
        .let { it.distinctBy { it.name }.size + maxDupes >= it.size }
            && points.filter { it == Cave.Start }.size == 1
            && points.filter { it == Cave.End }.size <= 1

    fun last() = points.last()
}

fun List<Pair<Cave, Cave>>.getNext(from: Cave) = this.filter { it.first == from }.map(Pair<Cave, Cave>::second) +
        this.filter { it.second == from }.map(Pair<Cave, Cave>::first)

fun partOne(input: List<String>) {
    val caves = mutableSetOf<Cave>()
    val edges = buildList {
        for ((fst, snd) in input.map { it.split("-").map(Cave.Companion::valueOf) }) {
            caves.add(fst)
            caves.add(snd)
            add(fst to snd)
        }
    }
    // Add all edges from start
    var routes = edges.filter { (from, _) -> from == Cave.Start }.map { Route(mutableListOf(it.first, it.second)) } +
            edges.filter { (_, to) -> to == Cave.Start }.map { Route(mutableListOf(it.second, it.first)) }
    val finishedRoutes = mutableListOf<Route>()

    while (routes.any { it.last() != Cave.End }) {
        routes = routes
            .map { route ->
                edges
                    .getNext(route.last())
                    .map {
                        Route((route.points + it) as MutableList<Cave>)
                    }
            }
            .flatten()
            .filter { it.isValid() }
            .also { finishedRoutes.addAll(it.filter { it.last() == Cave.End }) }
            .filterNot { it.last() == Cave.End }
    }

    println("Part 1: ${finishedRoutes.size}")
}

fun partTwo(input: List<String>) {
    val caves = mutableSetOf<Cave>()
    val edges = buildList {
        for ((fst, snd) in input.map { it.split("-").map(Cave.Companion::valueOf) }) {
            caves.add(fst)
            caves.add(snd)
            add(fst to snd)
        }
    }
    // Add all edges
    var routes = edges
        .filter { (from, _) -> from == Cave.Start }
        .map { Route(mutableListOf(it.first, it.second)) } + edges
        .filter { (_, to) -> to == Cave.Start }
        .map { Route(mutableListOf(it.second, it.first))}

    val finishedRoutes = mutableListOf<Route>()

    while (routes.any { it.last() != Cave.End }) {
        routes = routes
            .map { route ->
                edges
                    .getNext(route.last())
                    .map {
                        Route((route.points + it) as MutableList<Cave>)
                    }
            }
            .flatten()
            .filter { it.isValid(1) }
            .also { finishedRoutes.addAll(it.filter { it.last() == Cave.End }) }
            .filterNot { it.last() == Cave.End }
    }

    println("Part 2: ${finishedRoutes.size}")
}