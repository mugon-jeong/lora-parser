
fun main() {
    println("Hello, World!")
    val parseSensorData = parseSensorData<SensorData>(
        devEUI = "fdc388ffff2a0ea1",
        log = "AgAKAyAJFMduFkwl0KpTAA=="
    )
    println(parseSensorData)
}