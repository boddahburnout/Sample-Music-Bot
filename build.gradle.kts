plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://m2.chew.pro/releases/")
    }
    maven {
        url = uri("https://jitpack.io/")
    }
    maven {
        url = uri("https://maven.lavalink.dev/releases/")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation (files("libs/LeaflyScraper.main.jar"))
    implementation("net.dv8tion:JDA:6.0.0-rc.5")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("com.github.Carleslc:Simple-YAML:1.8.4")
    implementation("pw.chew:jda-chewtils:2.1")
    implementation("org.jsoup:jsoup:1.21.2")
    implementation("dev.arbjerg:lavaplayer:2.2.4")
    implementation("com.google.apis:google-api-services-youtube:v3-rev222-1.25.0")
    implementation("dev.lavalink.youtube:youtube-plugin:1.13.5")
    implementation("org.beryx:awt-color-factory:1.0.0")
}

tasks.test {
    useJUnitPlatform()
}