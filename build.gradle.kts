plugins {
    id("java-library")
    id("maven-publish")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "io.clickhouse.springdata"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    //spring
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    //utils
    implementation ("org.reflections:reflections:0.10.2")
    compileOnly ("org.projectlombok:lombok")
    annotationProcessor ("org.projectlombok:lombok")
    //clickhouse
    api ("com.clickhouse:client-v2:0.8.0")
}

tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(file("build/generated/sources/annotationProcessor/java/main"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])

            groupId = "com.github.${project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")}"
            artifactId = "clickhouse-spring-data"
            version = "1.0.0"

            pom {
                name.set("clickhouse-spring-data")
                description.set("Spring Boot integration for ClickHouse")
                url.set(project.findProperty("gpr.url") as String? ?: System.getenv("GITHUB_URL"))
//                url.set("https://maven.pkg.github.com/serzhe1/clickhouse-spring-data")
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/serzhe1/clickhouse-spring-data")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}