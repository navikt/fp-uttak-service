val junitJupiterVersion = "5.4.2"
val spekVersion = "1.2.1"
val kluentVersion = "1.41"
val khttpVersion = "0.1.0"
val javalinVersion = "3.0.0"
val slf4jVersion = "1.7.25"
val jacksonVersion = "2.9.7"
val kotlinReflectVersion = "1.3.41"
val ruleVersion = "2.1-20200121124755-f51f7d3"

val gprPassword: String by project
val gprUser: String by project
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
   implementation(kotlin("stdlib"))
   implementation("io.javalin:javalin:$javalinVersion")
   implementation("org.slf4j:slf4j-simple:$slf4jVersion")
   implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
   implementation("no.nav.foreldrepenger:uttak-regler:$ruleVersion")
   implementation ("org.jetbrains.kotlin:kotlin-reflect:$kotlinReflectVersion")

   testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
   testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
   testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
   testImplementation("org.amshove.kluent:kluent:$kluentVersion")
   testImplementation("khttp:khttp:$khttpVersion")
   testImplementation("org.jetbrains.spek:spek-api:$spekVersion") {
      exclude(group = "org.jetbrains.kotlin")
   }
   testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion") {
      exclude(group = "org.junit.platform")
      exclude(group = "org.jetbrains.kotlin")
   }
}

repositories {
   maven {
      name = "Github"
      url = uri("https://maven.pkg.github.com/navikt/fp-uttak")
      credentials {
         username = "x-access-token"
         password = System.getenv("GITHUB_TOKEN")
      }
   }
   jcenter()
}

java {
   sourceCompatibility = JavaVersion.VERSION_HIGHER
   targetCompatibility = JavaVersion.VERSION_HIGHER
}

tasks.withType<Wrapper> {
   gradleVersion = "6.0.1"
}

val fatJar = task("fatJar", type = Jar::class) {
   baseName = "${project.name}-all"
   manifest {
      attributes["Implementation-Title"] = "Uttak service"
      attributes["Main-Class"] = mainClass
   }
   from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
   with(tasks["jar"] as CopySpec)
}

