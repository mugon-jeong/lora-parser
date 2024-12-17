package handler

import AnnotationHandler
import annotation.ParseEnum
import enums.BitEnum
import utils.toHexFormatted
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

object ParseEnumHandler : AnnotationHandler {
    override fun canHandle(property: kotlin.reflect.KProperty<*>, param: kotlin.reflect.KParameter): Boolean {
        return property.findAnnotation<ParseEnum>() != null || param.findAnnotation<ParseEnum>() != null
    }

    override fun handle(
        property: kotlin.reflect.KProperty<*>,
        param: kotlin.reflect.KParameter,
        data: ByteArray,
        devEuiBytes: ByteArray
    ): Any {
        val annotation = property.findAnnotation<ParseEnum>() ?: param.findAnnotation<ParseEnum>()!!
        val rawBytes = data.slice(annotation.byteStart..annotation.byteEnd)
        val hexValue = rawBytes.joinToString("") { it.toHexFormatted() }
        val targetClass = param.type.classifier as? KClass<*>
            ?: throw IllegalArgumentException("Unsupported property '${property.name}'")

        require(BitEnum::class.java.isAssignableFrom(targetClass.java)) {
            "Enum Class ${targetClass.simpleName} must implement BitEnum"
        }

        val bitValue = if (annotation.bitRange.isNotEmpty()) {
            require(annotation.bitRange.size == 2) { "bitRange must contain exactly 2 elements: [start, end]" }
            hexValue.substring(annotation.bitRange[0], annotation.bitRange[1]).toInt(16)
        } else {
            hexValue.toInt(16)
        }

        val enumConstants = targetClass.java.enumConstants as Array<BitEnum>
        return enumConstants.find { it.bit == bitValue } ?: throw IllegalArgumentException("Invalid bit value: $bitValue")
    }
}