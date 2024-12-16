plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(project(":processor"))
    ksp(project(":processor"))
}

ksp {
    arg("option1", "value1")
    arg("option2", "value2")
}