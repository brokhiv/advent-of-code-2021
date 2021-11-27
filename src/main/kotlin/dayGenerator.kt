import java.io.File

fun main() {
    for (i in 1..25) {
        val file = File("src/main/kotlin/days/day${i.toString().padStart(2, '0')}.kt")
        file.writeText("""
            package days
            
            fun main() {
                
            }
        """.trimIndent())
    }
}