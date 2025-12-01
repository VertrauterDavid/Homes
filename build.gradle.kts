plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

group = "me.david"
version = "2.0"
description = "Fully customizable MySQL-based home system for Minecraft servers."

repositories {
    mavenLocal()
    mavenCentral()

    // PaperMC
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(libs.paper.api)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.commandapi)
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-${project.version}.jar"
        archiveClassifier = null


        manifest {
            attributes["Implementation-Version"] = rootProject.version
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
    }

    assemble {
        dependsOn(shadowJar)
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }

    withType<Javadoc>() {
        options.encoding = Charsets.UTF_8.name()
    }

    defaultTasks("build")

    val version = "1.21.8"
    val javaVersion = JavaLanguageVersion.of(21)

    val jvmArgsExternal = listOf(
        "-Dcom.mojang.eula.agree=true"
    )

    val sharedPlugins = runPaper.downloadPluginsSpec {
        url("https://github.com/ViaVersion/ViaVersion/releases/download/5.5.1/ViaVersion-5.5.1.jar")
        url("https://github.com/ViaVersion/ViaBackwards/releases/download/5.5.1/ViaBackwards-5.5.1.jar")
    }

    runServer {
        minecraftVersion(version)
        runDirectory = rootDir.resolve("run/paper/$version")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = javaVersion
        }

        downloadPlugins {
            from(sharedPlugins)
        }

        jvmArgs = jvmArgsExternal
    }

    runPaper.folia.registerTask {
        minecraftVersion(version)
        runDirectory = rootDir.resolve("run/folia/$version")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = javaVersion
        }

        downloadPlugins {
            from(sharedPlugins)
        }

        jvmArgs = jvmArgsExternal
    }
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(
            "version" to project.version
        )
    }
}