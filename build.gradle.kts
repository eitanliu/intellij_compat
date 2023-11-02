import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
    `java-library`
    `maven-publish`
}

group = "com.eitanliu.intellij"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

//打包源码
val sourcesJar by tasks.registering(Jar::class) {
    //如果没有配置main会报错
    from(sourceSets["main"].allSource)
    archiveClassifier.set("sources")
}

publishing {
    //配置maven仓库
    repositories {
        maven {
            url = uri("$buildDir/repo")
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.getByName("jar"))
            artifact(sourcesJar)
            // groupId = "com.xxx"
            // artifactId = "compat"
            // version = "1.0.0"
        }

    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
