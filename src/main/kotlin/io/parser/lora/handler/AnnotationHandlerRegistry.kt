package io.parser.lora.handler

object AnnotationHandlerRegistry {
    val handlers = listOf(
        DevEUIHandler,
        ParseHexHandler,
        ParseEnumHandler,
        ParseStatusHandler,
        FwVersionHandler,
    )
}