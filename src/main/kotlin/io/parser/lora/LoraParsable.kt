package io.parser.lora

import io.parser.lora.annotation.DevEUI
import io.parser.lora.annotation.LoraParser
import io.parser.lora.annotation.ParseStatus
import io.parser.lora.provider.RandomProvider
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
         * 클래스의 모든 필드 이름을 가져옵니다.
         */
        fun <T : LoraParsable> getAvailableKeys(clazz: KClass<T>): List<String> {
            return clazz.memberProperties.map { it.name }
        }
        /**
         * 제공된 키의 유효성을 검증합니다.
         * @param clazz 검증할 데이터 클래스
         * @param providedKeys 제공된 키 목록
         * @throws IllegalArgumentException 유효하지 않은 키가 포함된 경우 예외를 발생
         */
        fun <T : LoraParsable> validateKeys(clazz: KClass<T>, providedKeys: Set<String>) {
            val availableKeys = getAvailableKeys(clazz).toSet()
            val invalidKeys = providedKeys - availableKeys
            require(invalidKeys.isEmpty()) {
                "Invalid keys: $invalidKeys. Available keys are: $availableKeys"
            }
        }
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
        inline fun <reified T : LoraParsable> random(
            devEUI: String,
            providers: Map<String, RandomProvider<*>> = emptyMap(),
            crossinline classBasedRandomProvider: (KClass<*>) -> Any? = { null }
        ): T {
            val clazz = T::class

            // 키 검증
            validateKeys(clazz, providers.keys)

            val constructor = clazz.primaryConstructor
                ?: throw IllegalArgumentException("Class must have a primary constructor")

            val args = constructor.parameters.associateWith { param ->
                val property = clazz.memberProperties.find { it.name == param.name }
                    ?: throw IllegalArgumentException("Property ${param.name} not found in class ${clazz.simpleName}")
                property.isAccessible = true

                // 필드 이름 기반 프로바이더 확인
                val fieldRandomProvider = providers[param.name]?.getRandomValue()

                // 어노테이션 기반 처리: ParseStatus 확인
                val annotationBasedValue = property.annotations.firstNotNullOfOrNull { annotation ->
                    when (annotation) {
                        is ParseStatus -> {
                            val paramType = param.type.classifier as? KClass<*>
                                ?: throw IllegalArgumentException("Invalid type for ParseStatus on property '${property.name}'")
                            if (ByteParsable::class.java.isAssignableFrom(paramType.java)) {
                                ByteParsable.generateRandomInstance(paramType)
                            } else {
                                null
                            }
                        }
                        else -> null
                    }
                }

                // 필드 이름 기반 프로바이더가 없으면 클래스 기반 프로바이더 확인
                // 필드 이름 기반 프로바이더 또는 어노테이션 기반 처리 값 확인
                val value = fieldRandomProvider
                    ?: annotationBasedValue
                    ?: classBasedRandomProvider(param.type.classifier as KClass<*>)

                // 값이 없으면 기본 핸들러 사용
                value ?: run {
                    val handler = AnnotationHandlerRegistry.handlers.find { it.canHandle(property, param) }
                        ?: throw IllegalArgumentException("Unsupported property '${property.name}' in class ${clazz.simpleName}")
                    handler.random(property, param, devEUI) { classBasedRandomProvider(it) }
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