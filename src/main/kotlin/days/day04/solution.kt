package days.day04

import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day04"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = BingoGame(it.readText().split("\r\n\r\n|\n\n".toRegex()))
            solve(text)
            println()
            val winners = betterSolve(BetterBingoGame(it.readText().split("\r\n\r\n|\n\n".toRegex())))
            println("Part 1: ${winners.first()}")
            println("Part 2: ${winners.last()}")
            println()
        }
}

private fun List<List<Boolean>>.hasRow(): Boolean = this
    .map { it
        .reduce { acc, b -> acc && b}
    }
    .reduce { acc, r -> acc || r }

private fun List<List<Boolean>>.hasCol(): Boolean = this
    .mapIndexed { i, row -> row.mapIndexed { j, _ -> this[j][i] } }
    .hasRow()

typealias BingoCard = List<List<Int>>

data class BingoGame(val numbers: List<Int>, val cards: MutableList<BingoCard>) {
    val marked = MutableList(cards.size) { List(cards[0].size) { MutableList(cards[0].size) { false } } }

    constructor(texts: List<String>): this(
        texts[0]
            .split(",")
            .map(String::toInt),
        texts.drop(1)
            .map { it
                .split("\r\n|\n".toRegex())
                .map { row -> row
                    .removePrefix(" ")
                    .split(" +".toRegex())
                    .map { col -> col.toInt() }
                }
            } as MutableList<BingoCard>
    )

}

fun solve(game: BingoGame) {
    println("Scores (top line is part 1, bottom is part 2): ")

    for (n in game.numbers) {
        val lastWinners = mutableSetOf<Int>()
        game.marked.forEachIndexed { i, card -> game.marked[i]
            .forEachIndexed { j, row -> row
                .forEachIndexed { k, _ ->
                    if (n == game.cards[i][j][k]) row[k] = true
                }
            }
            if (card.hasRow() || card.hasCol()) {

                val score = game.cards[i]
                    .mapIndexed { j, it -> it
                        .filterIndexed { k, _ ->
                            !card[j][k]
                        }
                    }
                    .sumOf { it.sum() }
                println("${score * n}")
                lastWinners.add(i)
            }
        }
        game.cards.removeAll { game.cards.indexOf(it) in lastWinners }
        game.marked.removeAll { game.marked.indexOf(it) in lastWinners }
    }
}

data class BetterBingoGame(val numbers: List<Int>, val cards: MutableList<Array<Array<Field>>>) {
    data class Field(val num: Int, var marked: Boolean)

    constructor(texts: List<String>): this(
        texts[0]
            .split(",")
            .map(String::toInt),
        texts.drop(1)
            .map { it
                .split("\r\n|\n".toRegex())
                .map { row -> row
                    .removePrefix(" ")
                    .split(" +".toRegex())
                    .map { col -> Field(col.toInt(), false) }
                    .toTypedArray()
                }
                .toTypedArray()
            }
            .toMutableList()
    )

    fun hasRow(i: Int): Boolean = cards[i]
        .map { it.fold(true) { acc, b -> acc && b.marked} }
        .reduce { acc, r -> acc || r }

    fun hasCol(i: Int): Boolean = cards[i]
        .mapIndexed { r, row -> row.mapIndexed { c, _ -> cards[i][c][r] } }
        .map { it.fold(true) { acc, b -> acc && b.marked} }
        .reduce { acc, r -> acc || r }
}

fun betterSolve(game: BetterBingoGame): List<Int> {
    val winners = mutableListOf<Int>()
    for (n in game.numbers) {
        val lastWinners = mutableSetOf<Int>()
        game.cards.forEachIndexed { i, card -> game.cards[i]
            .forEachIndexed { j, row -> row
                .forEachIndexed { k, _ ->
                    if (n == game.cards[i][j][k].num) row[k].marked = true
                }
            }
            if (game.hasRow(i) || game.hasCol(i)) {
                val score = game.cards[i]
                    .mapIndexed { j, it -> it
                        .filterIndexed { k, _ ->
                            !card[j][k].marked
                        }
                    }
                    .sumOf { it.map { it.num }.sum() }
                winners.add(score * n)
                lastWinners.add(i)
            }
        }
        game.cards.removeAll { game.cards.indexOf(it) in lastWinners }
    }
    return winners
}
