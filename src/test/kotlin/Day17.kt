import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.text.replace

class Day17 {
    private fun parse(input: List<String>) = input.joinToString("").split(",").map { it.toLong() }

    private fun one(initial: List<String>): Int {
        val area = area(parse(initial))
        return area.tiles { it == '#' }.filter { area.neighbors4(it).all { area[it] == '#' } }.sumOf { it.x * it.y }
    }

    private fun area(program: List<Long>): CharArea {
        val computer = LongCodeComputer(program)
        var pos = Point(0, 0)
        val points = mutableMapOf<Point, Char>()
        while (!computer.halted) {
            val output = computer.calc(0L)
            output.toInt().toChar()
            when (output) {
                10L -> pos = Point(-1, pos.y + 1)
                else -> points[pos] = output.toInt().toChar()
            }
            pos = pos.move(Direction.E)
        }
        val maxX = points.keys.maxOf { it.x }
        val maxY = points.keys.maxOf { it.y }
        val area = CharArea(maxX + 1, maxY + 1, '.')
        points.forEach { area[it.key] = it.value }
//        area.png()
        return area
    }

    private fun two(initial: List<String>): Long {
        val program = parse(initial)
        val area = area(program)
        var pos = area.tiles { it in "<>^v" }.first()
        var direction = when (area[pos]) {
            '<' -> Direction.W
            '^' -> Direction.N
            'v' -> Direction.S
            else -> Direction.E
        }
        var count = 1
        val steps = mutableListOf<String>()
        while (true) {
            val next = pos.move(direction)
            if (next in area && area[next] == '#') {
                count++
                pos = next
            } else {
                if (steps.isNotEmpty()) {
                    steps.add(count.toString())
                    count = 1
                }
                val np = area.neighbors4(pos) { it == '#' }.find { it != pos.move(direction.reverse()) }
                if (np == null) break
                val nd = pos.direction(np)
                steps.add(if (nd == direction.turnRight()) "R" else "L")
                direction = nd
                pos = np
            }
        }
        val origCmd = steps.joinToString(",")
        val exA = mutableSetOf<String>()
        while (true) {
            var cmd = origCmd
            val fA = findFunc(cmd, exA)
            cmd = cmd.replace(fA, "A")
            val fB = findFunc(cmd)
            cmd = cmd.replace(fB, "B")
            val fC = findFunc(cmd)
            cmd = cmd.replace(fC, "C")
            if (cmd.matches(Regex("^[ABC,]+$"))) {
                val c2 = LongCodeComputer(listOf(2L) + program.drop(1))
                val input = "$cmd\n$fA\n$fB\n$fC\nn\n".map { it.code.toLong() }
                return c2.calc(input).last()
            }
            exA += fA
        }
    }

    fun findFunc(orig: String, excluded: Set<String> = emptySet()): String {
        val cmd = orig.replace(Regex("^([ABC],)+"), "")
        var end = minOf(20, cmd.length)
        var best = ""
        var bestRep = 0
        while (end > 2) {
            while (cmd[end] != ',') end--
            val fn = cmd.take(end)
            if (fn !in excluded && 'A' !in fn && 'B' !in fn && 'C' !in fn) {
                val occ = cmd.findAllOccurrences(fn)
                val rep = occ.size * fn.length
                if (rep > bestRep) {
                    best = fn
                    bestRep = rep
                }
            }
            end--
        }
        return best
    }

    @Test
    fun testOne(input: List<String>) {
        one(input) shouldBe 7280
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input) shouldBe 1045393L
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
