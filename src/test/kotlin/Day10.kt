import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sign

class Day10 {
    private val sample1 = """
        .#..#
        .....
        #####
        ....#
        ...##
    """.trimIndent().lines()

    private val sample2 = """
        ......#.#.
        #..#.#....
        ..#######.
        .#.#.###..
        .#..#.....
        ..#....#.#
        #..#....#.
        .##.#..###
        ##...#..#.
        .#....####
    """.trimIndent().lines()

    private val sample3 = """
        #.#...#.#.
        .###....#.
        .#....#...
        ##.#.#.#.#
        ....#.#.#.
        .##..###.#
        ..#...##..
        ..##....##
        ......#...
        .####.###.
    """.trimIndent().lines()

    private val sample4 = """
        .#..#..###
        ####.###.#
        ....###.#.
        ..###.##.#
        ##.##.#.#.
        ....###..#
        ..#.#..#.#
        #..#.#.###
        .##...##.#
        .....#.#..
    """.trimIndent().lines()

    private val sample5 = """
        .#..##.###...#######
        ##.############..##.
        .#.######.########.#
        .###.#######.####.#.
        #####.##.#.##.###.##
        ..#####..#.#########
        ####################
        #.####....###.#.#.##
        ##.#################
        #####.##.###..####..
        ..######..##.#######
        ####.##.####...##..#
        .#####..#.######.###
        ##...#.##########...
        #.##########.#######
        .####.#.###.###.#.##
        ....##.##.###..#####
        .#.#.###########.###
        #.#.#.#####.####.###
        ###.##.####.##.#..##
    """.trimIndent().lines()

    private fun parse(input: List<String>) = CharArea(input).tiles { it == '#' }.toSet()

    private fun one(input: List<String>): Int {
        val ast = parse(input)
        return ast.maxOf { a ->
            ast.filter { it != a }.count { b ->
                val dx = b.x - a.x
                val dy = b.y - a.y
                var sx = dx.sign
                var sy = dy.sign
                if (dx != 0 && dy != 0) {
                    val gcd = gcd(dx, dy)
                    sx *= abs(dx / gcd)
                    sy *= abs(dy / gcd)
                }
                var p = a
                do {
                    p = p.move(sx, sy)
                } while (p !in ast)
                p == b
            }
        }
    }

    private fun two(input: List<String>): Int {
        val ast = parse(input)
        val laser = ast.maxBy { a ->
            ast.filter { it != a }.count { b ->
                val dx = b.x - a.x
                val dy = b.y - a.y
                var sx = dx.sign
                var sy = dy.sign
                if (dx != 0 && dy != 0) {
                    val gcd = gcd(dx, dy)
                    sx *= abs(dx / gcd)
                    sy *= abs(dy / gcd)
                }
                var p = a
                do {
                    p = p.move(sx, sy)
                } while (p !in ast)
                p == b
            }
        }

        fun Point.angle() = atan2((x - laser.x).toDouble(), (y - laser.y).toDouble())

        val sorted = ast
            .sortedWith(compareByDescending(Point::angle).thenBy { manhattanDistance(it, laser) })
            .map { it to it.angle() }
            .toMutableList()
        var r = sorted.removeAt(0)
        var a = r.second
        var i = 0
        repeat(199) {
            while (sorted[i].second == a) i = (i + 1) % sorted.size
            r = sorted.removeAt(i)
            a = r.second
        }
        return r.first.x * 100 + r.first.y
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 8
        one(sample2) shouldBe 33
        one(sample3) shouldBe 35
        one(sample4) shouldBe 41
        one(sample5) shouldBe 210
        one(input) shouldBe 326
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample5) shouldBe 802
        two(input) shouldBe 1623
    }
}
