val junitJupiterVersion = "5.2.0"
val spekVersion = "1.1.5"
val kluentVersion = "1.41"
val khttpVersion = "0.1.0"
val javalinVersion = "2.2.0"
val slf4jVersion = "1.7.25"
val jacksonVersion = "2.9.7"
val ruleVersion = "1.2_20180904115611_cec846f"

val mainClass = "no.nav.foreldrepenger.AppKt"

plugins {
   application
   kotlin("jvm") version "1.2.71"
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
   compile("no.nav.foreldrepenger:uttak-regler:$ruleVersion")

   testCompile("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
   testCompile("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
   testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
   testCompile("org.amshove.kluent:kluent:$kluentVersion")
   testCompile("khttp:khttp:$khttpVersion")
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
   maven("https://repo.adeo.no/repository/maven-releases/")
}

java {
   sourceCompatibility = JavaVersion.VERSION_1_10
   targetCompatibility = JavaVersion.VERSION_1_10
}

tasks.withType<Test> {
   useJUnitPlatform()
   testLogging {
      events("passed", "skipped", "failed")
   }
}

tasks.withType<Wrapper> {
   gradleVersion = "4.10.2"
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

