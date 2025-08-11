plugins {
    id("java")
    id("maven-publish")
}

group   = "com.ronreynolds"
version = "0.0.1-SNAPSHOT"

val mainClass = "com.ronreynolds.tools.gcode.Parser"

// library versions
val assertJVersion      = "3.27.3"         // 2025-01-18
val jUnitJupiterVersion = "5.12.0"         // 2025-02-21
val logbackVersion      = "1.5.17"         // 2025-02-25
val lombokVersion       = "1.18.36"        // 2024-11-15
val slf4jVersion        = "2.0.17"         // 2025-02-25


java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    runtimeOnly   ("ch.qos.logback:logback-classic:$logbackVersion")
    
    // test dependencies
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$jUnitJupiterVersion")
    testRuntimeOnly   ("org.junit.platform:junit-platform-launcher")

    // Lombok dependencies
    compileOnly            ("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor    ("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly        ("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.compileTestJava {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.create("uberjar", Jar::class) {
    group = "build"
    description = "Creates a jar containing classes and all runtime dependencies"
    manifest.attributes["Main-Class"] = mainClass
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    with(tasks.jar.get())
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}