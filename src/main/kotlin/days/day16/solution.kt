package days.day16

import java.io.File

fun main() {
    val today = "src/main/kotlin/days/day16"

    File(today)
        .listFiles { _, name -> name.endsWith(".txt") }
        .forEach {
            println("Input: ${it.name}")
            val text = it.readText().map {
                it.digitToInt(16)
                    .toString(2).padStart(4, '0')
            }
                .joinToString("")
            partOne(text)
            partTwo(text)
        }
}

sealed class Packet(val version: Int) {
    abstract fun getVersionSum(): Int
    abstract fun eval(): Long

    class Literal(version: Int, val value: Long): Packet(version) {
        override fun getVersionSum() = version
        override fun eval() = value
    }

    class Operator(version: Int, val opCode: Int, val subPackets: List<Packet>): Packet(version) {
        override fun getVersionSum() = version + subPackets.sumOf { it.getVersionSum() }
        override fun eval(): Long = when (opCode) {
            0 -> subPackets.sumOf { it.eval() }
            1 -> subPackets.fold(1) { acc, p -> acc * p.eval() }
            2 -> subPackets.minOf { it.eval() }
            3 -> subPackets.maxOf { it.eval() }
            5 -> subPackets.let { (p1, p2) -> if (p1.eval() > p2.eval()) 1 else 0 }
            6 -> subPackets.let { (p1, p2) -> if (p1.eval() < p2.eval()) 1 else 0 }
            7 -> subPackets.let { (p1, p2) -> if (p1.eval() == p2.eval()) 1 else 0 }
            else -> error("opCode $opCode not supported")
        }
    }
}

fun parse(text: String): Pair<Packet, String> {
    val version = text.take(3).toInt(2)
    return when (text.drop(3).take(3).toInt(2)) {
        4 -> {
            var remaining = text.drop(6)
            var num = ""
            while (remaining[0] == '1') {
                num += remaining.drop(1).take(4)
                remaining = remaining.drop(5)
            }
            num += remaining.drop(1).take(4)
            Packet.Literal(version, num.toLong(2)) to remaining.drop(5)
        }
        else ->
            when (text.drop(6).first()) {
                '0' -> {
                    val subPackets = mutableListOf<Packet>()
                    var remaining = text.drop(22).take(text.drop(7).take(15).toInt(2))
                    while (remaining.isNotEmpty()) {
                        val (p, r) = parse(remaining)
                        subPackets.add(p)
                        remaining = r
                    }
                    subPackets.toList() to remaining + text.drop(22 + text.drop(7).take(15).toInt(2))
                }
                '1' -> {
                    var remaining = text.drop(18)
                    buildList {
                        repeat(text.drop(7).take(11).toInt(2)) {
                            val (p, r) = parse(remaining)
                            add(p)
                            remaining = r
                        }
                    } to remaining
                }
                else -> error("Illegal character: ${text.drop(6).first()}")
        }.let { (list, txt) -> Packet.Operator(version, text.drop(3).take(3).toInt(2), list) to txt }
    }
}

fun partOne(input: String) {
    val (packet, _) = parse(input)

    println("Part 1: ${packet.getVersionSum()}")
}

fun partTwo(input: String) {
    val (packet, _) = parse(input)

    println("Part 2: ${packet.eval()}")
}