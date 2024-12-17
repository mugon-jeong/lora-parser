import enums.BitEnum
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

    val devEuiBytes = HexUtils.base64ToByteArray(devEUI)
    val data = HexUtils.base64ToByteArray(log)

    require(clazz.annotations.any { it is LoraParser }) {
        "Class ${clazz.simpleName} is not annotated with @LoraParser"
    }

    val constructor = clazz.constructors.first()
    val args = constructor.parameters.associateWith { param ->
        val property = clazz.memberProperties.find { it.name == param.name }
            ?: throw IllegalArgumentException("Property ${param.name} not found in class ${clazz.simpleName}")

        when {
            // @DevEUI 어노테이션 처리
            property.findAnnotation<DevEUI>() != null -> devEuiBytes.toHexString()

            // @ParseStatus 어노테이션 처리 (SensorStatus)
            property.findAnnotation<ParseStatus>() != null -> {
                val annotation = property.findAnnotation<ParseStatus>()!!
                val rawBytes = data.slice(annotation.byteStart..annotation.byteEnd)
                SensorStatus.fromBytes(rawBytes)
            }

            // @FwVersion 어노테이션 처리
            property.findAnnotation<FwVersion>() != null -> {
                val annotation = property.findAnnotation<FwVersion>()!!
                val rawBytes = data.slice(annotation.byteStart..annotation.byteEnd)
                parseFwVersion(rawBytes)
            }

            // @ParseHex 어노테이션 처리
            property.findAnnotation<ParseHex>() != null -> {
                val annotation = property.findAnnotation<ParseHex>()!!
                handleParseHex(data, annotation, param.type.classifier as? KClass<*>)
            }

            else -> throw IllegalArgumentException("Unsupported property '${property.name}' in class ${clazz.simpleName}")
        }
    }

    return constructor.callBy(args)
}

/**
 * 두 개의 바이트를 결합하여 펌웨어 버전 문자열을 생성합니다.
 *
 * 바이트 배열에서 상위 4비트(major), 하위 4비트(minor), 나머지 8비트(patch)를 추출해
 * 버전 형식 "V{major}.{minor}.{patch}"으로 변환합니다.
 *
 * @param rawBytes 펌웨어 버전을 나타내는 두 개의 바이트 리스트.
 *                 - 예: [0x56, 0x78] → 결합된 값 0x5678
 * @return 펌웨어 버전을 나타내는 문자열. 예: "V5.6.120"
 *
 * @throws IllegalArgumentException 두 개의 바이트가 제공되지 않은 경우 예외를 발생시킵니다.
 */
fun parseFwVersion(rawBytes: List<Byte>): String {
    require(rawBytes.size == 2) { "FwVersion requires exactly 2 bytes to parse." }

    // 두 개의 바이트를 결합하여 16비트 정수 생성
    val version = ((rawBytes[0].toInt() and 0xFF) shl 8) or (rawBytes[1].toInt() and 0xFF)

    // 상위 4비트(major), 하위 4비트(minor), 나머지 8비트(patch) 추출
    val major = (version shr 12) and 0xF
    val minor = (version shr 8) and 0xF
    val patch = version and 0xFF

    // 버전 문자열 반환
    return "V$major.$minor.$patch"
}

/**
 * 바이트 배열을 16진수 문자열로 변환합니다.
 */
fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }

/**
 * 바이트를 포맷된 16진수 문자열로 변환합니다.
 */
fun Byte.toHexFormatted(): String = String.format("%02x", this.toInt() and 0xFF)

fun handleDevEUI(devEuiBytes: ByteArray): String = devEuiBytes.toHexString()

fun handleParseStatus(data: ByteArray, annotation: ParseStatus): SensorStatus {
    val rawBytes = data.slice(annotation.byteStart..annotation.byteEnd)
    return SensorStatus.fromBytes(rawBytes)
}

fun handleFwVersion(data: ByteArray, annotation: FwVersion): String {
    val rawBytes = data.slice(annotation.byteStart..annotation.byteEnd)
    return parseFwVersion(rawBytes)
}

fun handleParseHex(data: ByteArray, annotation: ParseHex, returnType: KClass<*>?): Any {
    val rawBytes = data.slice(annotation.byteStart..annotation.byteEnd)
    val hexValue = rawBytes.joinToString("") { it.toHexFormatted() }

    return when {
        // Enum 타입 처리 (BitEnum 구현체만 허용)
        annotation.enumType != Enum::class -> {
            val enumType = annotation.enumType
            require(BitEnum::class.java.isAssignableFrom(enumType.java)) {
                "Enum type ${enumType.simpleName} must implement BitEnum interface"
            }
            handleEnumType(enumType, hexValue, annotation.bitRange)
        }

        // Long 타입 처리
        returnType == Long::class -> HexUtils.hexToLong(hexValue)

        // Int 타입 처리
        returnType == Int::class -> HexUtils.hexToLong(hexValue).toInt()

        // Double 타입 처리
        returnType == Double::class -> HexUtils.hexToLong(hexValue) / annotation.scale

        else -> throw IllegalArgumentException("Unsupported type: $returnType")
    }
}

internal fun handleEnumType(enumType: KClass<*>, hexValue: String, bitRange: IntArray): BitEnum {
    // 비트 값 추출
    val bitValue = if (bitRange.isNotEmpty()) {
        require(bitRange.size == 2) { "bitRange must contain exactly 2 elements: [start, end]" }
        val bitStart = bitRange[0]
        val bitEnd = bitRange[1]
        hexValue.substring(bitStart, bitEnd).toInt(16)
    } else {
        hexValue.toInt(16)
    }

    // enumType이 BitEnum을 구현했는지 확인
    require(BitEnum::class.java.isAssignableFrom(enumType.java)) {
        "Enum type ${enumType.simpleName} must implement BitEnum interface"
    }

    // Enum의 entries를 가져와 bit 값으로 필터링
    val enumConstants = enumType.java.enumConstants as Array<BitEnum>
    return enumConstants.find { it.bit == bitValue }
        ?: enumConstants.find { it.bit == 100 }  // UNKNOWN 필드 처리
        ?: throw IllegalArgumentException("Invalid bit value: $bitValue for enum type ${enumType.simpleName}")
}