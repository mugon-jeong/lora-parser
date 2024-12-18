package io.parser.lora.examples.enums

import io.parser.lora.enums.BitEnum

enum class GasMsgType(
    override val bit: Int,
    override val description: String
) : BitEnum {
    PERIODIC(0, "Periodic message"),
    CO_WARNING(1, "CO warning"),
    LEL_WARNING(2, "LEL warning"),
    H2S_WARNING(3, "H2S warning"),
    O2_WARNING(4, "O2 warning"),
    CO_NORMAL(5, "CO normal"),
    LEL_NORMAL(6, "LEL normal"),
    H2S_NORMAL(7, "H2S normal"),
    O2_NORMAL(8, "O2 normal"),
    POWER_ON(9, "Power on"),
    POWER_OFF(10, "Power off"),
    HEART_BEAT(11, "Heart beat"),
    UNKNOWN(-1, "Unknown");

    companion object {
        /**
         * 비트 값으로 `GasMsgType` 객체를 반환합니다.
         *
         * @param bit 비트 값
         * @return 비트 값과 일치하는 `GasMsgType` 객체. 없으면 `UNKNOWN` 반환.
         */
        fun fromBit(bit: Int): GasMsgType {
            return entries.find { it.bit == bit } ?: UNKNOWN
        }
    }
}