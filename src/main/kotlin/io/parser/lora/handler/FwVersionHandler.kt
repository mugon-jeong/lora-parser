package io.parser.lora.handler

import AnnotationHandler
import io.parser.lora.annotation.FwVersion
import io.parser.lora.utils.parseFwVersion
import io.parser.lora.utils.parseFwVersionToShort
import java.nio.ByteBuffer
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

object FwVersionHandler : AnnotationHandler {
    override fun canHandle(property: KProperty<*>, param: KParameter): Boolean {
        return property.findAnnotation<FwVersion>() != null || param.findAnnotation<FwVersion>() != null
    }

    override fun handle(property: KProperty<*>, param: KParameter, data: ByteArray, devEuiBytes: ByteArray): Any? {
        val annotation = property.findAnnotation<FwVersion>() ?: param.findAnnotation<FwVersion>()!!
        val rawBytes = data.slice(annotation.byteStart..annotation.byteEnd)
        return parseFwVersion(rawBytes)
    }

    override fun random(
        property: KProperty<*>,
        param: KParameter,
        devEui: String,
        customRandomProvider: (KClass<*>) -> Any?
    ): Any {
        return "V${Random.nextInt(1, 10)}.${Random.nextInt(0, 10)}.${Random.nextInt(0, 100)}"
    }

    override fun handleDummy(property: KProperty<*>, param: KParameter, buffer: ByteBuffer, value: Any) {
        val annotation = property.findAnnotation<FwVersion>()!!
        val byteStart = annotation.byteStart
        buffer.position(byteStart)

        if (value is String) {
            buffer.putShort(parseFwVersionToShort(value))
        } else {
            throw IllegalArgumentException("FwVersion can only be used with String type")
        }
    }
}