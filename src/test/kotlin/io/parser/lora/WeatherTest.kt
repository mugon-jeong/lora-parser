package io.parser.lora

import io.parser.lora.examples.sensor.Weather
import io.parser.lora.examples.status.GasAlarmStatus
import io.parser.lora.examples.status.SensorStatus
import io.parser.lora.registry.ParseStatusRegistry
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.reflect.full.memberProperties

class WeatherTest {
    @BeforeEach
    fun init() {
        ParseStatusRegistry.parseStatusRegister<ByteParsable> {
            listOf(
                SensorStatus::class to { bytes -> SensorStatus.fromBytes(bytes) },
                GasAlarmStatus::class to { bytes -> GasAlarmStatus.fromBytes(bytes) },
            )
        }
    }

    @Test
    fun test() {
        val test = Weather.random("1b86fdae3d011c21")
        println(test)
        val dummy = test.toDummy()
        val weather = Weather.fromLora(dummy.devEUI, dummy.lora)
        println(weather)

        // 리플렉션을 사용하여 비교
        val differences = compareObjects(test, weather)

        if (differences.isEmpty()) {
            println("Objects are identical!")
        } else {
            println("Objects are different:")
            differences.forEach { (property, pair) ->
                println("Property '$property' is different: Expected=${pair.first}, Actual=${pair.second}")
            }
        }

        // 테스트 통과 여부 확인
        assertTrue(differences.isEmpty(), "Objects are not identical. Differences: $differences")
    }

    private fun compareObjects(obj1: Any, obj2: Any): Map<String, Pair<Any?, Any?>> {
        val differences = mutableMapOf<String, Pair<Any?, Any?>>()
        val obj1Class = obj1::class
        val obj2Class = obj2::class

        // 클래스가 다를 경우 차이점을 추가
        if (obj1Class != obj2Class) {
            differences["Class Type"] = Pair(obj1Class.simpleName, obj2Class.simpleName)
            return differences
        }

        // 두 객체의 모든 속성을 리플렉션으로 가져와 비교
        obj1Class.memberProperties.forEach { property ->
            val value1 = property.getter.call(obj1)
            val value2 = property.getter.call(obj2)

            // BigDecimal 타입은 compareTo로 비교
            if (value1 is BigDecimal && value2 is BigDecimal) {
                if (value1.compareTo(value2) != 0) {
                    differences[property.name] = Pair(value1, value2)
                }
            } else if (value1 != value2) { // 일반 비교
                differences[property.name] = Pair(value1, value2)
            }
        }

        return differences
    }
}