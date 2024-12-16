package com.example.enums

enum class PositioningSuccessType(val bit: Int, val description: String) {
    WIFI(0, "WIFI positioning success (Customized Format)"),
    BLUETOOTH(1, "Bluetooth positioning success"),
    GPS_LORA(2, "GPS positioning success (LoRa Cloud Customized Format)"),
    GPS_TRADITIONAL(3, "GPS positioning success (Traditional GPS Positioning)"),
    WIFI_LORA(4, "WIFI positioning success (LoRa Cloud DAS Format)"),
    GPS_LORA_DAS(5, "GPS positioning success (LoRa Cloud DAS Format)"),

    UNKNOWN(100, "Unknown"),
    ;

    companion object {
        fun fromBit(bit: Int): PositioningSuccessType {
            return entries.find { it.bit == bit } ?: UNKNOWN
        }
    }
}