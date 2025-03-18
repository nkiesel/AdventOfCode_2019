import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day04 {
    private fun parse(input: List<String>) = input[0].split("-").map { it.toInt() }

    private fun one(input: List<String>) = three(input, Part.ONE)

    private fun two(input: List<String>) = three(input, Part.TWO)

    private fun three(input: List<String>, part: Part): Int {
        val (a, b) = parse(input)
        return (a..b).count { v ->
            var same = false
            var inc = true
            v.toString().let { "-$it-" }.windowed(4).map { it.toList() }.forEach { (p, a, b, s) ->
                if (a > b) inc = false
                if (a == b && (part == Part.ONE || a != p && a != s)) same = true
            }
            same && inc
        }
    }

    @Test
    fun testOne(input: List<String>) {
        fun t(v: Int) = one(listOf("$v-$v"))
        t(111111) shouldBe 1
        t(223450) shouldBe 0
        t(123789) shouldBe 0
        one(input) shouldBe 1154
    }

    @Test
    fun testTwo(input: List<String>) {
        fun t(v: Int) = two(listOf("$v-$v"))
        t(112233) shouldBe 1
        t(123444) shouldBe 0
        t(111122) shouldBe 1
        two(input) shouldBe 750
    }
}
