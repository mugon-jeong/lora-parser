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
            return ByteParsable.parseBytes<GasAlarmStatus>(bytes)
        }
    }
}