package io.parser.lora

import io.parser.lora.examples.sensor.SafeGas
import io.parser.lora.examples.sensor.SafetySignal
import io.parser.lora.examples.sensor.Weather
import java.util.Base64

fun main() {
//    val safetySignal = SafetySignal.fromLora("fdc388ffff2a0ea1", "AgAKAyAJFMduFkwl0KpTAA==")
//    println(safetySignal)
//    val weather = Weather.fromLora("G4b9rj0BHCE=", "AtMXAAwCUgHzAOQAPgmsIokWMgEnAAknEA==")
//    println(weather)
//    val safeGas = SafeGas.fromLora("G4b9rj0BHCE=","BAMiAEEAAwAAAAAAZAAAAAAAAAAUAAAAMgAAAAAAAAAAAAEJ0nfo")
//    println(safeGas)
        val test = Weather.generate("G4b9rj0BHCE=")
        val weather = Weather.fromLora("G4b9rj0BHCE=", Base64.getEncoder().encodeToString(test))
        println(weather)
}