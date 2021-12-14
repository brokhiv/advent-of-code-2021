package days.day14

import util.countDistinct
import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day14"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readText()
            partOne(text)
            partTwo(text)
        }
}

fun partOne(input: String) {
    val (template, rules) = input
        .split("(\r?\n){2}".toRegex())
        .let { (t, r) -> t to r.split("\r?\n".toRegex()).associate { Pair(it[0], it[1]) to it[6] } }

    var polymer = template
    repeat(10) {
        val newElems = polymer.windowed(2).map { rules[it[0] to it[1]]!! }
        polymer = buildList {
            add(polymer[0])
            for (i in newElems.indices) {
                add(newElems[i])
                add(polymer[i + 1])
            }
        }.joinToString(separator = "")
    }

    val res = polymer
        .toList().distinct()
        .map { e -> polymer.count { it == e }.toLong() }
        .let { it.maxOrNull()!! - it.minOrNull()!! }
    println("Part 1: $res")
}

fun partTwo(input: String) {
    val (template, rules) = input
        .split("(\r?\n){2}".toRegex())
        .let { (t, r) -> t to r.split("\r?\n".toRegex()).associate { it.substring(0, 2) to it[6] } }

    var pairs = template.windowed(2).countDistinct().map { (p, c) -> p to c.toLong() }.toMap()

    repeat(40) {
        pairs = buildMap {
            pairs.forEach { (p, c) ->
                p.toList()
                    .let { (l, r) -> rules[p]!!.let { listOf(String(charArrayOf(l, it)), String(charArrayOf(it, r))) } }
                    .map { this[it] = (pairs[p] ?: 0L) + (this[it] ?: 0L) }
            }
        }
    }

    val res = (pairs.map { (p, c) -> p[0] to c } + Pair(template.last(), 1L)).merge { a, b -> a + b }

    println("Part 2: ${res.let{ it.maxOf { (_, v) -> v } - it.minOf { (_, v) -> v } }}")
}

private fun <K, V> Collection<Pair<K, V>>.merge(zip: (V, V) -> V): Map<K, V> =
    this.distinctBy { (k, _) -> k }.associate { (k, _) ->
        this.filter { (k2, _) -> k == k2 }
            .let { k to it.drop(1).fold(it.first().second) { acc, (_, v) -> zip(acc, v) } }
    }

