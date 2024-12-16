import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import java.io.OutputStreamWriter
import java.util.Collections.emptyList

class LoraParserProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // 유틸리티 클래스 생성
        generateUtilityClass()

        // @LoraParser 어노테이션이 적용된 클래스 찾기
        val symbols = resolver.getSymbolsWithAnnotation(LoraParser::class.qualifiedName!!)
        logger.info("Found symbols: ${symbols.joinToString { it.toString() }}")

        // KSClassDeclaration만 필터링
        val annotatedClasses = symbols.filterIsInstance<KSClassDeclaration>()

        // 각 클래스에 대해 Companion 처리 및 확장 함수 생성
        annotatedClasses.forEach { classDeclaration ->
            generateFromFunction(classDeclaration)
        }
        return emptyList()
    }

    private fun generateFromFunction(classDeclaration: KSClassDeclaration) {
        val packageName = classDeclaration.packageName.asString()
        val className = classDeclaration.simpleName.asString()
        val fileName = "${className}Extensions"

        try {
            val file = codeGenerator.createNewFile(
                dependencies = Dependencies(false, classDeclaration.containingFile!!),
                packageName = packageName,
                fileName = fileName,
            )

            OutputStreamWriter(file, "UTF-8").use { writer ->
                // 패키지 선언
                writer.write("package $packageName\n\n")

                // 유틸리티 함수 import
                writer.write("import com.example.utils.*\n\n")

                // 확장 함수 정의 (Companion 확장)
                writer.write("fun $className.Companion.from(devEUI: ByteArray, byteArray: ByteArray): $className {\n")
                writer.write("    return $className(\n")

                // 데이터 클래스의 프로퍼티 탐색
                classDeclaration.getAllProperties().forEach { property ->
                    generatePropertyMapping(writer, property)
                }

                // 함수 끝
                writer.write("    )\n")
                writer.write("}\n")
            }
        } catch (e: FileAlreadyExistsException) {
            logger.warn("$fileName.kt already exists. Skipping generation.")
        }
    }

    private fun generatePropertyMapping(
        writer: OutputStreamWriter,
        property: KSPropertyDeclaration
    ) {
        val propertyName = property.simpleName.asString()
        val annotation = property.annotations.firstOrNull {
            it.shortName.asString() == "ParseHex"
        }

        writer.write("        $propertyName = ")

        if (annotation != null) {
            val byteStart = annotation.arguments.find { it.name?.asString() == "byteStart" }?.value as Int
            val byteEnd = annotation.arguments.find { it.name?.asString() == "byteEnd" }?.value as Int
            val scale = annotation.arguments.find { it.name?.asString() == "scale" }?.value as Double? ?: 1.0
            val enumType = annotation.arguments.find { it.name?.asString() == "enumType" }?.value as KSType?
            val bitRangeArray = annotation.arguments.find { it.name?.asString() == "bitRange" }?.value as? List<Int> ?: emptyList()

            val bitRange = if (bitRangeArray.isNotEmpty()) {
                IntRange(bitRangeArray[0], bitRangeArray[1])
            } else {
                null
            }

            if (enumType != null && enumType.declaration.qualifiedName?.asString() != "kotlin.Enum") {
                val enumClassName = enumType.declaration.qualifiedName!!.asString()
                writer.write("byteArray.extractEnumValue<$enumClassName>(byteArray.slice($byteStart..$byteEnd).joinToString(\"\"){it.toHexFormatted()}, ${bitRange?.let { "IntRange(${it.first}, ${it.last})" }}),\n")
            } else {
                writer.write("hexToLong(byteArray.slice($byteStart..$byteEnd).joinToString(\"\"){it.toHexFormatted()})")
                if (property.type.resolve().declaration.qualifiedName?.asString() == "kotlin.Double") {
                    writer.write(" / $scale")
                }
                writer.write(",\n")
            }
        } else if (propertyName == "devEUI") {
            writer.write("devEUI.toHexString(),\n")
        } else {
            writer.write("TODO(\"Handle field $propertyName\")\n")
        }
    }

