import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.sign

class Day12 {
    private val sample1 = """
        <x=-1, y=0, z=2>
        <x=2, y=-10, z=-7>
        <x=4, y=-8, z=8>
        <x=3, y=5, z=-1>
    """.trimIndent().lines()

    private val sample2 = """
        <x=-8, y=-10, z=0>
        <x=5, y=5, z=10>
        <x=2, y=-7, z=3>
        <x=9, y=-8, z=-3>
    """.trimIndent().lines()

    class Moon(val pos: MutableList<Int>, val vel: MutableList<Int>) {
        fun clone(): Moon = Moon(pos.toList().toMutableList(), vel.toList().toMutableList())
    }

    private fun parse(input: List<String>) = input.map { Moon(it.ints().toMutableList(), MutableList(3) { 0 }) }

    private fun one(input: List<String>, steps: Int): Int {
        val moons = parse(input)
        val moonPairs = moons.combinations(2).toList()
        repeat(steps) {
            for ((a, b) in moonPairs) {
                for (i in a.pos.indices) {
                    val d = (a.pos[i] - b.pos[i]).sign
                    a.vel[i] -= d
                    b.vel[i] += d
                }
            }
            for (m in moons) {
                for (i in m.pos.indices) {
                    m.pos[i] += m.vel[i]
                }
            }
        }
        return moons.sumOf { m -> m.pos.sumOf { abs(it) } * m.vel.sumOf { abs(it) } }
    }

    private fun two(input: List<String>): Long {
        val start = parse(input)
        val moons = start.map { it.clone() }
        val moonPairs = moons.combinations(2).toList()
        fun single(d: Int): Long {
            var step = 0L
            do {
                for ((a, b) in moonPairs) {
                    val s = (a.pos[d] - b.pos[d]).sign
                    a.vel[d] -= s
                    b.vel[d] += s
                }
                for (m in moons) {
                    m.pos[d] += m.vel[d]
                }
                step++
            } while (moons.indices.any { mi ->
                    val sm = start[mi]
                    val cm = moons[mi]
                    cm.pos[d] != sm.pos[d] || cm.vel[d] != sm.vel[d]
                }
            )
            return step
        }

        return moons[0].pos.indices.map { single(it) }.lcm()
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1, 10) shouldBe 179
        one(sample2, 100) shouldBe 1940
        one(input, 1000) shouldBe 7202
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample1) shouldBe 2772L
        two(sample2) shouldBe 4686774924L
        two(input) shouldBe 537881600740876L
    }
}
