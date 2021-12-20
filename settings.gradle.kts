rootProject.name = "gradle-kotlin-start"

enableFeaturePreview("VERSION_CATALOGS")

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    val npmToolsVersion: String = "0.10.7"

    repositories {
        mavenLocal()
        maven("https://repo.kotlin.link")
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from("ru.mipt.npm:version-catalog:$npmToolsVersion")
        }
    }
}