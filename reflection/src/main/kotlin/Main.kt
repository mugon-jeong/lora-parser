fun main() {
    val parseSafetySignal = parseSensorData<SafetySignal>(
        devEUI = "fdc388ffff2a0ea1",
        log = "AgAKAyAJFMduFkwl0KpTAA==",
    )
    println(parseSafetySignal)
    val weather = parseSensorData<Weather>(
        devEUI = "G4b9rj0BHCE=",
        log = "AtMXAAwCUgHzAOQAPgmsIokWMgEnAAknEA==",
    )
    println(weather)
}