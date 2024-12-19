package io.parser.lora.manager

import kotlin.reflect.KClass

object LoraParserManager {
    private val parsers = mutableMapOf<KClass<*>, ParserMeta>()

    // 데이터 클래스와 파싱, 랜덤 규칙을 등록
    fun <T : Any> register(
        targetClass: KClass<T>,
        parser: (String, String) -> T,
        randomGenerator: (String?) -> T
    ) {
        parsers[targetClass] = ParserMeta(parser, randomGenerator)
    }

    // 데이터 클래스 파싱
    fun <T : Any> parse(devEui: String, log: String, targetClass: KClass<T>): T {
        val meta = parsers[targetClass]
            ?: throw IllegalArgumentException("No parser registered for ${targetClass.simpleName}")
        @Suppress("UNCHECKED_CAST")
        return meta.parser(devEui, log) as T
    }

    // 데이터 클래스 랜덤 생성
    fun <T : Any> random(targetClass: KClass<T>, devEui: String? = null): T {
        val meta = parsers[targetClass]
            ?: throw IllegalArgumentException("No random generator registered for ${targetClass.simpleName}")
        @Suppress("UNCHECKED_CAST")
        return meta.randomGenerator(devEui) as T
    }

    private data class ParserMeta(
        val parser: (String, String) -> Any,
        val randomGenerator: (String?) -> Any
    )
}