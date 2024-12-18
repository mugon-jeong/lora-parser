package io.parser.lora.registry

import AnnotationHandler
import io.parser.lora.handler.DevEUIHandler
import io.parser.lora.handler.FwVersionHandler
import io.parser.lora.handler.ParseEnumHandler
import io.parser.lora.handler.ParseHexHandler
import io.parser.lora.handler.ParseStatusHandler

object AnnotationHandlerRegistry {
    private val defaultHandlers = listOf(
        DevEUIHandler,
        ParseHexHandler,
        ParseEnumHandler,
        ParseStatusHandler(ParseStatusRegistry.registeredHandlers),
        FwVersionHandler,
    )

    private val customHandlers = mutableListOf<AnnotationHandler>()

    /**
     * 모든 핸들러를 반환
     */
    val handlers: List<AnnotationHandler>
        get() = defaultHandlers + customHandlers

    /**
     * 커스텀 핸들러를 등록
     */
    fun registerCustomHandler(handler: AnnotationHandler) {
        customHandlers.add(handler)
    }

    /**
     * 여러 커스텀 핸들러를 등록
     */
    fun registerCustomHandlers(handlers: List<AnnotationHandler>) {
        customHandlers.addAll(handlers)
    }
}