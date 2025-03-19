import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day06 {
    private val sample = """
        COM)B
        B)C
        C)D
        D)E
        E)F
        B)G
        G)H
        D)I
        E)J
        J)K
        K)L
    """.trimIndent().lines()

    private val sample2 = """
        COM)B
        B)C
        C)D
        D)E
        E)F
        B)G
        G)H
        D)I
        E)J
        J)K
        K)L
        K)YOU
        I)SAN
    """.trimIndent().lines()

    private fun parse(input: List<String>): Map<String, List<String>> {
        val tree = mutableMapOf<String, MutableList<String>>()
        input.forEach {
            val (l, r) = it.split(")")
            tree.getOrPut(l) { mutableListOf() }.add(r)
        }
        return tree
    }

    private fun one(input: List<String>): Int {
        val tree = parse(input)
        fun orbits(k: String, l: Int): Int = l + (tree[k]?.sumOf { orbits(it, l + 1) } ?: 0)
        return orbits("COM", 0)
    }

    private fun two(input: List<String>): Int {
        val tree = parse(input)
        fun path(k: String): MutableList<String> {
            val parent = tree.entries.find { k in it.value }?.key
            return if (parent == null) mutableListOf(k) else path(parent).apply { add(k) }
        }

        val you = path("YOU")
        val san = path("SAN")
        var i = 0
        while (you[i] == san[i]) i++
        return you.size + san.size - 2 * (i + 1)
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 42
        one(input) shouldBe 278744
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample2) shouldBe 4
        two(input) shouldBe 475
    }
}
