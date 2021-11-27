package util

/**
 * Utility class to parse strings to [T] with the given delimiter [patterns].
 */
class Parser<T>(private vararg val patterns: Regex) {

    /**
     * Transforms the [text] into an instance of [T] using the [builder].
     */
    fun parse(text: String, builder: (List<String>) -> T): T =
        buildList {
            var remainder = text
            patterns.forEach { pat ->
                // Split string into the part before the delimiter (which is discarded) and the rest
                val (comp, rest) = remainder.split(pat, 2)
                add(comp)
                remainder = rest
            }
            add(remainder)
        }.let(builder)

    /**
       Displays the parameter slots with the delimiter patterns in between
     */
    override fun toString(): String {
        var res = ""
        var idx = 0
        patterns.forEach {
            res += "{$idx}$it"
            idx++
        }
        res += "{$idx}"
        return res
    }
}
