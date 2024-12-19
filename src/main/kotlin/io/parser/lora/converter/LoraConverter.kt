package io.parser.lora.converter

import java.math.BigDecimal

interface LoraConverter {
    fun convert(bytes: ByteArray, bitIndex: Int? = null, bitSize: Int? = null, scale: Int? = 0, offset: BigDecimal? = null): Any

    fun random(scale: Int? = 0): Any
}