//    private fun generateUtilityClass() {
//        val fileName = "LoraUtils"
//        val packageName = "com.example.utils"
//        try {
//            // KSP를 통해 파일 생성
//            val file = codeGenerator.createNewFile(
//                dependencies = Dependencies(false),
//                packageName = packageName,
//                fileName = fileName,
//            )
//
//            val utilityClassContent = """
//            package $packageName
//
//            fun ByteArray.toHexString(): String {
//                return joinToString("") { "%02x".format(it) }
//            }
//
//            fun List<Byte>.toHexString(): String {
//                return joinToString("") { "%02x".format(it) }
//            }
//
//            fun String.hexStringToByteArray(): ByteArray {
//                require(length % 2 == 0) { "Hex string must have an even length" }
//                return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
//            }
//
//            fun hexToLong(hex: String): Long {
//                return hex.toLong(16)
//            }
//
//            fun base64ToByteArray(base64: String): ByteArray {
//                return java.util.Base64.getDecoder().decode(base64)
//            }
//
//            fun Byte.toHexFormatted(): String {
//                return String.format("%02x", this.toInt() and 0xFF)
//            }
//
//            inline fun <reified T : Enum<T>> ByteArray.extractEnumValue(
//                hex: String,
//                bitRange: IntRange? = null
//            ): T {
//                val value = if (bitRange != null) {
//                    // bitRange를 비트 단위로 처리
//                    val value = hex.substring(bitRange.first, bitRange.last)
//                    value.toInt()
//                } else {
//                    hex.toInt()
//                }
//                return enumValues<T>()[value]
//            }
//        """.trimIndent()
//
//            OutputStreamWriter(file, "UTF-8").use { writer ->
//                writer.write(utilityClassContent)
//            }
//        } catch (e: FileAlreadyExistsException) {
//            // 파일이 이미 존재할 경우 경고 메시지 출력 및 처리 중단
//            logger.warn("$fileName.kt already exists. Skipping file generation.")
//        }
//    }

    private fun generateUtilityClass() {
        val fileName = "LoraUtils"
        val packageName = "com.example.utils"

        try {
            // KotlinPoet으로 파일 생성
            val fileSpec = FileSpec.builder(packageName, fileName)
                .addFunction(
                    FunSpec.builder("toHexString")
                        .receiver(ByteArray::class)
                        .returns(String::class)
                        .addStatement("return joinToString(\"\") { \"%%02x\".format(it) }")
                        .build()
                )
                .addFunction(
                    FunSpec.builder("toHexString")
                        .receiver(List::class.asClassName().parameterizedBy(Byte::class.asClassName()))
                        .returns(String::class)
                        .addStatement("return joinToString(\"\") { \"%%02x\".format(it) }")
                        .build()
                )
                .addFunction(
                    FunSpec.builder("hexStringToByteArray")
                        .receiver(String::class)
                        .returns(ByteArray::class)
                        .addStatement("require(length %% 2 == 0) { \"Hex string must have an even length\" }")
                        .addStatement("return chunked(2).map { it.toInt(16).toByte() }.toByteArray()")
                        .build()
                )
                .addFunction(
                    FunSpec.builder("hexToLong")
                        .addParameter("hex", String::class)
                        .returns(Long::class)
                        .addStatement("return hex.toLong(16)")
                        .build()
                )
                .addFunction(
                    FunSpec.builder("base64ToByteArray")
                        .addParameter("base64", String::class)
                        .returns(ByteArray::class)
                        .addStatement("return java.util.Base64.getDecoder().decode(base64)")
                        .build()
                )
                .addFunction(
                    FunSpec.builder("toHexFormatted")
                        .receiver(Byte::class)
                        .returns(String::class)
                        .addStatement("return String.format(\"%%02x\", this.toInt() and 0xFF)")
                        .build()
                )
                .addFunction(
                    FunSpec.builder("extractEnumValue")
                        .addModifiers(KModifier.INLINE) // inline 추가
                        .addTypeVariable(
                            TypeVariableName("T", Enum::class.asClassName().parameterizedBy(TypeVariableName("T")))
                                .copy(reified = true) // reified 추가
                        )
                        .receiver(ByteArray::class)
                        .addParameter("hex", String::class)
                        .addParameter(
                            ParameterSpec.builder(
                                "bitRange",
                                IntRange::class.asTypeName().copy(nullable = true)
                            )
                                .defaultValue("null")
                                .build()
                        )
                        .returns(TypeVariableName("T")) // 반환 타입
                        .addStatement(
                            """
                        val value = if (bitRange != null) {
                            hex.substring(bitRange.first, bitRange.last).toInt()
                        } else {
                            hex.toInt()
                        }
                        return enumValues<T>()[value]
                        """.trimIndent()
                        )
                        .build()
                )
                .build()

            val file = codeGenerator.createNewFile(
                dependencies = Dependencies(false),
                packageName = packageName,
                fileName = fileName,
            )

            file.writer(Charsets.UTF_8).use {
                fileSpec.writeTo(it)
            }
        } catch (e: FileAlreadyExistsException) {
            logger.warn("$fileName.kt already exists. Skipping file generation.")
        }
    }
}
