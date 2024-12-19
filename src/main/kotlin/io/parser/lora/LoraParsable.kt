package io.parser.lora

import io.parser.lora.annotation.DevEUI
import io.parser.lora.annotation.LoraParser
import io.parser.lora.registry.AnnotationHandlerRegistry
import io.parser.lora.utils.HexUtils.base64ToByteArray
import io.parser.lora.utils.hexToByteArray
import io.parser.lora.utils.toBase64
import java.nio.ByteBuffer
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

interface LoraParsable {
    val devEUI: String

    companion object {
        /**
         * Base64로 인코딩된 `devEUI`와 센서 데이터 `log`를 기반으로 클래스 [T]를 파싱합니다.
         *
         * 파싱은 리플렉션과 등록된 핸들러(`AnnotationHandlerRegistry`)를 사용하여 이루어집니다.
         * 클래스 필드에는 처리에 필요한 어노테이션이 지정되어야 합니다.
         *
         * @param T 파싱될 데이터 클래스의 타입. 반드시 `@LoraParser` 어노테이션이 선언되어야 합니다.
         * @param devEUI 장치 고유 식별자 (Base64로 인코딩된 문자열).
         * @param log 센서 데이터 로그 (Base64로 인코딩된 문자열).
         * @return 파싱된 [T] 타입의 객체.
         *
         * @throws IllegalArgumentException
         * - 클래스에 `@LoraParser` 어노테이션이 없는 경우
         * - 필드에 필요한 어노테이션이 선언되지 않은 경우
         * - 등록된 핸들러가 필드를 처리하지 못할 경우
         */
        inline fun <reified T : LoraParsable> parse(devEUI: String, log: String): T {
            val clazz = T::class
            val devEuiBytes = base64ToByteArray(devEUI)
            val data = base64ToByteArray(log)

            require(clazz.annotations.any { it is LoraParser }) {
                "Class ${clazz.simpleName} is not annotated with @annotation.LoraParser"
            }

            val constructor = clazz.constructors.first()
            val args = constructor.parameters.associateWith { param ->
                val property = clazz.memberProperties.find { it.name == param.name }
                    ?: throw IllegalArgumentException("Property ${param.name} not found in class ${clazz.simpleName}")
                property.isAccessible = true

                // 핸들러를 순회하며 처리
                val handler = AnnotationHandlerRegistry.handlers.find { it.canHandle(property, param) }
                    ?: throw IllegalArgumentException("Unsupported property '${property.name}' in class ${clazz.simpleName}")
                handler.handle(property, param, data, devEuiBytes)
            }

            return constructor.callBy(args)
        }

        /**
         * 리플렉션을 사용해 클래스 [T]의 인스턴스를 랜덤 값으로 생성하는 함수
         * @param devEUI 장치 고유 식별자 (선택적)
         * @return 랜덤 [T] 객체
         */
        inline fun <reified T : Any> random(devEUI: String, crossinline customRandomProvider: (KClass<*>) -> Any? = { null }): T {
            val clazz = T::class
            require(clazz.isData) { "Only data classes are supported." }
            val constructor = clazz.primaryConstructor
                ?: throw IllegalArgumentException("Class must have a primary constructor")

            val args = constructor.parameters.associateWith { param ->
                val property = clazz.memberProperties.find { it.name == param.name }
                    ?: throw IllegalArgumentException("Property ${param.name} not found in class ${clazz.simpleName}")
                property.isAccessible = true

                // 핸들러를 순회하며 처리
                val handler = AnnotationHandlerRegistry.handlers.find { it.canHandle(property, param) }
                    ?: throw IllegalArgumentException("Unsupported property '${property.name}' in class ${clazz.simpleName}")
                handler.random(property, param, devEUI) {
                    customRandomProvider(it)
                }
            }

            return constructor.callBy(args)
        }

        inline fun <reified T : Any> toDummy(instance: T): Dummy {
            val clazz = T::class

            // @DevEUI 필드 확인
            val devEUI = clazz.memberProperties
                .find { it.findAnnotation<DevEUI>() != null }
                ?.get(instance) as? String ?: throw IllegalArgumentException("Missing @DevEUI field")

            // @LoraParser에서 size 가져오기
            val size = clazz.annotations.filterIsInstance<LoraParser>().firstOrNull()?.size
                ?: throw IllegalArgumentException("@LoraParser annotation with 'size' property is required.")

            // ByteBuffer 초기화
            val loraPayload = ByteArray(size)
            val buffer = ByteBuffer.wrap(loraPayload)

            val constructor = clazz.constructors.first()
            constructor.parameters.forEach { param ->
                val property = clazz.memberProperties.find { it.name == param.name } ?: return@forEach
                if (property.findAnnotation<DevEUI>() != null) return@forEach
                property.isAccessible = true
                val value = property.get(instance) ?: return@forEach
                val handler = AnnotationHandlerRegistry.handlers.find { it.canHandle(property, param) }
                    ?: throw IllegalArgumentException("Unsupported property '${property.name}' in class ${clazz.simpleName}")
                handler.handleDummy(property, param, buffer, value)
//                println("Property: ${property.name}, Value: $value, Buffer Position: ${buffer.position()}")
            }
//            println("Final Buffer Position: ${buffer.position()}, Expected Size: $size")


            // Dummy 객체 생성 및 반환
            return Dummy(
                devEUI = devEUI.hexToByteArray().toBase64(),
                lora = Base64.getEncoder().encodeToString(loraPayload),
            )
        }
    }
}