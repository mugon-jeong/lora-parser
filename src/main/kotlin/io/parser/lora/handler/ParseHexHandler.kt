package io.parser.lora.handler

import AnnotationHandler
import io.parser.lora.annotation.ParseHex
import io.parser.lora.converter.handleParseHex
import kotlin.reflect.KClass
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
}