@file:OptIn(ExperimentalStdlibApi::class)

plugins {
    kotlin("multiplatform") version "2.1.20"
    id("maven-publish")
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "com.github.winterreisender"
version = "0.6.0"
description = "webviewko"

repositories {
    mavenCentral()
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(rootDir.resolve("docs/kdoc"))
    }
}

lateinit var osPrefix :String

kotlin {
    jvmToolchain(17)
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    /* JS IR not work
    js(LEGACY) {
        nodejs {

        }
    }
     */

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        isMingwX64 -> mingwX64("native")
        hostOs == "Linux" -> linuxX64("native")
        hostOs == "Mac OS X" -> macosX64("native") // Not tested
        else -> throw GradleException("$hostOs is not supported.")
    }
    osPrefix = when {
        hostOs == "Linux" -> "linuxX64"
        hostOs == "Mac OS X" -> "macosX64"
        isMingwX64 -> "mingwX64"
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        compilations.getByName("main") {
            cinterops {
                val cwebview by creating {
                    defFile(project.file("src/nativeMain/nativeInterop/cinterop/webview.def"))
                    packageName("${group}.cwebview")
                }
            }

        }
        binaries {
            test("native") {
                if(hostOs == "Linux")
                    linkerOpts("native/src/nativeMain/resources/linuxx64/libwebview.so","-Wl,-rpath=${'$'}ORIGIN")

                /* Copy it manully
                copy {
                    from("src/nativeMain/nativeInterop/cinterop/webview/${osPrefix}/")
                    into(outputDirectory)
                    include("*.so")
                    duplicatesStrategy= DuplicatesStrategy.WARN
                }
                */
            }
        }


    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
            }
        }
        val jvmMain by getting {
            dependencies {
                api("net.java.dev.jna:jna:5.12.1")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("net.java.dev.jna:jna-platform:5.12.1")
            }
        }

        /*
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(npm("webview-nodejs", "0.1.5", generateExternals = false))
            }
        }


        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        */

        val nativeMain by getting {
            dependencies {
            }
        }

        val nativeTest by getting
    }

//    publishing {
//        publications {
//            matching {it.name == "native"}.all {
//                val targetPublication = this@all
//                tasks.withType<AbstractPublishToMaven>()
//                    .matching { it.publication == targetPublication }
//                    .configureEach {
//                        publication.artifactId = "webviewko-${osPrefix}".lowercase()
//                    }
//            }
//        }
//    }
}

/* deprecated shadow jar
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("all")
    val main by kotlin.jvm().compilations
    from(main.output)
    configurations += main.compileDependencyFiles as Configuration
    configurations += main.runtimeDependencyFiles as Configuration

    manifest {
        attributes(mapOf(
            "ImplementationTitle" to project.name,
            "Implementation-Version" to project.version)
        )
    }
}
*/

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

//publishing {
//    repositories {
//        maven {
//            name = "GitLabPackages"
//            url = uri("https://gitlab.com/api/v4/projects/38224197/packages/maven")
//            credentials(HttpHeaderCredentials::class) {
//                //name = "Deploy-Token"
//                name =  "Private-Token"
//                value = System.getenv("GITLAB_TOKEN")
//            }
//            authentication {
//                create<HttpHeaderAuthentication>("header")
//            }
//        }
//    }
//    publications {
//        matching {it.name == "native"}.all {
//            val targetPublication = this@all
//            tasks.withType<AbstractPublishToMaven>()
//                .matching { it.publication == targetPublication }
//                .configureEach {
//                    publication.artifactId = "webviewko-${osPrefix}".lowercase()
//                }
//        }
//    }
//}