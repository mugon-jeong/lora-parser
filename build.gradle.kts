plugins {
    kotlin("jvm") version "2.0.21"
}

group = "io.parser.lora"
version = "0.0.1"


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    // Reflection 라이브러리 추가
    testImplementation("org.reflections:reflections:0.10.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}