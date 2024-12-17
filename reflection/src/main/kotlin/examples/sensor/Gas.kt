package examples.sensor

import annotation.DevEUI
import annotation.LoraParser
import annotation.ParseEnum
import annotation.ParseHex
import annotation.ParseStatus
import examples.enums.GasMsgType
import examples.status.GasAlarmStatus
import lora.LoraParsable

@LoraParser
data class Gas(
    @DevEUI
    override val devEUI: String,
    @ParseEnum(byteStart = 0, byteEnd = 0)
    val messageType: GasMsgType,
    @ParseStatus(byteStart = 1, byteEnd = 1)
    val sensorState: GasAlarmStatus,
    @ParseHex(byteStart = 2, byteEnd = 5)
    val o2Value: Float,
    @ParseHex(byteStart = 6, byteEnd = 9)
    val h2Value: Float,
    @ParseHex(byteStart = 10, byteEnd = 13)
    val lelValue: Float,
    @ParseHex(byteStart = 14, byteEnd = 17)
    val coValue: Float,
) : LoraParsable {
    companion object {
        fun fromLora(devEUI: String, log: String): Gas {
            return LoraParsable.parse<Gas>(devEUI, log)
        }
    }
}