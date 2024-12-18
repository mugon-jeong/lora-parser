package io.parser.lora.handler

import AnnotationHandler
import io.parser.lora.annotation.DevEUI
import io.parser.lora.utils.toHexString
import kotlin.reflect.full.findAnnotation

object DevEUIHandler : AnnotationHandler {
    override fun canHandle(property: kotlin.reflect.KProperty<*>, param: kotlin.reflect.KParameter): Boolean {
        return property.findAnnotation<DevEUI>() != null || param.findAnnotation<DevEUI>() != null
    }

    override fun handle(
        property: kotlin.reflect.KProperty<*>,
        param: kotlin.reflect.KParameter,
        data: ByteArray,
        devEuiBytes: ByteArray
    ): Any {
        return devEuiBytes.toHexString()
    }
}