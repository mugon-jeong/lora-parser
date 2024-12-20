package io.parser.lora.registry

import io.parser.lora.provider.ListBasedRandomProvider
import io.parser.lora.provider.RandomProvider
import io.parser.lora.provider.RangeBasedRandomProvider

object RandomProviderRegistry {
    private val providers = mutableMapOf<String, RandomProvider<*>>()

    /**
     * 단일 값 기반 프로바이더 등록
     */
    fun <T> registerProvider(key: String, provider: RandomProvider<T>) {
        require(key.isNotBlank()) { "Key must not be blank." }
        providers[key] = provider
    }

    /**
     * 목록 기반 프로바이더 등록
     */
    fun <T> registerProvider(key: String, values: List<T>) {
        require(key.isNotBlank()) { "Key must not be blank." }
        require(values.isNotEmpty()) { "Values list must not be empty." }
        providers[key] = ListBasedRandomProvider(values)
    }

    /**
     * 범위 기반 프로바이더 등록
     */
    fun registerProvider(key: String, range: IntRange) {
        require(key.isNotBlank()) { "Key must not be blank." }
        providers[key] = RangeBasedRandomProvider(range)
    }

    /**
     * 키에 해당하는 RandomProvider 반환
     */
    fun <T> getProvider(key: String): RandomProvider<T>? {
        @Suppress("UNCHECKED_CAST")
        return providers[key] as? RandomProvider<T>
    }

    /**
     * 키에 해당하는 랜덤 값을 반환
     */
    fun <T> getRandomValue(key: String): T? {
        val provider = getProvider<T>(key)
        return provider?.getRandomValue()
    }

    /**
     * 현재 등록된 모든 프로바이더 키 반환
     */
    fun getRegisteredKeys(): Set<String> {
        return providers.keys
    }

    /**
     * 특정 키의 프로바이더 존재 여부 확인
     */
    fun hasProvider(key: String): Boolean {
        return key in providers
    }

    /**
     * 특정 키로 등록된 프로바이더 제거
     */
    fun removeProvider(key: String) {
        require(key.isNotBlank()) { "Key must not be blank." }
        providers.remove(key)
    }

    /**
     * 모든 프로바이더 초기화
     */
    fun clear() {
        providers.clear()
    }
}