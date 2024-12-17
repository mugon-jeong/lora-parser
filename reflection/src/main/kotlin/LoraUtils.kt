import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Base64로 인코딩된 `devEUI`와 센서 데이터 `log`를 기반으로 어노테이션이 적용된 클래스 [T]를 파싱합니다.
 *
 * @param T 파싱될 데이터 클래스의 타입. 반드시 `@LoraParser` 어노테이션이 선언되어야 합니다.
 * @param devEUI 장치 고유 식별자. Base64로 인코딩된 문자열 형태입니다.
 * @param log 센서 데이터 로그. Base64로 인코딩된 문자열 형태입니다.
 * @return [T] 타입의 객체.
 * @throws IllegalArgumentException 클래스에 `@LoraParser`가 없거나, 필드에 `@DevEUI`가 없는 경우 예외가 발생합니다.
 */
inline fun <reified T : BaseSensor> parseSensorData(devEUI: String, log: String): T {
    val clazz = T::class

    // Base64 유효성 검사 및 디코딩
    val devEuiBytes = base64ToByteArray(devEUI)
    val data = base64ToByteArray(log)

    // `LoraParser` 어노테이션 확인
    require(clazz.annotations.any { it is LoraParser }) {
        "Class ${clazz.simpleName} is not annotated with @LoraParser"
    }

    // `BaseSensor`를 구현한 클래스인지 확인
    require(BaseSensor::class.java.isAssignableFrom(clazz.java)) {
        "Class ${clazz.simpleName} must implement BaseSensor"
    }

    // `devEUI` 필드에 @DevEUI 어노테이션이 적용되었는지 확인
    val devEuiProperty = clazz.memberProperties.find { it.name == "devEUI" }
    require(devEuiProperty?.findAnnotation<DevEUI>() != null) {
        "Field 'devEUI' in class ${clazz.simpleName} must be annotated with @DevEUI"
    }

    // 생성자와 필드 매핑
    val constructor = clazz.constructors.first()
    val args = constructor.parameters.associateWith { param ->
        if (param.name == "devEUI") {
            devEuiBytes.toHexString()
        } else {
            val property = clazz.memberProperties.find { it.name == param.name }
                ?: throw IllegalArgumentException("Property ${param.name} not found in class ${clazz.simpleName}")

            val annotation = property.findAnnotation<ParseHex>()
                ?: throw IllegalArgumentException("Property ${property.name} is missing @ParseHex annotation")

            extractValueFromBytes(data, annotation, param.type.classifier as? KClass<*>)
        }
    }

    return constructor.callBy(args)
}

/**
 * 바이트 배열과 어노테이션 정보를 이용해 해당 필드의 값을 추출하고 타입에 맞게 변환합니다.
 *
 * @param byteArray 전체 데이터가 담긴 바이트 배열.
 * @param annotation 필드에 적용된 `@ParseHex` 어노테이션 정보.
 * @param returnType 변환될 데이터의 타입을 나타내는 [KClass].
 * @return 필드 타입에 맞게 변환된 값.
 * @throws IllegalArgumentException 지원되지 않는 타입일 경우 예외가 발생합니다.
 */
fun extractValueFromBytes(
    byteArray: ByteArray,
    annotation: ParseHex,
    returnType: KClass<*>?
): Any {
    val rawBytes = byteArray.slice(annotation.byteStart..annotation.byteEnd)
    val hexValue = rawBytes.joinToString("") { it.toHexFormatted() }

    return when {
        annotation.enumType != Enum::class -> handleEnumType(annotation.enumType, hexValue, annotation.bitRange)
        returnType == Long::class -> hexToLong(hexValue)
        returnType == Double::class -> hexToLong(hexValue) / annotation.scale
        else -> throw IllegalArgumentException("Unsupported type: $returnType")
    }
}

/**
 * 주어진 [hexValue]에서 비트 범위를 추출하거나 전체 값을 가져와 Enum 타입의 값으로 변환합니다.
 *
 * @param enumType 변환될 Enum 클래스.
 * @param hexValue 바이트 배열에서 추출한 16진수 문자열.
 * @param bitRange 비트 범위(예: [0, 1]). 비트 범위가 없는 경우 전체 값을 사용합니다.
 * @return Enum 값으로 변환된 결과.
 * @throws IllegalArgumentException Enum 값이 유효하지 않거나, bitRange가 잘못되었을 경우 예외를 발생시킵니다.
 */
internal fun handleEnumType(enumType: KClass<*>, hexValue: String, bitRange: IntArray): Any {
    val bitValue = if (bitRange.isNotEmpty()) {
        require(bitRange.size == 2) { "bitRange must contain exactly 2 elements: [start, end]" }
        val bitStart = bitRange.first()
        val bitEnd = bitRange.last()
        require(bitStart >= 0 && bitEnd <= hexValue.length) {
            "Invalid bitRange: start ($bitStart) or end ($bitEnd) out of bounds for hexValue length ${hexValue.length}"
        }
        hexValue.substring(bitStart, bitEnd).toInt(16)
    } else {
        hexValue.toInt(16)
    }

    return enumType.java.enumConstants.find { (it as Enum<*>).ordinal == bitValue }
        ?: throw IllegalArgumentException("Invalid enum value: $bitValue for $enumType")
}

/**
 * 바이트 배열을 16진수 문자열로 변환합니다.
 */
fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }

/**
 * 바이트 리스트를 16진수 문자열로 변환합니다.
 */
fun List<Byte>.toHexString(): String = joinToString("") { "%02x".format(it) }

/**
 * 16진수 문자열을 바이트 배열로 변환합니다.
 *
 * @throws IllegalArgumentException 문자열의 길이가 홀수인 경우 예외가 발생합니다.
 */
fun String.hexStringToByteArray(): ByteArray {
    require(length % 2 == 0) { "Hex string must have an even length" }
    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}

/**
 * 16진수 문자열을 Long 타입으로 변환합니다.
 */
fun hexToLong(hex: String): Long = hex.toLong(16)

/**
 * Base64 문자열을 바이트 배열로 변환합니다.
 *
 * @param base64 변환할 Base64 문자열.
 * @return 바이트 배열로 변환된 결과.
 * @throws IllegalArgumentException 입력된 문자열이 유효한 Base64 형식이 아닐 경우 예외가 발생합니다.
 */
fun base64ToByteArray(base64: String): ByteArray {
    return try {
        Base64.getDecoder().decode(base64)
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Invalid Base64 format: $base64")
    }
}

/**
 * 바이트를 포맷된 16진수 문자열로 변환합니다.
 */
fun Byte.toHexFormatted(): String = String.format("%02x", this.toInt() and 0xFF)