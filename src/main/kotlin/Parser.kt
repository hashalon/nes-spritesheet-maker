/**
 * Color Cube used in Median-Cut Algorithm
 * http://joelcarlson.github.io/2016/01/15/median-cut/
 * */
import java.awt.image.BufferedImage
import java.awt.image.Raster
import java.nio.file.Path

// NES only support 4 colors per tile
const val NB_COLORS = 4

class Parser(val sources: Array<Path>, val mode16: Boolean) {

    // list of images to process
    private var images = ArrayList<BufferedImage>()

    fun readSources () {

    }

    // process the images sources to extract tiles
    fun extractTiles() {

        // store list of tiles identified by hash value
        //var tiles = HashMap<Int, BufferedImage>()

        // for each image in the sources
        for (image in images) {
            when (image.type) {
                // for each type of image, deduce the most appropriate method to read tiles
                BufferedImage.TYPE_BYTE_INDEXED,
                BufferedImage.TYPE_BYTE_BINARY -> {
                    var buf = IntArray(1)
                    processPixels(image, buf) { pixel -> (pixel[0] % NB_COLORS) as Byte }
                }
                BufferedImage.TYPE_BYTE_GRAY,
                BufferedImage.TYPE_USHORT_GRAY -> {
                    val range = 0x100 / NB_COLORS
                    var buf = IntArray(1)
                    processPixels(image, buf) { pixel -> (pixel[0] rndDiv range) as Byte }
                }
            }
        }
    }




}

// iterate over each pixel of the image to deduce the
fun processPixels(image: BufferedImage, buf: IntArray, proc: (IntArray) -> Byte): Array<ByteArray> {
    var imageOut = Array(image.numXTiles * image.numYTiles) { _ -> ByteArray(0) }

    // iterate over each tile of the image
    var i = 0
    for (ix in 0 until image.numXTiles) {
        for (iy in 0 until image.numYTiles) {
            val tile = image.getTile(ix, iy)
            var tileOut = ByteArray(tile.width * tile.height)

            // iterate over each pixel in the tile
            var j = 0
            for (jx in 0 until tile.width) {
                for (jy in 0 until tile.height) {
                    tileOut[j++] = proc(tile.getPixel(jx, jy, buf))
                }
            }
            imageOut[i++] = tileOut
        }
    }
    return imageOut
}

// rounding of integer division
inline infix fun Int.rndDiv (b: Int): Int = (this + b - 1) / b


// process the tiles of a given image
fun processTilesInImage(image: BufferedImage, action: (Raster) -> ByteArray): Array<ByteArray> {
    // prepare a simple output buffer
    var out = Array<ByteArray>(image.numXTiles * image.numYTiles) { _ -> ByteArray(0) }

    // iterate over each tile of the whole image
    var i = 0
    for (x in 0 until image.numXTiles) {
        for (y in 0 until image.numYTiles) {
            out[i++] = action(image.getTile(x, y))
        }
    }
    return out
}

// process the pixels of a given tile
fun processPixelsInTile(tile: Raster, buf: IntArray, action: (IntArray) -> Byte): ByteArray {
    var out = ByteArray(tile.width * tile.height)

    // iterate over each pixel of the whole tile
    var i = 0
    for (x in 0 until  tile.width) {
        for (y in 0 until tile.height) {
            out[i++] = action(tile.getPixel(x, y, buf))
        }
    }
    return out
}