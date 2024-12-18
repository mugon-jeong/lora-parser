package io.parser.lora.handler

import AnnotationHandler
import io.parser.lora.annotation.ParseStatus
import io.parser.lora.examples.status.GasAlarmStatus
import io.parser.lora.ByteParsable
import io.parser.lora.examples.status.SensorStatus
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
        val targetClass = param.type.classifier as? KClass<*> ?: throw IllegalArgumentException("Unsupported type")

        require(ByteParsable::class.java.isAssignableFrom(targetClass.java)) {
            "Class ${targetClass.simpleName} must implement ByteParsable"
        }

        return when (targetClass) {
            GasAlarmStatus::class -> GasAlarmStatus.fromBytes(rawBytes)
            SensorStatus::class -> SensorStatus.fromBytes(rawBytes)
            else -> throw IllegalArgumentException("Unsupported ByteParsable implementation: ${targetClass.simpleName}")
        }
    }
}