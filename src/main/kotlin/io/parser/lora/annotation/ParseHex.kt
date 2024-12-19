package io.parser.lora.annotation

import io.parser.lora.enums.HexConverterType

/**
 * 바이트 배열의 특정 범위를 추출하고 값을 변환하기 위한 어노테이션입니다.
 *
 * 이 어노테이션은 바이트 데이터를 필드에 매핑할 때 사용되며, 정수(Long/Int), 실수(Double), Enum과 같은 다양한 타입을 지원합니다.
 *
 * @property byteStart 바이트 시작 인덱스입니다 (0부터 시작).
 * @property byteEnd 바이트 끝 인덱스입니다. 시작 인덱스를 포함하고 끝 인덱스를 포함합니다 (inclusive).
 * @property scale 소수점 자릿수 1이면 소수점 1자리까지 기본값은 0
 * @example
 * ```kotlin
 * @io.parser.lora.annotation.ParseHex(byteStart = 3, byteEnd = 3)
 * val positioningType: PositioningType
 * ```
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ParseHex(
    val byteStart: Int, // 시작 바이트
    val byteEnd: Int, // 끝 바이트
    val scale: Int = 0,     // 스케일 (기본값 0) 소수점 자릿수
    val converter: HexConverterType = HexConverterType.DEFAULT // 변환기 선택
)