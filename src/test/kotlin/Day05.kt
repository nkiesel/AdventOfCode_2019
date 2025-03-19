import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day05 {
    private val sample = """
        1002,4,3,4,33
    """.trimIndent().lines()

    private val sample2 = """
        3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
        1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
        999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input.joinToString("").split(",").map { it.toInt() }.toIntArray()

    private fun one(input: List<String>): Int {
        return calc(parse(input), 1)
    }

    private fun two(input: List<String>, v: Int): Int {
        return calc(parse(input), v)
    }

    private fun calc(prog: IntArray, input: Int): Int {
        var i = 0
        val m = IntArray(4)
        var output = 0
        fun v(o: Int) = prog[if (m[o] == 0) prog[i + o] else i + o]
        fun set(o: Int, v: Int) {
            prog[prog[i + o]] = v
        }
        while (true) {
            val opCode = prog[i] % 100
            m[1] = prog[i] / 100 % 10
            m[2] = prog[i] / 1000 % 10
            m[3] = prog[i] / 10000 % 10

            when (opCode) {
                1 -> {
                    set(3, v(1) + v(2))
                    i += 4
                }

                2 -> {
                    set(3, v(1) * v(2))
                    i += 4
                }

                3 -> {
                    set(1, input)
                    i += 2
                }

                4 -> {
                    output = v(1)
                    i += 2
                }

                5 -> {
                    if (v(1) != 0) i = v(2) else i += 3
                }

                6 -> {
                    if (v(1) == 0) i = v(2) else i += 3
                }

                7 -> {
                    set(3, if (v(1) < v(2)) 1 else 0)
                    i += 4
                }

                8 -> {
                    set(3, if (v(1) == v(2)) 1 else 0)
                    i += 4
                }

                99 -> break
            }
        }
        return output
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 0
        one(input) shouldBe 16348437
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample2, 5) shouldBe 999
        two(sample2, 8) shouldBe 1000
        two(sample2, 13) shouldBe 1001
        two(input, 5) shouldBe 6959377
    }
}
