package io.parser.lora.examples

import java.math.BigDecimal
import kotlin.reflect.full.memberProperties

object TestUtils {
    fun compareObjects(obj1: Any, obj2: Any): Map<String, Pair<Any?, Any?>> {
        val differences = mutableMapOf<String, Pair<Any?, Any?>>()
        val obj1Class = obj1::class
        val obj2Class = obj2::class

        // 클래스가 다를 경우 차이점을 추가
        if (obj1Class != obj2Class) {
            differences["Class Type"] = Pair(obj1Class.simpleName, obj2Class.simpleName)
            return differences
        }

        // 두 객체의 모든 속성을 리플렉션으로 가져와 비교
        obj1Class.memberProperties.forEach { property ->
            val value1 = property.getter.call(obj1)
            val value2 = property.getter.call(obj2)

            // BigDecimal 타입은 compareTo로 비교
            if (value1 is BigDecimal && value2 is BigDecimal) {
                if (value1.compareTo(value2) != 0) {
                    differences[property.name] = Pair(value1, value2)
                }
            } else if (value1 != value2) { // 일반 비교
                differences[property.name] = Pair(value1, value2)
            }
        }

        return differences
    }
}