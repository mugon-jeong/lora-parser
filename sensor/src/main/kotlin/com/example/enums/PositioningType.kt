package com.example.enums

enum class PositioningType(val bit: Int, val description: String) {
    WORKING(0, "Working mode positioning"),
    MAN_DOWN(1, "Man Down positioning"),
    DOWN_LINK(2, "Downlink for positioning"),
    ALERT_ALARM(3, "Alert alarm positioning"),
    SOS_ALARM(4, "SOS alarm positioning"),

    UNKNOWN(100, "Unknown"),
    ;

    companion object {
        fun fromBit(bit: Int): PositioningType {
            return entries.find { it.bit == bit } ?: UNKNOWN
        }
    }
}