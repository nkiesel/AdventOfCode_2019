import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day09 {
    private val sample1 = """
        109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99
    """.trimIndent().lines()

    private val sample2 = """
        1102,34915192,34915192,7,4,7,99,0
    """.trimIndent().lines()

    private val sample3 = """
        104,1125899906842624,99
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input.joinToString("").split(",").map { it.toLong() }

    private fun one(input: List<String>, value: Long = 0L): Long {
        return LongCodeComputer(parse(input)).calc(value)
    }

    private class LongCodeComputer(initial: List<Long>) {
        private val prog = initial.indices.associate { it.toLong() to initial[it] }.toMutableMap()
        private var i = 0L
        private val inputs = ArrayDeque<Long>()
        var halted = false
            private set
        private val output = mutableListOf<Long>()
        private val m = LongArray(4)
        private var base = 0L

        private fun g(i: Long) = prog.getOrDefault(i, 0L)

        private fun v(o: Int) = g(k(o))

        private fun k(o: Int) = when (m[o]) {
            0L -> g(i + o)
            1L -> i + o
            2L -> g(i + o) + base
            else -> error("Invalid mode ${m[o]}")
        }

        private fun set(o: Int, value: Long) {
            prog[k(o)] = value
        }

        fun calc(input: Long): Long {
            inputs.addLast(input)
            while (!halted) {
                val p = prog[i]!!
                val opCode = (p % 100L).toInt()
                m[1] = p / 100L % 10L
                m[2] = p / 1000L % 10L
                m[3] = p / 10000L % 10L

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
                        set(1, inputs.removeFirst())
                        i += 2
                    }

                    4 -> {
                        output += v(1)
                        i += 2
                    }

                    5 -> {
                        if (v(1) != 0L) i = v(2) else i += 3
                    }

                    6 -> {
                        if (v(1) == 0L) i = v(2) else i += 3
                    }

                    7 -> {
                        set(3, if (v(1) < v(2)) 1L else 0L)
                        i += 4
                    }

                    8 -> {
                        set(3, if (v(1) == v(2)) 1L else 0L)
                        i += 4
                    }

                    9 -> {
                        base += v(1)
                        i += 2
                    }

                    99 -> {
                        halted = true
                    }

                    else -> {
                        error("Unexpected opcode: $opCode")
                    }
                }
            }
//            println(output)
            return output.last()
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 99L
        one(sample2) shouldBe 1219070632396864L
        one(sample3) shouldBe 1125899906842624L
        one(input, 1L) shouldBe 2662308295L
    }

    @Test
    fun testTwo(input: List<String>) {
        one(input, 2L) shouldBe 63441L
    }
}
