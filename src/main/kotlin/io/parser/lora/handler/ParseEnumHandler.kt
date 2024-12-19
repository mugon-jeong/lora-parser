package io.parser.lora.handler

import AnnotationHandler
import io.parser.lora.annotation.ParseEnum
import io.parser.lora.enums.BitEnum
import io.parser.lora.utils.toHexFormatted
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

object ParseEnumHandler : AnnotationHandler {
    override fun canHandle(property: KProperty<*>, param: KParameter): Boolean {
        return property.findAnnotation<ParseEnum>() != null || param.findAnnotation<ParseEnum>() != null
    }

    override fun handle(property: KProperty<*>, param: KParameter, data: ByteArray, devEuiBytes: ByteArray): Any? {
        val annotation = property.findAnnotation<ParseEnum>() ?: param.findAnnotation<ParseEnum>()!!
        val targetClass = param.type.classifier as? KClass<*>
            ?: throw IllegalArgumentException("Unsupported property '${property.name}'")

        require(BitEnum::class.java.isAssignableFrom(targetClass.java)) {
            "Enum Class ${targetClass.simpleName} must implement BitEnum"
        }

        // 1. 바이트 배열 슬라이스
        val slicedBytes = data.slice(annotation.byteStart..annotation.byteEnd)

        // bitRange가 있는 경우
        if (annotation.bitRange.size == 2) {
            // 2. 16진수 문자열 변환
            val hexString = slicedBytes.joinToString("") { it.toHexFormatted() }

            // 3. 16진수 → 2진수 변환
            val binaryString = hexString.toBigInteger(16).toString(2).padStart(slicedBytes.size * 8, '0')

            // 4. `bitRange`를 사용해 비트 슬라이스
            val startBit = annotation.bitRange[0]
            val endBit = annotation.bitRange[1]

            val slicedBits = binaryString.substring(startBit, endBit + 1)

            // 5. 비트를 10진수로 변환
            val bitValue = slicedBits.toInt(2)

            // 6. `enum` 복원
            val enumConstants = targetClass.java.enumConstants as Array<BitEnum>
            return enumConstants.find { it.bit == bitValue } ?: enumConstants.first().unknown().also {
                println("Invalid bit value: $bitValue for property '${property.name}'. Using unknown value: ${it.description}")
            }
        } else {
            // bitRange가 없는 경우
            // 2. 슬라이스된 바이트 배열을 숫자로 변환
            val number = slicedBytes.fold(0) { acc, byte -> (acc shl 8) or (byte.toInt() and 0xFF) }
            println("Enum Number: $slicedBytes $number")
            // 3. `enum` 복원
            val enumConstants = targetClass.java.enumConstants as Array<BitEnum>
            return enumConstants.find { it.bit == number } ?: enumConstants.first().unknown().also {
                println("Invalid number value: $number for property '${property.name}'. Using unknown value: ${it.description}")
            }
        }
    }

    override fun random(
        property: KProperty<*>,
        param: KParameter,
        devEui: String,
        customRandomProvider: (KClass<*>) -> Any?
    ): Any {
        val targetClass = param.type.classifier as? KClass<BitEnum>
            ?: throw IllegalArgumentException("Unsupported property '${property.name}'")

        require(BitEnum::class.java.isAssignableFrom(targetClass.java)) {
            "Enum Class ${targetClass.simpleName} must implement BitEnum"
        }

        val enumConstants = targetClass.java.enumConstants as Array<BitEnum>
        return enumConstants.random()
    }

    override fun handleDummy(property: KProperty<*>, param: KParameter, buffer: ByteBuffer, value: Any) {
        val annotation = property.findAnnotation<ParseEnum>() ?: param.findAnnotation<ParseEnum>()!!
        val targetClass = param.type.classifier as? KClass<*>
            ?: throw IllegalArgumentException("Unsupported property '${property.name}'")

        require(BitEnum::class.java.isAssignableFrom(targetClass.java)) {
            "Enum Class ${targetClass.simpleName} must implement BitEnum"
        }

        val enumValue = value as? BitEnum
            ?: throw IllegalArgumentException("Value must implement BitEnum for property '${property.name}'")

        val byteStart = annotation.byteStart
        val byteEnd = annotation.byteEnd

        if (annotation.bitRange.size == 2) {
            // bitRange가 있는 경우 기존 로직 사용
            var bitValue = enumValue.bit and 0xFF // 부호 없는 값으로 변환
            val startBit = annotation.bitRange[0]
            val endBit = annotation.bitRange[1]
            val bitLength = endBit - startBit + 1

            val maxAllowedValue = (1 shl bitLength) - 1

            if (bitValue > maxAllowedValue) {
                println("Warning: bitValue ($bitValue) exceeds allowed range ($maxAllowedValue) for property '${property.name}'. Using UNKNOWN value.")
                bitValue = maxAllowedValue // UNKNOWN 값 사용
            }

            buffer.position(byteStart)
            buffer.order(ByteOrder.BIG_ENDIAN)

            val currentByte = buffer.get(byteStart).toInt() and 0xFF
            var binaryString = String.format("%8s", currentByte.toString(2)).replace(' ', '0')

            val newBits = String.format("%${bitLength}s", bitValue.toString(2)).replace(' ', '0')
            binaryString = buildString {
                append(binaryString.substring(0, startBit))
                append(newBits)
                append(binaryString.substring(endBit + 1))
            }

            val updatedByte = binaryString.toInt(2).toByte()
            buffer.put(byteStart, updatedByte)
        } else {
            val slicedBytes = ByteArray(byteEnd - byteStart + 1)
            buffer.position(byteStart)
            buffer.get(slicedBytes, 0, slicedBytes.size)

            val number = enumValue.bit
            // 변환된 숫자를 바이트 배열로 변환
            val numberBytes = ByteBuffer.allocate(4).putInt(number).array().takeLast(slicedBytes.size).toByteArray()
            // byteStart와 byteEnd를 고려하여 buffer에 숫자 바이트 배열을 넣음
            buffer.position(byteStart)
            buffer.put(numberBytes)
        }

//        println(
//            """
//        Property '${property.name}' written with:
//        - bitValue: $bitValue
//        - startBit: $startBit
//        - endBit: $endBit
//        - bitLength: $bitLength
//        - maxAllowedValue: $maxAllowedValue
//        - binaryString (after): $binaryString
//        - updatedByte (16진수): ${String.format("%02x", updatedByte)}
//        """.trimIndent()
//        )
    }
}