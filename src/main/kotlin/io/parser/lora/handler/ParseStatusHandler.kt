package io.parser.lora.handler

import AnnotationHandler
import io.parser.lora.ByteParsable
import io.parser.lora.annotation.ParseStatus
import io.parser.lora.registry.ParseStatusRegistry
import java.nio.ByteBuffer
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

object ParseStatusHandler : AnnotationHandler {

    override fun canHandle(property: KProperty<*>, param: KParameter): Boolean {
        return property.findAnnotation<ParseStatus>() != null || param.findAnnotation<ParseStatus>() != null
    }

    override fun handle(
        property: KProperty<*>,
        param: KParameter,
        data: ByteArray,
        devEuiBytes: ByteArray
    ): Any {
        val annotation = property.findAnnotation<ParseStatus>() ?: param.findAnnotation<ParseStatus>()!!
        val rawBytes = data.slice(annotation.byteStart..annotation.byteEnd)
        val targetClass = param.type.classifier as? KClass<ByteParsable>
            ?: throw IllegalArgumentException("Unsupported type")

        // ParseStatusRegistry에서 핸들러를 가져옴
        val handler = ParseStatusRegistry.getHandler(targetClass)
            ?: throw IllegalArgumentException("No handler registered for ${targetClass.simpleName}")

        return handler(rawBytes)
    }

    override fun handleDummy(property: KProperty<*>, param: KParameter, buffer: ByteBuffer, value: Any) {
        val annotation = property.findAnnotation<ParseStatus>()!!
        val byteStart = annotation.byteStart
        buffer.position(byteStart)

        if (value is ByteParsable) {
            buffer.put(value.toByte())
        } else {
            throw IllegalArgumentException("ParseStatus can only be used with ByteParsable types")
        }
    }
}