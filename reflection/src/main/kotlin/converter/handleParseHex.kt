package converter

import annotation.ParseHex
import enums.HexConverterType
import utils.HexUtils
import utils.toHexFormatted
import java.math.BigDecimal
import kotlin.reflect.KClass

fun handleParseHex(data: ByteArray, annotation: ParseHex, returnType: KClass<*>?): Any {
    val rawBytes = data.slice(annotation.byteStart..annotation.byteEnd).toByteArray()
    val scale = BigDecimal.valueOf(annotation.scale)

    // converter 값에 따라 적절한 변환기 적용
    return when (annotation.converter) {
        HexConverterType.UNSIGNED -> ByteToUnsignedNumParser.convert(rawBytes, scale, BigDecimal.ZERO)
        HexConverterType.SIGNED -> ByteToSignedNumberParser.convert(rawBytes, scale, BigDecimal.ZERO)
        HexConverterType.IEEE754 -> ByteToIEEE754Converter.convert(rawBytes)
        HexConverterType.BIT -> BitToUnsignedNumParser.convert(rawBytes, 0, rawBytes.size * 8)
        HexConverterType.DIVIDE -> DivideConverter.convert(rawBytes, annotation.scale)
        HexConverterType.MULTIPLY -> MultiplyConverter.convert(rawBytes, annotation.scale)
        HexConverterType.DEFAULT -> {
            // 기존 로직 사용
            val hexValue = rawBytes.joinToString("") { it.toHexFormatted() }
            when (returnType) {
                Long::class -> HexUtils.hexToLong(hexValue)
                Int::class -> HexUtils.hexToLong(hexValue).toInt()
                Float::class -> HexUtils.hexToDecimal(hexValue)
                Double::class -> HexUtils.hexToLong(hexValue)
                else -> throw IllegalArgumentException("Unsupported type: $returnType")
            }
        }
    }
}