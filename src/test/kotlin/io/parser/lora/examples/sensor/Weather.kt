package io.parser.lora.examples.sensor

import io.parser.lora.Dummy
import io.parser.lora.annotation.ParseHex
import io.parser.lora.annotation.ParseStatus
import io.parser.lora.enums.HexConverterType
import io.parser.lora.examples.status.SensorStatus
import io.parser.lora.LoraParsable
import io.parser.lora.annotation.DevEUI
import io.parser.lora.annotation.FwVersion
import io.parser.lora.annotation.LoraParser
import io.parser.lora.utils.parseFwVersionToShort
import java.math.BigDecimal
import java.nio.ByteBuffer

@LoraParser(size = 25)
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
         * 리플렉션을 사용해 랜덤 값을 생성하는 함수
         * @param devEUI 장치 고유 식별자
         * @return 랜덤 Weather 객체
         */
        fun random(devEUI: String): Weather {
            return LoraParsable.random(devEUI) { klass ->
                when (klass) {
                    SensorStatus::class -> SensorStatus.random() // SensorStatus에 대해 커스텀 랜덤 생성기 사용
                    else -> null // 기본 생성 로직 사용
                }
            }
        }
    }

    /**
     * Weather 객체를 Dummy 객체로 변환하는 함수
     */
    fun toDummy(): Dummy {
        return LoraParsable.toDummy(this)
    }
}