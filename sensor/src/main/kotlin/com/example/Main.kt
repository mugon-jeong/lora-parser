package com.example

import com.example.utils.base64ToByteArray

fun main() {
    println("Hello, World!")

    // SensorData 생성
    val sensorData = SensorData.from(
        devEUI = base64ToByteArray("fdc388ffff2a0ea1"),
        byteArray = base64ToByteArray("AgAKAyAJFMduFkwl0KpTAA==")
    )
    println(sensorData)
}