package io.parser.lora.converter

import io.parser.lora.utils.toHexFormatted
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow

/**
 * 스케일 값을 사용해 입력 값을 나누는 단순 변환기.
 */
object DivideConverter : LoraConverter {

    /**
     * 바이트 배열을 입력으로 받아 scale로 나눈 값을 반환합니다.
     *
     * @param bytes 입력 바이트 배열.
     * @param scale 나눌 스케일 값.
     * @return 나눈 결과값 [BigDecimal].
     */
    override fun convert(bytes: ByteArray, bitIndex: Int?, bitSize: Int?, scale: Int?, offset: BigDecimal?): BigDecimal {

        // 바이트 배열을 16진수 문자열로 변환 후 숫자로 변환
        val hexValue = bytes.joinToString("") { it.toHexFormatted() }
        val numericValue = hexValue.toLong(16)
        println("Hex Value: $hexValue")
        println("Numeric Value: $numericValue")

        // 스케일 적용
        return BigDecimal.valueOf(numericValue).movePointLeft(scale ?: 0).setScale(scale ?: 0)
    }

    override fun random(scale: Int?): BigDecimal {
        // scale + 2 자릿수의 랜덤 숫자 생성
        val digitCount = scale?.plus(2) ?: 2
        val randomValue = (10.0.pow(digitCount - 1).toInt()..<10.0.pow(digitCount).toInt()).random()

        // BigDecimal로 변환 후 소수점 설정
        return randomValue.toBigDecimal().movePointLeft(scale ?: 0).setScale(scale ?: 0, RoundingMode.HALF_DOWN)

    }
}