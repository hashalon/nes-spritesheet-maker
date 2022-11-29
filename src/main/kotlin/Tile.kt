/**
 * Represent a tile that can be used to generate the final CHR file
 * */
import java.io.DataOutputStream

const val BYTE_SIZE = 8

class Tile(private val data: ByteArray) {

    // write the bits into the writer
    fun write(stream: DataOutputStream) {
        val size = data.size / BYTE_SIZE
        var bitsLow  = ByteArray(size)
        var bitsHigh = ByteArray(size)
        for (i in 0 until size) bitsLow [i] = makeByte(i * BYTE_SIZE, 0b01)
        for (i in 0 until size) bitsHigh[i] = makeByte(i * BYTE_SIZE, 0b10)
        stream.write(bitsLow )
        stream.write(bitsHigh)
    }

    // read multiple pixels to construct a single byte of the tile data
    private inline fun makeByte(pos: Int, mask: Int): Byte {
        var buffer = 0
        for (i in 0 until BYTE_SIZE) {
            if (data[pos + i] as Int and mask != 0) {
                buffer = buffer or (0b1 shl i)
            }
        }
        return buffer as Byte
    }

}