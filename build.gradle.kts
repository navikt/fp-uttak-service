val junitJupiterVersion = "5.4.2"
val spekVersion = "1.2.1"
val kluentVersion = "1.41"
val khttpVersion = "0.1.0"
val javalinVersion = "3.0.0"
val slf4jVersion = "1.7.25"
val jacksonVersion = "2.9.7"
val kotlinReflectVersion = "1.3.41"
val ruleVersion = "1.2_20190923165252_44d68be"

val mainClass = "no.nav.foreldrepenger.AppKt"

plugins {
   application
   kotlin("jvm") version "1.3.41"
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
   compile ("org.jetbrains.kotlin:kotlin-reflect:$kotlinReflectVersion")

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
   maven("https://repo.adeo.no/repository/maven-releases/")
   jcenter()
}

java {
   sourceCompatibility = JavaVersion.VERSION_HIGHER
   targetCompatibility = JavaVersion.VERSION_HIGHER
}

tasks.withType<Wrapper> {
   gradleVersion = "5.5"
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

