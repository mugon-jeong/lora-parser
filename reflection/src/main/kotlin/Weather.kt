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

    @ParseHex(byteStart = 5, byteEnd = 6, scale = 10.0)
    val temperature: Double, // 온도 (소수점 1자리)

    @ParseHex(byteStart = 7, byteEnd = 8, scale = 10.0)
    val humidity: Double, // 습도 (소수점 1자리)

    @ParseHex(byteStart = 9, byteEnd = 10, scale = 10.0)
    val windSpeed: Double, // 풍속 (소수점 1자리)

    @ParseHex(byteStart = 11, byteEnd = 12)
    val windDirection: Int, // 풍향 (정수값, 소수점 없음)

    @ParseHex(byteStart = 13, byteEnd = 14, scale = 10.0)
    val magneticNorth: Double, // 자기 북극 방향 (소수점 1자리)

    @ParseHex(byteStart = 15, byteEnd = 16, scale = 10.0)
    val pressure: Double, // 기압 (소수점 1자리)

    @ParseHex(byteStart = 17, byteEnd = 18, scale = 100.0)
    val rainfall: Double, // 강우량 (소수점 2자리)

    @ParseHex(byteStart = 19, byteEnd = 20, scale = 10.0)
    val voltage: Double, // 전압 (소수점 1자리)

    @ParseHex(byteStart = 21, byteEnd = 22)
    val fwServiceType: Int, // 펌웨어 서비스 타입

    @FwVersion(byteStart = 23, byteEnd = 24)
    val fwVersion: String // 펌웨어 버전
) : BaseSensor