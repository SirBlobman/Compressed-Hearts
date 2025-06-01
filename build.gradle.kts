val baseVersion = fetchProperty("version.base", "invalid")
val betaString = fetchProperty("version.beta", "false")
val jenkinsBuildNumber = fetchEnv("BUILD_NUMBER", null, "Unofficial")

val betaBoolean = betaString.toBoolean()
val betaVersion = if (betaBoolean) "Beta-" else ""
version = "$baseVersion.$betaVersion$jenkinsBuildNumber"

fun fetchProperty(propertyName: String, defaultValue: String): String {
    val found = findProperty(propertyName)
    if (found != null) {
        return found.toString()
    }

    return defaultValue
}

fun fetchEnv(envName: String, propertyName: String?, defaultValue: String): String {
    val found = System.getenv(envName)
    if (found != null) {
        return found
    }

    if (propertyName != null) {
        return fetchProperty(propertyName, defaultValue)
    }

    return defaultValue
}

plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://nexus.sirblobman.xyz/public/")
}

dependencies {
    // Java Dependencies
    compileOnly("org.jetbrains:annotations:26.0.2")

    // Spigot API
    val spigotVersion = fetchProperty("version.spigot", "")
    compileOnly("org.spigotmc:spigot-api:$spigotVersion")

    // Plugin Dependencies
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

    processResources {
        val pluginName = fetchProperty("bukkit.plugin.name", "")
        val pluginPrefix = fetchProperty("bukkit.plugin.prefix", "")
        val pluginDescription = fetchProperty("bukkit.plugin.description", "")
        val pluginWebsite = fetchProperty("bukkit.plugin.website", "")
        val pluginMainClass = fetchProperty("bukkit.plugin.main", "")

        filesMatching("plugin.yml") {
            expand(
                mapOf(
                    "pluginName" to pluginName,
                    "pluginPrefix" to pluginPrefix,
                    "pluginDescription" to pluginDescription,
                    "pluginWebsite" to pluginWebsite,
                    "pluginMainClass" to pluginMainClass,
                    "pluginVersion" to version
                )
            )
        }

        filesMatching("config.yml") {
            expand(
                mapOf(
                    "pluginPrefix" to pluginPrefix,
                    "pluginVersion" to version
                )
            )
        }
    }
}
