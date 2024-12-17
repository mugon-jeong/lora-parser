package annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class BitField(
    val bitPosition: Int // 비트 위치 (0~7)
)