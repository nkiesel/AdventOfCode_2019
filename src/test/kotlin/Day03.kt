import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day03 {
    private val sample = """
        R8,U5,L5,D3
        U7,R6,D4,L4
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input.map { it.split(",") }

    private fun one(input: List<String>) = three(input, Part.ONE)

    private fun two(input: List<String>) = three(input, Part.TWO)

    private fun three(input: List<String>, part: Part): Int {
        val (w1, w2) = parse(input)
        val c = Point(0, 0)
        val p1 = points(w1, c)
        val p2 = points(w2, c)
        return p1.intersect(p2).minOf {
            when (part) {
                Part.ONE -> manhattanDistance(c, it)
                Part.TWO -> p1.indexOf(it) + p2.indexOf(it) + 2
            }
        }
    }

    private fun points(w: List<String>, s: Point): List<Point> {
        var p = s
        return buildList {
            for (i in w) {
                val d = when (i[0]) {
                    'U' -> Direction.N
                    'D' -> Direction.S
                    'R' -> Direction.E
                    'L' -> Direction.W
                    else -> error("Invalid direction ${i[0]}")
                }
                repeat(i.drop(1).toInt()) {
                    p = p.move(d)
                    add(p)
                }
            }
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 6
        one(input) shouldBe 221
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 30
        two(input) shouldBe 18542
    }
}
