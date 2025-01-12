version = "0.0.1-SNAPSHOT"

plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.dom4j:dom4j:2.1.4")
}

tasks.test {
    useJUnitPlatform()
}