import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Cube of colors
 * */
class ColorCube {

    // colors present in the color cube
    var colors = HashSet<Color>()

    // get colors from the given image
    fun extractFromImage(image: BufferedImage) {
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                colors.add(Color(image.getRGB(x, y)))
            }
        }
    }

    // compute the ranges for the given color cube
    fun computeRanges(): Ranges {
        var ranges = Ranges()
        for (c in colors) ranges.update(c)
        return ranges
    }

    // compute the median value for the given component
    fun computeMedian(comp: Component): Int {

        // count the occurrences of each value for the selected channel
        var counters = IntArray(0x100)
        for (c in colors) ++counters[c.get(comp)]

        // find the median
        val target = colors.size / 2
        var count = 0
        for (i in counters.indices) {
            count += counters[i]
            if (count >= target) return i
        }
        return 0
    }

    // split the color cube into two other one
    fun split(comp: Component, cut: Int): Pair<ColorCube, ColorCube> {
        var lower  = ColorCube()
        var higher = ColorCube()

        // store the colors in the appropriate cube based on the cut threshold value
        for (c in colors) {
            if (c.get(comp) < cut) lower .colors.add(c)
            else                   higher.colors.add(c)
        }
        return Pair(lower, higher)
    }
}


class Ranges {

    // values of the range
    var min = IntArray(4) { 0xff }
    var max = IntArray(4) { 0x00 }

    fun getMin (comp: Component): Int = min[comp.ordinal]
    fun getMax (comp: Component): Int = max[comp.ordinal]
    fun getRange (comp: Component): Int = max[comp.ordinal] - min[comp.ordinal]

    // compare ranges to the given color
    fun update(color: Color) {
        val c = listOf(color.red, color.green, color.blue, color.alpha)
        for (i in 0..3) {
            min[i] = minOf(min[i], c[i])
            max[i] = maxOf(max[i], c[i])
        }
    }

    // select the component with the largest range
    fun largestRange(): Component {
        val list = IntArray(4) { i -> max[i] - min[i] }
        return when (list.indexOf(list.max())) {
            0 -> Component.Red
            1 -> Component.Green
            2 -> Component.Blue
            else -> Component.Alpha
        }
    }
}

// list of components
enum class Component {
    Red, Green, Blue, Alpha
}

// get the required component form the color
inline fun Color.get(comp: Component): Int {
    return when (comp) {
        Component.Red   -> red
        Component.Green -> green
        Component.Blue  -> blue
        else            -> alpha
    }
}