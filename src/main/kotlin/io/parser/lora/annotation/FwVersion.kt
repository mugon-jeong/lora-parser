package io.parser.lora.annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class FwVersion(
    val byteStart: Int, // 시작 바이트
    val byteEnd: Int    // 끝 바이트
)
