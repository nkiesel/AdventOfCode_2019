import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day20 {
    private val sample1 = """
                 A           
                 A           
          #######.#########  
          #######.........#  
          #######.#######.#  
          #######.#######.#  
          #######.#######.#  
          #####  B    ###.#  
        BC...##  C    ###.#  
          ##.##       ###.#  
          ##...DE  F  ###.#  
          #####    G  ###.#  
          #########.#####.#  
        DE..#######...###.#  
          #.#########.###.#  
        FG..#########.....#  
          ###########.#####  
                     Z       
                     Z       
    """.trimIndent().lines()

    private val sample2 = """
                        A               
                        A               
       #################.#############  
       #.#...#...................#.#.#  
       #.#.#.###.###.###.#########.#.#  
       #.#.#.......#...#.....#.#.#...#  
       #.#########.###.#####.#.#.###.#  
       #.............#.#.....#.......#  
       ###.###########.###.#####.#.#.#  
       #.....#        A   C    #.#.#.#  
       #######        S   P    #####.#  
       #.#...#                 #......VT
       #.#.#.#                 #.#####  
       #...#.#               YN....#.#  
       #.###.#                 #####.#  
     DI....#.#                 #.....#  
       #####.#                 #.###.#  
     ZZ......#               QG....#..AS
       ###.###                 #######  
     JO..#.#.#                 #.....#  
       #.#.#.#                 ###.#.#  
       #...#..DI             BU....#..LF
       #####.#                 #.#####  
     YN......#               VT..#....QG
       #.###.#                 #.###.#  
       #.#...#                 #.....#  
       ###.###    J L     J    #.#.###  
       #.....#    O F     P    #.#...#  
       #.###.#####.#.#####.#####.###.#  
       #...#.#.#...#.....#.....#.#...#  
       #.#####.###.###.#.#.#########.#  
       #...#.#.....#...#.#.#.#.....#.#  
       #.###.#####.###.###.#.#.#######  
       #.#.........#...#.............#  
       #########.###.###.#############  
                B   J   C               
                U   P   P               
    """.trimIndent().lines()

    private fun parse(input: List<String>) = CharArea(input)

    private fun one(input: List<String>): Int {
        val area = parse(input)
        val two = area.tiles { it in 'A'..'Z' }.map { c -> c to area.neighbors4(c) { it in 'A'..'Z' || it == '.' } }
            .filter { it.second.size == 2 }
        val pt = mutableMapOf<String, MutableList<Point>>()
        for (p in two) {
            val p2 = p.second.first { area[it] == '.' }
            val key = listOf(p.first, p.second.first { area[it] != '.' }).sorted().map { area[it] }.joinToString("")
            pt.getOrPut(key) { mutableListOf<Point>() }.add(p2)
        }
        val start = pt["AA"]!!.first()
        val end = pt["ZZ"]!!.first()
        val portals =
            pt.filterKeys { it != "AA" && it != "ZZ" }.values.map { (a, b) -> listOf(a to b, b to a) }.flatten().toMap()

        fun neighbors(p: Point): List<Point> {
            val n = area.neighbors4(p) { it == '.' }
            val o = portals[p]
            return if (o == null) n else n + o
        }

        return bfs(start) { p -> neighbors(p) }.first { it.value == end }.index
    }

    private fun two(input: List<String>): Int {
        return 0
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 23
        one(sample2) shouldBe 58
        one(input) shouldBe 632
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(sample) shouldBe 0
//        two(input) shouldBe 0
    }
}
