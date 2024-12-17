package converter

import java.math.BigDecimal
import kotlin.math.pow

/**
 * 바이트 배열을 부호 있는 숫자로 변환한 후,
 * 스케일 및 오프셋을 적용하는 컨버터.
 */
object ByteToSignedNumberParser {

    /**
     * 바이트 배열을 부호 있는 숫자로 파싱하고, 스케일 및 오프셋을 적용합니다.
     *
     * @param bytes 변환할 바이트 배열.
     * @param scale 곱할 스케일 값 [BigDecimal].
     * @param offset 더할 오프셋 값 [BigDecimal].
     * @return 스케일 및 오프셋이 적용된 [BigDecimal] 값.
     */
    fun convert(bytes: ByteArray, scale: BigDecimal, offset: BigDecimal): BigDecimal {
        val hex = bytes.joinToString("") { "%02x".format(it) } // 바이트 배열을 16진수 문자열로 변환
        val byteLen = hex.length / 2 // 바이트 길이
        val bitLen = byteLen * 8 - 1 // 부호 비트 제외한 비트 길이
        val maxValue = (2.0.pow(bitLen) - 1).toLong() // 최대값 계산
        var decimalValue = hex.toLong(16) // 16진수 문자열을 Long 값으로 변환

        // 부호 비트를 고려한 값 보정
        if (decimalValue > maxValue) {
            decimalValue -= 1L shl (hex.length * 4)
        }

        val parsedValue = BigDecimal.valueOf(decimalValue) // Long 값을 BigDecimal로 변환
        return (parsedValue.multiply(scale)).add(offset) // 스케일과 오프셋 적용
    }
}