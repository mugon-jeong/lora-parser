package examples.status

import annotation.BitField
import lora.ByteParsable

data class GasAlarmStatus(
    @BitField(bitPosition = 7) val o2Alarm: Boolean,
    @BitField(bitPosition = 6) val h2SAlarm: Boolean,
    @BitField(bitPosition = 5) val lelAlarm: Boolean,
    @BitField(bitPosition = 4) val coAlarm: Boolean,
    @BitField(bitPosition = 3) val power: Boolean,
    @BitField(bitPosition = 2) val activate: Boolean
) : ByteParsable {
    companion object {
        fun fromBytes(bytes: List<Byte>): GasAlarmStatus {
            val binaryString = ByteParsable.parseBitField(bytes)
            return GasAlarmStatus(
                o2Alarm = ByteParsable.getBitAsBoolean(binaryString, 7),
                h2SAlarm = ByteParsable.getBitAsBoolean(binaryString, 6),
                lelAlarm = ByteParsable.getBitAsBoolean(binaryString, 5),
                coAlarm = ByteParsable.getBitAsBoolean(binaryString, 4),
                power = ByteParsable.getBitAsBoolean(binaryString, 3),
                activate = ByteParsable.getBitAsBoolean(binaryString, 2),
            )
        }
    }
}