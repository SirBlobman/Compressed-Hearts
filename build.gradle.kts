val base = providers.gradleProperty("version.base").orElse("invalid").get()
val betaBoolean = providers.gradleProperty("version.beta").orElse("false").get().toBoolean()
val build = providers.environmentVariable("BUILD_NUMBER").orElse("Unofficial").get()
val beta = if (betaBoolean) "Beta-" else ""
version = "$base.$beta$build"

plugins {
    id("java")
    id("de.eldoria.plugin-yml.bukkit") version "0.8.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

bukkit {
    name = "CompressedHearts"
    prefix = "Compressed Hearts"
    description="A plugin that allows you to change the heart scale of your players"
    website="https://www.spigotmc.org/resources/44024/"

    main="com.github.sirblobman.compressed.hearts.HeartsPlugin"
    apiVersion = "1.21.8"

    foliaSupported = true
    authors = listOf("SirBlobman")
    depend = listOf("BlueSlimeCore")

    commands {
        register("compressed-hearts") {
            description = "Main command for the CompressedHearts plugin"
            usage = "/<command> help"
            aliases = listOf("compressedhearts", "chearts", "compressedh", "ch")
        }

        register("hp") {
            description = "Check you current health."
            usage = "/<command> [player]"
            aliases = listOf("hearts", "health")
        }
    }

}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://nexus.sirblobman.xyz/public/")
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2") // JetBrains Annotations
    compileOnly("org.spigotmc:spigot-api:1.21.10-R0.1-SNAPSHOT") // Spigot API
    compileOnly("com.github.sirblobman.api:core:2.9-SNAPSHOT") // BlueSlimeCore
}

tasks {
    named<Jar>("jar") {
        archiveBaseName.set("Compressed-Hearts")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-Xlint:deprecation")
        options.compilerArgs.add("-Xlint:unchecked")
    }

    withType<Javadoc> {
        options.encoding = "UTF-8"
        val standardOptions = options as StandardJavadocDocletOptions
        standardOptions.addStringOption("Xdoclint:none", "-quiet")
    }
}
