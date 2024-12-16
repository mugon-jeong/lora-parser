import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ParseHex(
    val byteStart: Int, // 시작 바이트
    val byteEnd: Int, // 끝 바이트
    val scale: Double = 1.0, // 변환 스케일 (위도, 경도 변환 등)
    val enumType: KClass<out Enum<*>> = Enum::class, // Enum 타입
    val bitRange: IntArray = [] // 비트 범위 (시작, 끝)
)