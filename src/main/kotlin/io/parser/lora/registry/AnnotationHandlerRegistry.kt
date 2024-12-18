package io.parser.lora.registry

import io.parser.lora.handler.DevEUIHandler
import io.parser.lora.handler.FwVersionHandler
import io.parser.lora.handler.ParseEnumHandler
import io.parser.lora.handler.ParseHexHandler
import io.parser.lora.handler.ParseStatusHandler

object AnnotationHandlerRegistry {
    val handlers = listOf(
        DevEUIHandler,
        ParseHexHandler,
        ParseEnumHandler,
        ParseStatusHandler,
        FwVersionHandler,
    )
}