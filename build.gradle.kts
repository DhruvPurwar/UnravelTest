plugins {
    id("java")
    application
}

group = "cloud.unravel.com"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation ("org.redisson:redisson:3.16.4")
    implementation ("com.github.ben-manes.caffeine:caffeine:3.1.8")

}


tasks.test {
    useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
    // Set JVM arguments
    jvmArgs = listOf(
        "-XX:+HeapDumpOnOutOfMemoryError",
        "-XX:HeapDumpPath=/path/to/dumps" // Ensure this path is correct and writable
    )
}