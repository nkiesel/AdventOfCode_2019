import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day15 {
    private fun parse(input: List<String>) = input.joinToString("").split(",").map { it.toLong() }

    private fun one(initial: List<String>) = three(initial, Part.ONE)

    private fun two(input: List<String>) = three(input, Part.TWO)

    private fun three(initial: List<String>, part: Part): Int {
        val computer = LongCodeComputer(parse(initial))
        var pos = Point(0, 0)
        val walls = mutableSetOf<Point>()
        val open = mutableSetOf(pos)
        val path = mutableListOf(pos)
        outer@ while (true) {
            val n4 = pos.neighbors4().filter { it !in walls && it !in open }
            if (n4.isEmpty()) {
                var n: Point
                do {
                    n = path.removeLast()
                } while (n == pos)
                when (pos.direction(n)) {
                    Direction.N -> 1L
                    Direction.S -> 2L
                    Direction.W -> 3L
                    else -> 4L
                }.let { computer.calc(it) }
                pos = n
                path.add(n)
            } else {
                for (n in n4) {
                    val output = when (pos.direction(n)) {
                        Direction.N -> 1L
                        Direction.S -> 2L
                        Direction.W -> 3L
                        else -> 4L
                    }.let { computer.calc(it) }
                    if (output == 0L) {
                        walls.add(n)
                    } else {
                        pos = n
                        path.add(n)
                        open.add(n)
                        if (output == 2L) {
                            break@outer
                        }
                        break
                    }
                }
            }
        }

        return when (part) {
            Part.ONE -> path.size - 1
            Part.TWO -> bfs(path.last()) { p -> p.neighbors4().filter { it in open } }.last().index
        }
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
        one(input) shouldBe 294
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input) shouldBe 388
    }
}
