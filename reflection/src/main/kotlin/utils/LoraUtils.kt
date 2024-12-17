package utils

import annotation.ParseHex
import kotlin.reflect.KClass

/**
 * 두 개의 바이트를 결합하여 펌웨어 버전 문자열을 생성합니다.
 *
 * 바이트 배열에서 상위 4비트(major), 하위 4비트(minor), 나머지 8비트(patch)를 추출해
 * 버전 형식 "V{major}.{minor}.{patch}"으로 변환합니다.
 *
 * @param rawBytes 펌웨어 버전을 나타내는 두 개의 바이트 리스트.
 *                 - 예: [0x56, 0x78] → 결합된 값 0x5678
 * @return 펌웨어 버전을 나타내는 문자열. 예: "V5.6.120"
 *
 * @throws IllegalArgumentException 두 개의 바이트가 제공되지 않은 경우 예외를 발생시킵니다.
 */
fun parseFwVersion(rawBytes: List<Byte>): String {
    require(rawBytes.size == 2) { "annotation.FwVersion requires exactly 2 bytes to parse." }

    // 두 개의 바이트를 결합하여 16비트 정수 생성
    val version = ((rawBytes[0].toInt() and 0xFF) shl 8) or (rawBytes[1].toInt() and 0xFF)

    // 상위 4비트(major), 하위 4비트(minor), 나머지 8비트(patch) 추출
    val major = (version shr 12) and 0xF
    val minor = (version shr 8) and 0xF
    val patch = version and 0xFF

    // 버전 문자열 반환
    return "V$major.$minor.$patch"
}