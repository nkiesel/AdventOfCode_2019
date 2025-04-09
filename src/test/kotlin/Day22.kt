import io.kotest.matchers.shouldBe
import jdk.vm.ci.code.Location.stack
import org.junit.jupiter.api.Test

class Day22 {
    private val sample1 = """
        deal with increment 7
        deal into new stack
        deal into new stack
    """.trimIndent().lines()
    private val sample2 = """
        cut 6
        deal with increment 7
        deal into new stack
    """.trimIndent().lines()
    private val sample3 = """
        deal with increment 7
        deal with increment 9
        cut -2
    """.trimIndent().lines()
    private val sample4 = """
        deal into new stack
        cut -2
        deal with increment 7
        cut 8
        cut -4
        deal with increment 7
        cut 3
        deal with increment 9
        deal with increment 3
        cut -1
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input

    private fun one(input: List<String>, num: Int = 10007): IntArray {
        var stack = IntArray(num) { it }
        for (inst in parse(input)) {
            when {
                inst.startsWith("deal into") -> {
                    stack.reverse()
                }

                inst.startsWith("cut") -> {
                    val cut = inst.ints().first()
                    var next = IntArray(num)
                    if (cut > 0) {
                        for (i in 0..<cut) next[num - cut + i] = stack[i]
                        for (i in cut..<num) next[i - cut] = stack[i]
                    } else {
                        for (i in cut + num until num) {
                            next[i - num - cut] = stack[i]
                        }
                        for (i in 0 until cut + num) next[i - cut] = stack[i]
                    }
                    stack = next
                }

                inst.startsWith("deal with") -> {
                    val inc = inst.ints().first()
                    var next = IntArray(num)
                    var pos = 0
                    for (i in 0..<num) {
                        next[pos] = stack[i]
                        pos = (pos + inc) % num
                    }
                    stack = next
                }
            }
        }
        return stack
    }

    private fun two(input: List<String>): Long {
        val num = 119315717514047L
        val rep = 101741582076661L
        return 0L
    }

    @Test
    fun testOne(input: List<String>) {
        fun o(s: String, r: String) {
            one(listOf(s), 10).joinToString(" ") shouldBe r
        }
        o("deal into new stack", "9 8 7 6 5 4 3 2 1 0")
        o("cut 3 cards", "3 4 5 6 7 8 9 0 1 2")
        o("cut -4 cards", "6 7 8 9 0 1 2 3 4 5")
        o("deal with increment 3", "0 7 4 1 8 5 2 9 6 3")
        one(sample1, 10) shouldBe "0 3 6 9 2 5 8 1 4 7".ints().toIntArray()
        one(sample2, 10) shouldBe "3 0 7 4 1 8 5 2 9 6".ints().toIntArray()
        one(sample3, 10) shouldBe "6 3 0 7 4 1 8 5 2 9".ints().toIntArray()
        one(sample4, 10) shouldBe "9 2 5 8 1 4 7 0 3 6".ints().toIntArray()
        one(input).indexOf(2019) shouldBe 7545
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(input) shouldBe 0L
    }
}
