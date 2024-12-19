package io.parser.lora.handler

import AnnotationHandler
import io.parser.lora.annotation.DevEUI
import io.parser.lora.utils.toHexString
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

object DevEUIHandler : AnnotationHandler {
    override fun canHandle(property: KProperty<*>, param: KParameter): Boolean {
        return property.findAnnotation<DevEUI>() != null || param.findAnnotation<DevEUI>() != null
    }


    override fun handle(
        property: KProperty<*>,
        param: KParameter,
        data: ByteArray,
        devEuiBytes: ByteArray
    ): Any {
        return devEuiBytes.toHexString()
    }

    override fun random(property: KProperty<*>, param: KParameter, devEui: String, customRandomProvider: (KClass<*>) -> Any?): Any {
        return devEui
    }
}