package io.parser.lora.handler

import AnnotationHandler
import io.parser.lora.annotation.ParseHex
import io.parser.lora.converter.handleParseHex
import io.parser.lora.converter.handleRandomParseHex
import java.math.BigDecimal
import java.nio.ByteBuffer
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

object ParseHexHandler : AnnotationHandler {
    override fun canHandle(property: KProperty<*>, param: KParameter): Boolean {
        return property.findAnnotation<ParseHex>() != null || param.findAnnotation<ParseHex>() != null
    }

    override fun handle(
        property: KProperty<*>,
        param: KParameter,
        data: ByteArray,
        devEuiBytes: ByteArray
    ): Any {
        val annotation = property.findAnnotation<ParseHex>() ?: param.findAnnotation<ParseHex>()!!
        return handleParseHex(data, annotation, param.type.classifier as? KClass<*>)
    }

    override fun random(
        property: KProperty<*>,
        param: KParameter,
        devEui: String,
        customRandomProvider: (KClass<*>) -> Any?
    ): Any {
        val annotation = property.findAnnotation<ParseHex>() ?: param.findAnnotation<ParseHex>()!!
        return handleRandomParseHex(annotation, param.type.classifier as? KClass<*>)
    }

    override fun handleDummy(property: KProperty<*>, param: KParameter, buffer: ByteBuffer, value: Any) {
        val annotation = property.findAnnotation<ParseHex>()!!
        val byteStart = annotation.byteStart
        val byteEnd = annotation.byteEnd
        val scale = annotation.scale
        val byteLength = byteEnd - byteStart + 1 // 저장할 바이트 길이
        buffer.position(byteStart)

        val byteArray = when (property.returnType.classifier as KClass<*>) {
            Long::class -> convertNumberToByteArray((value as Long), byteLength)
            Int::class -> convertNumberToByteArray((value as Int).toLong(), byteLength)
            BigDecimal::class -> convertBigDecimalToByteArray(value as BigDecimal, scale, byteLength)
            else -> throw IllegalArgumentException("Unsupported type for ParseHex: ${property.name}")
        }

        buffer.put(byteArray)
        println("16진수로 변환된 값: ${byteArray.joinToString(" ") { String.format("%02X", it) }}")
    }

    private fun convertNumberToByteArray(value: Long, byteLength: Int): ByteArray {
        val valueBytes = ByteBuffer.allocate(Long.SIZE_BYTES).putLong(value).array()
        require(valueBytes.size >= byteLength) {
            "Value exceeds the allocated byte range (${valueBytes.size} > $byteLength)"
        }

        // 필요한 크기만큼 바이트 배열 패딩
        val paddedArray = ByteArray(byteLength) { 0 }
        System.arraycopy(valueBytes, valueBytes.size - byteLength, paddedArray, 0, byteLength)
        return paddedArray
    }

    private fun convertBigDecimalToByteArray(value: BigDecimal, scale: Int, byteLength: Int): ByteArray {
        val scaledValue = value.movePointRight(scale).toBigInteger() // scale 적용 후 정수 변환
        val byteArray = scaledValue.toByteArray() // 정수를 바이트 배열로 변환

        require(byteArray.size <= byteLength) {
            "Value exceeds the allocated byte range (${byteArray.size} > $byteLength)"
        }

        // 필요한 크기만큼 바이트 배열 패딩
        val paddedArray = ByteArray(byteLength) { 0 }
        System.arraycopy(byteArray, 0, paddedArray, byteLength - byteArray.size, byteArray.size)
        return paddedArray
    }
}