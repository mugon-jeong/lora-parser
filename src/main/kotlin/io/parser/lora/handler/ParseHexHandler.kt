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

        when (property.returnType.classifier as KClass<*>) {
            Long::class -> buffer.put((value as Long).toByte())
            Int::class -> buffer.putShort((value as Int).toShort())
            BigDecimal::class -> {
                val scaledValue = (value as BigDecimal).movePointRight(scale).toBigInteger() // scale 적용 후 정수 변환
                val byteArray = scaledValue.toByteArray() // 정수를 바이트 배열로 변환

                require(byteArray.size <= byteLength) {
                    "Value exceeds the allocated byte range (${byteArray.size} > $byteLength)"
                }

                // 버퍼에 값 저장 (초기 바이트를 0으로 채우고 뒤에서부터 값 삽입)
                val paddedArray = ByteArray(byteLength) { 0 }
                System.arraycopy(byteArray, 0, paddedArray, byteLength - byteArray.size, byteArray.size)
                buffer.put(paddedArray)
            }
            else -> throw IllegalArgumentException("Unsupported type for ParseHex: ${property.name}")
        }
    }
}