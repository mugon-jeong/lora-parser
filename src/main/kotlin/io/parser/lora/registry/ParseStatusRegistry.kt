package io.parser.lora.registry

import io.parser.lora.ByteParsable
import kotlin.reflect.KClass

object ParseStatusRegistry {
    val registeredHandlers = mutableMapOf<KClass<*>, (List<Byte>) -> ByteParsable>()

    /**
     * 핸들러를 수동으로 등록
     */
    fun <T : Any> registerHandler(targetClass: KClass<out T>, handler: (List<Byte>) -> ByteParsable) {
        registeredHandlers[targetClass] = handler
    }

    /**
     * 등록된 핸들러 가져오기
     */
    fun <T : ByteParsable> getHandler(targetClass: KClass<T>): ((List<Byte>) -> ByteParsable)? {
        return registeredHandlers[targetClass]
    }

    /**
     * ParseStatusHandler와 연동하여 핸들러를 등록
     *
     * @param handlers 핸들러 목록을 제공하는 람다
     */
    inline fun <reified T : ByteParsable> parseStatusRegister(
        handlers: () -> List<Pair<KClass<out T>, (List<Byte>) -> T>>
    ) {
        handlers().forEach { (klass, handler) ->
            // 핸들러 등록
            registerHandler(klass, handler as (List<Byte>) -> ByteParsable)
        }
    }
}