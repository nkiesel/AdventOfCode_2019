import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue

class Day16 {
    private val sample1 = """12345678""".trimIndent().lines()
    private val sample2 = """80871224585914546619083218645595""".trimIndent().lines()
    private val sample3 = """19617804207202209144916044189917""".trimIndent().lines()
    private val sample4 = """69317163492948606335995924319873""".trimIndent().lines()
    private val sample5 = """03036732577212944063491565474664""".trimIndent().lines()

    private fun parse(input: List<String>) = input[0].map { it.digitToInt() }

    private fun one(input: List<String>, rep: Int = 100): String {
        val signal = parse(input)
        var next = signal
        val base = listOf(0, 1, 0, -1)
        val bs = base.size
        fun pattern(col: Int, row: Int) : Int {
            val rep = bs * (row + 1)
            val f = (col + 1) % rep / (row + 1)
            return base[f % bs]
        }
//        repeat(8) { println(signal.withIndex().joinToString(" + ") { (i, c) -> "$c*${pattern(i, it)}" }) }
        repeat(rep) {
            next = next.indices.map { row -> next.withIndex().sumOf { (col, d) -> d * pattern(col, row) }.absoluteValue % 10 }
        }
        return next.take(8).joinToString("")
    }

    private fun two(input: List<String>, rep: Int = 100): String {
        val signal = parse(input)
        val offset = signal.take(7).joinToString("").toInt()
        val fromIndex = (offset / 8) % signal.size
        var next = List(10000) { signal }.flatten()
        val base = listOf(0, 1, 0, -1)
        val bs = base.size
        fun pattern(col: Int, row: Int) = base[(col + 1) % (bs * (row + 1)) / (row + 1) % bs]
        repeat(rep) {
            next = next.indices.map { row -> next.withIndex().sumOf { (col, d) -> d * pattern(col, row) }.absoluteValue % 10 }
        }
        println(offset)
        return next.subList(offset, offset + 8).joinToString("")
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1, 4) shouldBe "01029498"
        one(sample2) shouldBe "24176176"
        one(sample3) shouldBe "73745418"
        one(sample4) shouldBe "52432133"
        one(input) shouldBe "30379585"
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample5) shouldBe "84462026"
//        two(input) shouldBe 0
    }
}
