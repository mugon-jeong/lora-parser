import kotlin.reflect.KClass

/**
 * 바이트 배열의 특정 범위를 추출하고 값을 변환하기 위한 어노테이션입니다.
 *
 * 이 어노테이션은 바이트 데이터를 필드에 매핑할 때 사용되며, 정수(Long/Int), 실수(Double), Enum과 같은 다양한 타입을 지원합니다.
 *
 * @property byteStart 바이트 시작 인덱스입니다 (0부터 시작).
 * @property byteEnd 바이트 끝 인덱스입니다. 시작 인덱스를 포함하고 끝 인덱스를 포함합니다 (inclusive).
 * @property scale 실수(Double) 타입으로 변환할 때 적용되는 스케일입니다.
 *                 기본값은 `1.0`으로, 변환이 필요하지 않으면 설정하지 않아도 됩니다.
 * @property enumType Enum 값으로 변환할 때 사용하는 Enum 클래스입니다.
 *                   Enum 변환을 사용하지 않는 경우 기본값 `Enum::class`로 설정됩니다.
 * @property bitRange 비트 범위를 나타내는 정수 배열입니다.
 *                   **Enum 타입**을 사용할 때만 적용되며, 비트 범위를 통해 Enum 값을 추출합니다.
 *                   예: `[0, 1]` → 첫 번째와 두 번째 비트를 추출합니다.
 *
 * @example
 * ```kotlin
 * @ParseHex(byteStart = 3, byteEnd = 3, enumType = PositioningType::class, bitRange = [0, 1])
 * val positioningType: PositioningType
 * ```
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ParseHex(
    val byteStart: Int, // 시작 바이트
    val byteEnd: Int, // 끝 바이트
    val scale: Double = 1.0, // 변환 스케일 (위도, 경도 변환 등)
    val enumType: KClass<out Enum<*>> = Enum::class, // Enum 타입
    val bitRange: IntArray = [] // 비트 범위 (Enum 전용)
)