package util


inline fun <T> Iterable<T>.countEq(element: T): Int = this.count { it == element }

fun <A,B,C,D> Pair<A,B>.bimap(f: (A) -> C, g: (B) -> D) = Pair(f(first), g(second))

fun <A,B,C,D,E,F> Triple<A,B,C>.trimap(f: (A) -> D, g: (B) -> E, h: (C) -> F) = Triple(f(first), g(second), h(third))
