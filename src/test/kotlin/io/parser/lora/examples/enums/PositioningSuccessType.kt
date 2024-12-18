package io.parser.lora.examples.enums

import io.parser.lora.enums.BitEnum

enum class PositioningSuccessType(override val bit: Int, override val description: String) : BitEnum {
    WIFI(0, "WIFI positioning success (Customized Format)"),
    BLUETOOTH(1, "Bluetooth positioning success"),
    GPS_LORA(2, "GPS positioning success (LoRa Cloud Customized Format)"),
    GPS_TRADITIONAL(3, "GPS positioning success (Traditional GPS Positioning)"),
    WIFI_LORA(4, "WIFI positioning success (LoRa Cloud DAS Format)"),
    GPS_LORA_DAS(5, "GPS positioning success (LoRa Cloud DAS Format)"),

    UNKNOWN(-1, "Unknown"),
    ;

    companion object {
        fun fromBit(bit: Int): PositioningSuccessType {
            return entries.find { it.bit == bit } ?: UNKNOWN
        }
    }
}