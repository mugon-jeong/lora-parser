package io.parser.lora.examples.sensor

import io.parser.lora.annotation.ParseEnum
import io.parser.lora.annotation.ParseHex
import io.parser.lora.enums.HexConverterType
import io.parser.lora.examples.enums.CurrentAuxiliaryOperation
import io.parser.lora.examples.enums.CurrentWorkingMode
import io.parser.lora.examples.enums.PositioningSuccessType
import io.parser.lora.examples.enums.PositioningType
import io.parser.lora.LoraParsable
import io.parser.lora.annotation.DevEUI
import io.parser.lora.annotation.LoraParser
import java.math.BigDecimal

@LoraParser
data class SafetySignal(
    @DevEUI
    override val devEUI: String,
    @ParseHex(byteStart = 0, byteEnd = 0)
    val batteryLevel: Long,
    @ParseHex(byteStart = 1, byteEnd = 2)
    val age: Long,
    @ParseEnum(byteStart = 3, byteEnd = 3, bitRange = [0, 1])
    val positioningType: PositioningType,
    @ParseEnum(byteStart = 3, byteEnd = 3, bitRange = [1, 2])
    val positioningSuccessType: PositioningSuccessType,
    @ParseEnum(byteStart = 4, byteEnd = 4, bitRange = [0, 1])
    val currentWorkingMode: CurrentWorkingMode,
    @ParseEnum(byteStart = 4, byteEnd = 4, bitRange = [1, 2])
    val currentAuxiliaryOperation: CurrentAuxiliaryOperation,
    @ParseHex(byteStart = 5, byteEnd = 5)
    val positioningDataLength: Long,
    @ParseHex(byteStart = 6, byteEnd = 9, converter = HexConverterType.DIVIDE, scale = 10_000_000.0)
    val latitude: BigDecimal,
    @ParseHex(byteStart = 10, byteEnd = 13, converter = HexConverterType.DIVIDE, scale = 10_000_000.0)
    val longitude: BigDecimal,
) : LoraParsable {
    companion object {
        fun fromLora(devEUI: String, log: String): SafetySignal {
            return LoraParsable.parse<SafetySignal>(devEUI, log)
        }
    }
}