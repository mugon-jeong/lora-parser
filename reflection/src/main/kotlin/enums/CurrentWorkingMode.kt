package enums

enum class CurrentWorkingMode(override val bit: Int, override val description: String):BitEnum {
    STANDBY(0, "standby mode"),
    TIMING(1, "timing mode"),
    PERIODIC(2, "periodic mode"),
    STATIONARY_STATE_IN_MOTION(3, "stationary state in motion mode"),
    START_OF_MOVEMENT_IN_MOTION(4, "start of movement in motion mode"),
    IN_MOVEMENT_FOR_MOTION(5, "in movement for motion mode"),
    END_OF_MOVEMENT_IN_MOTION(6, "end of movement in motion mode"),

    UNKNOWN(100, "Unknown"),
    ;

    companion object {
        fun fromBit(bit: Int): CurrentWorkingMode {
            return entries.find { it.bit == bit } ?: UNKNOWN
        }
    }
}