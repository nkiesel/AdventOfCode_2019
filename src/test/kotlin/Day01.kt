import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day01 {
    private fun parse(input: List<String>) = input.map { it.toInt() }

    private fun one(input: List<String>): Int {
        return parse(input).sumOf { it / 3 - 2 }
    }

    private fun two(input: List<String>): Int {
        fun fuel(module: Int): Int {
            val f = module / 3 - 2
            return if (f <= 0) 0 else f + fuel(f)
        }
        return parse(input).sumOf { fuel(it) }
    }

    @Test
    fun testOne(input: List<String>) {
        one(input) shouldBe 3329926
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input) shouldBe 4992008
    }
}
