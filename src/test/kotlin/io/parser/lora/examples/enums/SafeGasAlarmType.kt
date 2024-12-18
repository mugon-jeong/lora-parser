package io.parser.lora.examples.enums

import io.parser.lora.enums.BitEnum

enum class SafeGasAlarmType(override val bit: Int, override val description: String) : BitEnum {
    UNKNOWN(0, "Unknown/Disconnected"),
    NORMAL(1, "Normal"),
    LOW_ALARM(2, "Low Alarm"),
    HIGH_ALARM(3, "High Alarm"),
    STEL_ALARM(4, "STEL Alarm"),
    TWA_ALARM(5, "TWA Alarm"),
    LOW_BATTERY(6, "Low Battery Alarm"),
    ANTI_THEFT(7, "Anti-theft Alarm"),
    ABNORMAL(8, "Abnormal Alarm"),
    ;

    companion object {
        fun fromCode(bit: Int): SafeGasAlarmType {
            return entries.find { it.bit == bit } ?: UNKNOWN
        }
    }
}
