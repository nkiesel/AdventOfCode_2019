import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day08 {
    private fun parse(input: List<String>): List<List<String>> {
        return input.joinToString("").chunked(25).chunked(6)
    }

    private fun one(input: List<String>): Int {
        val layers = parse(input)
        fun count(layer: List<String>, pixel: Char) = layer.sumOf { it.count { it == pixel } }
        return layers.minBy { count(it, '0') }.let { count(it, '1') * count(it, '2') }
    }

    private fun two(input: List<String>): String {
        val layers = parse(input)
        val picture = layers[0].let { CharArea(it[0].length, it.size, '.') }
        picture.tiles().forEach { p ->
            for (layer in layers) {
                picture[p] = when (layer[p.y][p.x]) {
                    '0' -> '#'
                    '1' -> ' '
                    else -> continue
                }
                break
            }
        }
        picture.png()
        return "YGRYZ"
    }

    @Test
    fun testOne(input: List<String>) {
        one(input) shouldBe 2684
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input) shouldBe "YGRYZ"
    }
}
