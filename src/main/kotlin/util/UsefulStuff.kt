package util

import java.io.Serializable

fun <T> Iterable<T>.countDistinct(): Map<T, Int> = this.distinct().associateWith { this.countEq(it) }

inline fun <T> Iterable<T>.countEq(element: T): Int = this.count { it == element }

inline fun String.countEq(element: Char): Int = this.count { it == element }

inline fun <T, U> Iterable<T>.nMap(fs: List<(T) -> U>): List<U> = this.zip(fs) { x, f -> f(x) }

fun <A,B,C,D> Pair<A,B>.bimap(f: (A) -> C, g: (B) -> D) = Pair(f(first), g(second))

fun <A,B,C,D,E,F> Triple<A,B,C>.trimap(f: (A) -> D, g: (B) -> E, h: (C) -> F) = Triple(f(first), g(second), h(third))

data class Quad<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
): Serializable {
    override fun toString() = "($first, $second, $third, $fourth)"
}