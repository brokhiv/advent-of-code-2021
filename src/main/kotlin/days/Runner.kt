package days

import java.time.LocalDate
// all the main functions have to be imported with an alias
import days.day01.main as day01
import days.day02.main as day02
import days.day03.main as day03
import days.day04.main as day04
import days.day05.main as day05
import days.day06.main as day06
import days.day07.main as day07
import days.day08.main as day08
import days.day09.main as day09
import days.day10.main as day10
import days.day11.main as day11
import days.day12.main as day12
import days.day13.main as day13
import days.day14.main as day14
import days.day15.main as day15
import days.day16.main as day16
import days.day17.main as day17
import days.day18.main as day18
import days.day19.main as day19
import days.day20.main as day20
import days.day21.main as day21
import days.day22.main as day22
import days.day23.main as day23
import days.day24.main as day24
import days.day25.main as day25

// Store all main functions in a list
val solutions = listOf(::day01, ::day02, ::day03, ::day04, ::day05, ::day06, ::day07, ::day08, ::day09, ::day10, ::day11, ::day12, ::day13, ::day14, ::day15, ::day16, ::day17, ::day18, ::day19, ::day20, ::day21, ::day22, ::day23, ::day24, ::day25)

fun main(args: Array<String>) {
    // Either get the given day or do the current day
    val day = if (args.isEmpty()) LocalDate.now().dayOfMonth else args[0].toInt()
    // Retrieve the main function and run it!
    solutions[day - 1]()
}