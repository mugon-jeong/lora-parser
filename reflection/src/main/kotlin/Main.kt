import examples.sensor.SafeGas
import examples.sensor.SafetySignal
import examples.sensor.Weather

fun main() {
    val safetySignal = SafetySignal.fromLora("fdc388ffff2a0ea1", "AgAKAyAJFMduFkwl0KpTAA==")
    println(safetySignal)
    val weather = Weather.fromLora("G4b9rj0BHCE=", "AtMXAAwCUgHzAOQAPgmsIokWMgEnAAknEA==")
    println(weather)
    val safeGas = SafeGas.fromLora("G4b9rj0BHCE=","BAMiAEEAAwAAAAAAZAAAAAAAAAAUAAAAMgAAAAAAAAAAAAEJ0nfo")
    println(safeGas)
}