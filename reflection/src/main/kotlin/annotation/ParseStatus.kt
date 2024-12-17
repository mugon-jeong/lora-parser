package annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ParseStatus(
    val byteStart: Int, // 시작 바이트
    val byteEnd: Int, // 끝 바이트
)
