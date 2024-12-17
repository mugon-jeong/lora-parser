package examples.status

import annotation.BitField
import lora.ByteParsable

data class SensorStatus(
    @BitField(bitPosition = 0) val watchDog: Boolean,
    @BitField(bitPosition = 1) val wakeUp: Boolean,
    @BitField(bitPosition = 2) val readyToSleep: Boolean,
    @BitField(bitPosition = 3) val triggerSensorEvent: Boolean,
    @BitField(bitPosition = 4) val downlinkAck: Boolean,
    @BitField(bitPosition = 5) val triggerBatteryStatus: Boolean,
    @BitField(bitPosition = 6) val battery: Boolean,
    @BitField(bitPosition = 7) val power: Boolean
) : ByteParsable {
    companion object {
        fun fromBytes(bytes: List<Byte>): SensorStatus {
            return ByteParsable.parseBytes(bytes)
        }
    }
}