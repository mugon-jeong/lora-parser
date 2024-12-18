package io.parser.lora.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ParseEnum(
    val byteStart: Int, // 시작 바이트
    val byteEnd: Int, // 끝 바이트
    val scale: Double = 1.0, // 변환 스케일 (위도, 경도 변환 등)
    val bitRange: IntArray = [], // 비트 범위 (Enum 전용)
)