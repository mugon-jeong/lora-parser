package com.example.enums

enum class CurrentAuxiliaryOperation(val bit: Int, val description: String) {
    NO_AUXILIARY_OPERATION(0, "no auxiliary operation"),
    DOWNLINK_FOR_POSITION(1, "downlink for position"),
    MAN_DOWN_STATUS(2, "Man Down status"),
    ALERT_ALARM(3, "Alert alarm"),
    SOS_ALARM(4, "SOS alarm"),

    UNKNOWN(100, "Unknown"),
    ;

    companion object {
        fun fromBit(bit: Int): CurrentAuxiliaryOperation {
            return entries.find { it.bit == bit } ?: UNKNOWN
        }
    }
}