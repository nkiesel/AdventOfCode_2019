import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day14 {
    private val sample1 = """
        10 ORE => 10 A
        1 ORE => 1 B
        7 A, 1 B => 1 C
        7 A, 1 C => 1 D
        7 A, 1 D => 1 E
        7 A, 1 E => 1 FUEL
    """.trimIndent().lines()

    private val sample2 = """
        9 ORE => 2 A
        8 ORE => 3 B
        7 ORE => 5 C
        3 A, 4 B => 1 AB
        5 B, 7 C => 1 BC
        4 C, 1 A => 1 CA
        2 AB, 3 BC, 4 CA => 1 FUEL
    """.trimIndent().lines()

    data class Chem(val name: String, val quantity: Int)

    data class Reaction(val quantity: Int, val input: List<Chem>)

    private fun parse(input: List<String>): List<List<Chem>> =
        input.map { l -> l.split(", ", " => ").map { it.split(" ").let { Chem(it[1], it[0].toInt()) } } }

    private fun one(input: List<String>): Int {
        val reactions = parse(input).associate {
            val out = it.last()
            out.name to Reaction(out.quantity, it.dropLast(1))
        }
        var ores = 0
        val quantities = reactions.keys.associateWith { 0 }.toMutableMap()
        quantities["ORE"] = 0
        fun produce(c: Chem) {
            if (c.name == "ORE") {

            }
            val r = reactions[c.name]!!
            if (c.name == "ORE") {
                ores += r.quantity
            } else {
                r.input.forEach { i ->
                    val n = i.name
                    if (n == "ORE") {
                        ores += r.quantity
                    } else {
                        val p = reactions[n]!!
                        repeat(Math.ceilDiv(i.quantity, p.quantity)) { produce(i) }
                    }
                }
            }
        }
        produce(Chem("FUEL", 1))
        return ores
    }

    private fun two(input: List<String>): Int {
        return 0
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 31
//        one(sample2) shouldBe 165
//        one(input) shouldBe 0
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(sample) shouldBe 0
//        two(input) shouldBe 0
    }
}
