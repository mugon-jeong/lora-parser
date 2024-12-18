package io.parser.lora.enums

enum class HexConverterType {
    UNSIGNED,     // 무부호 숫자 변환
    SIGNED,       // 부호 있는 숫자 변환
    IEEE754,      // IEEE 754 변환
    BIT,           // 비트 값 변환
    DIVIDE,         // 스케일 적용 - 나누기
    MULTIPLY,         // 스케일 적용 - 곱하기
    DEFAULT           // 변환하지 않음
}