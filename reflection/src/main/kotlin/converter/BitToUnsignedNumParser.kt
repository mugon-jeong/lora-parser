package converter

object BitToUnsignedNumParser {

    /**
     * 바이트 배열의 특정 비트 영역을 무부호 숫자로 변환합니다.
     *
     * @param bytes 입력 바이트 배열.
     * @param bitIndex 시작 비트 인덱스.
     * @param bitSize 추출할 비트 길이.
     * @return 비트 영역의 무부호 정수 값.
     */
    fun convert(bytes: ByteArray, bitIndex: Int, bitSize: Int): Int {
        val bits = byteToBitString(bytes) // 바이트 배열 → 비트 문자열
        val bitSubString = bits.substring(bitIndex, bitIndex + bitSize) // 비트 영역 추출
        return bitSubString.toInt(2) // 2진수 문자열 → 정수 변환
    }

    /**
     * 바이트 배열을 비트 문자열로 변환합니다.
     *
     * @param bytes 입력 바이트 배열.
     * @return 비트 문자열 (0과 1로 구성된 문자열).
     */
    private fun byteToBitString(bytes: ByteArray): String {
        return bytes.joinToString("") { byte ->
            String.format("%8s", Integer.toBinaryString(byte.toInt() and 0xFF)).replace(' ', '0')
        }
    }
}