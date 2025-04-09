import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.0.2"
    id("org.asciidoctor.jvm.convert") version "4.0.4"
    id("org.ajoberstar.git-publish") version "4.2.0"
    id("org.ec4j.editorconfig") version "0.0.3"
    id("java")
    id("checkstyle")
}

group = "com.jeein"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

spotless {
    java {
        removeUnusedImports()
        importOrder()
        googleJavaFormat().aosp()
    }

    kotlinGradle {
        target("**/*.gradle.kts", "*.gradle.kts")

        ktlint()
        trimTrailingWhitespace()
        endWithNewline()
    }

//    val prettierConfig by extra("$rootDir/.prettierrc.yml")
//
//    format("markdown") {
//        target("**/*.md", "*.md")
//
//        prettier().configFile(prettierConfig)
//    }
//
//    format("yaml") {
//        target("*.yml", "src/main/resources/*.yml")
//
//        prettier().configFile(prettierConfig)
//    }
}

repositories {
    mavenCentral()
}

val springCloudVersion = "2024.0.0"
val snippetsDir by extra { file("build/generated-snippets") }

configurations {
    create("asciidoctorExt")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
//    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
    implementation("org.postgresql:postgresql:42.6.2")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("io.micrometer:micrometer-registry-prometheus:1.15.0-M2")
    add("asciidoctorExt", "org.springframework.restdocs:spring-restdocs-asciidoctor")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("net.bytebuddy:byte-buddy-agent:1.15.11")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

 tasks.register("javaFormattingCheck") {
    dependsOn("spotlessCheck")

    doLast {
        println("\u001B[32m✔ spotlessCheck check completed successfully!\u001B[0m")
    }
 }

 tasks.register("javaFormattingApply") {
    dependsOn("spotlessApply")

    doLast {
        println("\u001B[32m✔ spotlessApply completed successfully!\u001B[0m")
    }
 }

checkstyle {
    toolVersion = "10.21.1"
    configFile = file("$rootDir/checkstyle.xml")
}

tasks.withType<Checkstyle>().configureEach {
    source = fileTree("src/main/java")
    include("**/*.java")
    reports {
        html.required.set(true)
        xml.required.set(false)
    }
}

tasks.named("check") {
    dependsOn("checkstyleMain")
}

tasks.named("checkstyleMain") {
    group = "verification"
    description = "Run Checkstyle"
    dependsOn("checkstyle")
}

tasks.jar {
    enabled = false
}

tasks.test {
    useJUnitPlatform()
    doFirst {
        val agentJar =
            configurations.testRuntimeClasspath
                .get()
                .files
                .find { it.name.contains("byte-buddy-agent") }
                ?: throw GradleException("Byte Buddy Agent JAR not found")

        jvmArgs("-javaagent:${agentJar.absolutePath}")
    }

    outputs.dir(snippetsDir)

    val testDurations = mutableMapOf<String, Long>()

    val listener =
        object : TestListener {
            private var startTime = 0L

            override fun beforeTest(descriptor: TestDescriptor) {
                startTime = System.currentTimeMillis()
            }

            override fun afterTest(
                descriptor: TestDescriptor,
                result: TestResult,
            ) {
                val duration = System.currentTimeMillis() - startTime
                val className = descriptor.className ?: "UnknownClass"
                testDurations[className] = testDurations.getOrDefault(className, 0L) + duration
            }

            override fun beforeSuite(suite: TestDescriptor) {}

            override fun afterSuite(
                suite: TestDescriptor,
                result: TestResult,
            ) {
                if (suite.parent == null) {
                    val reportFile =
                        layout.buildDirectory
                            .file("reports/tests/test/class-durations.txt")
                            .get()
                            .asFile
                    val content =
                        testDurations.entries.joinToString("\n") {
                            val seconds = "%.3f".format(it.value / 1000.0)
                            "✅ ${it.key} - ${seconds}s"
                        }
                    reportFile.writeText(content)
                    println("\u001B[36m✔ Test durations written to class-durations.txt\u001B[0m")
                }
            }
        }

    addTestListener(listener)
}

val asciidoctorTask =
    tasks.named<AsciidoctorTask>("asciidoctor").apply {
        configure {
            inputs.dir(snippetsDir)
            configurations("asciidoctorExt")
            dependsOn(tasks.test)
        }
    }

// gitPublish {
//    repoUri = 'git@github.com:socketing-refactoring/socketing-refactoring-log.git'
//    branch = 'gh-pages'
//    contents {
//        from(asciidoctor.outputDir) {
//            into '.'
//        }
//    }
// }

tasks.named<BootJar>("bootJar") {
    archiveFileName.set("member-service.jar")
    dependsOn(asciidoctorTask)

    from(asciidoctorTask.map { it.outputDir.resolve("html5") }) {
        into("static/docs")
    }
}
