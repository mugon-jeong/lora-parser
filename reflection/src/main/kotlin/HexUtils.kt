import java.util.*

object HexUtils {
    fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }
    fun List<Byte>.toHexString(): String = joinToString("") { "%02x".format(it) }

    fun String.hexStringToByteArray(): ByteArray {
        require(length % 2 == 0) { "Hex string must have an even length" }
        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }

    fun hexToLong(hex: String): Long = hex.toLong(16)

    fun base64ToByteArray(base64: String): ByteArray {
        return try {
            Base64.getDecoder().decode(base64)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid Base64 format: $base64")
        }
    }

    fun Byte.toHexFormatted(): String = String.format("%02x", this.toInt() and 0xFF)
}