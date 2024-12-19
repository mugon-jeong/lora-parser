package io.parser.lora.examples.enums

import io.parser.lora.enums.BitEnum

enum class CurrentAuxiliaryOperation(
    override val bit: Int,
    override val description: String
) : BitEnum {

    NO_AUXILIARY_OPERATION(0, "No auxiliary operation"),
    DOWNLINK_FOR_POSITION(1, "Downlink for position"),
    MAN_DOWN_STATUS(2, "Man Down status"),
    ALERT_ALARM(3, "Alert alarm"),
    SOS_ALARM(4, "SOS alarm"),
    UNKNOWN(-1, "Unknown");

    override fun unknown(): BitEnum {
        return UNKNOWN
    }
    companion object {
        fun fromBit(bit: Int): CurrentAuxiliaryOperation {
            return entries.find { it.bit == bit } ?: UNKNOWN
        }
    }
}