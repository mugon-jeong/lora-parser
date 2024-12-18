package io.parser.lora.examples.sensor

import io.parser.lora.Dummy
import io.parser.lora.annotation.ParseEnum
import io.parser.lora.annotation.ParseHex
import io.parser.lora.annotation.ParseStatus
import io.parser.lora.enums.HexConverterType
import io.parser.lora.examples.enums.GasMsgType
import io.parser.lora.examples.status.GasAlarmStatus
import io.parser.lora.LoraParsable
import io.parser.lora.annotation.DevEUI
import io.parser.lora.annotation.LoraParser
import java.math.BigDecimal

@LoraParser(size = 18)
data class Gas(
    @DevEUI
    override val devEUI: String,
    @ParseEnum(byteStart = 0, byteEnd = 0)
    val messageType: GasMsgType,
    @ParseStatus(byteStart = 1, byteEnd = 1)
    val sensorState: GasAlarmStatus,
    @ParseHex(byteStart = 2, byteEnd = 5, converter = HexConverterType.IEEE754)
    val o2Value: BigDecimal,
    @ParseHex(byteStart = 6, byteEnd = 9, converter = HexConverterType.IEEE754)
    val h2Value: BigDecimal,
    @ParseHex(byteStart = 10, byteEnd = 13, converter = HexConverterType.IEEE754)
    val lelValue: BigDecimal,
    @ParseHex(byteStart = 14, byteEnd = 17, converter = HexConverterType.IEEE754)
    val coValue: BigDecimal,
) : LoraParsable {
    companion object {
        fun fromLora(devEUI: String, log: String): Gas {
            return LoraParsable.parse<Gas>(devEUI, log)
        }
        fun random(devEUI: String): Gas {
            return LoraParsable.random(devEUI)
        }
    }
    fun toDummy(): Dummy {
        return LoraParsable.toDummy(this)
    }
}