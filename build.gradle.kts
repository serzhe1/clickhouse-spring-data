plugins {
    id("java-library")
    id("maven-publish")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "io.clickhouse.springdata"
version = "1.0.0-SNAPSHOT"

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
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = group.toString()
            artifactId = "clickhouse-spring-boot-starter"

            pom {
                name.set("ClickHouse Spring Boot Starter")
                description.set("Spring Boot Starter for ClickHouse")
//                url.set("https://github.com/serzhe1/clickhouse-spring-boot-starter")
                licenses {
                    license {
                        name.set("Apache 2")
//                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("serzhe1")
                        name.set("Sergei Ladygin")
                        email.set("s.a.ladygin@yandex.ru")
                    }
                }
                scm {
//                    connection.set("scm:git:git://github.com/serzhe1/clickhouse-spring-boot-starter.git")
//                    developerConnection.set("scm:git:ssh://github.com/serzhe1/clickhouse-spring-boot-starter.git")
//                    url.set("https://github.com/serzhe1/clickhouse-spring-boot-starter")
                }
            }
        }
    }
}
