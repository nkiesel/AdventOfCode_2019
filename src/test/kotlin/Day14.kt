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

    private val sample3 = """
        157 ORE => 5 NZVS
        165 ORE => 6 DCFZ
        44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL
        12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ
        179 ORE => 7 PSHF
        177 ORE => 5 HKGWZ
        7 DCFZ, 7 PSHF => 2 XJWVT
        165 ORE => 2 GPVTF
        3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT
    """.trimIndent().lines()

    private val sample4 = """
        171 ORE => 8 CNZTR
        7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL
        114 ORE => 4 BHXH
        14 VRPVC => 6 BMBT
        6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL
        6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT
        15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW
        13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW
        5 BMBT => 4 WPTQ
        189 ORE => 9 KTJDG
        1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP
        12 VRPVC, 27 CNZTR => 2 XDBXC
        15 KTJDG, 12 BHXH => 5 XCVML
        3 BHXH, 2 VRPVC => 7 MZWV
        121 ORE => 7 VRPVC
        7 XCVML => 6 RJRHP
        5 BHXH, 4 VRPVC => 5 LTCX
    """.trimIndent().lines()

    data class Chem(val name: String, val quantity: Long)

    data class Reaction(val quantity: Long, val input: List<Chem>)

    private fun parse(input: List<String>): List<List<Chem>> =
        input.map { l -> l.split(", ", " => ").map { it.split(" ").let { Chem(it[1], it[0].toLong()) } } }

    private fun one(input: List<String>): Int {
        val reactions = parse(input).associate {
            val out = it.last()
            out.name to Reaction(out.quantity, it.dropLast(1))
        }
        var ores = 0L
        val quantities = reactions.keys.associateWith { 0L }.toMutableMap()
        quantities["ORE"] = 0
        fun produce(c: Chem) {
            val p: Long
            if (c.name == "ORE") {
                ores += c.quantity
                p = c.quantity
            } else {
                val r = reactions[c.name]!!
                r.input.forEach { i ->
                    while (quantities[i.name]!! < i.quantity) {
                        produce(i)
                    }
                    quantities[i.name] = quantities[i.name]!! - i.quantity
                }
                p = r.quantity
            }
            quantities[c.name] = quantities[c.name]!! + p
        }
        produce(Chem("FUEL", 1))
        return ores.toInt()
    }

    private fun two(input: List<String>): Long {
        val reactions = parse(input).associate {
            val out = it.last()
            out.name to Reaction(out.quantity, it.dropLast(1))
        }
        val quantities = reactions.keys.associateWith { 0L }.toMutableMap()
        quantities["ORE"] = 1000000000000L
        fun produce(c: Chem): Boolean {
            if (c.name == "ORE") {
                return quantities[c.name]!! >= c.quantity
            } else {
                val r = reactions[c.name]!!
                r.input.forEach { i ->
                    while (quantities[i.name]!! < i.quantity) {
                        if (!produce(i)) return false
                    }
                    quantities[i.name] = quantities[i.name]!! - i.quantity
                }
                quantities[c.name] = quantities[c.name]!! + r.quantity
                return true
            }
        }
        var fuels = 0L
        while (produce(Chem("FUEL", 1))) {
            fuels++
        }
        return fuels
    }

    private fun twoA(input: List<String>): Long {
        val reactions = parse(input).associate {
            val out = it.last()
            out.name to Reaction(out.quantity, it.dropLast(1))
        }
        val quantities = reactions.keys.associateWith { 0L }.toMutableMap()
        quantities["ORE"] = 1000000000000L
        fun produce(c: Chem, num: Long): Boolean {
            val r = reactions[c.name]!!
            r.input.forEach { i ->
                if (i.name != "ORE") {
                    while (quantities[i.name]!! < i.quantity * num) {
                        if (!produce(i, num)) return false
                    }
                }
                quantities[i.name] = quantities[i.name]!! - i.quantity * num
                if (quantities[i.name]!! < 0) {
                    return false
                }
            }
            quantities[c.name] = quantities[c.name]!! + r.quantity * num
            return true
        }

        var f = quantities["ORE"]!!
        var fuels = 0L
        val fc = Chem("FUEL", 1)
        while (true) {
            f /= 10L
            var lq = quantities.toMap()
            while (produce(fc, f)) {
                fuels += f
                lq = quantities.toMap()
            }
            if (f == 1L) {
                return fuels
            }
            quantities.keys.forEach { quantities[it] = lq[it]!! }
        }
        return fuels
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 31
        one(sample2) shouldBe 165
        one(sample3) shouldBe 13312
        one(input) shouldBe 337862
    }

    @Test
    fun testTwo(input: List<String>) {
//        twoA(sample3) shouldBe 82892753L
//        twoA(sample4) shouldBe 460664L
        two(input) shouldBe 3687786L
    }
}
