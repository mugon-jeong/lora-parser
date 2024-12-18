package io.parser.lora.examples.enums

import io.parser.lora.enums.BitEnum

enum class PositioningType(override val bit: Int, override val description: String) : BitEnum {
    WORKING(0, "Working mode positioning"),
    MAN_DOWN(1, "Man Down positioning"),
    DOWN_LINK(2, "Downlink for positioning"),
    ALERT_ALARM(3, "Alert alarm positioning"),
    SOS_ALARM(4, "SOS alarm positioning"),

    UNKNOWN(-1, "Unknown"),
    ;

    companion object {
        fun fromBit(bit: Int): PositioningType {
            return entries.find { it.bit == bit } ?: UNKNOWN
        }
    }
}