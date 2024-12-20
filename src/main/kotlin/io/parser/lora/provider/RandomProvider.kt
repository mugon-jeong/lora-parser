package io.parser.lora.provider

interface RandomProvider<T> {
    fun getRandomValue(): T
}