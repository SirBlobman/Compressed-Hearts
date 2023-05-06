val baseVersion = fetchProperty("version.base", "invalid")
val betaString = fetchProperty("version.beta", "false")
val jenkinsBuildNumber = fetchEnv("BUILD_NUMBER", null, "Unofficial")

val betaBoolean = betaString.toBoolean()
val betaVersion = if (betaBoolean) "Beta-" else ""
val calculatedVersion = "$baseVersion.$betaVersion$jenkinsBuildNumber"

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
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://nexus.sirblobman.xyz/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    // Java Dependencies
    compileOnly("org.jetbrains:annotations:24.0.1")

    // Spigot API
    val spigotVersion = fetchProperty("spigot.version", "")
    compileOnly("org.spigotmc:spigot-api:$spigotVersion")

    // Plugin Dependencies
    compileOnly("com.github.sirblobman.api:core:2.9-SNAPSHOT") // BlueSlimeCore
    compileOnly("me.clip:placeholderapi:2.11.3") // PlaceholderAPI
}

tasks {
    named<Jar>("jar") {
        archiveBaseName.set("Compressed-Hearts")
        version = calculatedVersion
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Javadoc> {
        options.encoding = "UTF-8"
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
                    "pluginVersion" to calculatedVersion
                )
            )
        }

        filesMatching("config.yml") {
            expand(
                mapOf(
                    "pluginPrefix" to pluginPrefix,
                    "pluginVersion" to calculatedVersion
                )
            )
        }
    }
}
