package ru.mipt.npm.gradle.kotlinstart

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal fun Project.requestPropertyOrNull(propertyName: String): String? = findProperty(propertyName) as? String
    ?: System.getenv(propertyName)

internal fun Project.requestProperty(propertyName: String): String = requestPropertyOrNull(propertyName)
    ?: error("Property $propertyName not defined")


internal fun Project.setupPublication(
    mavenPomConfiguration: MavenPom.() -> Unit = {},
) = allprojects {
    //apply only to projects with publication plugin
    plugins.withId("maven-publish") {
        configure<PublishingExtension> {

            plugins.withId("org.jetbrains.kotlin.js") {
                val kotlin: KotlinJsProjectExtension = extensions.findByType()!!

                val sourcesJar by tasks.creating(Jar::class) {
                    archiveClassifier.set("sources")

                    kotlin.sourceSets.all {
                        from(kotlin)
                    }
                }
                afterEvaluate {
                    publications.create<MavenPublication>("js") {
                        kotlin.js().components.forEach {
                            from(it)
                        }

                        artifact(sourcesJar)
                    }
                }
            }

            plugins.withId("org.jetbrains.kotlin.jvm") {
                val kotlin = extensions.findByType<KotlinJvmProjectExtension>()!!

                val sourcesJar by tasks.creating(Jar::class) {
                    archiveClassifier.set("sources")
                    kotlin.sourceSets.forEach {
                        from(it.kotlin)
                    }
                }

                publications.create<MavenPublication>("jvm") {
                    kotlin.target.components.forEach {
                        from(it)
                    }

                    artifact(sourcesJar)
                }
            }

            val dokkaJar by tasks.creating(Jar::class) {
                group = "documentation"
                archiveClassifier.set("javadoc")
                from(tasks.findByName("dokkaHtml"))
            }

            // Process each publication we have in this project
            afterEvaluate {
                publications.withType<MavenPublication> {
                    artifact(dokkaJar)

                    pom {
                        name.set(project.name)
                        description.set(project.description ?: project.name)

                        licenses {
                            license {
                                name.set("The Apache Software License, Version 2.0")
                                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                                distribution.set("repo")
                            }
                        }

                        scm {
                            tag.set(project.version.toString())
                        }

                        mavenPomConfiguration()
                    }
                }
            }
        }
    }
}

internal fun Project.isSnapshot() = "dev" in version.toString() || version.toString().endsWith("SNAPSHOT")

internal val Project.publicationTarget: String
    get() {
        val publicationPlatform = project.findProperty("publishing.platform") as? String
        return if (publicationPlatform == null) {
            "AllPublications"
        } else {
            publicationPlatform.capitalize() + "Publication"
        }
    }

/**
 * Add sonatype/maven-central publishing for this project and subprojects
 */
internal fun Project.addPublishing(
    publicationName: String,
    repositoryUrl: String,
    signing: Boolean = false,
) {
    if (requestPropertyOrNull("publishing.enabled") != "true") {
        logger.info("Skipping $publicationName publishing because publishing is disabled")
        return
    }

    if (requestPropertyOrNull("publishing.$publicationName.enabled") == "false") {
        logger.info("Skipping $publicationName publishing because `publishing.$publicationName.enabled == false`")
        return
    }

    val repoUser: String = requestProperty("publishing.$publicationName.user")
    val repoPassword: String = requestProperty("publishing.$publicationName.password")

    allprojects {
        plugins.withId("maven-publish") {
            configure<PublishingExtension> {
                if (signing) {
                    if (!plugins.hasPlugin("signing")) {
                        apply<SigningPlugin>()
                    }

                    extensions.configure<SigningExtension>("signing") {
                        val signingId: String? = requestPropertyOrNull("publishing.signing.id")
                        if (!signingId.isNullOrBlank()) {
                            val signingKey: String = requestProperty("publishing.signing.key")
                            val signingPassphrase: String = requestProperty("publishing.signing.passPhrase")

                            // if key is provided, use it
                            useInMemoryPgpKeys(signingId, signingKey, signingPassphrase)
                        } // else use file signing
                        sign(publications)
                    }
                }

                repositories.maven {
                    name = publicationName
                    url = uri(repositoryUrl)

                    credentials {
                        username = repoUser
                        password = repoPassword
                    }
                }
            }
        }
    }
}

internal fun Project.addSonatypePublishing() = addPublishing(
    "sonatype",
    "https://oss.sonatype.org/service/local/staging/deploy/maven2",
    true
)
