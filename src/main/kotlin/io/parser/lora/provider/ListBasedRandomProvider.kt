package io.parser.lora.provider

class ListBasedRandomProvider<T>(private val values: List<T>) : RandomProvider<T> {
    override fun getRandomValue(): T {
        require(values.isNotEmpty()) { "Value list must not be empty" }
        return values.random()
    }
}