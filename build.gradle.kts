import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
    id("io.papermc.paperweight.userdev") version "1.3.8"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

group = "pro.darc"
version = "1.0-SNAPSHOT"

val coroutineLibVersion = "1.6.3"
val ktormVersion = "3.5.0"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.md-5.net/content/groups/public/")
    maven("https://build.darc.pro/plugin/repository/everything/")
    maven("https://jitpack.io")
}

dependencies {
    implementation("net.kyori", "adventure-api", "4.11.0")
    compileOnly("com.comphenix.protocol", "ProtocolLib", "4.7.0")
    compileOnly("LibsDisguises", "LibsDisguises", "10.0.29")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.nametagedit:nametagedit:4.5.8")
    paperDevBundle("1.19-R0.1-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:${getKotlinPluginVersion()}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutineLibVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.20")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("com.charleskorn.kaml:kaml:0.46.0")

    implementation("com.github.LittleLightMC:Skedule:1.2.6")
    implementation("org.redisson:redisson:3.17.4")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}

tasks.compileJava {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(17)
}

tasks.javadoc {
    options.encoding = Charsets.UTF_8.name()
}

tasks.processResources {
    filteringCharset = Charsets.UTF_8.name()
}

tasks.runServer {
    systemProperty("com.mojang.eula.agree", true)
    pluginJars(*getAllFilesFromDir(File(projectDir, "deps")).toTypedArray())
}

tasks.jar {
    val include = setOf(
        "kotlin",
        "Skedule",
        "kaml",
        "yaml"
    )

    duplicatesStrategy = DuplicatesStrategy.WARN

    configurations.runtimeClasspath.get()
        .filter { bt -> include.any { bt.name.contains(it, true) } }
        .map { zipTree(it) }
        .also { from(it) }
}

bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    main = "pro.darc.projectm.ProjectMCoreMain"
    apiVersion = "1.19"
    authors = listOf("DarcJC")
    version = getVersion().toString()
    depend = listOf("ProtocolLib")
}

fun getAllFilesFromDir(directory: File): List<File> {
    return directory.walk().filter { it.isFile }.toList().apply {
        println("Loaded ${this.size} dependencies...")
    }
}
