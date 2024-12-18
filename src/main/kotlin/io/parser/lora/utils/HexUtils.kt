package io.parser.lora.utils

import java.util.*

object HexUtils {

    /**
     * 16진수 문자열을 Long 타입으로 변환합니다.
     *
     * @param hex 16진수 문자열
     * @return 변환된 Long 값
     */
    fun hexToLong(hex: String): Long = hex.toLong(16)

    /**
     * Base64 문자열을 ByteArray로 변환합니다.
     *
     * @param base64 Base64 인코딩된 문자열
     * @return 변환된 ByteArray
     * @throws IllegalArgumentException Base64 형식이 잘못된 경우 예외를 발생시킵니다.
     */
    fun base64ToByteArray(base64: String): ByteArray {
        return try {
            Base64.getDecoder().decode(base64)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid Base64 format: $base64")
        }
    }

    /**
     * 16진수 문자열을 Float 타입으로 변환합니다.
     * (IEEE 754 표준을 따름)
     *
     * @param hex 16진수 문자열
     * @return 변환된 Float 값
     */
    fun hexToDecimal(hex: String): Float {
        // 16진수 문자열을 10진수로 변환
        val decimal = hex.toLong(16)

        // 10진수 값을 Float로 변환 (IEEE 754 표준을 따름)
        return Float.fromBits(decimal.toInt())
    }
}

/**
 * Byte 값을 16진수 형식의 문자열로 변환합니다.
 *
 * @return 변환된 16진수 문자열
 */
fun Byte.toHexFormatted(): String = String.format("%02x", this.toInt() and 0xFF)

/**
 * ByteArray를 16진수 형식의 문자열로 변환합니다.
 *
 * @return 변환된 16진수 문자열
 */
fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }

/**
 * Byte 리스트를 16진수 형식의 문자열로 변환합니다.
 *
 * @return 변환된 16진수 문자열
 */
fun List<Byte>.toHexString(): String = joinToString("") { "%02x".format(it) }

/**
 * 16진수 문자열을 ByteArray로 변환합니다.
 *
 * @return 변환된 ByteArray
 */
fun String.hexToByteArray(): ByteArray =
    chunked(2).map { it.toInt(16).toByte() }.toByteArray()

fun ByteArray.toBase64(): String = Base64.getEncoder().encodeToString(this)