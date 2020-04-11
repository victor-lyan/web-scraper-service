import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.bmuschko.gradle.docker.tasks.image.*

plugins {
	id("org.springframework.boot") version "2.2.2.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	id("com.bmuschko.docker-remote-api") version "6.2.0"
	kotlin("jvm") version "1.3.61"
	kotlin("plugin.spring") version "1.3.61"
}

group = "com.wictor-lyan"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
	runtimeClasspath {
		extendsFrom(developmentOnly)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jsoup:jsoup:1.12.1")
	implementation("org.liquibase:liquibase-core:3.8.2")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

val dockerBuildDir = "build/docker/"
val jar: Jar by tasks

tasks.create("createDockerfile", Dockerfile::class) {
	from("openjdk:8-jre-alpine")
	copyFile(jar.archiveFileName.get(), "/app/web-scraper-service.jar")
	entryPoint("java", "-Dspring.profiles.active=production", "-jar", "/app/web-scraper-service.jar")
	exposePort(9096)
}

tasks.create("syncJar", Copy::class) {
	dependsOn("build")
	from(jar.archiveFile)
	into(dockerBuildDir)
}

tasks.create("removeOldImage", DockerRemoveImage::class) {
	force.set(true)
	targetImageId("wictorlyan/web-scraper-service")
	onError {println(message)}
}

tasks.create("buildDockerImage", DockerBuildImage::class) {
	dependsOn("removeOldImage", "syncJar", "createDockerfile")
	images.add("wictorlyan/web-scraper-service:latest")
}

tasks.create("pushDockerImage", DockerPushImage::class) {
	dependsOn("buildDockerImage")
	images.add("wictorlyan/web-scraper-service:latest")
	registryCredentials {
		username.set(project.property("dockerHubUser") as String)
		password.set(project.property("dockerHubPassword") as String)
	}
}
