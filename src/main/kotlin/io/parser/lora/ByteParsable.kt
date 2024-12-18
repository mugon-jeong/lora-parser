package io.parser.lora

import io.parser.lora.annotation.BitField
import kotlin.random.Random
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * ByteParsable 인터페이스는 바이트 데이터를 비트 필드 기반으로 파싱하기 위한 기능을 제공합니다.
 *
 * 이 인터페이스는 주어진 바이트 데이터를 비트 필드 문자열로 변환하고,
 * 각 비트 위치를 Boolean 값으로 매핑하는 메서드를 포함합니다.
 */
interface ByteParsable {
    companion object {
        /**
         * 바이트 리스트를 비트 필드 문자열로 변환합니다.
         *
         * @param bytes 변환할 바이트 리스트. 반드시 1바이트 크기를 가져야 합니다.
         * @return 비트 필드 문자열 (8비트).
         * @throws IllegalArgumentException 입력된 바이트 리스트가 1바이트가 아닌 경우 예외를 발생시킵니다.
         *
         * 예시:
         * - 입력: [0xA5] → 출력: "10100101"
         */
        fun parseBitField(bytes: List<Byte>): String {
            require(bytes.size == 1) { "Parsing requires exactly 1 byte." }
            return bytes[0].toInt().and(0xFF).toString(2).padStart(8, '0').reversed()
        }

        /**
         * 비트 필드 문자열의 특정 위치의 값을 Boolean으로 변환합니다.
         *
         * @param binaryString 비트 필드 문자열 (8비트).
         * @param position 읽을 비트 위치 (0부터 시작).
         * @return 비트 값이 '1'이면 true, '0'이면 false를 반환합니다.
         * @throws IndexOutOfBoundsException 비트 위치가 문자열 범위를 벗어날 경우 예외를 발생시킵니다.
         *
         * 예시:
         * - 입력: "10100101", position = 0 → 출력: true
         * - 입력: "10100101", position = 3 → 출력: false
         */
        fun getBitAsBoolean(binaryString: String, position: Int): Boolean {
            return binaryString[position] == '1'
        }

        /**
         * 리플렉션을 사용하여 클래스 [T]의 생성자 파라미터에 설정된 @BitField 어노테이션을 기반으로 객체를 생성합니다.
         *
         * @param T 파싱할 클래스 타입. 필드에는 반드시 `@BitField` 어노테이션이 설정되어 있어야 합니다.
         * @param bytes 파싱할 바이트 리스트. 1바이트 크기의 입력이 필요합니다.
         * @return [T] 타입의 객체를 반환합니다.
         * @throws IllegalArgumentException 어노테이션이 없는 필드가 존재하거나 필드 매핑에 실패한 경우 예외를 발생시킵니다.
         *
         * 예시 사용법:
         * ```
         * @BitField(bitPosition = 0)
         * val exampleFlag: Boolean
         *
         * val parsedObject = ByteParsable.parseBytes<MyClass>(listOf(0xA5.toByte()))
         * println(parsedObject)
         * ```
         */
        inline fun <reified T : Any> parseBytes(bytes: List<Byte>): T {
            val binaryString = parseBitField(bytes)
            val clazz = T::class
            val constructor = clazz.constructors.first()

            val args = constructor.parameters.associateWith { param ->
                val property = clazz.members.find { it.name == param.name }
                val bitField = property?.annotations?.filterIsInstance<BitField>()?.firstOrNull()

                if (bitField != null) {
                    // Boolean 타입인지 검증
                    if (param.type.classifier == Boolean::class) {
                        getBitAsBoolean(binaryString, bitField.bitPosition)
                    } else {
                        throw IllegalArgumentException("Property ${param.name} annotated with @BitField must be of type Boolean")
                    }
                } else {
                    throw IllegalArgumentException("Property ${param.name} must be annotated with @io.parser.lora.annotation.BitField")
                }
            }

            return constructor.callBy(args)
        }

        /**
         * Reflection을 사용하여 클래스 [T]의 @BitField 어노테이션에 맞게
         * Byte 값을 생성합니다.
         *
         * @param instance 변환할 객체. 객체의 프로퍼티 값을 기반으로 Byte를 생성하기 위해 필요합니다.
         *                 Reflection으로 프로퍼티의 정의는 가져올 수 있지만, 특정 객체의 값을 읽으려면
         *                 해당 객체(`instance`)를 기준으로 값을 읽어야 합니다.
         *
         * @return 변환된 Byte 값
         * @throws IllegalArgumentException 어노테이션이 잘못되었거나 필드 값이 Boolean이 아닌 경우
         *
         * 예시:
         * ```
         * val sensorStatus = SensorStatus(
         *     watchDog = true,
         *     wakeUp = false,
         *     readyToSleep = true,
         *     triggerSensorEvent = false,
         *     downlinkAck = true,
         *     triggerBatteryStatus = false,
         *     battery = true,
         *     power = false
         * )
         *
         * val byteValue = ByteParsable.toByte(sensorStatus)
         * println(byteValue) // 0b10101001
         * ```
         */
        inline fun <reified T : Any> toByte(instance: T): Byte {
            var byte = 0

            // @BitField가 적용된 프로퍼티들을 가져옴
            T::class.memberProperties.forEach { property ->
                val bitField = property.findAnnotation<BitField>()
                if (bitField != null) {
                    // Boolean 값인지 확인
                    val value = property.get(instance)
                    require(value is Boolean) {
                        "Property ${property.name} must be of type Boolean to use @BitField"
                    }

                    // bitPosition에 따라 비트 설정
                    if (value) {
                        byte = byte or (1 shl bitField.bitPosition)
                    }
                }
            }
            return byte.toByte()
        }

        inline fun <reified T : Any> generateRandomInstance(): T {
            val clazz = T::class
            val constructor = clazz.constructors.first()

            // 생성자 파라미터와 매칭하여 값 설정
            val args = constructor.parameters.associateWith { param ->
                val property = clazz.members.find { it.name == param.name }
                val bitField = property?.annotations?.filterIsInstance<BitField>()?.firstOrNull()

                if (bitField != null) {
                    // Boolean 타입인지 검증
                    if (param.type.classifier == Boolean::class) {
                        Random.nextBoolean()
                    } else {
                        throw IllegalArgumentException("Property ${param.name} annotated with @BitField must be of type Boolean")
                    }
                } else {
                    throw IllegalArgumentException("Property ${param.name} must be annotated with @BitField")
                }
            }

            return constructor.callBy(args)
        }
    }
}