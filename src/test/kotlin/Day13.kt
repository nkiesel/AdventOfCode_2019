import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.sign

class Day13 {
    private fun parse(input: List<String>) = input.joinToString("").split(",").map { it.toLong() }

    private fun one(initial: List<String>): Int {
        val computer = LongCodeComputer(parse(initial))
        var blocks = 0
        while (!computer.halted) {
            val x = computer.calc(0)
            val y = computer.calc(0)
            val t = computer.calc(0)
            if (t == 2L) blocks++
        }
        return blocks
    }

    private fun two(initial: List<String>): Long {
        var input = 0L
        var output = 0L
        val computer = LongCodeComputer(listOf(2L) + parse(initial).drop(1))

        var paddleX = 0L
        while (!computer.halted) {
            val x = computer.calc(input)
            val y = computer.calc(input)
            val t = computer.calc(input)
            if (x == -1L && y == 0L) {
                output = t
            }
            when (t) {
                3L -> paddleX = x
                4L -> input = (x - paddleX).sign.toLong()
            }
        }
        return output
    }

    private class LongCodeComputer(initial: List<Long>) {
        private val prog = initial.indices.associate { it.toLong() to initial[it] }.toMutableMap()
        private var i = 0L
        var halted = false
            private set
        private var output: Long = 0L
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
                        set(1, input)
                        i += 2
                    }

                    4 -> {
                        output = v(1)
                        i += 2
                        return output
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
            return 0L
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(input) shouldBe 344
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input) shouldBe 17336
    }
}
