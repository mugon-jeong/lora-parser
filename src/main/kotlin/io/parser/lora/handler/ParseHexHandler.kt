package io.parser.lora.handler

import AnnotationHandler
import io.parser.lora.annotation.ParseHex
import io.parser.lora.converter.handleParseHex
import java.math.BigDecimal
import java.nio.ByteBuffer
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

object ParseHexHandler : AnnotationHandler {
    override fun canHandle(property: kotlin.reflect.KProperty<*>, param: kotlin.reflect.KParameter): Boolean {
        return property.findAnnotation<ParseHex>() != null || param.findAnnotation<ParseHex>() != null
    }

    override fun handle(
        property: kotlin.reflect.KProperty<*>,
        param: kotlin.reflect.KParameter,
        data: ByteArray,
        devEuiBytes: ByteArray
    ): Any {
        val annotation = property.findAnnotation<ParseHex>() ?: param.findAnnotation<ParseHex>()!!
        return handleParseHex(data, annotation, param.type.classifier as? KClass<*>)
    }

    override fun handleDummy(property: KProperty<*>, param: KParameter, buffer: ByteBuffer, value: Any) {
        val annotation = property.findAnnotation<ParseHex>()!!
        val byteStart = annotation.byteStart
        val scale = annotation.scale
        buffer.position(byteStart)

        when (property.returnType.classifier as KClass<*>) {
            Long::class -> buffer.put((value as Long).toByte())
            Int::class -> buffer.putShort((value as Int).toShort())
            BigDecimal::class -> buffer.putShort(
                ((value as BigDecimal) * BigDecimal(scale)).toInt().toShort()
            )
            else -> throw IllegalArgumentException("Unsupported type for ParseHex: ${property.name}")
        }
    }
}