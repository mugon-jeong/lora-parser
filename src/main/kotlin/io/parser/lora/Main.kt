package io.parser.lora

import io.parser.lora.examples.sensor.SafeGas
import io.parser.lora.examples.sensor.SafetySignal
import io.parser.lora.examples.sensor.Weather
import io.parser.lora.examples.status.SensorStatus
import java.util.Base64

fun main() {
//    val safetySignal = SafetySignal.fromLora("fdc388ffff2a0ea1", "AgAKAyAJFMduFkwl0KpTAA==")
//    println(safetySignal)

//    val safeGas = SafeGas.fromLora("G4b9rj0BHCE=","BAMiAEEAAwAAAAAAZAAAAAAAAAAUAAAAMgAAAAAAAAAAAAEJ0nfo")
//    println(safeGas)
//        val real = Weather.fromLora("G4b9rj0BHCE=", "AtMXAAwCUgHzAOQAPgmsIokWMgEnAAknEA==")
//        println(real)
    val status = SensorStatus(
        watchDog = true,
        wakeUp = false,
        readyToSleep = true,
        triggerSensorEvent = false,
        downlinkAck = true,
        triggerBatteryStatus = false,
        battery = true,
        power = false,
    )

    val byte = status.toByte()
    println("SensorStatus toByte: ${byte.toString(2).padStart(8, '0')}") // 기대: "10101010"

    val parsedStatus = ByteParsable.parseBytes<SensorStatus>(listOf(byte))
    println("Parsed SensorStatus: $parsedStatus")
    val test = Weather.random("1b86fdae3d011c21")
    println(test)
    val dummy = test.toDummy()
    val weather = Weather.fromLora(dummy.devEUI, dummy.lora)
    println(weather)
}