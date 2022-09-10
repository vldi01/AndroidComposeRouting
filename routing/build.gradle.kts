plugins {
    id("com.android.library")
    id("maven-publish")
    id("signing")
    kotlin("android")
}

group = "io.github.vldi01"
version = "1.1.0"

android {
    namespace = "com.diachuk.routing"

    compileSdk = AndroidSdk.compile
    sourceSets["main"].manifest.srcFile("src/main/AndroidManifest.xml")

    defaultConfig {
        minSdk = AndroidSdk.min
        targetSdk = AndroidSdk.target
        aarMetadata {
            minCompileSdk = 21
        }
    }

    lint {
        abortOnError = false
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

val androidSourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}
val androidJavadocs by tasks.creating(Javadoc::class) {
    source = android.sourceSets["main"].java.getSourceFiles()
}
val androidJavadocsJar by tasks.creating(Jar::class) {
    archiveClassifier.set("javadoc")
    archiveClassifier.convention("javadoc")

    archiveBaseName.convention("io.github.vldi01")
    archiveBaseName.set("io.github.vldi01")

    from(androidJavadocs.destinationDir)
}


artifacts {
    archives(androidSourcesJar)
}

dependencies {
    with(Compose) {
        implementation(compiler)
        implementation(ui)
        implementation(uiGraphics)
        implementation(uiTooling)
        implementation(foundationLayout)
        implementation(material)
        implementation(materialIconsCore)
        implementation(materialIconsExtended)
        implementation(navigation)
    }

    with(Kotlin) {
        implementation(coroutinesCore)
        implementation(coroutinesAndroid)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group as String
            artifactId = "android-compose-routing"
            version = project.version as String

            artifact("$buildDir/outputs/aar/routing-debug.aar")
            //artifact("$buildDir/libs/routing-${project.version as String}-sources.aar")
            artifact(androidSourcesJar)
            artifact(androidJavadocsJar)

            pom {
                name.set("Routing for Android Compose")
                description.set("Routing for Android Compose")
                url.set("https://github.com/vldi01/AndroidComposeRouting")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("vldi01")
                        name.set("Vladyslav Diachuk")
                        email.set("vladiachuk@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/vldi01/AndroidComposeRouting.git")
                    developerConnection.set("scm:git:ssh://github.com/vldi01/AndroidComposeRouting.git")
                    url.set("https://github.com/vldi01/AndroidComposeRouting")
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = project.properties["ossrhUsername"] as String?
                password = project.properties["ossrhPassword"] as String?
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
