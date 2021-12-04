package days

import java.io.File

fun main() {
    val file = File("src/main/kotlin/inputs/day04.txt")
    val example = """
        7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

        22 13 17 11  0
         8  2 23  4 24
        21  9 14 16  7
         6 10  3 18  5
         1 12 20 15 19
        
         3 15  0  2 22
         9 18 13 17  5
        19  8  7 25 23
        20 11 10 24  4
        14 21 16 12  6
        
        14 21 17 24  4
        10 16 15  9 19
        18  8 23 26 20
        22 11 13  6  5
         2  0 12  3  7
    """.trimIndent().split("\n\n")

    val game = BingoGame(file.readText().split("\r\n\r\n|\n\n".toRegex()))
    println("Scores (top line is part 1, bottom is part 2): ")
    solve(game)
}

fun solve(game: BingoGame) {
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