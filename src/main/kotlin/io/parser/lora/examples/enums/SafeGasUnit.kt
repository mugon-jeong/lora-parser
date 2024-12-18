package io.parser.lora.examples.enums

import io.parser.lora.enums.BitEnum


enum class SafeGasUnit(override val bit: Int, override val description: String) : BitEnum {
    PPM(0, "PPM"),
    PPB(1, "PPB"),
    PERCENT_VOL(2, "%VOL"),
    PERCENT_LEL(3, "%LEL"),
    MG_M3(4, "mg/m³"),
    DEG_C(5, "°C"),
    PERCENT_RH(6, "%RH"),
    G_M3(7, "g/m³"),
    UG_M3(8, "μg/m³"),
    KPA(9, "KPa"),
    UMOL_MOL(10, "μmol/mol"),
    BLANK(11, "Blank"),
    OU(12, "Odor Units (OU)"),
    UG_M3_ALT(13, "μg/m³ (alternative)"),
    DB(14, "dB"),
    LUX(15, "Lux"),
    MM(16, "mm"),
    M_S(17, "m/s"),
    MW_CM2(18, "mW/cm²"),
    NMOL_MOL(19, "nmol/mol"),
    UNKNOWN(-1, "Unknown"),
    ;

    companion object {
        fun fromCode(bit: Int): SafeGasUnit {
            return entries.find { it.bit == bit } ?: UNKNOWN
        }
    }
}