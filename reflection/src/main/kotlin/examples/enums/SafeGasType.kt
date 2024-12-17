package examples.enums

import enums.BitEnum

enum class SafeGasType(override val bit: Int, override val description: String) : BitEnum {
    NONE(0, "None"),
    CO(1, "CO"),
    H2S(2, "H2S"),
    O2(3, "O2"),
    EX(4, "EX"),
    SO2(5, "SO2"),
    NH3(6, "NH3"),
    H2(7, "H2"),
    N2(8, "N2"),
    O3(9, "O3"),
    TVOC(10, "TVOC"),
    CL2(11, "CL2"),
    HCL(12, "HCL"),
    NO(13, "NO"),
    NO2(14, "NO2"),
    PH3(15, "PH3"),
    AsH3(16, "AsH3"),
    HCN(17, "HCN"),
    CO2(18, "CO2"),
    SF6(19, "SF6"),
    Br2(20, "Br2"),
    HBr(21, "HBr"),
    F2(22, "F2"),
    HF(23, "HF"),
    N2O(24, "N2O"),
    H2O2(25, "H2O2"),
    NOX(26, "NOX"),
    SOX(27, "SOX"),
    ODOR(28, "Odor"),
    VOC(29, "VOC"),
    CH4(30, "CH4"),
    C2H6(31, "C2H6"),
    C3H8(32, "C3H8"),
    C4H10(33, "C4H10"),
    IC4H10(34, "iC4H10"),
    C5H12(35, "C5H12"),
    C2H4(36, "C2H4"),
    C3H6(37, "C3H6"),
    C4H8(38, "C4H8"),
    IC4H8(39, "iC4H8"),
    CH4O(40, "CH4O"),
    C2H6O(41, "C2H6O"),
    C3H8O(42, "C3H8O"),
    IC3H8O(43, "iC3H8O"),
    C4H10O(44, "C4H10O"),
    CH2O(45, "CH2O"),
    C2H4O(46, "C2H4O"),
    C3H6O(47, "C3H6O"),
    C3H4O(48, "C3H4O"),
    C2H2(49, "C2H2"),
    C6H6(50, "C6H6"),
    C7H8(51, "C7H8"),
    C8H10(52, "C8H10"),
    C8H8(53, "C8H8"),
    C6H6O(54, "C6H6O"),
    ETO(55, "ETO"),
    C2H8O2(56, "C2H8O2"),
    NMHC(57, "NMHC"),
    CH4S(58, "CH4S"),
    CLO2(59, "CLO2"),
    SO2F2(60, "SO2F2"),
    CS2(61, "CS2"),
    CH3Br(62, "CH3Br"),
    HC(63, "HC"),
    C2H6O2(64, "C2H6O2"),
    LEL(65, "LEL"),
    He(66, "He"),
    CxHy(67, "CxHy"),
    THC(68, "THC"),
    HBr2(69, "HBr"),
    C2H6S(70, "C2H6S"),
    C2H6S2(71, "C2H6S2"),
    C3H9N(72, "C3H9N"),
    VOCs(73, "VOCs"),
    C2H4O2(74, "C2H4O2"),
    Ar(75, "Ar"),
    R22(76, "R22"),
    R134a(77, "R134a"),
    COCL2(78, "COCL2"),
    C2Cl4(79, "C2Cl4"),
    CHCO(80, "CHCO"),
    CHN(81, "CHN"),
    C2HCl3(82, "C2HCl3"),
    R32(83, "R32"),
    OU(84, "OU"),
    TEMP(85, "Temperature"),
    RH(86, "Relative Humidity"),
    PM1_0_S(87, "PM1.0 (Sensor)"),
    PM2_5_S(88, "PM2.5 (Sensor)"),
    PM10_S(89, "PM10 (Sensor)"),
    PM1_0_A(90, "PM1.0 (Air)"),
    PM2_5_A(91, "PM2.5 (Air)"),
    PM10_A(92, "PM10 (Air)"),
    MICRON_0_3(93, "0.3μm"),
    MICRON_0_5(94, "0.5μm"),
    MICRON_1_0(95, "1.0μm"),
    MICRON_2_5(96, "2.5μm"),
    MICRON_5_0(97, "5.0μm"),
    MICRON_10_0(98, "10μm"),
    VEL(99, "Wind Speed (VEL)"),
    WSD(100, "Wind Direction (WSD)"),
    NVH(101, "Noise Vibration Harshness (NVH)"),
    RAINFALL(102, "Rainfall"),
    HV(103, "Illumination (HV)"),
    UV(104, "Ultraviolet (UV)"),
    ATM(105, "Atmospheric Pressure (ATM)"),
    B2H6(106, "B2H6"),
    CH3SH(107, "CH3SH"),
    C3H3N(108, "C3H3N"),
    CH3OH(109, "CH3OH"),
    LPG(110, "LPG"),
    C4H8S(111, "C4H8S"),
    C2H3C2(112, "C2H3C2"),
    C2H4Cl2(113, "C2H4Cl2"),
    C2H3CL(114, "C2H3CL"),
    SO3(115, "SO3"),
    THT(116, "THT"),
    C4H8O(117, "C4H8O"),
    C3H6O2(118, "C3H6O2"),
    C4H8O2(119, "C4H8O2"),
    C5H10O2(120, "C5H10O2"),
    CH2Cl2(121, "CH2Cl2"),
    THF(122, "THF"),
    BCl3(123, "BCl3"),
    SiH4(124, "SiH4"),
    TMA(125, "TMA"),

    // 필요한 경우 추가로 가스 타입을 여기서 정의할 수 있습니다.
    UNKNOWN(-1, "Unknown"),
    ;

    companion object {
        fun fromCode(bit: Int): SafeGasType {
            return entries.find { it.bit == bit } ?: UNKNOWN
        }
    }
}