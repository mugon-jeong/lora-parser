package io.parser.lora

import AnnotationHandler
import io.parser.lora.handler.AnnotationHandlerRegistry
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.reflections.Reflections

class AnnotationHandlerRegistryTest {
    @Test
    fun `all handlers should be registered in the registry`() {
        // 1. 패키지 스캔 - handler 패키지 내 AnnotationHandler 구현체 찾기
        val reflections = Reflections("io.parser.lora.handler") // 'handler' 패키지 경로 스캔
        val handlerClasses = reflections.getSubTypesOf(AnnotationHandler::class.java).map { it.kotlin } // AnnotationHandler 구현체 탐색

        // 2. 실제 레지스트리에 등록된 핸들러 목록
        val registeredHandlers = AnnotationHandlerRegistry.handlers.map { it::class }

        // 3. 모든 핸들러가 레지스트리에 존재하는지 검증
        handlerClasses.forEach { handlerClass ->
            assertTrue(
                registeredHandlers.contains(handlerClass),
                "Handler ${handlerClass.simpleName} is not registered in AnnotationHandlerRegistry"
            )
        }
    }
}