val junitJupiterVersion = "5.2.0"
val spekVersion = "1.1.5"
val kluentVersion = "1.38"
val javalinVersion = "1.7.0"
val slf4jVersion = "1.8.0-beta2"
val jacksonVersion = "2.9.6"

val mainClass = "no.nav.foreldrepenger.RunnerKt"

plugins {
   application
   kotlin("jvm") version "1.2.50"
}

buildscript {
   dependencies {
      classpath("org.junit.platform:junit-platform-gradle-plugin:1.2.0")
   }
}

application {
   mainClassName = "$mainClass"
}

dependencies {
   compile(kotlin("stdlib"))
   compile("io.javalin:javalin:$javalinVersion")
   compile("org.slf4j:slf4j-simple:$slf4jVersion")
   compile("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
   compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

   testCompile("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
   testCompile("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
   testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
   testCompile("org.amshove.kluent:kluent:$kluentVersion")
   testCompile("org.jetbrains.spek:spek-api:$spekVersion") {
      exclude(group = "org.jetbrains.kotlin")
   }
   testRuntime("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion") {
      exclude(group = "org.junit.platform")
      exclude(group = "org.jetbrains.kotlin")
   }
}

repositories {
   jcenter()
   mavenCentral()
}

java {
   sourceCompatibility = JavaVersion.VERSION_1_9
   targetCompatibility = JavaVersion.VERSION_1_9
}

tasks.withType<Test> {
   useJUnitPlatform()
   testLogging {
      events("passed", "skipped", "failed")
   }
}

tasks.withType<Wrapper> {
   gradleVersion = "4.8"
}

val fatJar = task("fatJar", type = Jar::class) {
   baseName = "${project.name}-all"
   manifest {
      attributes["Implementation-Title"] = "Uttak service"
      attributes["Main-Class"] = "$mainClass"
   }
   from(configurations.runtime.map({ if (it.isDirectory) it else zipTree(it) }))
   with(tasks["jar"] as CopySpec)
}

