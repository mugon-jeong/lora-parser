import java.nio.ByteBuffer
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty

/**
 * AnnotationHandler 인터페이스는 특정 어노테이션이 적용된 필드를 처리하는 로직을 제공합니다.
 *
 * 이 인터페이스를 구현하면 클래스의 프로퍼티에 특정 어노테이션이 적용되었는지를 확인하고,
 * 해당 프로퍼티에 대한 값을 처리하는 메서드를 정의할 수 있습니다.
 *
 * **주의사항:**
 * 구현된 핸들러는 `AnnotationHandlerRegistry`에 등록해야 합니다.
 * 등록된 핸들러는 `LoraParsable` 인터페이스의 `parse` 메서드에서 호출됩니다.
 */
interface AnnotationHandler {
    /**
     * 주어진 프로퍼티와 생성자 파라미터에 대해 이 핸들러가 처리 가능한지 검사합니다.
     *
     * @param property 클래스의 프로퍼티를 나타내는 [KProperty].
     * @param param 생성자 파라미터를 나타내는 [KParameter].
     * @return 이 핸들러가 처리할 수 있는 경우 `true`, 그렇지 않으면 `false`.
     *
     * 예시:
     * ```
     * override fun canHandle(property: KProperty<*>, param: KParameter): Boolean {
     *     return property.findAnnotation<SomeAnnotation>() != null
     * }
     * ```
     */
    fun canHandle(property: KProperty<*>, param: KParameter): Boolean

    /**
     * 처리 가능한 프로퍼티에 대해 값을 생성하고 반환합니다.
     *
     * @param property 클래스의 프로퍼티를 나타내는 [KProperty].
     * @param param 생성자 파라미터를 나타내는 [KParameter].
     * @param data 바이트 배열 데이터.
     * @param devEuiBytes devEUI 값을 바이트 배열로 변환한 값.
     * @return 처리된 값. 반환되는 값은 생성자 파라미터에 할당됩니다.
     *
     * 예시:
     * ```
     * override fun handle(
     *     property: KProperty<*>,
     *     param: KParameter,
     *     data: ByteArray,
     *     devEuiBytes: ByteArray
     * ): Any? {
     *     val annotation = property.findAnnotation<SomeAnnotation>()!!
     *     val rawBytes = data.slice(annotation.byteStart..annotation.byteEnd)
     *     return rawBytes.toHexString()  // 데이터를 Hex 문자열로 변환하여 반환
     * }
     * ```
     *
     * **핸들러 등록:**
     * 핸들러 구현 후 반드시 `AnnotationHandlerRegistry`에 등록해야 합니다.
     *
     * ```
     * object AnnotationHandlerRegistry {
     *     val handlers: List<AnnotationHandler> = listOf(
     *         DevEUIHandler(),
     *         ParseHexHandler()
     *     )
     * }
     * ```
     */
    fun handle(
        property: KProperty<*>,
        param: KParameter,
        data: ByteArray,
        devEuiBytes: ByteArray
    ): Any?

    fun random(
        property: KProperty<*>,
        param: KParameter,
        devEui: String,
        customRandomProvider: (KClass<*>) -> Any? = { null }
    ): Any?

    fun handleDummy(
        property: KProperty<*>,
        param: KParameter,
        buffer: ByteBuffer,
        value: Any
    ) {}
}