package io.parser.lora.utils

import java.util.*

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
    val major = (version shr 12) and 0xF  // 상위 4비트
    val minor = (version shr 8) and 0xF   // 하위 4비트
    val patch = version and 0xFF          // 나머지 8비트

    // 버전 문자열 반환
    return "V$major.$minor.$patch"
}

/**
 * fwVersion 문자열을 Short 타입으로 변환하는 함수
 * @param version 버전 문자열 (예: "V2.7.16")
 * @return Short 타입으로 변환된 값
 */
fun parseFwVersionToShort(version: String): Short {
    // "V2.7.16" 형태의 문자열에서 숫자 부분만 추출
    val numericParts = version.removePrefix("V").split(".")
    require(numericParts.size == 3) { "Invalid fwVersion format. Expected V<major>.<minor>.<patch>" }

    val (major, minor, patch) = numericParts.map { it.toInt() }

    // Short 형식으로 변환 (상위 4비트: major, 하위 4비트: minor, 나머지 8비트: patch)
    val combined = ((major and 0xF) shl 12) or ((minor and 0xF) shl 8) or (patch and 0xFF)

    return combined.toShort()
}