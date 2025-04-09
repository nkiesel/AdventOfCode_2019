import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day19 {
    private fun parse(input: List<String>) = input.joinToString("").split(",").map { it.toLong() }

    private fun one(input: List<String>): Int {
        val program = parse(input)
        var count = 0
        for (x in 0..<50) {
            for (y in 0..<50) {
                if (LongCodeComputer(program).calc(listOf(x.toLong(), y.toLong())).last() == 1L) count++
            }
        }
        return count
    }

    private fun two(input: List<String>): Int {
        val program = parse(input)
        fun pull(x: Int, y: Int) = LongCodeComputer(program).calc(listOf(x.toLong(), y.toLong())).last() == 1L
        val s = 99
        var x = s
        var y = s
        while (true) {
            while (!pull(x, y)) x--
            if (x >= s && pull(x - s, y + s) && pull(x - s, y) && pull(x, y + s)) return (x - s) * 10000 + y
            x += 2
            y++
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(input) shouldBe 192
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input) shouldBe 8381082
    }

    private class LongCodeComputer(initial: List<Long>) {
        private val prog = initial.indices.associate { it.toLong() to initial[it] }.toMutableMap()
        private val inputs = ArrayDeque<Long>()
        private val outputs = mutableListOf<Long>()
        private var i = 0L
        var halted = false
            private set
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
            return calc(true)
        }

        fun calc(input: List<Long>): List<Long> {
            inputs.clear()
            inputs.addAll(input)
            calc(false)
            return outputs
        }

        fun calc(singleOutput: Boolean): Long {
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
                        outputs += v(1)
                        i += 2
                        if (singleOutput) return outputs.last()
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
}
