package io.parser.lora

import io.parser.lora.examples.TestUtils
import io.parser.lora.examples.sensor.Weather
import io.parser.lora.examples.status.GasAlarmStatus
import io.parser.lora.examples.status.SensorStatus
import io.parser.lora.registry.ParseStatusRegistry
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
        val differences = TestUtils.compareObjects(test, weather)

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
}