package io.parser.lora.converter

import io.parser.lora.annotation.ParseHex
import io.parser.lora.enums.HexConverterType
import io.parser.lora.utils.HexUtils
import io.parser.lora.utils.toHexFormatted
import java.math.BigDecimal
import kotlin.random.Random
import kotlin.reflect.KClass

fun handleParseHex(data: ByteArray, annotation: ParseHex, returnType: KClass<*>?): Any {
    println("data: $data")
    val rawBytes = data.slice(annotation.byteStart..annotation.byteEnd).toByteArray()
    val scale = annotation.scale

    // converter 값에 따라 적절한 변환기 적용
    return when (annotation.converter) {
        HexConverterType.UNSIGNED -> ByteToUnsignedNumParser.convert(rawBytes, scale = scale, offset = BigDecimal.ZERO)
        HexConverterType.SIGNED -> ByteToSignedNumberParser.convert(rawBytes, scale = scale, offset = BigDecimal.ZERO)
        HexConverterType.IEEE754 -> ByteToIEEE754Converter.convert(rawBytes)
        HexConverterType.BIT -> BitToUnsignedNumParser.convert(rawBytes, 0, rawBytes.size * 8)
        HexConverterType.DIVIDE -> DivideConverter.convert(rawBytes, scale = scale)
        HexConverterType.MULTIPLY -> MultiplyConverter.convert(rawBytes, scale = scale)
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

fun handleRandomParseHex(annotation: ParseHex, returnType: KClass<*>?): Any {
    val scale = annotation.scale
    // converter 값에 따라 적절한 변환기 적용
    return when (annotation.converter) {
        HexConverterType.UNSIGNED -> ByteToUnsignedNumParser.random(scale = scale)
        HexConverterType.SIGNED -> ByteToSignedNumberParser.random(scale = scale)
        HexConverterType.IEEE754 -> ByteToIEEE754Converter.random(scale = scale)
        HexConverterType.BIT -> BitToUnsignedNumParser.random(scale = scale)
        HexConverterType.DIVIDE -> DivideConverter.random(scale = scale)
        HexConverterType.MULTIPLY -> MultiplyConverter.random(scale = scale)
        HexConverterType.DEFAULT -> {
            when (returnType) {
                Long::class -> Random.nextLong(0, 256)
                Int::class -> Random.nextInt(0, 65536)
                Float::class -> (1..100).random().toFloat() / scale.toFloat()
                Double::class -> (1..100).random().toDouble() / scale.toDouble()
                BigDecimal::class -> BigDecimal.valueOf((1..100).random().toDouble())
                else -> throw IllegalArgumentException("Unsupported type: $returnType")
            }
        }
    }
}