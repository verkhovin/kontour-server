import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"
}

group = "io.kontour"
version = "0.1"

val koinVersion = "2.0.1"
val ktorVersion = "1.2.4"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    //ktor
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    //koin
    implementation("org.koin:koin-ktor:$koinVersion")
    //mongo
    implementation("org.mongodb:mongodb-driver-sync:3.11.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}