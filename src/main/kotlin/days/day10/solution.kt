package days.day10

import java.io.File


fun main() {
    val today = "src/main/kotlin/days/day10"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readLines()
            partOne(text)
            partTwo(text)
        }
}

private val openClosers = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')

fun partOne(input: List<String>) {
    val illegals = Array(input.size) { ' ' }

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
                illegals[i] = input[i][j]
                continue@main
            }
        }
        if (!stack.isEmpty()) illegals[i] = "\n".single()
    }

    val scores = mapOf("\n".single() to 0, ')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
    println("Part 1: ${illegals.sumOf { scores[it]!! }}")
}

fun partTwo(input: List<String>) {
    val incompleteLines = mutableListOf<Long>()

    val scores = mapOf(')' to 1, ']' to 2, '}' to 3, '>' to 4)

    main@for (i in input.indices) {
        val stack = ArrayDeque<Char>()
        for (j in input[i].indices) {
            val c = stack.firstOrNull()
            // opening new chunk
            if (input[i][j] in openClosers.keys)
                stack.addFirst(input[i][j])
            else if (openClosers[c] == input[i][j])
                stack.removeFirst()
            else continue@main
        }
        var score = 0L
        while (!stack.isEmpty()) {
            val nextChar = openClosers[stack.removeFirst()]
            score = score * 5 + scores[nextChar]!!
        }
        incompleteLines.add(score)
    }

    println("Part 2: ${incompleteLines.sorted()[incompleteLines.size / 2]}")
}