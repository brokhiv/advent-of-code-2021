package days.day23

import java.io.File
import java.util.PriorityQueue
import kotlin.math.abs

fun main() {
    val today = "src/main/kotlin/days/day23"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val part1 = it.readText().toState()
            partOne(part1)
            val part2 = it.readLines()
                .let { it.take(3) + listOf("  #D#C#B#A#\n", "  #D#B#A#C#") + it.drop(3) }
                .joinToString("")
                .toState()
            partTwo(part2)
        }
}

enum class Amphipod(val energy: Int) {
    A(1), B(10), C(100), D(1000);

    fun rooms(part: Int = 1) = when (this) {
        A -> if (part == 1) listOf(11, 15) else listOf(11, 15, 19, 23)
        B -> if (part == 1) listOf(12, 16) else listOf(12, 16, 20, 24)
        C -> if (part == 1) listOf(13, 17) else listOf(13, 17, 21, 25)
        D -> if (part == 1) listOf(14, 18) else listOf(14, 18, 22, 26)
    }

    companion object {
        fun valueOfOrNull(value: String): Amphipod? = if (value == ".") null else valueOf(value)
    }
}

/** Index to spot layout:
 * 00 01 02 03 04 05 06 07 08 09 10
 *       11    12    13    14
 *       15    16    17    18
 *      (19    20    21    22)
 *      (23    24    25    26)
 */
data class State(private val spots: Array<Amphipod?>) {
    operator fun get(i: Int) = spots[i]

    fun dist(start: Int, end: Int, part: Int = 1) =
        if (start == end) 0
        else if (start in Amphipod.values().flatMap { it.rooms(part) }) {
            if (end in Amphipod.values().flatMap { it.rooms(part) }) {
                if (abs(start - end) == 4) 1 else
                    ((start - 7) / 4) + ((end - 7) / 4) + 2 * abs(((start + 1) % 4) - ((end + 1) % 4)) + 1
            } else {
                ((start - 7) / 4) + abs(end - 2 * ((start + 1) % 4 + 1))
            }
        } else if (end in Amphipod.values().flatMap { it.rooms(part) }) {
            ((end - 7) / 4) + abs(start - 2 * ((end + 1) % 4 + 1))
        } else {
            abs(start - end)
        }

    fun move(start: Int, end: Int) = State(spots.copyOf().apply { this[end] = this[start]; this[start] = null })

    fun expand(part: Int = 1): Map<State, Int> = buildMap {
        val current = this@State
        for (i in 0..10) {
            val a = current[i] ?: continue
            if (a.rooms(part).all { current[it]?.equals(a) != false }) {
                // room is free, try to move in
                if ((2 * ((a.rooms(part).first() + 1) % 4 + 1))
                        .let { if (it < i) it until i else (i + 1)..it }
                        .let { current.spots.sliceArray(it).any { it != null } }
                ) continue
                // a can go into its room, go as deep as possible
                a.rooms(part).last { current[it] == null }.let { put(current.move(i, it), a.energy * current.dist(i, it, part)) }
            } // No extra move possible
        }
        room@for (i in 11..(if (part == 1) 18 else 26)) {
            val a = current[i] ?: continue
            val row = (i - 7) / 4;
            if ((i in a.rooms(part).take(row) &&  a.rooms(part).drop(row).any { current[it] != a }) ||
                (i !in a.rooms(part) && !(i > 14 && current[i - 4] != null))
            ) {
                // a still has to move and can now
                for (j in (i - 4) downTo 11 step 4) { if (current[j] != null ) continue@room }
                val (left, right) = listOf(0,1,3,5,7,9,10).partition { it < 2 * ((i + 1) % 4 + 1) }
                for (j in left.reversed()
                ) {
                    if (current[j] != null) break // way is blocked
                    put(current.move(i, j), a.energy * current.dist(i, j, part))
                }
                for (j in right) {
                    if (current[j] != null) break // way is blocked
                    put(current.move(i, j), a.energy * current.dist(i, j, part))
                }
            }
        }
    }

    fun estimate(part: Int = 1) = spots.foldIndexed(0) { i, acc, a ->
        acc + if (a == null) 0 else a.energy * (a.rooms(part).minOf { dist(i, it, part) })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as State

        if (!spots.contentEquals(other.spots)) return false

        return true
    }

    fun upper() = spots.sliceArray(0..10)

    override fun toString() = if (spots.size == 19) """
        #############
        #${spots.sliceArray(0..10).joinToString("") { it?.toString() ?: "." }}#
        ###${spots[11] ?: "."}#${spots[12] ?: "."}#${spots[13] ?: "."}#${spots[14] ?: "."}###
          #${spots[15] ?: "."}#${spots[16] ?: "."}#${spots[17] ?: "."}#${spots[18] ?: "."}#
          #########
    """.trimIndent()
    else """
        #############
        #${spots.sliceArray(0..10).joinToString("") { it?.toString() ?: "." }}#
        ###${spots[11] ?: "."}#${spots[12] ?: "."}#${spots[13] ?: "."}#${spots[14] ?: "."}###
          #${spots[15] ?: "."}#${spots[16] ?: "."}#${spots[17] ?: "."}#${spots[18] ?: "."}#
          #${spots[19] ?: "."}#${spots[20] ?: "."}#${spots[21] ?: "."}#${spots[22] ?: "."}#
          #${spots[23] ?: "."}#${spots[24] ?: "."}#${spots[25] ?: "."}#${spots[26] ?: "."}#
          #########
    """.trimIndent()
}

fun String.toState() = State(this
    .filterNot { it in " #\r\n" }
    .map { Amphipod.valueOfOrNull(it.toString()) }
    .toTypedArray()
)

fun minCost(start: State, part: Int = 1): Int {
    val bestPaths = mutableMapOf(start to (start to 0))
    val queue = PriorityQueue<State>(compareBy { bestPaths[it]!!.second /*+ it.estimate()*/ }).apply { add(start) }

    return run {
        while (!queue.isEmpty()) {
            val state = queue.remove()
            if (state.estimate(part) == 0)
                return bestPaths[state]!!.second
            val nextOptions = state.expand(part)
            if (nextOptions.isEmpty() && queue.isEmpty()) {
                state.expand(part)
            }
            nextOptions.forEach { (next, cost) ->
                if (bestPaths[state]!!.second + cost < (bestPaths[next]?.second ?: Int.MAX_VALUE)) {
                    bestPaths[next] = state to bestPaths[state]!!.second + cost
                    if (next !in queue) queue.add(next)
                }
            }
        }
        -1
    }
}

fun partOne(input: State) {
    println("Part 1: ${minCost(input)}")
}

fun partTwo(input: State) {
    println("Part 2: ${minCost(input, 2)}")
}