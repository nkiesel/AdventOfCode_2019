import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day07 {
    private val sample1 = """
        3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0
    """.trimIndent().lines()

    private val sample2 = """
        3,23,3,24,1002,24,10,24,1002,23,-1,23,
        101,5,23,23,1,24,23,23,4,23,99,0,0
    """.trimIndent().lines()

    private val sample3 = """
        3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,
        1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0
    """.trimIndent().lines()

    private val sample4 = """
        3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,
        27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5
    """.trimIndent().lines()

    private val sample5 = """
        3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,
        -5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,
        53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input.joinToString("").split(",").map { it.toInt() }.toIntArray()

    private fun one(input: List<String>): Int {
        val prog = parse(input)
        return (0..4).toList().permutations().maxOf { phases ->
            var signal = 0
            repeat(5) {
                signal = Prog(prog, phases[it]).calc(signal)
            }
            signal
        }
    }

    private fun two(input: List<String>): Int {
        val prog = parse(input)
        return (5..9).toList().permutations().maxOf { phases ->
            val amplifiers = phases.map { Prog(prog, it) }
            var a = 0
            var signal = 0
            do {
                signal = amplifiers[a].calc(signal)
                a = (a + 1) % amplifiers.size
            } while (amplifiers.any { !it.halted })
            signal
        }
    }

    private class Prog(initial: IntArray, phase: Int) {
        private val prog = initial.copyOf()
        private var i = 0
        private val inputs = ArrayDeque<Int>().apply { add(phase) }
        var halted = false
            private set
        private var output = 0
        private val m = IntArray(4)

        private fun v(o: Int) = prog[if (m[o] == 0) prog[i + o] else i + o]

        private fun set(o: Int, v: Int) {
            prog[prog[i + o]] = v
        }

        fun calc(input: Int): Int {
            inputs.addLast(input)
            while (!halted) {
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
                        set(1, inputs.removeFirst())
                        i += 2
                    }

                    4 -> {
                        output = v(1)
                        i += 2
                        break
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

                    99 -> {
                        halted = true
                        break
                    }

                    else -> {
                        error("Unexpected opcode: $opCode")
                    }
                }
            }
            return output
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 43210
        one(sample2) shouldBe 54321
        one(sample3) shouldBe 65210
        one(input) shouldBe 255840
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample4) shouldBe 139629729
        two(sample5) shouldBe 18216
        two(input) shouldBe 84088865
    }
}
