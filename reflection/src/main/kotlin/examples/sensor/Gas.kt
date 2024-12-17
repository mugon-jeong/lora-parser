package examples.sensor

import annotation.DevEUI
import annotation.LoraParser
import annotation.ParseEnum
import annotation.ParseHex
import annotation.ParseStatus
import enums.HexConverterType
import examples.enums.GasMsgType
import examples.status.GasAlarmStatus
import lora.LoraParsable
import java.math.BigDecimal

@LoraParser
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
    }
}