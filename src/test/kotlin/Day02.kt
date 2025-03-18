import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day02 {
    private val sample = """
        1,9,10,3,2,3,11,0,99,30,40,50
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input[0].split(",").map { it.toInt() }.toIntArray()

    private fun one(input: List<String>, noun: Int, verb: Int) = calc(parse(input), noun, verb)

    private fun two(input: List<String>): Int {
        val prog = parse(input)
        for (noun in 0..99) {
            for (verb in 0..99) {
                if (calc(prog.copyOf(), noun, verb) == 19690720) {
                    return 100 * noun + verb
                }
            }
        }
        error("No solution for input")
    }

    private fun calc(prog: IntArray, noun: Int, verb: Int): Int {
        prog[1] = noun
        prog[2] = verb
        var i = 0
        while (true) {
            when (prog[i]) {
                1 -> {
                    prog[prog[i + 3]] = prog[prog[i + 1]] + prog[prog[i + 2]]
                    i += 4
                }
                2 -> {
                    prog[prog[i + 3]] = prog[prog[i + 1]] * prog[prog[i + 2]]
                    i += 4
                }
                99 -> break
            }
        }
        return prog[0]
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample, 9, 10) shouldBe 3500
        one(input, 12, 2) shouldBe 3895705
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input) shouldBe 6417
    }
}
