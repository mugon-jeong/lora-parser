# Lora Data Parser

## **프로젝트 개요**
Lora 센서 데이터를 효율적으로 파싱하고 변환하는 시스템입니다. **리플렉션**과 **어노테이션**을 활용하여 다양한 센서 데이터 구조를 지원하며, 확장 가능한 변환기 구조를 통해 **유연한 데이터 처리**가 가능합니다.

---

## **주요 기능**

1. **어노테이션 기반 파싱**
    - `@LoraParser` 어노테이션을 통해 파싱할 데이터 클래스를 정의합니다.
    - 각 필드에 `@ParseHex`, `@ParseEnum`, `@DevEUI` 등의 어노테이션을 붙여 바이트 데이터를 파싱합니다.

2. **다양한 변환기 제공**
    - **부호 있는 숫자 변환** (`ByteToSignedNumberParser`)
    - **무부호 숫자 변환** (`ByteToUnsignedNumParser`)
    - **IEEE 754 변환** (`ByteToIEEE754Converter`)
    - **비트 영역 숫자 변환** (`BitToUnsignedNumParser`)
    - **스케일링 지원** (곱셈 및 나눗셈)

3. **확장 가능한 구조**
    - 새로운 변환기와 어노테이션 핸들러를 쉽게 추가할 수 있습니다.
    - `AnnotationHandlerRegistry`를 통해 새로운 핸들러를 등록하면 즉시 반영됩니다.

4. **유틸리티 제공**
    - 바이트와 문자열 변환 (`toHexFormatted`, `toHexString`)
    - Base64 디코딩 및 숫자 변환 (`HexUtils`)

---

## **주요 어노테이션**

| 어노테이션       | 설명                                 | 주요 속성                           |
|------------------|--------------------------------------|------------------------------------|
| `@LoraParser`   | 데이터 클래스임을 명시               | N/A                                |
| `@DevEUI`       | 장치의 고유 식별자(devEUI) 필드를 명시| N/A                                |
| `@ParseHex`     | 바이트 데이터를 숫자로 변환          | `byteStart`, `byteEnd`, `converter`, `scale` |
| `@ParseEnum`    | 비트 값 또는 바이트 값을 Enum으로 변환| `byteStart`, `byteEnd`, `bitRange`  |
| `@ParseStatus`  | 상태 필드를 파싱                     | `byteStart`, `byteEnd`             |
| `@FwVersion`    | 펌웨어 버전 데이터를 파싱            | `byteStart`, `byteEnd`             |

---

## **예제**

### **1. 데이터 클래스 정의**
```kotlin
@LoraParser
data class SafeGas(
    @DevEUI
    val devEUI: String,
    @ParseHex(byteStart = 1, byteEnd = 1, converter = HexConverterType.UNSIGNED)
    val functionCode: Int,
    @ParseHex(byteStart = 2, byteEnd = 2, converter = HexConverterType.DIVIDE, scale = 100.0)
    val byteCount: BigDecimal,
    @ParseHex(byteStart = 3, byteEnd = 6, converter = HexConverterType.MULTIPLY, scale = 0.000001)
    val value: BigDecimal,
    @ParseEnum(byteStart = 7, byteEnd = 8)
    val gasType: GasType
)
```
---

## **주요 클래스 구조**

### **1. LoraParsable 인터페이스**
Lora 데이터 파싱의 중심 인터페이스로, `parse` 함수를 통해 동적으로 파싱을 수행합니다.

```kotlin
interface LoraParsable {
    val devEUI: String

    companion object {
        inline fun <reified T : LoraParsable> parse(devEUI: String, log: String): T
    }
}
```

### **2. 변환기 (Converters)**

모든 변환 로직은 모듈화된 컨버터를 통해 수행됩니다.

| 변환기                        | 설명                                      |
|------------------------------|-------------------------------------------|
| `ByteToUnsignedNumParser`    | 무부호 숫자 변환 및 스케일/오프셋 적용    |
| `ByteToSignedNumberParser`   | 부호 있는 숫자 변환 및 스케일/오프셋 적용 |
| `ByteToIEEE754Converter`     | IEEE 754 포맷을 Float로 변환              |
| `BitToUnsignedNumParser`     | 비트 필드를 추출하여 무부호 숫자로 변환   |
| `MultiplyConverter`          | 입력 값에 스케일을 곱한 결과 반환         |
| `DivideConverter`            | 입력 값을 스케일로 나눈 결과 반환         |

### **3. AnnotationHandler 및 Registry**

어노테이션별 처리 로직을 분리하고 관리합니다.

#### **AnnotationHandler**
어노테이션 처리 핸들러의 기본 인터페이스입니다.

```kotlin
interface AnnotationHandler {
    fun canHandle(property: KProperty<*>, param: KParameter): Boolean
    fun handle(
        property: KProperty<*>,
        param: KParameter,
        data: ByteArray,
        devEuiBytes: ByteArray
    ): Any?
}
```

#### **AnnotationHandlerRegistry**
모든 핸들러를 등록 및 관리합니다.

```kotlin
object AnnotationHandlerRegistry {
    val handlers = mutableListOf<AnnotationHandler>()

    fun register(handler: AnnotationHandler) {
        handlers.add(handler)
    }
}
```

### **확장 방법**

#### **1. 새로운 변환기 추가**

1. **변환기 생성**
   새 변환기를 객체로 추가합니다.

```kotlin
object CustomConverter {
    fun convert(bytes: ByteArray, scale: Double): BigDecimal {
        // 변환 로직
        return BigDecimal.valueOf(bytes.toLong() * scale)
    }
}
```

2. **HexConverterType에 추가**

```kotlin
enum class HexConverterType {
    DEFAULT,
    MULTIPLY,
    DIVIDE,
    CUSTOM // 새로운 변환기 추가
}
```

3. **핸들러에 반영**
   `handleParseHex` 함수에 추가 변환기 로직을 적용합니다.

#### **2. 새로운 어노테이션 핸들러 추가**

1. **핸들러 구현**
   `AnnotationHandler` 인터페이스를 구현하여 새로운 어노테이션 처리 로직을 작성합니다.

```kotlin
object CustomAnnotationHandler : AnnotationHandler {
    override fun canHandle(property: KProperty<*>, param: KParameter): Boolean {
        return property.annotations.any { it is CustomAnnotation }
    }

    override fun handle(
        property: KProperty<*>,
        param: KParameter,
        data: ByteArray,
        devEuiBytes: ByteArray
    ): Any? {
        // 어노테이션 처리 로직
        return "Custom value"
    }
}
```

2. **Registry에 등록**

```kotlin
AnnotationHandlerRegistry.register(CustomAnnotationHandler)
```

### **유틸리티**

#### **1. HexUtils**

- **Base64 디코딩 및 숫자 변환**
   - `base64ToByteArray`: Base64 문자열을 바이트 배열로 변환.
   - `hexToLong`: 16진수 문자열을 Long 값으로 변환.
- **확장 함수**
   - `toHexString`: 바이트 배열을 16진수 문자열로 변환.
   - `toHexFormatted`: 단일 바이트를 포맷된 16진수 문자열로 변환.

```kotlin
fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }
fun Byte.toHexFormatted(): String = String.format("%02x", this.toInt() and 0xFF)
```

---

## **결론**

이 시스템은 어노테이션과 리플렉션을 활용하여 유연하고 확장 가능한 파싱 시스템을 제공합니다. 새롭게 요구되는 데이터 포맷과 변환기가 등장하더라도 최소한의 코드 변경만으로 대응할 수 있으며, 효율적이고 일관된 데이터 처리를 보장합니다.