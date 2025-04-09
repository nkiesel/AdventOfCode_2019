import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day18 {
    private val sample1 = """
        #########
        #b.A.@.a#
        #########
    """.trimIndent().lines()

    private val sample2 = """
        ########################
        #f.D.E.e.C.b.A.@.a.B.c.#
        ######################.#
        #d.....................#
        ########################
    """.trimIndent().lines()

    private val sample3 = """
        ########################
        #...............b.C.D.f#
        #.######################
        #.....@.a.B.c.d.A.e.F.g#
        ########################
    """.trimIndent().lines()

    private val sample4 = """
        #################
        #i.G..c...e..H.p#
        ########.########
        #j.A..b...f..D.o#
        ########@########
        #k.E..a...g..B.n#
        ########.########
        #l.F..d...h..C.m#
        #################
    """.trimIndent().lines()

    private val sample5 = """
        ########################
        #@..............ac.GI.b#
        ###d#e#f################
        ###A#B#C################
        ###g#h#i################
        ########################
    """.trimIndent().lines()

    private fun parse(input: List<String>) = CharArea(input)

    private val lowerCase = ('a'..'z').toSet()
    private val upperCase = ('A'..'Z').toSet()

    private fun one(input: List<String>): Int {
        val area = parse(input)
        var pos = area.first('@')
//        area[pos] = '.'
        val keys = area.tiles { it in lowerCase }.toSet()
        val doors = area.tiles { it in upperCase }.toSet()
        return steps(area, pos, 0, keys, doors)!!
    }

    private fun steps(area: CharArea, p: Point, s: Int, k: Set<Point>, d: Set<Point>): Int? {
        var pos = p
        var steps = s
        var keys = k.toMutableSet()
        var doors = d.toMutableSet()
        while (keys.isNotEmpty()) {
            val reachableKeys =
                bfs(pos) { p -> area.neighbors4(p) { it != '#' }.filter { it !in doors } }
                    .filter { it.value in keys }
                    .toList()
            if (reachableKeys.isEmpty()) {
                return null
            }
            if (reachableKeys.size == 1) {
                pos = reachableKeys.first().value
                val door = doors.find { area[it] == area[pos].uppercaseChar() }
                if (door != null) {
                    doors -= door
                }
                steps += reachableKeys.first().index
                keys -= pos
            } else {
                val m = reachableKeys.mapNotNull { rk ->
                    val dd = doors.find { area[it] == area[pos].uppercaseChar() }
                    steps(
                        area,
                        rk.value,
                        rk.index,
                        keys - rk.value,
                        if (dd == null) doors else doors - dd
                    )
                }.minOrNull()
                return if (m == null) -1 else steps + m
            }
        }
        return steps
    }

    private fun two(input: List<String>): Int {
        return 0
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 8
        one(sample2) shouldBe 86
        one(sample3) shouldBe 132
//        one(sample4) shouldBe 136
//        one(input) shouldBe 0
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(sample) shouldBe 0
//        two(input) shouldBe 0
    }
}
