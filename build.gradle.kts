val baseVersion = findProperty("version.base") ?: "invalid"
val betaString = ((findProperty("version.beta") ?: "false") as String)
val jenkinsBuildNumber = System.getenv("BUILD_NUMBER") ?: "Unofficial"

val betaBoolean = betaString.toBoolean()
val betaVersion = if (betaBoolean) "Beta-" else ""
val calculatedVersion = "$baseVersion.$betaVersion$jenkinsBuildNumber"

plugins {
    id("java")
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
    compileOnly("org.jetbrains:annotations:24.0.1") // JetBrains Annotations
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT") // Spigot API

    // Plugin Dependencies
    compileOnly("com.github.sirblobman.api:core:2.8-SNAPSHOT") // BlueSlimeCore
    compileOnly("me.clip:placeholderapi:2.11.3") // PlaceholderAPI
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    named<Jar>("jar") {
        archiveFileName.set("Compressed-Hearts-$calculatedVersion.jar")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Javadoc> {
        options.encoding = "UTF-8"
    }

    processResources {
        val pluginName = (findProperty("bukkit.plugin.name") ?: "") as String
        val pluginPrefix = (findProperty("bukkit.plugin.prefix") ?: "") as String
        val pluginDescription = (findProperty("bukkit.plugin.description") ?: "") as String
        val pluginWebsite = (findProperty("bukkit.plugin.website") ?: "") as String
        val pluginMainClass = (findProperty("bukkit.plugin.main") ?: "") as String

        filesMatching("plugin.yml") {
            expand(mapOf(
                "pluginName" to pluginName,
                "pluginPrefix" to pluginPrefix,
                "pluginDescription" to pluginDescription,
                "pluginWebsite" to pluginWebsite,
                "pluginMainClass" to pluginMainClass,
                "pluginVersion" to calculatedVersion
            ))
        }

        filesMatching("config.yml") {
            expand(mapOf(
                "pluginPrefix" to pluginPrefix,
                "pluginVersion" to calculatedVersion
            ))
        }
    }
}
