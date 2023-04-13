plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.bootonica"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.telegram:telegrambots:6.5.0")
    implementation("org.telegram:telegrambotsextensions:6.5.0")
    implementation("org.json:json:20230227")

    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")

    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.2.0")

    //zxing decoder
    implementation("com.google.zxing:core:3.5.1")
    implementation("com.google.zxing:javase:3.5.1")

    // firebase
    implementation("com.google.firebase:firebase-admin:9.1.1")

    // kotlinx-coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}