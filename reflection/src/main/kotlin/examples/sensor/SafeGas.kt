package examples.sensor

import annotation.DevEUI
import annotation.LoraParser
import annotation.ParseEnum
import annotation.ParseHex
import examples.enums.SafeGasAlarmType
import examples.enums.SafeGasType
import examples.enums.SafeGasUnit
import lora.LoraParsable

@LoraParser
data class SafeGas(
    @DevEUI
    override val devEUI: String,
    @ParseHex(byteStart = 1, byteEnd = 1)
    val functionCode: Int,
    @ParseHex(byteStart = 2, byteEnd = 2)
    val byteCount: Int,
    @ParseEnum(byteStart = 3, byteEnd = 4)
    val type: SafeGasType,
    @ParseEnum(byteStart = 5, byteEnd = 6)
    val unit: SafeGasUnit,
    @ParseHex(byteStart = 7, byteEnd = 8)
    val decimalPlaces: Int,
    @ParseHex(byteStart = 9, byteEnd = 12)
    val range: Int,
    @ParseHex(byteStart = 13, byteEnd = 16)
    val value: Int,
    @ParseHex(byteStart = 17, byteEnd = 20)
    val highAlarmValue: Int,
    @ParseHex(byteStart = 21, byteEnd = 24)
    val lowAlarmValue: Int,
    @ParseHex(byteStart = 25, byteEnd = 28)
    val stelAlarmValue: Int,
    @ParseHex(byteStart = 29, byteEnd = 32)
    val twaAlarmValue: Int,
    @ParseEnum(byteStart = 33, byteEnd = 34)
    val alarmType: SafeGasAlarmType,
    @ParseHex(byteStart = 32, byteEnd = 33)
    val adc: Int,
) : LoraParsable {
    companion object {
        fun decimalPlaceResult(
            decimalPlaces: Int,
            value: Int,
        ): Double {
            return if (decimalPlaces != 0) value / Math.pow(10.0, decimalPlaces.toDouble()) else value.toDouble()
        }

        fun fromLora(devEUI: String, log: String): SafeGas {
            return LoraParsable.parse<SafeGas>(devEUI, log)
        }
    }

}