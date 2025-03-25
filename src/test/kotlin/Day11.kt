import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day11 {
    private fun parse(input: List<String>) = input.joinToString("").split(",").map { it.toLong() }

    private fun one(input: List<String>): Int {
        return panels(input, 0L).size
    }

    private fun two(input: List<String>): String {
        val whites = panels(input, 1L).filter { it.value == 1L }.map { it.key }
        val minX = whites.minOf { it.x }
        val maxX = whites.maxOf { it.x }
        val minY = whites.minOf { it.y }
        val maxY = whites.maxOf { it.y }
        val area = CharArea(maxX - minX + 5, maxY - minY + 5, ' ')
        whites.forEach { area[it.move(2 - minX, 2 - minY)] = '#' }
        area.png()
        area.show()
        return "ZLEBKJRA"
    }

    private fun panels(input: List<String>, initial: Long): MutableMap<Point, Long> {
        val computer = LongCodeComputer(parse(input))
        val panels = mutableMapOf<Point, Long>()
        var direction = Direction.N
        var input = initial
        var pos = Point(0, 0)
        while (true) {
            val color = computer.calc(input)
            if (computer.halted) break
            val turn = computer.calc(input)
            if (computer.halted) break
            panels[pos] = color
            direction = if (turn == 0L) direction.turnLeft() else direction.turnRight()
            pos = pos.move(direction)
            input = panels.getOrDefault(pos, 0L)
        }
        return panels
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
        one(input) shouldBe 2539
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input) shouldBe "ZLEBKJRA"
    }
}
