package converter

import utils.toHexFormatted
import java.math.BigDecimal

/**
 * 스케일 값을 사용해 입력 값을 곱하는 단순 변환기.
 */
object MultiplyConverter {

    /**
     * 바이트 배열을 입력으로 받아 scale로 곱한 값을 반환합니다.
     *
     * @param bytes 입력 바이트 배열.
     * @param scale 곱할 스케일 값.
     * @return 곱한 결과값 [BigDecimal].
     */
    fun convert(bytes: ByteArray, scale: Double): BigDecimal {
        require(scale != 0.0) { "Scale cannot be zero" }

        // 바이트 배열을 16진수 문자열로 변환 후 숫자로 변환
        val hexValue = bytes.joinToString("") { it.toHexFormatted() }
        val numericValue = hexValue.toLong(16)

        // 스케일 적용
        return BigDecimal.valueOf(numericValue).multiply(BigDecimal.valueOf(scale))
    }
}