package days.day21

import util.countDistinct
import java.io.File
import kotlin.math.max

fun main() {
    val today = "src/main/kotlin/days/day21"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readLines().map { it.substringAfter(": ").toInt() }
            partOne(text)
            partTwo(text)
        }
}

fun partOne(input: List<Int>) {
    val players = input.map { it to 0 }.toMutableList() // (pos, score)
    var turn = 0
    val die = (1..100).toList()
    var i = 0

    while (players.none { it.second >= 1000 }) {
        val roll = die[i % die.size] + die[(i + 1) % die.size] + die[(i + 2) % die.size]
        players[turn] = with (players[turn]) { ((first - 1 + roll) % 10 + 1).let { it to second + it } }
        turn = (turn + 1) % players.size
        i += 3
    }

    val (winner, loser) = players.partition { it.second >= 1000 }

    println("Part 1: ${loser.single().second * i}")
}

fun getWinner(player1: Pair<Int, Int>, player2: Pair<Int, Int>, turn: Int): Pair<Long, Long> {
    operator fun Pair<Long, Long>.plus(other: Pair<Long, Long>): Pair<Long, Long> =
        first + other.first to second + other.second

    if (player1.second >= 21) return 1L to 0L
    if (player2.second >= 21) return 0L to 1L

    val die = listOf(1,2,3)
        .let { die -> die.flatMap { die1 -> die.flatMap { die2 -> die.map { die3 -> die1 + die2 + die3 } } } }
    var universes = 0L to 0L
    for ((d, c) in die.countDistinct()) {
        universes += if (turn == 1) getWinner(
            ((player1.first - 1 + d) % 10 + 1).let { it to player1.second + it },
            player2,
            2
        ).let { it.first * c to it.second * c }
        else getWinner(
            player1,
            ((player2.first - 1 + d) % 10 + 1).let { it to player2.second + it },
            1
        ).let { it.first * c to it.second * c }
    }
    return universes
}

fun partTwo(input: List<Int>) {
    val (player1, player2) = input.map { it to 0 } // pos, score

    val universes = getWinner(player1, player2, 1)

    println("Part 2: ${max(universes.first, universes.second)}")
}