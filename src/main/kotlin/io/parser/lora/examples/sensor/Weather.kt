package io.parser.lora.examples.sensor

import io.parser.lora.annotation.ParseHex
import io.parser.lora.annotation.ParseStatus
import io.parser.lora.enums.HexConverterType
import io.parser.lora.examples.status.SensorStatus
import io.parser.lora.LoraParsable
import io.parser.lora.annotation.DevEUI
import io.parser.lora.annotation.FwVersion
import io.parser.lora.annotation.LoraParser
import java.math.BigDecimal
import java.nio.ByteBuffer
import kotlin.random.Random

@LoraParser
data class Weather(
    @DevEUI
    override val devEUI: String, // 장치 고유 식별자

    @ParseHex(byteStart = 0, byteEnd = 0)
    val payloadType: Long, // 페이로드 타입

    @ParseStatus(byteStart = 1, byteEnd = 1)
    val status: SensorStatus, // 상태 (비트별 상태)

    @ParseHex(byteStart = 2, byteEnd = 2)
    val ackId: Long, // ACK ID

    @ParseHex(byteStart = 3, byteEnd = 4)
    val serviceType: Int, // 두 바이트를 결합한 값

    @ParseHex(byteStart = 5, byteEnd = 6, converter = HexConverterType.DIVIDE, scale = 10.0)
    val temperature: BigDecimal, // 온도 (소수점 1자리)

    @ParseHex(byteStart = 7, byteEnd = 8, converter = HexConverterType.DIVIDE, scale = 10.0)
    val humidity: BigDecimal, // 습도 (소수점 1자리)

    @ParseHex(byteStart = 9, byteEnd = 10, converter = HexConverterType.DIVIDE, scale = 10.0)
    val windSpeed: BigDecimal, // 풍속 (소수점 1자리)

    @ParseHex(byteStart = 11, byteEnd = 12)
    val windDirection: Int, // 풍향 (정수값, 소수점 없음)

    @ParseHex(byteStart = 13, byteEnd = 14, converter = HexConverterType.DIVIDE, scale = 10.0)
    val magneticNorth: BigDecimal, // 자기 북극 방향 (소수점 1자리)

    @ParseHex(byteStart = 15, byteEnd = 16, converter = HexConverterType.DIVIDE, scale = 10.0)
    val pressure: BigDecimal, // 기압 (소수점 1자리)

    @ParseHex(byteStart = 17, byteEnd = 18, converter = HexConverterType.DIVIDE, scale = 100.0)
    val rainfall: BigDecimal, // 강우량 (소수점 2자리)

    @ParseHex(byteStart = 19, byteEnd = 20, converter = HexConverterType.DIVIDE, scale = 10.0)
    val voltage: BigDecimal, // 전압 (소수점 1자리)

    @ParseHex(byteStart = 21, byteEnd = 22)
    val fwServiceType: Int, // 펌웨어 서비스 타입

    @FwVersion(byteStart = 23, byteEnd = 24)
    val fwVersion: String // 펌웨어 버전
) : LoraParsable {
    companion object {
        fun fromLora(devEUI: String, log: String): Weather {
            return LoraParsable.parse<Weather>(devEUI, log)
        }

        /**
         * 더미 데이터를 생성하는 메서드
         * @param devEUI 장치 고유 식별자
         * @return 생성된 바이트 배열
         */
        fun generate(devEUI: String): ByteArray {
            val buffer = ByteBuffer.allocate(25) // 전체 바이트 크기: 25바이트

            buffer.put(0, Random.nextLong(0, 256).toByte()) // payloadType (1바이트)
            buffer.put(1, Random.nextInt(0, 256).toByte()) // status (1바이트)
            buffer.put(2, Random.nextLong(0, 256).toByte()) // ackId (1바이트)
            buffer.putShort(3, Random.nextInt(0, 65536).toShort()) // serviceType (2바이트)
            buffer.putShort(5, (Random.nextDouble(0.0, 100.0) * 10).toInt().toShort()) // temperature (2바이트)
            buffer.putShort(7, (Random.nextDouble(0.0, 100.0) * 10).toInt().toShort()) // humidity (2바이트)
            buffer.putShort(9, (Random.nextDouble(0.0, 50.0) * 10).toInt().toShort()) // windSpeed (2바이트)
            buffer.putShort(11, Random.nextInt(0, 360).toShort()) // windDirection (2바이트)
            buffer.putShort(13, (Random.nextDouble(0.0, 100.0) * 10).toInt().toShort()) // magneticNorth (2바이트)
            buffer.putShort(15, (Random.nextDouble(900.0, 1100.0) * 10).toInt().toShort()) // pressure (2바이트)
            buffer.putShort(17, (Random.nextDouble(0.0, 500.0) * 100).toInt().toShort()) // rainfall (2바이트)
            buffer.putShort(19, (Random.nextDouble(0.0, 50.0) * 10).toInt().toShort()) // voltage (2바이트)
            buffer.putShort(21, Random.nextInt(0, 65536).toShort()) // fwServiceType (2바이트)
            buffer.putShort(23, Random.nextInt(0, 65536).toShort()) // fwVersion (2바이트)

            return buffer.array()
        }
    }
}