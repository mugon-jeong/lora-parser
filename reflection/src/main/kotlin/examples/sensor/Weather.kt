package examples.sensor

import annotation.DevEUI
import annotation.FwVersion
import annotation.LoraParser
import annotation.ParseHex
import annotation.ParseStatus
import enums.HexConverterType
import examples.status.SensorStatus
import lora.LoraParsable
import java.math.BigDecimal

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
    }
}