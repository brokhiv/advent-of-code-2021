package days.day01

import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day01"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readLines().map(String::toInt)
            partOne(text)
            partTwo(text)
        }
}

fun List<Int>.countIncrease() = this
    .windowed(2)
    .count { it[0] < it[1] }

fun partOne(nums: List<Int>) {
    println(
        nums.countIncrease()
    )
}

fun partTwo(nums: List<Int>) {
    println(nums
        .windowed(3)
        .map { it.sum() }
        .countIncrease()
    )
}