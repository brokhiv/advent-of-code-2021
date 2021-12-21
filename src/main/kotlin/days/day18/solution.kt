package days.day18

import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day18"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            partOne(it.readLines().map(SnailfishNumber::valueOf))
            partTwo(it.readLines().map(SnailfishNumber::valueOf))
        }
}

sealed class SnailfishNumber {
    data class Regular(override var value: Int = 0): SnailfishNumber() {
        fun split() = Pair(Regular(value / 2), Regular((value + 1) / 2))

        override fun getLeftmost() = this
        override fun getRightmost() = this

        override fun magnitude() = value
        override fun reduce() = this
        override fun toString() = value.toString()
    }

    class Pair(val fst: SnailfishNumber, val snd: SnailfishNumber): SnailfishNumber() {
        override fun magnitude() = 3 * fst.magnitude() + 2 * snd.magnitude()

        override val value: Int
            get() = error("This is a pair, not a Regular")

        private fun getFirstNested(depth: Int = 4): Pair? {
            if (depth == 1)
                return if (fst is Pair) fst else if (snd is Pair) snd else null

            return (if (fst is Pair) fst.getFirstNested(depth - 1) else null)
                ?: if (snd is Pair) snd.getFirstNested(depth - 1) else null
        }

        fun getFirstBig(): Regular? = when {
            fst is Regular && fst.value >= 10 -> fst
            fst is Pair -> fst.getFirstBig()
                ?: if (snd is Regular && snd.value >= 10) snd
                else if (snd is Pair) snd.getFirstBig()
                else null
            snd is Regular && snd.value >= 10 -> snd
            snd is Pair -> snd.getFirstBig()
            else -> null
        }

        override fun getLeftmost(): Regular = when (fst) {
            is Regular -> fst
            is Pair -> fst.getLeftmost()
        }

        override fun getRightmost(): Regular = when (snd) {
            is Regular -> snd
            is Pair -> snd.getRightmost()
        }

        private fun getLeftNeighbor(path: List<Pair>): Regular? {
            var lastChild = this
            var lastParent: Pair
            for (i in path.lastIndex downTo 0) {
                lastParent = path[i]
                if (path[i].snd == lastChild) {
                    return lastParent.fst.getRightmost()
                }
                lastChild = path[i]
            }
            return null
        }

        private fun getRightNeighbor(path: List<Pair>): Regular? {
            var lastChild = this
            var lastParent: Pair
            for (i in path.lastIndex downTo 0) {
                lastParent = path[i]
                if (path[i].fst == lastChild) {
                    return lastParent.snd.getLeftmost()
                }
                lastChild = path[i]
            }
            return null
        }

        fun getPath(pair: Pair): List<Pair> {
            if (fst == pair) return listOf(this, fst as Pair)
            val res: List<Pair> = if (fst is Pair) listOf(this) + fst.getPath(pair) else listOf()
            return if (res.isNotEmpty()) res
            else if (snd == pair) listOf(this, snd as Pair)
            else if (snd is Pair) listOf(this) + snd.getPath(pair)
            else listOf()
        }

        fun explode(path: List<Pair> = listOf(this)): Pair? {
            if (path.size >= 4 && (fst is Pair || snd is Pair)) {
                if (fst is Pair) { // this one!
                    fst.getLeftNeighbor(path)?.apply { value += fst.fst.value }
                    snd.getLeftmost().value += fst.snd.value
                    return Pair(Regular(), snd)
                } else if (snd is Pair) {
                    snd.getRightNeighbor(path)?.apply { value += snd.snd.value }
                    fst.getRightmost().value += snd.fst.value
                    return Pair(fst, Regular())
                }
            }
            if (fst is Pair) {
                val exploded = fst.explode(path + fst)
                if (exploded != null) return Pair(exploded, snd)
            }
            if (snd is Pair) {
                return snd.explode(path + snd)?.let { Pair(fst, it) }
            }
            return null
        }

        fun split(): Pair = when {
            fst is Regular && fst.value >= 10 -> Pair(fst.split(), snd)
            fst is Pair && fst.getFirstBig() != null -> Pair(fst.split(), snd)
            snd is Regular && snd.value >= 10 -> Pair(fst, snd.split())
            snd is Pair && snd.getFirstBig() != null -> Pair(fst, snd.split())
            else -> error("Not splittable")
        }

        override fun reduce(): SnailfishNumber {
            var last = this
            while ((last.getFirstNested() ?: last.getFirstBig()) != null) {
                last = last.explode() ?: if (last.getFirstBig() != null) last.split() else last
            }
            return last
        }

        override fun toString() = "[$fst,$snd]"
    }

    abstract override fun toString(): String
    abstract val value: Int
    abstract fun magnitude(): Int
    abstract fun reduce(): SnailfishNumber
    operator fun plus(other: SnailfishNumber) = Pair(this, other).reduce()

    companion object {
        private val subPattern = """\d+""".toRegex()

        private fun parse(text: String): kotlin.Pair<SnailfishNumber, String> =
            if (subPattern.find(text)?.range?.first == 0) {
                subPattern.find(text)!!.let { Regular(it.value.toInt()) to text.drop(it.range.last + 1) }
            } else {
                val (fst, rest) = parse(text.drop(1))
                val (snd, last) = parse(rest.drop(1))
                Pair(fst, snd) to last.drop(1)
            }

        fun valueOf(text: String): SnailfishNumber = parse(text).first

    }

    protected abstract fun getLeftmost(): Regular
    protected abstract fun getRightmost(): Regular
    fun deepCopy(): SnailfishNumber = valueOf(this.toString())
}

fun partOne(input: List<SnailfishNumber>) {
    println("Part 1: ${input.reduce { acc, n -> acc + n }.let { it to it.magnitude() }}")
}

fun partTwo(input: List<SnailfishNumber>) {
    val sums = input.flatMap { x ->
        input.map { y -> (x.deepCopy() + y.deepCopy()).magnitude() }
    }

    println("Part 2: ${sums.maxOf { it } }")
}