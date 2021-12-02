package days

import java.io.File

fun main() {
    val file = File("src/main/kotlin/inputs/day02.txt")

    val directions = file.readLines().map { it.split(" ")}

    partOne(directions)
    partTwo(directions)
}

fun partTwo(directions: List<List<String>>) {
    var depth = 0
    var horizontal = 0
    var aim = 0

    for (dir in directions) {
        when (dir[0]) {
            "up" -> {
                aim -= dir[1].toInt()
            }
            "down" -> {
                aim += dir[1].toInt()
            }
            "forward" -> {
                horizontal += dir[1].toInt()
                depth += aim * dir[1].toInt()
            }
        }
    }
    println("Part 2: ${horizontal * depth}")
}

fun partOne(directions: List<List<String>>) {
    var depth = 0
    var horizontal = 0

    for (dir in directions) {
        when (dir[0]) {
            "up" -> depth -= dir[1].toInt()
            "down" -> depth += dir[1].toInt()
            "forward" -> horizontal += dir[1].toInt()
        }
    }
    println("Part 1: ${horizontal * depth}")
}