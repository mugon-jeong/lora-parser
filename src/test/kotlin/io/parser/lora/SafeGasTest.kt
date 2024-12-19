package io.parser.lora

import io.parser.lora.examples.TestUtils
import io.parser.lora.examples.sensor.SafeGas
import io.parser.lora.examples.status.GasAlarmStatus
import io.parser.lora.examples.status.SensorStatus
import io.parser.lora.registry.ParseStatusRegistry
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SafeGasTest {
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
        val random = SafeGas.random("fdc388ffff2a0ea1")
        println(random)
        val dummy = random.toDummy()
        println(dummy)
        val safeGas = SafeGas.fromLora(dummy.devEUI, dummy.lora)
        println(safeGas)

        // 리플렉션을 사용하여 비교
        val differences = TestUtils.compareObjects(random, safeGas)

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