package handler

import AnnotationHandler
import annotation.FwVersion
import utils.parseFwVersion
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
}