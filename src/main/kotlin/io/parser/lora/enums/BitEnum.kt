package io.parser.lora.enums

/**
 * 비트 기반 Enum을 위한 인터페이스입니다.
 *
 * @property bit 비트 값
 * @property description 비트 값에 대한 설명
 */
interface BitEnum {
    val bit: Int
    val description: String

    fun unknown(): BitEnum
}