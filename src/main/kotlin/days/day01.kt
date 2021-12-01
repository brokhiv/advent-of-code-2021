package days

import java.io.File

fun main() {
    // Import and parsing
    val file = File("src/main/kotlin/inputs/day01.txt")
    val nums = file.readLines().map(String::toInt)

    // Part 1
    println(
        nums.countIncrease()
    )

    // Part 2
    println(nums
            .windowed(3)
            .map { it.sum() }.countIncrease()
    )
}

private fun List<Int>.countIncrease() = this
    .windowed(2)
    .count { it[0] < it[1] }