package io.parser.lora.converter

import java.math.BigDecimal
import java.math.BigDecimal.valueOf

object ByteToUnsignedNumParser : LoraConverter {

    /**
     * 바이트 배열을 무부호 숫자로 변환하고 scale 및 offset을 적용합니다.
     *
     * @param bytes 입력 바이트 배열.
     * @param scale 스케일 값을 곱합니다.
     * @param offset 오프셋 값을 더합니다.
     * @return 계산된 결과값 (BigDecimal).
     */
    override fun convert(bytes: ByteArray, bitIndex: Int?, bitSize: Int?, scale: Int?, offset: BigDecimal?): Any {
        // 바이트 배열을 16진수 문자열로 변환
        val hex = bytes.joinToString("") { "%02x".format(it) }

        // 16진수 문자열을 무부호 Long 값으로 변환
        val decimalValue = hex.toLong(16)

        // scale 적용 후 offset 추가
        val parsedValue = valueOf(decimalValue)
        return (parsedValue.setScale(scale?:0)).add(offset)
    }

    override fun random(scale: Int?): Any {
        val randomValue = (1..UInt.MAX_VALUE.toLong()).random()
        return BigDecimal(randomValue).setScale(scale?:0)
    }
}