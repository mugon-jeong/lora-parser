data class SensorStatus(
    val watchDog: Boolean,
    val wakeUp: Boolean,
    val readyToSleep: Boolean,
    val triggerSensorEvent: Boolean,
    val downlinkAck: Boolean,
    val triggerBatteryStatus: Boolean,
    val battery: Boolean,
    val power: Boolean
) {
    companion object {
        /**
         * 주어진 바이트 배열을 기반으로 SensorStatus 객체를 생성합니다.
         *
         * @param bytes 상태 정보를 포함한 바이트 배열.
         * @return SensorStatus 객체.
         */
        fun fromBytes(bytes: List<Byte>): SensorStatus {
            require(bytes.size == 1) { "SensorStatus requires exactly 1 byte to parse." }
            val binaryString = bytes[0].toInt().and(0xFF).toString(2).padStart(8, '0')
            return SensorStatus(
                watchDog = binaryString[0] == '1',
                wakeUp = binaryString[1] == '1',
                readyToSleep = binaryString[2] == '1',
                triggerSensorEvent = binaryString[3] == '1',
                downlinkAck = binaryString[4] == '1',
                triggerBatteryStatus = binaryString[5] == '1',
                battery = binaryString[6] == '1',
                power = binaryString[7] == '1'
            )
        }
    }
}