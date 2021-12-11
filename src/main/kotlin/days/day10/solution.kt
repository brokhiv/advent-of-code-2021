package days.day10

import java.io.File


fun main() {
    val today = "src/main/kotlin/days/day10"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readLines()
            val (illegals, incomplete) = process(text)
            partOne(illegals)
            partTwo(incomplete)
        }
}

private val openClosers = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
private val scoresIllegal = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
private val scoresIncomplete = mapOf(')' to 1, ']' to 2, '}' to 3, '>' to 4)

fun process(input: List<String>): Pair<List<Char>, List<Long>> {
    val (illegal, incomplete) = mutableListOf<Char>() to mutableListOf<Long>()

    main@for (i in input.indices) {
        val stack = ArrayDeque<Char>()
        for (j in input[i].indices) {
            val c = stack.firstOrNull()
            // opening new chunk
            if (input[i][j] in openClosers.keys)
                stack.addFirst(input[i][j])
            else if (openClosers[c] == input[i][j])
                stack.removeFirst()
            else {
                illegal.add(input[i][j])
                continue@main
            }
        }
        if (!stack.isEmpty()) {
            var score = 0L
            while (!stack.isEmpty()) {
                val nextChar = openClosers[stack.removeFirst()]
                score = score * 5 + scoresIncomplete[nextChar]!!
            }
            incomplete.add(score)
        }
    }

    return illegal.toList() to incomplete.toList()
}

fun partOne(illegals: List<Char>) {
    println("Part 1: ${illegals.sumOf { scoresIllegal[it]!! }}")
}

fun partTwo(incompletes: List<Long>) {
    println("Part 2: ${incompletes.sorted()[incompletes.size / 2]}")
}