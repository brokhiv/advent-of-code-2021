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
