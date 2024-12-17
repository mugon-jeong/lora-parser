package com.example

import LoraParser
import ParseHex
import com.example.utils.hexToLong
import com.example.utils.toHexFormatted
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * 파라미터로 전달된 `devEUI`와 `byteArray` 데이터를 기반으로 어노테이션이 적용된 클래스 [T]를 파싱합니다.
 *
 * @param T 파싱될 데이터 클래스의 타입. 반드시 `@LoraParser` 어노테이션이 선언되어야 합니다.
 * @param devEUI 장치 고유 식별자(바이트 배열 형태).
 * @param byteArray 센서 데이터(바이트 배열 형태).
 * @return [T] 타입의 객체.
 * @throws IllegalArgumentException 클래스에 `@LoraParser`가 없거나, 필드에 `@ParseHex`가 없는 경우 예외가 발생합니다.
 */
inline fun <reified T : Any> parseSensorData(devEUI: ByteArray, byteArray: ByteArray): T {
    val clazz = T::class

    require(clazz.annotations.any { it is LoraParser }) {
        "Class ${clazz.simpleName} is not annotated with @LoraParser"
    }

    val constructor = clazz.constructors.first()
    val args = constructor.parameters.associateWith { param ->
        val property = clazz.memberProperties.find { it.name == param.name }
            ?: throw IllegalArgumentException("Property ${param.name} not found in class ${clazz.simpleName}")

        property.isAccessible = true
        val annotation = property.findAnnotation<ParseHex>()
            ?: throw IllegalArgumentException("Property ${property.name} is missing @ParseHex annotation")

        extractValueFromBytes(byteArray, annotation, property.returnType.classifier as? KClass<*>)
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
 * @throws IllegalArgumentException Enum 값이 유효하지 않으면 예외를 발생시킵니다.
 */
internal fun handleEnumType(enumType: KClass<*>, hexValue: String, bitRange: IntArray): Any {
    val bitValue = if (bitRange.isNotEmpty()) {
        val bitStart = bitRange[0]
        val bitEnd = bitRange[1]
        hexValue.substring(bitStart, bitEnd).toInt(16)
    } else {
        hexValue.toInt(16)
    }

    return enumType.java.enumConstants.find { (it as Enum<*>).ordinal == bitValue }
        ?: throw IllegalArgumentException("Invalid enum value: $bitValue for $enumType")
}