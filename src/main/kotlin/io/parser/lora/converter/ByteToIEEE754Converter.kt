package io.parser.lora.converter

import java.math.BigDecimal
import java.nio.ByteBuffer

/**
 * IEEE 754 표준의 바이트 배열을 Float 값으로 변환하고,
 * 이를 BigDecimal로 반환하는 컨버터.
 */
object ByteToIEEE754Converter: LoraConverter {

    /**
     * 주어진 바이트 배열을 IEEE 754 표준에 따라 Float로 변환 후 BigDecimal로 반환합니다.
     *
     * @param bytes 변환할 4바이트 크기의 배열.
     * @return 변환된 [BigDecimal] 값.
     * @throws IllegalArgumentException 바이트 배열의 길이가 4가 아니면 예외를 발생시킵니다.
     */
    override fun convert(bytes: ByteArray, bitIndex: Int?, bitSize: Int?, scale: Int?, offset: BigDecimal?): Any {
        require(bytes.size == 4) { "Input byte array must be exactly 4 bytes long" }

        // ByteBuffer를 사용해 바이트를 Float로 변환
        val floatValue = ByteBuffer.wrap(bytes).float

        // Float 값을 BigDecimal로 변환해 반환
        return BigDecimal.valueOf(floatValue.toDouble())
    }

    override fun random(scale: Int?): Any {
        return Float.fromBits((1..Int.MAX_VALUE).random())
    }
}